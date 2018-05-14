package com.app.sell.dao;

import android.util.Log;

import com.app.sell.events.OffersRetrievedEvent;
import com.app.sell.events.UserRetrievedEvent;
import com.app.sell.model.Offer;
import com.app.sell.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

@EBean
public class SpecificUserOffersDao {

    private static final String USERS_TAG = "users";
    private static final String OFFERS_TAG = "offers";
    FirebaseDatabase mDatabase;

    @AfterInject
    void init() {
        mDatabase = FirebaseDatabase.getInstance();
    }

    public void getData(final String uid) {
        final DatabaseReference usersRef = mDatabase.getReference(USERS_TAG + "/" + uid);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    Log.d("No user found.", "No user found.");
                } else {
                    EventBus.getDefault().post(new UserRetrievedEvent(user));
                    Log.d("user loaded", "current user loaded");
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
                    if (offer != null && offer.getOffererId().contains(uid)) {
                        userOffers.add(offer);
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
