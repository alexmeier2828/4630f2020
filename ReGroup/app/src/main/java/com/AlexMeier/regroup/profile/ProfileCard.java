package com.AlexMeier.regroup.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.AlexMeier.regroup.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileCard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileCard extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER_NAME = "param1";
    private static final String ARG_IMG_REF = "param2";
    private static final String ARG_USER_ID = "param3";

    // TODO: Rename and change types of parameters
    private String mName;
    private String mImageRef;
    private String mUserID;

    public ProfileCard() {
        // Required empty public constructor
    }

    /**
     *      * Use this factory method to create a new instance of
     *      * this fragment using the provided parameters.
     * @param userID
     * @return
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileCard newInstance(String userID) {
        ProfileCard fragment = new ProfileCard();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mName = getArguments().getString(ARG_USER_NAME);
            mImageRef = getArguments().getString(ARG_IMG_REF);
            mUserID = getArguments().getString(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_bar, container, false);
        ProfileUtil.getProfile(mUserID, (profile)->{
            final TextView profileName = view.findViewById(R.id.user_name);
            final TextView profileBody = view.findViewById(R.id.profile_body);
            final ImageView profilePic = view.findViewById(R.id.profile_picture);
            profileName.setText(profile.getUserName());
            profileBody.setText(profile.getProfileBody());
            Glide.with(this)
                    .load(profile.getImageReference())
                    .signature(new ObjectKey(profile))
                    .into(profilePic);
        });
        return view;
    }
}