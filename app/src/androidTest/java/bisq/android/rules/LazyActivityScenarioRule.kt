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

package bisq.android.rules

import android.app.Activity
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.rules.ExternalResource

/**
 * Provides a drop-in replacement for [ActivityScenarioRule] that allows for
 * optional launching of the Activity on start.
 *
 *      @get:Rule
 *      val rule = lazyActivityScenarioRule<MyActivity>(launchActivity = false)
 *
 *      @Test
 *      fun myTest() {
 *          // do some setup
 *          rule.launch()
 *          // do some stuff with the Activity
 *      }
 */
class LazyActivityScenarioRule<A : Activity> : ExternalResource {
    private var launchActivity: Boolean

    private var scenarioSupplier: () -> ActivityScenario<A>

    private var scenario: ActivityScenario<A>? = null

    private var scenarioLaunched: Boolean = false

    constructor(launchActivity: Boolean, startActivityIntentSupplier: () -> Intent) {
        this.launchActivity = launchActivity
        scenarioSupplier = { ActivityScenario.launch(startActivityIntentSupplier()) }
    }

    constructor(launchActivity: Boolean, startActivityIntent: Intent) {
        this.launchActivity = launchActivity
        scenarioSupplier = { ActivityScenario.launch(startActivityIntent) }
    }

    constructor(launchActivity: Boolean, startActivityClass: Class<A>) {
        this.launchActivity = launchActivity
        scenarioSupplier = { ActivityScenario.launch(startActivityClass) }
    }

    override fun before() {
        if (launchActivity) {
            launch()
        }
    }

    override fun after() {
        scenario?.close()
    }

    fun launch(newIntent: Intent? = null) {
        if (!scenarioLaunched) {
            newIntent?.let { scenarioSupplier = { ActivityScenario.launch(it) } }
            scenario = scenarioSupplier()
            scenarioLaunched = true
        }
    }

    fun getScenario(): ActivityScenario<A> = checkNotNull(scenario)
}

inline fun <reified A : Activity> lazyActivityScenarioRule(
    launchActivity: Boolean = true,
    noinline intentSupplier: () -> Intent
): LazyActivityScenarioRule<A> =
    LazyActivityScenarioRule(launchActivity, intentSupplier)

inline fun <reified A : Activity> lazyActivityScenarioRule(
    launchActivity: Boolean = true,
    intent: Intent? = null
): LazyActivityScenarioRule<A> = if (intent == null) {
    LazyActivityScenarioRule(launchActivity, A::class.java)
} else {
    LazyActivityScenarioRule(launchActivity, intent)
}
