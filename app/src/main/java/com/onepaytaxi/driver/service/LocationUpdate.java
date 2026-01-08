package com.onepaytaxi.driver.service;

import static com.onepaytaxi.driver.MainActivity.mMyStatus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.bluetaxi.driver.tripnotification.TripNotificationActivity;
import com.onepaytaxi.driver.Login.DriverLoginActivity;
import com.onepaytaxi.driver.homepage.HomePageActivity;
import com.onepaytaxi.driver.trackschdule.TrackMovement;
import com.onepaytaxi.driver.utils.ElevationHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.onepaytaxi.driver.BuildConfig;
import com.onepaytaxi.driver.CallReceiver;
import com.onepaytaxi.driver.MyApplication;

import com.onepaytaxi.driver.OngoingAct;
import com.onepaytaxi.driver.R;
import com.onepaytaxi.driver.SplashAct;
import com.onepaytaxi.driver.data.CommonData;
import com.onepaytaxi.driver.errorLog.ApiErrorModel;
import com.onepaytaxi.driver.errorLog.ErrorLogRepository;
import com.onepaytaxi.driver.interfaces.APIResult;
import com.onepaytaxi.driver.interfaces.DistanceMatrixInterface;
import com.onepaytaxi.driver.interfaces.Pickupupdate;
import com.onepaytaxi.driver.interfaces.StreetPickupInterface;
import com.onepaytaxi.driver.route.FindApproxDistance;
import com.onepaytaxi.driver.utils.CL;
import com.onepaytaxi.driver.utils.DistanceMatrixUtil;
import com.onepaytaxi.driver.utils.DriverUtils;
import com.onepaytaxi.driver.utils.ExceptionConverter;
import com.onepaytaxi.driver.utils.FontHelper;
import com.onepaytaxi.driver.utils.LocationDb;
import com.onepaytaxi.driver.utils.LocationUtils;
import com.onepaytaxi.driver.utils.NC;
import com.onepaytaxi.driver.utils.NetworkStatus;
import com.onepaytaxi.driver.utils.SessionSave;
import com.onepaytaxi.driver.utils.Systems;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

//import com.taximobility.driver.utils.DeviceUtils;

/**
 * getting gps status without location manager
 * This class helps to get the driver current location using location client. It
 * Keep on updating driver location to server with certain time interval (Every
 * 5sec). In this class,Driver gets the new request notification and trip cancel
 * notifications.
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class LocationUpdate extends Service implements DistanceMatrixInterface {
    public static final String LOCATION_ACCURACY_LOW = "LOW_ACCURACY";
    public static final String WAITING_TIME = "waiting_time";
    public static final float slabAccuracy = 70f;
    public static String oLocation = "";
    public static double currentLatitude = 0.0;
    public static double currentLongtitude = 0.0;
    public static double currentAccuracy = 0.0;
    public static double speed = 0.0;
    public static double HTotdistanceKM = 0.0;
    public static boolean DISTANCE_CALCULATION_INPROGRESS;
    public static long STARTED_AT;
    public static long startTime = 0L, timeInMillies = 0L, finalTime = 0L;
    public static LocationUpdate instance;
    public static StreetPickupInterface streetPickupInterface;
    public static Location currentLocation = null;
    public static String sTimer = "00:00:00";
    private static final int Notification_ID = 1;
    private static final Handler myHandler = new Handler();
    private static boolean waitingTimeRunning;
    private static final int idleNotification = 201;
    private final long FREE_UPDATE_INTERVAL = 10000;
    private final long INTRIP_UPDATE_INTERVAL = 5000;
    LocationDb LocDB;
    DecimalFormat latlngdf = new DecimalFormat("#.######");
    private LocalBroadcastManager localBroadcastManager;
    private final ArrayList<Float> locationAccuracyList = new ArrayList<>();
    private long logLocationInterval = 0;
    private double lastlatitude = 0.0, lastlongitude = 0.0;
    private final double slabDistance = 250;
    public String updateLocation = "", bearing = "0";
    public static String sLocation = "";
    private String serviceStartedFrom = "", serviceStartedTime = "", serviceCreatedTime = "";
    private boolean canCalculateDistance;
    private boolean UPDATE_LOCATION_NO_TRAFFIC = true;
    private final long locationUpdatedAt = 0L;
    private int startID, errorCount = 0;
    private long UPDATE_INTERVAL = 0;
    private final long TIMER_INTERVAL = 5000;
    private final long DELAY_DUE_TO_TRAFFIC = 10000;
    private long timeSwap;

    boolean isOutsideCity = false;




    boolean isHillsStarted = false;

   // private ServiceCallback callback;
    private TrackMovement mTrackMovent;
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public LocationUpdate getService() {
            return LocationUpdate.this;
        }
    }
    JSONObject data = new JSONObject();
    private final Runnable updateTimerMethod = new Runnable() {
        @Override
        public void run() {
            timeInMillies = SystemClock.uptimeMillis() - startTime;
            if (startTime != 0) {
                finalTime = timeSwap + timeInMillies;
                sTimer = CommonData.getDateForWaitingTime(finalTime);

                if (finalTime != 0) {
                    SessionSave.setWaitingTime(finalTime, LocationUpdate.this);
                    if (localBroadcastManager != null) {
                        Intent localIntent = new Intent(WAITING_TIME);
                        localIntent.putExtra(CommonData.FINAL_WAITING_TIME, sTimer);
                        localBroadcastManager.sendBroadcast(localIntent);
                    }
                }
            }
            myHandler.postDelayed(this, 1000);
        }
    };
    BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Systems.out.println("LISTENER Location Update");
            if (intent.getStringExtra(CommonData.WAITING_TIME_START_STOP).equalsIgnoreCase(CommonData.WAITING_TIME_START)) {
                startWaitingTime();
            } else {
                stopWaitingTime();
            }
        }
    };
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Handler DistanceHandler, mhandler;
    private Runnable distanceRunnable, timerRunnable;
    private Date mDateObject;
    private ScheduledFuture<?> excecuter;
    private NotificationManager notificationManager;
    private final ScheduledExecutorService mTimer = Executors.newSingleThreadScheduledExecutor();

    FusedLocationProviderClient mFusedLocationClient;

    private static Location mLastLocationTemp;
    public static double localDistance = 0.0;
    public Double Pickup_lat, Pickup_long;

    public static int runningFor() {

        return (int) ((new Date().getTime() - STARTED_AT) / 1000);
    }

    public static void startLocationService(Context context) {
        Systems.out.println("Location  ConnectionResult !");
        if (!SessionSave.getSession("Id", context).equals("") && !SessionSave.getSession(CommonData.SHIFT_OUT, context, false)) {
            if (!CommonData.serviceIsRunningInForeground(context)) {
                String serviceStartedFrom = context.getClass().getSimpleName();
                Intent pushIntent1 = new Intent(context, LocationUpdate.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    pushIntent1.putExtra("started_from", serviceStartedFrom);
                    context.startForegroundService(pushIntent1);
                } else {
                    pushIntent1.putExtra("started_from", serviceStartedFrom);
                    context.startService(pushIntent1);
                }
            }
        }
    }

    public static void registerInterface(StreetPickupInterface streetPickupInterfaces) {
        streetPickupInterface = streetPickupInterfaces;
    }

    public static boolean isNetworkEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }


    public static String GetUTCdatetimeAsString() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static void ClearSession(Context context) {
        Systems.out.println("nn--ClearSession");
        timeInMillies = 0L;
        finalTime = 0L;
        startTime = 0L;
        sTimer = "00:00:00";
        mLastLocationTemp = null;
        localDistance = 0.0;
        waitingTimeRunning = false;
        LocationUpdate.sLocation = "";
        SessionSave.setWaitingTime(0L, context);
        SessionSave.setDistance(0.0, context);
        SessionSave.setDistanceCityLimit(0.0, context);
        SessionSave.setGoogleDistance(0f, context);
        SessionSave.saveSession(CommonData.LAST_KNOWN_LAT, "", context);
        SessionSave.saveGoogleWaypoints(null, null, "", 0.0, "", context);
        SessionSave.saveWaypoints(null, null, "", 0.0, "", context);
    }

    public static void ClearSessionwithTrip(Context context) {
        Systems.out.println("nn--ClearSessionwithTrip");
        timeInMillies = 0L;
        finalTime = 0L;
        startTime = 0L;
        sTimer = "00:00:00";
        waitingTimeRunning = false;
        mLastLocationTemp = null;
        localDistance = 0.0;
        LocationUpdate.sLocation = "";
        SessionSave.setWaitingTime(0L, context);
        SessionSave.setDistance(0.0, context);
        SessionSave.setDistanceCityLimit(0.0, context);
        SessionSave.setGoogleDistance(0f, context);
        SessionSave.saveSession(CommonData.LAST_KNOWN_LAT, "", context);
        SessionSave.saveSession("status", "F", context);
        SessionSave.saveSession("travel_status", "", context);
        SessionSave.saveSession("trip_id", "", context);
        SessionSave.saveGoogleWaypoints(null, null, "", 0.0, "", context);
        SessionSave.saveWaypoints(null, null, "", 0.0, "", context);
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Systems.out.println("Location  ConnectionResult @");
        Log.e("", "Location update service started Start");
        if (intent != null && intent.getStringExtra("started_from") != null)
            serviceStartedFrom = intent.getStringExtra("started_from");
        serviceStartedTime = GetUTCdatetimeAsString();
        startID = startId;
        Systems.out.println("startID" + startId);
//        StreetPickUpAct.registerDistanceInterface(new Pickupupdate() {
//
//            @Override
//            public void pickUpdate(Location location) {
//                currentLocation = location;
//                lastlatitude = location.getLatitude();
//                lastlongitude = location.getLongitude();
//                DistanceHandler.postDelayed(distanceRunnable, 0);
//            }
//        });

        OngoingAct.registerDistanceInterface(new Pickupupdate() {

            @Override
            public void pickUpdate(Location location) {
                currentLocation = location;
                lastlatitude = location.getLatitude();
                lastlongitude = location.getLongitude();
                DistanceHandler.postDelayed(distanceRunnable, 0);
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("Wakelock")
    @Override
    public void onCreate() {
        Systems.out.println("Location  ConnectionResult #");
        super.onCreate();
        this.startForeground(10, getNotification());
        serviceCreatedTime = GetUTCdatetimeAsString();
        cancelNotification(LocationUpdate.this, idleNotification);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mDateObject = new Date();
        STARTED_AT = mDateObject.getTime();
        SessionSave.saveSession("service_status", true, LocationUpdate.this);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        mhandler = new Handler(Looper.getMainLooper());
        lastlatitude = SessionSave.getLastLng(LocationUpdate.this).latitude;
        lastlongitude = SessionSave.getLastLng(LocationUpdate.this).longitude;
        DistanceHandler = new Handler();
//        this.localBroadcastManager.registerReceiver(listener,
//                new IntentFilter(StreetPickUpAct.WAITING_TIME_RUN));
        distanceRunnable = new Runnable() {
            @Override
            public void run() {
                canCalculateDistance = true;
            }
        };
        DistanceHandler.post(distanceRunnable);

        instance = this;
        Systems.out.println("Location  ConnectionResult 1");
        Log.e("", "Location update service create");

        //Get Auth
        SessionSave.saveSession(CommonData.NODE_TOKEN, "0", LocationUpdate.this);
        NodeAuth.getInstance().getAuth(LocationUpdate.this);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
       mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY );
        mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);


        LocDB = new LocationDb(LocationUpdate.this);


        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (excecuter != null && (excecuter.getDelay(TimeUnit.SECONDS) >= 0 && excecuter.getDelay(TimeUnit.SECONDS) < 5)) {
                    Systems.out.println("Gcmupdate ----->   " + new java.util.Date());

                    UPDATE_INTERVAL += TIMER_INTERVAL;


                    if (sLocation.equals("")) {
                        if (ActivityCompat.checkSelfPermission(LocationUpdate.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationUpdate.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            mFusedLocationClient.getLastLocation()
                                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {
                                            // Got last known location. In some rare situations this can be null.
                                            if (location != null) {
                                                mLastLocation = location;
                                                if (servicesConnected() && mLastLocation != null && mLastLocation.hasAccuracy() && mLastLocation.getAccuracy() <= slabAccuracy) {
                                                    Systems.out.println("nnn---onConnected!!!!!!%%%%%%");
                                                    currentLatitude = mLastLocation.getLatitude();
                                                    currentLongtitude = mLastLocation.getLongitude();
                                                    if (mLastLocation.hasBearing())
                                                        bearing = String.valueOf(mLastLocation.getBearing());
                                                }
                                            }
                                        }
                                    });
                        }

                    } else {
                        if (!GPSEnabled(LocationUpdate.this) && SessionSave.getSession("status", LocationUpdate.this).equalsIgnoreCase("F")) {
                            sLocation = "";
                        }
                    }

                    try {


                        utilizeLocationToCalcDistance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (SessionSave.getSession("Id", LocationUpdate.this).trim().equals("") || !SessionSave.getSession("shift_status", LocationUpdate.this).equals("IN")) {
                        if (mTimer != null)
                            mTimer.shutdown();
                        stopSelf();
                    } else {
                        if (NetworkStatus.isOnline(LocationUpdate.this)) {
                            if (localBroadcastManager != null) {
                                Intent localIntent = new Intent(LOCATION_ACCURACY_LOW);
                                localIntent.putExtra("show_alert", false);
                                localBroadcastManager.sendBroadcast(localIntent);
                            }
                            if (!SessionSave.getSession("Id", LocationUpdate.this).equals("")) {
                                /**
                                 * if location history throws error for 3 times continously
                                 * we will wait for DELAY_DUE_TO_TRAFFIC sec to next update
                                 */
                                if (errorCount >= 3 && UPDATE_LOCATION_NO_TRAFFIC) {
                                    Systems.out.println("Nandhini Distance Calculation UPDATE_LOCATION_NO_TRAFFIC");
                                    UPDATE_LOCATION_NO_TRAFFIC = false;
                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            errorCount = 0;
                                            UPDATE_LOCATION_NO_TRAFFIC = true;
                                        }
                                    }, DELAY_DUE_TO_TRAFFIC);
                                }
                                if (UPDATE_LOCATION_NO_TRAFFIC) {
                                    if (SessionSave.getSession("travel_status", LocationUpdate.this).equals("2")) {
                                        if (UPDATE_INTERVAL >= INTRIP_UPDATE_INTERVAL) {
                                            if (!SessionSave.getSession(CommonData.NODE_TOKEN, LocationUpdate.this).equals("0"))
                                                DriverStatusUpdate(SessionSave.getSession("Id", LocationUpdate.this), SessionSave.getSession("status", LocationUpdate.this), "");
                                            else
                                                NodeAuth.getInstance().getAuth(LocationUpdate.this);
                                            UPDATE_INTERVAL = 0;
                                        }
                                    } else if (UPDATE_INTERVAL >= FREE_UPDATE_INTERVAL) {
                                        if (!SessionSave.getSession(CommonData.NODE_TOKEN, LocationUpdate.this).equals("0"))
                                            DriverStatusUpdate(SessionSave.getSession("Id", LocationUpdate.this), SessionSave.getSession("status", LocationUpdate.this), "");
                                        else
                                            NodeAuth.getInstance().getAuth(LocationUpdate.this);
                                        UPDATE_INTERVAL = 0;
                                    }
                                }
                            } else {
                                SessionSave.saveSession(CommonData.LOGOUT, true, LocationUpdate.this);
                                Intent intent = new Intent(LocationUpdate.this, DriverLoginActivity.class);
                                startActivity(intent);
                                if (mTimer != null)
                                    mTimer.shutdown();
                                stopSelf();
                            }
                        } else if (sLocation.equals("")) {
                            if (localBroadcastManager != null) {
                                Intent localIntent = new Intent(LOCATION_ACCURACY_LOW);
                                localIntent.putExtra("show_alert", true);
// Send local broadcast
                                localBroadcastManager.sendBroadcast(localIntent);
                            }
                        }
                    }
                }

            }
        };

        excecuter = mTimer.scheduleAtFixedRate(timerRunnable, 0, TIMER_INTERVAL, TimeUnit.MILLISECONDS);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (servicesConnected() && mLastLocation != null && mLastLocation.hasAccuracy() && mLastLocation.getAccuracy() <= slabAccuracy) {
                        Systems.out.println("nnn---onConnected!!!!!!%%%%%%");

                        currentLatitude = mLastLocation.getLatitude();
                        currentLongtitude = mLastLocation.getLongitude();
                        sLocation = currentLatitude + "," + currentLongtitude + "NAN5" + "|";
                        if (mLastLocation.hasBearing())
                            bearing = String.valueOf(mLastLocation.getBearing());
                    } else {

                    }
                } else {
//                            Toast.makeText(LocationUpdate.this, "No Last known location found. Try current location..!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void utilizeLocationToCalcDistance() {
        if (mLastLocation != null) {
            Location location = mLastLocation;


            if(mMyStatus.getOnstatus().equals("Complete"))
            {


                findhillsStation(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            }

//            if(mMyStatus.getOnstatus().equals("Complete"))
//            {
//                double cityCenterLat = Double.valueOf(SessionSave.getSession("city_latitude", LocationUpdate.this));
//                double cityCenterLon = Double.valueOf(SessionSave.getSession("cityLongitude", LocationUpdate.this));
//                double cityRadius = Double.valueOf(SessionSave.getSession("cityRadius", LocationUpdate.this)); // Assume radius is in kilometers
//                System.out.println("citylimt_test: " + cityCenterLat);
//                System.out.println("citylimt_test: " + cityCenterLon);
//                System.out.println("citylimt_test: " + cityRadius);
//// Calculate the distance from the user's current location to the city center
//                double distanceFromCityCenter = haversineRadius(cityCenterLat, cityCenterLon, mLastLocation.getLatitude(), mLastLocation.getLongitude());
//                System.out.println("citylimt_test " + "distanceFromCityCenter" + " " + distanceFromCityCenter);
//
//                if (distanceFromCityCenter <= cityRadius) {
//                    // User is within the city limits
//                    System.out.println("citylimt_test " + "inside" + " " + mMyStatus.getOnstatus());
//                    isOutsideCity = false;
//                } else {
//                    // User is outside the city limits
//                    System.out.println("citylimt_test " + "outside" + " " + mMyStatus.getOnstatus());
//                    isOutsideCity = true;
//                }
//            }




            if (SessionSave.getSession("status", LocationUpdate.this).equalsIgnoreCase("A") || SessionSave.getSession("status", LocationUpdate.this).equalsIgnoreCase("B")) {
                if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
                    if (location.hasAccuracy() && location.getAccuracy() <= slabAccuracy) {
                        if (mLastLocationTemp != null) {
                            if (mLastLocationTemp.getLatitude() != 0.0 && mLastLocationTemp.getLongitude() != 0.0) {
                                System.out.println("Nan distance mLastLocationTemp" + "___1");
                                if (mLastLocationTemp.getLatitude() != location.getLatitude() && mLastLocationTemp.getLongitude() != location.getLongitude()) {
                                    System.out.println("Nan distance mLastLocationTemp" + "mLastLocationTemp" + mLastLocationTemp + "___location" + location);
                                    double distance = DistanceMatrixUtil.INSTANCE.calculateDistance(SessionSave.getSession("Metric", LocationUpdate.this).trim(), new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(mLastLocationTemp.getLatitude(), mLastLocationTemp.getLongitude()));
                                    localDistance = localDistance + distance;
                                    sLocation += location.getLatitude() + "," + location.getLongitude() + "," + location.getAccuracy() + "," + speed + "," + SessionSave.getDistance(LocationUpdate.this) + "," + distance + "," + DateFormat.getTimeInstance().format(new Date()) + "|";
                                    distanceCalculation(location, mLastLocationTemp);
//                                  if (isOutsideCity)
//                                  {
//                                      System.out.println("getDistanceCityLimit"+SessionSave.getDistanceCityLimit(LocationUpdate.this));
//                                      System.out.println("getDistanceCityLimit"+SessionSave.getSession("city_limit_distance",LocationUpdate.this));
//if(SessionSave.getSession("isstart_trip",LocationUpdate.this).equals("true"))
//                                      {
//                                          SessionSave.setDistanceCityLimit(0.0, LocationUpdate.this);
//
//                                          SessionSave.saveSession("isstart_trip","false",LocationUpdate.this);
//                                          SessionSave.saveSession("city_limit_distance","0.0",LocationUpdate.this);
//
//                                      }
//                                      System.out.println("getDistanceCityLimit"+SessionSave.getDistanceCityLimit(LocationUpdate.this));
//
//
//                                      distanceCalculationCityLimit(location, mLastLocationTemp);
//
//                                  }


                                }
                                mLastLocationTemp = location;
                            } else {
                                System.out.println("Nan distance mLastLocationTemp" + "___2");
                                sLocation += currentLatitude + "," + currentLongtitude + "," + location.getAccuracy() + "," + speed + "," + SessionSave.getDistance(LocationUpdate.this) + ",1" + "|";
                                mLastLocationTemp = location;
                            }
                        } else {
                            System.out.println("Nan distance mLastLocationTemp" + "___3");
                            sLocation += currentLatitude + "," + currentLongtitude + "," + location.getAccuracy() + "," + speed + "," + SessionSave.getDistance(LocationUpdate.this) + ",2" + "|";
                            mLastLocationTemp = location;
                        }
                    }
                }
            } else {
                if (SessionSave.getSession("status", LocationUpdate.this).equalsIgnoreCase("F")) {
                    if (location != null)
                        if (location.getAccuracy() <= slabAccuracy) {
                            sLocation = currentLatitude + "," + currentLongtitude + "|";
                            lastlatitude = currentLatitude;
                            lastlongitude = currentLongtitude;
                        } else {
                            if (lastlatitude != 0.0 && lastlongitude != 0.0) {
                                sLocation = lastlatitude + "," + lastlongitude + "|";
                            }
                        }
                } else if (SessionSave.getSession("status", LocationUpdate.this).equalsIgnoreCase("B")) {
                    if (location != null) {
                        if (location.getAccuracy() <= slabAccuracy) {
                            sLocation = currentLatitude + "," + currentLongtitude + "|";
                            lastlatitude = currentLatitude;
                            lastlongitude = currentLongtitude;
                        } else {
                            if (lastlatitude != 0.0 && lastlongitude != 0.0) {
                                sLocation = lastlatitude + "," + lastlongitude + "|";
                            }
                        }
                    }
                }
                mLastLocationTemp = null;
            }
        }
    }

    private void distanceCalculation(Location location, Location lastLatLng) {
        DistanceCalculation(location, lastLatLng);
        SessionSave.saveLastLng(new LatLng(location.getLatitude(), location.getLongitude()), LocationUpdate.this);
    }

    private void distanceCalculationCityLimit(Location location, Location lastLatLng) {
        DistanceCalculationCityLimit(location, lastLatLng);
      //  SessionSave.saveLastLng(new LatLng(location.getLatitude(), location.getLongitude()), LocationUpdate.this);
    }

    private void createLocationLog() {
        if (logLocationInterval == (TIMER_INTERVAL * 60)) {
            logLocationInterval = 0L;
            float totalAccuracy = 0.0f;
            for (Float accuracy : locationAccuracyList) {
                totalAccuracy += accuracy;
            }
//            errorLogRepository.insertLocationLog(new LocationModel(GetUTCdatetimeAsString(), new LatLng(currentLatitude, currentLongtitude), String.valueOf(totalAccuracy / locationAccuracyList.size()), SessionSave.getSession("travel_status", LocationUpdate.this)));
            locationAccuracyList.clear();
        } else
            logLocationInterval += TIMER_INTERVAL;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }
    public void setMoveTrack(TrackMovement moveTrack) {
        this.mTrackMovent = moveTrack;
    }
    public void cancelNotify() {
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    @Override
    public void onDestroy() {
        SessionSave.saveSession(CommonData.SERVICE_STOPPED_TIME, GetUTCdatetimeAsString(), LocationUpdate.this);
        if (localBroadcastManager != null) {
            Intent localIntent = new Intent(LOCATION_ACCURACY_LOW);
            localIntent.putExtra("show_alert", false);
            localBroadcastManager.sendBroadcast(localIntent);
        }
        // unregister local broadcast
        this.localBroadcastManager.unregisterReceiver(listener);
        stopWaitingTime();
        try {
            removeLocationUpdates();
            if (SessionSave.getSession("Id", LocationUpdate.this).equals("")) {
                stopLocationUpdates();
            } else {
                stopLocationUpdates();
            }
            if (mTimer != null) {
                mTimer.shutdown();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        stopForeground(true);
        Systems.out.println("LocationUpdate.sTimer" + LocationUpdate.sTimer);
        super.onDestroy();
    }


    public void startWaitingTime() {
        if (!SessionSave.getSession("trip_id", LocationUpdate.this).equals("") && SessionSave.getSession("travel_status", LocationUpdate.this).equalsIgnoreCase("2")) {
            waitingTimeRunning = true;
            startTime = SystemClock.uptimeMillis();

            timeInMillies = SystemClock.uptimeMillis() - startTime;
            timeSwap = SessionSave.getWaitingTime(LocationUpdate.this);
            if (SessionSave.getSession("taxi_speed", LocationUpdate.this).trim().equals(""))
                SessionSave.saveSession("taxi_speed", "0.0", LocationUpdate.this);

            if (myHandler != null) {
                if (updateTimerMethod != null) {
                    myHandler.removeCallbacks(updateTimerMethod);
                }
                myHandler.postDelayed(updateTimerMethod, 1000);
            }
        }
    }

    public void stopWaitingTime() {
        waitingTimeRunning = false;
        if (myHandler != null) {
            myHandler.removeCallbacks(updateTimerMethod);
        }
        timeSwap += SessionSave.getWaitingTime(LocationUpdate.this);
    }

    /**
     * Calculates the Internal distance that travel by fleet during active status
     */
    public void DistanceCalculation(Location currentLocation, Location to) {
        haversine(currentLocation.getLatitude(), currentLocation.getLongitude(), to.getLatitude(),
                to.getLongitude());
    }

    public void DistanceCalculationCityLimit(Location currentLocation, Location to) {
        haversineCityLimit(currentLocation.getLatitude(), currentLocation.getLongitude(), to.getLatitude(),
                to.getLongitude());
    }



    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Systems.out.println("nnn---onConnected LocationResult!!!!!!%%%%%%^^^^^&&&&");
            for (Location location : locationResult.getLocations()) {
                if (location != null && location.getLatitude() != 0.0) {
                    if (location.hasAccuracy() && location.getAccuracy() < 250 && sLocation.isEmpty()) {
                        location.setAccuracy(45.0f);
                    }

                    try {
                        location.setLatitude(Double.parseDouble(latlngdf.format(location.getLatitude())));
                        location.setLongitude(Double.parseDouble(latlngdf.format(location.getLongitude())));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String mLastUpdateTime = DateFormat.getTimeInstance().format(mDateObject);
                    double _speed = location.getSpeed();
                    speed = roundDecimal(convertSpeed(_speed), 2);
                    if (!SessionSave.getSession("Metric", LocationUpdate.this).equalsIgnoreCase("KM")) {
                        try {
                            speed = Double.parseDouble(FontHelper.convertfromArabic(String.valueOf(speed))) / 1.60934;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    currentLatitude = location.getLatitude();
                    currentLongtitude = location.getLongitude();
                    currentAccuracy = location.getAccuracy();
                    locationAccuracyList.add(location.getAccuracy());
                    SessionSave.saveSession(CommonData.SOS_LAST_LAT, String.valueOf(currentLatitude), LocationUpdate.this);
                    SessionSave.saveSession(CommonData.SOS_LAST_LNG, String.valueOf(currentLongtitude), LocationUpdate.this);
                    if (SessionSave.getSession("taxi_speed", LocationUpdate.this).trim().equals(""))
                        SessionSave.saveSession("taxi_speed", "0.0", LocationUpdate.this);
                    double taxiMinimumSpeed = Double.parseDouble(SessionSave.getSession("taxi_speed", LocationUpdate.this));

                    if (speed <= taxiMinimumSpeed) {
                        if (!waitingTimeRunning && !SessionSave.getSession(CommonData.WAITING_TIME_MANUAL, LocationUpdate.this, false))
                            startWaitingTime();

                        if (!waitingTimeRunning && SessionSave.getSession(CommonData.WAITING_TIME_MANUAL, LocationUpdate.this, false) && (SessionSave.getSession(CommonData.WAITING_TIME, LocationUpdate.this, false) || SessionSave.getSession(CommonData.ST_WAITING_TIME, LocationUpdate.this, false))) {
                            startWaitingTime();
                        }
                    } else if (waitingTimeRunning && !SessionSave.getSession(CommonData.WAITING_TIME_MANUAL, LocationUpdate.this, false))
                        stopWaitingTime();
//                    else  if (!waitingTimeRunning && SessionSave.getSession(CommonData.WAITING_TIME_MANUAL, LocationUpdate.this, false) &&(SessionSave.getSession(CommonData.WAITING_TIME, LocationUpdate.this, false)|| SessionSave.getSession(CommonData.ST_WAITING_TIME, LocationUpdate.this, false))){
//                        startWaitingTime();
//                    }

                    if (!SessionSave.getSession("trip_id", LocationUpdate.this).equals("")) {
                        if (speed > 5) {
                            SessionSave.saveSession(CommonData.LAST_KNOWN_LAT, String.valueOf(currentLatitude), LocationUpdate.this);
                            SessionSave.saveSession(CommonData.LAST_KNOWN_LONG, String.valueOf(currentLongtitude), LocationUpdate.this);
                        }
                    } else {
                        SessionSave.saveSession(CommonData.LAST_KNOWN_LAT, String.valueOf(currentLatitude), LocationUpdate.this);
                        SessionSave.saveSession(CommonData.LAST_KNOWN_LONG, String.valueOf(currentLongtitude), LocationUpdate.this);
                    }

                    mLastLocation = location;
                    Systems.out.println("onNewLocationAvailable " + startID + "__" + mLastUpdateTime + "_" + SessionSave.getSession("status", LocationUpdate.this) + "++" + currentLatitude + "__" + speed + "__" + location.getAccuracy());
                }
            }
        }


    };

    private void stopLocationUpdates() {
        Systems.out.println("disconnect " + "stopLocationUpdates");
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private synchronized void getAndStoreStringValues(String result) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
            Document doc = dBuilder.parse(is);
            Element element = doc.getDocumentElement();
            element.normalize();
            NodeList nList = doc.getElementsByTagName("*");
            int chhh = 0;
            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    chhh++;

                    Element element2 = (Element) node;
                    NC.nfields_byName.put(element2.getAttribute("name"), element2.getTextContent());

                }
            }
            getValueDetail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized void getValueDetail() {
        Field[] fieldss = R.string.class.getDeclaredFields();
        for (Field field : fieldss) {
            int id = getResources().getIdentifier(field.getName(), "string", getPackageName());
            if (NC.nfields_byName.containsKey(field.getName())) {
                NC.fields.add(field.getName());
                NC.fields_value.add(getResources().getString(id));
                NC.fields_id.put(field.getName(), id);

            }
        }
        for (Map.Entry<String, String> entry : NC.nfields_byName.entrySet()) {
            String h = entry.getKey();
            String value = entry.getValue();
            NC.nfields_byID.put(NC.fields_id.get(h), NC.nfields_byName.get(h));
            // do stuff
        }

    }

    synchronized void getColorValueDetail() {
        Field[] fieldss = R.color.class.getDeclaredFields();
        // fields =new int[fieldss.length];
        for (Field field : fieldss) {
            int id = getResources().getIdentifier(field.getName(), "color", getPackageName());
            if (CL.nfields_byName.containsKey(field.getName())) {
                CL.fields.add(field.getName());
                CL.fields_value.add(getResources().getString(id));
                CL.fields_id.put(field.getName(), id);

            }
        }

        for (Map.Entry<String, String> entry : CL.nfields_byName.entrySet()) {
            String h = entry.getKey();
            String value = entry.getValue();
            CL.nfields_byID.put(CL.fields_id.get(h), CL.nfields_byName.get(h));
            // do stuff
        }

    }

    private double convertSpeed(double speed) {
        return ((speed * 3600) * 0.001);
    }

    private double roundDecimal(double value, final int decimalPlace) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
        value = bd.doubleValue();
        return value;
    }

    /**
     * This Function is used for calculate the distance travelled
     */
    public synchronized void haversine(double lat1, double lon1,
                                       double lat2, double lon2) {
        double tempDistance = 0.0;

        LatLng from = new LatLng(lat1, lon1);
        LatLng to = new LatLng(lat2, lon2);

        //Calculating the distance in meters
        double distance = DistanceMatrixUtil.INSTANCE.calculateDistance(SessionSave.getSession("Metric", LocationUpdate.this).trim(), from, to);
        Systems.out.println("Nan distance mLastLocationTemp" + "distance" + distance + "FROM" + from + "TOOO" + to);
//        tempDistance = distance * 1000;
        if (distance > 0) {
            if (distance < 5) {
                Systems.out.println("sabari distance mLastLocationTemp" + "___4" + SessionSave.getDistance(LocationUpdate.this));
                Systems.out.println("sabari Distan Calc" + distance + "___Total Dis" + (distance + SessionSave.getDistance(LocationUpdate.this)) + "____Time" + DateFormat.getTimeInstance().format(new Date()) + "___haversine");
                distance += SessionSave.getDistance(LocationUpdate.this);
                SessionSave.setDistance(distance, LocationUpdate.this);
                SessionSave.setArriveDistance(distance, LocationUpdate.this);
                SessionSave.saveWaypoints(from, to, "haversine", distance, "___" + startID, LocationUpdate.this);
            } else {
                Systems.out.println("Nan distance mLastLocationTemp" + "___5");
                DISTANCE_CALCULATION_INPROGRESS = true;
                SessionSave.saveWaypoints(new LatLng(from.latitude, from.longitude), new LatLng(to.latitude, to.longitude), "googleDistanceCall", 0.0, "server" + "___" + startID, LocationUpdate.this);
                new FindApproxDistance(this).getDistance(this, to.latitude, to.longitude, from.latitude, from.longitude);
            }
        }


        SessionSave.saveSession(CommonData.LAST_KNOWN_LAT, String.valueOf(to.latitude), LocationUpdate.this);
        SessionSave.saveSession(CommonData.LAST_KNOWN_LONG, String.valueOf(to.longitude), LocationUpdate.this);


    }

    public synchronized void haversineCityLimit(double lat1, double lon1,
                                                  double lat2, double lon2) {
        double tempDistance = 0.0;
        System.out.println("getDistanceCityLimit"+SessionSave.getDistanceCityLimit(LocationUpdate.this));

        System.out.println("getDistanceCityLimit" +" "+" from "+ lat1 + ","+lon1);
        System.out.println("getDistanceCityLimit" +" "+" To "+ lat2 + ","+lon2);

        LatLng from = new LatLng(lat1, lon1);
        LatLng to = new LatLng(lat2, lon2);

        //Calculating the distance in meters
        double distances = DistanceMatrixUtil.INSTANCE.calculateDistance(SessionSave.getSession("Metric", LocationUpdate.this).trim(), from, to);


        System.out.println("getDistanceCityLimit" +" "+" distance "+ distances);
        System.out.println("distance_getDistanceCityLimit" +" "+" distance "+ distances);

//        tempDistance = distance * 1000;s
        if (distances> 0) {
            if (distances < 5) {
                System.out.println("getDistanceCityLimit"+SessionSave.getDistanceCityLimit(LocationUpdate.this));
                System.out.println("getDistanceCityLimit_sting"+SessionSave.getSession("city_limit_distance",LocationUpdate.this));
                double newdistance = 0.0;
                if(SessionSave.getSession("city_limit_distance",LocationUpdate.this).equals("0.0"))
                {
                    newdistance =  distances ;
                }
                {
                    newdistance = distances + Double.valueOf(SessionSave.getSession("city_limit_distance",LocationUpdate.this));

                }
                SessionSave.saveSession("city_limit_distance",String.valueOf(newdistance),LocationUpdate.this);

                distances += SessionSave.getDistanceCityLimit(LocationUpdate.this);
                SessionSave.setDistanceCityLimit(distances, LocationUpdate.this);


            }



        }


        SessionSave.saveSession(CommonData.LAST_KNOWN_LAT, String.valueOf(to.latitude), LocationUpdate.this);
        SessionSave.saveSession(CommonData.LAST_KNOWN_LONG, String.valueOf(to.longitude), LocationUpdate.this);


    }

    public synchronized double haversineRadius(double lat1, double lon1,
                                                  double lat2, double lon2) {
        double tempDistance = 0.0;

        LatLng from = new LatLng(lat1, lon1);
        LatLng to = new LatLng(lat2, lon2);

        //Calculating the distance in meters
        double distance = DistanceMatrixUtil.INSTANCE.calculateDistance(SessionSave.getSession("Metric", LocationUpdate.this).trim(), from, to);
        Systems.out.println("Nan distance mLastLocationTemp" + "distance" + distance + "FROM" + from + "TOOO" + to);
        return distance;

    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     */
    public void removeLocationUpdates() {
        try {
            this.stopSelf();
        } catch (SecurityException unlikely) {
            unlikely.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("Wakelock")
    public String DriverStatusUpdate(final String id, final String status, final String gcmid) {
        String r_message = "";
        try {


            if (NetworkStatus.isOnline(LocationUpdate.this) && CallReceiver.phoneState()) {
                updateLocation = sLocation;
                SessionSave.saveSession(CommonData.DRIVER_LOCATION_STATIC, updateLocation, LocationUpdate.this);
                SessionSave.saveSession(CommonData.DRIVER_LOCATION, updateLocation, LocationUpdate.this);

                ServiceGenerator.API_BASE_URL = SessionSave.getSession("base_url", LocationUpdate.this);
                if (!SessionSave.getSession("wholekey", LocationUpdate.this).trim().equals("")) {
                    if (NC.getString(R.string.ok) == null) {
                        //getAndStoreStringValues(SessionSave.getSession("wholekey", LocationUpdate.this));
                      //  getAndStoreColorValues(SessionSave.getSession("wholekeyColor", LocationUpdate.this));
                    }
                }
                if (SessionSave.getSession("status", LocationUpdate.this).equalsIgnoreCase("F")) {
                    sLocation = "";
                }
                JSONObject j = new JSONObject();
                j.put("driver_id", SessionSave.getSession("Id", LocationUpdate.this));
                j.put("trip_id", SessionSave.getSession("trip_id", LocationUpdate.this));

                Systems.out.println("NAN sLocation" + sLocation + "____" + updateLocation);
                //Need to handle When location Settings disable
                if (updateLocation.equals("")) {
                    updateLocation = "0.0,0.0|";
                    currentAccuracy = 10000;
                    SessionSave.saveSession(CommonData.DRIVER_LOCATION, updateLocation, LocationUpdate.this);
                }

                j.put("locations", SessionSave.getSession(CommonData.DRIVER_LOCATION, LocationUpdate.this).replace("null", ""));

                if (SessionSave.getSession("trip_id", LocationUpdate.this).equals("")) {
                    SessionSave.saveSession("status", "F", LocationUpdate.this);
                    SessionSave.saveSession("travel_status", "", LocationUpdate.this);
                }
                j.put("status", SessionSave.getSession("status", LocationUpdate.this));
                j.put("travel_status", SessionSave.getSession("travel_status", LocationUpdate.this));
                String mDeviceid = "";
                AtomicInteger c = new AtomicInteger(0);
                if (CommonData.mDevice_id.equals("")) {
                    if (!UUID.randomUUID().toString().equals("")) {
                        mDeviceid = UUID.randomUUID().toString();
                    } else {
                        mDeviceid = CommonData.mDevice_id_constant + c.incrementAndGet();
                    }
                    CommonData.mDevice_id = mDeviceid;
                }

                if (SessionSave.getSession("sDevice_id", LocationUpdate.this).equals("")) {
                    if (!UUID.randomUUID().toString().equals("")) {
                        mDeviceid = UUID.randomUUID().toString();
                    } else {
                        mDeviceid = CommonData.mDevice_id_constant + c.incrementAndGet();
                    }
                    SessionSave.saveSession("sDevice_id", mDeviceid, LocationUpdate.this);
                }

                j.put("device_token", SessionSave.getSession("sDevice_id", LocationUpdate.this));

//                j.put("device_token", Settings.Secure.getString(LocationUpdate.this.getContentResolver(), Settings.Secure.ANDROID_ID));
                j.put("device_type", "1");
                j.put("above_min_km", String.valueOf(CommonData.km_calc));
                j.put("bearings", mLastLocation != null ? mLastLocation.getBearing() : 0);
                j.put("distance", String.valueOf(SessionSave.getDistance(LocationUpdate.this)));
                j.put("shift_id", SessionSave.getSession("Shiftupdate_Id", LocationUpdate.this));
                j.put("driver_name", SessionSave.getSession("Name", LocationUpdate.this));
                j.put("driver_taxi_number", SessionSave.getSession("taxi_no", LocationUpdate.this));
                j.put("driver_taxi_model", SessionSave.getSession("model_name", LocationUpdate.this));
                j.put("waiting_hour", String.valueOf(Float.valueOf(Float.valueOf(SessionSave.getWaitingTime(LocationUpdate.this)) / 3600000)));
                j.put("accuracy", currentAccuracy);
                //     j.put("brand", Build.MANUFACTURER);
                //     j.put("model", Build.MODEL);
                j.put("service_status", SessionSave.getSession("service_status", LocationUpdate.this, false));
                j.put("version_code", BuildConfig.VERSION_CODE);
//                j.put("carrier_name", DeviceUtils.INSTANCE.getCarriername(LocationUpdate.this));
                j.put("carrier_name", "");
                data.put("data", j);
                data.put("platform", "ANDROID");
                data.put("app", "DRIVER");
                data.put("lang", SessionSave.getSession("Lang", LocationUpdate.this));
                data.put("id", SessionSave.getSession("Id", LocationUpdate.this));
                CoreClient client = null;
                if (SessionSave.getSession("travel_status", LocationUpdate.this).equals("2")) {
                    client = MyApplication.getInstance().getNodeApiManagerWithTimeOut(SessionSave.getSession(CommonData.NODE_URL, LocationUpdate.this), 12L);
//                    client = new NodeServiceGenerator(LocationUpdate.this, dont_encode, SessionSave.getSession(CommonData.NODE_URL, LocationUpdate.this), 12).createService(CoreClient.class);
                } else {
                    client = MyApplication.getInstance().getNodeApiManagerWithTimeOut(SessionSave.getSession(CommonData.NODE_URL, LocationUpdate.this), 8L);
//                    client = new NodeServiceGenerator(LocationUpdate.this, dont_encode, SessionSave.getSession(CommonData.NODE_URL, LocationUpdate.this), 8).createService(CoreClient.class);
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), data.toString());

                Call<ResponseBody> coreResponse = client.nodeUpdate(body, CommonData.getTime(LocationUpdate.this), SessionSave.getSession("trip_id", LocationUpdate.this).equals("") ? "5" : "10");

                coreResponse.enqueue(new RetrofitCallbackClass<>(LocationUpdate.this, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        String datas = null;
                        JSONObject json = null;
                        if (!SessionSave.getSession("status", LocationUpdate.this).equalsIgnoreCase("F")) {
                            sLocation = "";
                        }
                        try {
                            datas = response.body().string();
                            if (datas != null) {
                                driverResponseHandling(new JSONObject(datas));
                            } else {
                                errorCount += 1;
                                updateLocation = "";
                                SessionSave.saveSession(CommonData.DRIVER_LOCATION, "", LocationUpdate.this);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            errorCount += 1;
                            updateLocation = "";
                            ErrorLogRepository.getRepository(LocationUpdate.this).insertAllApiErrorLogs(new ApiErrorModel(0, CommonData.getCurrentTimeForLogger(), "type=driver_location_history_update", ExceptionConverter.INSTANCE.buildStackTraceString(e.getStackTrace()), DriverUtils.INSTANCE.driverInfo(LocationUpdate.this), data, LocationUpdate.this.getClass().getSimpleName(), 0));
                            SessionSave.saveSession(CommonData.DRIVER_LOCATION, "", LocationUpdate.this);
                            //CToast.ShowToast(LocationUpdate.this, NC.getString(R.string.server_error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                        updateLocation = "";
                        errorCount += 1;
//                        ErrorLogRepository.getRepository(LocationUpdate.this).insertAllApiErrorLogs(new ApiErrorModel(0,"2019-03-28", "type=driver_location_history_update",ExceptionConverter.INSTANCE.buildStackTraceString(t.getStackTrace()), DriverUtils.INSTANCE.driverInfo(LocationUpdate.this), data, LocationUpdate.this.getClass().getSimpleName(),0));
                        ErrorLogRepository.getRepository(LocationUpdate.this).insertAllApiErrorLogs(new ApiErrorModel(0, CommonData.getCurrentTimeForLogger(), "type=driver_location_history_update", ExceptionConverter.INSTANCE.buildStackTraceString(t.getStackTrace()), DriverUtils.INSTANCE.driverInfo(LocationUpdate.this), data, LocationUpdate.this.getClass().getSimpleName(), 0));
                        SessionSave.saveSession(CommonData.DRIVER_LOCATION, "", LocationUpdate.this);
                        // CToast.ShowToast(LocationUpdate.this, NC.getString(R.string.server_error));
                    }
                }));
            }


            SessionSave.saveSession("status", SessionSave.getSession("status", LocationUpdate.this), LocationUpdate.this);
            if (SessionSave.getSession("base_url", LocationUpdate.this).trim().equals("")) {
                ServiceGenerator.API_BASE_URL = SessionSave.getSession("base_url", LocationUpdate.this);
            //    getAndStoreStringValues(SessionSave.getSession("wholekey", LocationUpdate.this));
            //    getAndStoreColorValues(SessionSave.getSession("wholekeyColor", LocationUpdate.this));
            }
        } catch (final Exception e) {
            e.printStackTrace();
            ErrorLogRepository.getRepository(LocationUpdate.this).insertAllApiErrorLogs(new ApiErrorModel(0, CommonData.getCurrentTimeForLogger(), "type=driver_location_history_update", ExceptionConverter.INSTANCE.buildStackTraceString(e.getStackTrace()), DriverUtils.INSTANCE.driverInfo(LocationUpdate.this), data, LocationUpdate.this.getClass().getSimpleName(), 0));
        }

        return r_message;
    }

    private void driverResponseHandling(JSONObject json) {

        try {
            Systems.out.println("GCMM____driverResponseHandling_____" + json.getInt("status"));

            /**
             * To handle IF driver has trip but driver loc history updated as free
             * Need to move to ongoing screen
             */
            if (json.has("current_trip_id")) {
                if (!json.getString("current_trip_id").equals("0") && SessionSave.getSession("status", LocationUpdate.this).equals("F")) {
                    cancelNotify();
                    generateNotifications(LocationUpdate.this, json.getString("message"), OngoingAct.class, false, Notification_ID);
                    Intent ongoing = new Intent();
                    Bundle extras = new Bundle();
                    extras.putString("alert_message", "");
                    extras.putString("status", json.getString("status"));
                    SessionSave.saveSession("trip_id", json.getString("current_trip_id"), LocationUpdate.this);
                    ongoing.putExtras(extras);
                    ongoing.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    ComponentName cn = new ComponentName(LocationUpdate.this, OngoingAct.class);
                    ongoing.setComponent(cn);
                    getApplication().startActivity(ongoing);
                } else {

                }
            }

            if (json.getInt("status") == 1) {
                updateLocation = "";
                errorCount = 0;
                Systems.out.println("ssssssres____________" +"Location updated");
                SessionSave.saveSession(CommonData.DRIVER_LOCATION, "", LocationUpdate.this);
                if (SessionSave.getSession("status", LocationUpdate.this).equals("A")) {
                    if (streetPickupInterface != null) {

                        final JSONObject jsons = json;
                        if (mhandler != null) {
                            mhandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        streetPickupInterface.updateFare(jsons.getString("trip_fare"), mLastLocation);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                    }
//                    CommonData.travel_km = json.getDouble("distance");
/*
                    try {
                        CommonData.DISTANCE_FARE = json.getString("trip_fare");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                } else {
                    CommonData.travel_km = 0;
                    SessionSave.setGoogleDistance(0f, LocationUpdate.this);
                    SessionSave.setDistance(0f, LocationUpdate.this);
                    SessionSave.setDistanceCityLimit(0f, LocationUpdate.this);
                    SessionSave.saveWaypoints(null, null, "", 0.0, "___" + startID, LocationUpdate.this);
                    SessionSave.saveGoogleWaypoints(null, null, "", 0.0, "", LocationUpdate.this);
                }

            } else if (json.getInt("status") == 5)
            {
                // notificationforTrip();
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                boolean isScreenOn = pm.isScreenOn();
                if (!isScreenOn) {
                    @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
                    wl.acquire(10000);
                    @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");

                    wl_cpu.acquire(10000);
                }
                JSONObject trip_details = json.getJSONObject("trip_details");
                if (trip_details != null) {
                    JSONObject booking_details = trip_details.getJSONObject("booking_details");
                    if (booking_details != null) {

                        Pickup_lat = Double.valueOf(booking_details.getString("pickup_latitude"));
                        Pickup_long = Double.valueOf(booking_details.getString("pickup_longitude"));



//                        NotificationAct.LatlongValue(Pickup_lat, Pickup_long, currentLatitude, currentLongtitude);
                    }
                }
                /*int time_out = json.getJSONObject("trip_details").getInt("notification_time");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(CommonData.current_trip_accept==0) {
                                json.getJSONObject("trip_details").getString("notification_time");
                                JSONObject j = new JSONObject();
                                j.put("trip_id", json.getJSONObject("trip_details").getString("passengers_log_id"));
                                j.put("driver_id", SessionSave.getSession("Id", LocationUpdate.this));
                                j.put("taxi_id", SessionSave.getSession("taxi_id", LocationUpdate.this));
                                j.put("company_id", SessionSave.getSession("company_id", LocationUpdate.this));
                                j.put("reason", "");
                                j.put("reject_type", "0");
                                final String Url = "type=reject_trip";
                                new TripRejectLocationUpdate(Url, j);
                                CommonData.current_trip_accept=1;
                            }


                        } catch (Exception e) {

                        }
                    }
                },time_out*1000);
*/

                Intent intent = new Intent();
                intent.putExtra("message", json.toString());
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                ComponentName cn = new ComponentName(LocationUpdate.this, TripNotificationActivity.class);
                intent.setComponent(cn);
                startActivity(intent);

            } else if (json.getInt("status") == 7 || json.getInt("status") == 10) {
                CommonData.current_trip_accept = 0;
                Systems.out.println("VVVVVVVVVVv" + json.getInt("status"));
                final JSONObject jsons = json;
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String cancelmsg = "";
                            LocationUpdate.ClearSessionwithTrip(LocationUpdate.this);
//                            stopService(new Intent(LocationUpdate.this, WaitingTimerRun.class));

                            generateNotifications(LocationUpdate.this, jsons.getString("message"), HomePageActivity.class, true, Notification_ID);
                            cancelmsg = jsons.getString("message");
                            mMyStatus.setStatus("F");
                            SessionSave.saveSession("status", "F", LocationUpdate.this);
                            mMyStatus.settripId("");
                            SessionSave.saveSession("trip_id", "", LocationUpdate.this);
                            SessionSave.setWaitingTime(0L, LocationUpdate.this);
                            mMyStatus.setOnstatus("");
                            mMyStatus.setOnPassengerImage("");
                            mMyStatus.setOnpassengerName("");
                            mMyStatus.setOndropLocation("");
                            mMyStatus.setOnpickupLatitude("");
                            mMyStatus.setOnpickupLongitude("");
                            mMyStatus.setOndropLatitude("");
                            mMyStatus.setOndropLongitude("");
                            mMyStatus.setOndriverLatitude("");
                            mMyStatus.setOndriverLongitude("");
                            SessionSave.saveSession(CommonData.ST_WAITING_TIME, false, getApplicationContext());
                            SessionSave.saveSession(CommonData.WAITING_TIME, false, getApplicationContext());
                            /*sabari coding*/
                            Toast.makeText(LocationUpdate.this, cancelmsg, Toast.LENGTH_LONG).show();

                            Intent cancelIntent = new Intent();
                            //Bundle bun = new Bundle();
                            // bun.putString("message", cancelmsg);
                            //cancelIntent.putExtras(bun);
                            cancelIntent.setAction(Intent.ACTION_MAIN);
                            cancelIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                            cancelIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            ComponentName cn = new ComponentName(getApplicationContext(), HomePageActivity.class);
                            cancelIntent.setComponent(cn);
                            startActivity(cancelIntent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, 100);
            } else if (json.getInt("status") == 15 || json.getInt("status") == -15) {
                CommonData.current_trip_accept = 0;
                Systems.out.println("lTaximobilityut_____" + json);
                LocationUpdate.this.stopSelf();
                int length = CommonData.mActivitylist.size();
                if (length != 0) {
                    for (int i = 0; i < length; i++) {
                        CommonData.mActivitylist.get(i).finish();
                    }
                }

                try {
                    SessionSave.saveSession("status", "", LocationUpdate.this);
                    SessionSave.saveSession("Id", "", LocationUpdate.this);
                    SessionSave.saveSession(CommonData.DRIVER_LOCATION, "", LocationUpdate.this);
                    SessionSave.saveSession("driver_id", "", LocationUpdate.this);
                    SessionSave.saveSession("Name", "", LocationUpdate.this);
                    SessionSave.saveSession("company_id", "", LocationUpdate.this);
                    SessionSave.saveSession("bookedby", "", LocationUpdate.this);
                    SessionSave.saveSession("p_image", "", LocationUpdate.this);
                    SessionSave.saveSession("Email", "", LocationUpdate.this);
                    SessionSave.saveSession("trip_id", "", LocationUpdate.this);
                    SessionSave.saveSession("phone_number", "", LocationUpdate.this);
                    SessionSave.saveSession("driver_password", "", LocationUpdate.this);
                    SessionSave.setWaitingTime(0L, LocationUpdate.this);

                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                SessionSave.saveSession(CommonData.USER_KEY, "", LocationUpdate.this);
                Intent intent = new Intent();
                Bundle bun = new Bundle();
                bun.putString("alert_message", json.getString("message"));
                intent.putExtras(bun);
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                ComponentName cn = new ComponentName(LocationUpdate.this, DriverLoginActivity.class);
                intent.setComponent(cn);
                startActivity(intent);
            }

            // For getting SMS From Passenger
            else if (json.getInt("status") == 11) {
                CommonData.current_trip_accept = 0;
                cancelNotify();
                generateNotifications(LocationUpdate.this, json.getString("message"), OngoingAct.class, false, Notification_ID);
                Intent ongoing = new Intent();
                Bundle extras = new Bundle();
                String lTaximobilityutlmsg = "";
                lTaximobilityutlmsg = json.getString("message");
                extras.putString("alert_message", lTaximobilityutlmsg);
                extras.putString("status", json.getString("status"));
                ongoing.putExtras(extras);
                ongoing.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                ComponentName cn = new ComponentName(LocationUpdate.this, OngoingAct.class);
                ongoing.setComponent(cn);
                getApplication().startActivity(ongoing);
            } else if (json.getInt("status") == 16) {
                CommonData.current_trip_accept = 0;
                LocationUpdate.this.stopSelf();
                String lTaximobilityutlmsg = "";
                lTaximobilityutlmsg = json.getString("message");

                Toast.makeText(LocationUpdate.this, lTaximobilityutlmsg, Toast.LENGTH_LONG).show();

                Intent intent = new Intent();
                //intent.putExtra("message", lTaximobilityutlmsg);
                // intent.putExtra("status", json.getInt("status"));
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                ComponentName cn = new ComponentName(LocationUpdate.this, HomePageActivity.class);

                intent.setComponent(cn);
                startActivity(intent);
            } else if (json.getInt("status") == -4 || json.getInt("status") == -3) {
                CommonData.current_trip_accept = 0;
                updateLocation = "";
                SessionSave.saveSession(CommonData.DRIVER_LOCATION, "", LocationUpdate.this);
            } else if (json.getInt("status") == -1) {
                CommonData.current_trip_accept = 0;
                updateLocation = "";
                SessionSave.saveSession(CommonData.DRIVER_LOCATION, "", LocationUpdate.this);
            } else if (json.getInt("status") == -101) {
                CommonData.current_trip_accept = 0;
                forceLogout();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ErrorLogRepository.getRepository(LocationUpdate.this).insertAllApiErrorLogs(new ApiErrorModel(0, CommonData.getCurrentTimeForLogger(), "type=driver_location_history_update", ExceptionConverter.INSTANCE.buildStackTraceString(e.getStackTrace()), DriverUtils.INSTANCE.driverInfo(LocationUpdate.this), data, LocationUpdate.this.getClass().getSimpleName(), 0));
        }

    }

    private void notificationforTrip() {
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, HomePageActivity.class), 0);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
        int notifyId = 10;
        Notification notification;
        Notification.Builder builder = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Action action = new Notification.Action.Builder(Icon.createWithResource(this, R.drawable.small_logo), NC.getString(R.string.notiy_lanch_app), activityPendingIntent).build();
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .addAction(action)
                    .setContentText("You have new trip")
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.small_logo)
                    .setWhen(System.currentTimeMillis());
        } else {
            builder = new Notification.Builder(this)
                    .addAction(0, getString(R.string.notiy_lanch_app)/* + getTripStatus()*/,
                            activityPendingIntent)
                    .setContentText("You have new trip")
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSmallIcon(R.drawable.small_logo)
                    .setWhen(System.currentTimeMillis());

        }

        notification = builder.build();
        //  notificationManager.notify(notifyId, notification);
        startForeground(notifyId, notification);
    }


    //Trip Reject Api

    public class TripRejectLocationUpdate implements APIResult {
        String msg;
        JSONObject jsonObject;

        public TripRejectLocationUpdate(final String url, JSONObject data) {
            jsonObject = data;
            new APIService_Retrofit_JSON(LocationUpdate.this, this, data, false).execute(url);
        }


        @Override
        public void getResult(final boolean isSuccess, final String result) {
            Log.d("result", "result" + result);
            try {
                if (isSuccess) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                CommonData.current_trip_accept = 0;

                                cancelNotifications(LocationUpdate.this, Notification_ID);
                            } catch (Exception e) {

                            }
                        }
                    }, 20 * 1000);
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 6) {
                        msg = json.getString("message");
                    } else if (json.getInt("status") == 7) {
                        msg = json.getString("message");
                    } else if (json.getInt("status") == 8) {
                        msg = json.getString("message");
                    } else if (json.getInt("status") != 6 || json.getInt("status") != 8 || json.getInt("status") != 3 || json.getInt("status") != 2 || json.getInt("status") != -1) {
                        msg = "Trip has been rejected";
                    } else {
                        msg = "Trip has been already cancelled";
                    }


                }
            } catch (final JSONException e) {

                e.printStackTrace();
            }
        }
    }

    public void cancelNotifications(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
        stopForeground(false);

    }

    private boolean servicesConnected() {
        // Check that Google Play services is available
        final int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        // Google Play services was not available for some reason
        // Display an error dialog
        return ConnectionResult.SUCCESS == resultCode;
    }

    public void generateNotifications(Context context, String message, Class<?> class1,
                                      boolean cancelable, int Notification_ID) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String title = getString(R.string.app_name);
        Intent notificationIntent = new Intent(this, class1);
        PendingIntent pendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT |  PendingIntent.FLAG_IMMUTABLE);

        } else {
            //    pendingIntent = PendingIntent.getActivity(this,
            //  0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);


        }


        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
        Notification myNotication;
        Notification.Builder builder = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(notificationChannel);

            builder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentText(message)
                    .setContentTitle(title)
                    .setOngoing(true)
                    .setSmallIcon(getNotificationIcon())
                    .setContentIntent(pendingIntent)
                    // .setLargeIcon(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.ic_launcher)).getBitmap())
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(message))
                    .setWhen(System.currentTimeMillis());
        } else {
            builder = new Notification.Builder(this)
                    .setAutoCancel(true)
                    .setTicker(getResources().getString(R.string.common_name))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setSmallIcon(getNotificationIcon())
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(message));
            // .setLargeIcon(((BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher)).getBitmap());

        }

        myNotication = builder.build();

        myNotication.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Notification_ID, myNotication);
        Uri notification1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        try {
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification1);
            r.play();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.small_logo : R.drawable.small_logo;
    }

    private boolean GPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private synchronized void getAndStoreColorValues(String result) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
            Document doc = dBuilder.parse(is);
            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("*");

            Systems.out.println("lislength" + nList.getLength());
            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element2 = (Element) node;
                    CL.nfields_byName.put(element2.getAttribute("name"), element2.getTextContent());

                }
            }
            getColorValueDetail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to logout user if status -101 and redirect to login page
     */
    private void forceLogout() {
        ServiceGenerator.API_BASE_URL = "";
        SessionSave.saveSession("base_url", "", LocationUpdate.this);
        SessionSave.saveSession("Id", "", LocationUpdate.this);
        SessionSave.clearAllSession(LocationUpdate.this);
        stopSelf();
//        stopService(new Intent(this, WaitingTimerRun.class));
        Intent intent = new Intent(LocationUpdate.this, DriverLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDistanceCalled(LatLng pick, LatLng drop, double distance, double time, String
            result, String status) {
        Systems.out.println("haiiiiiii " + "LocationUpdate " + pick.latitude + "__" + pick.longitude + "_____" + drop.latitude + "__" + drop.longitude + "___" + distance + "____" + status);
        if (status.equalsIgnoreCase("OK")) {

            SessionSave.setGoogleDistance(SessionSave.getGoogleDistance(LocationUpdate.this) + distance, LocationUpdate.this);

            SessionSave.saveSession(CommonData.LAST_KNOWN_LAT, String.valueOf(drop.latitude), LocationUpdate.this);
            SessionSave.saveSession(CommonData.LAST_KNOWN_LONG, String.valueOf(drop.longitude), LocationUpdate.this);
            if (SessionSave.getSession(CommonData.isGoogleDistance, LocationUpdate.this, true)) {
                SessionSave.saveGoogleWaypoints(pick, drop, "google", distance, "___" + startID + "____" + System.currentTimeMillis(), LocationUpdate.this);
                SessionSave.saveWaypoints(pick, drop, "google", distance, "server" + "___" + startID, LocationUpdate.this);

            } else {
                SessionSave.saveGoogleWaypoints(pick, drop, "mapbox", distance, "___" + startID, LocationUpdate.this);
                SessionSave.saveWaypoints(pick, drop, "mapbox", distance, "server" + "___" + startID, LocationUpdate.this);
            }
        } else {
            SessionSave.setGoogleDistance(distance, LocationUpdate.this);
            SessionSave.saveGoogleWaypoints(pick, drop, "haversine", distance, "UNKNOWN" + result, LocationUpdate.this);
            SessionSave.saveSession(CommonData.LAST_KNOWN_LAT, String.valueOf(drop.latitude), LocationUpdate.this);
            SessionSave.saveSession(CommonData.LAST_KNOWN_LONG, String.valueOf(drop.longitude), LocationUpdate.this);
            SessionSave.saveWaypoints(pick, drop, "google_haversine", distance, "___" + startID, LocationUpdate.this);
        }
        DISTANCE_CALCULATION_INPROGRESS = false;
    }

    private Notification getNotification() {

        PendingIntent activityPendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            activityPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, SplashAct.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE );


        } else {
            activityPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, SplashAct.class), 0);


        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
        int notifyId = 10;
        Notification notification;
        Notification.Builder builder = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Action action = new Notification.Action.Builder(Icon.createWithResource(this, R.drawable.small_logo), NC.getString(R.string.notiy_lanch_app), activityPendingIntent).build();
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .addAction(action)
                    .setContentText(NC.getString(R.string.app_running))
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.small_logo)
                    .setWhen(System.currentTimeMillis());
        } else {
            builder = new Notification.Builder(this)
                    .addAction(0, getString(R.string.notiy_lanch_app)/* + getTripStatus()*/,
                            activityPendingIntent)
                    .setContentText(NC.getString(R.string.app_running))
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSmallIcon(R.drawable.small_logo)
                    .setWhen(System.currentTimeMillis());
        }

        notification = builder.build();
        notificationManager.notify(notifyId, notification);
        return notification;
    }


    public void findhillsStation(double latitude, double longitude)
    {
        ElevationHelper elevationHelper = new ElevationHelper();


        elevationHelper.isHillStation(latitude,longitude,new ElevationHelper.ElevationCallback(){

            @Override
            public void onFailure(@NonNull String message) {
                System.out.println("hills_test"+ " "+ "not_working");

            }

            @Override
            public void onResult(boolean isHillStation, @NonNull String message) {
                System.out.println("hills_test"+ " "+ "working");
                System.out.println("hills_test"+ " "+ message);


                if (Double.valueOf(message)>= 1000) {
                    System.out.println("hills_test"+ " "+ " "+" hill");
                    SessionSave.saveSession("ishills","true",LocationUpdate.this);

                } else {
                    SessionSave.saveSession("ishills","false",LocationUpdate.this);

                    System.out.println("hills_test"+ " "+ " "+"Not hill");
                }


            }
        } );
    }
}


