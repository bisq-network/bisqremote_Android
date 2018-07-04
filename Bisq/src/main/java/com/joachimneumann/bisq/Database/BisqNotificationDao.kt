package com.joachimneumann.bisq.Database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface BisqNotificationDao {
    @get:Query("SELECT * FROM BisqNotification")
    val all: LiveData<List<BisqNotification>>

    @Insert
    fun insert(bisqNotification: BisqNotification)

    @Insert
    fun insertAll(vararg bisqNotification: BisqNotification)

    @Delete
    fun delete(bisqNotification: BisqNotification)

    @Query("DELETE FROM BisqNotification")
    fun nukeTableBisqNotification()
}
