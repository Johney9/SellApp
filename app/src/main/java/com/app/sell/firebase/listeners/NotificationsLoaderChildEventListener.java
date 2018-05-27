package com.app.sell.firebase.listeners;

import android.content.Context;
import android.util.Log;

import com.app.sell.R;
import com.app.sell.dao.NotificationsDao;
import com.app.sell.model.ChatMessage;
import com.app.sell.model.Chatroom;
import com.app.sell.model.NotificationItem;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NotificationsLoaderChildEventListener implements ChildEventListener {

    private static final String TAG = "NotificationsLoaderEventListener";
    NotificationsDao notificationsDao;
    Context context;

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        final Chatroom concreteChatroom = dataSnapshot.getValue(Chatroom.class);
        Log.d(TAG, "onChildAdded: loaded chatroom: " + concreteChatroom);
        dataSnapshot.getRef().child(context.getString(R.string.db_node_chatroom_messages)).limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage lastMessage = dataSnapshot.getValue(ChatMessage.class);
                Log.d(TAG, "onChildAdded: loaded message: " + lastMessage);
                long timestamp = lastMessage.getTimestamp();
                //get the datetime in a formatted way for the current locale
                String description = lastMessage.getSenderUsername() + ": " + lastMessage.getMessage();
                String title = concreteChatroom.getChatroomName();
                String chatroomId = concreteChatroom.getId();
                String iconUri = concreteChatroom.getOfferImageUri();
                String username = lastMessage.getSenderUsername();
                NotificationItem notification = new NotificationItem(username, title, description, iconUri, chatroomId, timestamp);
                Log.d(TAG, "onDataChange: notfication: " + notification);
                notificationsDao.getNotificationsMap().put(chatroomId, notification);
                notificationsDao.publish();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
