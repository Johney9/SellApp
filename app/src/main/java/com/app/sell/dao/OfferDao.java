package com.app.sell.dao;

import android.content.Context;

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

@EBean
public class OfferDao {

    @RootContext
    Context context;

    public void loadOffer(String offerId) {
        FirebaseDatabase.getInstance()
                .getReference(context.getString(R.string.db_node_offers))
                .child(offerId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Offer offer = dataSnapshot.getValue(Offer.class);
                        EventBus.getDefault().post(new OfferLoadedEvent(offer));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
