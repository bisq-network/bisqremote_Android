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

package bisq.android.model

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.util.Log
import bisq.android.ext.capitalizeEachWord
import bisq.android.util.generateKey
import java.io.IOException

class Device private constructor() {

    companion object {
        private const val TAG = "Device"
        const val DEVICE_MAGIC_ANDROID = "android"
        const val DEVICE_SEPARATOR = "|"
        const val DEVICE_SEPARATOR_ESCAPED = "\\|"
        const val BISQ_SHARED_PREFERENCE_FILE = "pairingToken.txt"
        const val BISQ_SHARED_PREFERENCE_PAIRING_TOKEN = "pairingToken"
        val instance: Device by lazy {
            Holder.INSTANCE
        }
    }

    var descriptor: String? = getDeviceName()
    var key: String? = null
    var token: String? = null
    var status: DeviceStatus = DeviceStatus.UNPAIRED

    private object Holder {
        var INSTANCE = Device()
    }

    fun pairingToken(): String? {
        return if (key != null) {
            DEVICE_MAGIC_ANDROID + DEVICE_SEPARATOR + descriptor + DEVICE_SEPARATOR + key + DEVICE_SEPARATOR + token
        } else null
    }

    fun newToken(token: String) {
        this.token = token
        key = generateKey()
        status = DeviceStatus.UNPAIRED
    }

    fun reset() {
        key = null
        token = null
        status = DeviceStatus.UNPAIRED
    }

    fun readFromPreferences(context: Context): Boolean {
        val prefs = context.getSharedPreferences(BISQ_SHARED_PREFERENCE_FILE, MODE_PRIVATE)
        val deviceString = prefs.getString(BISQ_SHARED_PREFERENCE_PAIRING_TOKEN, null)
        if (deviceString != null) {
            return fromString(deviceString)
        }
        return false
    }

    fun saveToPreferences(context: Context) {
        val editor = context.getSharedPreferences(BISQ_SHARED_PREFERENCE_FILE, MODE_PRIVATE).edit()
        editor.putString(BISQ_SHARED_PREFERENCE_PAIRING_TOKEN, pairingToken())
        editor.apply()
    }

    fun clearPreferences(context: Context) {
        val editor = context.getSharedPreferences(BISQ_SHARED_PREFERENCE_FILE, MODE_PRIVATE).edit()
        editor.clear()
        editor.apply()
    }

    fun fromString(s: String): Boolean {
        val a =
            s.split(DEVICE_SEPARATOR_ESCAPED.toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        try {
            if (a.size != 4) {
                throw IOException("invalid $BISQ_SHARED_PREFERENCE_PAIRING_TOKEN format")
            }
            if (a[2].length != 32) {
                throw IOException("invalid $BISQ_SHARED_PREFERENCE_PAIRING_TOKEN format; incorrect key value")
            }
            if (a[3].length < 32) {
                throw IOException("invalid $BISQ_SHARED_PREFERENCE_PAIRING_TOKEN format; incorrect token value")
            }
            if (a[0] != DEVICE_MAGIC_ANDROID) {
                throw IOException("invalid $BISQ_SHARED_PREFERENCE_PAIRING_TOKEN format; incorrect device magic value")
            }
            key = a[2]
            token = a[3]
            status = DeviceStatus.UNPAIRED
            return true
        } catch (e: IOException) {
            Log.w(TAG, e.message.toString())
            reset()
        }
        return false
    }

    fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            model.capitalizeEachWord()
        } else manufacturer.capitalizeEachWord() + " " + model
    }

    fun isEmulator(): Boolean {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("sdk_gphone64_arm64")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator"))
    }

}
