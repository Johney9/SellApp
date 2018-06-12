package com.app.sell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.sell.dao.LoginDao;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_edit_account)
public class EditAccountActivity extends AppCompatActivity {

    static final int LOCATION_REQUEST = 21225;
    @Bean
    LoginDao loginDao;

    @ViewById(R.id.editText_name)
    EditText editTextName;
    @ViewById(R.id.edit_account_location_textview)
    TextView editAccountLocationTextview;

    @AfterViews
    void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.account_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bind();
    }

    void bind() {
        editTextName.setHint(loginDao.getCurrentUser().getUsername());
        editAccountLocationTextview.setText(loginDao.getCurrentUser().getPrettyLocation());
    }

    public void openLocationActivity(View view) {
        Intent intent = new Intent(this, LocationActivity_.class);
        startActivity(intent);
    }

    public void saveChanges(View view) {
        String newUsername = String.valueOf(editTextName.getText());

        if(!newUsername.contentEquals("null") && !newUsername.contentEquals("")) {
            loginDao.getCurrentUser().setUsername(newUsername);
            loginDao.writeCurrentUser();
        }
        finish();
    }
}
