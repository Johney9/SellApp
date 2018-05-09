package com.app.sell;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.app.sell.view.SquareImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_change_picture)
public class ChangePictureActivity extends AppCompatActivity {

    //TODO: implement proper changing of images. implement proper retrieval of current image.

    @ViewById(R.id.change_picture_imageview)
    SquareImageView profileImage;

    @AfterViews
    void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.change_picture_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Picasso.get().load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(profileImage);
    }

}
