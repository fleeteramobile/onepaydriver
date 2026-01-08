package com.onepaytaxi.driver.bookings.completed

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.onepaytaxi.driver.MyApplication
import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.bookings.adapter.CompletedTripListAdapter
import com.onepaytaxi.driver.bookings.interfaces.CompletedTrip
import com.onepaytaxi.driver.bookings.model.ResponsePastBooking
import com.onepaytaxi.driver.data.apiData.ApiRequestData
import com.onepaytaxi.driver.interfaces.ClickInterface
import com.onepaytaxi.driver.service.RetrofitCallbackClass
import com.onepaytaxi.driver.service.ServiceGenerator
import com.onepaytaxi.driver.triphistory.TripDetailsActivity
import com.onepaytaxi.driver.utils.SessionSave
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompletedTripActivity : AppCompatActivity(), CompletedTrip, ClickInterface {

    lateinit var upcoming_trip_list: RecyclerView
    lateinit var no_data_image: ImageView
    private var upComingData: ArrayList<ResponsePastBooking.Detail.PastBooking> = ArrayList()
    private lateinit var completedTripListAdapter: CompletedTripListAdapter
    var mshowDialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_completed_trip)
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }
        upcoming_trip_list = findViewById(R.id.completed_trip_list_new)
        no_data_image = findViewById(R.id.no_data_image)
        upcoming_trip_list.layoutManager = LinearLayoutManager(this@CompletedTripActivity)


        completedTripListAdapter = CompletedTripListAdapter(this@CompletedTripActivity, upComingData,
            this@CompletedTripActivity)

        upcoming_trip_list.adapter = completedTripListAdapter

        loadCancelledListApi()

    }

    private fun loadCancelledListApi() {



        showLoadings(this@CompletedTripActivity)
        val client = MyApplication.getInstance().apiManagerWithEncryptBaseUrl

        val request = ApiRequestData.UpcomingRequest()
        request.setId(SessionSave.getSession("Id", this@CompletedTripActivity))
        request.setDeviceType("2")
        request.setLimit("10")
        request.setStart("0")
        request.setRequestType("2")
        val LoginResponse = client.completedTrips(
            ServiceGenerator.COMPANY_KEY,
            request,
            SessionSave.getSession("Lang",this@CompletedTripActivity)
        )
        LoginResponse.enqueue(
            RetrofitCallbackClass<ResponsePastBooking>(
                this@CompletedTripActivity,
                object : Callback<ResponsePastBooking?> {
                    override fun onResponse(
                        call: Call<ResponsePastBooking?>,
                        response: Response<ResponsePastBooking?>
                    ) {
                        if (response.isSuccessful) {
                            cancelLoadings()




                            if (response.isSuccessful) {
                                val data = response.body()

                                if (data != null && data.status == 1) {
                                    upComingData.clear() // Clear the old data
                                    // Add all new bookings to the mutable list


                                    if (data.detail.past_booking?.size != 0) {
                                        data.detail.past_booking?.let {
                                            upComingData.addAll(it)
                                        }
                                        println("pickup_location_newbooking_size" + " " + upComingData.size)

                                        // Notify the adapter that the data set has changed
                                        completedTripListAdapter.notifyDataSetChanged()

                                        println("pickup_location_newbooking" + " " + "issettttttttttttttttttt")
                                        upcoming_trip_list.visibility = View.VISIBLE
                                        no_data_image.visibility = View.GONE
                                    } else {
                                        upcoming_trip_list.visibility = View.GONE
                                        no_data_image.visibility = View.VISIBLE
                                    }

                                } else {
                                    upComingData.clear() // Clear data if status is not 1 or data is null
                                    completedTripListAdapter.notifyDataSetChanged() // Update UI to show empty list
                                    upcoming_trip_list.visibility = View.GONE
                                    no_data_image.visibility = View.VISIBLE
                                    Toast.makeText(
                                        this@CompletedTripActivity,
                                        data?.message
                                            ?: "No bookings found", // Use data.message if available
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                // Handle HTTP errors (e.g., 404, 500)
                                // cancelLoadings() // Uncomment if you have this function
                                Toast.makeText(
                                    this@CompletedTripActivity,
                                    "API Error: ${response.code()}",
                                    Toast.LENGTH_SHORT
                                ).show()

                                upcoming_trip_list.visibility = View.GONE
                                no_data_image.visibility = View.VISIBLE
                            }


                        } else {
                            cancelLoadings()
                        }
                    }

                    override fun onFailure(call: Call<ResponsePastBooking?>, t: Throwable) {
                        cancelLoadings()
                    }
                })
        )
    }
    fun showLoadings(context: Context) {
        try {
            if (mshowDialog != null) if (mshowDialog!!.isShowing) mshowDialog!!.dismiss()
            val view = View.inflate(context, R.layout.progress_bar, null)
            mshowDialog = Dialog(context, R.style.dialogwinddow)
            mshowDialog!!.setContentView(view)
            mshowDialog!!.setCancelable(false)
            mshowDialog!!.show()
            val iv = mshowDialog!!.findViewById<ImageView>(R.id.giff)
            val imageViewTarget = DrawableImageViewTarget(iv)
            Glide.with(context)
                .load(R.raw.loading_anim)
                .into<DrawableImageViewTarget>(imageViewTarget)
        } catch (e: Exception) {
            // TODO: handle exception
        }
    }




    private fun cancelLoadings() {

        try {
            if (mshowDialog != null) if (mshowDialog!!.isShowing && this@CompletedTripActivity != null) mshowDialog!!.dismiss()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }
    override fun showTripDetails(_category: ResponsePastBooking.Detail.PastBooking) {
        SessionSave.saveSession("bookingid", _category.passengers_log_id.toString(), this@CompletedTripActivity)
        val intent = Intent(
            this@CompletedTripActivity,
            TripDetailsActivity::class.java
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(intent)
    }

    override fun positiveButtonClick(dialog: DialogInterface?, id: Int, s: String?) {
        dialog!!.dismiss()
    }

    override fun negativeButtonClick(dialog: DialogInterface?, id: Int, s: String?) {
        dialog!!.dismiss()
    }
}