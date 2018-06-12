package com.app.sell;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.app.sell.adapter.UserOfferAdapter;
import com.app.sell.dao.SpecificUserOffersDao;
import com.app.sell.events.LoggedInEvent;
import com.app.sell.model.User;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

@EActivity(R.layout.activity_profile)
public class ProfileActivity extends AppCompatActivity {

    @ViewById(R.id.profile_username)
    TextView mProfileUsernameTextView;
    @ViewById(R.id.profile_offers_gridview)
    RecyclerView mOffersGridRecyclerView;
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

    @AfterViews
    void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        uid = getIntent().getStringExtra("uid");
        specificUserOffersDao.getData(uid);
        mOffersGridRecyclerView.setAdapter(new UserOfferAdapter(this));
        mOffersGridRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Subscribe
    @UiThread
    public void bindUsers(LoggedInEvent loggedInEvent) {
        User user = loggedInEvent.user;
        mProfileUsernameTextView.setText(user.getUsername());
        Glide.with(this).load(user.getImage()).into(mProfileImage);
    }
}
