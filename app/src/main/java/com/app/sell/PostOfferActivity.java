package com.app.sell;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anton46.stepsview.StepsView;
import com.app.sell.dao.LoginDao;
import com.app.sell.model.Offer;
import com.app.sell.view.NonSwipeableViewPager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

import java.util.UUID;

@EActivity
public class PostOfferActivity extends AppCompatActivity {

    String[] steps = {"Photo", "Details", "Price", "Finish"};

    //Firebase
    private DatabaseReference mDatabase;
    FirebaseStorage storage;
    StorageReference storageReference;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Bean
    LoginDao loginDao;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private NonSwipeableViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_offer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.account_toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (NonSwipeableViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        StepsView mStepsView = (StepsView) findViewById(R.id.stepsView);
        mStepsView.setLabels(steps)
                .setBarColorIndicator(ContextCompat.getColor(this, R.color.material_blue_grey_800))
                .setProgressColorIndicator(ContextCompat.getColor(this, R.color.facebook_blue))
                .setLabelColorIndicator(ContextCompat.getColor(this, R.color.facebook_blue))
                .setCompletedPosition(0)
                .drawView();



        final Button mNextButton = (Button)findViewById(R.id.nextButton);
        mNextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();

                IPostOfferFragment postOfferFragment = (IPostOfferFragment)fm.findFragmentByTag(getFragmentTag(mViewPager.getId(), mViewPager.getCurrentItem()));
                if (postOfferFragment.validationSuccess()) {
                    if (mViewPager.getCurrentItem() == 3) {
                        uploadImageAndPostOffer();
                    } else {
                        mViewPager.setCurrentItem(getItem(+1), true);
                    }
                }

            }
        });
        Button mPreviousButton = (Button)findViewById(R.id.previousButton);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(getItem(-1), true);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            StepsView mStepsView = (StepsView) findViewById(R.id.stepsView);
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                mStepsView.setCompletedPosition(position);
                mStepsView.drawView();
                if(mViewPager.getCurrentItem() == 3){
                    mNextButton.setText("Post Item");
                }else{
                    mNextButton.setText("Next");
                }
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    private int getItem(int i) {
        return mViewPager.getCurrentItem() + i;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_offer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new PostOfferPhotoFragment();
                case 1:
                    return new PostOfferDetailsFragment();
                case 2:
                    return new PostOfferPriceFragment();
                case 3:
                    return new PostOfferFinishFragment();
            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

    }

    public void takePicture(View view) {
        PostOfferPhotoFragment photoFragment = (PostOfferPhotoFragment)getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + 0); // 0 is PostOfferPhotoFragment
        photoFragment.takePicture(view);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                PostOfferPhotoFragment photoFragment = (PostOfferPhotoFragment)getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + 0); // 0 is PostOfferPhotoFragment
                photoFragment.enableTakePictureButton();
            }
        }
    }

    public void uploadImageAndPostOffer(){
        FragmentManager fm = getSupportFragmentManager();
        PostOfferPhotoFragment photoFragment = (PostOfferPhotoFragment)fm.findFragmentByTag(getFragmentTag(mViewPager.getId(), 0));
        UUID offerId = UUID.randomUUID();
        uploadImage(photoFragment.getOfferPhotoUri(), offerId);
    }

    public void postOffer(UUID offerId, Uri imageUrl){
        FragmentManager fm = getSupportFragmentManager();

        PostOfferPhotoFragment photoFragment = (PostOfferPhotoFragment)fm.findFragmentByTag(getFragmentTag(mViewPager.getId(), 0));
        PostOfferDetailsFragment detailsFragment = (PostOfferDetailsFragment)fm.findFragmentByTag(getFragmentTag(mViewPager.getId(), 1));
        PostOfferPriceFragment priceFragment = (PostOfferPriceFragment)fm.findFragmentByTag(getFragmentTag(mViewPager.getId(), 2));
        PostOfferFinishFragment finishFragment = (PostOfferFinishFragment)fm.findFragmentByTag(getFragmentTag(mViewPager.getId(), 3));

        Offer newOffer = new Offer();
        newOffer.setId(offerId.toString());
        newOffer.setImage(imageUrl.toString());
        newOffer.setTitle(photoFragment.getOfferTitle());
        newOffer.setCategoryId(detailsFragment.getOfferCategoryId());
        newOffer.setCondition(detailsFragment.getOfferCondition());
        newOffer.setDescription(detailsFragment.getOfferDescription());
        newOffer.setPrice(priceFragment.getOfferPrice());
        newOffer.setFirmOnPrice(priceFragment.getOfferFirmOnPrice());
        newOffer.setLocation(finishFragment.getOfferLocation());
        newOffer.setOffererId(loginDao.getCurrentUser().getUid());
        newOffer.setTimestamp(System.currentTimeMillis());

        mDatabase.child("offers").child(offerId.toString()).setValue(newOffer);
    }

    private void uploadImage(Uri filePath, final UUID offerId) {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("offers/"+ offerId.toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri imageUrl = taskSnapshot.getDownloadUrl();
                            postOffer(offerId, imageUrl);
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();

                            int resultCode = RESULT_OK;
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("IS_NEW_OFFER_CREATED", true);
                            setResult(resultCode, resultIntent);
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    private String getFragmentTag(int viewPagerId, int fragmentPosition)
    {
        return "android:switcher:" + viewPagerId + ":" + fragmentPosition;
    }


}
