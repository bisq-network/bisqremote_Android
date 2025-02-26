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

package bisq.android

import android.util.Log
import bisq.android.database.DebugLog
import bisq.android.database.DebugLogLevel
import bisq.android.database.DebugLogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Logging {
    companion object {
        private const val TAG = "Logging"
    }

    // Attempt to initialize debugRepository with the application context.
    // If the context is unavailable (e.g., in unit tests where Application is not initialized),
    // catch the exception and log a warning instead of throwing a NullPointerException.
    // This ensures that tests still work since trying to provide a mocked context is not straight forward.
    @Suppress("SwallowedException", "TooGenericExceptionCaught")
    private val debugRepository: DebugLogRepository? = try {
        val context = Application.applicationContext()
        DebugLogRepository(context)
    } catch (e: NullPointerException) {
        Log.w(TAG, "Skipping debugRepository initialization due to missing context")
        null
    }

    fun debug(tag: String, msg: String) {
        Log.d(tag, msg)
        insert(DebugLogLevel.DEBUG, msg)
    }

    fun info(tag: String, msg: String) {
        Log.i(tag, msg)
        insert(DebugLogLevel.INFO, msg)
    }

    fun warn(tag: String, msg: String) {
        Log.w(tag, msg)
        insert(DebugLogLevel.WARN, msg)
    }

    fun error(tag: String, msg: String) {
        Log.e(tag, msg)
        insert(DebugLogLevel.ERROR, msg)
    }

    private fun insert(level: DebugLogLevel, msg: String) {
        debugRepository?.let {
            CoroutineScope(Dispatchers.IO).launch {
                it.insert(
                    DebugLog(
                        timestamp = System.currentTimeMillis(),
                        level = level,
                        text = msg
                    )
                )
            }
        } ?: Log.w(TAG, "Skipping log insert; debugRepository is unavailable")
    }
}
