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

package bisq.android.tests.services

import bisq.android.model.Device
import bisq.android.model.NotificationMessage.Companion.BISQ_MESSAGE_ANDROID_MAGIC
import bisq.android.services.DecryptingException
import bisq.android.services.DeserializationException
import bisq.android.services.NotificationProcessor
import bisq.android.util.CryptoUtil
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.Before
import org.junit.Test
import java.text.ParseException
import java.util.UUID

class NotificationProcessorTest {
    private var token = generateToken()
    private var iv = generateValidIV()

    @Before
    fun setup() {
        Device.instance.newToken(token)
    }

    @Test
    fun testParseValidNotificationContent() {
        val notificationMessage =
            NotificationProcessor.parseNotificationContent(
                "$BISQ_MESSAGE_ANDROID_MAGIC|$iv|encryptedPayload"
            )
        assertThat(notificationMessage.magicValue)
            .describedAs("Magic value")
            .isEqualTo(BISQ_MESSAGE_ANDROID_MAGIC)
        assertThat(notificationMessage.initializationVector)
            .describedAs("Initialization vector")
            .isEqualTo(iv)
        assertThat(notificationMessage.encryptedPayload)
            .describedAs("Encrypted payload")
            .isEqualTo("encryptedPayload")
    }

    @Test(expected = ParseException::class)
    fun testParseEmptyNotificationContentRaisesException() {
        NotificationProcessor.parseNotificationContent("")
    }

    @Test(expected = ParseException::class)
    fun testParseNullNotificationContentRaisesException() {
        NotificationProcessor.parseNotificationContent(null)
    }

    @Test(expected = ParseException::class)
    fun testParseNotificationContentInUnexpectedFormatRaisesException() {
        NotificationProcessor.parseNotificationContent(
            "this is not the format you are looking for"
        )
    }

    @Test(expected = ParseException::class)
    fun testParseInvalidMagicValueRaisesException() {
        NotificationProcessor.parseNotificationContent(
            "magicValue|initializationVector|encryptedPayload"
        )
    }

    @Test(expected = ParseException::class)
    fun testParseInvalidInitializationVectorLengthRaisesException() {
        NotificationProcessor.parseNotificationContent(
            "$BISQ_MESSAGE_ANDROID_MAGIC|initializationVector|encryptedPayload"
        )
    }

    @Test
    fun testDecryptValidMessage() {
        val payload = "{" +
            "\"type\":\"TRADE\"," +
            "\"title\":\"Trade confirmed\"," +
            "\"message\":\"The trade with ID 38765384 is confirmed.\"," +
            "\"receivedDate\":1740381234151," +
            "\"sentDate\":1740381174151" +
            "}"

        val encryptedPayload = CryptoUtil(Device.instance.key!!).encrypt(payload, iv)

        val decryptedPayload =
            NotificationProcessor.decryptNotificationPayload(encryptedPayload, iv)

        assertThat(decryptedPayload.trim())
            .describedAs("Decrypted payload")
            .isEqualTo(payload)
    }

    @Test(expected = DecryptingException::class)
    fun testDecryptFailureRaisesException() {
        NotificationProcessor.decryptNotificationPayload("encryptedPayload", iv)
    }

    @Test(expected = DecryptingException::class)
    fun testDecryptWithNoDeviceKeyRaisesException() {
        Device.instance.key = null
        NotificationProcessor.decryptNotificationPayload("encryptedPayload", iv)
    }

    @Test(expected = DeserializationException::class)
    fun testDeserializeFailureRaisesException() {
        NotificationProcessor.deserializeNotificationPayload("decryptedPayload")
    }

    @Test
    fun testDeserializeValidNotificationMessageIsDeserialized() {
        val payload = "{" +
            "\"type\":\"TRADE\"," +
            "\"title\":\"Trade confirmed\"," +
            "\"message\":\"The trade with ID 38765384 is confirmed.\"," +
            "\"receivedDate\":1740381234151," +
            "\"sentDate\":1740381174151" +
            "}"

        val deserializedBisqNotification =
            NotificationProcessor.deserializeNotificationPayload(payload)

        SoftAssertions.assertSoftly { softAssertions: SoftAssertions ->
            softAssertions.assertThat(deserializedBisqNotification.type)
                .describedAs("Notification type")
                .isEqualTo("TRADE")
            softAssertions.assertThat(deserializedBisqNotification.title)
                .describedAs("Notification title")
                .isEqualTo("Trade confirmed")
            softAssertions.assertThat(deserializedBisqNotification.message)
                .describedAs("Notification message")
                .isEqualTo("The trade with ID 38765384 is confirmed.")
            softAssertions.assertThat(deserializedBisqNotification.sentDate)
                .describedAs("Notification sent date")
                .isEqualTo(1740381174151L)
            softAssertions.assertThat(deserializedBisqNotification.receivedDate)
                .describedAs("Notification received date")
                .isEqualTo(1740381234151L)
        }
    }

    private fun generateToken(): String {
        val uuid1 = UUID.randomUUID().toString()
        val uuid2 = UUID.randomUUID().toString()
        var uuid = uuid1 + uuid2
        uuid = uuid.replace("-", "")
        return uuid.substring(0, 32)
    }

    private fun generateValidIV(): String {
        var uuid = UUID.randomUUID().toString()
        uuid = uuid.replace("-", "")
        return uuid.substring(0, 16)
    }
}
