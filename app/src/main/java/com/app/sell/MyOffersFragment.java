package com.app.sell;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.sell.adapter.BuyingOffersImageAdapter;
import com.app.sell.adapter.OffersAdapter;
import com.app.sell.adapter.SellingOffersImageAdapter;
import com.app.sell.dao.LoginDao;
import com.app.sell.model.Chatroom;
import com.app.sell.model.Offer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.EFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyOffersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyOffersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyOffersFragment extends Fragment {

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

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CURRENT_USER_UID = "param1";

    // TODO: Rename and change types of parameters
    private String currentUserUid;

    private OnFragmentInteractionListener mListener;

    public MyOffersFragment() {
        // Required empty public constructor
    }

    public static MyOffersFragment newInstance(String currentUserUid) {
        MyOffersFragment fragment = new MyOffersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CURRENT_USER_UID, currentUserUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUserUid = getArguments().getString(ARG_CURRENT_USER_UID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_offers, container, false);
        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), currentUserUid);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) v.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private List<Offer> offers;

        GridView gridview;

        Query databaseOffers;
        Query databaseChatrooms;
        List<String> buyingOffersIds;
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_CURRENT_USER_UID = "current_user_uid";

        public PlaceholderFragment() {
            offers = new ArrayList<>();
            buyingOffersIds = new ArrayList<>();
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String currentUserUid) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_CURRENT_USER_UID, currentUserUid);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_my_offers_tab, container, false);

            gridview = (GridView) rootView.findViewById(R.id.gridviewOffers);

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Offer offer = (Offer)gridview.getAdapter().getItem(position);
                    Intent intent = new Intent(v.getContext(), OfferActivity_.class);
                    intent.putExtra(getString(R.string.field_offer_id),offer.getId());
                    startActivity(intent);
                }
            });
            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            final int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

            if(sectionNumber==1){
                loadSellingOffers();
            }else if(sectionNumber==2){
                loadBuyingOffers();
            }
        }

        private void loadSellingOffers(){
            final String currentUserUid = getArguments().getString(ARG_CURRENT_USER_UID);

            databaseOffers = FirebaseDatabase.getInstance().getReference("offers").orderByChild("timestamp");

            databaseOffers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    offers.clear();
                    for (DataSnapshot offerSnapshot: dataSnapshot.getChildren()) {
                        Offer offer = offerSnapshot.getValue(Offer.class);
                            if(offer.getOffererId().equals(currentUserUid)){
                                offers.add(offer);
                            }

                    }

                    Collections.reverse(offers);
                    gridview.setAdapter(new SellingOffersImageAdapter(getContext(), offers));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        private void loadBuyingOffers(){
            final String currentUserUid = getArguments().getString(ARG_CURRENT_USER_UID);

            databaseChatrooms = FirebaseDatabase.getInstance().getReference("chatrooms").orderByChild("timestamp");
            databaseChatrooms.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    buyingOffersIds.clear();
                    for (DataSnapshot chatroomSnapshot: dataSnapshot.getChildren()) {
                        Chatroom chatroom = chatroomSnapshot.getValue(Chatroom.class);
                        if(chatroom.getAskerId().equalsIgnoreCase(currentUserUid)){
                            buyingOffersIds.add(chatroom.getOfferId());
                        }

                    }

                    databaseOffers = FirebaseDatabase.getInstance().getReference("offers").orderByChild("timestamp");

                    databaseOffers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            offers.clear();
                            for (DataSnapshot offerSnapshot: dataSnapshot.getChildren()) {
                                Offer offer = offerSnapshot.getValue(Offer.class);

                                    boolean isBuyingOffer = false;
                                    for(String buyingOfferId: buyingOffersIds){
                                        if(buyingOfferId.equals(offer.getId())){
                                            isBuyingOffer = true;
                                            break;
                                        }
                                    }

                                    if(isBuyingOffer){
                                        offers.add(offer);
                                    }
                            }

                            Collections.reverse(offers);
                            gridview.setAdapter(new BuyingOffersImageAdapter(getContext(), offers));
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
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        String currentUserUid;

        public SectionsPagerAdapter(FragmentManager fm, String currentUserUid) {
            super(fm);
            this.currentUserUid = currentUserUid;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, currentUserUid);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }
}
