package com.joachimneumann.bisq.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RawBisqNotificationDao {
    @Query("SELECT * FROM rawBisqNotification")
    LiveData<List<RawBisqNotification>> getAll();

    @Insert
    void insert(RawBisqNotification rawBisqNotification);

    @Insert
    void insertAll(RawBisqNotification... rawBisqNotification);

    @Delete
    void delete(RawBisqNotification rawBisqNotification);
}
