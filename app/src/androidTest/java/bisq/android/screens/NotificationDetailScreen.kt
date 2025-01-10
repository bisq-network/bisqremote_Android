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
import bisq.android.screens.elements.ButtonElement
import bisq.android.screens.elements.TextElement

class NotificationDetailScreen : Screen() {
    val title = TextElement(R.id.notification_detail_title)
    val message = TextElement(R.id.notification_detail_message)
    val action = TextElement(R.id.notification_detail_action)
    val eventTime = TextElement(R.id.notification_detail_event_time)
    val receivedTime = TextElement(R.id.notification_detail_received_time)
    val deleteButton = ButtonElement(R.id.notification_delete_button)
}
