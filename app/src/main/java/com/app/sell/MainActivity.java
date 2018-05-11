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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.sell.adapter.OffersAdapter;
import com.app.sell.helper.BottomNavigationViewHelper;
import com.app.sell.model.Offer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity
public class MainActivity extends AppCompatActivity {

    private String[] sort = {"Newest first", "Closest first", "Price: Low to high", "Price: High to low"};
    @ViewById(R.id.navigation)
    BottomNavigationView navigation;
    DatabaseReference databaseOffers;
    private List<Offer> offers;

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
                    openNotificationsActivity();
                    return true;
                case R.id.navigation_photo:
                    openPostOfferActivity(findViewById(android.R.id.content));
                    return true;
                case R.id.navigation_offers:
                    transaction.replace(R.id.content, new MyOffersFragment()).commit();
                    return true;
                case R.id.navigation_account:
                    openAccountActivity();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, new HomeFragment()).commit();
        databaseOffers = FirebaseDatabase.getInstance().getReference("offers");
        offers = new ArrayList<>();
    }

    public void search(View view) {
        final GridView gridview = (GridView) findViewById(R.id.gridview);
        final SearchView searchView = (SearchView) findViewById(R.id.search_view);

        CharSequence searchQuery = searchView.getQuery();
        Query searchedOffers = databaseOffers.orderByChild("title").startAt(searchQuery.toString()).endAt(searchQuery.toString() + "\uf8ff");
        searchedOffers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                offers.clear();
                for (DataSnapshot offerSnapshot : dataSnapshot.getChildren()) {
                    Offer offer = offerSnapshot.getValue(Offer.class);
                    offers.add(offer);
                }

                gridview.setAdapter(new OffersAdapter(getApplicationContext(), offers));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() == 0) {
                    search(searchView);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                search(searchView);
                return true;
            }

        });
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
                                price.setText("$" + priceFrom + " - $" + priceTo);
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

    public void openNotificationsActivity() {
        Intent notificationsIntent = new Intent(MainActivity.this, NotificationsActivity.class);
        startActivity(notificationsIntent);
    }

    public void openAccountActivity() {
        Intent accountIntent = new Intent(MainActivity.this, AccountActivity.class);
        startActivity(accountIntent);
    }
}
