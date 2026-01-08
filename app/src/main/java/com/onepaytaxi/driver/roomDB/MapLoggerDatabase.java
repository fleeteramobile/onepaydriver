package com.onepaytaxi.driver.roomDB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {GoogleMapModel.class, MapboxModel.class, GeocoderModel.class}, version = 1,exportSchema = false)
public abstract class MapLoggerDatabase extends RoomDatabase {
    public abstract MapLoggerDao loggerDao();

    private static MapLoggerDatabase INSTANCE;


    static MapLoggerDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MapLoggerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MapLoggerDatabase.class, "maplogger_database")
//                            .allowMainThreadQueries()
                            .build();

                }
            }
        }
        return INSTANCE;
    }
}
