package com.joachimneumann.bisq.screens.elements

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers

class ButtonElement(private val id: Int) : Element(id) {

    fun click() {
        Espresso.onView(ViewMatchers.withId(id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.click())
    }

}
