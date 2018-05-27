package com.app.sell;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.app.sell.dao.LoginDao;
import com.app.sell.events.UserRetrievedEvent;
import com.app.sell.model.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

@EActivity(R.layout.activity_account)
public class AccountActivity extends AppCompatActivity {

    private static final String TAG = "AccountActivity";

    @ViewsById({R.id.account_profile_username, R.id.account_content_username, R.id.account_content_email, R.id.account_content_location})
    List<TextView> textViews;

    @ViewById(R.id.account_profile_image)
    RoundedImageView profileImage;

    @Bean
    LoginDao loginDao;

    @AfterViews
    void init() {
        Toolbar toolbar = findViewById(R.id.account_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void bind(UserRetrievedEvent userRetrievedEvent) {

        if (!loginDao.isUserLoggedIn()) {
            loginDao.init();
            return;
        }

        User currentUser = loginDao.getCurrentUser();

        for (TextView textView : textViews) {
            String id = textView.getResources().getResourceName(textView.getId());

            if (id.contains("username")) {
                String username = currentUser.getUsername();
                textView.setText(username);
            }
            if (id.contains("email")) {
                textView.setText(currentUser.getEmail());
            }
            if (id.contains("location")) {
                textView.setText(currentUser.getLocation());
            }
        }
        Picasso.get().load(currentUser.getImage()).into(profileImage);
    }

    @Click(R.id.sign_out)
    public void signOut(final View view) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            LoginActivity_.intent(AccountActivity.this).start();
                            finish();
                        } else {
                            Log.w(TAG, "signOut:failure", task.getException());
                            Snackbar.make(view, R.string.sign_out_failed, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
        loginDao.removeLoggedInUser();
    }

    public void openEditAccountActivity(View view) {
        Intent intent = new Intent(view.getContext(), EditAccountActivity_.class);
        startActivity(intent);
    }

    public void openChangePictureActivity(View view) {
        Intent intent = new Intent(view.getContext(), ChangePictureActivity_.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bind(new UserRetrievedEvent(null));
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
