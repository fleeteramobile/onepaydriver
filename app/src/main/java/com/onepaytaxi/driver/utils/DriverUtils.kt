package com.onepaytaxi.driver.utils

import android.content.Context
import com.onepaytaxi.driver.data.CommonData
import com.onepaytaxi.driver.data.ModelDriverInfo
import com.onepaytaxi.driver.service.LocationUpdate.*



object DriverUtils {
    fun driverInfo(context: Context): ModelDriverInfo {
        val driverLastLocation = "$currentLatitude,${currentLongtitude}"
        val driverLocationAccuracy = "$currentAccuracy"
        return ModelDriverInfo(SessionSave.getSession("Id", context),
                SessionSave.getSession("trip_id", context), "$driverLastLocation,$driverLocationAccuracy", SessionSave.getSession("shift_status", context), SessionSave.getSession("travel_status", context),
                SessionSave.getSession("service_status", context, false),
                SessionSave.getSession(CommonData.DRIVER_LOCATION_STATIC, context).replace("null", ""))
    }
}