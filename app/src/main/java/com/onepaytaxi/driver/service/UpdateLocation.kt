package com.onepaytaxi.driver.service
//
//import android.Manifest
//import android.app.*
//import android.content.Context
//import android.content.Intent
//import android.content.IntentSender
//import android.content.pm.PackageManager
//import android.graphics.Color
//import android.graphics.drawable.Icon
//import android.location.Location
//import android.os.Build
//import android.os.IBinder
//import android.os.Looper
//import android.provider.Settings
//import android.support.v4.content.ContextCompat
//import android.widget.Toast
//import com.Taximobility.driver.R
//import com.Taximobility.driver.SplashAct
//import com.Taximobility.driver.data.CommonData
//import com.Taximobility.driver.interfaces.DistanceMatrixInterface
//import com.Taximobility.driver.interfaces.DistanceUpdate
//import com.Taximobility.driver.route.FindApproxDistance
//import com.Taximobility.driver.utils.NC
//import com.Taximobility.driver.utils.SessionSave
//import com.google.android.gms.common.api.ResolvableApiException
//import com.google.android.gms.location.*
//import com.google.android.gms.maps.model.LatLng
//import com.google.maps.android.SphericalUtil
//import java.util.concurrent.Executors
//
//
//class UpdateLocation : Service(), DistanceMatrixInterface {
//
//
//    val NOTIFICATION_ID = 10
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//    private val mTimer = Executors.newSingleThreadScheduledExecutor()
//    private lateinit var builder: LocationSettingsRequest.Builder
//    private lateinit var deviceID: String
//    private lateinit var locationCallback: LocationCallback
//
//    /**
//     * This will be the most recently got location
//     */
//    private lateinit var lastKnownLocation: Location
//
//    private val SLAB_ACCURACY: Int = 100
//    private val slabDistance = 250.0
//
//    /**
//     * Location request settings
//     * interval ->  The location client will actively try to obtain location updates for your application at this interval,
//     * fastestInterval -> The fastest rate that that you will receive updates
//     * priority-> Priority of the location request High -> more accurate & battery consuming
//     */
//    val locationRequest = LocationRequest().apply {
//        interval = 5000
//        fastestInterval = 1000
//        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        smallestDisplacement = 10.0f
//    }
//    val timerInterval: Long = 5000
//
//
//    var tripLastLatLng: HashMap<Int, LatLng> = HashMap()
//    var tripDistance: HashMap<Int, Float> = HashMap()
//
//    var googleTripDistance: HashMap<Int, Double> = HashMap()
//
//
//    companion object {
//        val TAG = UpdateLocation::class.simpleName
//        private var distanceUpdates: DistanceUpdate? = null
//
//        var isSocket = false
//        /**
//         * Checks whether service is previously running if not it will start service else do nothing
//         */
//        @JvmStatic
//        fun startLocationService(context: Context) {
//
//            var calledClass = ""
//            if (context is Activity?) calledClass = context.localClassName
//            println(TAG + " start service called from   " + calledClass + "  " + UpdateLocation::class.qualifiedName)
//            if (checkValidUser(context)) {
//                if (!CommonData.serviceIsRunningInForeground(context, UpdateLocation::class.qualifiedName)) {
//                    println(TAG + " start service NOT RUNNING  ")
//                    val pushIntent1 = Intent(context, UpdateLocation::class.java)
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        context.startForegroundService(pushIntent1)
//                    } else {
//                        context.startService(pushIntent1)
//                    }
//                } else {
//                    Toast.makeText(context, "Service already running", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//        @JvmStatic
//        fun distanceUpdate(distanceUpdate: DistanceUpdate) {
//            distanceUpdates = distanceUpdate
//        }
//
//        /**
//         * To check necessary variables like id,shift status etc
//         * returns true if satisfied
//         */
//        fun checkValidUser(context: Context): Boolean {
//            return true
//            //  return  SessionSave.getSession("Id", context) != "" && !SessionSave.getSession(CommonData.SHIFT_OUT, context, false)
//        }
//
//    }
//
//
//    override fun onBind(p0: Intent?): IBinder? {
//        return null
//    }
//
//
//    private var isDistanceCalculationEnabled: Boolean = true
//
//    /**
//     * oncreate called only once when new service is created
//     * it will not call by startservice if already created and running
//     */
//    override fun onCreate() {
//        super.onCreate()
//        println(TAG + " create called  ")
//        startForeground(NOTIFICATION_ID, getNotification())
//        deviceID = Settings.Secure.getString(this@UpdateLocation.getContentResolver(), Settings.Secure.ANDROID_ID)
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
//        isSocket = SessionSave.getSession(CommonData.SOCKET_ENABLED, this@UpdateLocation, false)
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult?) {
//                locationResult ?: return
//                for (location in locationResult.locations) {
//                    println("$TAG  current ${location?.latitude},${location?.longitude}")
//
//                    if (location != null && location.hasAccuracy() && location.accuracy < SLAB_ACCURACY) {
//                        lastKnownLocation = location
//                        if (isDistanceCalculationEnabled) {
//                            calculateDistance(1, location)
//                        }
//                    }
//
//                    // Update UI with location data
//                    // ...
//                }
//            }
//        }
//
//
//        locationSettingEnabled()
//        // createSocket()
//
//    }
//
//    private fun calculateDistance(id: Int, location: Location) {
//
//        println("nan--tripLastLatLng" + tripLastLatLng.get(id))
//        val from = tripLastLatLng.get(id) ?: LatLng(location.latitude, location.longitude)
////        val from=LatLng(tripLastLatLng.get(id)?.latitude!!,tripLastLatLng.get(id)?.longitude!!)
//        val to = LatLng(location.latitude, location.longitude)
//        val calculatedDistance = SphericalUtil.computeDistanceBetween(from, to).toFloat() / 1000
//        var distance = tripDistance.get(id)?.plus(calculatedDistance)
//                ?: calculatedDistance
//        println("nan-test-distance" + (distance * 1000))
//        println("nan-test-calculatedDistance ${calculatedDistance * 1000} $from $to")
//
//        //Nandhini to calculate a distance based on the slabDistance
//        if ((calculatedDistance * 1000) > slabDistance) {
//            FindApproxDistance(this@UpdateLocation).getDistance(this, from.latitude, from.longitude, to.latitude, to.longitude)
//        }
//        println("$TAG Distance covered  $distance cal: $calculatedDistance")
//
//
//
//        distanceUpdates?.onDistanceUpdate(distance.toDouble(), "1")
//
//        SessionSave.saveGoogleWaypointsWithId(from, to, "haversine", distance.toDouble(), id.toString(), this@UpdateLocation)
//
//
//        tripDistance.put(id, distance)
//        tripLastLatLng.put(id, LatLng(location.latitude, location.longitude))
//
//
//    }
//
//
//    override fun onDistanceCalled(pick: LatLng?, drop: LatLng?, distance: Double, time: Double, result: String?, status: String?) {
//        println("nan-test-onDistanceCalled" + distance)
//        // SessionSave.saveGoogleWaypointsWithId(pick, drop, "google", distance, "1", this@UpdateLocation)
//
//        distanceUpdates?.onDistanceUpdate(distance, "2")
//
//        googleTripDistance.put(1, distance)
//    }
//
//    /**
//     * calls when start service is being called  all initialization needs to be done here.
//     */
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        println(TAG + " start Command called  ")
//        return START_STICKY
//    }
//
//
//    private fun locationSettingEnabled() {
//        val task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
//        task.addOnSuccessListener { locationSettingsResponse ->
//            // All location settings are satisfied. The client can initialize
//            // location requests here.
//            // ...
//            if (ContextCompat.checkSelfPermission(this@UpdateLocation, Manifest.permission.ACCESS_FINE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED) {
//                // Permission is not granted
//                Toast.makeText(this@UpdateLocation, "Enable permission", Toast.LENGTH_SHORT).show();
//
//            } else {
//
//                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback
//                        , Looper.getMainLooper())
//            }
//            println("we can execute")
//
//        }
//        task.addOnFailureListener { exception ->
//            if (exception is ResolvableApiException) {
//                // Location settings are not satisfied, but this can be fixed
//                // by showing the user a dialog.
//                try {
//                    // Show the dialog by calling startResolutionForResult(),
//                    // and check the result in onActivityResult().
//                    Toast.makeText(this@UpdateLocation, "Enable location to high accuracy", Toast.LENGTH_SHORT).show();
//                } catch (sendEx: IntentSender.SendIntentException) {
//                    // Ignore the error.
//                }
//            }
//        }
//
//
//    }
//
//
//    /**
//     * Generate and return notification with os version compactability
//     */
//
//    private fun getNotification(): Notification {
//
//        val activityPendingIntent = PendingIntent.getActivity(this, 0, Intent(this, SplashAct::class.java), 0)
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val NOTIFICATION_CHANNEL_ID = "my_channel_id_01"
//        val notifyId = 10
//        val notification: Notification
//        var builder: Notification.Builder? = null
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val action = Notification.Action.Builder(Icon.createWithResource(this, R.drawable.ic_launcher), NC.getString(R.string.notiy_lanch_app), activityPendingIntent).build()
//            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH)
//            // Configure the notification channel.
//            notificationChannel.description = "Channel description"
//            notificationChannel.enableLights(true)
//            notificationChannel.lightColor = Color.RED
//            notificationManager.createNotificationChannel(notificationChannel)
//
//            builder = Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
//                    .addAction(action)
//                    .setContentText(NC.getString(R.string.app_running))
//                    .setContentTitle(resources.getString(R.string.app_name))
//                    .setOngoing(true)
//                    .setSmallIcon(R.drawable.small_logo)
//                    .setWhen(System.currentTimeMillis())
//
//        } else {
//            builder = Notification.Builder(this)
//                    .addAction(0, NC.getString(R.string.notiy_lanch_app) + ""/* + getTripStatus()*/,
//                            activityPendingIntent)
//                    .setContentText(NC.getString(R.string.app_running))
//                    .setContentTitle(resources.getString(R.string.app_name))
//                    .setOngoing(true)
//                    .setPriority(Notification.PRIORITY_HIGH)
//                    .setSmallIcon(R.drawable.small_logo)
//                    .setWhen(System.currentTimeMillis())
//        }
//
//
//        notification = builder!!.build()
//        notificationManager.notify(notifyId, notification)
//        return notification
//    }
//
//
//}