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

package bisq.android.screens.elements

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
