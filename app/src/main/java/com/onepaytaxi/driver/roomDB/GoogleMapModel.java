package com.onepaytaxi.driver.roomDB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "google_maplogger")
public class GoogleMapModel {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "from_to")
    public String fromTo;

    public double time;
    public double distance;
    public String routeResult;
    public String distanceResult;
}
