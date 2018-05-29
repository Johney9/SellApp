package com.app.sell.firebase.listeners;

import android.util.Log;

import com.app.sell.events.ChatroomLoadedEvent;
import com.app.sell.model.Chatroom;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoadExistingChatroomChildEventListener implements ChildEventListener {

    private static final String TAG = "ChatroomChildEventListen";
    private String userId;
    private String offererId;

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Chatroom concreteChatroom = dataSnapshot.getValue(Chatroom.class);
        Map users = concreteChatroom.getUsers();

        if (users.containsKey(userId) && users.containsKey(offererId)) {
            Log.d(TAG, "onChildAdded: chatroom loaded: " + concreteChatroom);
            EventBus.getDefault().post(new ChatroomLoadedEvent(concreteChatroom));
        }
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
