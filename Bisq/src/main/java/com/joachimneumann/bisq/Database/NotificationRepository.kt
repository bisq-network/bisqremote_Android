package com.joachimneumann.bisq.Database

import android.arch.lifecycle.LiveData
import android.content.Context
import android.os.AsyncTask

class NotificationRepository(context: Context) {


    private val bisqNotificationDao: BisqNotificationDao
    val allBisqNotifications: LiveData<List<BisqNotification>>

    init {
        val db = NotificationDatabase.getDatabase(context)
        bisqNotificationDao = db.bisqNotificationDao()
        allBisqNotifications = bisqNotificationDao.all
    }

    fun insert(bisqNotification: BisqNotification) {
        insertAsyncTask(bisqNotificationDao).execute(bisqNotification)
    }


    fun erase() {
        eraseAsyncTask(bisqNotificationDao).execute()
    }

    fun markAllAsRead() {
        markAllAsReadAsyncTask(bisqNotificationDao).execute()
    }


    private class insertAsyncTask internal constructor(private val mAsyncTaskDao: BisqNotificationDao) : AsyncTask<BisqNotification, Void, Void>() {
        override fun doInBackground(vararg params: BisqNotification): Void? {
            mAsyncTaskDao.insert(params[0])
            return null
        }
    }

    private class eraseAsyncTask internal constructor(private val mAsyncTaskDao: BisqNotificationDao) : AsyncTask<BisqNotification, Void, Void>() {
        override fun doInBackground(vararg params: BisqNotification): Void? {
            mAsyncTaskDao.nukeTableBisqNotification()
            return null
        }
    }

    private class markAllAsReadAsyncTask internal constructor(private val mAsyncTaskDao: BisqNotificationDao) : AsyncTask<BisqNotification, Void, Void>() {
        override fun doInBackground(vararg params: BisqNotification): Void? {
            mAsyncTaskDao.markAllAsRead(true)
            return null
        }
    }
}