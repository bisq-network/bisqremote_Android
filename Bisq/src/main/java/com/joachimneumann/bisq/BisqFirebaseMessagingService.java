package com.joachimneumann.bisq;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class BisqFirebaseMessagingService extends FirebaseMessagingService {
    public static final String BISQ_MESSAGE_ANDROID_MAGIC = "BisqMessageAndroid";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String encrypted = remoteMessage.getData().get("encrypted");
        if (encrypted != null) {
            processNotification(encrypted);
        }
    }

    private void processNotification(String e) {
        String key;
        String enc;
        String[] array = e.split("\\|");
        if (array.length == 3) {
            if (array[0].equals(BISQ_MESSAGE_ANDROID_MAGIC)) {
                if (array[1].length() == 16) {
                    key = array[2];
                    enc = array[3];
                }
            }
        }
    }
}
