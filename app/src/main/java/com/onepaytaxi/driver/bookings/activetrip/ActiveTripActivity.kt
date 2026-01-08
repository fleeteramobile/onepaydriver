package com.onepaytaxi.driver.bookings.activetrip

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
import com.onepaytaxi.driver.OngoingAct
import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.bookings.adapter.ActiveTripListAdapter
import com.onepaytaxi.driver.bookings.interfaces.ActiveTrip
import com.onepaytaxi.driver.bookings.model.ResponseUpcomingTrips
import com.onepaytaxi.driver.data.apiData.ApiRequestData
import com.onepaytaxi.driver.interfaces.ClickInterface
import com.onepaytaxi.driver.service.RetrofitCallbackClass
import com.onepaytaxi.driver.service.ServiceGenerator
import com.onepaytaxi.driver.utils.CToast
import com.onepaytaxi.driver.utils.NC
import com.onepaytaxi.driver.utils.SessionSave
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActiveTripActivity : AppCompatActivity(), ActiveTrip, ClickInterface {
    private var trip_id: String? = null
    lateinit var upcoming_trip_list: RecyclerView
    lateinit var no_data_image: ImageView
    private var upComingData: ArrayList<ResponseUpcomingTrips.Detail.PendingBooking> = ArrayList()
    private lateinit var activeTripListAdapter: ActiveTripListAdapter
    var mshowDialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_acvite_trip)
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }
        upcoming_trip_list = findViewById(R.id.completed_trip_list_new)
        no_data_image = findViewById(R.id.no_data_image)
        upcoming_trip_list.layoutManager = LinearLayoutManager(this@ActiveTripActivity)


        activeTripListAdapter = ActiveTripListAdapter(this@ActiveTripActivity, upComingData,
            this@ActiveTripActivity)

        upcoming_trip_list.adapter = activeTripListAdapter

        loadCancelledListApi()
    }

    private fun loadCancelledListApi() {

        showLoadings(this@ActiveTripActivity)

        val client = MyApplication.getInstance().apiManagerWithEncryptBaseUrl

        val request = ApiRequestData.UpcomingRequest().apply {
            setId(SessionSave.getSession("Id", this@ActiveTripActivity))
            setDeviceType("2")
            setLimit("10")
            setStart("0")
            setRequestType("1")
        }

        val loginResponse = client.activeTrips(
            ServiceGenerator.COMPANY_KEY,
            request,
            SessionSave.getSession("Lang", this@ActiveTripActivity)
        )

        loginResponse.enqueue(
            RetrofitCallbackClass<ResponseUpcomingTrips>(
                this@ActiveTripActivity,
                object : Callback<ResponseUpcomingTrips?> {

                    override fun onResponse(
                        call: Call<ResponseUpcomingTrips?>,
                        response: Response<ResponseUpcomingTrips?>
                    ) {
                        cancelLoadings()

                        if (!response.isSuccessful) {
                            Toast.makeText(
                                this@ActiveTripActivity,
                                "API Error: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()

                            upcoming_trip_list.visibility = View.GONE
                            no_data_image.visibility = View.VISIBLE
                            return
                        }

                        val data = response.body()

                        if (data != null && data.status == 1) {

                            // 🔹 FILTER: travel_status != 0
                            val filteredList = data.detail.pending_booking
                                ?.filter { it.travel_status != 0 }

                            upComingData.clear()

                            if (!filteredList.isNullOrEmpty()) {

                                upComingData.addAll(filteredList)
                                activeTripListAdapter.notifyDataSetChanged()

                                println("pickup_location_newbooking_size ${upComingData.size}")

                                upcoming_trip_list.visibility = View.VISIBLE
                                no_data_image.visibility = View.GONE

                            } else {
                                activeTripListAdapter.notifyDataSetChanged()
                                upcoming_trip_list.visibility = View.GONE
                                no_data_image.visibility = View.VISIBLE
                            }

                        } else {
                            upComingData.clear()
                            activeTripListAdapter.notifyDataSetChanged()

                            upcoming_trip_list.visibility = View.GONE
                            no_data_image.visibility = View.VISIBLE

                            Toast.makeText(
                                this@ActiveTripActivity,
                                data?.message ?: "No bookings found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<ResponseUpcomingTrips?>,
                        t: Throwable
                    ) {
                        cancelLoadings()
                        upcoming_trip_list.visibility = View.GONE
                        no_data_image.visibility = View.VISIBLE
                    }
                }
            )
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
            if (mshowDialog != null) if (mshowDialog!!.isShowing && this@ActiveTripActivity != null) mshowDialog!!.dismiss()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    override fun trackTrip(_category: ResponseUpcomingTrips.Detail.PendingBooking) {
        trip_id = _category.passengers_log_id
        if (SessionSave.getSession("shift_status", this@ActiveTripActivity).equals("IN", ignoreCase = true)) {
            SessionSave.saveSession(
                "trip_id",
                trip_id!!.trim(),
                this@ActiveTripActivity
            )
            val intent = Intent(this@ActiveTripActivity, OngoingAct::class.java)
            startActivity(intent)
        } else {
            CToast.ShowToast(this@ActiveTripActivity, NC.getString(R.string.track_shift_status))
        }
    }

    override fun callCustomer(_category: ResponseUpcomingTrips.Detail.PendingBooking) {

    }

    override fun positiveButtonClick(
        dialog: DialogInterface?,
        id: Int,
        s: String?
    ) {
        dialog!!.dismiss()
    }

    override fun negativeButtonClick(
        dialog: DialogInterface?,
        id: Int,
        s: String?
    ) {
        dialog!!.dismiss()
    }
}