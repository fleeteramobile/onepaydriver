package com.onepaytaxi.driver.triphistory

import android.app.Dialog
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.onepaytaxi.driver.MainActivity
import com.onepaytaxi.driver.MyApplication
import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.data.apiData.ApiRequestData.getTripDetailRequest
import com.onepaytaxi.driver.data.apiData.TripDetailResponse
import com.onepaytaxi.driver.service.RetrofitCallbackClass
import com.onepaytaxi.driver.service.ServiceGenerator
import com.onepaytaxi.driver.utils.NC
import com.onepaytaxi.driver.utils.SessionSave
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TripDetailsActivity : MainActivity() {

    var pickup_time: TextView? = null
    var passenger_comments: TextView? = null
    var drop_time: TextView? = null
    var pickup_location: TextView? = null
    var drop_location: TextView? = null
    var distance: TextView? = null
    var name: TextView? = null
    var back_trip_details: CardView? = null

    var trip_amount: TextView? = null
    var discount_amount: TextView? = null
    var total_bill: TextView? = null
    var total_payable: TextView? = null
    var tax_amount: TextView? = null
    var proimg: ImageView? = null
    var driverRat: ImageView? = null
    var payment_type: TextView? = null
    var mshowDialog: Dialog? = null
    override fun setLayout(): Int {
        return R.layout.activity_trip_details
    }

    override fun Initialize() {
        pickup_time = findViewById(R.id.pickup_time)
        drop_time = findViewById(R.id.drop_time)
        pickup_location = findViewById(R.id.pickup_location)
        drop_location = findViewById(R.id.drop_location)
        distance = findViewById(R.id.distance)
        name = findViewById(R.id.name)
        trip_amount = findViewById(R.id.trip_amount)
        discount_amount = findViewById(R.id.discount_amount)
        total_bill = findViewById(R.id.total_bill)
        total_payable = findViewById(R.id.total_payable)
        tax_amount = findViewById(R.id.tax_amount)
        proimg = findViewById(R.id.proimg)
        driverRat = findViewById(R.id.rating)
        payment_type = findViewById(R.id.payment_type)
        passenger_comments = findViewById(R.id.passenger_comments)
        back_trip_details = findViewById(R.id.back_trip_details)


        back_trip_details!!.setOnClickListener {
            onBackPressed()
        }










        callDetail()
    }

    private fun callDetail() {
        showLoadings(this@TripDetailsActivity)
        val client = MyApplication.getInstance().apiManagerWithEncryptBaseUrl
        val request = getTripDetailRequest()
        request.setTrip_id(SessionSave.getSession("bookingid", this@TripDetailsActivity))
        val LoginResponse = client.callData(
            ServiceGenerator.COMPANY_KEY,
            request,
            SessionSave.getSession("Lang", this@TripDetailsActivity)
        )
        LoginResponse.enqueue(
            RetrofitCallbackClass<TripDetailResponse>(
                this@TripDetailsActivity,
                object : Callback<TripDetailResponse?> {
                    override fun onResponse(
                        call: Call<TripDetailResponse?>,
                        response: Response<TripDetailResponse?>
                    ) {
                        cancelLoadings()
                        if (response.isSuccessful) {

                            val data = response.body()
                            if (data != null) {
                                if (data.status == 1) {

                                    Picasso.get().load(data.detail.passenger_image)

                                        .placeholder(resources.getDrawable(R.drawable.loadingimage))
                                       .into(proimg)

                                    pickup_time!!.text = data.detail.pickup_time_text
                                    drop_time!!.text = data.detail.drop_time_text
                                    pickup_location!!.text = data.detail.current_location
                                    drop_location!!.text = data.detail.drop_location
                                    distance!!.text = data.detail.distance + " "+ data.detail.metric
                                    name!!.text = data.detail.passenger_name
                                    trip_amount!!.text = data.site_currency + " " + data.detail.amt
                                    discount_amount!!.text = data.site_currency + " " + data.detail.promocode_fare
                                    total_bill!!.text = data.site_currency + " " + data.detail.actual_paid_amount
                                    total_payable!!.text = data.site_currency + " " + data.detail.actual_paid_amount
                                    payment_type!!.text = data.detail.payment_type_label
                                    if (data.detail.comments.equals("")) {
                                        passenger_comments!!.text = "NA"
                                    } else {
                                        passenger_comments!!.text = data.detail.comments

                                    }

                                    val driver_rating = data.detail.rating.toFloat().toInt()
                                    if (driver_rating == 0) driverRat!!.setImageResource(R.drawable.star6)
                                    else if (driver_rating == 1) driverRat!!.setImageResource(
                                        R.drawable.star1)
                                    else if (driver_rating == 2) driverRat!!.setImageResource(R.drawable.star2)
                                    else if (driver_rating == 3) driverRat!!.setImageResource(
                                        R.drawable.star3
                                    ) else if (driver_rating == 4)
                                        driverRat!!.setImageResource(R.drawable.star4)
                                    else if
                                                (driver_rating == 5) driverRat!!.setImageResource(
                                        R.drawable.star5
                                    )
                                    tax_amount!!.text = "Includes " + " " + data.site_currency + " " + data.detail.tax_fare + " " + "Taxes"
                                } else {
                                    Toast.makeText(
                                        this@TripDetailsActivity,
                                        data.message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            } else {
                                Toast.makeText(
                                    this@TripDetailsActivity,
                                    NC.getString(R.string.please_check_internet),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@TripDetailsActivity,
                                NC.getString(R.string.server_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<TripDetailResponse?>, t: Throwable) {
                        t.printStackTrace()
                        cancelLoading()
                        Toast.makeText(
                            this@TripDetailsActivity,
                            NC.getString(R.string.server_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        )
    }

    private fun cancelLoadings() {

        try {
            if (mshowDialog != null) if (mshowDialog!!.isShowing && this@TripDetailsActivity != null) mshowDialog!!.dismiss()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }


    fun showLoadings(tripDetailsActivity: TripDetailsActivity) {
        try {
            if (mshowDialog != null) if (mshowDialog!!.isShowing) mshowDialog!!.dismiss()
            val view = View.inflate(context, R.layout.progress_bar, null)
            mshowDialog = Dialog(context!!, R.style.dialogwinddow)
            mshowDialog!!.setContentView(view)
            mshowDialog!!.setCancelable(false)
            mshowDialog!!.show()
            val iv = mshowDialog!!.findViewById<ImageView>(R.id.giff)
            val imageViewTarget = DrawableImageViewTarget(iv)
            Glide.with(tripDetailsActivity)
                .load(R.raw.loading_anim)
                .into<DrawableImageViewTarget>(imageViewTarget)
        } catch (e: Exception) {
            // TODO: handle exception
        }
    }
}