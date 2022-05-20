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

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

@Entity
open class BisqNotification {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @SerializedName("uid")
    var uid: Int = 0

    @ColumnInfo(name = "version")
    @SerializedName("version")
    var version: Int = 0

    @ColumnInfo(name = "type")
    @SerializedName("type")
    var type: String? = null

    @ColumnInfo(name = "title")
    @SerializedName("title")
    var title: String? = null

    @ColumnInfo(name = "message")
    @SerializedName("message")
    var message: String? = null

    @ColumnInfo(name = "actionRequired")
    @SerializedName("actionRequired")
    var actionRequired: String? = null

    @ColumnInfo(name = "txId")
    @SerializedName("txId")
    var txId: String? = null

    @ColumnInfo(name = "receivedDate")
    @SerializedName("receivedDate")
    var receivedDate: Long = 0

    @ColumnInfo(name = "sentDate")
    @SerializedName("sentDate")
    var sentDate: Long = 0

    @ColumnInfo(name = "read")
    @SerializedName("read")
    var read: Boolean = false

    override fun toString(): String {
        return "BisqNotification[" + Gson().toJson(this) + "]"
    }
}
