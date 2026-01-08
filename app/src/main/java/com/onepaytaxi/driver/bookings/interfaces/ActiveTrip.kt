package com.onepaytaxi.driver.bookings.interfaces


import com.onepaytaxi.driver.bookings.model.ResponseUpcomingTrips


interface ActiveTrip {

    fun trackTrip(_category: ResponseUpcomingTrips.Detail.PendingBooking)
    fun callCustomer(_category: ResponseUpcomingTrips.Detail.PendingBooking)


}