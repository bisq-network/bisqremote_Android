package com.joachimneumann.bisq.screens.elements

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers

open class Element(private val id: Int) {

    fun isDisplayed(): Boolean {
        try {
            Espresso.onView(ViewMatchers.withId(id))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        } catch (e: AssertionError) {
            return false
        }
        return true
    }

    fun isEnabled(): Boolean {
        try {
            Espresso.onView(ViewMatchers.withId(id))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        } catch (e: AssertionError) {
            return false
        }
        return true
    }

}
