package com.onepaytaxi.driver.bookings.model

data class ResponsePastBooking(
    val detail: Detail,
    val message: String,
    val status: Int
) {
    data class Detail(
        val past_booking: List<PastBooking>,
        val pending_booking: List<Any>,
        val show_booking: Any
    ) {
        data class PastBooking(
            val _id: Int,
            val actual_paid_amt: Double,
            val actual_pickup_time: String,
            val amt: String,
            val bookby: Int,
            val corporate_booking: Int,
            val corporate_company_id: Int,
            val distance: String,
            val distance_fare: Int,
            val distance_fare_km: String,
            val drop_latitude: Double,
            val drop_location: String,
            val drop_longitude: Double,
            val drop_time: String,
            val drop_time_text: String,
            val dynamic_fare: Int,
            val fare_type: Int,
            val final_amt: Int,
            val map_image: String,
            val metric: String,
            val model_fare_stage1_fare: Int,
            val model_fare_stage1_from: Int,
            val model_fare_stage1_to: Int,
            val model_fare_stage2_fare: Int,
            val model_fare_stage2_from: Int,
            val model_fare_stage2_to: Int,
            val model_fare_stage3_fare: Int,
            val model_fare_stage3_from: Int,
            val model_fare_stage3_to: Int,
            val passenger_name: String,
            val passengers_log_id: Int,
            val pay_mod_id: Int,
            val payment_type: String,
            val pickup_latitude: Double,
            val pickup_location: String,
            val pickup_longitude: Double,
            val pickup_time: String,
            val pickup_time_text: String,
            val profile_image: String,
            val route_path: String,
            val started_time: String,
            val total: Double,
            val trans_amt: Double,
            val travel_status: Int,
            val trip_duration: String,
            val trip_type: String,
            val twaiting_hour: Int,
            val used_wallet_amount: Int,
            val vehicle_distance_fare: String,
            val waiting_fare: Int,
            val waiting_fare_per_hour: Int
        )
    }
}