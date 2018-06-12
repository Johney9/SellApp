package com.app.sell.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.app.sell.OfferActivity_;
import com.app.sell.R;
import com.app.sell.dao.SpecificUserOffersDao;
import com.app.sell.events.OffersRetrievedEvent;
import com.app.sell.model.Offer;
import com.app.sell.view.OfferItemView;
import com.app.sell.view.OfferItemView_;
import com.app.sell.view.ViewWrapper;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

@EBean
public class UserOfferAdapter extends RecyclerViewAdapterBase<Offer, OfferItemView> {

    private static final String TAG = "UserOfferAdapter";
    Context context;

    public UserOfferAdapter(Context context) {
        this.context = context;
    }

    @Subscribe
    public void onDataUpdated(OffersRetrievedEvent offersRetrievedEvent) {
        this.items = offersRetrievedEvent.offers;
        this.notifyDataSetChanged();
        Log.d(TAG, "onDataUpdated: updated offers for user");
    }

    @Override
    protected OfferItemView onCreateItemView(ViewGroup parent, int viewType) {
        return OfferItemView_.build(parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewWrapper<OfferItemView> holder, int position) {
        OfferItemView view = holder.getView();
        final Offer offer= items.get(position);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserOfferAdapter.this.onClick(offer);
            }
        });
        view.bind(offer);
    }

    private void onClick(Offer offer) {
        OfferActivity_.intent(context).extra(context.getString(R.string.field_offer_id), offer.getId()).start();
    }
}
