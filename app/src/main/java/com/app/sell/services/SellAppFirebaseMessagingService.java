package com.app.sell.services;

import android.util.Log;

import com.app.sell.R;
import com.app.sell.model.Chatroom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class SellAppFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "SellAppFirebaseMessaging";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> notificationData;
        try {
            notificationData = remoteMessage.getData();

        } catch (NullPointerException npe) {
            Log.e(TAG, "onMessageReceived: NullPointerException", npe);
            return;
        }

        Log.d(TAG, "onMessageReceived: message data: " + notificationData);

        String title = notificationData.get(getString(R.string.message_data_title));
        String message = notificationData.get(getString(R.string.message_data_message));
        String identifyDataType = notificationData.get(getString(R.string.message_data_type));

        if(isApplicationInChat()) {

            buildAndSendOfferNotification(title, message);

        }
        else {
            if(identifyDataType.contains(getString(R.string.data_type_chat_message))) {

                String chatroomId = notificationData.get(getString(R.string.message_data_chatroom_id));
                processChatMessage(chatroomId, title, message);

            } else if(identifyDataType.contains(getString(R.string.data_type_offer_message))) {

                buildAndSendOfferNotification(title, message);

            }
        }
    }

    private void processChatMessage(final String chatroomId, final String title, final String message) {
        Log.d(TAG, "handleChatMessage: " + chatroomId);

        Query query = FirebaseDatabase.getInstance().getReference().child(getString(R.string.db_node_chatrooms))
                .orderByKey()
                .equalTo(chatroomId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();

                Chatroom chatroom = new Chatroom();
                Map<String, Object> objectMap = (HashMap<String, Object>) snapshot.getValue();

                chatroom.setChatroomName(objectMap.get(getString(R.string.field_chatroom_name)).toString());
                chatroom.setChatroomId(objectMap.get(getString(R.string.field_chatroom_id)).toString());
                chatroom.setAskerId(objectMap.get(getString(R.string.field_asker_id)).toString());
                chatroom.setOffererId(objectMap.get(getString(R.string.field_owner_id)).toString());
                chatroom.setOfferId(objectMap.get(getString(R.string.field_offer_id)).toString());

                Log.d(TAG, "onDataChange: chatroom: " + chatroom);

                //TODO: make this work with number of messages.
                /*
                int numMessagesSeen = Integer.parseInt(snapshot
                        .child(getString(R.string.db_node_users))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.field_last_message_seen))
                        .getValue().toString());
                        */
                int numMessagesSeen = 0;

                int numMessages = (int) snapshot
                        .child(getString(R.string.field_chatroom_messages)).getChildrenCount();

                numMessages -= numMessagesSeen;
                Log.d(TAG, "onDataChange: num pending messages: " + numMessages);

                buildAndSendChatNotification(title, message, chatroom, numMessages);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //do nothing
            }
        });
    }

    private boolean isApplicationInChat() {
        boolean isChatRunning = false;

        //TODO: implement logic which checks if chat is active or not.

        return isChatRunning;
    }

    private void buildAndSendOfferNotification(String title, String message) {
        MessageNotificationService_.intent(this).handleOfferMessage(title, message, R.mipmap.ic_launcher).start();
    }

    private void buildAndSendChatNotification(String title, String message, Chatroom chatroom, int numberOfMessages) {
        MessageNotificationService_.intent(this).handleChatMessage(title, message, chatroom, numberOfMessages, R.mipmap.ic_launcher).start();
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
