package com.joachimneumann.bisq.screens.dialogs

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*

open class Dialog(private val message: String) {

    fun isDisplayed(): Boolean {
        return try {
            onView(withText(message)).check(
                matches(withEffectiveVisibility(Visibility.VISIBLE))
            )
            true
        } catch (e: NoMatchingViewException) {
            false
        }
    }

}
