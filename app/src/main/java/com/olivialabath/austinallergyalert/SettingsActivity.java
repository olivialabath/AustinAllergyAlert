package com.olivialabath.austinallergyalert;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

public class SettingsActivity extends AppCompatActivity{

    private final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // display the fragment as the main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();


    }



    public static class SettingsFragment extends PreferenceFragment{

        private static final String TAG = "SettingsFragment";
        SwitchPreference mAlertsEnabled;

        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);

            mAlertsEnabled = (SwitchPreference) findPreference("alerts_enabled");
            mAlertsEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    boolean isEnabled = (boolean) o;
                    if (isEnabled) {
                        //Log.d(TAG, "subscribing to austinAllergens");
                        FirebaseMessaging.getInstance().subscribeToTopic("austinAllergens");
                    } else {
                        //Log.d(TAG, "unsubscribing from austinAllergens");
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("austinAllergens");
                    }
                    return true;
                }
            });
        }
    }
}
