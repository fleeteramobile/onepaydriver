package com.onepaytaxi.driver.roomDB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MapLoggerDao {
    //Methods for google logger table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGoogleLog(GoogleMapModel... models);

    @Query("SELECT routeResult FROM google_maplogger WHERE from_to =:key")
    LiveData<String> getRouteResult(String key);

    @Query("SELECT distance FROM google_maplogger WHERE from_to =:key")
    LiveData<Double> getDistanceAndTime(String key);

    @Query("SELECT distanceResult FROM google_maplogger WHERE from_to =:key")
    LiveData<String> getDistanceResult(String key);

    @Query("SELECT * From google_maplogger where from_to =:fromTo")
    LiveData<List<GoogleMapModel>> loadAll(String fromTo);

    @Query("SELECT * FROM google_maplogger WHERE from_to =:key")
    GoogleMapModel getGoogleModel(String key);

    //Methods for MapBox logger table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMapboxLog(MapboxModel... models);

    @Query("SELECT * FROM mapbox_maplogger WHERE from_to =:key")
    MapboxModel getMapboxModel(String key);

    //Methods for Geocode logger table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGeocodeLog(GeocoderModel... models);

    @Query("SELECT * FROM geocode_logger WHERE lat_lng =:key AND type =:type")
    GeocoderModel getGeocodeModel(String key, int type);

}