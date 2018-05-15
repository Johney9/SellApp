package com.app.sell;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.sell.dao.LoginDao;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity
public class LocationActivity extends AppCompatActivity {

    TextView mQuestion;

    @ViewById(R.id.location_edit_text_location)
    EditText locationEditText;

    @ViewById(R.id.location_save_location_button)
    Button saveLocationButton;

    @Bean
    LoginDao loginDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        mQuestion = findViewById(R.id.question);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null && extras.getInt("requestCode") == PostOfferFinishFragment.REQ_SELL_LOCATION) {
                mQuestion.setText(R.string.selling_question);
            }
        }
    }

    @Click(R.id.location_save_location_button)
    void saveLocation() {

        loginDao.getCurrentUser().setLocation(locationEditText.getText().toString());
        loginDao.write(loginDao.getCurrentUser());
        finish();
    }

}
