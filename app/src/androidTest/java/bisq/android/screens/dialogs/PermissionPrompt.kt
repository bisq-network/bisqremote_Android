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

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector

class PermissionPrompt {
    private val device
        get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    private val textElement
        get() = device.findObject(UiSelector().index(1))
    private val grantPermissionButton
        get() = device.findObject(UiSelector().textMatches("(?:Allow|ALLOW)"))
    private val denyPermissionButton
        get() = device.findObject(UiSelector().textMatches("(?:Don’t allow|DON’T ALLOW)"))

    fun isDisplayed(): Boolean = grantPermissionButton.exists() && denyPermissionButton.exists()

    fun text(): String {
        if (!textElement.exists()) {
            throw IllegalStateException("Text element does not exist")
        }
        return textElement.text
    }

    fun grantPermission() {
        if (!grantPermissionButton.exists()) {
            throw IllegalStateException("Grant permissions button does not exist")
        }
        grantPermissionButton.click()
    }

    fun denyPermission() {
        if (!grantPermissionButton.exists()) {
            throw IllegalStateException("Deny permissions button does not exist")
        }
        denyPermissionButton.click()
    }
}
