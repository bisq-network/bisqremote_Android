package com.joachimneumann.bisq;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class Phone {
    private static final String PHONE_MAGIC_ANDROID = "BisqPhoneAndroid";
    static final String PHONE_SEPARATOR = "_";
    private static final String BISQ_SHARED_PREFERENCES = "BisqPreferences";
    private static final String BISQ_SHARED_PREFERENCE_PHONE = "BisqPhone";

    public String key;
    public String apsToken;
    public Boolean isInitialized;
    private Context context;


    public Phone(Context c) {
        context = c;
        SharedPreferences prefs = context.getSharedPreferences(BISQ_SHARED_PREFERENCES, MODE_PRIVATE);
        String phoneString = prefs.getString(BISQ_SHARED_PREFERENCE_PHONE, null);
        if (phoneString != null) {
            fromString(phoneString);
        } else {
            key = "";
            apsToken = "";
            isInitialized = false;
        }
    }

    public void create(String token) {
        apsToken = token;
        // create key and store to Userdefaults
        key = UUID.randomUUID().toString().replace("-",  "");
        isInitialized = true;
        save();
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
