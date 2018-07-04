package com.joachimneumann.bisq


import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

import com.joachimneumann.bisq.Database.NotificationRepository
import com.joachimneumann.bisq.Database.RawBisqNotification

class RawBisqNotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val mRepository: NotificationRepository
    var rawBisqNotifications: LiveData<List<RawBisqNotification>>

    init {
        mRepository = NotificationRepository(application)
        rawBisqNotifications = mRepository.allRawBisqNotifications
    }

    fun insert(rawBisqNotification: RawBisqNotification) {
        mRepository.insert(rawBisqNotification)
    }

}
