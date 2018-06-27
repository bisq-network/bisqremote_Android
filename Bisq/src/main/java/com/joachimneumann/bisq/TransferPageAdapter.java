package com.joachimneumann.bisq;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class TransferPageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public TransferPageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TransferCodeQR tab1 = new TransferCodeQR();
                return tab1;
            case 1:
                TransferCodeEmail tab2 = new TransferCodeEmail();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}