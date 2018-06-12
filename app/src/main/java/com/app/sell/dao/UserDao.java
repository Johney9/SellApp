package com.app.sell.dao;

import android.content.Context;

import com.app.sell.R;
import com.app.sell.events.UserLoadedEvent;
import com.app.sell.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.greenrobot.eventbus.EventBus;

@EBean
public class UserDao {

    @RootContext
    Context context;

    public void loadUser(String userId) {
        FirebaseDatabase.getInstance()
                .getReference(context.getString(R.string.db_node_users))
                .child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        EventBus.getDefault().post(new UserLoadedEvent(user));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
