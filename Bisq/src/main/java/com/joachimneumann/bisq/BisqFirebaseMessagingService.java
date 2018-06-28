package com.joachimneumann.bisq;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class BisqFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String notifyData = remoteMessage.getData().get("score");

        if(notifyData.contains("|")){
            String[] itens = notifyData.split("\\|");
            notifyData = itens[0];
        }


        String notifyType = remoteMessage.getData().get("notifType");
        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("body");

//        if(!isAppInForeground(App.getContext())){
//            sendNotification(title, message, notifyData, notifyType);
//        }
    }
}
