package com.app.sell;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.app.sell.adapter.HomeImageAdapter;
import com.app.sell.helper.BottomNavigationViewHelper;

public class MainActivity extends AppCompatActivity {

    private String[] sort = {"Newest first", "Closest first", "Price: Low to high", "Price: High to low"};

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    transaction.replace(R.id.content, new HomeFragment()).commit();
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_offers);
                    Intent notificationsIntent = new Intent(MainActivity.this, NotificationsActivity.class);
                    startActivity(notificationsIntent);
                    return true;
                case R.id.navigation_photo:
                    openPostOfferActivity(findViewById(android.R.id.content));
                    return true;
                case R.id.navigation_offers:
                    transaction.replace(R.id.content, new MyOffersFragment()).commit();
                    return true;
                case R.id.navigation_account:
                    mTextMessage.setText(R.string.title_account);
                    Intent accountIntent = new Intent(MainActivity.this, AccountActivity.class);
                    startActivity(accountIntent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, new HomeFragment()).commit();
    }

    public void openSortDialog(View view) {
        AlertDialog.Builder sortDialog = new AlertDialog.Builder(this);
        //alt_bld.setIcon(R.drawable.icon);
        sortDialog.setTitle("Select a sorting");
        sortDialog.setSingleChoiceItems(sort, -1, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Toast.makeText(getApplicationContext(),
                        "Sort = " + sort[item], Toast.LENGTH_SHORT).show();
                TextView sortText = (TextView) findViewById(R.id.sort);
                sortText.setText(sort[item]);
                dialog.dismiss();

            }
        });
        AlertDialog alert = sortDialog.create();
        alert.show();
    }

    public void openPriceDialog(View view) {
        LayoutInflater li = LayoutInflater.from(this);
        View priceView = li.inflate(R.layout.price_layout, null);
        final EditText priceFromText = (EditText) priceView
                .findViewById(R.id.priceFrom);
        final EditText priceToText = (EditText) priceView
                .findViewById(R.id.priceTo);
        AlertDialog.Builder priceDialogBuilder = new AlertDialog.Builder(this);
        priceDialogBuilder.setTitle("Select a pricing range");
        priceDialogBuilder.setView(priceView);
        priceDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String priceFrom = priceFromText.getText().toString();
                                String priceTo = priceToText.getText().toString();
                                final EditText price = findViewById(R.id.price);
                                price.setText("$"+priceFrom+" - $"+priceTo);
                                Toast.makeText(getApplicationContext(),
                                        "Price from = " + priceFrom + "\nPrice to = " + priceTo, Toast.LENGTH_SHORT).show();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog priceDialog = priceDialogBuilder.create();

        // show it
        priceDialog.show();
    }

    public void openCategoriesActivity(View view) {
        Intent intent = new Intent(view.getContext(), CategoriesActivity.class);
        startActivity(intent);
    }

    public void openLocationActivity(View view) {
        Intent intent = new Intent(view.getContext(), LocationActivity.class);
        startActivity(intent);
    }

    public void openPostOfferActivity(View view) {
        Intent intent = new Intent(view.getContext(), PostOfferActivity.class);
        startActivity(intent);
    }
}
