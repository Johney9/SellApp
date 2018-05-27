package com.app.sell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.app.sell.adapter.UserOffersAdapter;
import com.app.sell.dao.SpecificUserOffersDao;
import com.app.sell.events.LoggedInEvent;
import com.app.sell.events.OffersRetrievedEvent;
import com.app.sell.model.Offer;
import com.app.sell.model.User;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

@EActivity(R.layout.activity_profile)
public class ProfileActivity extends AppCompatActivity {

    @ViewById(R.id.profile_username)
    TextView mProfileUsernameTextView;
    @ViewById(R.id.profile_offers_gridview)
    GridView mOffersGridView;
    @ViewById(R.id.profile_image_imageview)
    RoundedImageView mProfileImage;
    @Bean
    SpecificUserOffersDao specificUserOffersDao;

    String uid;
    EventBus eventBus;

    @Override
    protected void onStart() {
        super.onStart();
        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        eventBus.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        uid = getIntent().getStringExtra("uid");
        specificUserOffersDao.getData(uid);
    }

    @AfterViews
    void init() {
        mOffersGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Offer offer = (Offer) mOffersGridView.getAdapter().getItem(position);
                Intent intent = new Intent(v.getContext(), OfferActivity_.class);
                intent.putExtra("offerId", offer.getId());
                startActivity(intent);
            }
        });
    }

    @Subscribe
    public void bindUsers(LoggedInEvent userRetrievedEvent) {
        User user = userRetrievedEvent.user;
        mProfileUsernameTextView.setText(user.getUsername());
        Picasso.get().load(user.getImage()).into(mProfileImage);
    }

    @Subscribe
    public void bindOffers(OffersRetrievedEvent offersRetrievedEvent) {
        mOffersGridView.setAdapter(new UserOffersAdapter(this, offersRetrievedEvent.offers));
    }
}
