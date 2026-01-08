package com.onepaytaxi.driver.tracklocation

data class NearestDriverDatas(
    var detail: List<Detail>,
    var fav_drivers: Int = 0,
    var fav_driver_message: String = "",
    var fare_details: FareDetails,
    var driver_around_miles: String = "",
    var status: Int = 0,
    var message: String = "",
    var metric: String = "",
    var favourite_places: List<LocationData>,
    var popular_places: List<LocationData>,
    var past_booking_places: List<LocationData>,
    var zone_fare_applicable: String,
    var zone_zone_fare: Double)

data class Detail(
    var driver_id: Int = 0,
    var distance_km: String = "",
    var travel_modelid: Int = 0,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var nearest_driver: String = "",
    var driver_coordinates: String = ""
)


data class FareDetails(
    var _id: Int = 0,
    var base_fare: Double = 0.0,
    var min_fare: Double = 0.0,
    var below_km: Double = 0.0,
    var above_km: Double = 0.0,
    var minutes_fare: Double = 0.0,
    var night_timing_from: String = "",
    var night_timing_to: String = "",
    var night_fare: Double = 0.0,
    var evening_fare: Double = 0.0,
    var min_km: Double = 0.0,
    var model_name: String = "",
    var model_size: Int = 0,
    var below_above_km: String = "",
    var model_id: Int = 0,
    var km_wise_fare: String = "",
    var additional_fare_per_km: Double = 0.0,
    var nightfare_applicable: Int = 0,
    var eveningfare_applicable: Int = 0,
    var metric: String = "",
    var fare_calculation_type: String = "",

    var model_fare_stage1_from: Double = 0.0,
    var model_fare_stage1_to: Double = 0.0,
    var model_fare_stage2_from: Double = 0.0,
    var model_fare_stage2_to: Double = 0.0,
    var model_fare_stage3_from: Double = 0.0,
    var model_fare_stage3_to: Double = 0.0,
    var model_fare_stage1_fare: Int = 0,
    var model_fare_stage2_fare: Int = 0,
    var model_fare_stage3_fare: Int = 0

)
