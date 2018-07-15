package com.joachimneumann.bisq


import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.joachimneumann.bisq.Database.BisqNotification

import com.joachimneumann.bisq.Database.NotificationRepository
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking

class BisqNotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val mRepository: NotificationRepository
    var bisqNotifications: LiveData<List<BisqNotification>>

    init {
        mRepository = NotificationRepository(application)
        bisqNotifications = mRepository.allBisqNotifications
    }

    fun insert(bisqNotification: BisqNotification) {
        mRepository.insert(bisqNotification)
    }

    fun erase() {
        mRepository.erase()
    }

    fun getFromID(id: Int): BisqNotification {
        var x: BisqNotification? = null
        runBlocking {
            async {
                x = mRepository.getFromID(id)
            }.await()
        }
        return x!!
    }

    fun markAllAsRead() {
        mRepository.markAllAsRead()
    }
}
