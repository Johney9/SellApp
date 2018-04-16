package com.app.sell;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class LocationActivity extends AppCompatActivity {

    TextView mQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        mQuestion = findViewById(R.id.question);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null && extras.getInt("requestCode") == PostOfferFinishFragment.REQ_SELL_LOCATION) {
                mQuestion.setText("Where are you selling?");
            }
        }
    }
}
