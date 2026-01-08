package com.mayan.sospluginmodlue.service

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Telephony
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mayan.sospluginmodlue.interfaces.GetAddress
import com.mayan.sospluginmodlue.model.ApiRequestData
import com.mayan.sospluginmodlue.model.CommonData
import com.mayan.sospluginmodlue.model.ContactsData
import com.mayan.sospluginmodlue.util.SessionSave
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SOSService : Service(), GetAddress {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    var contactsDataList: ArrayList<ContactsData> = ArrayList()
    private var sosCustomMessage: String = ""
    override fun setaddress(latitude: Double, longitude: Double, Address: String?) {
        if (SessionSave.getSession("sos_id", this@SOSService) != "") {
            if (Address != null) {
                if (isOnline(this@SOSService)) {
                    callSOSApi(latitude, longitude, Address)
                } else {
                    stopSelf()
                }
                sendSMS("LatLng:$latitude,$longitude  $Address", latitude, longitude)
            } else {
                if (isOnline(this@SOSService)) {
                    callSOSApi(latitude, longitude, "")
                } else {
                    stopSelf()
                }
                sendSMS("LatLng:$latitude,$longitude  ", latitude, longitude)
            }
        } else {
            stopSelf()
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        fun startLocationService(context: Context) {
            if (!CommonData.serviceIsRunningInForeground(context)) {
                val pushIntent1 = Intent(context, SOSService::class.java)
                context.startService(pushIntent1)
            }
        }
    }

    private fun sendSMS(address: String, latitude: Double, longitude: Double) {

        try {
            sosCustomMessage = SessionSave.getSession("sos_message", this@SOSService)
                    .replace("##USERTYPE##", SessionSave.getSession("user_type", this@SOSService))
                    .replace("##USERNAME##", SessionSave.getSession("u_name", this@SOSService))
                    .replace("##LOCATION##", address)
                    .replace("##LOCATION_URL##", "https://maps.google.com/?q=$latitude,$longitude")

            if (contactsDataList != null && contactsDataList.size > 0) {
                sendViaIntent()
            } else {
                sendViaIntentZeroContact()
            }
        } catch (e: Exception) {
            if (contactsDataList != null && contactsDataList.size > 0) {
                sendViaIntent()
            } else {
                sendViaIntentZeroContact()
            }
        }
    }

    private fun sendViaIntent() {
        var number = ""
        for (contacts in contactsDataList) {
            number += contacts.contact_number + ";"
        }
        number = number.substring(0, number.length - 1)

        val i = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto: $number"))
        i.putExtra("sms_body", sosCustomMessage)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (i.resolveActivity(packageManager) != null) {
            startActivity(i)
        } else {
            Toast.makeText(this, "SMS App not found", Toast.LENGTH_LONG).show()
        }

    }


    override fun onCreate() {
        super.onCreate()

        val listOfTestObject = object : TypeToken<List<ContactsData>>() {

        }.type

        if (SessionSave.getSession("contact_sos_list", this@SOSService) != "" && listOfTestObject != null) {
            contactsDataList = Gson().fromJson(SessionSave.getSession("contact_sos_list", this@SOSService), listOfTestObject)
        }


        if (checkPermission(this@SOSService)) {
//            getLastLocation()


            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            AddressFromLatLng(this, LatLng(location.latitude, location.longitude), this, "").execute()
                        } else {
                            if (!SessionSave.getSession("sos_last_lat", this@SOSService).equals(""))
                                AddressFromLatLng(this, LatLng(SessionSave.getSession("sos_last_lat", this@SOSService).toDouble(), SessionSave.getSession("sos_last_lng", this@SOSService).toDouble()), this, "").execute()
                            else
                                sendSMS("Unknown", 0.0, 0.0)
                        }
                    }
        }

    }

    private fun checkPermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }


    private fun callSOSApi(latitude: Double, longitude: Double, location: String) {


        var tripId = 0
        if (SessionSave.getSession("trip_id", this@SOSService) != null &&
                SessionSave.getSession("trip_id", this@SOSService) != "") {
            tripId = SessionSave.getSession("trip_id", this@SOSService).toInt()
        }
        val client = ServiceGenerator(this@SOSService,"").createService(CoreClient::class.java)
        val request = ApiRequestData.EmergencyRequestData(SessionSave.getSession("sos_id", this@SOSService).toInt(), SessionSave.getSession("user_type", this@SOSService), tripId, latitude = "" + latitude, longitude = "" + longitude, location = location, company_id = SessionSave.getSession("company_id", this@SOSService))

        val loginResponse = client.Emergencysos(request, SessionSave.getSession("Lang", this@SOSService))

        loginResponse.enqueue(RetrofitCallbackClass<ApiRequestData.StandardResponse>(this@SOSService, object : Callback<ApiRequestData.StandardResponse> {
            override fun onFailure(call: Call<ApiRequestData.StandardResponse>?, t: Throwable?) {
                println(t?.localizedMessage)
                sendSMS("unknow#", latitude, longitude)
                stopSelf()
            }

            override fun onResponse(call: Call<ApiRequestData.StandardResponse>?, response: Response<ApiRequestData.StandardResponse>?) {
                val emergencyListData = response?.body()
                if (emergencyListData?.status == 1) {
                    Toast.makeText(this@SOSService, emergencyListData.message, Toast.LENGTH_LONG).show()
                } else {
                    // more_info.setText(emergencyListData?.message)
                    Toast.makeText(this@SOSService, emergencyListData?.message, Toast.LENGTH_LONG).show()
                }
                stopSelf()
            }

        }))


    }

    fun isOnline(mContext2: Context): Boolean {
        val connectivity = mContext2.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.allNetworkInfo
            if (info != null)
                for (i in info.indices)
                    if (info[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
        }
        return false
    }

    private fun sendViaIntentZeroContact() {
        val defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this)
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        sendIntent.type = "text/plain"
        sendIntent.putExtra(Intent.EXTRA_TEXT, sosCustomMessage)

        if (defaultSmsPackageName != null) {
            sendIntent.setPackage(defaultSmsPackageName)
        }
        startActivity(sendIntent)
    }
}

