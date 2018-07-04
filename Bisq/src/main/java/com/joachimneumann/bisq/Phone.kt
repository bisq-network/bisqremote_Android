package com.joachimneumann.bisq

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

import com.google.firebase.iid.FirebaseInstanceId

import java.io.IOException
import java.util.UUID

import android.content.Context.MODE_PRIVATE

class Phone {

    var key: String? = null
    var apsToken: String? = null
    var isInitialized: Boolean? = null
    private var context: Context? = null
    var cryptoHelper: CryptoHelper? = null

    //private constructor.
    private constructor() {
        //Prevent form the reflection api.
        if (sSoleInstance != null) {
            throw RuntimeException("Use getInstance() method to get the single instance of this class.")
        }
    }

    public fun decrypt(cipher: String, iv: String): String? {
        if (cryptoHelper != null) {
            return cryptoHelper!!.decrypt(cipher, iv)
        } else {
            return null
        }
    }

    private constructor(c: Context) {
        context = c
        val prefs = context!!.getSharedPreferences(BISQ_SHARED_PREFERENCE_FILE, MODE_PRIVATE)
        val phoneString = prefs.getString(BISQ_SHARED_PREFERENCE_PHONEID, null)
        if (phoneString != null) {
            fromString(phoneString)
        } else {
            apsToken = FirebaseInstanceId.getInstance().token
            if (apsToken != null) {
                key = UUID.randomUUID().toString().replace("-", "")
                isInitialized = true
                cryptoHelper = CryptoHelper(key!!)
                save()
            } else {
                Log.e("Bisq", "Token is null)")
            }
        }
    }

    fun createNew() {}


    fun fromString(s: String) {
        val a = s.split(PHONE_SEPARATOR_ESCAPED.toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
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
            isInitialized = true
            cryptoHelper = CryptoHelper(key!!)
        } catch (e: IOException) {
            key = ""
            apsToken = ""
            isInitialized = false
            cryptoHelper = null
        }

    }

    fun description(): String {
        return PHONE_MAGIC_ANDROID + PHONE_SEPARATOR + key + PHONE_SEPARATOR + apsToken
    }

    fun save() {
        val editor = context!!.getSharedPreferences(BISQ_SHARED_PREFERENCE_FILE, MODE_PRIVATE).edit()
        editor.putString(BISQ_SHARED_PREFERENCE_PHONEID, description())
        editor.apply()
    }

    companion object {
        private val PHONE_MAGIC_ANDROID = "BisqPhoneAndroid"
        internal val PHONE_SEPARATOR = "|"
        internal val PHONE_SEPARATOR_ESCAPED = "\\|"
        private val BISQ_SHARED_PREFERENCE_FILE = "Bisq.txt"
        private val BISQ_SHARED_PREFERENCE_PHONEID = "BisqPhone"


        @Volatile
        private var sSoleInstance: Phone? = null

        fun getInstance(context: Context): Phone {

            //Double check locking pattern
            if (sSoleInstance == null) { //Check for the first time
                synchronized(Phone::class.java) {
                    //Check for the second time.
                    // if there is no instance available... create new one
                    if (sSoleInstance == null) {
                        // Do not create a new instance, the instance needs to be created with getInstance(Context c)
                        sSoleInstance = Phone(context)
                    }
                }
            }
            return sSoleInstance!!
        }
    }
}
