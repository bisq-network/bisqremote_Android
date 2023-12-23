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
import bisq.android.util.CryptoUtil.Companion.KEY_LENGTH
import bisq.android.util.CryptoUtil.Companion.generateKey
import java.io.IOException

class Device private constructor() {
    companion object {
        private const val TAG = "Device"
        private const val BISQ_SHARED_PREFERENCE_FILE = "preferences.txt"
        private const val BISQ_SHARED_PREFERENCE_PAIRING_TOKEN = "pairingToken"
        private const val PAIRING_TOKEN_SEGMENTS = 4
        private const val PAIRING_TOKEN_MAGIC_INDEX = 0
        private const val PAIRING_TOKEN_KEY_INDEX = 2
        private const val PAIRING_TOKEN_TOKEN_INDEX = 3
        private const val TOKEN_LENGTH = 32
        const val DEVICE_MAGIC_ANDROID = "android"
        const val DEVICE_SEPARATOR = "|"
        const val DEVICE_SEPARATOR_ESCAPED = "\\|"
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
            DEVICE_MAGIC_ANDROID + DEVICE_SEPARATOR + descriptor + DEVICE_SEPARATOR + key +
                DEVICE_SEPARATOR + token
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
        val pairingToken = prefs.getString(BISQ_SHARED_PREFERENCE_PAIRING_TOKEN, null)
        if (pairingToken != null) {
            return fromString(pairingToken)
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

    @Suppress("ThrowsCount")
    fun fromString(s: String): Boolean {
        val a = s.split(DEVICE_SEPARATOR_ESCAPED.toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        try {
            if (a.size != PAIRING_TOKEN_SEGMENTS) {
                throw IOException(
                    "Invalid $BISQ_SHARED_PREFERENCE_PAIRING_TOKEN format"
                )
            }
            if (a[PAIRING_TOKEN_KEY_INDEX].length != KEY_LENGTH) {
                throw IOException(
                    "Invalid $BISQ_SHARED_PREFERENCE_PAIRING_TOKEN format; incorrect key value"
                )
            }
            if (a[PAIRING_TOKEN_TOKEN_INDEX].length < TOKEN_LENGTH) {
                throw IOException(
                    "Invalid $BISQ_SHARED_PREFERENCE_PAIRING_TOKEN format; incorrect token value"
                )
            }
            if (a[PAIRING_TOKEN_MAGIC_INDEX] != DEVICE_MAGIC_ANDROID) {
                throw IOException(
                    "Invalid $BISQ_SHARED_PREFERENCE_PAIRING_TOKEN format;" + " incorrect device magic value"
                )
            }
            key = a[PAIRING_TOKEN_KEY_INDEX]
            token = a[PAIRING_TOKEN_TOKEN_INDEX]
            status = DeviceStatus.PAIRED
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
        } else {
            manufacturer.capitalizeEachWord() + " " + model
        }
    }

    fun isEmulator(): Boolean {
        val emulatorHardware = listOf(
            "goldfish",
            "ranchu"
        )
        val emulatorModels = listOf(
            "google_sdk",
            "Emulator",
            "Android SDK built for x86"
        )
        val emulatorManufacturers = listOf(
            "Genymotion"
        )
        val emulatorProducts = listOf(
            "sdk_google",
            "google_sdk",
            "sdk",
            "sdk_x86",
            "sdk_gphone64_arm64",
            "vbox86p",
            "emulator",
            "simulator"
        )
        return (
            Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic") ||
                Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.startsWith("unknown") ||
                emulatorHardware.contains(Build.HARDWARE) ||
                emulatorModels.contains(Build.MODEL) ||
                emulatorManufacturers.contains(Build.MANUFACTURER) ||
                emulatorProducts.contains(Build.PRODUCT)
            )
    }
}
