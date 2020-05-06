package com.example.demo.logsample.log;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Log.class, Stats.class}, version = 1, exportSchema = false)
public abstract class LogRoomDb extends RoomDatabase {
    public abstract LogDao logDao();
    private static volatile LogRoomDb INSTANCE;
    private static final int NUMBER_OF_THREADS =4;
    static final ExecutorService dbWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static LogRoomDb getDb(final Context context) {
        if(INSTANCE ==null) {
            synchronized (LogRoomDb.class){
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            LogRoomDb.class,
                            "log_database").build();
                }
            }
        }
        return INSTANCE;
    }
}
