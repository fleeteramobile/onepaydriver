package com.onepaytaxi.driver.utils

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType


import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ElevationHelper {

    private val client = OkHttpClient()

    // Callback interface to handle results
    interface ElevationCallback {
        fun onResult(isHillStation: Boolean, message: String)
        fun onFailure(error: String)
    }

    // Method to check if the location is a hill station
    fun isHillStation(lat: Double, lon: Double, callback: ElevationCallback) {
        val url = "https://api.open-elevation.com/api/v1/lookup"
        val jsonBody = """{"locations": [{"latitude": $lat, "longitude": $lon}]}"""

        val body = RequestBody.create("application/json".toMediaType(), jsonBody)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback.onFailure("Network Request Failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        try {
                            val json = JSONObject(responseBody)
                            val results: JSONArray = json.getJSONArray("results")
                            val elevation = results.getJSONObject(0).getDouble("elevation")

                            // Define the threshold for a hill station
                            val isHillStation = elevation >= 1000
                            val message = if (isHillStation) {
                                " $elevation"
                            } else {
                                "$elevation"
                            }

                            callback.onResult(isHillStation, message)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            callback.onFailure("Error Parsing JSON: ${e.message}")
                        }
                    } else {
                        callback.onFailure("Response Body is Null")
                    }
                } else {
                    callback.onFailure("Response Failed: ${response.code}")
                }
            }
        })
    }
}
