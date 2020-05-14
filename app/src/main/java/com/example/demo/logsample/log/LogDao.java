package com.example.demo.logsample.log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.demo.logsample.log.Log;
import com.example.demo.logsample.log.Stats;

import java.util.List;

@Dao
public interface LogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Log log);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Stats log);

    @Query("DELETE FROM log_table")
    void deleteAllLog();

    @Query("DELETE FROM stats_table")
    void deleteAllStats();

    @Query("SELECT * from log_table WHERE time >= :start AND time < :end ORDER BY time DESC")
    List<Log> queryLog(long start, long end);

    @Query("SELECT * from stats_table WHERE time >= :start AND time < :end  AND type=:type  ORDER BY time DESC")
    LiveData<Stats> queryStats(long start, long end, String type);
    @Query("SELECT value from stats_table WHERE time >= :start AND time < :end  AND type=:type LIMIT 1")
    Long queryStatsValue(long start, long end, String type);

    @Query("SELECT COUNT(*) from log_table")
    long getLogCount();

    @Query("SELECT COUNT(*) from stats_table")
    long getStatsCount();
}
