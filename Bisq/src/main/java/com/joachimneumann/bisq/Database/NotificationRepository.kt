package com.joachimneumann.bisq.Database

import android.arch.lifecycle.LiveData
import android.content.Context
import android.os.AsyncTask
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking


class NotificationRepository(context: Context){

    private val bisqNotificationDao: BisqNotificationDao

    private val delegate: NotificationRepository? = null

    val allBisqNotifications: LiveData<List<BisqNotification>>

    init {
        val db = NotificationDatabase.getDatabase(context)
        bisqNotificationDao = db.bisqNotificationDao()
        allBisqNotifications = bisqNotificationDao.all
    }

    fun insert(bisqNotification: BisqNotification) {
        async { bisqNotificationDao.insert(bisqNotification) }
    }

    fun delete(bisqNotification: BisqNotification) {
        async { bisqNotificationDao.delete(bisqNotification) }
    }

    fun getFromID(id: Int): BisqNotification? {
        var x: BisqNotification? = null
        runBlocking {
            async {
                x = bisqNotificationDao.getFromID(id)
            }.await()
        }
        return x
    }

    fun nukeTable() {
        async { bisqNotificationDao.nukeTableBisqNotification() }
    }

    fun markAllAsRead() {
        async { bisqNotificationDao.markAllAsRead(true) }
    }

}