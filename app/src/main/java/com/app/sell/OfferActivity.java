package com.app.sell;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.sell.dao.LoginDao;
import com.app.sell.dao.UserDao;
import com.app.sell.model.Offer;
import com.app.sell.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.klinker.android.badged_imageview.BadgedImageView;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

@EActivity
public class OfferActivity extends AppCompatActivity {

    DatabaseReference databaseOffer;
    DatabaseReference databaseUser;

    @ViewById(R.id.offer_title) TextView offerTitle;
    @ViewById(R.id.offer_description) TextView offerDescription;
    @ViewById(R.id.offer_location) TextView offerLocation;
    @ViewById(R.id.offer_image) BadgedImageView offerImage;
    @ViewById(R.id.offer_user_profile_image) CircleImageView offerUserImage;
    @ViewById(R.id.offer_user_name) TextView offerUserName;
    @ViewById(R.id.ask) Button ask;
    @ViewById(R.id.make_offer) Button makeOffer;
    @ViewById(R.id.offer_condition) TextView offerCondition;
    @ViewById(R.id.offer_fixed_price) TextView offerFixedPrice;

    private Offer offer;
    private User user;

    @Bean
    LoginDao loginDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent myIntent = getIntent();
        String id = myIntent.getStringExtra(getString(R.string.field_offer_id));
        databaseOffer = FirebaseDatabase.getInstance().getReference(getString(R.string.db_node_offers)).child(id);
        databaseOffer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                offer = dataSnapshot.getValue(Offer.class);

                //offer binding
                offerTitle.setText(offer.getTitle());
                offerDescription.setText(offer.getDescription());
                String city = offer.getLocation().split(",")[2];
                String country = offer.getLocation().split(",")[3];
                offerLocation.setText(city + ", " +  country);
                Picasso.get().load(offer.getImage()).fit().into(offerImage);
                String offerPrice = "$" + String.valueOf(offer.getPrice());
                offerImage.setBadge(offerPrice);
                offerCondition.setText("Condition: " + offer.getCondition());
                offerFixedPrice.setText((offer.getFirmOnPrice()? "Fixed price!": "Dynamic price!"));

                if(loginDao.getCurrentUser().getUid().equals(offer.getOffererId())) {
                    ask.setVisibility(View.INVISIBLE);
                    makeOffer.setVisibility(View.INVISIBLE);
                }

                //user binding
                databaseUser = FirebaseDatabase.getInstance().getReference(getString(R.string.db_node_users)).child(offer.getOffererId());
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
    }

    public void openAskActivity(View view) {
        Intent intent = new Intent(view.getContext(), AskActivity_.class);
        intent.putExtra(view.getContext().getString(R.string.field_offerer_id), user.getUid());
        intent.putExtra(view.getContext().getString(R.string.field_offer_id), offer.getId());
        startActivity(intent);
    }

    public void openMakeOfferActivity(View view) {
        Intent intent = new Intent(view.getContext(), MakeOfferActivity_.class);
        intent.putExtra(view.getContext().getString(R.string.field_offerer_id), user.getUid());
        intent.putExtra(view.getContext().getString(R.string.field_offer_id), offer.getId());
        intent.putExtra(view.getContext().getString(R.string.field_offer_price), offer.getPrice());
        intent.putExtra(view.getContext().getString(R.string.field_offer_fix_price), offer.getFirmOnPrice());
        startActivity(intent);
    }

    @Click(R.id.offer_user_area)
    public void openProfileActivity(View view) {
        Intent intent = new Intent(view.getContext(), ProfileActivity_.class);
        intent.putExtra("uid", offer.getOffererId());
        startActivity(intent);
    }
}
