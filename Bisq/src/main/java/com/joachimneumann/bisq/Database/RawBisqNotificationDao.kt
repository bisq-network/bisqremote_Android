package com.joachimneumann.bisq.Database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface RawBisqNotificationDao {
    @get:Query("SELECT * FROM rawBisqNotification")
    val all: LiveData<List<RawBisqNotification>>

    @Insert
    fun insert(rawBisqNotification: RawBisqNotification)

    @Insert
    fun insertAll(vararg rawBisqNotification: RawBisqNotification)

    @Delete
    fun delete(rawBisqNotification: RawBisqNotification)

    @Query("DELETE FROM rawBisqNotification")
    fun nukeTableRawBisqNotification()
}
