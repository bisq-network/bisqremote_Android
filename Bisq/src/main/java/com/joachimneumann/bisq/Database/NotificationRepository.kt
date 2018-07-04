package com.joachimneumann.bisq.Database

import android.app.Application
import android.arch.lifecycle.LiveData
import android.content.Context
import android.os.AsyncTask

class NotificationRepository(context: Context) {
    private val rawBisqNotificationDao: RawBisqNotificationDao
    val allRawBisqNotifications: LiveData<List<RawBisqNotification>>

    init {
        val db = NotificationDatabase.getDatabase(context)
        rawBisqNotificationDao = db.rawBisqNotificationDao()
        allRawBisqNotifications = rawBisqNotificationDao.all
    }

    fun insert(rawBisqNotification: RawBisqNotification) {
        insertAsyncTask(rawBisqNotificationDao).execute(rawBisqNotification)
    }

    private class insertAsyncTask internal constructor(private val mAsyncTaskDao: RawBisqNotificationDao) : AsyncTask<RawBisqNotification, Void, Void>() {

        override fun doInBackground(vararg params: RawBisqNotification): Void? {
            mAsyncTaskDao.insert(params[0])
            return null
        }
    }
}
