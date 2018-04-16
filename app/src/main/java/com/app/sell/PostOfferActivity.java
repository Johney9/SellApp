package com.app.sell;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
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
import android.widget.ImageView;

import com.anton46.stepsview.StepsView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostOfferActivity extends AppCompatActivity {

    String[] steps = {"Photo", "Details", "Price", "Finish"};

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_offer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
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
                mViewPager.setCurrentItem(getItem(+1), true);

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


}
