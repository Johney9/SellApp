package com.app.sell.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.sell.R;

import java.util.ArrayList;
import java.util.List;

public class HomeImageAdapter extends BaseAdapter {
    private final List<Item> mItems = new ArrayList<Item>();
    private final LayoutInflater mInflater;

    public HomeImageAdapter(Context context) {
        mInflater = LayoutInflater.from(context);

        mItems.add(new Item("Offer 1", R.drawable.sample_0));
        mItems.add(new Item("Offer 2", R.drawable.sample_1));
        mItems.add(new Item("Offer 3", R.drawable.sample_2));
        mItems.add(new Item("Offer 4", R.drawable.sample_3));
        mItems.add(new Item("Offer 5", R.drawable.sample_4));
        mItems.add(new Item("Offer 6", R.drawable.sample_5));
        mItems.add(new Item("Offer 7", R.drawable.sample_6));
        mItems.add(new Item("Offer 8", R.drawable.sample_7));
        mItems.add(new Item("Offer 9", R.drawable.sample_0));
        mItems.add(new Item("Offer 10", R.drawable.sample_1));
        mItems.add(new Item("Offer 11", R.drawable.sample_2));
        mItems.add(new Item("Offer 12", R.drawable.sample_3));
        mItems.add(new Item("Offer 13", R.drawable.sample_4));
        mItems.add(new Item("Offer 14", R.drawable.sample_5));
        mItems.add(new Item("Offer 15", R.drawable.sample_6));
        mItems.add(new Item("Offer 16", R.drawable.sample_7));
        mItems.add(new Item("Offer 17", R.drawable.sample_0));
        mItems.add(new Item("Offer 18", R.drawable.sample_1));
        mItems.add(new Item("Offer 19", R.drawable.sample_2));
        mItems.add(new Item("Offer 20", R.drawable.sample_3));
        mItems.add(new Item("Offer 21", R.drawable.sample_4));
        mItems.add(new Item("Offer 22", R.drawable.sample_5));
        mItems.add(new Item("Offer 23", R.drawable.sample_6));
        mItems.add(new Item("Offer 24", R.drawable.sample_7));
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
        return mItems.get(i).drawableId;
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

        picture.setImageResource(item.drawableId);
        name.setText(item.name);

        v.setPadding(8, 8, 8, 8);

        return v;
    }

    private static class Item {
        public final String name;
        public final int drawableId;

        Item(String name, int drawableId) {
            this.name = name;
            this.drawableId = drawableId;
        }
    }
}
