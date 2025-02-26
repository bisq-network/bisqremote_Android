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

class DebugLogRepository(context: Context) {

    private val debugLogDao: DebugLogDao

    val allLogs: LiveData<List<DebugLog>>

    init {
        val db = DebugLogDatabase.getDatabase(context)
        debugLogDao = db.debugLogDao()
        allLogs = debugLogDao.all
    }

    suspend fun insert(debugLog: DebugLog) = coroutineScope {
        launch {
            debugLogDao.insert(debugLog)
        }
    }

    suspend fun deleteAll() = coroutineScope {
        launch { debugLogDao.deleteAll() }
    }
}
