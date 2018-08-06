package com.joachimneumann.bisq

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context

import java.io.IOException
import java.util.UUID

import android.content.Context.MODE_PRIVATE
import android.text.TextUtils
import android.os.Build




public class Phone private constructor() {

    companion object {
        private const val PHONE_MAGIC_ANDROID = "android"
        private const val PHONE_SEPARATOR = "|"
        private const val PHONE_SEPARATOR_ESCAPED = "\\|"

        const val BISQ_SHARED_PREFERENCE_FILE = "pairingToken.txt"
        const val BISQ_SHARED_PREFERENCE_PAIRING_TOKEN = "pairingToken"
        val instance: Phone by lazy {
            Holder.INSTANCE
        }
    }

    var descriptor: String? = getDeviceName()
    var key: String? = null
    var token: String? = null
    var confirmed: Boolean = false

    private object Holder {
        @SuppressLint("StaticFieldLeak")
        var INSTANCE = Phone()
    }

    fun exampleToken(): Boolean {
        fromString(Phone.PHONE_MAGIC_ANDROID + Phone.PHONE_SEPARATOR + "android emulator" + Phone.PHONE_SEPARATOR + "12345678901234567890123456789012" + Phone.PHONE_SEPARATOR + "exampleToken12345678901234567890123456789012")
        return true
    }

    fun pairingToken(): String? {
        if (key != null) {
            return Phone.PHONE_MAGIC_ANDROID + Phone.PHONE_SEPARATOR + descriptor + Phone.PHONE_SEPARATOR + key + Phone.PHONE_SEPARATOR + token
        } else return null
    }

    fun readFromPreferences(context: Context): Boolean {
        val prefs = context.getSharedPreferences(Phone.BISQ_SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE)
        val phoneString = prefs.getString(Phone.BISQ_SHARED_PREFERENCE_PAIRING_TOKEN, null)
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
        editor.putString(Phone.BISQ_SHARED_PREFERENCE_PAIRING_TOKEN, pairingToken())
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
            if (a.size != 4) {
                throw IOException("invalid ${BISQ_SHARED_PREFERENCE_PAIRING_TOKEN} format")
            }
            if (a[2].length != 32) {
                throw IOException("invalid $BISQ_SHARED_PREFERENCE_PAIRING_TOKEN} format")
            }
            if (a[3].length < 32) {
                throw IOException("invalid $BISQ_SHARED_PREFERENCE_PAIRING_TOKEN} format")
            }
            if (a[0] != PHONE_MAGIC_ANDROID) {
                throw IOException("invalid $BISQ_SHARED_PREFERENCE_PAIRING_TOKEN} format")
            }
            key = a[2]
            token = a[3]
            confirmed = false
            return true
        } catch (e: IOException) {
            reset()
        }
        return false
    }

    fun getDeviceName(): String? {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else capitalize(manufacturer) + " " + model
    }

    private fun capitalize(str: String): String? {
        if (TextUtils.isEmpty(str)) {
            return str
        }
        val arr = str.toCharArray()
        var capitalizeNext = true

        val phrase = StringBuilder()
        for (c in arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c))
                capitalizeNext = false
                continue
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true
            }
            phrase.append(c)
        }

        return phrase.toString()
    }

}
