package com.app.sell;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.sell.dao.LoginDao;
import com.app.sell.model.User;
import com.app.sell.view.SquareImageView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import lombok.NonNull;

@EActivity(R.layout.activity_change_picture)
public class ChangePictureActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int GALLERY_REQUEST = 2;
    static final String TAG = ChangePictureActivity.class.getSimpleName();
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    String mCurrentPhotoPath;
    Uri mPhotoURI;

    @ViewById(R.id.change_picture_imageview)
    SquareImageView mProfileImageView;

    @Bean
    LoginDao loginDao;

    @AfterViews
    void init() {
        Toolbar toolbar = findViewById(R.id.change_picture_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
            if(grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "read permission granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "read permission denied, image will be shown only when uploaded", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bind();
    }

    void bind() {
        User currentUser = loginDao.getCurrentUser();
        Glide.with(this).load(currentUser.getImage()).into(mProfileImageView);
    }

    @Click(R.id.change_picture_from_gallery_button)
    void dispatchPickPictureIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Click(R.id.change_picture_take_new_button)
    void dispatchTakePictureIntent(View view) {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

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
            uploadProfileImageToFirebase(mPhotoURI, mProfileImageView);
        }
    }

    @OnActivityResult(GALLERY_REQUEST)
    void handleGetPictureFromGalleryResponse(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            uploadProfileImageToFirebase(selectedImage, mProfileImageView);
        }
    }

    private void uploadProfileImageToFirebase(Uri selectedImage, final ImageView profileImageView) {
        //load image temporary
        loadImageTemporary(selectedImage, profileImageView);

        //get reference
        final String newUserImageName = "/" + loginDao.getCurrentUser().getUid() + ".jpg";
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child(getString(R.string.db_node_users) + newUserImageName);

        //begin upload
        try {
            UploadTask uploadTask = reference.putFile(selectedImage);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        changeProfilePhoto(downloadUri, profileImageView);
                        Toast.makeText(getApplicationContext(), R.string.image_uploaded_to_firebase, Toast.LENGTH_LONG).show();
                    } else {
                        // Handle failures
                        Toast.makeText(getApplicationContext(), R.string.image_uploaded_to_firebase_fail, Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "uploadProfileImageToFirebase: ", e);
            Toast.makeText(getApplicationContext(), R.string.image_uploaded_to_firebase_fail, Toast.LENGTH_LONG).show();
        }
    }

    @UiThread
    public void loadImageTemporary(Uri selectedImage, ImageView profileImageView) {
        ContentResolver cr = getContentResolver();
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media
                    .getBitmap(cr, selectedImage);

            profileImageView.setImageBitmap(bitmap);
            Toast.makeText(this, "Uploading image to our servers, please wait...",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                    .show();
            Log.e("Camera", e.toString());
        }
    }

    private void changeProfilePhoto(@NonNull Uri uri, ImageView imageView) {
        loginDao.getCurrentUser().setImage(uri.toString());
        loginDao.writeCurrentUser();
    }
}
