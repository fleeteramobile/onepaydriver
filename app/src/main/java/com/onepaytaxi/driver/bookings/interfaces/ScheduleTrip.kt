package com.onepaytaxi.driver.bookings.interfaces


import com.onepaytaxi.driver.bookings.model.ResponseNewSchduleBooking


interface ScheduleTrip {

    fun acceptScheduleTrip(_category: ResponseNewSchduleBooking.Detail.ShowBooking)


}