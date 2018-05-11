package com.app.sell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.sell.dao.UserDao;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;

@EActivity
public class LocationActivity extends AppCompatActivity {

    TextView mQuestion;

    @ViewById(R.id.location_edit_text_location)
    EditText locationEditText;

    @ViewById(R.id.location_save_location_button)
    Button saveLocationButton;

    @Bean
    UserDao userDao;

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

    @Click(R.id.location_save_location_button)
    void saveLocation() {

        userDao.getCurrentUser().setLocation(locationEditText.getText().toString());
        userDao.write(userDao.getCurrentUser());
        finish();
    }

}
