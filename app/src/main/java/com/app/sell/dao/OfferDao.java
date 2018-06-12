package com.app.sell.dao;

import android.content.Context;
import android.util.Log;

import com.app.sell.R;
import com.app.sell.events.OfferLoadedEvent;
import com.app.sell.model.Offer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

@EBean
public class OfferDao {

    private static final String TAG = "OfferDao";

    @RootContext
    Context context;

    public void loadOffer(final String offerId) {
        Log.d(TAG, "loadOffer: started");
        FirebaseDatabase.getInstance()
                .getReference(context.getString(R.string.db_node_offers))
                .child(offerId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Offer offer = dataSnapshot.getValue(Offer.class);
                        EventBus.getDefault().post(new OfferLoadedEvent(offer));
                        Log.d(TAG, "loadOffer: offer loaded: " + Objects.requireNonNull(offer).getId());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "loadOffer error: ", databaseError.toException());
                    }
                });
    }
}
