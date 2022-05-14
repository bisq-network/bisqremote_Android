package bisq.android.database

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class NotificationRepository(context: Context) {

    private val bisqNotificationDao: BisqNotificationDao

    val allBisqNotifications: LiveData<List<BisqNotification>>

    init {
        val db = NotificationDatabase.getDatabase(context)
        bisqNotificationDao = db.bisqNotificationDao()
        allBisqNotifications = bisqNotificationDao.all
    }

    suspend fun insert(bisqNotification: BisqNotification) = coroutineScope {
        launch { bisqNotificationDao.insert(bisqNotification) }
    }

    suspend fun delete(bisqNotification: BisqNotification) = coroutineScope {
        launch { bisqNotificationDao.delete(bisqNotification) }
    }

    fun getFromUid(uid: Int): BisqNotification? {
        var x: BisqNotification?
        runBlocking {
            coroutineScope {
                x = bisqNotificationDao.getFromUid(uid)
            }
        }
        return x
    }

    suspend fun deleteAll() = coroutineScope {
        launch { bisqNotificationDao.deleteAll() }
    }

    suspend fun markAllAsRead() = coroutineScope {
        launch { bisqNotificationDao.markAllAsRead(true) }
    }

    suspend fun markAsRead(uid: Int) = coroutineScope {
        launch { bisqNotificationDao.markAsRead(uid, true) }
    }
}
