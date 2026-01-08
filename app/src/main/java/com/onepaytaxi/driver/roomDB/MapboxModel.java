package com.onepaytaxi.driver.roomDB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Created by developer on 29/5/18.
 */

@Entity(tableName = "mapbox_maplogger")
public class MapboxModel {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "from_to")
    public String fromTo;

    public double time;
    public double distance;
    public String routeResult;
    public String distanceResult;
}
