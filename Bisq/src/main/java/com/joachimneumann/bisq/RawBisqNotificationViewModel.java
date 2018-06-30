package com.joachimneumann.bisq;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.joachimneumann.bisq.Database.NotificationRepository;
import com.joachimneumann.bisq.Database.RawBisqNotification;

import java.util.List;

public class RawBisqNotificationViewModel extends AndroidViewModel {

    private NotificationRepository mRepository;
    public LiveData<List<RawBisqNotification>> rawBisqNotifications;

    public RawBisqNotificationViewModel (Application application) {
        super(application);
        mRepository = new NotificationRepository(application);
        rawBisqNotifications = mRepository.getAllRawBisqNotifications();
    }

    public void insert(RawBisqNotification rawBisqNotification) {
        mRepository.insert(rawBisqNotification);
    }

}
