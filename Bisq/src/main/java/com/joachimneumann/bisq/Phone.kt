package com.joachimneumann.bisq

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

import com.google.firebase.iid.FirebaseInstanceId

import java.io.IOException
import java.util.UUID

import android.content.Context.MODE_PRIVATE
import com.google.firebase.internal.FirebaseAppHelper.getToken
import com.google.firebase.iid.InstanceIdResult
import com.google.android.gms.tasks.OnSuccessListener


public class Phone private constructor() {

    companion object {
        private const val PHONE_MAGIC_ANDROID = "BisqPhoneAndroid"
        private const val PHONE_SEPARATOR = "|"
        private const val PHONE_SEPARATOR_ESCAPED = "\\|"

        const val BISQ_SHARED_PREFERENCE_FILE = "Bisq.txt"
        const val BISQ_SHARED_PREFERENCE_PHONEID = "BisqPhone"
        val instance: Phone by lazy {
            Holder.INSTANCE
        }
    }

    private object Holder {
        @SuppressLint("StaticFieldLeak")
        var INSTANCE = Phone()
    }

    var key: String? = null
    var apsToken: String? = null
    var confirmed: Boolean = false
    var context: Context? = null
    var cryptoHelper: CryptoHelper? = null

    fun phoneID(): String {
        return Phone.PHONE_MAGIC_ANDROID + Phone.PHONE_SEPARATOR + key + Phone.PHONE_SEPARATOR + apsToken
    }

    fun readFromPreferences(): Boolean {
        if (key == null) return false
        if (apsToken == null) return false
        if (context == null) return false
        if (!confirmed) return false
        val prefs = context!!.getSharedPreferences(Phone.BISQ_SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE)
        val phoneString = prefs.getString(Phone.BISQ_SHARED_PREFERENCE_PHONEID, null)
        var ok = false
        if (phoneString != null) {
            ok = fromString(phoneString)
        }
        return ok
    }

    fun saveToPreferences() {
        val editor = context!!.getSharedPreferences(Phone.BISQ_SHARED_PREFERENCE_FILE, MODE_PRIVATE).edit()
        editor.putString(Phone.BISQ_SHARED_PREFERENCE_PHONEID, phoneID())
        editor.apply()
    }

    fun decrypt(cipher: String, iv: String): String? {
        if (cryptoHelper != null) {
            return cryptoHelper!!.decrypt(cipher, iv)
        } else {
            return null
        }
    }

    fun setToken(context_: Context, token_: String) {
        apsToken = token_
        context = context_
        key = UUID.randomUUID().toString().replace("-", "")
        cryptoHelper = CryptoHelper(key!!)
        confirmed = false
    }

    fun fromString(s: String): Boolean {
        val a = s.split(Phone.PHONE_SEPARATOR_ESCAPED.toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        try {
            if (a.size != 3) {
                throw IOException("invalid $BISQ_SHARED_PREFERENCE_PHONEID format")
            }
            if (a[1].length != 32) {
                throw IOException("invalid $BISQ_SHARED_PREFERENCE_PHONEID format")
            }
            if (a[2].length < 32) {
                throw IOException("invalid $BISQ_SHARED_PREFERENCE_PHONEID format")
            }
            if (a[0] != PHONE_MAGIC_ANDROID) {
                throw IOException("invalid $BISQ_SHARED_PREFERENCE_PHONEID format")
            }
            key = a[1]
            apsToken = a[2]
            confirmed = false
            cryptoHelper = CryptoHelper(key!!)
            return true
        } catch (e: IOException) {
            key = null
            apsToken = null
            confirmed = false
            cryptoHelper = null
        }
        return false
    }


}
