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

package bisq.android.screens.dialogs

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import bisq.android.R
import bisq.android.screens.elements.ButtonElement
import bisq.android.screens.elements.SelectionElement

class ThemePromptDialog : PromptDialog(message) {
    val darkThemeSelection = SelectionElement(applicationContext.getString(R.string.dark_theme))
    val lightThemeSelection = SelectionElement(applicationContext.getString(R.string.light_theme))
    val systemThemeSelection = SelectionElement(applicationContext.getString(R.string.system_theme))
    val cancelButton = ButtonElement(android.R.id.button2)

    companion object {
        private val applicationContext: Context = ApplicationProvider.getApplicationContext()
        val message: String = applicationContext.getString(R.string.theme)
    }
}
