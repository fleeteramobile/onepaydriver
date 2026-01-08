package com.bluetaxi.driver.tripnotification


import DirectionsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApiService {
    @GET("maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String = "driving", // Default to driving mode
        @Query("key") apiKey: String // Your Google Maps API Key
    ): Response<DirectionsResponse> // The response data class
}
