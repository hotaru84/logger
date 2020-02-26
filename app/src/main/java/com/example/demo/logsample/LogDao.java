package com.example.demo.logsample;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Log log);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Stats log);

    @Query("DELETE FROM log_table")
    void deleteAll();

    @Query("SELECT * from log_table ORDER BY time DESC")
    LiveData<List<Log>> getLogs();

    @Query("SELECT * from stats_table ORDER BY date DESC")
    LiveData<List<Stats>> getStats();

    @Query("SELECT * from log_table ORDER BY time DESC")
    List<Log> getLogList();

    @Query("SELECT * from stats_table WHERE date=:date AND hour=:hour AND type=:type")
    Stats queryStats(String date, int hour, String type);

    @Query("SELECT COUNT(*) from log_table")
    long getLogCount();

    @Delete
    void delete(Log log);
}
