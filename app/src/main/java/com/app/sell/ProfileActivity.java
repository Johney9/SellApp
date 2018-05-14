package com.app.sell;

import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.TextView;

import com.app.sell.adapter.UserOffersAdapter;
import com.app.sell.dao.SpecificUserOffersDao;
import com.app.sell.events.OffersRetrievedEvent;
import com.app.sell.events.UserRetrievedEvent;
import com.app.sell.model.User;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

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

        uid = getIntent().getStringExtra("uid");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        specificUserOffersDao.getData(uid);
    }

    @Subscribe
    void bindUsers(UserRetrievedEvent userRetrievedEvent) {
        User user = userRetrievedEvent.user;
        mProfileUsernameTextView.setText(user.getUsername());
        Picasso.get().load(user.getImage()).into(mProfileImage);
    }

    @Subscribe
    void bindOffers(OffersRetrievedEvent offersRetrievedEvent) {
        mOffersGridView.setAdapter(new UserOffersAdapter(this, offersRetrievedEvent.offers));
    }
}
