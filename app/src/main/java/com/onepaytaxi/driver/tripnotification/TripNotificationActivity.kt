package com.bluetaxi.driver.tripnotification // Make sure this matches your actual package name

// import com.google.android.gms.maps.MapView // This import is not needed, as you're using SupportMapFragment
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.onepaytaxi.driver.MainActivity
import com.onepaytaxi.driver.OngoingAct
import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.data.CommonData
import com.onepaytaxi.driver.errorLog.ApiErrorModel
import com.onepaytaxi.driver.errorLog.ErrorLogRepository.Companion.getRepository
import com.onepaytaxi.driver.homepage.HomePageActivity
import com.onepaytaxi.driver.interfaces.APIResult
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON
import com.onepaytaxi.driver.utils.CToast
import com.onepaytaxi.driver.utils.DriverUtils.driverInfo
import com.onepaytaxi.driver.utils.ExceptionConverter.buildStackTraceString
import com.onepaytaxi.driver.utils.NC
import com.onepaytaxi.driver.utils.NetworkStatus
import com.onepaytaxi.driver.utils.SessionSave
import com.onepaytaxi.driver.utils.Systems
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

class TripNotificationActivity : AppCompatActivity(), OnMapReadyCallback {
    // UI elements
    private lateinit var acceptButton: Button
    private lateinit var pickupCityTxt: TextView
    private lateinit var dropCityTxt: TextView
    private lateinit var pickupLocationTXt: TextView
    private lateinit var dropLocationTxt: TextView
    private lateinit var timeTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var trip_type_txt: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var progressBar: ProgressBar // This is the spinner for the accept button
    private lateinit var closeButton: ImageButton
    private lateinit var requestTimerProgressBar: ProgressBar // The line progress bar
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var polyline: Polyline? = null // For drawing the route

    // Google Map instance
    private var googleMap: GoogleMap? = null

    // Handler and Runnable for the progress animation
    private var progressHandler: Handler? = null
    private var progressRunnable: Runnable? = null

    // Configuration for the dynamic timer
    private val DYNAMIC_TOTAL_TIME_MILLIS = 15000L // Example: 15 seconds for total progress
    private val PROGRESS_INTERVAL_MILLIS = 100L // Update every 100 milliseconds
    var nActivity: AppCompatActivity? = null
    var bun: Bundle? = null
    var pickup_time: String? = null
    var profile_image: String? = null

    var message: String? = null
    var distance: String? = null
    var passenger_id: String? = null
    var time_out = 0

    private var passenger_phone: String? = null
    private var cityname: String? = null
    private var passenger_name: String? = null
    private var notes: String? = null
    private var estimate_amount: String? = null
    private var est_distance: String? = null
    private var eta_time: String? = null
    private var stops_count: String? = null
    private var trip_id = ""
    private var pickup: String? = null
    private var drop: String? = null
    private var bookedby: String? = null
    private var nowAfter = -1
    private var model_name: String? = null
    private var trip_type: String? = null
    private var pickup_lat = 0.0
    private var pickup_lng: Double = 0.0
    private var drop_lattitude = 0.0
    private var drop_longitude = 0.0
    private var pickupCity: String? = null
    private var dropCity: String? = null

    // Member variables that LatlongValue will populate
    private var pick_lat: Double? = null
    private var pick_long: Double? = null
    private var current_lattitude: Double? = null
    private var current_longitude: Double? = null

    private lateinit var directionsApiService: DirectionsApiService
    private var animatedPolyline: Polyline? = null // New: for the animation itself

    private var originPulseCircle: Circle? = null
    private var destinationPulseCircle: Circle? = null
    private var pulseAnimatorSet: AnimatorSet? = null

    private var polylineDrawingHandler: Handler? = null
    private var polylineDrawingRunnable: Runnable? = null
    private var polylineIndex = 0 // Also needs to be a class member for state persistence
    private var animatedCarMarker: Marker? = null // Add this line
    private var mPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_notification)



        bun = intent.extras
        nActivity = this

        CommonData.current_trip_accept = 1
        message = bun!!.getString("message")

        // Initialize Retrofit for Directions API
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Log network requests/responses
        }
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient) // Add the client with logging
            .build()
        directionsApiService = retrofit.create(DirectionsApiService::class.java)




        // 1. Initialize the Map Fragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragmentContainer) as? SupportMapFragment

        // 2. Request the map asynchronously
        if (mapFragment != null) {
            mapFragment.getMapAsync(this)
        } else {
            Toast.makeText(this, "Map fragment not found!", Toast.LENGTH_LONG).show()
        }

        // Initialize other UI elements
        acceptButton = findViewById(R.id.acceptButton)
        pickupCityTxt = findViewById(R.id.pickup_city)
        dropCityTxt = findViewById(R.id.drop_city)
        pickupLocationTXt = findViewById(R.id.pickup_location)
        dropLocationTxt = findViewById(R.id.drop_location)
        timeTextView = findViewById(R.id.timeTextView)
        priceTextView = findViewById(R.id.priceTextView)
        trip_type_txt = findViewById(R.id.trip_type_txt)
        distanceTextView = findViewById(R.id.distanceTextView)
        progressBar = findViewById(R.id.progressBar) // For the accept button's loading state
        closeButton = findViewById(R.id.closeButton)
        requestTimerProgressBar =
            findViewById(R.id.requestTimerProgressBar) // Initialize the line progress bar

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        loadUI()

        startProgressAnimation()
        tone_play()
        // Set OnClickListener for the Accept button
        acceptButton.setOnClickListener {
            // Stop the progress animation when the user interacts
            stopProgressAnimation()

            // Show spinner progress bar and disable button
            progressBar.visibility = View.VISIBLE
            acceptButton.text = "" // Optionally clear text
            acceptButton.isEnabled = false
            acceptTripFun()

        }

        // Set OnClickListener for the Close button
        closeButton.setOnClickListener {
            callDeclineAPi()
            stopProgressAnimation()
            tone_stop()
        }


    }


    private fun callDeclineAPi() {
        try {
            if (NetworkStatus.isOnline(this@TripNotificationActivity)) {

                val j = JSONObject()
                j.put("trip_id", trip_id)
                j.put("driver_id", SessionSave.getSession("Id", this@TripNotificationActivity))
                j.put("taxi_id", SessionSave.getSession("taxi_id", this@TripNotificationActivity))
                j.put(
                    "company_id",
                    SessionSave.getSession("company_id", this@TripNotificationActivity)
                )
                j.put("reason", "")
                j.put("reject_type", "1")
                val Url = "type=reject_trip"
                TripReject(Url, j, 2)
            } else {

                CToast.ShowToast(
                    this@TripNotificationActivity,
                    NC.getString(R.string.check_net_connection)
                )
                finish()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun acceptTripFun() {
        try {
            var latitude = 0.0
            var longitude = 0.0



            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        // Handle the new location here
                        latitude = location.latitude
                        latitude = location.longitude
                        Log.d(
                            "LocationTracker",
                            "Current Location: Lat = $latitude, Lng = $longitude"
                        )
                        Toast.makeText(
                            this@TripNotificationActivity,
                            "Lat: $latitude, Lng: $latitude", Toast.LENGTH_SHORT
                        ).show()

                    }
                }

                override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                    // Handle changes in location availability (e.g., GPS turned off)
                    Log.d(
                        "LocationTracker",
                        "Location available: ${locationAvailability.isLocationAvailable}"
                    )
                    if (!locationAvailability.isLocationAvailable) {
                        Toast.makeText(
                            this@TripNotificationActivity,
                            "Location not available. Please check settings.",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

            MainActivity.mMyStatus.settripId(trip_id)
            SessionSave.saveSession("trip_id", trip_id, this)
            MainActivity.mMyStatus.setpassengerId(trip_id)
            val j = JSONObject()
            j.put("pass_logid", trip_id)
            j.put("driver_id", SessionSave.getSession("Id", this))
            j.put("taxi_id", SessionSave.getSession("taxi_id", this))
            j.put("company_id", SessionSave.getSession("company_id", this))
            j.put("driver_reply", "A")
            j.put("field", "rejection")
            j.put("flag", "0")
            j.put("latitude", latitude)
            j.put("longitude", longitude)
            val Url = "type=driver_reply"
            Systems.out.println("result" + "Sucess")
            TripAccept(Url, j)


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun loadUI() {
        Log.d("Driver_location_request", "test")
        try {
            message?.let { jsonString ->
                val rootJson = JSONObject(jsonString) // Renamed to rootJson for clarity
                Log.d("Driver_location_request", rootJson.toString())

                // Now, "trip_details" is a JSONObject at the root level
                val tripdetails = rootJson.getJSONObject("trip_details")
                //   Log.d("Driver_location_request_tripdetails", tripdetails.toString())

                // All these fields are now under "trip_details"
                trip_id = tripdetails.optString("passengers_log_id", "")
                //  Log.d("Driver_location_request_trip_id", trip_id)

                time_out = tripdetails.optInt("notification_time", 0) // Default to 0 if not found
                notes = tripdetails.optString("notes", "")
                estimate_amount = tripdetails.optString("approx_fare", "")

                val pickup_notes = tripdetails.optString("pickup_notes", "")
                val drop_notes = tripdetails.optString("dropoff_notes", "")
                est_distance = tripdetails.optString("approx_distance", "")
                eta_time = tripdetails.optString("approx_distance", "")
                trip_type = tripdetails.optString("trip_type", "")

                updateLayoutStates(trip_type!!.toInt())
                stops_count = tripdetails.optString("stops", "0")

                if (tripdetails.has(CommonData.SHOW_CANCEL_BUTTON)) {
                    SessionSave.saveSession(
                        CommonData.SHOW_CANCEL_BUTTON,
                        tripdetails.getString(CommonData.SHOW_CANCEL_BUTTON),
                        this
                    )
                } else {
                    SessionSave.saveSession(CommonData.SHOW_CANCEL_BUTTON, "0", this)
                }

                // booking_details is a JSONObject inside "trip_details"
                val bookingDetails = tripdetails.getJSONObject("booking_details")

                if (bookingDetails.has("now_after")) {
                    nowAfter = bookingDetails.optInt("now_after", -1)
                    // Your original logic here
                }
                model_name = bookingDetails.optString("model_name", "")

                // Your original logic for pickup_notes and drop_notes if needed

                pickup = bookingDetails.optString("pickupplace", "")
                pickup_lat = bookingDetails.optDouble("pickup_latitude", 0.0)
                pickup_lng = bookingDetails.optDouble("pickup_longitude", 0.0)

                val dropLocation = bookingDetails.optString("dropplace", "")
                drop_lattitude = bookingDetails.optDouble("drop_latitude", 0.0)
                drop_longitude = bookingDetails.optDouble("drop_longitude", 0.0)


                val pickupplace = bookingDetails.optString("pickupplace", "")
                val dropplace = bookingDetails.optString("dropplace", "")


                pickupCity = AddressParser.getCityFromAddress(this, pickupplace)
                dropCity = AddressParser.getCityFromAddress(this, dropplace)
                pickupCityTxt.setText(pickupCity)
                dropCityTxt.setText(pickupCity)

                pickupLocationTXt.setText(pickupplace)
                dropLocationTxt.setText(dropplace)
                SessionSave.getSession("site_currency", this@TripNotificationActivity)
                val currencySymbol = SessionSave.getSession("site_currency", this@TripNotificationActivity) ?: "$" // Provide a default if null
                priceTextView.text = "$currencySymbol $estimate_amount"
                distanceTextView.text = "$est_distance KM"

                pickup_time = bookingDetails.optString("pickup_time", "")

//                val result = bookingDetails.optString("pickup_time", "").split(" ".toRegex())
//                    .dropLastWhile { it.isEmpty() }.toTypedArray()
//                if (result.size > 1) {
//                    pickup_time = result[1]
//                } else {
//                    pickup_time = ""
//                }
                val inputDate = pickup_time

                val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                val outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a", Locale.ENGLISH)

                val dateTime = LocalDateTime.parse(inputDate, inputFormatter)
                val formattedDate = dateTime.format(outputFormatter)

                println(formattedDate) // Output: 22-Sep-2025 04:17 PM
                timeTextView.setText(formattedDate)
                println("pick_up_time"+" "+formattedDate)

                passenger_phone = bookingDetails.optString("passenger_phone", "")
                passenger_id = bookingDetails.optString("passenger_id", "")
                distance = bookingDetails.optString("distance_away", "")
                passenger_name = bookingDetails.optString("passenger_name", "")
                bookedby = bookingDetails.optString("bookedby", "")

                MainActivity.mMyStatus.setpassengerphone(passenger_phone)
                // Log the parsed values


            } ?: run {
                Log.e("TripNotification", "Message is null, cannot parse trip details.")
                Toast.makeText(this, "No trip details received.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e("TripNotification", "Error parsing JSON: ${e.message}", e)
            Toast.makeText(this, "Error processing trip details.", Toast.LENGTH_LONG).show()
        }
    }
    private fun updateLayoutStates(bookingType: Int) {
        // Reset all layouts to disabled

        println("bookingType_test $bookingType")
        when (bookingType) {
            0 -> {
               trip_type_txt.setText("Local")
            }

            2 -> {
                trip_type_txt.setText("Rental")

            }

            3 -> {
                trip_type_txt.setText("Outstation")

            }



            else -> {
                trip_type_txt.setText("Local")
            }
        }
    }

    fun LatlongValue(
        pickup_lat: Double?,    // Renamed parameters to avoid confusion with member variables
        pickup_long: Double?,
        current_lat: Double?,
        current_long: Double?
    ) {
        // Assigning parameter values to the member variables of the class instance
        this.pick_lat = pickup_lat
        this.pick_long = pickup_long
        this.current_lattitude = current_lat
        this.current_longitude = current_long
    }

    /**
     * Starts the progress bar animation from 0 to 100 over a dynamic time.
     */
    private fun startProgressAnimation() {
        if (time_out <= 0) {
            tone_stop()
            Log.w(
                "TripNotification",
                "notification_time is 0 or less, progress animation will not run."
            )
            requestTimerProgressBar.visibility = View.GONE
            Toast.makeText(this, "Invalid notification time received.", Toast.LENGTH_LONG).show()
            return
        }

        requestTimerProgressBar.max = 100
        requestTimerProgressBar.progress = 0
        requestTimerProgressBar.visibility = View.VISIBLE

        progressHandler = Handler(Looper.getMainLooper())
        val startTime = System.currentTimeMillis()

        progressRunnable = object : Runnable {
            override fun run() {
                val elapsedTime = System.currentTimeMillis() - startTime
                // time_out is in seconds, convert to milliseconds for calculation
                val calculatedProgress =
                    ((elapsedTime.toFloat() / (time_out * 1000L)) * 100).toInt()

                if (calculatedProgress <= requestTimerProgressBar.max) {
                    requestTimerProgressBar.progress = calculatedProgress
                    progressHandler?.postDelayed(this, PROGRESS_INTERVAL_MILLIS)
                } else {
                    requestTimerProgressBar.progress = requestTimerProgressBar.max
                    requestTimerProgressBar.visibility = View.GONE
                    try {


                        val j = JSONObject()
                        j.put("trip_id", trip_id)
                        j.put(
                            "driver_id",
                            SessionSave.getSession("Id", this@TripNotificationActivity)
                        )
                        j.put(
                            "taxi_id",
                            SessionSave.getSession("taxi_id", this@TripNotificationActivity)
                        )
                        j.put(
                            "company_id",
                            SessionSave.getSession("company_id", this@TripNotificationActivity)
                        )
                        j.put("reason", "")
                        j.put("reject_type", "0")
                        val Url = "type=reject_trip"
                        Handler().postDelayed({

                            TripReject(Url, j, 1)

                        }, 500)
                    } catch (e: java.lang.Exception) {

                        val intent =
                            Intent(this@TripNotificationActivity, HomePageActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivity(intent)
                        finish()
                        CToast.ShowToast(
                            this@TripNotificationActivity,
                            NC.getString(R.string.server_error)
                        )

                        e.printStackTrace()
                    }
                }
            }
        }
        progressHandler?.post(progressRunnable!!)
    }

    private fun stopProgressAnimation() {
        progressHandler?.removeCallbacks(progressRunnable!!)
        requestTimerProgressBar.visibility = View.GONE
        tone_stop()
    }
    fun drawableToBitmap(context: Context, drawableResId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableResId) ?: return null
        // You can adjust the size here if your vector drawable is too small/large by default
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }


    /**
     * This callback is triggered when the map is ready to be used.
     * @param map The GoogleMap object
     */
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.clear() // Clear any existing polylines or markers

        if (pickup_lat != 0.0 && pickup_lng != 0.0 && drop_lattitude != 0.0 && drop_longitude != 0.0) {
            val pickupLatLng = LatLng(pickup_lat, pickup_lng)
            val dropLatLng = LatLng(drop_lattitude, drop_longitude)
            val pickupBitmap: Bitmap? = drawableToBitmap(this, R.drawable.ic_pick_dot) // CHANGE THIS TO YOUR PICKUP VECTOR DRAWABLE ID
            val dropBitmap: Bitmap? = drawableToBitmap(this, R.drawable.ic_drop)     // CHANGE THIS TO YOUR DROP VECTOR DRAWABLE ID

            val pickupIconDescriptor = if (pickupBitmap != null) {
                BitmapDescriptorFactory.fromBitmap(pickupBitmap)
            } else {
                Log.e("MapMarkers", "Failed to load pickup marker icon. Using default.")
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            }

            val dropIconDescriptor = if (dropBitmap != null) {
                BitmapDescriptorFactory.fromBitmap(dropBitmap)
            } else {
                Log.e("MapMarkers", "Failed to load drop-off marker icon. Using default.")
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            }
            // --- End Use ---

            googleMap?.addMarker(
                MarkerOptions()
                    .position(pickupLatLng)
                    .title("Pickup Location")
                    .icon(pickupIconDescriptor)
            )
            googleMap?.addMarker(
                MarkerOptions()
                    .position(dropLatLng)
                    .title("Drop-off Location")
                    .icon(dropIconDescriptor)
            )


            // Start Ripple Animation
            startPulseAnimation(pickupLatLng, true)
            startPulseAnimation(dropLatLng, false)

            LatlongValue(pickup_lat, pickup_lng, current_lattitude ?: 0.0, current_longitude ?: 0.0)

            // Fetch and draw the route using Google Directions API, which will then trigger animation
            fetchAndDrawRoute(pickupLatLng, dropLatLng)

        } else {
            val defaultLocation = LatLng(11.0045, 76.9616) // Coimbatore
            googleMap?.addMarker(MarkerOptions().position(defaultLocation).title("Coimbatore (Default)"))
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
            Toast.makeText(this, "Pickup or drop-off coordinates not available for route.", Toast.LENGTH_LONG).show()
        }
    }

//    private fun fetchAndDrawRoute(origin: LatLng, destination: LatLng) {
//        val apiKey = SessionSave.getSession(CommonData.GOOGLE_KEY, this@TripNotificationActivity)
//
//        val originStr = "${origin.latitude},${origin.longitude}"
//        val destinationStr = "${destination.latitude},${destination.longitude}"
//
//        lifecycleScope.launch {
//            try {
//                val response = directionsApiService.getDirections(originStr, destinationStr, apiKey = apiKey)
//
//                if (response.isSuccessful) {
//                    val directionsResponse = response.body()
//                    directionsResponse?.routes?.firstOrNull()?.overviewPolyline?.points?.let { encodedPolyline ->
//                        val polylinePoints = PolyUtil.decode(encodedPolyline)
//
//                        runOnUiThread {
//                            // Remove previous polyline
//                            polyline?.remove()
//
//                            // Add new route polyline
//                            polyline = googleMap?.addPolyline(
//                                PolylineOptions()
//                                    .addAll(polylinePoints)
//                                    .width(10f)
//                                    .color(Color.parseColor("#054EE5"))
//                                    .zIndex(1f)
//                            )
//
//                            // Start car animation
//                            startCarAnimation(polylinePoints)
//
//                            // ✅ Calculate distance between origin & destination
//                            val results = FloatArray(1)
//                            Location.distanceBetween(
//                                origin.latitude, origin.longitude,
//                                destination.latitude, destination.longitude,
//                                results
//                            )
//                            val distanceInMeters = results[0]
//                            val distanceInKm = distanceInMeters / 1000.0
//
//                            println("Distance (KM): $distanceInKm")
//
//                            // ✅ Calculate midpoint for camera centering
//                            val midLat = (origin.latitude + destination.latitude) / 2
//                            val midLng = (origin.longitude + destination.longitude) / 2
//                            val centerPoint = LatLng(midLat, midLng)
//
//                            // ✅ Zoom based on your requested ranges
//                            val zoomLevel = when {
//                                distanceInKm > 500 -> 6f   // Above 500 km → Very long route
//                                distanceInKm > 300 -> 7f   // 300–500 km → Long outstation
//                                distanceInKm > 200 -> 8f   // 200–300 km → Intercity
//                                distanceInKm > 100 -> 9f   // 100–200 km → Mid-range trip
//                                distanceInKm > 30 -> 12f   // 30–100 km → Nearby city
//                                distanceInKm >= 1 -> 16f   // 1–30 km → Within city
//                                else -> 15f                // <1 km → Same area
//                            }
//
//                            // ✅ Animate camera to fit route center
//                            googleMap?.animateCamera(
//                                CameraUpdateFactory.newCameraPosition(
//                                    CameraPosition.Builder()
//                                        .target(centerPoint)
//                                        .zoom(zoomLevel)
//                                        .tilt(0f)
//                                        .bearing(0f)
//                                        .build()
//                                )
//                            )
//                        }
//                    } ?: run {
//                        Log.w("DirectionsAPI", "No routes found or polyline missing.")
//                        Toast.makeText(this@TripNotificationActivity, "Could not find a route.", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    val errorBody = response.errorBody()?.string()
//                    Log.e("DirectionsAPI", "Error: ${response.code()} - ${response.message()}. Body: $errorBody")
//                    Toast.makeText(this@TripNotificationActivity, "Failed to get directions: ${response.code()}", Toast.LENGTH_LONG).show()
//                }
//            } catch (e: Exception) {
//                Log.e("DirectionsAPI", "Exception fetching directions: ${e.message}", e)
//                Toast.makeText(this@TripNotificationActivity, "Error fetching directions.", Toast.LENGTH_LONG).show()
//            }
//        }
//    }


    private fun fetchAndDrawRoute(origin: LatLng, destination: LatLng) {
        //

        val apiKey = SessionSave.getSession(CommonData.GOOGLE_KEY, this@TripNotificationActivity)

        val originStr = "${origin.latitude},${origin.longitude}"
        val destinationStr = "${destination.latitude},${destination.longitude}"

        lifecycleScope.launch {
            try {
                val response = directionsApiService.getDirections(originStr, destinationStr, apiKey = apiKey)

                if (response.isSuccessful) {
                    val directionsResponse = response.body()
                    directionsResponse?.routes?.firstOrNull()?.overviewPolyline?.points?.let { encodedPolyline ->
                        val polylinePoints = PolyUtil.decode(encodedPolyline)

                        runOnUiThread {
                            // Remove previous polyline
                            polyline?.remove()

                            // Add new route polyline
                            polyline = googleMap?.addPolyline(
                                PolylineOptions()
                                    .addAll(polylinePoints)
                                    .width(10f)
                                    .color(Color.parseColor("#054EE5"))
                                    .zIndex(1f)
                            )

                            // Start car animation
                            startCarAnimation(polylinePoints)

                            // --- Distance Calculation (KEPT) ---
                            val results = FloatArray(1)
                            Location.distanceBetween(
                                origin.latitude, origin.longitude,
                                destination.latitude, destination.longitude,
                                results
                            )
                            val distanceInMeters = results[0]
                            val distanceInKm = distanceInMeters / 1000.0
                            println("Distance (KM): $distanceInKm")
                            // ------------------------------------


                            // 💡 FIX START: Use LatLngBounds to fit ALL polyline points

                            val boundsBuilder = LatLngBounds.Builder()

                            // Crucially, include all points from the decoded polyline,
                            // not just the origin and destination.
                            for (point in polylinePoints) {
                                boundsBuilder.include(point)
                            }

                            val bounds = boundsBuilder.build()

                            // Define padding in pixels (e.g., 150 pixels on all sides)
                            val padding = 150

                            // Animate camera to fit the calculated bounds perfectly
                            googleMap?.animateCamera(
                                CameraUpdateFactory.newLatLngBounds(bounds, padding)
                            )

                            // 💡 FIX END: Removed the old midPoint and fixed zoom level calculation
                        }
                    } ?: run {
                        Log.w("DirectionsAPI", "No routes found or polyline missing.")
                        Toast.makeText(this@TripNotificationActivity, "Could not find a route.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("DirectionsAPI", "Error: ${response.code()} - ${response.message()}. Body: $errorBody")
                    Toast.makeText(this@TripNotificationActivity, "Failed to get directions: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("DirectionsAPI", "Exception fetching directions: ${e.message}", e)
                Toast.makeText(this@TripNotificationActivity, "Error fetching directions.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startCarAnimation(points: List<LatLng>) {
        if (points.isEmpty()) return

        // If a previous car marker exists, remove it before adding a new one
        // This is good practice to prevent multiple markers if the animation is called again
        // You might need a class-level variable for `animatedCarMarker` if you want to stop/remove it later.
        // For this specific case, if called only once per notification, it might be less critical.
        // Let's add a class-level variable for it for better management:
        // private var animatedCarMarker: Marker? = null // Add this at the top with other map variables

        animatedCarMarker?.remove() // Remove any previous animated car marker

        val carMarker = googleMap?.addMarker(
            MarkerOptions()
                .position(points[0])
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.top)) // Replace with your car icon
                .flat(true) // Make the icon flat on the map
                .anchor(0.5f, 0.5f) // Center the icon
        )
        animatedCarMarker = carMarker // Assign to the class-level variable

        // Calculate the duration based on time_out (which is in seconds)
        // Ensure time_out is not zero or negative to prevent division by zero or infinite loop
        val animationDuration = if (time_out > 0) time_out * 1000L else 5000L // Default to 5 seconds if time_out is invalid

        val valueAnimator = ValueAnimator.ofInt(0, points.size - 1)
        valueAnimator.duration = animationDuration // Use the calculated duration here
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener { animator ->
            val index = animator.animatedValue as Int
            if (index < points.size) {
                carMarker?.position = points[index]
                // Optionally, you can calculate bearing for smooth car rotation
                if (index < points.size - 1) {
                    val bearing = calculateBearing(points[index], points[index + 1])
                    carMarker?.rotation = bearing
                }
            } else {
                // Animation completed
                // You might want to remove the car marker or make it static here
                // For now, it will simply stop at the last point
            }
        }
        valueAnimator.start()
    }


    // Helper function to calculate bearing between two LatLng points
    private fun calculateBearing(from: LatLng, to: LatLng): Float {
        val lat1 = Math.toRadians(from.latitude)
        val lng1 = Math.toRadians(from.longitude)
        val lat2 = Math.toRadians(to.latitude)
        val lng2 = Math.toRadians(to.longitude)

        val dLng = lng2 - lng1
        val y = Math.sin(dLng) * Math.cos(lat2)
        val x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLng)
        var bearing = Math.toDegrees(Math.atan2(y, x)).toFloat()

        bearing = (bearing + 360) % 360
        return bearing
    }



    // --- Polyline Drawing Animation Logic ---
    private fun startPolylineDrawingAnimation(points: List<LatLng>) {
        stopPolylineDrawingAnimation() // Stop any previous animation

        if (points.isEmpty()) return

        animatedPolyline?.remove()
        animatedPolyline = googleMap?.addPolyline(
            PolylineOptions()
                .width(10f)
                .color(Color.RED) // Color for the animated drawing
                .zIndex(2f) // Ensure it draws over the base polyline
        )

        polylineIndex = 0
        polylineDrawingHandler = Handler(Looper.getMainLooper())
        polylineDrawingRunnable = object : Runnable {
            override fun run() {
                if (polylineIndex < points.size - 1) {
                    val currentPoints = animatedPolyline?.points?.toMutableList() ?: mutableListOf()
                    currentPoints.add(points[polylineIndex])
                    currentPoints.add(points[polylineIndex + 1])
                    animatedPolyline?.points = currentPoints

                    polylineIndex++
                    polylineDrawingHandler?.postDelayed(this, 50) // Adjust delay for animation speed
                } else {
                    // Animation finished
                    // Optionally remove the base polyline if you only want the animated one to stay
                    // polyline?.remove()
                }
            }
        }
        polylineDrawingHandler?.post(polylineDrawingRunnable!!)
    }

    private fun stopPolylineDrawingAnimation() {
        polylineDrawingHandler?.removeCallbacks(polylineDrawingRunnable!!)
        polylineDrawingHandler = null
        polylineDrawingRunnable = null
        animatedPolyline?.remove() // Remove the animated polyline
        animatedPolyline = null
        polylineIndex = 0
    }
    // --- End Polyline Drawing Animation Logic ---

    // --- Ripple Animation Logic ---
    private fun startPulseAnimation(latLng: LatLng, isOrigin: Boolean = true) {
        val initialRadius = 1.0 // Small initial radius in meters
        val maxRadius = 800.0 // Max radius for the pulse in meters (adjust as needed for map zoom)
        val animationDuration = 2500L // 2.5 seconds per pulse
        val pulseColor = if (isOrigin) Color.parseColor("#4CAF50") else Color.parseColor("#F44336")

        if (isOrigin) originPulseCircle?.remove() else destinationPulseCircle?.remove()

        val circleOptions = CircleOptions()
            .center(latLng)
            .radius(initialRadius)
            .strokeWidth(5f)
            .strokeColor(pulseColor)
            .fillColor(Color.TRANSPARENT)
            .zIndex(0f)

        val circle = googleMap?.addCircle(circleOptions)

        if (isOrigin) originPulseCircle = circle else destinationPulseCircle = circle

        val radiusAnimator = ValueAnimator.ofFloat(initialRadius.toFloat(), maxRadius.toFloat()).apply {
            duration = animationDuration
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener { animator ->
                circle?.radius = (animator.animatedValue as Float).toDouble()
            }
        }

        val alphaAnimator = ValueAnimator.ofInt(255, 0).apply {
            duration = animationDuration
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener { animator ->
                val alpha = animator.animatedValue as Int
                val newColor = Color.argb(alpha, Color.red(pulseColor), Color.green(pulseColor), Color.blue(pulseColor))
                circle?.strokeColor = newColor
                circle?.fillColor = Color.argb(alpha / 4, Color.red(pulseColor), Color.green(pulseColor), Color.blue(pulseColor))
            }
        }

        pulseAnimatorSet = AnimatorSet().apply {
            playTogether(radiusAnimator, alphaAnimator)
            start()
        }
    }

    private fun stopPulseAnimation() {
        pulseAnimatorSet?.cancel()
        pulseAnimatorSet = null

        originPulseCircle?.remove()
        originPulseCircle = null
        destinationPulseCircle?.remove()
        destinationPulseCircle = null
    }

    private fun stopAllMapAnimations() {
        stopPulseAnimation()
        animatedCarMarker?.remove() // Remove the car marker
        animatedCarMarker = null
    }
    // --- End Ripple Animation Logic ---


    // --- Activity Lifecycle Overrides ---

    override fun onStop() {
        super.onStop()
        stopProgressAnimation()
        stopAllMapAnimations() // Stop all animations when going to background
    }

    override fun onDestroy() {
        super.onDestroy()
        progressHandler?.removeCallbacks(progressRunnable!!)
        progressHandler = null
        progressRunnable = null
        stopProgressAnimation()
        stopAllMapAnimations() // Stop all animations on destroy
        if (::fusedLocationClient.isInitialized && ::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }




    // Other lifecycle methods are fine as empty overrides or can be removed if not used.
    // The SupportMapFragment handles its own lifecycle within FragmentContainerView.
    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    inner class TripAccept(url: String, var jsonObject: JSONObject) : APIResult {
        var msg: String? = null

        init {
            Systems.out.println("result$url")

            APIService_Retrofit_JSON(
                this@TripNotificationActivity,
                this,
                jsonObject,
                false
            ).execute(url)
        }

        override fun getResult(isSuccess: Boolean, result: String) {
            try {
                if (isSuccess) {
                    val json = JSONObject(result)
                    msg = json.getString("message")
                    CommonData.current_trip_accept = 1
                    if (json.getInt("status") == 7) {
                        bookedby = ""
                        SessionSave.saveSession("trip_id", "", this@TripNotificationActivity)
                        msg = json.getString("message")
                        val i = Intent(
                            getBaseContext(),
                            HomePageActivity::class.java
                        )
                        // showLoading(NotificationAct.this);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        val extras = Bundle()
                        extras.putString("alert_message", msg)
                        CToast.ShowToast(this@TripNotificationActivity, msg)
                        getApplication().startActivity(i)
                        nActivity!!.finish()
                    } else if (json.getInt("status") == 1 || bookedby == "2") {
                        SessionSave.saveSession("speedwaiting", "", this@TripNotificationActivity)
                        MainActivity.mMyStatus.settripId(trip_id)
                        SessionSave.saveSession("trip_id", trip_id, this@TripNotificationActivity)
                        SessionSave.saveSession(
                            "status", "B",
                            this@TripNotificationActivity
                        )
                        SessionSave.saveSession("otp_enter", "yes", this@TripNotificationActivity)


                        SessionSave.saveSession(
                            CommonData.IS_STREET_PICKUP,
                            false,
                            this@TripNotificationActivity
                        )
                        SessionSave.saveSession("bookedby", bookedby, this@TripNotificationActivity)
                        //  showLoading(NotificationAct.this);
                        val intent = Intent(
                            this@TripNotificationActivity,
                            OngoingAct::class.java
                        )
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        val extras = Bundle()
                        extras.putString("alert_message", msg)
                        intent.putExtras(extras)
                        startActivity(intent)
                        finish()
                    } else if (json.getInt("status") == 5) {
                        SessionSave.saveSession("trip_id", "", this@TripNotificationActivity)
                        msg = json.getString("message")
                        val i = Intent(
                            getBaseContext(),
                            HomePageActivity::class.java
                        )
                        // showLoading(NotificationAct.this);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        val extras = Bundle()
                        extras.putString("alert_message", msg)
                        //i.putExtras(extras);
                        getApplication().startActivity(i)
                        CToast.ShowToast(this@TripNotificationActivity, msg)
                        nActivity!!.finish()
                    } else if (json.getInt("status") == 25) {
                        runOnUiThread(Runnable {
                            CToast.ShowToast(
                                this@TripNotificationActivity,
                                NC.getString(R.string.server_error)
                            )
                        })
                    } else {
                        runOnUiThread(Runnable {
                            CToast.ShowToast(
                                this@TripNotificationActivity,
                                msg
                            )
                        })
                        finish()
                    }
                } else {
                    runOnUiThread(Runnable {
                        CToast.ShowToast(
                            this@TripNotificationActivity,
                            NC.getString(R.string.server_error)
                        )
                    })
                    finish()
                }
            } catch (e: JSONException) {
                getRepository(this@TripNotificationActivity)!!.insertAllApiErrorLogs(
                    ApiErrorModel(
                        0,
                        CommonData.getCurrentTimeForLogger(),
                        "type=driver_reply",
                        buildStackTraceString(e.stackTrace),
                        driverInfo(this@TripNotificationActivity),
                        jsonObject,
                        this@TripNotificationActivity.javaClass.getSimpleName(),
                        0
                    )
                )

                e.printStackTrace()
            }
        }
    }


    inner class TripReject(url: String?, var jsonObject: JSONObject, type: Int) : APIResult {
        var msg: String? = null
        var cancel_type = 0

        init {
            APIService_Retrofit_JSON(
                this@TripNotificationActivity,
                this,
                jsonObject,
                false
            ).execute(url)
        }

        override fun getResult(isSuccess: Boolean, result: String) {
            Log.d("result", "result$result")
            try {
                if (isSuccess) {
                    CommonData.current_trip_accept = 0
                    //  nonactiityobj.startServicefromNonActivity(this@TripNotificationActivity)
                    CommonData.current_trip_accept = 0
                    val json = JSONObject(result)
                    if (json.getInt("status") == 6) {
                        msg = json.getString("message")
                    } else if (json.getInt("status") == 7) {
                        msg = json.getString("message")
                        cancel_type = if (json.getString("allow_offline_api")
                                .equals("1", ignoreCase = true)
                        ) {
                            1
                        } else {
                            0
                        }
                    } else if (json.getInt("status") == 8) {
                        msg = json.getString("message")
                    } else if (json.getInt("status") != 6 || json.getInt("status") != 8 || json.getInt(
                            "status"
                        ) != 3 || json.getInt("status") != 2 || json.getInt("status") != -1
                    ) {
                        msg = "Trip has been rejected"
                    } else {
                        msg = "Trip has been already cancelled"
                    }
                    SessionSave.saveSession("trip_id", "", this@TripNotificationActivity)
                    // showLoading(NotificationAct.this);

//                    if (cancel_type == 1) {
//                        shiftFunction();
//                    } else {
                    val intent = Intent(
                        this@TripNotificationActivity,
                        HomePageActivity::class.java
                    )
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    finish()
                    //}
                    CToast.ShowToast(this@TripNotificationActivity, msg)
                } else {
                    runOnUiThread(Runnable {
                        CToast.ShowToast(
                            this@TripNotificationActivity,
                            NC.getString(R.string.server_error)
                        )
                    })
                    finish()
                }
            } catch (e: JSONException) {
                if (this@TripNotificationActivity != null) {
                    //   shiftFunction();
                    val intent = Intent(
                        this@TripNotificationActivity,
                        HomePageActivity::class.java
                    )
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    finish()
                    CToast.ShowToast(
                        this@TripNotificationActivity,
                        NC.getString(R.string.server_error)
                    )
                }
                getRepository(this@TripNotificationActivity)!!.insertAllApiErrorLogs(
                    ApiErrorModel(
                        0,
                        CommonData.getCurrentTimeForLogger(),
                        "type=reject_trip",
                        buildStackTraceString(e.stackTrace),
                        driverInfo(this@TripNotificationActivity),
                        jsonObject,
                        this@TripNotificationActivity.javaClass.getSimpleName(),
                        0
                    )
                )
                e.printStackTrace()
            }
        }
    }

    private fun tone_play() {
        // Stop and release any currently playing audio before starting a new one
        if (mPlayer != null && mPlayer!!.isPlaying) {
            mPlayer?.stop()
            mPlayer?.release()
            mPlayer = null
        }

        try {
            mPlayer = MediaPlayer.create(this, R.raw.taxi_arrived)
            mPlayer?.isLooping = true  // <-- keep playing until you stop manually
            mPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MediaPlayer", "Error playing tone: ${e.message}")
        }
    }


    private fun tone_stop() {
        if (mPlayer != null) { // Check if mPlayer is initialized
            if (mPlayer!!.isPlaying) { // Check if it's currently playing
                mPlayer?.stop() // Stop playback
            }
            mPlayer?.release() // Release resources
            mPlayer = null // Prevent memory leaks and ensure it's re-initialized if played again
        }
    }

}



