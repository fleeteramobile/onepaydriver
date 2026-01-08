package com.onepaytaxi.driver.toll

data class ResponseTollList(
    val message: String,
    val status: Int,
    val trip_addon_fare: List<TripAddonFare>
)