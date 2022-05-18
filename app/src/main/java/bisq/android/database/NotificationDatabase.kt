package bisq.android.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import bisq.android.util.DateUtil

@Database(entities = [BisqNotification::class], version = 1, exportSchema = false)
@TypeConverters(DateUtil::class)
abstract class NotificationDatabase : RoomDatabase() {

    abstract fun bisqNotificationDao(): BisqNotificationDao

    companion object {

        private var instance: NotificationDatabase? = null

        fun getDatabase(context: Context): NotificationDatabase {
            if (instance == null) {
                synchronized(NotificationDatabase::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            NotificationDatabase::class.java, "notifications.db"
                        ).build()
                    }
                }
            }
            return instance!!
        }

    }
}
