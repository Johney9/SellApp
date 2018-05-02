package com.app.sell;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.TextView;

import com.app.sell.adapter.UserOffersAdapter;
import com.app.sell.model.Offer;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView textView = findViewById(R.id.profile_username);
        textView.setText(getIntent().getStringExtra("username"));
        GridView mGridView = findViewById(R.id.profile_offers_gridview);
        mGridView.setAdapter(new UserOffersAdapter(this, new ArrayList<Offer>()));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
