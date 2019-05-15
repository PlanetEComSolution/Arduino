package com.pombingsoft.planet_iot.activity.switch_controller;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.pombingsoft.planet_iot.R;
import com.pombingsoft.planet_iot.fragment.RunningScheduler_Fragment;
import com.pombingsoft.planet_iot.fragment.SavedScheduler_Fragment;

import java.util.ArrayList;
import java.util.List;


public class SchedulerActivity extends AppCompatActivity {

    private ViewPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    String UserId, DeviceId, DeviceName, Device_Type, DeviceLockStatus, PinNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acheduler);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/


        try {
            Intent i = getIntent();
            UserId = i.getStringExtra("UserId");
            DeviceId = i.getStringExtra("DeviceId");
            DeviceLockStatus = i.getStringExtra("LockStatus");
            Device_Type = i.getStringExtra("DeviceType");
            DeviceName = i.getStringExtra("DeviceName");
            PinNum = i.getStringExtra("PinNum");

        } catch (Exception e) {
            e.getMessage();
        }




        mSectionsPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    public  Fragment getInstance_SavedSchedulerFragment() {
        SavedScheduler_Fragment fragment = new SavedScheduler_Fragment();
        Bundle args = new Bundle();

        args.putString("UserId", UserId);
        args.putString("DeviceId", DeviceId);
        args.putString("DeviceLockStatus", DeviceLockStatus);
        args.putString("Device_Type", Device_Type);
        args.putString("DeviceName", DeviceName);
        args.putString("PinNum", PinNum);

        fragment.setArguments(args);
        return fragment;
    }
    public  Fragment getInstance_RunningSchedulerFragment() {
        RunningScheduler_Fragment fragment = new RunningScheduler_Fragment();
        Bundle args = new Bundle();

        args.putString("UserId", UserId);
        args.putString("DeviceId", DeviceId);
        args.putString("DeviceLockStatus", DeviceLockStatus);
        args.putString("Device_Type", Device_Type);
        args.putString("DeviceName", DeviceName);
        args.putString("PinNum", PinNum);

        fragment.setArguments(args);
        return fragment;
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(getInstance_SavedSchedulerFragment(), "Saved Scheduler");
        adapter.addFragment(getInstance_RunningSchedulerFragment(), "Running Scheduler");

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_acheduler, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
