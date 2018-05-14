package com.app.sell.dao;

import android.content.Context;
import android.util.Log;

import com.app.sell.LoginActivity_;
import com.app.sell.events.UserRetrievedEvent;
import com.app.sell.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.greenrobot.eventbus.EventBus;

@EBean(scope = EBean.Scope.Singleton)
public class UserDao {

    private static final String USERS_TAG = "users";
    @RootContext
    Context context;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private User currentUser;

    @AfterInject
    public void init() {

        String uid = getFirebaseUserOrPromptLogin(mAuth).getUid();
        final DatabaseReference usersRef = db.getReference(USERS_TAG + "/" + uid);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setCurrentUser(dataSnapshot.getValue(User.class));
                if(getCurrentUser() == null) {
                    registerNewUser(getCurrentUser(), mAuth);
                }
                EventBus.getDefault().post(new UserRetrievedEvent(getCurrentUser()));
                Log.d("current user loaded", "current user loaded");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("users cancelled", "users cancelled");
            }
        });
    }

    private void registerNewUser(User currentUser, FirebaseAuth mAuth) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser != null) {
            setCurrentUser(new User(firebaseUser));
            writeCurrentUser();
        }
    }

    private FirebaseUser getFirebaseUserOrPromptLogin(FirebaseAuth auth) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null)
            LoginActivity_.intent(context).start();

        return user;
    }

    public void write(User user) {
        String reference = USERS_TAG + "/" + user.getUid();
        db.getReference(reference).setValue(user);
    }

    public void writeCurrentUser() {
        write(currentUser);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
