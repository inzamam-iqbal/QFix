package com.jobbs.jobsapp.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.jobbs.jobsapp.MainActivity;
import com.jobbs.jobsapp.RootFragment;
import com.jobbs.jobsapp.TabFragment1;
import com.jobbs.jobsapp.TabFragment2;
import com.jobbs.jobsapp.ViewOwnProfileFragment;


public class PagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    boolean signedIn;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, boolean signedIn) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.signedIn = signedIn;
    }

    @Override
    public Fragment getItem(int position) {
        Log.e("from adaptor", signedIn + "");
        switch (position) {
            case 0:
                TabFragment1 tab1 = new TabFragment1();
                return tab1;
            case 1:
                if (signedIn){
                    Log.e("from adaptor", "yes");
                    return  new ViewOwnProfileFragment();
                }else{
                    return new RootFragment();
                }


                //return new RootFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

