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

package bisq.android.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class BisqNotification(
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0,

    @ColumnInfo(name = "version")
    var version: Int = 0,

    @ColumnInfo(name = "type")
    var type: String? = null,

    @ColumnInfo(name = "title")
    var title: String? = null,

    @ColumnInfo(name = "message")
    var message: String? = null,

    @ColumnInfo(name = "actionRequired")
    var actionRequired: String? = null,

    @ColumnInfo(name = "txId")
    var txId: String? = null,

    @ColumnInfo(name = "receivedDate")
    var receivedDate: Long = 0,

    @ColumnInfo(name = "sentDate")
    var sentDate: Long = 0,

    @ColumnInfo(name = "read")
    var read: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BisqNotification) return false
        return version == other.version &&
            type == other.type &&
            title == other.title &&
            message == other.message &&
            actionRequired == other.actionRequired &&
            txId == other.txId &&
            sentDate == other.sentDate
    }

    override fun hashCode(): Int = listOf(version, type, title, message, actionRequired, txId, sentDate)
        .hashCode()
}
