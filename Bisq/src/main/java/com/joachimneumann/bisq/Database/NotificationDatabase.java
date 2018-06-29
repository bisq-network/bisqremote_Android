package com.joachimneumann.bisq.Database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {RawBisqNotification.class}, version = 1, exportSchema = false)
@TypeConverters({DateTypeConverter.class})
public abstract class NotificationDatabase extends RoomDatabase {

    private static NotificationDatabase INSTANCE;

    public abstract RawBisqNotificationDao rawBisqNotificationDao();


    public static NotificationDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (NotificationDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            NotificationDatabase.class, "bisq_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}