package com.joachimneumann.bisq.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BisqNotificationDao {
    @get:Query("SELECT * FROM BisqNotification ORDER BY sentDate DESC")
    val all: LiveData<List<BisqNotification>>

    @Query("SELECT * FROM BisqNotification WHERE uid=:uid")
    suspend fun getFromUid(uid: Int): BisqNotification

    @Insert
    suspend fun insert(bisqNotification: BisqNotification)

    @Insert
    suspend fun insertAll(vararg bisqNotification: BisqNotification)

    @Delete
    suspend fun delete(bisqNotification: BisqNotification)

    @Query("DELETE FROM BisqNotification")
    suspend fun deleteAll()

    @Query("UPDATE BisqNotification SET read=:readValue")
    suspend fun markAllAsRead(readValue: Boolean)

    @Query("UPDATE BisqNotification SET read=:readValue WHERE uid=:uid")
    suspend fun markAsRead(uid: Int, readValue: Boolean)
}
