package com.app.sell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.app.sell.dao.LoginDao;
import com.app.sell.model.User;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewsById;

import java.util.List;

@EActivity(R.layout.activity_edit_account)
public class EditAccountActivity extends AppCompatActivity {

    static final int LOCATION_REQUEST = 21225;
    @Bean
    LoginDao loginDao;

    @ViewsById({R.id.editText_name, R.id.editText_email, R.id.editText_password})
    List<EditText> textViews;

    @AfterViews
    void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.account_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateWithUserData();
    }

    void populateWithUserData() {
        User currentUser = loginDao.getCurrentUser();

        for (EditText editText : textViews) {
            String id = editText.getResources().getResourceName(editText.getId());

            if (id.contains("name")) editText.setText(currentUser.getUsername());
            if (id.contains("email")) editText.setText(currentUser.getEmail());
        }
    }

    public void openLocationActivity(View view) {
        Intent intent = new Intent(this, LocationActivity_.class);
        startActivity(intent);
    }
}
