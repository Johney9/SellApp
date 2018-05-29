package com.app.sell.dao;

import android.content.Context;

import com.app.sell.R;
import com.app.sell.events.NotificationsUpdatedEvent;
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
}
