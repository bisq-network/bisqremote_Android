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

import android.util.Log
import bisq.android.database.BisqNotification
import bisq.android.util.CryptoUtil
import bisq.android.util.CryptoUtil.Companion.IV_LENGTH
import bisq.android.util.DateUtil
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import java.text.ParseException
import java.util.Date

class NotificationMessage(private var notification: String?) {

    private lateinit var magicValue: String
    private lateinit var initializationVector: String
    private lateinit var encryptedPayload: String
    private lateinit var decryptedPayload: String

    lateinit var bisqNotification: BisqNotification

    companion object {
        private const val TAG = "NotificationMessage"
        const val BISQ_MESSAGE_ANDROID_MAGIC = "BisqMessageAndroid"
    }

    init {
        parseNotification()
        decryptNotificationMessage()
        deserializeNotificationMessage()
    }

    private fun parseNotification() {
        try {
            val array = notification?.split("\\|".toRegex())?.dropLastWhile { it.isEmpty() }
                ?.toTypedArray()
            if (array == null || array.size != 3) {
                throw ParseException("Invalid format", 0)
            }
            magicValue = array[0]
            initializationVector = array[1]
            encryptedPayload = array[2]
            if (magicValue != BISQ_MESSAGE_ANDROID_MAGIC) {
                throw ParseException("Invalid magic value", 0)
            }
            if (initializationVector.length != IV_LENGTH) {
                throw ParseException(
                    "Invalid initialization vector (must be $IV_LENGTH characters)",
                    0
                )
            }
        } catch (e: ParseException) {
            val message = "Failed to parse notification; ${e.message}"
            Log.e(TAG, message)
            throw ParseException(message, e.errorOffset)
        }
    }

    private fun decryptNotificationMessage() {
        try {
            decryptedPayload = CryptoUtil(Device.instance.key!!).decrypt(
                encryptedPayload, initializationVector
            )
        } catch (e: Exception) {
            val message = "Failed to decrypt notification"
            Log.e(TAG, "$message: $encryptedPayload")
            throw Exception(message)
        }
    }

    private fun deserializeNotificationMessage() {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(Date::class.java, DateUtil())
        val gson = gsonBuilder.create()
        try {
            bisqNotification = gson.fromJson(decryptedPayload, BisqNotification::class.java)
        } catch (e: JsonSyntaxException) {
            val message = "Failed to deserialize notification"
            Log.e(TAG, "$message: $decryptedPayload")
            throw Exception(message)
        }
    }
}
