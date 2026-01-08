package com.onepaytaxi.driver.interfaces

import com.onepaytaxi.driver.data.apiData.UpcomingResponse

interface UpcomingAdapterInterface {
    fun updateUpcomingAdapter(data : List<UpcomingResponse.PastBooking>, clickedPosition : Int)
}