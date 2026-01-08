package com.onepaytaxi.driver.bookings.interfaces


import com.onepaytaxi.driver.bookings.model.ResponseUpcomingTrips


interface UpcomeingTrip {

    fun startTrip(_category: ResponseUpcomingTrips.Detail.PendingBooking)
    fun decline(_category: ResponseUpcomingTrips.Detail.PendingBooking)
    fun callCustomer(_category: ResponseUpcomingTrips.Detail.PendingBooking)


}