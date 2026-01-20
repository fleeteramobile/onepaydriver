package com.onepaytaxi.driver.bookings.model

data class ResponseUpcomingTrips(
    val detail: Detail,
    val message: String,
    val status: Int
) {
    data class Detail(
        val past_booking: List<Any>,
        val pending_booking: List<PendingBooking>,
        val show_booking: Any
    ) {
        data class PendingBooking(
            val _id: String,
            val approx_fare: Double,
            val away: String,
            val bookby: String,
            val driver_loc: DriverLoc,
            val driver_name: String,
            val drivername: String,
            val drop_latitude: String,
            val drop_location: String,
            val os_trip_type: String,
            val drop_longitude: String,
            val trip_approval: String,
            val dynamic_fare: String,
            val fare_type: String,
            val cancellation_fare: String,
            val map_image: String,
            val model_fare_stage1_fare: String,
            val model_fare_stage1_from: String,
            val model_fare_stage1_to: String,
            val model_fare_stage2_fare: String,
            val model_fare_stage2_from: String,
            val model_fare_stage2_to: String,
            val model_fare_stage3_fare: String,
            val model_fare_stage3_from: String,
            val model_fare_stage3_to: String,
            val notes: String,
            val passenger_country_code: String,
            val passenger_name: String,
            val passenger_phone: String,
            val passenger_profile_image: String,
            val passengers_log_id: String,
            val pay_mod_id: String,
            val payment_type: String,
            val pickup_latitude: String,
            val pickup_location: String,
            val pickup_longitude: String,
            val pickup_time: String,
            val pickup_time_text: String,
            val profile_image: String,
            val route_path: String,
            val schedule: String,
            val time: String,
            val travel_status: Int,
            val trip_location: TripLocation,
            val trip_type: String
        ) {
            data class DriverLoc(
                val coordinates: List<Double>,
                val type: String
            )

            data class TripLocation(
                val coordinates: List<Double>,
                val type: String
            )
        }
    }
}