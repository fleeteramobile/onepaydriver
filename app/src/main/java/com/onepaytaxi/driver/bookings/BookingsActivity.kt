package com.onepaytaxi.driver.bookings

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.onepaytaxi.driver.bookings.activetrip.ActiveTripActivity
import com.onepaytaxi.driver.bookings.completed.CompletedTripActivity
import com.onepaytaxi.driver.bookings.schdule.ScheduleBookingActivity
import com.onepaytaxi.driver.bookings.upcomingbookings.UpcomingBookingsActivity
import com.onepaytaxi.driver.databinding.ActivityBookingsBinding

class BookingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        omClickMethod()
    }

    private fun omClickMethod() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.completedTrip.setOnClickListener {
            val intent = Intent(this, CompletedTripActivity::class.java)
            startActivity(intent)
        }
        binding.activeTrip.setOnClickListener {
            val intent = Intent(this, ActiveTripActivity::class.java)
            startActivity(intent)
        }
        binding.schduleItem.setOnClickListener {
            val intent = Intent(this, ScheduleBookingActivity::class.java)
            startActivity(intent)

        }
        binding.upcomingItem.setOnClickListener {
            val intent = Intent(this, UpcomingBookingsActivity::class.java)
            startActivity(intent)
        }
    }
}