package com.app.sell.dao;

import android.content.Context;
import android.util.Log;

import com.app.sell.R;
import com.app.sell.events.LoggedInEvent;
import com.app.sell.events.OffersRetrievedEvent;
import com.app.sell.model.Offer;
import com.app.sell.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

@EBean
public class SpecificUserOffersDao {

    private static final String TAG = "SpecificUserOffersDao";
    private static String USERS_TAG;
    private static String OFFERS_TAG;
    FirebaseDatabase mDatabase;

    @RootContext
    Context context;

    @AfterInject
    void init() {
        mDatabase = FirebaseDatabase.getInstance();
        USERS_TAG = context.getString(R.string.db_node_users);
        OFFERS_TAG = context.getString(R.string.db_node_offers);
    }

    public void getData(final String uid) {
        final DatabaseReference usersRef = mDatabase.getReference(USERS_TAG + "/" + uid);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    Log.d(TAG, "No user found.");
                } else {
                    EventBus.getDefault().post(new LoggedInEvent(user));
                    Log.d(TAG, "current user loaded");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("users cancelled", "users cancelled");
            }
        });

        final DatabaseReference offersRef = mDatabase.getReference(OFFERS_TAG);
        offersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Offer> userOffers = new ArrayList<>();
                for (DataSnapshot userOffer : dataSnapshot.getChildren()) {

                    Offer offer = userOffer.getValue(Offer.class);
                    try {
                        if (offer != null && offer.getOffererId().contains(uid) && !offer.getIsDeleted()) {
                            userOffers.add(offer);
                            Log.d(TAG, "added offer: " + offer.getId());
                        }
                    } catch (NullPointerException e) {
                        Log.e(TAG, "error adding offer: ", e);
                    }
                }
                EventBus.getDefault().post(new OffersRetrievedEvent(userOffers));
                Log.d("user offers loaded", "user offers loaded");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("user offers cancelled", "user offers cancelled");
            }
        });
    }
}
