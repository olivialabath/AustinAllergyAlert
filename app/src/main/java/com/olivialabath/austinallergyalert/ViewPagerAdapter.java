package com.olivialabath.austinallergyalert;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by olivialabath on 6/18/17.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private CharSequence tab_names[];
    private int num_tabs;

    public ViewPagerAdapter(FragmentManager fm, CharSequence[] tab_names, int num_tabs) {
        super(fm);

        this.tab_names = tab_names;
        this.num_tabs = num_tabs;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new AllergenDailyFragment();
        }
        if (position == 1) {
            return new AllergenCalendarFragment();
        }
        else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return num_tabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tab_names[position];
    }
}
