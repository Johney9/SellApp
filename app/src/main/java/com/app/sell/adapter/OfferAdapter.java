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

import java.util.ArrayList;
import java.util.List;

public class OfferAdapter extends BaseAdapter {
    private final List<Item> mItems = new ArrayList<Item>();
    private final LayoutInflater mInflater;
    private final Context context;

    public OfferAdapter(Context context, List<Offer> offers) {
        mInflater = LayoutInflater.from(context);
        this.context = context;

        for(Offer offer: offers) {
            mItems.add(new Item(offer.getTitle(), offer.getImage()));
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Item getItem(int i) {
        return mItems.get(i);
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

        Item item = getItem(i);

        Glide.with(context).load(item.image).into(picture);
        name.setText(item.name);

        v.setPadding(8, 8, 8, 8);

        return v;
    }

    private static class Item {
        public final String name;
        public final String image;

        Item(String name, String image) {
            this.name = name;
            this.image = image;
        }
    }
}
