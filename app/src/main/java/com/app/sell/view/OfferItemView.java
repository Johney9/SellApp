package com.app.sell.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.app.sell.R;
import com.app.sell.model.Offer;
import com.bumptech.glide.Glide;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.item_offer)
public class OfferItemView extends CardView {

    @ViewById(R.id.profile_item_offer_imageview)
    ImageView profileOfferImageView;

    public OfferItemView(Context context) {
        super(context);
    }

    public void bind(Offer offer) {
        Glide.with(getContext()).load(offer.getImage()).into(profileOfferImageView);
    }
}
