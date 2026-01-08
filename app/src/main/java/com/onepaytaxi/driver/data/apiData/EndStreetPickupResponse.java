package com.onepaytaxi.driver.data.apiData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developer on 14/11/16.
 */
public class EndStreetPickupResponse {

    public String message;
    public Detail detail;
    public String status;

    public class GatewayDetail {

        public String pay_mod_id;
        public String pay_mod_name;
        public String pay_mod_default;

    }

    public class Detail {

        public String trip_id;
        public String distance;
        public String trip_fare;
        public String amt,nightfare_applicable, distance_fare, new_base_fare, new_distance_fare, passenger_name, passenger_image, waiting_fare, distance_fare_metric,subtotal,promocode_fare,tax_fare;
        public String nightfare;
        public String eveningfare_applicable;
        public String eveningfare;
        public String waiting_time;
        public String waiting_cost;
        public String fare_per_minute, waiting_fare_minutes, trip_minutes;
        public int min_distance_status;
        public String tax_amount;
        public String subtotal_fare;
        public String total_fare;
        public List<GatewayDetail> gateway_details = new ArrayList<>();
        public String pickup;
        public String drop;
        public String company_tax;
        public String waiting_per_hour;
        public String roundtrip;
        public String minutes_traveled;
        public String minutes_fare;
        public String metric;
        public String promo_type;
        public String base_fare;
        public String wallet_amount_used;
        public String promo_discount_per;
        public String pass_id;
        public String referdiscount;
        public String promodiscount_amount;
        public String passenger_discount;
        public String cancellation_fee;
        public String street_pickup, credit_card_status, fare_calculation_type;


        public String os_distance_unit;
        public String os_duration;
        public String os_additional_fare_per_hour;
        public String os_additional_fare_per_distance;
        public String os_plan_duration;
        public String os_plan_distance;
        public String os_plan_fare;
        public String trip_type;
        public String trip_start_time = "";
        public String trip_end_time = "";

    }


}
