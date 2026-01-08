package com.onepaytaxi.driver.roomDB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "maplogger")
public class MapLoggerModel {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "from_to")
    public String fromTo;
    public double time;
    public double distance;
    public boolean routeApi;
    public boolean distanceMatrix;
}
