package com.onepaytaxi.driver.bookings.schdule

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
import com.onepaytaxi.driver.bookings.adapter.ScheduleTripListAdapter
import com.onepaytaxi.driver.bookings.interfaces.ScheduleTrip
import com.onepaytaxi.driver.bookings.model.ResponseNewSchduleBooking
import com.onepaytaxi.driver.data.apiData.ApiRequestData
import com.onepaytaxi.driver.interfaces.APIResult
import com.onepaytaxi.driver.interfaces.ClickInterface
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON
import com.onepaytaxi.driver.service.RetrofitCallbackClass
import com.onepaytaxi.driver.service.ServiceGenerator
import com.onepaytaxi.driver.utils.CToast
import com.onepaytaxi.driver.utils.NC
import com.onepaytaxi.driver.utils.NetworkStatus.isOnline
import com.onepaytaxi.driver.utils.SessionSave
import com.onepaytaxi.driver.utils.Utils
import com.onepaytaxi.driver.wallet.DriverWalletRechargeActivity
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScheduleBookingActivity : AppCompatActivity(), ScheduleTrip, ClickInterface {

    lateinit var upcoming_trip_list: RecyclerView
    lateinit var no_data_image: ImageView
    var mshowDialog: Dialog? = null
    var trip_id = ""
    private var upComingData: ArrayList<ResponseNewSchduleBooking.Detail.ShowBooking> = ArrayList()
    private lateinit var scheduleTripListAdapter: ScheduleTripListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_schedule_booking)


        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }
        upcoming_trip_list = findViewById(R.id.completed_trip_list_new)
        no_data_image = findViewById(R.id.no_data_image)
        upcoming_trip_list.layoutManager = LinearLayoutManager(this@ScheduleBookingActivity)


        scheduleTripListAdapter = ScheduleTripListAdapter(this@ScheduleBookingActivity, upComingData,
            this@ScheduleBookingActivity)

        upcoming_trip_list.adapter = scheduleTripListAdapter

        loadCancelledListApi()

    }


    private fun loadCancelledListApi() {



        showLoadings(this@ScheduleBookingActivity)
        val client = MyApplication.getInstance().apiManagerWithEncryptBaseUrl

        val request = ApiRequestData.UpcomingRequest()
        request.setId(SessionSave.getSession("Id", this@ScheduleBookingActivity))
        request.setDeviceType("2")
        request.setLimit("10")
        request.setStart("0")
        request.setRequestType("3")
        val LoginResponse = client.schduleTrip(
            ServiceGenerator.COMPANY_KEY,
            request,
            SessionSave.getSession("Lang",this@ScheduleBookingActivity)
        )
        LoginResponse.enqueue(
            RetrofitCallbackClass<ResponseNewSchduleBooking>(
                this@ScheduleBookingActivity,
                object : Callback<ResponseNewSchduleBooking?> {
                    override fun onResponse(
                        call: Call<ResponseNewSchduleBooking?>,
                        response: Response<ResponseNewSchduleBooking?>
                    ) {
                        if (response.isSuccessful) {
                            cancelLoadings()




                            if (response.isSuccessful) {
                                val data = response.body()

                                if (data != null && data.status == 1) {
                                    upComingData.clear() // Clear the old data
                                    // Add all new bookings to the mutable list


                                    if (data.detail.show_booking.size  !=0 )
                                    {
                                        data.detail.show_booking?.let {
                                            upComingData.addAll(it)
                                        }
                                        println("pickup_location_newbooking_size" + " " + upComingData.size)

                                        // Notify the adapter that the data set has changed
                                        scheduleTripListAdapter.notifyDataSetChanged()

                                        println("pickup_location_newbooking" + " " + "issettttttttttttttttttt")
                                        upcoming_trip_list.visibility = View.VISIBLE
                                        no_data_image .visibility = View.GONE
                                    }
                                    else{
                                        upcoming_trip_list.visibility = View.GONE
                                        no_data_image .visibility = View.VISIBLE
                                    }

                                } else {
                                    upComingData.clear() // Clear data if status is not 1 or data is null
                                    scheduleTripListAdapter.notifyDataSetChanged() // Update UI to show empty list
                                    upcoming_trip_list.visibility = View.GONE
                                    no_data_image .visibility = View.VISIBLE
                                    Toast.makeText(
                                        this@ScheduleBookingActivity,
                                        data?.message ?: "No bookings found", // Use data.message if available
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                // Handle HTTP errors (e.g., 404, 500)
                                // cancelLoadings() // Uncomment if you have this function
                                Toast.makeText(
                                    this@ScheduleBookingActivity,
                                    "API Error: ${response.code()}",
                                    Toast.LENGTH_SHORT
                                ).show()

                                upcoming_trip_list.visibility = View.GONE
                                no_data_image .visibility = View.VISIBLE
                            }



                        } else {
                            cancelLoadings()
                        }
                    }

                    override fun onFailure(call: Call<ResponseNewSchduleBooking?>, t: Throwable) {
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
            if (mshowDialog != null) if (mshowDialog!!.isShowing && this@ScheduleBookingActivity != null) mshowDialog!!.dismiss()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

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

    override fun acceptScheduleTrip(_category: ResponseNewSchduleBooking.Detail.ShowBooking) {trip_id
        trip_id = _category.passengers_log_id
        checkWalletBalance()


       // accpetSchduleTrip()
    }

    private fun checkWalletBalance() {
        try {
            val j = JSONObject()
            j.put("trip_id",trip_id)
            j.put(
                "driver_id",
                SessionSave.getSession("Id", this@ScheduleBookingActivity)
            )
            val checkWalletBalance = "type=check_driver_trip_threshold"
            CheckWalletBalance(checkWalletBalance, j)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    inner class CheckWalletBalance internal constructor(url: String?, data: JSONObject?) :
        APIResult {
        init {
            try {
                if (isOnline(this@ScheduleBookingActivity)) {
                    APIService_Retrofit_JSON(
                        this@ScheduleBookingActivity,
                        this,
                        data,
                        false
                    ).execute(url)
                } else {
                    Utils.alert_view(
                        this@ScheduleBookingActivity,
                        NC.getString(R.string.message),
                        NC.getString(R.string.check_net_connection),
                        NC.getString(R.string.ok),
                        "",
                        true,
                        this@ScheduleBookingActivity,
                        "4"
                    )
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        override fun getResult(isSuccess: Boolean, result: String) {
            if (isSuccess) {
                try {
                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {
accpetSchduleTrip()
                    } else {
                        CToast.ShowToast(this@ScheduleBookingActivity, json.getString("message"))

                        startActivity(Intent(this@ScheduleBookingActivity, DriverWalletRechargeActivity::class.java))
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                // CToast.ShowToast(MyStatus.this, NC.getString(R.string.server_error));
            }
        }
    }
    fun accpetSchduleTrip()
    {
        try {
            val j = JSONObject()
            j.put("trip_id",trip_id)
            j.put(
                "driver_id",
                SessionSave.getSession("Id", this@ScheduleBookingActivity)
            )
            j.put(
                "taxi_id",
                SessionSave.getSession("taxi_id", this@ScheduleBookingActivity)
            )
            val scheduleTripUrl = "type=show_booking_accept_trip"
            ScheduleTrip(scheduleTripUrl, j)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    inner class ScheduleTrip internal constructor(url: String?, data: JSONObject?) :
        APIResult {
        init {
            try {
                if (isOnline(this@ScheduleBookingActivity)) {
                    APIService_Retrofit_JSON(
                        this@ScheduleBookingActivity,
                        this,
                        data,
                        false
                    ).execute(url)
                } else {
                    Utils.alert_view(
                        this@ScheduleBookingActivity,
                        NC.getString(R.string.message),
                        NC.getString(R.string.check_net_connection),
                        NC.getString(R.string.ok),
                        "",
                        true,
                        this@ScheduleBookingActivity,
                        "4"
                    )
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        override fun getResult(isSuccess: Boolean, result: String) {
            if (isSuccess) {
                try {
                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {
                        loadCancelledListApi()
                        CToast.ShowToast(this@ScheduleBookingActivity, json.getString("message"))
                    } else {
                        CToast.ShowToast(this@ScheduleBookingActivity, json.getString("message"))
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                // CToast.ShowToast(MyStatus.this, NC.getString(R.string.server_error));
            }
        }
    }
}