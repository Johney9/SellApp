package com.app.sell;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.app.sell.dao.UserDao;
import com.app.sell.model.User;
import com.app.sell.view.SquareImageView;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@EActivity(R.layout.activity_change_picture)
public class ChangePictureActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int GALLERY_REQUEST = 2;
    static final String TAG = ChangePictureActivity.class.getSimpleName();
    String mCurrentPhotoPath;
    Uri mPhotoURI;

    @ViewById(R.id.change_picture_imageview)
    SquareImageView mProfileImage;

    @Bean
    UserDao userDao;

    @AfterViews
    void init() {
        Toolbar toolbar = findViewById(R.id.change_picture_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bind();
    }

    void bind() {
        User currentUser = userDao.getCurrentUser();
        Picasso.get().load(currentUser.getImage()).into(mProfileImage);
    }

    @Click(R.id.change_picture_from_gallery_button)
    void dispatchPickPictureIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Click(R.id.change_picture_take_new_button)
    void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(TAG, ex.getLocalizedMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mPhotoURI = FileProvider.getUriForFile(view.getContext(),
                        view.getContext().getApplicationContext().getPackageName() + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @OnActivityResult(REQUEST_TAKE_PHOTO)
    void handleTakePictureResponse(int resultCode, Intent data) {
        Log.d("handleTakePictureResponse:", String.valueOf(resultCode));

        if (resultCode == RESULT_OK) {
            changeProfilePhoto(mPhotoURI, mProfileImage);
        }
    }

    @OnActivityResult(GALLERY_REQUEST)
    void handleGetPictureFromGalleryResponse(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            changeProfilePhoto(selectedImage, mProfileImage);
        }
    }

    private void changeProfilePhoto(Uri uri, ImageView imageView) {
        User currentUser = userDao.getCurrentUser();
        currentUser.setImage(uri.toString());
        userDao.writeCurrentUser();
        Picasso.get().load(currentUser.getImage()).into(imageView);
    }
}
