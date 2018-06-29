package com.joachimneumann.bisq.Database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class NotificationRepository {
    private RawBisqNotificationDao rawBisqNotificationDao;
    private LiveData<List<RawBisqNotification>> allRawBisqNotifications;

    public NotificationRepository(Application application) {
        NotificationDatabase db = NotificationDatabase.getDatabase(application);
        rawBisqNotificationDao = db.rawBisqNotificationDao();
        allRawBisqNotifications = rawBisqNotificationDao.getAll();
    }

    public LiveData<List<RawBisqNotification>> getAllRawBisqNotifications() {
        return allRawBisqNotifications;
    }

    public void insert (RawBisqNotification rawBisqNotification) {
        new insertAsyncTask(rawBisqNotificationDao).execute(rawBisqNotification);
    }

    private static class insertAsyncTask extends AsyncTask<RawBisqNotification, Void, Void> {
        private RawBisqNotificationDao mAsyncTaskDao;

        insertAsyncTask(RawBisqNotificationDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final RawBisqNotification... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
