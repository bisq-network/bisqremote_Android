package bisq.android.tests

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import bisq.android.model.Device
import bisq.android.model.DeviceStatus
import bisq.android.screens.*
import org.junit.After
import org.junit.Before

abstract class BaseTest {

    protected val applicationContext: Context = ApplicationProvider.getApplicationContext()

    protected val welcomeScreen = WelcomeScreen()
    protected val settingsScreen = SettingsScreen()
    protected val pairingScanScreen = PairingScanScreen()
    protected val pairingSendScreen = PairingSendScreen()
    protected val pairingSuccessScreen = PairingSuccessScreen()
    protected val notificationDetailScreen = NotificationDetailScreen()
    protected val notificationTableScreen = NotificationTableScreen()

    @Before
    open fun setup() {
        Intents.init()
    }

    @After
    open fun cleanup() {
        Intents.release()
    }

    fun pairDevice() {
        val token =
            "fnWtGaJGSByKiPwT71O3Lo:APA91bGU05lvoKxvz3Y0fnFHytSveA_juVjq2QMY3_H9URqDsEpLHGbLSFBN" +
                "3wY7YdHDD3w52GECwRWuKGBJm1O1f5fJhVvcr1rJxo94aDjoWwsnkVp-ecWwh5YY_MQ6LRqbWzumCeX_"
        Device.instance.newToken(token)
        Device.instance.status = DeviceStatus.PAIRED
        Device.instance.saveToPreferences(applicationContext)
    }

}
