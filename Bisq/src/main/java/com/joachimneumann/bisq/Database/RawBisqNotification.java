package com.joachimneumann.bisq.Database;

// This class contains the data received from the notifiaction server
// It lacks the received timestamp for example

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity
public class RawBisqNotification {
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getActionRequired() {
        return actionRequired;
    }

    public void setActionRequired(String actionRequired) {
        this.actionRequired = actionRequired;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public Date getTimestampEvent() {
        return timestampEvent;
    }

    public void setTimestampEvent(Date timestampEvent) {
        this.timestampEvent = timestampEvent;
    }

    @PrimaryKey
    @NonNull
    private int uid;

    @ColumnInfo(name = "version")
    private int version;

    @ColumnInfo(name = "notificationType")
    private String notificationType;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "message")
    private String message;

    @ColumnInfo(name = "actionRequired")
    private String actionRequired;

    @ColumnInfo(name = "transactionID")
    private String transactionID;

    @ColumnInfo(name = "timestampEvent")
    private Date timestampEvent;
}
