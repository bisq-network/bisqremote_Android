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

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class RecyclerViewElement(private val id: Int) : ElementById(id) {
    fun scrollToPosition(position: Int) {
        onView(withId(id)).perform(
            scrollToPosition<ViewHolder>(position)
        )
    }

    fun clickAtPosition(position: Int) {
        onView(withId(id)).perform(
            scrollToPosition<ViewHolder>(position),
            actionOnItemAtPosition<ViewHolder>(position, click())
        )
    }

    fun swipeRightToLeftAtPosition(position: Int) {
        val from = CoordinatesProvider { it.getPointCoordinatesOfView(0.75f, 0.5f) }
        val to = CoordinatesProvider { it.getPointCoordinatesOfView(0f, 0.5f) }
        val swipeAction = GeneralSwipeAction(Swipe.FAST, from, to, Press.FINGER)
        onView(withId(id)).perform(
            scrollToPosition<ViewHolder>(position),
            actionOnItemAtPosition<ViewHolder>(position, swipeAction)
        )
    }

    private fun View.getPointCoordinatesOfView(xPercent: Float, yPercent: Float): FloatArray {
        val xy = IntArray(2).apply { getLocationOnScreen(this) }
        val x = xy[0] + (width - 1) * xPercent
        val y = xy[1] + (height - 1) * yPercent
        return floatArrayOf(x, y)
    }

    fun getItemCount(): Int {
        val count = intArrayOf(0)
        val matcher: Matcher<View?> = object : TypeSafeMatcher<View?>() {
            override fun describeTo(description: Description?) {
                // Intentionally left empty
            }

            override fun matchesSafely(item: View?): Boolean {
                count[0] = (item as RecyclerView).adapter!!.itemCount
                return true
            }
        }
        onView(allOf(withId(id), ViewMatchers.isDisplayed())).check(matches(matcher))
        return count[0]
    }

    fun getScrollPosition(): Int {
        val position = intArrayOf(0)
        val matcher: Matcher<View?> = object : TypeSafeMatcher<View?>() {
            override fun describeTo(description: Description?) {
                // Intentionally left empty
            }

            override fun matchesSafely(item: View?): Boolean {
                position[0] = ((item as RecyclerView).layoutManager!! as LinearLayoutManager)
                    .findFirstVisibleItemPosition()
                return true
            }
        }
        onView(allOf(withId(id), ViewMatchers.isDisplayed())).check(matches(matcher))
        return position[0]
    }

    fun getContentAtPosition(position: Int): ViewHolder {
        var viewHolder: ViewHolder? = null
        val matcher: Matcher<View?> = object : TypeSafeMatcher<View?>() {
            override fun describeTo(description: Description?) {
                // Intentionally left empty
            }

            override fun matchesSafely(item: View?): Boolean {
                viewHolder = (item as RecyclerView).findViewHolderForAdapterPosition(position)
                    ?: return false // has no item on such position
                return true
            }
        }
        onView(allOf(withId(id), ViewMatchers.isDisplayed())).check(matches(matcher))
        return viewHolder!!
    }
}
