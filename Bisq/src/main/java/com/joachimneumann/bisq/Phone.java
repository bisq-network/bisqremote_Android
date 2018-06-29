package com.joachimneumann.bisq;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class Phone {
    private static final String PHONE_MAGIC_ANDROID = "BisqPhoneAndroid";
    static final String PHONE_SEPARATOR = "|";
    private static final String BISQ_SHARED_PREFERENCES = "BisqPreferences";
    private static final String BISQ_SHARED_PREFERENCE_PHONE = "BisqPhone";

    public String key;
    public String apsToken;
    public Boolean isInitialized;
    private Context context;


    private static volatile Phone sSoleInstance;

    //private constructor.
    private Phone() {
        //Prevent form the reflection api.
        if (sSoleInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static Phone getInstance() {
        //Double check locking pattern
        if (sSoleInstance == null) { //Check for the first time
            synchronized (Phone.class) {   //Check for the second time.
                //if there is no instance available... create new one
                if (sSoleInstance == null) {
                    // Do not create a new instance, the instance needs to be created with getInstance(Context c)
                    // sSoleInstance = new Phone();
                    Log.e("BISQ", "Phone needs the context. Cannot create the Instance");
                }
            }
        }
        return sSoleInstance;
    }
    public static Phone getInstance(Context c) {
        //Double check locking pattern
        if (sSoleInstance == null) { //Check for the first time
            synchronized (Phone.class) {   //Check for the second time.
                //if there is no instance available... create new one
                if (sSoleInstance == null) {
                    sSoleInstance = new Phone(c);
                }
            }
        }
        return sSoleInstance;
    }


    private Phone(Context c) {
        context = c;
        SharedPreferences prefs = context.getSharedPreferences(BISQ_SHARED_PREFERENCES, MODE_PRIVATE);
        String phoneString = prefs.getString(BISQ_SHARED_PREFERENCE_PHONE, null);
        if (phoneString != null) {
            fromString(phoneString);
        } else {
            apsToken = FirebaseInstanceId.getInstance().getToken();
            key = UUID.randomUUID().toString().replace("-",  "");
            isInitialized = true;
            save();
        }
    }

    public void createNew() {
    }


    public void fromString(String s) {
        String[] a = s.split(PHONE_SEPARATOR);
        try {
            if (a.length != 3) {
                throw new IOException("invalid " + BISQ_SHARED_PREFERENCE_PHONE + " format");
            }
            if (a[1].length() != 32) {
                throw new IOException("invalid " + BISQ_SHARED_PREFERENCE_PHONE + " format");
            }
            if (a[2].length() != 64) {
                throw new IOException("invalid " + BISQ_SHARED_PREFERENCE_PHONE + " format");
            }
            if (!a[0].equals(PHONE_MAGIC_ANDROID)) {
                throw new IOException("invalid " + BISQ_SHARED_PREFERENCE_PHONE + " format");
            }
            key = a[1];
            apsToken = a[2];
            isInitialized = true;
        }
        catch (IOException e) {
            key = "";
            apsToken = "";
            isInitialized = false;
        }
    }

    public String description() {
        return PHONE_MAGIC_ANDROID+PHONE_SEPARATOR+key+PHONE_SEPARATOR+apsToken;
    }

    public void save() {
        SharedPreferences.Editor editor = context.getSharedPreferences(BISQ_SHARED_PREFERENCES, MODE_PRIVATE).edit();
        editor.putString("BISQ_SHARED_PREFERENCE_PHONE", description());
        editor.apply();
    }
}
