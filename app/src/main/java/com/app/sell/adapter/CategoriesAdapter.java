package com.app.sell.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.sell.R;
import com.app.sell.model.Category;
import com.bumptech.glide.Glide;

import java.util.List;

public class CategoriesAdapter extends BaseAdapter {
    private final List<Category> categories;
    private final LayoutInflater mInflater;
    private final Context context;

    public CategoriesAdapter(Context context, List<Category> categories) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.categories = categories;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Category getItem(int i) {
        return categories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView picture;
        TextView title;

        if (v == null) {
            v = mInflater.inflate(R.layout.grid_item, viewGroup, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }

        picture = (ImageView) v.getTag(R.id.picture);
        title = (TextView) v.getTag(R.id.text);

        Category category = getItem(i);

        Glide.with(context).load(category.getImage()).into(picture);
        title.setText(category.getName());

        v.setPadding(8, 8, 8, 8);

        return v;
    }
}
