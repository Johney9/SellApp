package com.app.sell.services;

import android.content.SharedPreferences;
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

        if(identifyDataType.contains(getString(R.string.data_type_chat_message)) && !isChatActivityRunning()) {

            String chatroomId = notificationData.get(getString(R.string.message_data_chatroom_id));
            processChatMessage(chatroomId, title, message);

        } else if(identifyDataType.contains(getString(R.string.data_type_offer_message))) {

            String offerId = notificationData.get(getString(R.string.field_offer_id));
            buildAndSendOfferNotification(title, message, offerId);

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
                Map<String, Object> objectMap = (Map<String, Object>) snapshot.getValue();

                chatroom.setChatroomName(String.valueOf(objectMap.get(getString(R.string.field_chatroom_name))));
                chatroom.setChatroomId(String.valueOf(objectMap.get(getString(R.string.field_chatroom_id))));
                chatroom.setAskerId(String.valueOf(objectMap.get(getString(R.string.field_asker_id))));
                chatroom.setOffererId(String.valueOf(objectMap.get(getString(R.string.field_owner_id))));
                chatroom.setOfferId(String.valueOf(objectMap.get(getString(R.string.field_offer_id))));

                Log.d(TAG, "onDataChange: chatroom: " + chatroom);

                int numMessagesSeen;
                try {
                     numMessagesSeen = Integer.parseInt(snapshot
                            .child(getString(R.string.db_node_users))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(getString(R.string.field_last_message_seen))
                            .getValue().toString());
                } catch (NumberFormatException|NullPointerException e) {
                    Log.e(TAG, "onDataChange: ", e);
                    numMessagesSeen = 0;
                }

                int newMessages = (int) snapshot
                        .child(getString(R.string.field_chatroom_messages)).getChildrenCount();

                newMessages -= numMessagesSeen;
                Log.d(TAG, "onDataChange: num pending messages: " + newMessages);

                buildAndSendChatNotification(title, message, chatroom, newMessages);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //do nothing
            }
        });
    }

    protected Boolean isChatActivityRunning() {
        SharedPreferences sp = getSharedPreferences(getString(R.string.ask_activity_shared_pref), MODE_PRIVATE);
        return sp.getBoolean("active", false);
    }

    private void buildAndSendOfferNotification(String title, String message, String offerId) {
        MessageNotificationService_.intent(this).handleOfferMessage(title, message, offerId, R.mipmap.ic_launcher).start();
    }

    private void buildAndSendChatNotification(String title, String message, Chatroom chatroom, int numberOfMessages) {
        MessageNotificationService_.intent(this).handleChatMessage(title, message, chatroom, numberOfMessages, R.mipmap.ic_launcher).start();
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
