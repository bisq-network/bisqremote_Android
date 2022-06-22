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

package bisq.android.services

import android.util.Log
import bisq.android.database.BisqNotification
import bisq.android.model.Device
import bisq.android.model.NotificationMessage
import bisq.android.model.NotificationMessage.Companion.BISQ_MESSAGE_ANDROID_MAGIC
import bisq.android.util.CryptoUtil
import bisq.android.util.DateUtil
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import java.text.ParseException
import java.util.Date

object NotificationProcessor {

    private const val TAG = "NotificationProcessor"
    private const val NOTIFICATION_CONTENT_SEGMENTS = 3

    @Throws(ProcessingException::class)
    fun processNotification(notificationContent: String?): BisqNotification {
        @Suppress("TooGenericExceptionCaught")
        try {
            val notificationMessage = parseNotificationContent(notificationContent)
            val decryptedNotificationPayload = decryptNotificationPayload(
                notificationMessage.encryptedPayload, notificationMessage.initializationVector
            )
            val bisqNotification = deserializeNotificationPayload(decryptedNotificationPayload)
            bisqNotification.receivedDate = Date().time
            return bisqNotification
        } catch (e: Throwable) {
            when (e) {
                is ParseException, is DecryptingException, is DeserializationException -> {
                    val message = "Failed to process notification; ${e.message}"
                    Log.e(TAG, message)
                    throw ProcessingException(message)
                }
                else -> throw e
            }
        }
    }

    @Throws(ParseException::class)
    @Suppress("ThrowsCount")
    fun parseNotificationContent(notificationContent: String?): NotificationMessage {
        val array = notificationContent?.split("\\|".toRegex())?.dropLastWhile { it.isEmpty() }
            ?.toTypedArray()
        if (array == null || array.size != NOTIFICATION_CONTENT_SEGMENTS) {
            throw ParseException("Invalid format", 0)
        }
        val magicValue = array[0]
        val initializationVector = array[1]
        val encryptedPayload = array[2]
        if (magicValue != BISQ_MESSAGE_ANDROID_MAGIC) {
            throw ParseException("Invalid magic value", 0)
        }
        if (initializationVector.length != CryptoUtil.IV_LENGTH) {
            throw ParseException(
                "Invalid initialization vector; must be ${CryptoUtil.IV_LENGTH} characters",
                0
            )
        }
        return NotificationMessage(magicValue, initializationVector, encryptedPayload)
    }

    @Throws(DecryptingException::class)
    @Suppress("ThrowsCount")
    fun decryptNotificationPayload(encryptedPayload: String, initializationVector: String): String {
        @Suppress("TooGenericExceptionCaught")
        try {
            if (Device.instance.key == null) {
                throw IllegalStateException("Device key is null")
            }
            return CryptoUtil(Device.instance.key!!).decrypt(
                encryptedPayload, initializationVector
            )
        } catch (e: Throwable) {
            when (e) {
                is IllegalStateException,
                is IllegalArgumentException,
                is CryptoUtil.Companion.CryptoException -> {
                    val message = "Failed to decrypt notification payload"
                    Log.e(TAG, "$message: $encryptedPayload")
                    throw DecryptingException(message)
                }
                else -> throw e
            }
        }
    }

    @Throws(DeserializationException::class)
    fun deserializeNotificationPayload(decryptedPayload: String): BisqNotification {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(Date::class.java, DateUtil())
        val gson = gsonBuilder.create()
        try {
            return gson.fromJson(decryptedPayload, BisqNotification::class.java)
        } catch (e: JsonSyntaxException) {
            val message = "Failed to deserialize notification payload"
            Log.e(TAG, "$message: $decryptedPayload")
            throw DeserializationException(message)
        }
    }
}

class ProcessingException(message: String) : Exception(message)
class DecryptingException(message: String) : Exception(message)
class DeserializationException(message: String) : Exception(message)
