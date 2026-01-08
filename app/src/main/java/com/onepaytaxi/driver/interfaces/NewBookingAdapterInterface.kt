package com.onepaytaxi.driver.interfaces

import com.onepaytaxi.driver.data.apiData.UpcomingResponse

interface NewBookingAdapterInterface {
    fun newbookingUpcomingAdapter(data : List<UpcomingResponse.ShowBooking>, clickedPosition : Int)
}