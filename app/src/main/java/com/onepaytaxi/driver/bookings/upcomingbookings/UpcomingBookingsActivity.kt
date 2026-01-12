package com.onepaytaxi.driver.bookings.upcomingbookings

import androidx.appcompat.app.AlertDialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.onepaytaxi.driver.MainActivity
import com.onepaytaxi.driver.MyApplication
import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.bookings.adapter.UpcomingTripListAdapter
import com.onepaytaxi.driver.bookings.interfaces.UpcomeingTrip
import com.onepaytaxi.driver.bookings.model.ResponseUpcomingTrips
import com.onepaytaxi.driver.data.apiData.ApiRequestData
import com.onepaytaxi.driver.interfaces.APIResult
import com.onepaytaxi.driver.interfaces.ClickInterface
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON
import com.onepaytaxi.driver.service.RetrofitCallbackClass
import com.onepaytaxi.driver.service.ServiceGenerator
import com.onepaytaxi.driver.trackschdule.TrackSchduleTripActivity
import com.onepaytaxi.driver.utils.CToast
import com.onepaytaxi.driver.utils.NC
import com.onepaytaxi.driver.utils.NetworkStatus
import com.onepaytaxi.driver.utils.SessionSave
import com.onepaytaxi.driver.utils.Utils
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpcomingBookingsActivity : AppCompatActivity(), UpcomeingTrip, ClickInterface {

    private var trip_id: String? = null
    lateinit var upcoming_trip_list: RecyclerView
    lateinit var no_data_image: ImageView
    private var upComingData: ArrayList<ResponseUpcomingTrips.Detail.PendingBooking> = ArrayList()
    private lateinit var upcomingTripListAdapter: UpcomingTripListAdapter
    var mshowDialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_upcoming_bookings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }
        upcoming_trip_list = findViewById(R.id.completed_trip_list_new)
        no_data_image = findViewById(R.id.no_data_image)
        upcoming_trip_list.layoutManager = LinearLayoutManager(this@UpcomingBookingsActivity)


        upcomingTripListAdapter = UpcomingTripListAdapter(
            this@UpcomingBookingsActivity, upComingData,
            this@UpcomingBookingsActivity
        )

        upcoming_trip_list.adapter = upcomingTripListAdapter

        loadCancelledListApi()
    }

    private fun loadCancelledListApi() {

        showLoadings(this@UpcomingBookingsActivity)

        val client = MyApplication.getInstance().apiManagerWithEncryptBaseUrl

        val request = ApiRequestData.UpcomingRequest().apply {
            setId(SessionSave.getSession("Id", this@UpcomingBookingsActivity))
            setDeviceType("2")
            setLimit("10")
            setStart("0")
            setRequestType("1")
        }

        val loginResponse = client.activeTrips(
            ServiceGenerator.COMPANY_KEY,
            request,
            SessionSave.getSession("Lang", this@UpcomingBookingsActivity)
        )

        loginResponse.enqueue(
            RetrofitCallbackClass<ResponseUpcomingTrips>(
                this@UpcomingBookingsActivity,
                object : Callback<ResponseUpcomingTrips?> {

                    override fun onResponse(
                        call: Call<ResponseUpcomingTrips?>,
                        response: Response<ResponseUpcomingTrips?>
                    ) {
                        cancelLoadings()

                        if (!response.isSuccessful) {
                            Toast.makeText(
                                this@UpcomingBookingsActivity,
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
                                ?.filter { it.travel_status == 0 }

                            upComingData.clear()

                            if (!filteredList.isNullOrEmpty()) {

                                upComingData.addAll(filteredList)
                                upcomingTripListAdapter.notifyDataSetChanged()

                                println("pickup_location_newbooking_size ${upComingData.size}")

                                upcoming_trip_list.visibility = View.VISIBLE
                                no_data_image.visibility = View.GONE

                            } else {
                                upcomingTripListAdapter.notifyDataSetChanged()
                                upcoming_trip_list.visibility = View.GONE
                                no_data_image.visibility = View.VISIBLE
                            }

                        } else {
                            upComingData.clear()
                            upcomingTripListAdapter.notifyDataSetChanged()

                            upcoming_trip_list.visibility = View.GONE
                            no_data_image.visibility = View.VISIBLE

                            Toast.makeText(
                                this@UpcomingBookingsActivity,
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
            if (mshowDialog != null) if (mshowDialog!!.isShowing && this@UpcomingBookingsActivity != null) mshowDialog!!.dismiss()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    override fun startTrip(_category: ResponseUpcomingTrips.Detail.PendingBooking) {

        val pickupLat = _category.pickup_latitude.toDoubleOrNull()
        val pickupLng = _category.pickup_longitude.toDoubleOrNull()
        val dropLat = _category.drop_latitude.toDoubleOrNull()
        val dropLng = _category.drop_longitude.toDoubleOrNull()

        if (pickupLat == null || pickupLng == null || dropLat == null || dropLng == null) {
            Toast.makeText(this, "Invalid location data", Toast.LENGTH_SHORT).show()
            return
        }

        if (_category.trip_approval == "0") {

            // 🔴 Redirect to Google Maps
            val mapUri = Uri.parse(
                "https://www.google.com/maps/dir/?api=1" +
                        "&origin=$pickupLat,$pickupLng" +
                        "&destination=$dropLat,$dropLng" +
                        "&travelmode=driving"
            )

            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                startActivity(Intent(Intent.ACTION_VIEW, mapUri))
            }

        } else {

            // 🟢 Start Trip Activity
            val intent = Intent(this, TrackSchduleTripActivity::class.java)
            intent.putExtra("schdule_trip_id", _category.passengers_log_id)
            intent.putExtra("pick_up_lat", _category.pickup_latitude)
            intent.putExtra("pick_up_lang", _category.pickup_longitude)
            intent.putExtra("booking_Type", "3")
            startActivity(intent)
        }
    }


    override fun decline(_category: ResponseUpcomingTrips.Detail.PendingBooking) {

        val dialog = AlertDialog.Builder(this)
            .setTitle("Cancel Trip")
            .setMessage(
                "Cancellation charges of ₹500 will be applied if you cancel this trip.\n\nDo you want to continue?"
            )
            .setPositiveButton("Yes") { d, _ ->
                try {
                    val j = JSONObject()
                    j.put("trip_id", _category.passengers_log_id)
                    j.put("driver_id", SessionSave.getSession("Id", this))
                    j.put("model_id", SessionSave.getSession("taxi_id", this))

                    val canceltripUrl = "type=show_booking_driver_cancel"
                    CancelTrip(canceltripUrl, j)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                d.dismiss()
            }
            .setNegativeButton("No") { d, _ ->
                d.dismiss()
            }
            .setCancelable(false)
            .create()

        dialog.show()
    }



    override fun callCustomer(_category: ResponseUpcomingTrips.Detail.PendingBooking) {
        if(_category.trip_approval.equals("0"))
        {
            openDialer(
                SessionSave.getSession("driver_phone_number", this@UpcomingBookingsActivity)
            )
        }
        else{
            openDialer(
                _category.passenger_phone
            )
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


    private fun openDialer(phoneNumber: String) {
        // Create a URI with the "tel:" scheme and the phone number
        val dialIntentUri = Uri.parse("tel:$phoneNumber")

        // Create an Intent with ACTION_DIAL to open the dialer app
        val dialIntent = Intent(Intent.ACTION_DIAL, dialIntentUri)

        // Check if there's an app that can handle this Intent
        if (dialIntent.resolveActivity(packageManager) != null) {
            startActivity(dialIntent)
        } else {
            Toast.makeText(this, "No dialer app found to handle this request.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    inner class CancelTrip internal constructor(url: String?, data: JSONObject?) :
        APIResult {
        init {
            try {
                if (NetworkStatus.isOnline(this@UpcomingBookingsActivity)) {

                    APIService_Retrofit_JSON(
                        this@UpcomingBookingsActivity,
                        this,
                        data,
                        false
                    ).execute(url)
                } else {
                    Utils.alert_view(
                        this@UpcomingBookingsActivity,
                        NC.getString(R.string.message),
                        NC.getString(R.string.check_net_connection),
                        NC.getString(R.string.ok),
                        "",
                        true,
                        this@UpcomingBookingsActivity,
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
                        CToast.ShowToast(this@UpcomingBookingsActivity, json.getString("message"))
                    } else {
                        CToast.ShowToast(this@UpcomingBookingsActivity, json.getString("message"))
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                //CToast.ShowToast(MyStatus.this, NC.getString(R.string.server_error));
            }
        }
    }
}