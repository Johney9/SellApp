package com.app.sell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

    public void openProfileActivity(View view) {
        Intent intent = new Intent(view.getContext(), ProfileActivity.class);
        TextView textView = view.findViewById(R.id.user_name);
        intent.putExtra("username", textView.getText());
        startActivity(intent);
    }
}
