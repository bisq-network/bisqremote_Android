package com.joachimneumann.bisq;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.joachimneumann.bisq.Database.RawBisqNotification;

import java.util.List;

public class NotificationTable extends AppCompatActivity {
    private TextView tabletext;

    private RawBisqNotificationViewModel mViewModel;
    private NotificationManager notificationManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel;

            notificationChannel = new NotificationChannel("Bisq", "Bisq", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        setContentView(R.layout.activity_notifcationtable);
        tabletext = findViewById(R.id.tableText);
        tabletext.setText("list");

        mViewModel = ViewModelProviders.of(this).get(RawBisqNotificationViewModel.class);
        mViewModel.rawBisqNotifications.observe(this, new Observer<List<RawBisqNotification>>() {
            @Override
            public void onChanged(@Nullable List<RawBisqNotification> rawBisqNotifications) {
                updateGUI(rawBisqNotifications);
            }
        });
    }

    private void updateGUI(final @NonNull List<RawBisqNotification> rawBisqNotifications) {
        tabletext.setText("n = "+rawBisqNotifications.size());

    }
}