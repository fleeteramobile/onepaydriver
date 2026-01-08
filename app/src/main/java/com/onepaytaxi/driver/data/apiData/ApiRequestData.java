package com.onepaytaxi.driver.data.apiData;

import com.onepaytaxi.driver.data.WayPointsData;
import com.onepaytaxi.driver.route.StopData;

import java.util.ArrayList;

/**
 * Created by developer on 1/11/16.
 */
public class ApiRequestData {
    public static class SettlementHistory {
        public String driver_id;
    }

    public static class PaymentReq {
        public String driver_id, comments;
        public SettlementReqData.Info info;
    }

    public static class SettlementReq {
        public String driver_id, start_date, end_date;
    }

    public static class UpcomingRequest {
        public void setId(String id) {
            this.driver_id = id;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public void setLimit(String limit) {
            this.limit = limit;
        }

        public void setDeviceType(String deviceType) {
            this.device_type = deviceType;
        }

        public void setRequestType(String requestType) {
            this.request_type = requestType;
        }


//        j.put("driver_id", SessionSave.getSession("Id", JobsAct.this));
//        j.put("start", "0");
//        j.put("limit", limit);
//        j.put("device_type", "1");
//        String type = "";
//        if (isShowingPendingTrip)
//        type = "1";
//        else
//        type = "2";
//        j.put("request_type", type);


        public String driver_id;
        public String start;
        public String limit;
        public String device_type;

        public void setType(String type) {
            this.type = type;
        }

        public void setDevice_type(String device_type) {
            this.device_type = device_type;
        }

        public void setDriver_id(String driver_id) {
            this.driver_id = driver_id;
        }

        public String type;
        public String request_type;
    }

    public static class Earnings {
        public String driver_id;

        public String getDriver_id() {
            return driver_id;
        }

        public void setDriver_id(String driver_id) {
            this.driver_id = driver_id;
        }

        @Override
        public String toString() {
            return "ClassPojo [driver_id = " + driver_id + "]";
        }
    }

    public static class PastBookingRequest {
        public String passenger_id;
        public String start;
        public String limit;
        public String month;
        public String year;
        public String device_type;

        public void setPassenger_id(String passenger_id) {
            this.passenger_id = passenger_id;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public void setLimit(String limit) {
            this.limit = limit;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public void setDevice_type(String device_type) {
            this.device_type = device_type;
        }
    }

    public static class getTripDetailRequest {
        public void setTrip_id(String trip_id) {
            this.trip_id = trip_id;
        }

        public String trip_id;
    }


    public static class HelpSubmit {
        public void setTrip_id(String trip_id) {
            this.trip_id = trip_id;
        }

        public void setHelp_id(String help_id) {
            this.help_id = help_id;
        }

        public void setHelp_comment(String help_comment) {
            this.help_comment = help_comment;
        }

        String trip_id, help_id, help_comment;
    }


    public static class StreetPickRequest {
        public String driver_id;
        public String taxi_id;
        public String pickup_location;
        public String pickup_latitude;
        public String pickup_longitude;
        public String drop_location;
        public String drop_latitude;
        public String drop_longitude;
        public String approx_distance;
        public String approx_fare;
        public String approx_duration;
        public String driver_app_version;
        public String cityname;
        public String motor_model;
        public String time_to_reach_passen;
        public ArrayList<StopData>  stops;
    }

    public static class EndStreetPickup {
        public String trip_id;
        public String drop_latitude;
        public String drop_longitude;
        public String drop_location;
        public String distance;
        public String actual_distance;
        public String waiting_hour;
        public double new_distance;
        public String distance_time;
        public String driver_app_version;
        public ArrayList<WayPointsData> waypoints;
        public ArrayList<StopData>  stops;
    }

    public static class TripDetailRequest {
        public String trip_id;
    }

    public static class StreetPickComplete {

        public String pay_mod_id;

        public String trip_fare;

        public String eveningfare;

        public String eveningfare_applicable;

        public String waiting_cost;

        public String fare;

        public String minutes_traveled;

        public String remarks;

        public String actual_amount;

        public String trip_id;

        public String distance;

        public String base_fare;

        public String company_tax;

        public String actual_distance;

        public String tax_amount;

        public String minutes_fare;

        public String nightfare;

        public String tips;

        public String nightfare_applicable;

        public String waiting_time;
    }

    public static class BaseUrl {
        public String company_main_domain, company_domain, device_type;
    }

}
