package com.onepaytaxi.driver.bookings.interfaces


import com.onepaytaxi.driver.bookings.model.ResponsePastBooking


interface CompletedTrip {

    fun showTripDetails(_category: ResponsePastBooking.Detail.PastBooking)


}