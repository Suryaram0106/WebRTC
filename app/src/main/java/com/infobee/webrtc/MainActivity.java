package com.infobee.webrtc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private TabItem tabProfile;
    private TabItem tabCall;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        tabProfile = findViewById(R.id.tabProfile);
        tabCall = findViewById(R.id.tabCall);

        addFragments();
        setupTabSelection();
    }

    private void addFragments() {

        String email = getIntent().getStringExtra("Email");


        String s1 = getIntent().getStringExtra("NAME");
        String s2 = getIntent().getStringExtra("EMAIL");
        String s3 = getIntent().getStringExtra("PHOTO");


        ProfileFragment profileFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("NAME", s1);
        bundle.putString("EMAIL", s2);
        bundle.putString("PHOTO", s3);


        CallFragment callFragment = new CallFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("email", email);
        bundle1.putString("NAME", s1);
        bundle1.putString("EMAIL", s2);
        bundle1.putString("PHOTO", s3);


        profileFragment.setArguments(bundle);
        callFragment.setArguments(bundle1);


        viewPager = findViewById(R.id.viewPager);
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(profileFragment, "Profile");
        adapter.addFragment(callFragment, "Call");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        setTabSelection(0);
    }

    private void setupTabSelection() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                setTabSelection(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed for your case, but you can add any logic for when a tab is unselected
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed for your case, but you can add any logic for when a tab is reselected
            }
        });
    }

    private void setTabSelection(int position) {
        viewPager.setCurrentItem(position);
    }

//    private void setTabSelection(int position) {
//        TabLayout.Tab tab = tabLayout.getTabAt(position);
//        if (tab != null) {
//            tab.select();
//        }
//    }
}