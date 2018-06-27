package com.joachimneumann.bisq;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class TransferPageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    String message;

    public TransferPageAdapter(FragmentManager fm, int NumOfTabs, String message) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.message = message;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("bisqPhoneID", message);
        switch (position) {
            case 0:
                TransferCodeQR tab1 = new TransferCodeQR();
                tab1.setArguments(bundle);
                return tab1;
            case 1:
                TransferCodeEmail tab2 = new TransferCodeEmail();
                tab2.setArguments(bundle);
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