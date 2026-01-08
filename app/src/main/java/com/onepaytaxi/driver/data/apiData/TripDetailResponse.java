package com.onepaytaxi.driver.data.apiData;

import com.onepaytaxi.driver.route.StopData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developer on 2/11/16.
 */
public class TripDetailResponse{
    public String message;
    public Detail detail;
    public Integer status;
    public String site_currency;

    public class Detail{
        //outstation
        public String trip_type;
        public String base_fare, paid_amount, promotion, tax;
        public String additional_time_fare;
        public String additional_distance_fare;

        public int min_distance_status;
        public String new_base_fare;
        public String os_driver_beta;
        public Double   os_addtinal_km ;;
        public String new_distance_fare;

        public String taxi_min_speed;
        public String trip_id;
        public String current_location;
        public String booking_fare;
        public String service_fare;
        public String toll_amount;
        public String parking_amount;
        public String trip_duration;
        public String pickup_latitude;
        public String pickup_longitude;
        public String drop_location;
        public String pending_cancel_amount;
        public String drop_latitude;
        public String drop_longitude;
        public String drop_time;
        public String pickup_date_time;
        public String pickup_time;
        public String booking_time;
        public String time_to_reach_passen;
        public String no_passengers;
        public String rating;
        public String notes;
        public String driver_name;
        public String driver_id;
        public String taxi_id;
        public String taxi_number;
        public String driver_phone;
        public String passenger_phone;
        public String passenger_name;
        public String travel_status;
        public String bookedby;
        public String waiting_time;
        public String waiting_fare;
        public String distance;
        public String actual_distance;
        public String metric;
        public String amt;
        public String used_wallet_amount;
        public String job_ref;
        public String payment_type;
        public String taxi_speed;
        public Integer is_split_fare;
        public String credit_card_status;
        public Boolean is_primary;

        public String driver_image, subtotal, tax_fare;
        public String passenger_image;
        public String driver_longtitute;
        public String driver_latitute;
        public String driver_status, nightfare, eveningfare;
        public String driver_rating, distance_fare, distance_fare_metric, tax_percentage, trip_minutes, fare_per_minute, minutes_fare, waiting_fare_minutes, actual_paid_amount, promocode_fare, payment_type_label, map_image, fare_calculation_type,comments;
        public List<String> split_fare_details = new ArrayList<>();
        public ArrayList<StopData> stops;
        public String cancellation_fee;
        public String corporate_booking;
        public String approx_fare;
        public String total_travelled_distance;
        public String incentive_amount;
        public String is_on_my_way_trip;
        public String roundtrip;
        public String pickup_time_text;
        public String drop_time_text;

    }
}