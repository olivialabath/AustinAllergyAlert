package com.olivialabath.austinallergyalert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private ViewPager mPager;
    private ViewPagerAdapter mAdapter;
    private SlidingTabLayout mTabs;
    private SharedPreferences mPrefs;

    private final CharSequence TAB_NAMES[] = {"Daily Allergens","Calendar"};
    private final int NUM_TABS = 2;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String token = FirebaseInstanceId.getInstance().getToken();
        //Log.d(TAG, "Token = " + token);

        // if this is the user's first time opening the app,
        // automatically subscribe them to austinAllergens
        mPrefs = getPreferences(Context.MODE_PRIVATE);
        if(mPrefs.getBoolean("is_first_time", true))
            subscribeToTopic();

        // Creating The ViewPagerAdapter and Passing Fragment Manager, TAB_NAMES fot the Tabs and Number Of Tabs.
        mAdapter =  new ViewPagerAdapter(getSupportFragmentManager(), TAB_NAMES, NUM_TABS);

        // Assigning ViewPager View and setting the mAdapter
        mPager = findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        // Assigning the Sliding Tab Layout View
        mTabs = findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true);

        // Setting the ViewPager For the SlidingTabsLayout
        mTabs.setViewPager(mPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent;
        switch (item.getItemId()){
            case R.id.about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void subscribeToTopic(){
//        Log.d(TAG, "subscribing to austinAllergens");
        FirebaseMessaging.getInstance().subscribeToTopic("austinAllergens");
        mPrefs.edit().putBoolean("is_first_time", false).commit();
    }
}
