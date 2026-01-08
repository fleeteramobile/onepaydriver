package com.onepaytaxi.driver.roomDB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Created by developer on 30/5/18.
 */

@Entity(tableName = "geocode_logger")
public class GeocoderModel {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "lat_lng")
    public String latLng;

    public String result;
    public int type;
}