package com.joachimneumann.bisq

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter


class TransferPageAdapter(fm: FragmentManager, internal var mNumOfTabs: Int, internal var message: String) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        val bundle = Bundle()
        bundle.putString("bisqPhoneID", message)
        when (position) {
            0 -> {
                val tab1 = TransferCodeQR()
                tab1.arguments = bundle
                return tab1
            }
            1 -> {
                val tab2 = TransferCodeEmail()
                tab2.arguments = bundle
                return tab2
            }
            else -> return null
        }
    }

    override fun getCount(): Int {
        return mNumOfTabs
    }
}