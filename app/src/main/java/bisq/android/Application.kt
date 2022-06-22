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

import android.content.Context
import android.content.pm.PackageManager
import androidx.multidex.MultiDexApplication

class Application : MultiDexApplication() {
    init {
        instance = this
    }

    companion object {
        private var instance: Application? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        fun getAppVersion(): String {
            val context: Context = applicationContext()

            val version = try {
                val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                pInfo.versionName
            } catch (ignored: PackageManager.NameNotFoundException) {
                R.string.unknown
            }
            return version.toString()
        }
    }
}
