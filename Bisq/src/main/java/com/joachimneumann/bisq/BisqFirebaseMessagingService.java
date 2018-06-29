package com.joachimneumann.bisq;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class BisqFirebaseMessagingService extends FirebaseMessagingService {
    public static final String BISQ_MESSAGE_ANDROID_MAGIC = "BisqMessageAndroid";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String notificationMessage = remoteMessage.getData().get("encrypted");
        if (notificationMessage != null) {
            processNotification(notificationMessage);
        }
    }

    private void processNotification(String notificationMessage) {
        String initializationVector;
        String encryptedJson;
        String[] array = notificationMessage.split("\\|");
        if (array.length == 3) {
            if (array[0].equals(BISQ_MESSAGE_ANDROID_MAGIC)) {
                if (array[1].length() == 16) {
                    initializationVector = array[1];
                    encryptedJson = array[2];
                    Phone phone = Phone.getInstance();
                    CryptoHelper cryptoHelper = new CryptoHelper(phone.key);
                    String success = null;
                    try {
                        success = cryptoHelper.decrypt(encryptedJson, initializationVector);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (success != null) {
                        Log.i("Bisq", "decrypted json: "+success);
                    }


                }
            }
        }
    }
}
