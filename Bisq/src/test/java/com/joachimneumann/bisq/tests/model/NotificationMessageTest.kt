package bisq.android.tests.model

import bisq.android.database.BisqNotification
import bisq.android.model.Device
import bisq.android.model.NotificationMessage
import bisq.android.model.NotificationType
import bisq.android.services.BISQ_MESSAGE_ANDROID_MAGIC
import bisq.android.util.CryptoUtil
import bisq.android.util.DateUtil
import com.google.gson.GsonBuilder
import org.junit.Before
import org.junit.Test
import java.util.*

class NotificationMessageTest {

    private var token = generateToken()
    private var iv = generateValidIV()

    @Before
    fun setup() {
        Device.instance.newToken(token)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testEmptyNotificationRaisesException() {
        NotificationMessage("")
    }

    @Test(expected = IllegalArgumentException::class)
    fun testNullNotificationRaisesException() {
        NotificationMessage(null)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testNotificationInUnexpectedFormatRaisesException() {
        NotificationMessage("this is not the format you are looking for")
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidMagicValueRaisesException() {
        NotificationMessage("magicValue|initializationVector|encryptedPayload")
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidInitializationVectorLengthRaisesException() {
        NotificationMessage("$BISQ_MESSAGE_ANDROID_MAGIC|initializationVector|encryptedPayload")
    }

    @Test(expected = Exception::class)
    fun testUnableToDecryptMessageRaisesException() {
        NotificationMessage("$BISQ_MESSAGE_ANDROID_MAGIC|$iv|encryptedPayload")
    }

    @Test(expected = Exception::class)
    fun testDecryptingWithNoDeviceKeyRaisesException() {
        Device.instance.key = null
        NotificationMessage("$BISQ_MESSAGE_ANDROID_MAGIC|$iv|encryptedPayload")
    }

    @Test(expected = Exception::class)
    fun testUnableToDeserializeMessageRaisesException() {
        val message = "this message cannot be deserialized"
        val encryptedPayload = CryptoUtil(Device.instance.key!!).encrypt(message, iv)
        NotificationMessage("$BISQ_MESSAGE_ANDROID_MAGIC|$iv|$encryptedPayload")
    }

    @Test
    fun testValidNotificationMessageIsDeserialized() {
        val now = Date()
        val bisqNotification = BisqNotification()
        bisqNotification.type = NotificationType.TRADE.name
        bisqNotification.title = "(example) Trade confirmed"
        bisqNotification.message = "The trade with ID 38765384 is confirmed."
        bisqNotification.sentDate = now.time - 1000 * 60 // 1 minute earlier
        bisqNotification.receivedDate = now.time

        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(Date::class.java, DateUtil())
        val gson = gsonBuilder.create()

        val message = gson.toJson(bisqNotification)

        val encryptedPayload = CryptoUtil(Device.instance.key!!).encrypt(message, iv)
        NotificationMessage("$BISQ_MESSAGE_ANDROID_MAGIC|$iv|$encryptedPayload")
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
