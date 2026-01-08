package com.onepaytaxi.driver.errorLog

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.onepaytaxi.driver.data.ModelDriverInfo
import org.json.JSONObject

class Converter {
    @TypeConverter
    fun driverInfotoString(driverInfo: ModelDriverInfo): String {
        return Gson().toJson(driverInfo)
    }

    @TypeConverter
    fun stringToDriverInfo(value: String): ModelDriverInfo {
        val type = object : TypeToken<ModelDriverInfo>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun latLngToString(latLng: LatLng): String {
        return Gson().toJson(latLng)
    }

    @TypeConverter
    fun stringToLatLng(latLng: String): LatLng {
        val type = object : TypeToken<LatLng>() {}.type
        return Gson().fromJson(latLng, type)
    }

    @TypeConverter
    fun inputParamsToJson(inputData: String): JSONObject {
        return JSONObject(inputData)
    }


    @TypeConverter
    fun inputParamsToString(inputData: JSONObject): String {
        return inputData.toString()
    }

}