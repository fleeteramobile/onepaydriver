package com.onepaytaxi.driver.interfaces;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by developer on 24/5/18.
 */

public interface DistanceMatrixInterface {
    void onDistanceCalled(LatLng pick, LatLng drop, double distance, double time, String result, String status);
}
