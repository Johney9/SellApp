package com.app.sell.dao;

import android.content.Context;

import com.app.sell.R;
import com.app.sell.events.NotificationsUpdatedEvent;
import com.app.sell.firebase.listeners.NotificationsLoaderChildEventListener;
import com.app.sell.model.NotificationItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.AfterInject;
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

@EBean
public class NotificationsDao {

    private static final String TAG = "NotificationsDao";
    @RootContext
    Context context;
    @Bean
    LoginDao loginDao;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Getter
    private List<NotificationItem> notifications = new ArrayList<>();
    @Getter
    private Map<String, NotificationItem> notificationsMap = new HashMap<>();

    //@AfterInject
    void init() {
        /*
        database.getReference(context.getString(R.string.db_node_chatrooms)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot concreteChatroomSnapshot :
                        dataSnapshot.getChildren()) {
                    Map<String, Object> users = concreteChatroomSnapshot.child("users").getValue(new GenericTypeIndicator<Map<String, Object>>(){});
                    Log.d(TAG, "onDataChange: users: " + String.valueOf(users));
                    if(users.containsKey(loginDao.getCurrentUser().getUid())) {
                        try {
                            Chatroom concreteChatroom = concreteChatroomSnapshot.getValue(Chatroom.class);
                            ChatMessage lastMessage = Iterables.getLast(concreteChatroom.getChatroomMessages().values());
                            long timestamp = lastMessage.getTimestamp();
                            //get the datetime in a formatted way for the current locale
                            String description = lastMessage.getSenderUsername() + ": " + lastMessage.getMessage();
                            String title = concreteChatroom.getChatroomName();
                            String chatroomId = concreteChatroomSnapshot.getKey();
                            String iconUri = concreteChatroomSnapshot.child("offerImageUri").getValue(String.class);
                            NotificationItem notification = new NotificationItem(title, description, iconUri, chatroomId, timestamp);
                            Log.d(TAG, "onDataChange: notfication: " + notification);
                            notificationsMap.put(chatroomId, notification);
                        } catch (NoSuchElementException|NullPointerException e) {
                            Log.e(TAG, "onDataChange: ", e);
                        }
                    }
                }
                publish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //do nothing
            }
        });
        */
        notifications.clear();
        String currentUserId = loginDao.getCurrentUser().getUid();

        database.getReference(context.getString(R.string.db_node_chatrooms))
                .orderByChild(context.getString(R.string.db_node_users) + "/" + currentUserId).limitToLast(30)
                .addChildEventListener(new NotificationsLoaderChildEventListener(this, context));
    }

    @AfterInject
    public void initialize() {
        database.getReference(context.getString(R.string.db_node_notifications))
                .child(loginDao.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        notificationsMap = dataSnapshot.getValue(new GenericTypeIndicator<Map<String, NotificationItem>>() {
                        });
                        publish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void publish() {
        notifications.clear();
        if (notificationsMap != null) {
            notifications.addAll(notificationsMap.values());
            Collections.sort(notifications);
            Collections.reverse(notifications);
        }
        EventBus.getDefault().postSticky(new NotificationsUpdatedEvent());
    }

    /*
    public void publish() {
        if(notifications != null) {
            Collections.sort(notifications);
            Collections.reverse(notifications);
            EventBus.getDefault().postSticky(new NotificationsUpdatedEvent());
        }
    }
    */
}
