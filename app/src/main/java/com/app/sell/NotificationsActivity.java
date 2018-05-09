package com.app.sell;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.app.sell.adapter.NotificationsAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_notifications)
public class NotificationsActivity extends AppCompatActivity {

    @AfterViews
    void init() {
        Toolbar toolbar = findViewById(R.id.notifications_toolbar);
        setSupportActionBar(toolbar);

        ListView notificationsList = findViewById(R.id.notifications_list);
        notificationsList.setAdapter(new NotificationsAdapter(this));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
