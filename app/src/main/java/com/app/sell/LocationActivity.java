package com.app.sell;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.sell.dao.LoginDao;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@EActivity
public class LocationActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_ACCESS_LOCATION = 1;
    TextView mQuestion;

    @ViewById(R.id.location)
    TextView location;

//    @ViewById(R.id.location_edit_text_location)
//    EditText locationEditText;

    @ViewById(R.id.location_save_location_button)
    Button saveLocationButton;

    @Bean
    LoginDao loginDao;

    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private double lat;
    private double lng;
    private String city;
    private String country;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        mQuestion = findViewById(R.id.question);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null && extras.getInt("requestCode") == PostOfferFinishFragment.REQ_SELL_LOCATION) {
                mQuestion.setText(R.string.selling_question);
            }
        }
        if(loginDao.getCurrentUser().getLocation() != null) {
            lat = Double.parseDouble(loginDao.getCurrentUser().getLocation().split(",")[0]);
            lng = Double.parseDouble(loginDao.getCurrentUser().getLocation().split(",")[1]);
            city = loginDao.getCurrentUser().getLocation().split(",")[2];
            country = loginDao.getCurrentUser().getLocation().split(",")[3];
        }
        startLocationUpdates();
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_LOCATION);
            return;
        }
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        lat = location.getLatitude();
        lng = location.getLongitude();

        String cityName = null;
        String countryName = null;
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(lat,
                    lng, 1);
            if (addresses.size() > 0) {
                cityName = addresses.get(0).getLocality();
                countryName = addresses.get(0).getCountryName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        city = cityName;
        country = countryName;
    }

    @Click(R.id.location_get)
    public void getLastLocation() {
        String s = lng + "\n" + lat + "\nCountry: " + country + "\nCity: " + city;
        Toast.makeText(
                getBaseContext(),
                s, Toast.LENGTH_SHORT).show();
        location.setText(city + ", " + country);
    }

    @Click(R.id.location_save_location_button)
    void saveLocation() {
        String location = lat + "," + lng + "," + city + "," + country;
        loginDao.getCurrentUser().setLocation(location);
        loginDao.write(loginDao.getCurrentUser());

        int resultCode = RESULT_OK;
        Intent resultIntent = new Intent();
        resultIntent.putExtra("SELECTED_LOCATION_FULL", location);
        resultIntent.putExtra("SELECTED_LOCATION_DISPLAY", city + ", " + country);
        setResult(resultCode, resultIntent);
        finish();
    }

}
