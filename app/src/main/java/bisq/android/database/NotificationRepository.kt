/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

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
