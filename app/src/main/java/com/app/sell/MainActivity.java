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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.sell.adapter.OffersAdapter;
import com.app.sell.dao.LoginDao;
import com.app.sell.helper.BottomNavigationViewHelper;
import com.app.sell.model.Offer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EActivity
public class MainActivity extends AppCompatActivity {

    private enum SORT {
        NEWEST_FIRST, PRICE_LOW_HIGH, PRICE_HIGH_LOW
    }

    private enum DISTANCE {
        FIVE, TEN, TWENTY, FIFTY, HUNDRED, ANY
    }

    private String[] sort = {"Newest first", "Price: Low to high", "Price: High to low"};
    private String[] distance = {"< 5 km", "< 10 km", "< 20 km", "< 50 km", "< 100 km", "Any"};
    @ViewById(R.id.navigation)
    BottomNavigationView navigation;
    DatabaseReference databaseOffers;
    private List<Offer> offers;
    private String priceFromEdit;
    private String priceToEdit;
    private String searchTermForQuery;
    private double priceFromForQuery;
    private double priceToForQuery;
    private SORT currentSorting;
    private DISTANCE currentDistance;
    @Bean
    LoginDao loginDao;

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
                    NotificationsActivity_.intent(getApplicationContext()).start();
                    return true;
                case R.id.navigation_photo:
                    openPostOfferActivity(findViewById(android.R.id.content));
                    return true;
                case R.id.navigation_offers:
                    transaction.replace(R.id.content, new MyOffersFragment()).commit();
                    return true;
                case R.id.navigation_account:
                    AccountActivity_.intent(getApplicationContext()).start();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startHomeScreen();
    }

    private void startHomeScreen() {
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, new HomeFragment()).commit();
        databaseOffers = FirebaseDatabase.getInstance().getReference("offers");
        offers = new ArrayList<>();
        priceFromEdit = "";
        priceToEdit = "";
        searchTermForQuery = "";
        priceFromForQuery = 0;
        priceToForQuery = Double.MAX_VALUE;
        currentSorting = SORT.NEWEST_FIRST;
        currentDistance = DISTANCE.ANY;
    }

    public void searchSort() {
        final GridView gridview = (GridView) findViewById(R.id.gridview);
        Query searchedOffers = databaseOffers;
        if(currentSorting == SORT.PRICE_HIGH_LOW || currentSorting == SORT.PRICE_LOW_HIGH) {
            searchedOffers = searchedOffers.orderByChild("price");
        } else if (currentSorting == SORT.NEWEST_FIRST) {
            searchedOffers = searchedOffers.orderByChild("timestamp");
        }
        searchedOffers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                offers.clear();
                for (DataSnapshot offerSnapshot : dataSnapshot.getChildren()) {
                    Offer offer = offerSnapshot.getValue(Offer.class);
//                    if(searchTermForQuery.length() == 0 || searchTermForQuery.length() != 0 && offer.getTitle().contains(searchTermForQuery)) {
                    if(checkSearchTerm(offer) && checkPrice(offer) && checkDistance(offer)) {
                        offers.add(offer);
                    }
                }

                if(currentSorting == SORT.PRICE_HIGH_LOW || currentSorting == SORT.NEWEST_FIRST) {
                    Collections.reverse(offers);
                }

                gridview.setAdapter(new OffersAdapter(getApplicationContext(), offers));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private boolean checkSearchTerm(Offer offer){
        return searchTermForQuery.length() == 0 || offer.getTitle().contains(searchTermForQuery);
    }

    private boolean checkPrice(Offer offer) {
        return offer.getPrice() >= priceFromForQuery && offer.getPrice() <= priceToForQuery;
    }

    private boolean checkDistance(Offer offer) {
        double userLat = Double.parseDouble(loginDao.getCurrentUser().getLocation().split(",")[0]);
        double userLng = Double.parseDouble(loginDao.getCurrentUser().getLocation().split(",")[1]);
        double offerLat = Double.parseDouble(offer.getLocation().split(",")[0]);
        double offerLng = Double.parseDouble(offer.getLocation().split(",")[1]);
        boolean isGoodDistance;
        double dis = distance(userLat, userLng, offerLat, offerLng);
        switch (currentDistance) {
            case FIVE:  isGoodDistance = dis <= 5;
                break;
            case TEN:  isGoodDistance = dis <= 10;
                break;
            case TWENTY:  isGoodDistance = dis <= 20;
                break;
            case FIFTY:  isGoodDistance = dis <= 50;
                break;
            case ANY:  isGoodDistance = true;
                break;
            default: isGoodDistance = true;
                break;
        }
        return isGoodDistance;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return dist;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public void searchSort(View view) {
        final SearchView searchView = (SearchView) findViewById(R.id.search_view);

        searchTermForQuery = searchView.getQuery().toString();
        searchSort();
    }

    public void openSortDialog(View view) {
        AlertDialog.Builder sortDialog = new AlertDialog.Builder(this);
        //alt_bld.setIcon(R.drawable.icon);
        sortDialog.setTitle("Select a sorting");
        sortDialog.setSingleChoiceItems(sort, currentSorting.ordinal(), new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Toast.makeText(getApplicationContext(),
                        "SORT = " + sort[item], Toast.LENGTH_SHORT).show();
                TextView sortText = (TextView) findViewById(R.id.sort);
                sortText.setText(sort[item]);
                currentSorting = SORT.values()[item];
                searchSort();
                dialog.dismiss();

            }
        });
        AlertDialog alert = sortDialog.create();
        alert.show();
    }

    public void openDistanceDialog(View view) {
        AlertDialog.Builder distanceDialog = new AlertDialog.Builder(this);
        //alt_bld.setIcon(R.drawable.icon);
        distanceDialog.setTitle("Select a distance");
        distanceDialog.setSingleChoiceItems(distance, currentDistance.ordinal(), new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Toast.makeText(getApplicationContext(),
                        "DISTANCE = " + distance[item], Toast.LENGTH_SHORT).show();
                TextView distanceText = (TextView) findViewById(R.id.distance);
                currentDistance = DISTANCE.values()[item];
                distanceText.setText("Distance: " + distance[item]);
                searchSort();
                dialog.dismiss();

            }
        });
        AlertDialog alert = distanceDialog.create();
        alert.show();
    }

    public void openPriceDialog(View view) {
        LayoutInflater li = LayoutInflater.from(this);
        View priceView = li.inflate(R.layout.price_layout, null);
        final EditText priceFromText = (EditText) priceView
                .findViewById(R.id.priceFrom);
        final EditText priceToText = (EditText) priceView
                .findViewById(R.id.priceTo);
        priceFromText.setText(priceFromEdit);
        priceToText.setText(priceToEdit);
        AlertDialog.Builder priceDialogBuilder = new AlertDialog.Builder(this);
        priceDialogBuilder.setTitle("Select a pricing range");
        priceDialogBuilder.setView(priceView);
        priceDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String priceFrom = priceFromText.getText().toString().trim();
                                String priceTo = priceToText.getText().toString().trim();
                                boolean minPriceSet = true;
                                boolean maxPriceSet = true;
                                if(priceFrom.length() == 0){
                                    priceFrom = "0";
                                    minPriceSet = false;
                                }
                                if(priceTo.length() == 0){
                                    priceTo = String.valueOf(Double.MAX_VALUE);
                                    maxPriceSet = false;
                                }
                                try {
                                    priceFromForQuery = Double.parseDouble(priceFrom);
                                    priceToForQuery = Double.parseDouble(priceTo);
                                } catch (NumberFormatException ex) {
                                    Toast.makeText(getApplicationContext(),
                                            "Please enter numbers.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                final EditText price = findViewById(R.id.price);
                                String priceText = "Price: Any";
                                if(minPriceSet && maxPriceSet){
                                    priceText = "$" +  priceFrom + " - " + "$" + priceTo;
//                                    offersByPrice = offersByPrice.orderByChild("price").startAt(fromPrice).endAt(toPrice);
                                    priceFromEdit = priceFrom;
                                    priceToEdit = priceTo;
                                } else if (minPriceSet) {
                                    priceText = "From $" + priceFrom;
                                    priceFromEdit = priceFrom;
                                    priceToEdit = "";
//                                    offersByPrice = offersByPrice.orderByChild("price").startAt(fromPrice);
                                } else if(maxPriceSet) {
                                    priceText = "To $" + priceTo;
                                    priceFromEdit = "";
                                    priceToEdit = priceTo;
//                                    offersByPrice = offersByPrice.orderByChild("price").endAt(toPrice);
                                } else{
                                    priceFromEdit = "";
                                    priceToEdit = "";
                                }
                                price.setText(priceText);

                                searchSort();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                priceFromEdit = "";
                                priceToEdit = "";
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
        Intent intent = new Intent(view.getContext(), LocationActivity_.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int arg1, int arg2, Intent data )
    {
        if(arg1 == 1)
        {
            searchSort();
        }
    }

    public void openPostOfferActivity(View view) {
        Intent intent = new Intent(view.getContext(), PostOfferActivity_.class);
        startActivity(intent);
    }

    public void setSearchTermForQuery(String searchTermForQuery) {
        this.searchTermForQuery = searchTermForQuery;
    }
}
