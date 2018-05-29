package com.app.sell.dao;

import android.content.Context;
import android.util.Log;

import com.app.sell.R;
import com.app.sell.events.ChatMessageQueuedEvent;
import com.app.sell.events.ChatMessagesUpdatedEvent;
import com.app.sell.model.ChatMessage;
import com.app.sell.model.Chatroom;
import com.app.sell.services.MessageNotificationService_;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;

@EBean
public class ChatMessageDao {

    private static final String TAG = "ChatMessageDao";
    @RootContext
    Context context;
    @Bean
    LoginDao loginDao;
    @Bean
    ChatroomDao chatroomDao;
    @Getter
    private List<ChatMessage> chatMessageList = new ArrayList<>();
    private Map<String, ChatMessage> chatMessageMap = new HashMap<>();

    public void initFor(String chatroomId) {
        DatabaseReference concreteChatroomReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.db_node_chatrooms)).child(chatroomId).child(context.getString(R.string.db_node_chatroom_messages));

        concreteChatroomReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatMessageMap = dataSnapshot.getValue(new GenericTypeIndicator<Map<String, ChatMessage>>() {
                });
                publish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //do nothing
            }
        });
    }

    private void publish() {
        if (chatMessageMap == null) {
            return;
        }
        chatMessageList = new ArrayList<>(chatMessageMap.values());
        Collections.sort(chatMessageList);
        EventBus.getDefault().post(new ChatMessagesUpdatedEvent());
    }


    public void processChatMessageForNotification(final String title, final String message, final String chatroomId, final String iconUri) {
        Log.d(TAG, "processChatMessageForNotification: " + String.format("message: %s, chatroom: %s", message, chatroomId));

        Query query = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.db_node_chatrooms))
                .orderByKey()
                .equalTo(chatroomId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                Chatroom chatroom = snapshot.getValue(Chatroom.class);
                Log.d(TAG, "onDataChange: chatroom: " + chatroom.getId());

                int numMessagesSeen;
                try {
                    numMessagesSeen = snapshot
                            .child(context.getString(R.string.db_node_users))
                            .child(loginDao.getCurrentUser().getUid())
                            .child(context.getString(R.string.field_last_message_seen))
                            .getValue(Integer.class);
                } catch (NullPointerException e) {
                    numMessagesSeen = 0;
                }

                int newMessageNumber = (int) snapshot
                        .child(context.getString(R.string.field_chatroom_message_number)).getChildrenCount();

                newMessageNumber -= numMessagesSeen;
                Log.d(TAG, "onDataChange: num pending messages: " + newMessageNumber);

                //EventBus.getDefault().post(new ChatNotificationEvent(title, message, iconUri, chatroom, newMessageNumber));
                MessageNotificationService_.intent(context).handleChatMessage(title, message, chatroom, newMessageNumber, R.mipmap.ic_launcher).start();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //do nothing
            }
        });
    }

    public boolean sendChatroomMessage(String chatroomId, final String offerId, final String offererId, @NonNull ChatMessage chatMessage) {

        DatabaseReference chatroomNodeReference = FirebaseDatabase.getInstance().getReference(context.getString(R.string.db_node_chatrooms));
        try {
            DatabaseReference concreteChatMessageReference = chatroomNodeReference.child(chatroomId).child(context.getString(R.string.db_node_chatroom_messages));
            DatabaseReference pushedReference = concreteChatMessageReference.push();
            chatMessage.setId(pushedReference.getKey());
            pushedReference.setValue(chatMessage);
            Log.d(TAG, "sendChatroomMessage: message sent: " + chatMessage);
        } catch (NullPointerException e) {
            chatroomDao.createChatroom(chatMessage.getSenderId(), offererId, offerId);
            EventBus.getDefault().post(new ChatMessageQueuedEvent(chatMessage));
        }

        return true;
    }
}
