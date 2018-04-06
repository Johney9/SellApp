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

public class CategoryImageAdapter extends BaseAdapter {
    private final List<Item> mItems = new ArrayList<Item>();
    private final LayoutInflater mInflater;

    public CategoryImageAdapter(Context context) {
        mInflater = LayoutInflater.from(context);

        mItems.add(new Item("Category 1", R.drawable.sample_0));
        mItems.add(new Item("Category 2", R.drawable.sample_1));
        mItems.add(new Item("Category 3", R.drawable.sample_2));
        mItems.add(new Item("Category 4", R.drawable.sample_3));
        mItems.add(new Item("Category 5", R.drawable.sample_4));
        mItems.add(new Item("Category 6", R.drawable.sample_5));
        mItems.add(new Item("Category 7", R.drawable.sample_6));
        mItems.add(new Item("Category 8", R.drawable.sample_7));
        mItems.add(new Item("Category 9", R.drawable.sample_0));
        mItems.add(new Item("Category 10", R.drawable.sample_1));
        mItems.add(new Item("Category 11", R.drawable.sample_2));
        mItems.add(new Item("Category 12", R.drawable.sample_3));
        mItems.add(new Item("Category 13", R.drawable.sample_4));
        mItems.add(new Item("Category 14", R.drawable.sample_5));
        mItems.add(new Item("Category 15", R.drawable.sample_6));
        mItems.add(new Item("Category 16", R.drawable.sample_7));
        mItems.add(new Item("Category 17", R.drawable.sample_0));
        mItems.add(new Item("Category 18", R.drawable.sample_1));
        mItems.add(new Item("Category 19", R.drawable.sample_2));
        mItems.add(new Item("Offer 20", R.drawable.sample_3));
        mItems.add(new Item("Category 21", R.drawable.sample_4));
        mItems.add(new Item("Offer 22", R.drawable.sample_5));
        mItems.add(new Item("Category 23", R.drawable.sample_6));
        mItems.add(new Item("Category 24", R.drawable.sample_7));
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
