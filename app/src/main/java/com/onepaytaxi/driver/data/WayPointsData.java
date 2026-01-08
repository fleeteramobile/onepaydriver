package com.onepaytaxi.driver.data;

/**
 * Created by developer on 13/2/18.
 */

public class WayPointsData {
    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    private String latlng, type, error;

    public double getPickuplat() {
        return pickuplat;
    }

    public void setPickuplat(double pickuplat) {
        this.pickuplat = pickuplat;
    }

    public double getPickuplng() {
        return pickuplng;
    }

    public void setPickuplng(double pickuplng) {
        this.pickuplng = pickuplng;
    }

    public double getDroplat() {
        return droplat;
    }

    public void setDroplat(double droplat) {
        this.droplat = droplat;
    }

    public double getDroplng() {
        return droplng;
    }

    public void setDroplng(double droplng) {
        this.droplng = droplng;
    }

    private double pickuplat, pickuplng,
            droplat, droplng;
    private double dist = 0.0;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    private String time, trip_id;
}
