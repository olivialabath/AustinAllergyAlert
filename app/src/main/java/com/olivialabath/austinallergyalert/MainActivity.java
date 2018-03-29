package com.olivialabath.austinallergyalert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

    private final CharSequence TAB_NAMES[] = {"Daily Allergens","Calendar"};
    private final int NUM_TABS = 2;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Token = " + token);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, TAB_NAMES fot the Tabs and Number Of Tabs.
        mAdapter =  new ViewPagerAdapter(getSupportFragmentManager(), TAB_NAMES, NUM_TABS);

        // Assigning ViewPager View and setting the mAdapter
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        // Assigning the Sliding Tab Layout View
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true);

        // Setting the ViewPager For the SlidingTabsLayout
        mTabs.setViewPager(mPager);



        // subscribe/unsubscribe to topics if alerts are enabled/disabled
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isEnabled = prefs.getBoolean("alerts_enabled", true);
        Log.d(TAG, "alerts_enabled = " + isEnabled);
        if(isEnabled) {
            Log.d(TAG, "subscribed to austinAllergens");
            FirebaseMessaging.getInstance().subscribeToTopic("austinAllergens");
        } else {
            Log.d(TAG, "unsubscribed from austinAllergens");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("austinAllergens");
        }
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

//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if(key.equals("alerts_enabled")){
//
//        }
//    }
//
//
//    private boolean isAlertServiceRunning(Class<?> serviceClass){
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
//            if(serviceClass.getName().equals(service.service.getClassName())){
//                Log.i("isAlertServiceRunning", "" + true);
//                return true;
//            }
//        }
//        Log.i("isAlertServiceRunning", "" + false);
//        return false;
//    }
}
