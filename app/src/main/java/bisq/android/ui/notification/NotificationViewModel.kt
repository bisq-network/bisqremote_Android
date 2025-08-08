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

package bisq.android.ui.notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import bisq.android.database.BisqNotification
import bisq.android.database.NotificationRepository
import kotlinx.coroutines.launch

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NotificationRepository = NotificationRepository(application)

    var bisqNotifications: LiveData<List<BisqNotification>> = repository.allBisqNotifications

    fun insert(bisqNotification: BisqNotification) = viewModelScope.launch {
        repository.insert(bisqNotification)
    }

    fun delete(bisqNotification: BisqNotification) = viewModelScope.launch {
        repository.delete(bisqNotification)
    }

    fun nukeTable() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun getFromUid(uid: Int): BisqNotification? = repository.getFromUid(uid)

    fun markAllAsRead() = viewModelScope.launch {
        repository.markAllAsRead()
    }

    fun markAsRead(uid: Int) = viewModelScope.launch {
        repository.markAsRead(uid)
    }
}
