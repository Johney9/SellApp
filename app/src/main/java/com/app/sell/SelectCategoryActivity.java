package com.app.sell;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.app.sell.adapter.CategoriesArrayAdapter;
import com.app.sell.model.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectCategoryActivity extends AppCompatActivity {
    Query databaseCategories;
    private ArrayList<Category> categories;

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);
        ArrayAdapter adapter = new ArrayAdapter<Category>(this, R.layout.category_layout, categories);

        lv = (ListView) findViewById(R.id.listView);


        databaseCategories = FirebaseDatabase.getInstance().getReference("categories");
        categories = new ArrayList<>();

        databaseCategories.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categories.clear();
                for (DataSnapshot offerSnapshot : dataSnapshot.getChildren()) {
                    Category category = offerSnapshot.getValue(Category.class);
                    if (!category.getName().equalsIgnoreCase("Popular near me")) {
                        categories.add(category);
                    }

                }
                lv.setAdapter(new CategoriesArrayAdapter(getApplicationContext(), categories));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //Category category = (Category) lv.getAdapter().getItem(position);
                Category category = (Category) lv.getItemAtPosition(position);

                int resultCode = RESULT_OK;
                Intent resultIntent = new Intent();
                resultIntent.putExtra("SELECTED_CATEGORY_ID", category.getId());
                resultIntent.putExtra("SELECTED_CATEGORY_NAME", category.getName());
                setResult(resultCode, resultIntent);
                finish();
            }
        });

    }

}
