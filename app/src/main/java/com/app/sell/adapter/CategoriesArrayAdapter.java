package com.app.sell.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.sell.R;
import com.app.sell.model.Category;

import java.util.ArrayList;

public class CategoriesArrayAdapter extends ArrayAdapter<Category> {
    public CategoriesArrayAdapter(Context context, ArrayList<Category> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Category category = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_layout, parent, false);
        }
        // Lookup view for data population
        TextView categoryName = (TextView) convertView.findViewById(R.id.category_name);
        // Populate the data into the template view using the data object
        categoryName.setText(category.getName());
        // Return the completed view to render on screen
        return convertView;
    }
}
