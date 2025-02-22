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
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import bisq.android.util.DateUtil

@Database(entities = [DebugLog::class], version = 1, exportSchema = false)
@TypeConverters(DateUtil::class)
abstract class DebugLogDatabase : RoomDatabase() {

    abstract fun debugLogDao(): DebugLogDao

    companion object {

        private var instance: DebugLogDatabase? = null

        fun getDatabase(context: Context): DebugLogDatabase {
            if (instance == null) {
                synchronized(DebugLogDatabase::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            DebugLogDatabase::class.java, "debug.db"
                        ).build()
                    }
                }
            }
            return instance!!
        }
    }
}
