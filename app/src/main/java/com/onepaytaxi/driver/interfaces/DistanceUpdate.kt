package com.onepaytaxi.driver.interfaces

interface DistanceUpdate {
    fun onDistanceUpdate(distance: Double?, s: String)
}