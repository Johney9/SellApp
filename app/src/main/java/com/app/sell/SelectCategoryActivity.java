package com.app.sell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectCategoryActivity extends AppCompatActivity {
    //Ucitati iz firebase-a
    String[] categories = {"Baby & kids", "Furniture", "Clothing & shoes", "Cars & trucks",
            "Household", "Cell phones", "Electronics", "Jewelry & accessories", "Games & toys",
            "Appliances", "Collectibles", "Antiques", "General", "Tools", "Beauty & health", "Boats & marine",
            "Sports & outdoors", "Arts & crafts", "TVs", "Home & garden", "CDs & DVDs", "Books & magazines",
            "Motorcycles", "Computer equipment", "Software", "Bicycles", "Video games", "Audio equipment",
            "Video equipment", "Photography", "Auto parts", "Musical instruments", "Tickets", "Pet supplies",
            "Business equipment", "Wedding", "Campers & RVs", "Free", "Exercise"};
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.category_layout, categories);

        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String selected = (String) lv.getItemAtPosition(position).toString();

                int resultCode = RESULT_OK;
                Intent resultIntent = new Intent();
                resultIntent.putExtra("SELECTED_CATEGORY", selected);
                setResult(resultCode, resultIntent);
                finish();
            }
        });
    }

}
