package com.joachimneumann.bisq.database

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
