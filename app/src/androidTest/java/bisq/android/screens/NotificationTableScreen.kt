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

package bisq.android.screens

import bisq.android.R
import bisq.android.screens.dialogs.ChoicePromptDialog
import bisq.android.screens.elements.MenuItemElement
import bisq.android.screens.elements.RecyclerViewElement

class NotificationTableScreen : Screen() {
    val addExampleNotificationsMenuItem =
        MenuItemElement(applicationContext.resources.getString(R.string.button_add_example_notifications))
    val markAllAsReadMenuItem =
        MenuItemElement(applicationContext.resources.getString(R.string.button_mark_as_read))
    val deleteAllMenuItem =
        MenuItemElement(applicationContext.resources.getString(R.string.button_delete_notifications))
    val settingsMenuItem =
        MenuItemElement(applicationContext.resources.getString(R.string.settings))
    val notificationRecylerView = RecyclerViewElement(R.id.notification_table_recycler_view)
    val alertDialogDeleteAll = ChoicePromptDialog(
        applicationContext.resources.getString(R.string.delete_all_notifications_confirmation)
    )
}
