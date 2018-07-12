package com.joachimneumann.bisq

import android.annotation.SuppressLint
import android.content.Context

import java.io.IOException
import java.util.UUID

import android.content.Context.MODE_PRIVATE


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

    var key: String? = null
    var token: String? = null
    var confirmed: Boolean = false

    private object Holder {
        @SuppressLint("StaticFieldLeak")
        var INSTANCE = Phone()
    }

    fun phoneID(): String? {
        if (key != null) {
            return Phone.PHONE_MAGIC_ANDROID + Phone.PHONE_SEPARATOR + key + Phone.PHONE_SEPARATOR + token
        } else return null
    }

    fun readFromPreferences(context: Context): Boolean {
        val prefs = context.getSharedPreferences(Phone.BISQ_SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE)
        val phoneString = prefs.getString(Phone.BISQ_SHARED_PREFERENCE_PHONEID, null)
        if (phoneString != null) {
            return fromString(phoneString)
        }
        return false
    }

    fun reset() {
        key = null
        token = null
        confirmed = false
    }

    fun saveToPreferences(context: Context) {
        val editor = context.getSharedPreferences(Phone.BISQ_SHARED_PREFERENCE_FILE, MODE_PRIVATE).edit()
        editor.putString(Phone.BISQ_SHARED_PREFERENCE_PHONEID, phoneID())
        editor.apply()
    }

    fun clearPreferences(context: Context) {
        val editor = context.getSharedPreferences(Phone.BISQ_SHARED_PREFERENCE_FILE, MODE_PRIVATE).edit()
        editor.clear()
        editor.commit()
    }

    fun newToken(token_: String) {
        token = token_
        key = UUID.randomUUID().toString().replace("-", "")
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
            token = a[2]
            confirmed = false
            return true
        } catch (e: IOException) {
            reset()
        }
        return false
    }

}
