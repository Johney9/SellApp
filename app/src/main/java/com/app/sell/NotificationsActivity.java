package com.app.sell;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.app.sell.adapter.NotificationsAdapter;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Toolbar toolbar = findViewById(R.id.notifications_toolbar);
        setSupportActionBar(toolbar);

        ListView notificationsList = findViewById(R.id.notifications_list);
        notificationsList.setAdapter(new NotificationsAdapter(this));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
