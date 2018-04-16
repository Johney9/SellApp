package com.app.sell;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.app.sell.adapter.HomeImageAdapter;
import com.app.sell.adapter.UserOffersAdapter;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView textView = findViewById(R.id.profile_username);
        textView.setText(getIntent().getStringExtra("username"));
        GridView mGridView = findViewById(R.id.profile_offers_gridview);
        mGridView.setAdapter(new UserOffersAdapter(this));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
