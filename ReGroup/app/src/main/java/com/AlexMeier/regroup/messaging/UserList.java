package com.AlexMeier.regroup.messaging;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.AlexMeier.regroup.R;
import com.AlexMeier.regroup.profile.MyFragmentPagerAdapter;
import com.AlexMeier.regroup.profile.ProfileCard;

import java.util.ArrayList;

public class UserList extends FragmentActivity {
    ArrayList<String> users;
    ArrayList<Fragment> profileFragments;
    RecyclerView.Adapter mPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_list);
        users = getIntent().getExtras().getStringArrayList("users");

        profileFragments = new ArrayList<>();
        for (String userID: users
             ) {
            ProfileCard bar = ProfileCard.newInstance(userID);
            profileFragments.add(bar);
        }

        mPagerAdapter = new MyFragmentPagerAdapter(this, profileFragments);
        ViewPager2 viewPager2 = findViewById(R.id.profile_pager);
        viewPager2.setAdapter(mPagerAdapter);
    }
}