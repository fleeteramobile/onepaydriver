package com.onepaytaxi.driver.data.apiData;

/**
 * Created by developer on 16/11/16.
 */
public class StreetCompleteResponse {


    public String message;
    public Detail detail;
    public String status;


    public class Detail {


        public String fare;
        public String pickup;
        public String trip_id;

    }

}
