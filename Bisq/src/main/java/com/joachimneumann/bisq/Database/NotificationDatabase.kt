package com.joachimneumann.bisq.Database


import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context

@Database(entities = arrayOf(RawBisqNotification::class), version = 1, exportSchema = false)
@TypeConverters(DateTypeConverter::class)
abstract class NotificationDatabase : RoomDatabase() {

    abstract fun rawBisqNotificationDao(): RawBisqNotificationDao

    companion object {

        private var INSTANCE: NotificationDatabase? = null


        fun getDatabase(context: Context): NotificationDatabase {
            if (INSTANCE == null) {
                synchronized(NotificationDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                NotificationDatabase::class.java, "weather.db")
                                .build()
                    }
                }
            }
            return INSTANCE!!
        }
    }

}