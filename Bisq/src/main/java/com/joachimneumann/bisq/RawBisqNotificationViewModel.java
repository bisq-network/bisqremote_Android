package com.joachimneumann.bisq;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.joachimneumann.bisq.Database.NotificationRepository;
import com.joachimneumann.bisq.Database.RawBisqNotification;

import java.util.List;

public class RawBisqNotificationViewModel extends AndroidViewModel {

    private NotificationRepository mRepository;
    private LiveData<List<RawBisqNotification>> allRawBisqNotifications;

    public RawBisqNotificationViewModel (Application application) {
        super(application);
        mRepository = new NotificationRepository(application);
        allRawBisqNotifications = mRepository.getAllRawBisqNotifications();
    }

    public void insert(RawBisqNotification rawBisqNotification) {
        mRepository.insert(rawBisqNotification);
    }

}
