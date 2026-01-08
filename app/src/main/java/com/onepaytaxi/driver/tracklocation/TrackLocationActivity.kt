package com.onepaytaxi.driver.tracklocation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.util.SparseArray
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.onepaytaxi.driver.MainActivity
import com.onepaytaxi.driver.MyApplication
import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.data.CommonData
import com.onepaytaxi.driver.interfaces.APIResult
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON
import com.onepaytaxi.driver.service.CoreClient
import com.onepaytaxi.driver.service.NonActivity
import com.onepaytaxi.driver.service.RetrofitCallbackClass
import com.onepaytaxi.driver.utils.CToast
import com.onepaytaxi.driver.utils.NC
import com.onepaytaxi.driver.utils.NetworkStatus
import com.onepaytaxi.driver.utils.SessionSave
import com.onepaytaxi.driver.utils.Utils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.seero.bookingmodule.DriverLiveMovement.DriverLiveMove
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ConcurrentModificationException
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


class TrackLocationActivity : MainActivity(), OnMapReadyCallback
{

    var gMaps: GoogleMap? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermissionGranted = false
    private var lastKnownLocation: Location? = null
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    var dialog1: Dialog? = null
    var checked = "OUT"
    var nonactiityobj = NonActivity()
    private var driverIdData: ArrayList<String> = ArrayList()
    private var availablecarcount: Int = 0

    private var onMovingDriverMarkers: SparseArray<Marker> = SparseArray()
    private var driverDelayHandler: Handler = Handler()
    private var onExistingDriverMarkers: ArrayList<Int> = ArrayList()

    var mytrip : SwitchCompat? = null
    private val distanceRunnable: Runnable? =
        null
    private var timerRunnable:Runnable? = null
    private var excecuter: ScheduledFuture<*>? = null
    private var UPDATE_INTERVAL: Long = 0
    private val TIMER_INTERVAL: Long = 30000
    private val mTimer = Executors.newSingleThreadScheduledExecutor()
    private var driverMarkerService: java.util.ArrayList<Marker> = java.util.ArrayList()
    private var driverLiveMovement: DriverLiveMove = DriverLiveMove()

    private val driverLocationHistoryRunnable = Runnable {
        startDriverMovementSocket()

    }

    @SuppressLint("MissingPermission")
    private fun startDriverMovementSocket() {
        println("shiftbefore " + " "+  "tested1234556789")
        Handler(Looper.getMainLooper()).postDelayed({


            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {


                            callNearestApi(   getNearestJsonObject(LatLng(lastKnownLocation!!.latitude,lastKnownLocation!!.longitude)))
                        }
                    }
                }
            }


        }, 7000)
    }

    override fun setLayout(): Int {
        return R.layout.fragment_track_location
    }

    @SuppressLint("MissingPermission")
    override fun Initialize() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        mytrip = findViewById(R.id.mytrip)

SessionSave.saveSession("current_page","TrackLocationActivity",this@TrackLocationActivity)
        mytrip!!.isChecked = SessionSave.getSession("shift_status",this@TrackLocationActivity).equals("IN")
        mytrip!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                checked = "IN"
                RequestingCheckBox()

            }
            else{

                checked = "OUT"
                RequestingCheckBox()


            }

        }




timerRunnable =  Runnable {
    run {
        if (excecuter != null && excecuter!!.getDelay(TimeUnit.SECONDS) >= 0 && excecuter!!.getDelay(
                TimeUnit.SECONDS
            ) < 5
        ) {
            println("shiftbefore " + " "+  "tested1234556789")
            UPDATE_INTERVAL += TIMER_INTERVAL
            driverDelayHandler.post { driverLocationHistoryRunnable.run() }
        }


    }
}
        excecuter =
            mTimer.scheduleAtFixedRate(timerRunnable, 0, TIMER_INTERVAL, TimeUnit.MILLISECONDS)

    }



    private fun getDeviceLocation() {

        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {


                            gMaps?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude, lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )

                            callNearestApi(   getNearestJsonObject(LatLng(lastKnownLocation!!.latitude,lastKnownLocation!!.longitude)))


                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        gMaps?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                defaultLocation,
                                DEFAULT_ZOOM.toFloat()
                            )
                        )
                        gMaps?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getLocationPermission() {/*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
    }

    private fun updateLocationUI() {
        if (gMaps == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                gMaps?.isMyLocationEnabled = true
                gMaps?.uiSettings?.isMyLocationButtonEnabled = true


            } else {
                gMaps?.isMyLocationEnabled = false
                gMaps?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    companion object {

        private const val DEFAULT_ZOOM = 16
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

    }

    inner class RequestingCheckBox : APIResult {

        init {
            val j = JSONObject()
            j.put("driver_id", SessionSave.getSession("Id", this@TrackLocationActivity))
            j.put("shiftstatus", checked)
            j.put("reason", "")
            Log.e("shiftbefore ", j.toString())
            println("shiftbefore " + " "+  j.toString())
            j.put("update_id", SessionSave.getSession("Shiftupdate_Id", this@TrackLocationActivity))
            val requestingCheckBox = "type=driver_shift_status"

            APIService_Retrofit_JSON(this@TrackLocationActivity, this, j, false).execute(
                requestingCheckBox
            )
        }

        @SuppressLint("MissingPermission")
        override fun getResult(isSuccess: Boolean, result: String?) {
            if (isSuccess) {
                val mJSONObject = JSONObject(result)
                Toast.makeText(this@TrackLocationActivity,mJSONObject.getString("message"),Toast.LENGTH_LONG).show()

                if (mJSONObject.getInt("status") == 1) {


                    if (locationPermissionGranted) {
                        val locationResult = fusedLocationProviderClient.lastLocation
                        locationResult.addOnCompleteListener(this@TrackLocationActivity) { task ->
                            if (task.isSuccessful) {
                                // Set the map's camera position to the current location of the device.
                                lastKnownLocation = task.result
                                if (lastKnownLocation != null) {


                                    callNearestApi(   getNearestJsonObject(LatLng(lastKnownLocation!!.latitude,lastKnownLocation!!.longitude)))
                                }
                            }
                        }
                    }

                    if (checked == "IN") {


                        SessionSave.saveSession("shift_status", "IN", this@TrackLocationActivity)
                        SessionSave.saveSession(CommonData.SHIFT_OUT, false, this@TrackLocationActivity)
                        SessionSave.saveSession(
                            "Shiftupdate_Id",
                            mJSONObject.getJSONObject("detail").getString("update_id"),
                            this@TrackLocationActivity
                        )

                        if (!SessionSave.getSession("driver_type", this@TrackLocationActivity)
                                .equals("D", ignoreCase = true)
                        ) {
                            nonactiityobj.startServicefromNonActivity(this@TrackLocationActivity)
                        }


                    }else{
                        SessionSave.saveSession("shift_status", "OUT", this@TrackLocationActivity)
                        SessionSave.saveSession("trip_id", "", this@TrackLocationActivity)
                        SessionSave.setWaitingTime(0L, this@TrackLocationActivity)
                        nonactiityobj.stopServicefromNonActivity(this@TrackLocationActivity)
                    }
                }
                else
                {

                    dialog1 = Utils.alert_view(
                        this@TrackLocationActivity,
                        "" + NC.getString(R.string.message),
                        "" + mJSONObject.getString("message"),
                        "" + NC.getString(R.string.ok),
                        "",
                        true,
                        this@TrackLocationActivity,
                        "6"
                    )
                }
            }
        }
    }

    override fun onMapReady(gMap: GoogleMap) {
        gMaps = gMap

        getLocationPermission()
        updateLocationUI()
        getDeviceLocation()
    }

    //model id one to 5

    private fun getNearestJsonObject(lastKnownLatLng: LatLng): JSONObject {
        val data = JSONObject()
        try {
            val j = JSONObject()

            j.put("latitude", lastKnownLatLng.latitude.toString())
            j.put("longitude", lastKnownLatLng.longitude.toString())
            j.put("motor_model", SessionSave.getSession("model_id",this@TrackLocationActivity))
            j.put("passenger_id", "")
            j.put("city_name", "")

            j.put("drop_longitude", "")
            j.put("drop_latitude", "")

            j.put("skip_fav", "0")

            j.put("skip_pop", "1")


            j.put(
                "corporate_company_id", ""
            )
            data.put("data", j)

            data.put("lang", SessionSave.getSession("Lang", this@TrackLocationActivity))

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return data
    }
    fun callNearestApi(requestData: JSONObject) {
        if (NetworkStatus.isOnline(context)) {
            if (SessionSave.getSession(CommonData.NODE_TOKEN, context) != "") {
                var client: CoreClient? = null
//                client = NodeServiceGenerator(context, SessionSave.getSession(TaxiUtil.NODE_URL, context), 8).createService(CoreClient::class.java)
                client = MyApplication.getInstance().getNodeApiManagerWithTimeOut(
                    SessionSave.getSession(
                        CommonData.NODE_URL, context
                    ), 8L
                )

                val body = RequestBody.create(
                    "application/json; charset=utf-8".toMediaTypeOrNull(),
                    requestData.toString()
                )
                val coreResponse = client.nodeUpdates(
                    body,
                    SessionSave.getSession("Lang", this@TrackLocationActivity)
                )
                coreResponse.enqueue(
                    RetrofitCallbackClass(
                        context,
                        object : Callback<NearestDriverDatas> {
                            override fun onResponse(
                                call: Call<NearestDriverDatas>,
                                response: Response<NearestDriverDatas>
                            ) {
                                if (response.isSuccessful) {
                                    try {
                                        val data = response.body()
                                        SessionSave.saveSession(
                                            "Server_Response",
                                            Gson().toJson(data),
                                            context
                                        )
                                        val jsonResponse: NearestDriverDatas = data!!
                                        var driverCoordinates: String


                                        if (CommonData.mDriverdata.size != 0)
                                            CommonData.mDriverdata.clear()
                                        driverIdData = ArrayList()


                                        if (CommonData.mDrivermovementdata != null) {
                                            CommonData.mDrivermovementdata.clear()
                                            removeMarker(driverMarkerService)
                                            driverLiveMovement.removeDriverLiveMovement()
                                        }


                                        val status = jsonResponse.status

                                        when (status) {
                                            1 -> {
                                                for (i in 0 until jsonResponse.detail.size) {
                                                    availablecarcount++
                                                    val detail = jsonResponse.detail
                                                    val listLatLng = java.util.ArrayList<String>()
                                                    driverCoordinates = detail[i].driver_coordinates
                                                    val latLng = driverCoordinates.split("#".toRegex())
                                                        .dropLastWhile { it.isEmpty() }.toTypedArray()
                                                    for (x in latLng.indices) {
                                                        listLatLng.add(latLng[x])
                                                    }
                                                    println("driver_id_texting"+" "+detail[i].driver_id.toString())
                                                    var driverId = detail[i].driver_id.toString()
                                                    var driverName = ""
                                                    var   lat = detail[i].latitude.toString()
                                                    var  lng = detail[i].longitude.toString()
                                                    var  nearest = detail[i].nearest_driver
                                                    var   distance = detail[i].distance_km
                                                    driverIdData.add(driverId)
                                                    val data = DriverData(
                                                        driverId,
                                                        driverName,
                                                        "45",
                                                        lat,
                                                        lng,
                                                        nearest,
                                                        distance,
                                                        null,
                                                        listLatLng
                                                    )
                                                    CommonData.mDriverdata.add(data)

                                                }
                                                findNearestlocal(

                                                    CommonData.mDriverdata,

                                                    )

                                            }
                                            3 -> {
                                                DriverLiveMovement(null)
                                              //  newDriverMarker(data)

                                            }
                                            0 ->{
                                                DriverLiveMovement(null)
                                            }
                                        }



                                    } catch (e: Exception) {
                                        e.printStackTrace()

                                    }
                                } else {

                                }
                            }

                            override fun onFailure(call: Call<NearestDriverDatas>, t: Throwable) {
                                t.printStackTrace()
//                        CToast.ShowToast(context, NC.getString(R.string.server_con_error))
//                        viewModel.nearestResponse.value = null

                            }
                        })
                )
            } else {

            }
        } else {
            CToast.ShowToast(context, NC.getString(R.string.change_network))
        }
    }

    private fun removeMarker(data: java.util.ArrayList<Marker>?) {
        if (data != null) {
            for (marker in data) {
                driverLiveMovement.removeMarkerWithAnimation(marker)
            }
            driverMarkerService.removeAll(data)
        }
    }

    fun newDriverMarker(driver: DriverData) {

        if (isValidCoordinates(driver.lat) && isValidCoordinates(driver.lng)) {
            val lat = java.lang.Double.parseDouble(driver.lat)
            val lng = java.lang.Double.parseDouble(driver.lng)
            // val carIcon = BitmapFactory.decodeResource(mContext.resources, R.drawable.car_movement_icon)

            var carIcon: Bitmap?

                carIcon = BitmapFactory.decodeResource(resources, R.drawable.top)
//
//            if (carIcon != null) {
                if (onMovingDriverMarkers.indexOfKey(Integer.parseInt(driver.driverId)) < 0) {
                    val marker = createAndGetMarker(LatLng(lat, lng), 0f, carIcon)
                    onMovingDriverMarkers.put(Integer.parseInt(driver.driverId), marker)
                }

            }
        }





    private fun isValidCoordinates(latOrLng: String): Boolean {
        return !TextUtils.isEmpty(latOrLng) && latOrLng != "0" && latOrLng != "0.0"
    }

    private fun createAndGetMarker(latLng: LatLng, bearing: Float, carIcon: Bitmap): Marker? {




        var bearing = bearing
        if (gMaps == null) {
            return null
        }
        if (bearing < 0) {
            bearing = 0f
        }
        val markerOptions = MarkerOptions().position(latLng)
        // markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_movement_icon))

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.top))


        val marker = gMaps!!.addMarker(markerOptions)
        marker!!.isFlat = true
        marker!!.rotation = bearing
        CarMovementAnimation.getInstance().addMarkerAnimate(marker)
        return marker
    }
    fun removeMarkerWithAnimation(removeMarker: Marker?) {
        if (removeMarker != null) {
            CarMovementAnimation.getInstance().removeMarkerWithAnimation(removeMarker)
        }
    }

    override fun onDestroy() {
        mTimer?.shutdown()
        super.onDestroy()

       // stopSelf()

    }

    override fun onBackPressed() {
        mTimer?.shutdown()
        super.onBackPressed()

    }

    override fun onStop() {
     //   mTimer?.shutdown()
        super.onStop()


    }



    fun findNearestlocal(detail: ArrayList<DriverData>) {





        try {
            availablecarcount = 0
            if (detail.size != 0) {
                for (i in 0 until detail.size) {
                    availablecarcount++
                }
                CommonData.mDrivermovementdata = CommonData.mDriverdata
                DriverLiveMovement(CommonData.mDrivermovementdata)
               // driverDelayHandler.removeCallbacks(driverLocationHistoryRunnable)
            //    driverDelayHandler.post { driverLocationHistoryRunnable.run() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @Synchronized
    private fun DriverLiveMovement(drivers: java.util.ArrayList<DriverData>?) {
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                if (drivers == null || onMovingDriverMarkers.size() > 0 && drivers.size == 0) {
                    //If no driver and clear the previous drivers
                    val len = onMovingDriverMarkers.size()
                    for (i in 0 until len) {
                        val key = onMovingDriverMarkers.keyAt(i)
                        //clear the marker
                        onMovingDriverMarkers.get(key).remove()
                    }
                    onMovingDriverMarkers.clear()
                } else {
                    // has driver to show or move the driver marker
                    if (onMovingDriverMarkers.size() == 0) {
                        // Initial drivers
                        for (driver in drivers) {
                            //add the marker to google map
                            newDriverMarker(driver)
                        }
                    } else {
                        // Main part add, remove , move the duplicate driver
                        val len = onMovingDriverMarkers.size()
                        // get the previous marker icons
                        onExistingDriverMarkers.clear()
                        for (i in 0 until len) {
                            onExistingDriverMarkers.add(onMovingDriverMarkers.keyAt(i))
                        }
                        for (driver in drivers) {
                            val driverId = Integer.parseInt(driver.driverId)
                            val marker = onMovingDriverMarkers.get(driverId)
                            if (marker != null) {
                                //duplicate one, so move it with bearing
                                if (isValidCoordinates(driver.lat) && isValidCoordinates(driver.lng)) {
                                    try {
                                        val lat = java.lang.Double.parseDouble(driver.lat)
                                        val lng = java.lang.Double.parseDouble(driver.lng)
                                        var bearing = 0f
                                        try {
                                            bearing = java.lang.Float.parseFloat(driver.nearest)
                                        } catch (e: NumberFormatException) {
                                            bearing = 0f
                                            e.printStackTrace()
                                        }

                                        getHeadingDirectionFromCoordinate(marker, LatLng(lat, lng), marker.position, bearing)
                                    } catch (e: Exception) {
                                        e.printStackTrace()

                                    }

                                }
                                onExistingDriverMarkers.remove(driverId)
                            } else {
                                // add new driver , Welcome driver !
                                newDriverMarker(driver)
                            }
                        }
                        //Now its time to remove out of drivers from map, Bye bye driver
                        for (previousOnRoleDriver in onExistingDriverMarkers) {
                            removeMarkerWithAnimation(onMovingDriverMarkers.get(previousOnRoleDriver))
                            onMovingDriverMarkers.remove(previousOnRoleDriver)
                        }
                        onExistingDriverMarkers.clear()
                    }
                }
            } catch (e: ConcurrentModificationException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 100)
    }

    private fun getHeadingDirectionFromCoordinate(marker: Marker, latLng: LatLng, currentposition: LatLng, bearing: Float) {
        animateMarker(marker, latLng, bearing)
    }


    private fun animateMarker(marker: Marker, newLatLng: LatLng, bearing: Float) {
        var bearing = bearing
        if (bearing < 0) {
            bearing = 0f
        }
        CarMovementAnimation.getInstance().animateMarker(marker, newLatLng, bearing)
    }

    }





