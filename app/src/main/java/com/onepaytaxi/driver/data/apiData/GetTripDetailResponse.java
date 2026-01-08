package com.onepaytaxi.driver.data.apiData;

/**
 * Created by developer on 14/11/16.
 */
public class GetTripDetailResponse {
    public String message;

    public Detail detail;

    public String site_currency;

    public String status;

    public class Detail {
        public String map_image;

        public String driver_image;

        public String driver_phone;

        public String drop_location;

        public String manual_waiting_time;
        public String used_wallet_amount;

        public String bookedby;

        public String time_to_reach_passen;

        public String distance_fare_metric;

        public String passenger_image;

        public String booking_time;

        public String driver_rating;

        public String route_path;

        public String taxi_id;

        public String driver_status;

        public String passenger_name;

        public String driver_id;

        public String pickup_time;

        public String payment_type_label;

        public String tax_fare;

        public String eveningfare;

        public String pickup_date_time;

        public String current_location;

        public String subtotal;

        public String travel_status;

        public String amt;

        public String driver_latitute;

        public String waiting_fare_minutes;

        public String passenger_phone;

        public String metric;

        public String actual_distance;

        public String fare_per_minute;

        public String[] splitFareDetails;

        public String is_primary;

        public String actual_paid_amount;

        public String driver_name;

        public String driver_longtitute;

        public String distance;

        public String taxi_speed;

        public String nightfare;

        public String drop_longitude;

        public String taxi_min_speed;

        public String pickup_latitude;

        public String taxi_number;

        public String drop_latitude;

        public String credit_card_status;

        public String drop_time;

        public String pickup_longitude;

        public String trip_duration;

        public String trip_minutes;

        public String trip_id;

        public String distance_fare;

        public String no_passengers;

        public String waiting_fare;

        public String isSplit_fare;

        public String promocode_fare;

        public String minutes_fare;

        public String rating;

        public String notes;

        public String waiting_time;

        public String tax_percentage;

        public String street_pickup_trip;

        public String payment_type;

        public String job_ref;

    }


    @Override
    public String toString() {
        return "ClassPojo [message = " + message + ", detail = " + detail + ", site_currency = " + site_currency + ", status = " + status + "]";
    }
}
