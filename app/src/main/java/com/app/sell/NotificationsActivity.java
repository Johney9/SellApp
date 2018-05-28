package com.app.sell;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.app.sell.adapter.NotificationAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_notifications)
public class NotificationsActivity extends AppCompatActivity {

    @Bean
    NotificationAdapter notificationAdapter;

    @AfterViews
    void init() {
        Toolbar toolbar = findViewById(R.id.notifications_toolbar);
        setSupportActionBar(toolbar);

        final RecyclerView notificationsList = findViewById(R.id.notifications_list);
        notificationsList.setAdapter(notificationAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
