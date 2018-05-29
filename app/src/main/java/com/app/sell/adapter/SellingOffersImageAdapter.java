package com.app.sell.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.sell.R;
import com.app.sell.model.Offer;
import com.bumptech.glide.Glide;

import java.util.List;

public class SellingOffersImageAdapter extends BaseAdapter {
    private final List<Offer> offers;
    private final LayoutInflater mInflater;
    private final Context context;

    public SellingOffersImageAdapter(Context context, List<Offer> offers) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.offers = offers;
    }

    @Override
    public int getCount() {
        return offers.size();
    }

    @Override
    public Offer getItem(int i) {
        return offers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView picture;
        TextView name;

        if (v == null) {
            v = mInflater.inflate(R.layout.grid_item, viewGroup, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }

        picture = (ImageView) v.getTag(R.id.picture);
        name = (TextView) v.getTag(R.id.text);

        Offer offer = getItem(i);

        Glide.with(context).load(offer.getImage()).into(picture);
        name.setText(offer.getTitle());

        v.setPadding(8, 8, 8, 8);

        return v;
    }
}
