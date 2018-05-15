package com.app.sell;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.sell.model.Offer;
import com.app.sell.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.klinker.android.badged_imageview.BadgedImageView;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@EActivity
public class OfferActivity extends AppCompatActivity {

    DatabaseReference databaseOffer;
    DatabaseReference databaseUser;
    @ViewById(R.id.offer_title)
    TextView offerTitle;
    @ViewById(R.id.offer_description)
    TextView offerDescription;
    @ViewById(R.id.offer_location)
    TextView offerLocation;
    @ViewById(R.id.offer_image)
    BadgedImageView offerImage;
    @ViewById(R.id.offer_user_image)
    ImageView offerUserImage;
    @ViewById(R.id.offer_user_name)
    TextView offerUserName;
    private Offer offer;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent();
        String id = myIntent.getStringExtra("offerId");
        databaseOffer = FirebaseDatabase.getInstance().getReference("offers").child(id);
        databaseOffer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                offer = dataSnapshot.getValue(Offer.class);

                offerTitle.setText(offer.getTitle());
                offerDescription.setText(offer.getDescription());
                offerLocation.setText(offer.getLocation());
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                URL url = null;
                try {
                    url = new URL(offer.getImage());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                Bitmap bmp = null;
                try {
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                offerImage.setImageBitmap(bmp);
                offerImage.setBadge(offer.getPrice().toString());
                databaseUser = FirebaseDatabase.getInstance().getReference("users").child(offer.getOffererId());
                databaseUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(User.class);
                        offerUserName.setText(user.getFirstName() + " " + user.getLastName());
                        Glide.with(getApplicationContext()).load(user.getImage()).into(offerUserImage);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        setContentView(R.layout.activity_offer);
    }

    public void openAskActivity(View view) {
        Intent intent = new Intent(view.getContext(), AskActivity.class);
        startActivity(intent);
    }

    public void openMakeOfferActivity(View view) {
        Intent intent = new Intent(view.getContext(), MakeOfferActivity.class);
        startActivity(intent);
    }

    @Click(R.id.offer_user_area)
    public void openProfileActivity(View view) {
        Intent intent = new Intent(view.getContext(), ProfileActivity_.class);
        intent.putExtra("uid", offer.getOffererId());
        startActivity(intent);
    }
}
