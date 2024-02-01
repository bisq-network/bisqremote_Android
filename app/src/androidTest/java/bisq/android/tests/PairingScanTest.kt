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

package bisq.android.tests

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import bisq.android.ui.pairing.PairingScanActivity
import bisq.android.ui.pairing.PairingSendActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PairingScanTest : BaseTest() {
    @Test
    fun clickNoWebcamButtonLoadsPairingSendActivity() {
        ActivityScenario.launch(PairingScanActivity::class.java).use {
            pairingScanScreen.noWebcamButton.click()
            intended(hasComponent(PairingSendActivity::class.java.name))
        }
    }
}
