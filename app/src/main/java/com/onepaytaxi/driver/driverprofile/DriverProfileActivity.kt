package com.onepaytaxi.driver.driverprofile

import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.databinding.ActivityDriverProfileBinding

import com.onepaytaxi.driver.interfaces.APIResult
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON
import com.onepaytaxi.driver.utils.SessionSave

import org.json.JSONObject

class DriverProfileActivity : AppCompatActivity(), APIResult {

    private lateinit var binding: ActivityDriverProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        fetchProfileData()
    }

    private fun fetchProfileData() {
        try {
            val j = JSONObject()
            j.put("userid", SessionSave.getSession("Id", this))
            val url = "type=driver_profile"

            APIService_Retrofit_JSON(this, this, j, false).execute(url)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getResult(isSuccess: Boolean, result: String) {
        if (!isSuccess) return

        try {
            val json = JSONObject(result)
            if (json.getInt("status") == 1) {

                val d = json.getJSONObject("detail")

                // TEXT DATA
                binding.tvName.text = d.optString("name")
                binding.tvEmail.text = d.optString("email")
                binding.tvPhone.text = d.optString("phone")
                binding.tvTaxiModel.text = d.optString("taxi_model")
                binding.tvLicenseId.text = d.optString("driver_license_id")
                binding.tvAddress.text = d.optString("address")
                binding.tvTaxiNo.text = d.optString("taxi_no")

                // PROFILE IMAGE
                Glide.with(this)
                    .load(d.optString("main_image_path"))
                    .into(binding.profileImage)

                // LICENSE IMAGES
                val frontUrl = d.optString("driver_licence_path")
                val backUrl = d.optString("driver_licence_back_side_path")

                Glide.with(this).load(frontUrl).into(binding.ivLicenseFront)
                Glide.with(this).load(backUrl).into(binding.ivLicenseBack)

                binding.ivLicenseFront.setOnClickListener { showZoom(frontUrl) }
                binding.ivLicenseBack.setOnClickListener { showZoom(backUrl) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showZoom(url: String) {
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_zoom_image)

        val img = dialog.findViewById<ImageView>(R.id.ivZoom)
        val close = dialog.findViewById<ImageView>(R.id.btnCloseZoom)

        Glide.with(this).load(url).into(img)
        close.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }
}
