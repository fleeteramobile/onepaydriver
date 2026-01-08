package com.onepaytaxi.driver.bookings.model

data class ResponseNewSchduleBooking(
    val auth_key: String,
    val detail: Detail,
    val message: String,
    val status: Int
) {
    data class Detail(
        val past_booking: List<Any>,
        val pending_booking: List<Any>,
        val show_booking: List<ShowBooking>
    ) {
        data class ShowBooking(
            val _id: Int,
            val approx_fare: String,
            val away: String,
            val bookby: Int,
            val distance: String,
            val drop_latitude: String,
            val drop_location: String,
            val drop_longitude: String,
            val map_image: String,
            val notes: String,
            val passengers_log_id: String,
            val pay_mod_id: String,
            val payment_type: String,
            val pickup_latitude: String,
            val pickup_location: String,
            val pickup_longitude: String,
            val pickup_time: String,
            val pickup_time_text: String,
            val profile_image: String,
            val schedule:  Any?,
            val schedule_driver_id: String,
            val schedule_driver_name: String,
            val schedule_driver_phone: String,
            val schedule_rejected_drivers:  Any?,
            val time: String,
            val trip_type: String
        )
    }
}