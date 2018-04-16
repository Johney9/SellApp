package com.app.sell;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostOfferDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostOfferDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostOfferDetailsFragment extends Fragment {
    public final static int REQ_CODE = 1;
    public final String[] possibleConditions = {"Other (see description)", "For Parts", "Used (normal wear)", "Open Box (never used)", "Reconditioned/Certified", "New (never used)"};
    private TextView mSelectedCategory;
    private SeekBar mSeekBar;
    private TextView mCondition;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PostOfferDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostOfferDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostOfferDetailsFragment newInstance(String param1, String param2) {
        PostOfferDetailsFragment fragment = new PostOfferDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_post_offer_details, container, false);

        mSelectedCategory = v.findViewById(R.id.selected_category);
        mSelectedCategory.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getContext(), SelectCategoryActivity.class);
                startActivityForResult(intent, REQ_CODE);
            }
        });

        mSeekBar = v.findViewById(R.id.seekBar);
        mCondition = v.findViewById(R.id.condition);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mCondition.setText(possibleConditions[i]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return v;
    }

    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                String selectedCategoryName = data.getExtras().getString("SELECTED_CATEGORY");
                if (selectedCategoryName != null) {
                    mSelectedCategory.setText(selectedCategoryName);
                }
            }
        }
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
}
