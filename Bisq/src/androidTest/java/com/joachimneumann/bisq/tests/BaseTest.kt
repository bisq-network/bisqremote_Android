package com.joachimneumann.bisq.tests

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import com.joachimneumann.bisq.screens.*
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

}
