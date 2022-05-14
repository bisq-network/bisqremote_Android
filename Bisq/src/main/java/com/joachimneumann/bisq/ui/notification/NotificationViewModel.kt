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

    fun getFromUid(uid: Int): BisqNotification? {
        return repository.getFromUid(uid)
    }

    fun markAllAsRead() = viewModelScope.launch {
        repository.markAllAsRead()
    }

    fun markAsRead(uid: Int) = viewModelScope.launch {
        repository.markAsRead(uid)
    }
}
