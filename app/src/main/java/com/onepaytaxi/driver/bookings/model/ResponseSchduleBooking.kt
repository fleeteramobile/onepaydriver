package com.bluetaxi.driver.triplist.model

data class ResponseSchduleBooking(
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
            val _id: Id,
            val approx_fare: Double,
            val away: String,
            val bookby: Int,
            val createtime: String,
            val distance: Int,
            val drop_latitude: String,
            val drop_location: String,
            val drop_longitude: String,
            val map_image: String,
            val notes: String,
            val os_trip_type: String,
            val passengers_log_id: String,
            val pay_mod_id: String,
            val payment_type: String,
            val pickup_latitude: String,
            val pickup_location: String,
            val pickup_longitude: String,
            val pickup_time: String,
            val pickup_time_text: String,
            val profile_image: String,
            val rejected_drivers: List<Int>,
            val schedule_status: Int,
            val schedule_time: String,
            val time: String,
            val trip_type: String
        ) {
            data class Id(
                val `$oid`: String
            )
        }
    }
}