package com.joachimneumann.bisq.Database

// This class contains the data received from the notifiaction server
// It lacks the received timestamp for example

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull

import java.util.Date

enum class NotificationType {
    SETUP_CONFIRMATION, ERASE, // setup
    TRADE, DISPUTE, FINANCIAL, // from Bisq
    ERROR, PLACEHOLDER         // internal
}

@Entity
open class RawBisqNotification {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    var uid: Int = 0

    @ColumnInfo(name = "version")
    var version: Int = 0

    @ColumnInfo(name = "notificationType")
    var notificationType: String? = null

    @ColumnInfo(name = "title")
    var title: String? = null

    @ColumnInfo(name = "message")
    var message: String? = null

    @ColumnInfo(name = "actionRequired")
    var actionRequired: String? = null

    @ColumnInfo(name = "transactionID")
    var transactionID: String? = null

    @ColumnInfo(name = "timestampEvent")
    var timestampEvent: Date? = null
}
