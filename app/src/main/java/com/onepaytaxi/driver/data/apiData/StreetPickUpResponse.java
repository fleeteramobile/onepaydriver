
package com.onepaytaxi.driver.data.apiData;


public class StreetPickUpResponse {

    public String message;
    public Integer status;
    public Detail detail;

    public class Detail {
        public String taxi_min_speed;
        public Integer driver_tripid;
        public String base_fare;
        public String min_fare;
        public String below_km;
        public String above_km;
        public String below_above_km, metric;
        public String minutes_fare;
        public String km_wise_fare;
        public String additional_fare_per_km;
        public String route_path;
    }
}