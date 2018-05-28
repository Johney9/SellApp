package com.app.sell;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.app.sell.adapter.CategoriesAdapter;
import com.app.sell.model.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

    Query databaseOffers;
    private List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        final GridView gridview = (GridView) findViewById(R.id.gridviewCategories);

        databaseOffers = FirebaseDatabase.getInstance().getReference("categories");
        categories = new ArrayList<>();

        databaseOffers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categories.clear();
                for (DataSnapshot offerSnapshot : dataSnapshot.getChildren()) {
                    Category category = offerSnapshot.getValue(Category.class);
                    categories.add(category);
                }

                gridview.setAdapter(new CategoriesAdapter(getApplicationContext(), categories));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Category category = (Category) gridview.getAdapter().getItem(position);
                Intent intent = new Intent(v.getContext(), MainActivity_.class);
                intent.putExtra("categoryId", category.getId());
                intent.putExtra("categoryName", category.getName());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }


}
