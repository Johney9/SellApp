package com.app.sell;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;

import java.util.List;

@EActivity(R.layout.activity_account)
public class AccountActivity extends AppCompatActivity {

    private static final String TAG = "AccountActivity";

    @ViewsById({R.id.account_profile_username, R.id.account_content_username, R.id.account_content_email, R.id.account_content_location})
    List<TextView> textViews;

    @ViewById(R.id.account_profile_image)
    RoundedImageView profileImage;

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
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        for (TextView textView : textViews) {
            String id = textView.getResources().getResourceName(textView.getId());

            if(id.contains("username")) textView.setText(currentUser.getDisplayName());
            if(id.contains("email")) textView.setText(currentUser.getEmail());
            if(id.contains("location")) textView.setText(R.string.default_location);
        }
        Picasso.get().load(currentUser.getPhotoUrl()).into(profileImage);
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
    }

    public void openEditAccountActivity(View view) {
        Intent intent = new Intent(view.getContext(), EditAccountActivity_.class);
        startActivity(intent);
    }

    public void openLoginActivity(View view) {
        Intent intent = new Intent(view.getContext(), LoginActivity_.class);
        startActivity(intent);
    }

    public void openChangePictureActivity(View view) {
        Intent intent = new Intent(view.getContext(), ChangePictureActivity_.class);
        startActivity(intent);
    }
}
