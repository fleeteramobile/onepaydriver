package com.onepaytaxi.driver.tracklocation

data class LocationData(
    var _id: String = "",
    var p_location_name: String = "",
    var d_location_name: String = "",
    var latitude: Double = 0.0,
    var longtitute: Double = 0.0,
    var p_latitude:Double = 0.0,
    var p_longtitute:Double = 0.0,
    var loction_type: String = "",
    var location_name: String = "",
    var label_name: String = "",
    var android_icon: String = "",
    var ios_icon: String = "",
    var type:String = "0"
)
