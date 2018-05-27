package com.app.sell.dao;

import android.content.Context;
import android.util.Log;

import com.app.sell.R;
import com.app.sell.events.LoggedInEvent;
import com.app.sell.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.greenrobot.eventbus.EventBus;

@EBean(scope = EBean.Scope.Singleton)
public class LoginDao {

    private static final String TAG = "LoginDao";
    private static String USERS_TAG;
    @RootContext
    Context context;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private User currentUser;

    public void init() {
        USERS_TAG = context.getString(R.string.db_node_users);

        String uid = mAuth.getCurrentUser().getUid();
        final DatabaseReference usersRef = db.getReference(USERS_TAG + "/" + uid);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setCurrentUser(dataSnapshot.getValue(User.class));
                if (getCurrentUser() == null || getCurrentUser().getUid() == null) {
                    registerNewUser(mAuth);
                }
                EventBus.getDefault().postSticky(new LoggedInEvent(getCurrentUser()));
                Log.d(TAG, "current user loaded");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "users cancelled");
            }
        });
    }

    private void registerNewUser(FirebaseAuth mAuth) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            setCurrentUser(new User(firebaseUser));
            writeCurrentUser();
        }
    }

    public boolean isUserLoggedIn() {
        boolean isLogged = false;
        if (getCurrentUser() != null)
            isLogged = true;
        return isLogged;
    }

    public void removeLoggedInUser() {
        setCurrentUser(null);
    }

    public void write(User user) {
        String reference = USERS_TAG + "/" + user.getUid();
        db.getReference(reference).setValue(user);
    }

    public void writeCurrentUser() {
        write(currentUser);
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    private void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
