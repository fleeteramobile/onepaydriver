package com.seero.bookingmodule.DriverLiveMovement

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.text.TextUtils
import android.util.SparseArray
import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.data.CommonData
import com.onepaytaxi.driver.tracklocation.DriverData


import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList

private const val speed = "45"

class DriverLiveMove {

    private var driverDelayHandler: Handler = Handler()
    private var onMovingDriverMarkers: SparseArray<Marker> = SparseArray()
    private var onExistingDriverMarkers: ArrayList<Int> = ArrayList()
    private var availablecarcount = 0
    private lateinit var mContext: Context
    private lateinit var mFragment: Fragment
    private lateinit var detailList: ArrayList<DriverData>
    private lateinit var driverIdData: ArrayList<String>
    private var mMap: GoogleMap? = null

    init {
        driverDelayHandler = Handler()
    }

    private val driverLocationHistoryRunnable = Runnable {
        startDriverMovementSocket(driverIdData)
    }

    fun findNearestlocal(context: Context, fragment: Fragment, detail: ArrayList<DriverData>, driverData: ArrayList<String>, map: GoogleMap?) {
        mContext = context
        detailList = detail
        driverIdData = driverData
        mFragment = fragment
        mMap = map

        try {
            availablecarcount = 0
            if (detail.size != 0) {
                for (i in 0 until detail.size) {
                    availablecarcount++
                }
                CommonData.mDrivermovementdata = CommonData.mDriverdata
                DriverLiveMovement(CommonData.mDrivermovementdata)
                driverDelayHandler.removeCallbacks(driverLocationHistoryRunnable)
                driverDelayHandler.post { driverLocationHistoryRunnable.run() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun startDriverMovementSocket(driver_id: ArrayList<String>) {
        Handler(Looper.getMainLooper()).postDelayed({
            val array = JSONArray()
            for (i in driver_id.indices) {
                array.put(Integer.parseInt(driver_id[i]))
            }
            realTimeTracking(array)
        }, 200)
    }


    private fun realTimeTracking(array: JSONArray) {
//        try {
//            val data = JSONObject()
//            data.put("data", array)
//            data.put("platform", "ANDROID")
//            data.put("app", "PASS")
//            data.put("id", SessionSave.getSession("PASS_ID", mContext))
//            if (NetworkStatus.isOnline(mContext)) {
////                client = NodeServiceGenerator(mContext, SessionSave.getSession(TaxiUtil.NODE_URL, mContext), 8).createService(CoreClient::class.java)
//                val client = MyApplication.getInstance().getNodeApiManagerWithTimeOut(SessionSave.getSession(CommonData.NODE_URL, mContext), 8L)
//                val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), data.toString())
//                val coreResponse = client.getDriverCurrentLocation(body)
//                coreResponse.enqueue(RetrofitCallbackClass(mContext, object : Callback<ResponseBody> {
//                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                        val data: String
//                        var json: JSONArray? = null
//                        var driverId: String
//                        var driverName: String
//                        var lat: String
//                        var lng: String
//                        var location: String
//                        var bearing: String
//                        try {
//                            data = response.body()!!.string()
//                            if (data != null) {
//                                val jsonObject = JSONObject(data)
//                                if (jsonObject.getString("status") == "1") {
//                                    json = jsonObject.getJSONArray("data")
//                                    if (json!!.length() > 0 && availablecarcount > 0) {
//                                        TaxiUtil.mDrivermovementdata.clear()
//                                        for (i in 0 until json.length()) {
//                                            try {
//                                                driverId = json.getJSONObject(i).getString("driver_id")
//                                                driverName = ""// jarray.getJSONObject(i).getString("name");
//                                                location = json.getJSONObject(i).getString("locations")
//                                                location = location.substring(0, location.length - 1)
//                                                val latlong = location.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//                                                lat = latlong[0]
//                                                lng = latlong[1]
//                                                var length = -1
//                                                length = lng.indexOf("|")
//                                                if (length != -1) {
//                                                    lng = lng.substring(0, length)
//                                                }
//                                                bearing = if (json.getJSONObject(i).has("bearings")) {
//                                                    json.getJSONObject(i).getString("bearings")
//                                                } else {
//                                                    "0"
//                                                }
//                                                val mDriverData = DriverData(driverId, driverName, speed, lat, lng, bearing, "", null, null)
//                                                CommonData.mDrivermovementdata.add(mDriverData)
//                                            } catch (e: JSONException) {
//                                                e.printStackTrace()
//                                            }
//
//                                        }
//                                        DriverLiveMovement(TaxiUtil.mDrivermovementdata)
//                                        if (driverDelayHandler != null) {
//                                            driverDelayHandler.removeCallbacks(driverLocationHistoryRunnable)
//                                            if (mFragment.isAdded) {
//                                                val fragment = mFragment.requireActivity().supportFragmentManager.findFragmentById(R.id.mainFrag)
//                                                if (fragment is BookTaxiHomePage && fragment.isResumed) {
//                                                    driverDelayHandler.postDelayed(driverLocationHistoryRunnable, 4000)
//                                                }
//                                            }
//                                        }
//                                    } else {
//                                        DriverLiveMovement(null)
//                                        driverDelayHandler.postDelayed(driverLocationHistoryRunnable, 15000)
//                                    }
//                                }
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }
//
//                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                        t.printStackTrace()
//                    }
//                }))
//            }
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            driverDelayHandler.removeCallbacks(driverLocationHistoryRunnable)
//        }

    }


    /**
     * Driver marker Logic
     *
     * @param drivers current response driver
     */

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


    /**
     * Add new Marker
     *
     * @param driver driver information
     */
    private fun newDriverMarker(driver: DriverData) {
        println("driver_data_testing"+ " "+ driver.taxiModel)
        if (isValidCoordinates(driver.lat) && isValidCoordinates(driver.lng)) {
            val lat = java.lang.Double.parseDouble(driver.lat)
            val lng = java.lang.Double.parseDouble(driver.lng)
           // val carIcon = BitmapFactory.decodeResource(mContext.resources, R.drawable.car_movement_icon)

             var carIcon: Bitmap?


                 carIcon = BitmapFactory.decodeResource(mContext.resources, R.drawable.top)

            if (carIcon != null) {
                if (onMovingDriverMarkers.indexOfKey(Integer.parseInt(driver.driverId)) < 0) {
                    val marker = createAndGetMarker(LatLng(lat, lng), 0f, carIcon)
                    onMovingDriverMarkers.put(Integer.parseInt(driver.driverId), marker)
                }
            }
        }
    }

    /**
     * Check empty, 0
     *
     * @param latOrLng src coordinates
     */
    private fun isValidCoordinates(latOrLng: String): Boolean {
        return !TextUtils.isEmpty(latOrLng) && latOrLng != "0" && latOrLng != "0.0"
    }

    private fun createAndGetMarker(latLng: LatLng, bearing: Float, carIcon: Bitmap): Marker? {
        var bearing = bearing
        if (mMap == null) {
            return null
        }
        if (bearing < 0) {
            bearing = 0f
        }
        val markerOptions = MarkerOptions().position(latLng)
     // markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_movement_icon))

            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.top))


        val marker = mMap!!.addMarker(markerOptions)
//        marker.isFlat = true
//        marker.rotation = bearing
//        CarMovementAnimation.getInstance().addMarkerAnimate(marker)
        return marker
    }

    private fun getHeadingDirectionFromCoordinate(marker: Marker, latLng: LatLng, currentposition: LatLng, bearing: Float) {
        animateMarker(marker, latLng, bearing)
    }

    private fun animateMarker(marker: Marker, newLatLng: LatLng, bearing: Float) {
        var bearing = bearing
        if (bearing < 0) {
            bearing = 0f
        }
      //  CarMovementAnimation.getInstance().animateMarker(marker, newLatLng, bearing)
    }

    fun removeMarkerWithAnimation(removeMarker: Marker?) {
        if (removeMarker != null) {
           // CarMovementAnimation.getInstance().removeMarkerWithAnimation(removeMarker)
        }
    }

    fun removeDriverLiveMovementCallback() {
        if (driverDelayHandler != null)
            driverDelayHandler.removeCallbacks(driverLocationHistoryRunnable)
    }

    fun removeDriverLiveMovement() {
        DriverLiveMovement(null)
    }

    fun removeStartDriverLiveMovement() {
        if (driverDelayHandler != null) {
            driverDelayHandler.removeCallbacks(driverLocationHistoryRunnable)
            driverDelayHandler.postDelayed(driverLocationHistoryRunnable, 4000)
        }
    }

}