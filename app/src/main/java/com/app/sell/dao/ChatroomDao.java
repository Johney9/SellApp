package com.app.sell.dao;

import android.content.Context;
import android.util.Log;

import com.app.sell.R;
import com.app.sell.events.ChatroomCreatedEvent;
import com.app.sell.events.ChatroomLoadedEvent;
import com.app.sell.firebase.listeners.LoadExistingChatroomChildEventListener;
import com.app.sell.model.ChatMessage;
import com.app.sell.model.Chatroom;
import com.app.sell.model.Offer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import lombok.NonNull;

@EBean(scope = EBean.Scope.Singleton)
public class ChatroomDao {

    private static final String TAG = "ChatroomDao";

    DatabaseReference chatroomNodeReference;
    DatabaseReference offerNodeReference;

    @RootContext
    Context context;

    @AfterInject
    void init() {
        chatroomNodeReference = FirebaseDatabase.getInstance().getReference(context.getString(R.string.db_node_chatrooms));
        offerNodeReference = FirebaseDatabase.getInstance().getReference(context.getString(R.string.db_node_offers));
    }

    public void loadChatroom(final String chatroomId) {
        Log.d(TAG, "loadChatroom: chatroomId" + chatroomId);
        DatabaseReference concreteChatroomReference = chatroomNodeReference.child(chatroomId);
        concreteChatroomReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Chatroom chatroom = dataSnapshot.getValue(Chatroom.class);
                Log.d(TAG, "onDataChange: chatroom: " + String.valueOf(chatroom));
                EventBus.getDefault().post(new ChatroomLoadedEvent(chatroom));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //do nothing
            }
        });
    }

    public void loadChatroom(@NonNull final String userId, @NonNull final String offererId, @NonNull final String offerId) {
        chatroomNodeReference
                .orderByChild("offerId")
                .equalTo(offerId)
                .addChildEventListener(new LoadExistingChatroomChildEventListener(userId, offererId));
    }

    public void createChatroom(@NonNull final String userId, @NonNull final String offererId, @NonNull final String offerId) {

        offerNodeReference.child(offerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Offer offer = dataSnapshot.getValue(Offer.class);

                final Chatroom newChatroom = createNewChatroom(offer, userId, offerId, offererId);

                DatabaseReference newChatroomReference = chatroomNodeReference.push();
                String newChatroomId = newChatroomReference.getKey();
                newChatroom.setId(newChatroomId);
                newChatroomReference.setValue(newChatroom);

                EventBus.getDefault().post(new ChatroomLoadedEvent(newChatroom));
                EventBus.getDefault().post(new ChatroomCreatedEvent(newChatroomId));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @android.support.annotation.NonNull
    private Chatroom createNewChatroom(Offer offer, @NonNull String userId, @NonNull String offerId, @NonNull String offererId) {
        final Chatroom newChatroom = new Chatroom();
        newChatroom.setAskerId(userId);
        newChatroom.setChatroomName("");
        newChatroom.setOfferId(offerId);
        Map<String, Map<String, Integer>> users = new HashMap<>();
        Map<String, Integer> lastMessageSeen = new HashMap<>();
        lastMessageSeen.put(context.getString(R.string.field_last_message_seen), 0);
        users.put(userId, lastMessageSeen);
        users.put(offererId, lastMessageSeen);
        newChatroom.setUsers(users);
        newChatroom.setChatroomName(offer.getTitle());
        newChatroom.setOfferImageUri(offer.getImage());
        newChatroom.setChatroomMessages(new HashMap<String, ChatMessage>());
        return newChatroom;
    }
}
