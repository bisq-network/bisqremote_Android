package bisq.android.screens.elements

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import bisq.android.ui.notification.NotificationAdapter
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class RecyclerViewElement(private val id: Int) : Element(id) {

    fun scrollToPosition(position: Int) {
        onView(withId(id)).perform(
            scrollToPosition<RecyclerView.ViewHolder>(position)
        )
    }

    fun clickAtPosition(position: Int) {
        onView(withId(id)).perform(
            scrollToPosition<RecyclerView.ViewHolder>(position),
            actionOnItemAtPosition<RecyclerView.ViewHolder>(position, click())
        )
    }

    fun swipeToDeleteAtPosition(position: Int) {
        onView(withId(id)).perform(
            scrollToPosition<RecyclerView.ViewHolder>(position),
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                position, GeneralSwipeAction(
                    Swipe.FAST, GeneralLocation.BOTTOM_RIGHT, GeneralLocation.BOTTOM_LEFT,
                    Press.FINGER
                )
            )
        )
    }

    fun getItemCount(): Int {
        val count = intArrayOf(0)
        val matcher: Matcher<View?> = object : TypeSafeMatcher<View?>() {
            override fun describeTo(description: Description?) {}
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
            override fun describeTo(description: Description?) {}
            override fun matchesSafely(item: View?): Boolean {
                position[0] = ((item as RecyclerView).layoutManager!! as LinearLayoutManager)
                    .findFirstVisibleItemPosition()
                return true
            }
        }
        onView(allOf(withId(id), ViewMatchers.isDisplayed())).check(matches(matcher))
        return position[0]
    }

    fun getContentAtPosition(position: Int): String {
        var content = String()
        val matcher: Matcher<View?> = object : TypeSafeMatcher<View?>() {
            override fun describeTo(description: Description?) {}
            override fun matchesSafely(item: View?): Boolean {
                val viewHolder = (item as RecyclerView).findViewHolderForAdapterPosition(position)
                    ?: return false  // has no item on such position
                content =
                    (viewHolder as NotificationAdapter.NotificationViewHolder).title.text.toString()
                return true
            }
        }
        onView(allOf(withId(id), ViewMatchers.isDisplayed())).check(matches(matcher))
        return content
    }

    fun getReadStateAtPosition(position: Int): Boolean {
        var readState = false
        val matcher: Matcher<View?> = object : TypeSafeMatcher<View?>() {
            override fun describeTo(description: Description?) {}
            override fun matchesSafely(item: View?): Boolean {
                val viewHolder = (item as RecyclerView).findViewHolderForAdapterPosition(position)
                    ?: return false  // has no item on such position
                readState = (viewHolder as NotificationAdapter.NotificationViewHolder).read
                return true
            }
        }
        onView(allOf(withId(id), ViewMatchers.isDisplayed())).check(matches(matcher))
        return readState
    }

}
