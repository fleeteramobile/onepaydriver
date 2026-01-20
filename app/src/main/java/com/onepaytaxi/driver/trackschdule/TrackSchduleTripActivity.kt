package com.onepaytaxi.driver.trackschdule

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.imageview.ShapeableImageView
import com.onepaytaxi.driver.OngoingAct

import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.data.CommonData
import com.onepaytaxi.driver.interfaces.APIResult
import com.onepaytaxi.driver.interfaces.ClickInterface
import com.onepaytaxi.driver.interfaces.LocalDistanceInterface
import com.onepaytaxi.driver.route.Route
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON
import com.onepaytaxi.driver.service.LocationUpdate
import com.onepaytaxi.driver.service.NonActivity
import com.onepaytaxi.driver.utils.CToast.ShowToast
import com.onepaytaxi.driver.utils.LocationUtils
import com.onepaytaxi.driver.utils.NC
import com.onepaytaxi.driver.utils.NetworkStatus
import com.onepaytaxi.driver.utils.SessionSave
import com.onepaytaxi.driver.utils.Utils
import com.squareup.picasso.Picasso
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class TrackSchduleTripActivity : AppCompatActivity(), OnMapReadyCallback, LocalDistanceInterface,
    GoogleMap.OnCameraMoveStartedListener, ClickInterface, TrackMovement {
    var trip_id = ""
    var pick_up_lat = ""
    var pick_up_lang = ""
    var p_logid = ""
    var p_name = ""
    var p_pickloc = ""
    var p_droploc = ""
    var p_picklat = ""
    var p_picklng = ""
    var p_droplat = ""
    var p_droplng = ""
    var p_driverlat = ""
    var p_driverlng = ""
    var myOtoMetter: Dialog? = null

    private var p_image = ""
    private var p_phone = ""
    private var p_notes = ""
    private var p_driverstatus = ""
    private var p_taxi_speed: String? = ""
    private var pickup_notes: String? = ""
    private var dropoff_notes: String? = ""
    private var p_travelstatus: String? = ""
    private var booking_Type: String? = ""
    private var model_id: String? = ""
    private var payment_type_label: String? = ""
    private var mroute: String? = null
    var enable_os_waiting_fare = false

    private lateinit var profile_avatar: ShapeableImageView
    private lateinit var origin_name: TextView
    private lateinit var origin_address: TextView
    private lateinit var destination_name: TextView
    private lateinit var destination_address: TextView
    private lateinit var profile_name: TextView
    private lateinit var passnameTxt: TextView
    private lateinit var payment_method_button: Button
    private lateinit var btn_trip_update: Button
    private lateinit var btn_call: ImageButton
    private var p_marker: Marker? = null
    private var d_marker: Marker? = null

    private var myOTPDialog: Dialog? = null

    var polygonPoints: ArrayList<LatLng?> = ArrayList<LatLng?>()

    private val nonactiityobj = NonActivity()
    private var googleMap: GoogleMap? = null
    var zoom = 17f
    var bearing: Float = 0f
    var bearings: Float = 0f

    private var route: Route? = null

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLastLocation: Location? = null
    private var latitude1 = 0.0
    private var longitude1 = 0.0
    private var speed = 0.0
    private var carMarker: Marker? = null
    private var previousLatLng: LatLng? = null
    private var locationService: LocationUpdate? = null
    private var bound = false
    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationUpdate.LocalBinder
            locationService = binder.service
            locationService!!.setMoveTrack(this@TrackSchduleTripActivity) // Register callback
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            bound = false
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_schdule_trip)

        val root = findViewById<View>(R.id.rootLayout)

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        trip_id = intent.getStringExtra("schdule_trip_id").toString()
        booking_Type = intent.getStringExtra("booking_Type").toString()
        pick_up_lat = intent.getStringExtra("pick_up_lat").toString()
        pick_up_lang = intent.getStringExtra("pick_up_lang").toString()


        route = Route()
        createLocationRequest()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as? SupportMapFragment

        val j = JSONObject()
        j.put("trip_id", trip_id)
        val Url = "type=get_trip_detail"
        Tripdetails(Url, j)

        profile_avatar = findViewById(R.id.profile_avatar)
        origin_name = findViewById(R.id.origin_name)
        origin_address = findViewById(R.id.origin_address)
        destination_name = findViewById(R.id.destination_name)
        destination_address = findViewById(R.id.destination_address)
        profile_name = findViewById(R.id.profile_name)
        payment_method_button = findViewById(R.id.payment_method_button)
        btn_trip_update = findViewById(R.id.btn_trip_update)
        btn_call = findViewById(R.id.btn_call)
        btn_call.setOnClickListener {
            openDialer(p_phone)
        }
        btn_trip_update.setOnClickListener {

            val j = JSONObject()
            j.put("trip_id", trip_id)
            val Url = "type=send_booking_otp"
            createOTP(Url, j)
        }
        if (mapFragment != null) {
            mapFragment.getMapAsync(this)
        } else {
            Toast.makeText(this, "Map fragment not found!", Toast.LENGTH_LONG).show()
        }

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
            Toast.makeText(this, "No dialer app found to handle this request.", Toast.LENGTH_SHORT).show()
        }
    }

    inner class Tripdetails(url: String, data: JSONObject) : APIResult {
        init {
            APIService_Retrofit_JSON(this@TrackSchduleTripActivity, this, data, false).execute(url)

        }

        override fun getResult(isSuccess: Boolean, result: String?) {
            val json = JSONObject(result)
            println("workload" + " " + "samdinasndansdnasklscla")
            if (json.getInt("status") == 1) {
                val detail = json.getJSONObject("detail")



                p_logid = detail.getString("trip_id")
                p_name = detail.getString("passenger_name")
                p_pickloc = detail.getString("current_location")
                p_droploc = detail.getString("drop_location")
                p_picklat = detail.getString("pickup_latitude")
                p_picklng = detail.getString("pickup_longitude")
                p_droplat = detail.getString("drop_latitude")
                p_droplng = detail.getString("drop_longitude")
                p_driverlat = detail.getString("driver_latitute")
                p_driverlng = detail.getString("driver_longtitute")
                p_travelstatus = detail.getString("travel_status")
                p_driverstatus = detail.getString("driver_status")
                p_notes = detail.getString("notes")
                p_phone = detail.getString("passenger_phone")
                p_image = detail.getString("passenger_image")
                p_taxi_speed = detail.getString("taxi_min_speed")
                pickup_notes = detail.getString("pickup_notes")
                dropoff_notes = detail.getString("dropoff_notes")
                booking_Type = detail.getString("trip_type")
                model_id = detail.getString("model_id")
                payment_type_label = detail.getString("payment_type_label")
                println("Location_changes_view_pickupLng_1] ${p_picklat},${p_picklng}")

                println("p_droploc" + " " + p_droploc)

                profile_name.setText(p_name)
                val pickupCity: String = getCityName(
                    this@TrackSchduleTripActivity,
                    p_picklat.toDouble(),
                    p_picklng.toDouble()
                )
                val dropCity: String = getCityName(
                    this@TrackSchduleTripActivity,
                    p_droplat.toDouble(),
                    p_droplng.toDouble()
                )
                origin_name.text = pickupCity
                destination_name.text = dropCity
                Picasso.get().load(p_image)
                    .placeholder(getResources().getDrawable(R.drawable.loadingimage))
                    .error(getResources().getDrawable(R.drawable.flag_green)).into(profile_avatar)

                origin_address.text = p_pickloc
                destination_address.text = p_droploc
                println("worked_city_limit"+ " "+ "1")


                // Check if the 'detail' object has 'citylimit_data'

            }


        }
    }

    inner class createOTP(url: String, data: JSONObject) : APIResult {
        init {
            APIService_Retrofit_JSON(this@TrackSchduleTripActivity, this, data, false).execute(url)

        }

        override fun getResult(isSuccess: Boolean, result: String?) {
            val json = JSONObject(result)
            println("workload" + " " + "samdinasndansdnasklscla")
            if (json.getInt("status") == 1) {

                showOtp(this@TrackSchduleTripActivity)

            }


        }
    }

    private fun getCityName(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var cityName = ""
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && !addresses.isEmpty()) {
                val address = addresses[0]
                println("city1_address ${address}")
                cityName = address.locality // Preferred
                if (cityName == null) {
                    cityName = address.subAdminArea // Fallback
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return cityName ?: ""
    }

    private fun createLocationRequest() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS)
        mLocationRequest!!.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS)
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)


    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        try {
            MapsInitializer.initialize(this)

            googleMap?.apply {
                uiSettings.isZoomControlsEnabled = false
                uiSettings.isCompassEnabled = false
                uiSettings.isMyLocationButtonEnabled = false
                isMyLocationEnabled = false
                setPadding(0, 0, 0, 200) // adjust for bottom panel
                mapType = GoogleMap.MAP_TYPE_NORMAL
            }

            val currentLat = LocationUpdate.currentLatitude
            val currentLng = LocationUpdate.currentLongtitude

            // Move camera
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(currentLat, currentLng), zoom)
            )

            // Get pickup/drop from booking
            val pickupLat = pick_up_lat.toDouble()
            val pickupLng = pick_up_lang.toDouble()
//            val dropLat = drop_lat.toDouble()
//            val dropLng = drop_lang.toDouble()

            // Draw route from current -> pickup
            getRoute(currentLat, currentLng, pickupLat, pickupLng)

            // Car marker (moving)
//            carMarker = map.addMarker(
//                MarkerOptions()
//                    .position(LatLng(currentLat, currentLng))
//                    .rotation(0f)
//                    .anchor(0.5f, 0.5f)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.top))
//            )
            addCarMarker(currentLat, currentLng)

            // Pickup marker
            p_marker = map.addMarker(
                MarkerOptions()
                    .position(LatLng(currentLat, currentLng))
                    .title("Driver Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_green))
                    .draggable(false)
            )

            // Drop marker
            d_marker = map.addMarker(
                MarkerOptions()
                    .position(LatLng(pickupLat, pickupLng))
                    .title("User Location ")
                    .draggable(false)
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun haversineResult(success: Boolean?) {

    }

    override fun onCameraMoveStarted(p0: Int) {

    }

    override fun positiveButtonClick(dialog: DialogInterface?, id: Int, s: String?) {

    }

    override fun negativeButtonClick(dialog: DialogInterface?, id: Int, s: String?) {

    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }

    private fun getRoute(
        currentLat: Double,
        currentLng: Double,
        pickupLat: Double,
        pickupLng: Double
    ) {
        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=$currentLat,$currentLng&destination=$pickupLat,$pickupLng&" +
                "mode=driving&key=${
                    SessionSave.getSession(
                        CommonData.GOOGLE_KEY,
                        this@TrackSchduleTripActivity
                    )
                }"
        println("Location_changes_view_pickupLng ${url}")

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                val jsonObj = JSONObject(json)
                val routes = jsonObj.getJSONArray("routes")

                if (routes.length() > 0) {
                    val points = routes.getJSONObject(0)
                        .getJSONObject("overview_polyline")
                        .getString("points")

                    val polyline = decodePolyline(points)

                    runOnUiThread {
                        googleMap?.addPolyline(
                            PolylineOptions()
                                .addAll(polyline)
                                .width(10f)
                                .color(Color.BLUE)
                                .geodesic(true)
                        )

                        // Adjust camera
                        val bounds = LatLngBounds.builder()
                         for (latLng in polyline) {
                            bounds.include(latLng)
                        }
                        googleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                bounds.build(),
                                100
                            )
                        )
                    }
                }
            }
        })
    }

    private fun addCarMarker(startLat: Double, startLng: Double) {
        val startPosition = LatLng(startLat, startLng)
        carMarker = googleMap?.addMarker(
            MarkerOptions()
                .position(startPosition)
                .flat(true)
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.top))
        )
        previousLatLng = startPosition
    }

    // Smoothly move car to new location
    private fun moveCar(newLat: Double, newLng: Double) {
        val newLatLng = LatLng(newLat, newLng)

        if (carMarker == null) {
            addCarMarker(newLat, newLng)
            return
        }
        println("Move_ani_23  ${newLat}${newLng}")

        val oldLatLng = previousLatLng ?: newLatLng
        previousLatLng = newLatLng

        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 2000  // match location update interval
        valueAnimator.interpolator = LinearInterpolator()

        valueAnimator.addUpdateListener { animation ->
            val v = animation.animatedFraction
            val lng = v * newLatLng.longitude + (1 - v) * oldLatLng.longitude
            val lat = v * newLatLng.latitude + (1 - v) * oldLatLng.latitude
            val newPos = LatLng(lat, lng)

            // Move marker
            carMarker?.position = newPos

            // Rotate smoothly
            carMarker?.rotation = getBearing(oldLatLng, newLatLng).toFloat()

            // Keep camera centered
            googleMap?.moveCamera(CameraUpdateFactory.newLatLng(newPos))
        }
        valueAnimator.start()
    }


    // Bearing calculation for car rotation
    private fun getBearing(begin: LatLng, end: LatLng): Double {
        val lat1 = Math.toRadians(begin.latitude)
        val lng1 = Math.toRadians(begin.longitude)
        val lat2 = Math.toRadians(end.latitude)
        val lng2 = Math.toRadians(end.longitude)

        val dLng = lng2 - lng1
        val y = sin(dLng) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLng)
        return (Math.toDegrees(atan2(y, x)) + 360) % 360
    }


    override fun onStop() {
        super.onStop()
        if (bound) {
            unbindService(connection)
            bound = false
        }
    }

    override fun carMoveAnimation(latitude: Double, longitude: Double) {
        println("Move_ani   1")
        println("Move_ani  ${latitude}${longitude}")
        runOnUiThread {
            moveCar(latitude, longitude)
        }

    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, LocationUpdate::class.java)
        bindService(intent, connection, BIND_AUTO_CREATE)
    }

    fun showOtp(mContext: Context?) {
        val view1 = View.inflate(mContext, R.layout.odometer_otp_input, null)
        if (myOTPDialog != null && myOTPDialog!!.isShowing()) myOTPDialog!!.cancel()
        myOTPDialog = Dialog(mContext!!, R.style.NewDialog)
        myOTPDialog!!.setContentView(view1)
        myOTPDialog!!.setCancelable(false)
        myOTPDialog!!.setCanceledOnTouchOutside(false)
        myOTPDialog!!.setCancelable(true)
        myOTPDialog!!.show()
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(myOTPDialog!!.getWindow()!!.getAttributes())
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        myOTPDialog!!.getWindow()!!.setAttributes(layoutParams)


//
        val btn_confirm: LinearLayout = myOTPDialog!!.findViewById<LinearLayout>(R.id.btn_confirm)
        val verifyno1Txt: EditText = myOTPDialog!!.findViewById<EditText>(R.id.verifyno1Txt)
        val verifyno2Txt: EditText = myOTPDialog!!.findViewById<EditText>(R.id.verifyno2Txt)
        val verifyno3Txt: EditText = myOTPDialog!!.findViewById<EditText>(R.id.verifyno3Txt)
        val verifyno4Txt: EditText = myOTPDialog!!.findViewById<EditText>(R.id.verifyno4Txt)
        verifyno1Txt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
                if (s.toString().trim { it <= ' ' }.length == 1) {
                    verifyno2Txt.requestFocus()
                    verifyno2Txt.setText("")
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }
        })
        verifyno2Txt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
                if (s.toString().trim { it <= ' ' }.length == 1) {
                    verifyno3Txt.requestFocus()
                    verifyno3Txt.setText("")
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }
        })
        verifyno3Txt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
                if (s.toString().trim { it <= ' ' }.length == 1) {
                    verifyno4Txt.requestFocus()
                    verifyno4Txt.setText("")
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }
        })
        btn_confirm.setOnClickListener {
            if (verifyno1Txt.getText().toString() == "") {
                Toast.makeText(
                    mContext,
                    "Enter first number",
                    Toast.LENGTH_LONG
                ).show()
            } else if (verifyno2Txt.getText().toString() == "") {
                Toast.makeText(
                    mContext,
                    "Enter second number",
                    Toast.LENGTH_LONG
                ).show()
            } else if (verifyno3Txt.getText().toString() == "") {
                Toast.makeText(
                    mContext,
                    "Enter third number",
                    Toast.LENGTH_LONG
                ).show()
            } else if (verifyno4Txt.getText().toString() == "") {
                Toast.makeText(
                    mContext,
                    "Enter fourth number",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                myOTPDialog!!.dismiss()
                val otpnumber = verifyno1Txt.getText().toString() + verifyno2Txt.getText()
                    .toString() + verifyno3Txt.getText().toString() + verifyno4Txt.getText()
                    .toString()
                val url = "type=booking_otp_verify"
                updateOTP(url, "3", otpnumber)
            }
        }
    }

    inner class updateOTP internal constructor(
        url: String?,
        type: String?,
        odometer_number: String?
    ) :
        APIResult {
        init {
            try {
                val j = JSONObject()
                j.put("trip_id", trip_id)
                j.put("otp", odometer_number)
                APIService_Retrofit_JSON(this@TrackSchduleTripActivity, this, j, false).execute(url)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        override fun getResult(isSuccess: Boolean, result: String?) {
            try {
                if (isSuccess) {
                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {
                        run {
                            SessionSave.saveSession(
                                "otp_enter",
                                "no",
                                this@TrackSchduleTripActivity
                            )
                            SessionSave.saveSession(
                                "odameter_status",
                                "2",
                                this@TrackSchduleTripActivity
                            )
                            //showodometer();
                            if (booking_Type.equals("1")) {
                                try {
                                    val j = JSONObject()
                                    j.put("trip_id", trip_id)
                                    j.put(
                                        "driver_id",
                                        SessionSave.getSession("Id", this@TrackSchduleTripActivity)
                                    )
                                    j.put(
                                        "pickup_latitude",
                                        p_picklat

                                    )
                                    j.put(
                                        "pickup_longitude",
                                        p_picklng
                                    )
                                    val scheduleTripUrl = "type=schedule_start_trip"
                                    NonActivity().stopServicefromNonActivity(this@TrackSchduleTripActivity)
                                    ScheduleStartTrip(scheduleTripUrl, j)
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                showodometer()
                            }


                        }
                    } else {
                        // dialog1 = Utils.alert_view(mContext, NC.getResources().getString(R.string.message),json.getString("message") , NC.getResources().getString(R.string.ok), "", true, mContext, "4");
                        Utils.alert_view(
                            this@TrackSchduleTripActivity,
                            NC.getString(R.string.message),
                            json.getString("message"),
                            NC.getString(R.string.ok),
                            "",
                            true,
                            this@TrackSchduleTripActivity,
                            "4"
                        )
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun showodometer() {
        val view1 = View.inflate(this@TrackSchduleTripActivity, R.layout.odometer_input, null)
        if (myOtoMetter != null && myOtoMetter!!.isShowing()) myOtoMetter!!.cancel()
        myOtoMetter = Dialog(this@TrackSchduleTripActivity, R.style.NewDialog)
        myOtoMetter!!.setContentView(view1)
        myOtoMetter!!.setCancelable(false)
        myOtoMetter!!.setCanceledOnTouchOutside(false)
        myOtoMetter!!.setCancelable(true)
        myOtoMetter!!.show()
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(myOtoMetter!!.getWindow()!!.getAttributes())
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        myOtoMetter!!.getWindow()!!.setAttributes(layoutParams)


//
        val btn_confirm: LinearLayout = myOtoMetter!!.findViewById<LinearLayout>(R.id.btn_confirm)
        val odameter_heading: TextView = myOtoMetter!!.findViewById<TextView>(R.id.odameter_heading)
        val verifyno1Txt: EditText = myOtoMetter!!.findViewById<EditText>(R.id.verifyno1Txt)
        if (SessionSave.getSession("odameter_status", this@TrackSchduleTripActivity) == "2") {
            odameter_heading.text = "Start  Reading"
        } else if (SessionSave.getSession(
                "odameter_status",
                this@TrackSchduleTripActivity
            ) == "3"
        ) {
            odameter_heading.text = "End Reading"
        } else if (SessionSave.getSession(
                "odameter_status",
                this@TrackSchduleTripActivity
            ) == "3"
        ) {
            odameter_heading.text = "Accept Reading"
        }



        btn_confirm.setOnClickListener {
            if (verifyno1Txt.getText().toString() == "") {
                Toast.makeText(
                    this@TrackSchduleTripActivity,
                    "Enter first number",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                myOtoMetter!!.dismiss()
                val otpnumber = verifyno1Txt.getText().toString()
                val url = "type=new_update_odometer"
                updateOdaMeter(url, "2", otpnumber)
            }
        }
    }




    inner class updateOdaMeter internal constructor(
        url: String?,
        type: String?,
        odometer_number: String?
    ) :
        APIResult {
        init {
            try {
                val j = JSONObject()
                j.put("driver_id", SessionSave.getSession("Id", this@TrackSchduleTripActivity))
                j.put("trip_id", trip_id)
                j.put("odometer_number", odometer_number)
                j.put("level", type)
                APIService_Retrofit_JSON(this@TrackSchduleTripActivity, this, j, false).execute(url)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        @SuppressLint("MissingPermission")
        override fun getResult(isSuccess: Boolean, result: String) {
            try {
                if (isSuccess) {


                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {
                        run {
                            try {
                                val j = JSONObject()
                                j.put("trip_id", trip_id)
                                j.put(
                                    "driver_id",
                                    SessionSave.getSession("Id", this@TrackSchduleTripActivity)
                                )
                                j.put(
                                    "pickup_latitude",
                                    p_picklat

                                )
                                j.put(
                                    "pickup_longitude",
                                    p_picklng
                                )
                                val scheduleTripUrl = "type=schedule_start_trip"
                                NonActivity().stopServicefromNonActivity(this@TrackSchduleTripActivity)
                                ScheduleStartTrip(scheduleTripUrl, j)
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        Utils.alert_view(
                            this@TrackSchduleTripActivity,
                            NC.getString(R.string.message),
                            json.getString("message"),
                            NC.getString(R.string.ok),
                            "",
                            true,
                            this@TrackSchduleTripActivity,
                            "4"
                        )
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }


    inner class ScheduleStartTrip internal constructor(
        url: String?,
        data: JSONObject?

    ) :
        APIResult {
        init {
            try {
                if (NetworkStatus.isOnline(this@TrackSchduleTripActivity)) {
                    APIService_Retrofit_JSON(
                        this@TrackSchduleTripActivity,
                        this,
                        data,
                        false
                    ).execute(url)
                } else {
                    Utils.alert_view(
                        this@TrackSchduleTripActivity,
                        NC.getString(R.string.message),
                        NC.getString(R.string.check_net_connection),
                        NC.getString(R.string.ok),
                        "",
                        true,
                        this@TrackSchduleTripActivity,
                        "4"
                    )
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        override fun getResult(isSuccess: Boolean, result: String) {
            if (isSuccess) {
                NonActivity().startServicefromNonActivity(this@TrackSchduleTripActivity)
                try {
                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {
                        SessionSave.saveSession(
                            "trip_id",
                            json.getString("trip_id"),
                            this@TrackSchduleTripActivity
                        )
                        SessionSave.saveSession(
                            "status",
                            json.getString("driver_status"),
                            this@TrackSchduleTripActivity
                        )
                        SessionSave.saveSession(
                            "travel_status",
                            json.getString("travel_status"),
                            this@TrackSchduleTripActivity
                        )
                        if (SessionSave.getSession("shift_status", this@TrackSchduleTripActivity)
                                .equals("IN", ignoreCase = true)
                        ) {
                            SessionSave.saveSession(
                                "trip_id",
                                trip_id,
                                this@TrackSchduleTripActivity
                            )
                            val `in` = Intent(
                                this@TrackSchduleTripActivity,
                                OngoingAct::class.java
                            )
                            intent.putExtra("from_activity", "TrackScheduleTripActivity")

                            startActivity(`in`)
                        } else {
                            ShowToast(
                                this@TrackSchduleTripActivity,
                                NC.getString(R.string.track_shift_status)
                            )
                        }
                    } else if (json.getInt("status") == -2) {


                    } else {
                        ShowToast(this@TrackSchduleTripActivity, json.getString("message"))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                // CToast.ShowToast(mContext, NC.getString(R.string.server_error));
                NonActivity().startServicefromNonActivity(this@TrackSchduleTripActivity)
            }
        }
    }
}