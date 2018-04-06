package com.app.sell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class OfferActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
    }

    public void openAskActivity(View view) {
        Intent intent = new Intent(view.getContext(), AskActivity.class);
        startActivity(intent);
    }

    public void openMakeOfferActivity(View view) {
        Intent intent = new Intent(view.getContext(), MakeOfferActivity.class);
        startActivity(intent);
    }
}
