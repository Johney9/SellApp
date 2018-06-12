package com.app.sell;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.app.sell.dao.LoginDao;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressLint("Registered")
@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9002;
    private static final String TAG = LoginActivity.class.getSimpleName();
    FirebaseAuth mAuth;
    @Bean
    LoginDao loginDao;

    @Override
    protected void onResume() {
        super.onResume();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startSignIn();
        } else {
            startMainActivity();
        }
    }

    private void startSignIn() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(getAvailableProviders())
                        .build(),
                RC_SIGN_IN
        );
    }

    private List<AuthUI.IdpConfig> getAvailableProviders() {
        List<AuthUI.IdpConfig> providers = new ArrayList<>();

        providers.add(new AuthUI.IdpConfig.GoogleBuilder()
                .build()
        );

        providers.add(new AuthUI.IdpConfig.EmailBuilder()
                .setRequireName(true)
                .setAllowNewAccounts(true)
                .build()
        );

        return providers;
    }

    /**
     * Callback when Login is finished
     */
    @OnActivityResult(RC_SIGN_IN)
    void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);
        Log.d("handleSignInResponse:", response != null ? response.toString() : null);

        // Successfully signed in
        if (resultCode == RESULT_OK) {
            startMainActivity();
            finish();
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                showSnackbar(R.string.sign_in_cancelled);
                return;
            }

            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                showSnackbar(R.string.no_internet_connection);
                return;
            }

            showSnackbar(R.string.unknown_error);
            Log.e(TAG, "Sign-in error: ", response.getError());
        }
    }

    private void startMainActivity() {
        loginDao.init();
        MainActivity_.intent(this).start();
    }

    private void showSnackbar(@StringRes int errorMessageRes) {
        try {
            Snackbar.make(Objects.requireNonNull(this.getCurrentFocus()), errorMessageRes, Snackbar.LENGTH_LONG).show();
        } catch (NullPointerException e) {
            Log.e(TAG, "showSnackbar failed: ", e);
        }
    }
}
