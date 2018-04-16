package com.app.sell;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void openEditAccountActivity(View view) {
        Intent intent = new Intent(view.getContext(), EditAccountActivity.class);
        startActivity(intent);
    }

    public void openLoginActivity(View view) {
        Intent intent = new Intent(view.getContext(), LoginActivity.class);
        startActivity(intent);
    }
}
