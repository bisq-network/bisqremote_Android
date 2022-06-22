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

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BisqNotificationDao {
    @get:Query("SELECT * FROM BisqNotification ORDER BY sentDate DESC")
    val all: LiveData<List<BisqNotification>>

    @Query("SELECT * FROM BisqNotification ORDER BY sentDate DESC")
    suspend fun getAll(): List<BisqNotification>

    @Query("SELECT * FROM BisqNotification WHERE uid=:uid")
    suspend fun getFromUid(uid: Int): BisqNotification

    @Insert
    suspend fun insert(bisqNotification: BisqNotification): Long

    @Delete
    suspend fun delete(bisqNotification: BisqNotification)

    @Query("DELETE FROM BisqNotification")
    suspend fun deleteAll()

    @Query("UPDATE BisqNotification SET read=1")
    suspend fun markAllAsRead()

    @Query("UPDATE BisqNotification SET read=1 WHERE uid=:uid")
    suspend fun markAsRead(uid: Int)
}
