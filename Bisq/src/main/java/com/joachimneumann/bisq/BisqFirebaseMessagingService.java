package com.joachimneumann.bisq;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.joachimneumann.bisq.Database.NotificationDatabase;
import com.joachimneumann.bisq.Database.NotificationRepository;
import com.joachimneumann.bisq.Database.RawBisqNotification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                    Phone phone = Phone.getInstance(this);
                    if (phone != null) {
                        CryptoHelper cryptoHelper = new CryptoHelper(phone.key);
                        Log.i("Bisq", "key = "+phone.key);
                        Log.i("Bisq", "iv = "+initializationVector);
                        Log.i("Bisq", "encryptedJson = "+encryptedJson);
                        String success = null;
                        try {
                            success = cryptoHelper.decrypt(encryptedJson, initializationVector);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (success != null) {

                            // TODO add to database
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(success);
                                String pageName = obj.getJSONObject("pageInfo").getString("pageName");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            RawBisqNotification newNotification = new RawBisqNotification();
                            newNotification.setUid(1);
                            newNotification.setTitle("t");
                            NotificationRepository notificationRepository = new NotificationRepository(this);
                            notificationRepository.insert(newNotification);

                            Intent intent = new Intent(this, TransferCodeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            // Create the pending intent to launch the activity
                            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                                    PendingIntent.FLAG_ONE_SHOT);


                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "Bisq")
                                    .setSmallIcon(R.drawable.help)
                                    .setContentTitle("bisq")
                                    .setContentText(success)
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent);

                            NotificationManager notificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            notificationManager.notify((int)System.currentTimeMillis(), notificationBuilder.build());

                            Log.i("Bisq", "decrypted json: "+success);
                        }
                    }
                }
            }
        }
    }
}
