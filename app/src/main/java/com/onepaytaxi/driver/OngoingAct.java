package com.onepaytaxi.driver;

import static com.onepaytaxi.driver.service.LocationUpdate.currentAccuracy;
import static com.onepaytaxi.driver.service.LocationUpdate.localDistance;
import static com.onepaytaxi.driver.service.LocationUpdate.runningFor;
import static com.onepaytaxi.driver.service.LocationUpdate.slabAccuracy;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.onepaytaxi.driver.adapter.SelectedServiceAdapter;
import com.onepaytaxi.driver.adapter.StopListAdapter;
import com.onepaytaxi.driver.data.CommonData;
import com.onepaytaxi.driver.data.MapWrapperLayout;
import com.onepaytaxi.driver.data.MystatusData;
import com.onepaytaxi.driver.homepage.HomePageActivity;
import com.onepaytaxi.driver.interfaces.APIResult;
import com.onepaytaxi.driver.interfaces.ClickInterface;
import com.onepaytaxi.driver.interfaces.GetAddress;
import com.onepaytaxi.driver.interfaces.LocalDistanceInterface;
import com.onepaytaxi.driver.interfaces.Pickupupdate;

import com.onepaytaxi.driver.route.Route;
import com.onepaytaxi.driver.route.StopData;
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON;
import com.onepaytaxi.driver.service.LocationUpdate;
import com.onepaytaxi.driver.service.NonActivity;
import com.onepaytaxi.driver.toll.TollInformation;
import com.onepaytaxi.driver.utils.CToast;

import com.onepaytaxi.driver.utils.FontHelper;
import com.onepaytaxi.driver.utils.GetAddressFromLatLng;
import com.onepaytaxi.driver.utils.GpsStatus;
import com.onepaytaxi.driver.utils.LatLngInterpolator;
import com.onepaytaxi.driver.utils.LocationUtils;
import com.onepaytaxi.driver.utils.NC;
import com.onepaytaxi.driver.utils.RoundedImageView;
import com.onepaytaxi.driver.utils.ServiceItem;
import com.onepaytaxi.driver.utils.SessionSave;
import com.onepaytaxi.driver.utils.Systems;
import com.onepaytaxi.driver.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.PolyUtil;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * This class will be called once the trip is accepted.Here we can start,end trip etc.
 */
@SuppressLint("DefaultLocale")
public class OngoingAct extends MainActivity implements ClickInterface, OnMapReadyCallback, LocalDistanceInterface, GoogleMap.OnCameraMoveStartedListener, GetAddress {
    //static member declarations
    public static final int MY_PERMISSIONS_REQUEST_CALL = 112;
    private static final int MY_PERMISSIONS_REQUEST_GPS = 113;
    private static boolean ROUTE_DRAW_ON_START, LOCATION_UPDATE_STOPPED;
    private static Pickupupdate sendPickupPoints;
    private final int LOCATION_REQUEST_TYPE_RETRY = 1;
    private final int LOCATION_REQUEST_TYPE_INITIAL = 2;
    private final int LOCATION_REQUEST_TYPE_COMPLETE_TRIP = 3;
    public int retryCount = 1;
    LatLngInterpolator _latLngInterpolator = new LatLngInterpolator.Spherical();
    ObjectAnimator animator = null;
    long timeclear = 0L;
    float zoom = 17f, bearing, bearings;
    float animteBearing;
    int layoutheight;
    //array list declarations
    ArrayList<LatLng> listPoint = new ArrayList<LatLng>();
    ArrayList<LatLng> savedpoint = new ArrayList<LatLng>();
    ArrayList<LatLng> _trips = new ArrayList<LatLng>();
    //Marker declarations
    Marker _marker;
    LocalBroadcastManager localBroadcastManager;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest mLocationRequest;

    private GoogleMap map;
    private Route route = null;
    private final NonActivity nonactiityobj = new NonActivity();
    private Button endtrip;
    private TextView butt_onboard;
    private RoundedImageView proimg;
    private MapWrapperLayout mapWrapperLayout;
    private Location mLastLocation;
    private Float waitingHr;
    private String p_travelstatus = "", alert_msg = "", status, Address = "";
    private String mroute;
    private String metricss = "";
    private final String model_name = "";
    private LatLng savedLatLng = null;
    private LatLng viaLatlng;
    private LatLng pickupLatLng, dropLatLng, currentLatLng;
    private double latitude1 = 0.0;
    private double longitude1 = 0.0;
    private double speed = 0.0;
    private Double p_latitude, p_longtitude;
    private Double d_latitude, d_longtitude;
    private Double driver_latitude, driver_longtitude;
    private String waitingTime = "";
    private boolean animStarted = false;
    private boolean animLocation = false;
    private Bundle alert_bundle = new Bundle();
    //layout declarations
    private LinearLayout speed_lay, km_lay, start_trip;
    private LinearLayout pickup_drop_lay, tripInfo, dropppp;
    private LinearLayout tripinprogress_lay, tripDetails_lay, trip_lay;
    private RelativeLayout navigator_layout, slide_lay;
    private RelativeLayout dropLay, mapsupport_lay;
    private LinearLayout infoLayout;
    private FrameLayout pickup_pinlay, contact_lay;
    private View view_line_trip, pickup_drop_Sep;
    //View Declarations
    private ImageView pickup_pin;
    private CardView card_view_pickup, drop_lay_card;

    private LinearLayout drop_lay, phonelay, cancellay;
    private TextView contact_txt, backup, mapInfoTxt, trip_types, taxitypeTxt;
    private TextView CurrentlocationTxt, pickup_location_txt, txt_pickup, txt_drop;
    private TextView droplocationTxt, tv_notes, pickup_notesTxt, drop_notesTxt;
    private TextView nodataTxt, passnameTxt, speedTxt,mobile_txt;
    ImageView passengerphoneTxt, TripcancelTxt;
    private TextView HeadTitle, CancelTxt;
    private TextView waitingTimeTxt, total_km;
    BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LocationUpdate.sTimer = intent.getStringExtra(CommonData.FINAL_WAITING_TIME);
            waitingTimeTxt.setText(CommonData.getDateForWaitingTime(SessionSave.getWaitingTime(OngoingAct.this)));
        }
    };
    private AppCompatImageView ssWaitingTime_img;
    private ImageView btn_emergency_contact;
    private FloatingActionButton mov_cur_loc;
    private RecyclerView stop_recyclerView;
    private LinearLayoutManager mLayoutManager;
    private ImageView pick_fav;
    private View trip_view;
    private ArrayList<LatLng> stopListData = new ArrayList<>();
    private ArrayList<StopData> stopLists = new ArrayList<>();
    private Marker c_marker, p_marker, d_marker;
    private Marker a_marker;
    //Dialog declarations
    private Dialog mProgressdialog;
    private JSONArray servicesJsonArray;


    double os_distance, os_duration, os_fare, promo_percentage = 0.0;
    private String trip_type = "1";
    private String booking_Type = "1";
    private String model_id = "";
    private String promo_type;
    private String existing_wallet_amount = "";
    private String distanceFare = "";
    private double os_tax;
    private double os_plan_fare, os_plan_distance;
    private double os_plan_duration;
    private double os_additional_fare_per_distance, os_additional_fare_per_hour;
    private double os_minute_fare;
    private String f_farediscount = "";
    double tax = 0.0;
    private String[] os_hr_min = new String[2];
    private String f_metric;
    private String f_tripid;
    private String f_totalfare;
    private double m_totalfare;
    private String f_distance;
    private double m_distance;
    private double m_waitingcost;
    private String f_waitingcost;
    private String f_payamt = "";
    private double m_payamt;
    private String f_walletamt = "";
    private double m_walletamt;
    private String f_nightfareapplicable;
    private String f_nightfare;
    private String f_eveningfare_applicable = "0";
    private String f_eveningfare;
    private String f_pickup = "", drop_location = "";
    private String f_waitingtime;
    private String f_taxamount;
    private String f_tripfare;
    private double m_taxamount;
    private double m_tripfare;
    private String p_dis = "";
    private double f_fare;
    private double f_total;
    Double amount_tobe_paid = 0.0;
    Double amount_used_from_wallet = 0.0;
    private String base_fare = "";
    private final String promotax = "";
    private String Cvv;
    private String cmpTax = "";
    private String f_minutes_traveled;
    private String f_minutes_fare;
    private String fare_calculation_type = "3";
    private String pending_cancel_amount = "";
    CountDownTimer countDownTimer;
    private TextView pickup_timerTxt;
    private LinearLayout timer_lay;
    private LinearLayout toll_lay;
    private CardView estimatelay;
    private TextView estimateTxt;
    private String call_masking_ph_no = "";
Boolean enable_os_waiting_fare = false;
    private Dialog myDialog;
    private Dialog myOTPDialog;

     double manual_outstation_hours;
    private AlertDialog altdialog; // Your dialog reference
    private List<ServiceItem> allServices;
    private List<ServiceItem> selectedServices;
   private SelectedServiceAdapter selectedServiceAdapter;
    private TextView tvTotalAmount;
    /**
     * This handler helps to draw the route between driver place to pickup place and pickup place to drop place.
     */
    Handler mHandler = new Handler() {
        private CountDownTimer countDownTimer;

        @Override
        public void handleMessage(final android.os.Message msg) {

            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    try {
                        if (map != null && (!LOCATION_UPDATE_STOPPED || ROUTE_DRAW_ON_START)) {

                            LOCATION_UPDATE_STOPPED = true;
                            ROUTE_DRAW_ON_START = false;
                            if (MainActivity.mMyStatus.getOnstatus().equalsIgnoreCase("Complete") || MainActivity.mMyStatus.getOnstatus().equalsIgnoreCase("Arrivd")) {
                                if (route != null)
                                    route.removePolyLines();
                                pickUpDropMarker();
                                ArrayList<LatLng> pp = new ArrayList<>();
                                pp.add(pickupLatLng);
                                pp.add(dropLatLng);
                                if (viaLatlng != null)
                                    pp.add(viaLatlng);

                                if (pickupLatLng != null && pickupLatLng.latitude != 0.0 && pickupLatLng.longitude != 0.0) {
                                    p_marker = map.addMarker(new MarkerOptions().position(new LatLng(pickupLatLng.latitude, pickupLatLng.longitude)).title(NC.getResources().getString(R.string.pickuploc)).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_green)).draggable(true));
                                }
                                if (dropLatLng != null && dropLatLng.latitude != 0.0 && dropLatLng.longitude != 0.0) {
                                    d_marker = map.addMarker(new MarkerOptions().position(new LatLng(dropLatLng.latitude, dropLatLng.longitude)).title(NC.getResources().getString(R.string.droploc)).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_red)).draggable(true));
//                                    route.setUpPolyLine(map, OngoingAct.this, pp.get(0), pp.get(1));

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mroute != null && !mroute.isEmpty() && !mroute.equalsIgnoreCase("0"))
                                                route.drawRouteFromPolyline(map, mroute, stopListData);
                                            else
                                                route.setUpPolyLine(map, OngoingAct.this, pp.get(0), pp.get(1), stopListData);
                                        }
                                    }, 500);
                                }
                            } else if (MainActivity.mMyStatus.getOnstatus().equalsIgnoreCase("On")) {


                                ArrayList<LatLng> pp = new ArrayList<>();
                                pp.add(currentLatLng);
                                pp.add(pickupLatLng);
//                                if (viaLatlng != null)
//                                    pp.add(viaLatlng);
                                if (pp != null) {
                                    route.setUpPolyLine(map, OngoingAct.this, pp.get(0), pp.get(1), pp);
                                }
                            } else {
                                ArrayList<LatLng> pp = new ArrayList<>();
                                pp.add(pickupLatLng);
                                pp.add(dropLatLng);
                                if (viaLatlng != null)
                                    pp.add(viaLatlng);
                                try {
                                    if (pp != null && map != null) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (mroute != null && !mroute.isEmpty() && !mroute.equalsIgnoreCase("0"))
                                                    route.drawRouteFromPolyline(map, mroute, stopListData);
                                                else
                                                    route.setUpPolyLine(map, OngoingAct.this, pp.get(0), pp.get(1), stopListData);
                                            }
                                        }, 500);

//                                        route.setUpPolyLine(map, OngoingAct.this, pp.get(0), pp.get(1));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    LOCATION_UPDATE_STOPPED = false;
                                }
                            }, 50000);
                        }

                    } catch (final Exception e) {
                        mHandler.sendEmptyMessage(5);
                        e.printStackTrace();
                    }
                    break;
                case 2:
//                    final View view = View.inflate(OngoingAct.this, R.layout.progress_bar, null);
//                    mProgressdialog = new Dialog(OngoingAct.this, R.style.NewDialog);
//                    mProgressdialog.setContentView(view);
//                    mProgressdialog.setCancelable(false);
//                    mProgressdialog.show();

                    ImageView iv = mProgressdialog.findViewById(R.id.giff);
                    DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(iv);
//                    Glide.with(OngoingAct.this)
//                            .load(R.raw.loading_anim)
//                            .into(imageViewTarget);

                    mHandler.sendEmptyMessage(1);
                    break;
                case 3:
                    showLog("dismiss handler");
                    mProgressdialog.dismiss();
                    break;
                case 4:
                    countDownTimer.cancel();
                    break;
                case 5:
                    try {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mroute != null && !mroute.isEmpty() && !mroute.equalsIgnoreCase("0"))
                                    route.drawRouteFromPolyline(map, mroute, stopListData);
                            }
                        }, 500);
//                        route.setUpPolyLine(map, OngoingAct.this, pickupLatLng, dropLatLng);
//                        route.drawRoute(map, OngoingAct.this, pickupLatLng, dropLatLng, "en", Color.parseColor("#00BFFF"));
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

    };

    private JSONArray stops;
    private Dialog dialog1;
    private Dialog dialogotp;
   // private PickupDropView pickUpDropView;

    public static void registerDistanceInterface(Pickupupdate distanceInterface) {
        sendPickupPoints = distanceInterface;
    }

    /**
     * Get the google map pixels from xml density independent pixel.
     */
    public static int getPixelsFromDp(final Context context, final float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    // Set the layout to activity.
    @Override
    public int setLayout() {
        setLocale();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return R.layout.accept_lay;

    }

    @Override
    protected void onStop() {
        Utils.closeDialog(mProgressdialog);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalDistanceCalculation.registerDistanceInterface(OngoingAct.this);

    }

    /**
     * Handling functionality after permission granted
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ensureCall();
                        }
                    });

                }
                break;
            case MY_PERMISSIONS_REQUEST_GPS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
                }
                break;

        }
    }

    /**
     * Call passenger
     */
    private void ensureCall() {
        dialog1 = Utils.alert_view(this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.confirm_call), NC.getResources().getString(R.string.call), NC.getResources().getString(R.string.cancel), false, OngoingAct.this, "1");
    }

    /**
     * Initialize the views on layout
     */
    @SuppressLint("DefaultLocale")
    @Override
    public void Initialize() {
        CommonData.mActivitylist.add(this);
        CommonData.current_act = "OngoingAct";
        CommonData.sContext = this;
        CommonData.current_trip_accept = 1;
        // FontHelper.applyFont(this, findViewById(R.id.ongoing_lay));

        route = new Route();
        createLocationRequest();

        //Map initialization
        final SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        //local broadcast manager initialization
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

       // pickUpDropView = findViewById(R.id.pdview);
        dropppp = findViewById(R.id.dropppp);
        pickup_pinlay = findViewById(R.id.pickup_pinlay);
        pick_fav = findViewById(R.id.pick_fav);
        pick_fav.setVisibility(View.GONE);
        pickup_pin = findViewById(R.id.pickup_pin);
        HeadTitle = findViewById(R.id.headerTxt);
        CancelTxt = findViewById(R.id.waittime_txt);
        TripcancelTxt = findViewById(R.id.TripcancelTxt);
        nodataTxt = findViewById(R.id.nodataTxt);
        butt_onboard = findViewById(R.id.butt_onboard);
        endtrip = findViewById(R.id.endtrip);
        HeadTitle.setText(" " + NC.getResources().getString(R.string.app_name));
        speed_lay = findViewById(R.id.timerlayout);
        start_trip = findViewById(R.id.start_trip);
        waitingTimeTxt = findViewById(R.id.waittime_txt);
        km_lay = findViewById(R.id.km_lay);
        total_km = findViewById(R.id.total_km);
        backup = findViewById(R.id.back_txt);
        CurrentlocationTxt = findViewById(R.id.currentlocTxt);
        droplocationTxt = findViewById(R.id.droplocTxt);
        CurrentlocationTxt.setSelected(true);
        droplocationTxt.setSelected(true);
        passnameTxt = findViewById(R.id.passnameTxt);
        mobile_txt = findViewById(R.id.mobile_txt);
        proimg = findViewById(R.id.proimg);
        passengerphoneTxt = findViewById(R.id.phoneTxt);
        speedTxt = findViewById(R.id.speedTxt);
        tripinprogress_lay = findViewById(R.id.tripinprogress_lay);
        tripDetails_lay = findViewById(R.id.tripDetails_lay);
        trip_lay = findViewById(R.id.trip_lay);
        contact_txt = findViewById(R.id.contact_txt);
        taxitypeTxt = findViewById(R.id.taxitypeTxt);
        stop_recyclerView = findViewById(R.id.stop_listview);
        mLayoutManager = new LinearLayoutManager(OngoingAct.this);
        stop_recyclerView.setLayoutManager(mLayoutManager);
        contact_lay = findViewById(R.id.contact_lay);
        phonelay = findViewById(R.id.phonelay);
        cancellay = findViewById(R.id.cancellay);

        drop_lay_card = findViewById(R.id.drop_lay_card);
        drop_lay_card.setVisibility(View.GONE);
        pickup_drop_lay = findViewById(R.id.pickup_drop_lay);
        drop_lay = findViewById(R.id.drop_lay);
        trip_view = findViewById(R.id.trip_view);
        mov_cur_loc = findViewById(R.id.mov_cur_loc);
        card_view_pickup = findViewById(R.id.card_view_pickup);
        view_line_trip = findViewById(R.id.view_line_trip);
        pickup_location_txt = findViewById(R.id.pickup_location_txt);
        txt_pickup = findViewById(R.id.txt_pickup);

        txt_drop = findViewById(R.id.txt_drop);
        pickup_drop_Sep = findViewById(R.id.pickup_drop_Sep);
        slide_lay = findViewById(R.id.slide_lay);
        pickup_location_txt.setVisibility(View.VISIBLE);
        ssWaitingTime_img = findViewById(R.id.img_start);
        tripInfo = findViewById(R.id.tripdetail_layout);
        tv_notes = findViewById(R.id.notes);
        mapsupport_lay = findViewById(R.id.mapsupport_lay);
        navigator_layout = findViewById(R.id.botton_layout);
        infoLayout = findViewById(R.id.info_layout);
        mapInfoTxt = findViewById(R.id.mapinfo_txt);
        pickup_timerTxt = findViewById(R.id.pickup_timerTxt);
        timer_lay = findViewById(R.id.timer_lay);
        toll_lay = findViewById(R.id.toll_lay);
        estimatelay = findViewById(R.id.estimatelay);
        estimateTxt = findViewById(R.id.estimateTxt);
        trip_types = findViewById(R.id.trip_types);

        pickup_notesTxt = findViewById(R.id.pickup_notesTxt);
        drop_notesTxt = findViewById(R.id.drop_notesTxt);
        dropppp.setVisibility(View.GONE);

        allServices = new ArrayList<>();
        selectedServices = new ArrayList<>();
        //   dropLay.setVisibility(View.GONE);
//        if (dropppp.getVisibility() == View.GONE) {
//
//            final float scale = this.getResources().getDisplayMetrics().density;
//            int pixels = (int) (60 * scale + 0.5f);
//            dropLay.getLayoutParams().height = pixels;
//            dropLay.invalidate();
//
//        }

        try {
            alert_bundle = getIntent().getExtras();
            if (alert_bundle != null) {
                alert_msg = alert_bundle.getString("alert_message");
                try {
                    status = alert_bundle.getString("status");
                    getIntent().replaceExtras(new Bundle());
                    getIntent().setAction("");
                    getIntent().setData(null);
                    getIntent().setFlags(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (status != null)
                if (status.equals("11")) {
                    startActivity(new Intent(OngoingAct.this, OngoingAct.class));
                }
            if (alert_msg != null && alert_msg.length() != 0)
                dialog1 = Utils.alert_view(OngoingAct.this, NC.getResources().getString(R.string.message), alert_msg, NC.getResources().getString(R.string.ok), "", true, OngoingAct.this, "4");
            if (!SessionSave.getSession("trip_id", OngoingAct.this).equals("")) {
                JSONObject j = new JSONObject();
                j.put("trip_id", SessionSave.getSession("trip_id", OngoingAct.this));
                final String Url = "type=get_trip_detail";
                new Tripdetails(Url, j);
                nonactiityobj.startServicefromNonActivity(OngoingAct.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //SOS Handling in Trip
        btn_emergency_contact = findViewById(R.id.btn_emergency_contact);
      /*  if (SessionSave.getSession(CommonData.SOS_ENABLED, this, false)) {
            btn_emergency_contact.setVisibility(View.VISIBLE);
        }*/
        btn_emergency_contact.setVisibility(View.VISIBLE);

        taxitypeTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                TollInformation bottomSheet = new TollInformation();
//                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
                showodometerEdit();
            }
        });
        btn_emergency_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View view1 = View.inflate(OngoingAct.this, R.layout.emergency_alert, null);
                Dialog emergency_dialog = new Dialog(OngoingAct.this, R.style.dialogwinddow);
                emergency_dialog.setContentView(view1);
                emergency_dialog.setCancelable(true);
                emergency_dialog.show();
                final Button button_success = emergency_dialog.findViewById(R.id.button_success);
                final Button button_failure = emergency_dialog.findViewById(R.id.button_failure);
                button_success.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        emergency_dialog.dismiss();
                        startSOSService();
                    }
                });
                button_failure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        emergency_dialog.dismiss();
                    }
                });
            }
        });


        contact_txt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contact_lay.isShown()) {
                    stop_recyclerView.setVisibility(View.GONE);
                 ///   pickUpDropView.setVisibility(View.GONE);
                    contact_lay.setVisibility(View.GONE);
                    cancellay.setVisibility(View.VISIBLE);
                    phonelay.setVisibility(View.GONE);
                    pickup_drop_lay.setVisibility(View.GONE);
                    // contact_txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_trip_inprogress_profile, 0);
                    setCurrentLocationPosition(0, 5, 10, 150);
                } else {
                    stop_recyclerView.setVisibility(View.GONE);
                  //  pickUpDropView.setVisibility(View.GONE);
                    contact_lay.setVisibility(View.VISIBLE);
                    cancellay.setVisibility(View.VISIBLE);
                    phonelay.setVisibility(View.VISIBLE);

                    drop_lay.setVisibility(View.GONE);
                    pickup_location_txt.setVisibility(View.INVISIBLE);
                    pickup_drop_lay.setVisibility(View.GONE);
                    //contact_txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.user_focus, 0);
                    MapWrapperLayout.setmMapIsTouched(true);
                    setCurrentLocationPosition(0, 50, 10, 100);
                }
            }
        });

        mov_cur_loc.setOnClickListener(v -> {
            if (map != null && mLastLocation != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), zoom));
                navigator_layout.setVisibility(View.VISIBLE);
                mov_cur_loc.setVisibility(View.GONE);
            }
        });

        //To update the metric in speed
        if (SessionSave.getSession("Metric", OngoingAct.this).equalsIgnoreCase("KM")) {
            metricss = " km/hr";
        } else {
            metricss = " miles/hr";
        }
        LocalBroadcastManager.getInstance(OngoingAct.this).registerReceiver(listener,
                new IntentFilter(LocationUpdate.WAITING_TIME));
        // to handle the whether the waiting time is auto or manual

        //set waiting time image if waiting time is manual
        if (!SessionSave.getSession(CommonData.WAITING_TIME, OngoingAct.this, false)) {
            ssWaitingTime_img.setImageResource(R.drawable.ic_play_circle);
            if (!SessionSave.getSession("trip_id", OngoingAct.this).equals("")) {
                CommonData.km_calc = 1;
                if (localBroadcastManager != null) {
//                 //   Intent localIntent = new Intent(WAITING_TIME_RUN);
//                    localIntent.putExtra(CommonData.WAITING_TIME_START_STOP, CommonData.WAITING_TIME_STOP);
//                    localBroadcastManager.sendBroadcast(localIntent);
                }
            }
        } else {
            ssWaitingTime_img.setImageResource(R.drawable.map_icon_red);
            if (!SessionSave.getSession("trip_id", OngoingAct.this).equals("")) {
                CommonData.km_calc = 0;
//                if (localBroadcastManager != null) {
//                    Intent localIntent = new Intent(WAITING_TIME_RUN);
//                    localIntent.putExtra(CommonData.WAITING_TIME_START_STOP, CommonData.WAITING_TIME_START);
//                    localBroadcastManager.sendBroadcast(localIntent);
//                }
            }
        }

        ssWaitingTime_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SessionSave.getSession(CommonData.WAITING_TIME, OngoingAct.this, false)) {
                    CommonData.km_calc = 0;
                    if (!SessionSave.getSession("trip_id", OngoingAct.this).equals("")) {
//                        WaitingTimerRun.startTimerService(OngoingAct.this);
//                        myHandler.postDelayed(r, 0);

                        if (localBroadcastManager != null) {
//                            Intent localIntent = new Intent(WAITING_TIME_RUN);
//                            localIntent.putExtra(CommonData.WAITING_TIME_START_STOP, CommonData.WAITING_TIME_START);
//                            localBroadcastManager.sendBroadcast(localIntent);
                        }
                        ssWaitingTime_img.setImageResource(R.drawable.map_icon_red);
                        SessionSave.saveSession(CommonData.WAITING_TIME, true, OngoingAct.this);

                        waitingTimeTxt.setText(String.format(Locale.UK, CommonData.getDateForWaitingTime(SessionSave.getWaitingTime(OngoingAct.this))));
                    }
                } else {
                    Systems.out.println("timer started ongoing" + SessionSave.getWaitingTime(OngoingAct.this));

//                    stopService(new Intent(OngoingAct.this, WaitingTimerRun.class));
                    SessionSave.saveSession(CommonData.WAITING_TIME, false, OngoingAct.this);
                    if (localBroadcastManager != null) {
//                        Intent localIntent = new Intent(WAITING_TIME_RUN);
//                        localIntent.putExtra(CommonData.WAITING_TIME_START_STOP, CommonData.WAITING_TIME_STOP);
//                        localBroadcastManager.sendBroadcast(localIntent);
                    }
                    ssWaitingTime_img.setImageResource(R.drawable.ic_play_circle);
                    CommonData.km_calc = 1;

                    waitingTimeTxt.setText(String.format(Locale.UK, CommonData.getDateForWaitingTime(SessionSave.getWaitingTime(OngoingAct.this))));
                }
            }
        });
        // ViewEnabledWithDelay(3000, butt_onboard);

//        Glide.with(OngoingAct.this)
//                .load(SessionSave.getSession("image_path", OngoingAct.this) + "callDriver.png")
//                .apply(RequestOptions.placeholderOf(R.drawable.cancel).override((int) pxtoDp(100), (int) pxtoDp(100))).into(passengerphoneTxt);
//                .into(new SimpleTarget<Drawable>() {
//                    @Override
//                    public void onResourceReady(@NonNull Drawable resource,
//                                                @Nullable Transition<? super Drawable> transition) {
//                        /* Set a drawable to the left of textView */
//                        passengerphoneTxt.setCompoundDrawablesWithIntrinsicBounds(resource, null, null, null);
//
////                    }
////                });
//
//        Glide.with(OngoingAct.this)
//                .load(SessionSave.getSession("image_path", OngoingAct.this) + "tripCancel.png").error(R.drawable.ic_fleetera_cancel)
//                .apply(RequestOptions.placeholderOf(R.drawable.ic_fleetera_cancel).override((int) pxtoDp(100), (int) pxtoDp(100))).into(TripcancelTxt);
//                .into(new SimpleTarget<Drawable>() {
//                    @Override
//                    public void onResourceReady(@NonNull Drawable resource,
//                                                @Nullable Transition<? super Drawable> transition) {
//                        /* Set a drawable to the left of textView */
//                        TripcancelTxt.setCompoundDrawablesWithIntrinsicBounds(resource, null, null, null);
//
//                    }
//                });


        Glide.with(this).load(SessionSave.getSession("image_path", this) + "mapDirection.png").apply(RequestOptions.placeholderOf(R.drawable.gps_navigator).error(R.drawable.gps_navigator)).into((ImageView) findViewById(R.id.butt_navigator));

        butt_onboard.setVisibility(View.VISIBLE);
        // This onclick method used to hide the passenger info view.


        mapInfoTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {

                tripInfo.setVisibility(View.VISIBLE);
                infoLayout.setVisibility(View.GONE);
            }
        });
        // This onclick method used to show the passenger info view.
        // Following set of code to initialize and google map.

        // This onclick method used to make a call to passenger.
        mobile_txt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (SessionSave.getSession(CommonData.CALL_MASKING_ENABLED, OngoingAct.this, false)) {
                    try {

                        JSONObject j = new JSONObject();
                        j.put("trip_id", SessionSave.getSession("trip_id", OngoingAct.this));
                        final String Url = "type=get_twilio_number";
                        new getMaskedPhoneNumber(Url, j);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    dialog1 = Utils.alert_view(OngoingAct.this, "", NC.getResources().getString(R.string.confirm_call), NC.getResources().getString(R.string.call), NC.getResources().getString(R.string.cancel), true, OngoingAct.this, "5");

                    //  ExtensionKt.customDialogyerno(OngoingAct.this, NC.getResources().getString(R.string.message, NC.getResources().getString(R.string.call),NC.getResources().getString(R.string.cancel),OngoingAct.this);


//                    try {
//
//
//                        dialog1 = Utils.alert_view_dialog(OngoingAct.this, NC.getResources().getString(R.string.message),
//                                NC.getResources().getString(R.string.confirm_call),
//                                NC.getResources().getString(R.string.call),
//                                NC.getResources().getString(R.string.cancel), true, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        try {
//                                            dialog.dismiss();
//                                            // TODO Auto-generated method stub
//                                            if (MainActivity.mMyStatus.getpassengerphone().length() == 0)
//                                                CToast.ShowToast(OngoingAct.this, NC.getResources().getString(R.string.invalid_mobile_number));
//                                            else {
//
//                                   /* if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
//                                            || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//
//                                        dialog1 = Utility.alert_view_dialog(getActivity(), "",
//                                                "" + NC.getResources().getString(R.string.str_phone),
//                                                "" + NC.getResources().getString(R.string.yes),
//                                                "" + NC.getResources().getString(R.string.no),
//                                                true, new android.content.DialogInterface.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(android.content.DialogInterface dialog, int which) {
//                                                        ActivityCompat.requestPermissions(getActivity(),
//                                                                new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE},
//                                                                MY_PERMISSIONS_REQUEST_CALL);
//                                                    }
//                                                }, new android.content.DialogInterface.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(android.content.DialogInterface dialog, int which) {
//                                                        dialog.dismiss();
//                                                    }
//                                                }, "");
//                                    } else {*/
//
////                                    }
//                                            }
//                                        } catch (Exception e) {
//                                            // TODO: handle exception
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                }, "");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }


                }


            }
        });
        // This onclick method used to cancel the current ongoing trip.
        TripcancelTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialog1 = Utils.alert_view(OngoingAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.cancel_in_going_trip), NC.getResources().getString(R.string.yes), NC.getResources().getString(R.string.no), true, OngoingAct.this, "3");
            }
        });
        // This onclick method used to move from this activity to home activity.
        backup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {

                //   showLoading(OngoingAct.this);
                try {
                    stopLocationUpdates();
                    map = null;
                    if (c_marker != null && a_marker != null) {
                        c_marker = null;
                        a_marker = null;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                backup.setEnabled(false);
                Intent jobintent = new Intent(OngoingAct.this, HomePageActivity.class);
                startActivity(jobintent);
                finish();
            }
        });
        // This onclick method used to move navigator application with pickup and drop place lat/lng.
        navigator_layout.setOnClickListener(v -> {
            try {
                Log.e("URL_Test" + mMyStatus.getOnstatus(), "hai");
                if (mMyStatus.getOnstatus().equalsIgnoreCase("Complete")) {
                    if (pickupLatLng.latitude != 0.0 && pickupLatLng.longitude != 0.0) {

                        String locationurl = "";
                        if (stopListData != null && stopListData.size() > 0) {
                            if (stopListData.size() == 1) {
                                //http://maps.google.com/maps?saddr=
                                locationurl = "https://www.google.com/maps/dir/?api=1&origin=" + stopListData.get(0).latitude + "," + stopListData.get(0).longitude;
                            } else /*if (mLastLocation.getLatitude() != 0.0 && mLastLocation.getLongitude() != 0.0 && pickupLatLng.latitude != 0.0 && pickupLatLng.longitude != 0.0)*/ {
                                locationurl = "https://www.google.com/maps/dir/?api=1&origin=" + stopListData.get(0).latitude + "," + stopListData.get(0).longitude + "&destination=" + stopListData.get(stopListData.size() - 1).latitude + "," + stopListData.get(stopListData.size() - 1).longitude + "&travelmode=driving&waypoints=" + route.makeDirectionUrl(stopListData);
                            }
                            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationurl));
                            startActivity(intent);
                        } else {

                            if (dropLatLng != null && dropLatLng.latitude != 0.0) {
                                locationurl = "https://www.google.com/maps/dir/?api=1&origin=" + pickupLatLng.latitude + "," + pickupLatLng.longitude + "&destination=" + dropLatLng.latitude + "," + dropLatLng.longitude + "&travelmode=driving";
                                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationurl));
                                startActivity(intent);
                            } else {
                                locationurl = "https://www.google.com/maps/dir/?api=1&origin=" + pickupLatLng.latitude + "," + pickupLatLng.longitude;
                                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationurl));
                                startActivity(intent);
                            }
                        }
                    }
                } else {
                    String locationurl = "";
                    if (MainActivity.mMyStatus.getOnstatus().equalsIgnoreCase("On")) {
                        if (mLastLocation.getLatitude() != pickupLatLng.latitude) {
                            locationurl = "https://www.google.com/maps/dir/?api=1&origin=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&destination=" + pickupLatLng.latitude + "," + pickupLatLng.longitude + "&travelmode=driving";
                            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationurl));
                            startActivity(intent);
                        } else {
                            locationurl = "https://www.google.com/maps/dir/?api=1&origin=" + pickupLatLng.latitude + "," + pickupLatLng.longitude;
                            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationurl));
                            startActivity(intent);
                        }
                    } else {
                        if (stopListData != null && stopListData.size() > 0) {
                            if (stopListData.size() == 1) {
                                //http://maps.google.com/maps?saddr=
                                locationurl = "https://www.google.com/maps/dir/?api=1&origin=" + stopListData.get(0).latitude + "," + stopListData.get(0).longitude;
                            } else /*if (mLastLocation.getLatitude() != 0.0 && mLastLocation.getLongitude() != 0.0 && pickupLatLng.latitude != 0.0 && pickupLatLng.longitude != 0.0)*/ {
                                locationurl = "https://www.google.com/maps/dir/?api=1&origin=" + stopListData.get(0).latitude + "," + stopListData.get(0).longitude + "&destination=" + stopListData.get(stopListData.size() - 1).latitude + "," + stopListData.get(stopListData.size() - 1).longitude + "&travelmode=driving&waypoints=" + route.makeDirectionUrl(stopListData);
                            }
                            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationurl));
                            startActivity(intent);
                        } else {
                            if (dropLatLng != null && dropLatLng.latitude != 0.0) {
                                locationurl = "https://www.google.com/maps/dir/?api=1&origin=" + pickupLatLng.latitude + "," + pickupLatLng.longitude + "&destination=" + dropLatLng.latitude + "," + dropLatLng.longitude + "&travelmode=driving";
                                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationurl));
                                startActivity(intent);
                            } else {
                                locationurl = "https://www.google.com/maps/dir/?api=1&origin=" + pickupLatLng.latitude + "," + pickupLatLng.longitude;
                                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationurl));
                                startActivity(intent);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        });

        // showodometer();
        // This onclick method used to handle the three state in ongoing trip page(Arrived,Start and End).In each phase will use different API.
        butt_onboard.setOnClickListener(v -> {
            try {

                if (SessionSave.getSession("otp_enter", OngoingAct.this).equals("yes")) {

                    showOtp();
                    // showodometer();

                } else {
                    // If the trip in accepted not get arrived
                    if (MainActivity.mMyStatus.getOnstatus().equalsIgnoreCase("On")) {
                        Systems.out.println("distanceeeeee " + SessionSave.getDistance(OngoingAct.this) + "____" + SessionSave.getArriveDistance(OngoingAct.this));


                        JSONObject jDriverArrived = new JSONObject();
                        jDriverArrived.put("trip_id", SessionSave.getSession("trip_id", OngoingAct.this));
                        jDriverArrived.put("driver_id", SessionSave.getSession("Id", OngoingAct.this));
                        jDriverArrived.put("taxi_id", SessionSave.getSession("taxi_id", OngoingAct.this));
                        jDriverArrived.put("distance", localDistance);

                        final String arrived_url = "type=driver_arrived";
                        new DriverArrived(arrived_url, jDriverArrived);
                    }
                    // If trip in arrived state and going to start the trip
                    else if (MainActivity.mMyStatus.getOnstatus().equalsIgnoreCase("Arrivd")) {

                        //


                        try {
                            TripcancelTxt.setVisibility(View.VISIBLE);
                            timer_lay.setVisibility(View.GONE);

                            countDownTimer.cancel();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        retryCount = 1;
                        //                        nonactiityobj.stopServicefromNonActivity(OngoingAct.this);
                        if (latitude1 != 0.0 && longitude1 != 0.0) {
                            JSONObject jstart = new JSONObject();
                            jstart.put("driver_id", SessionSave.getSession("Id", OngoingAct.this));
                            jstart.put("latitude", latitude1);
                            jstart.put("longitude", longitude1);
                            jstart.put("status", "A");
                            stopLists.get(0).setLat(latitude1);
                            stopLists.get(0).setLng(longitude1);
                            jstart.put("stops", new JSONArray(new Gson().toJson(stopLists)));
                            jstart.put("trip_id", SessionSave.getSession("trip_id", OngoingAct.this));
                            jstart.put("driver_id", SessionSave.getSession("Id", OngoingAct.this));
                            jstart.put("taxi_id", SessionSave.getSession("taxi_id", OngoingAct.this));
                            final String driver_status_update = "type=driver_status_update";
                            SessionSave.saveSession("slat", String.valueOf(latitude1), OngoingAct.this);
                            SessionSave.saveSession("slng", String.valueOf(longitude1), OngoingAct.this);
                            CommonData.last_getlatitude = latitude1;
                            CommonData.last_getlongitude = longitude1;
                            if (currentAccuracy <= slabAccuracy) {
                                new Onboard(driver_status_update, jstart);
                            } else {
                                showLowAccuracyAlert();
                            }
                        } else {
                            JSONObject jstart = new JSONObject();
                            jstart.put("driver_id", SessionSave.getSession("Id", OngoingAct.this));
                            jstart.put("latitude", "");
                            jstart.put("longitude", "");
                            jstart.put("status", "A");
                            stopLists.get(0).setLat(0.0);
                            stopLists.get(0).setLng(0.0);
                            jstart.put("stops", new JSONArray(new Gson().toJson(stopLists)));
                            jstart.put("trip_id", SessionSave.getSession("trip_id", OngoingAct.this));
                            jstart.put("driver_id", SessionSave.getSession("Id", OngoingAct.this));
                            jstart.put("taxi_id", SessionSave.getSession("taxi_id", OngoingAct.this));
                            final String driver_status_update = "type=driver_status_update";
                            if (currentAccuracy <= slabAccuracy) {
                                new Onboard(driver_status_update, jstart);
                            } else {
                                showLowAccuracyAlert();
                            }
                        }


                    }
                    // If trip in progress and going to end the trip
                    else if (MainActivity.mMyStatus.getOnstatus().equalsIgnoreCase("Complete")) {
                        System.out.println("enable_os_waiting_fare"+enable_os_waiting_fare);
                        if (enable_os_waiting_fare)
                        {
                            showDialogWithDropdown();


                        }
                        else {
                            CompleteTrip(OngoingAct.this);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        endtrip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("enable_os_waiting_fare"+enable_os_waiting_fare);
                if (enable_os_waiting_fare)
                {
                    showDialogWithDropdown();


                }
                else {
                    CompleteTrip(OngoingAct.this);
                }


            }
        });

        toll_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TollInformation bottomSheet = new TollInformation();
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());

            }
        });
    }

    public void showodometerEdit() {
        final View view1 = View.inflate(OngoingAct.this, R.layout.odometer_input, null);
        if (myDialog != null && myDialog.isShowing())
            myDialog.cancel();
        myDialog = new Dialog(OngoingAct.this, R.style.NewDialog);
        myDialog.setContentView(view1);
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setCancelable(true);
        myDialog.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(myDialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        myDialog.getWindow().setAttributes(layoutParams);


//
        LinearLayout btn_confirm = myDialog.findViewById(R.id.btn_confirm);

        TextView odameter_heading = myDialog.findViewById(R.id.odameter_heading);
        EditText verifyno1Txt = myDialog.findViewById(R.id.verifyno1Txt);



        btn_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyno1Txt.getText().toString().equals("")) {
                    Toast.makeText(OngoingAct.this, "Enter first number", Toast.LENGTH_LONG).show();
                } else {
                    myDialog.dismiss();
                    String otpnumber = verifyno1Txt.getText().toString();

                    ;
                    final String url = "type=new_update_odometer";

                    new updateOdaMeterUpdate(url, "2", otpnumber);


                }


            }
        });

    }
    private void showDialogWithDropdown() {
        // Create a list of numbers from 1 to 24
        List<String> numbers = new ArrayList<>();
        for (int i = 0; i <= 24; i++) {
            numbers.add(String.valueOf(i));
        }

        // Inflate the custom layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_with_dropdown, null);

        // Find the Spinner in the dialog's layout
        Spinner spinner = dialogView.findViewById(R.id.spinner);

        // Set up the Spinner with the numbers
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, numbers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Create and show the dialog
        // Create the dialog
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Select Waiting Hours")
                .setView(dialogView)
                .setPositiveButton("ADD", (dialog, which) -> {
                    // Get the selected number
                    String selectedNumber = spinner.getSelectedItem().toString();
                    // Perform an action with the selected number (example: log it)

                    manual_outstation_hours = Double.parseDouble(spinner.getSelectedItem().toString());
                    System.out.println("Selected Number: " + selectedNumber);
                    CompleteTrip(OngoingAct.this);

                    SessionSave.saveSession("manual_outstation_hours",selectedNumber,OngoingAct.this);
                })
                .setNegativeButton("Cancel", null)
                .create();

        // Set an OnShowListener to modify the button colors after the dialog is shown
        alertDialog.setOnShowListener(dialog -> {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.app_theme_main));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.pure_black));
        });

        // Show the dialog
        alertDialog.show();
    }


    private void initializeLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    mLastLocation = location;
                    if (mapWrapperLayout != null && !mapWrapperLayout.isShown())
                        mapWrapperLayout.setVisibility(View.VISIBLE);
                    latitude1 = location.getLatitude();
                    longitude1 = location.getLongitude();
                    mLastLocation = location;
                    speed = LocationUpdate.speed;
                    speedTxt.setText(String.format(Locale.UK, "%.2f", speed) + metricss.toLowerCase());


                    if (dropLatLng != null && p_travelstatus.trim().equals("2"))
                        if (!checkLocationInRoute()) {
                            viaLatlng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            mHandler.sendEmptyMessage(1);
                        }

                    if (MainActivity.mMyStatus.getOnstatus().equalsIgnoreCase("Complete")) {
                        HeadTitle.setText(" " + NC.getResources().getString(R.string.ongoing_journey));
                    }
                    bearing = location.getBearing();
                    bearings = location.getBearing();
                    if (map != null)
                        zoom = map.getCameraPosition().zoom;

                    if (bearing >= 0)
                        bearing = bearing + 90;
                    else
                        bearing = bearing - 90;

                    try {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        // marker Animation Function
                        if (!animLocation) {
                            listPoint.add(latLng);
                        } else {
                            savedpoint.add(latLng);
                        }
                        if (listPoint.size() > 1) {

                            if (a_marker != null) {
                                a_marker.setVisible(false);
                                a_marker.remove();
                            }

                            if (!animStarted) {
                                if (savedLatLng != null) {
                                    listPoint.set(0, savedLatLng);
                                }

                                    c_marker = map.addMarker(new MarkerOptions().position(listPoint.get(0)).rotation(0).anchor(0.5f, 0.5f).title(Address).icon(BitmapDescriptorFactory.fromResource(R.drawable.top)));



                                c_marker.setVisible(true);
                                if (speed > 20 && map != null) {
                                    animStarted = true;
                                    animLocation = true;

                                    if (map != null) {
                                        CameraPosition camPos = CameraPosition
                                                .builder(
                                                        map.getCameraPosition() // current Camera
                                                )
                                                .bearing(bearings)
                                                .build();

                                        if (MapWrapperLayout.ismMapIsTouched()) {
                                            map.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
                                        }
                                    }
                                    savedLatLng = listPoint.get(listPoint.size() - 1);
                                    animateLine(listPoint, c_marker, bearings);
                                } else {
                                    if (c_marker != null) {
                                        c_marker.setVisible(false);
                                        c_marker.remove();
                                    }
                                    if (GpsStatus.ischecked == 0) {
                                        GpsStatus.ischecked = 1;

                                            a_marker = map.addMarker(new MarkerOptions().position(latLng).rotation(0).anchor(0.5f, 0.5f).title(Address).icon(BitmapDescriptorFactory.fromResource(R.drawable.top)));


                                        a_marker.setVisible(true);

                                        if (speed > 20 && map != null) {

                                            CameraPosition camPos = CameraPosition
                                                    .builder(
                                                            map.getCameraPosition() // current Camera
                                                    )
                                                    .bearing(bearings)
                                                    .build();
                                            map.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
                                        }


                                    } else {

                                            a_marker = map.addMarker(new MarkerOptions().position(latLng).rotation(0).anchor(0.5f, 0.5f).title(Address).icon(BitmapDescriptorFactory.fromResource(R.drawable.top)));


                                        a_marker.setVisible(true);
                                        if (speed > 20 && map != null) {


                                            CameraPosition camPos = CameraPosition
                                                    .builder(
                                                            map.getCameraPosition() // current Camera
                                                    )
                                                    .bearing(bearings)
                                                    .build();
                                            map.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
                                        }


                                    }
                                }
                            }

                        }

                        bearing = 0;
                        bearings = 0;

                    } catch (Exception ex) {
                        ex.printStackTrace();

                    }
                }
            }
        };
    }

    public void setCurrentLocationPosition(int left, int top, int right, int bottom) {
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) mov_cur_loc.getLayoutParams();
        marginParams.setMargins(left, top, right, bottom);
        mov_cur_loc.setLayoutParams(marginParams);

        ViewGroup.MarginLayoutParams navigatorLayoutLayoutParams = (ViewGroup.MarginLayoutParams) navigator_layout.getLayoutParams();
        navigatorLayoutLayoutParams.setMargins(left, top, right, bottom);
        navigator_layout.setLayoutParams(navigatorLayoutLayoutParams);
    }

    /**
     * View enabling in display with delay
     *
     * @param i
     * @param butt_onboard
     */
    public void ViewEnabledWithDelay(int i, Button butt_onboard) {
        butt_onboard.setEnabled(false);
        new Handler().postDelayed(() -> {
            if (butt_onboard != null)
                butt_onboard.setEnabled(true);
        }, i);
    }


    private void RetryLocationPopUp() {
        //   cancelLoading();
        if (retryCount > 2) {
            dialog1 = Utils.alert_view_dialog(OngoingAct.this, NC.getResources().getString(R.string.message), NC.getString(R.string.address_cant_fetch), NC.getString(R.string.retry), NC.getString(R.string.use_map), false, (dialog, which) -> {
                retryCount++;
                // showLoading(OngoingAct.this);
                getCurrentLocation(LOCATION_REQUEST_TYPE_RETRY);
            }, (dialog, which) -> {

            }, "");
        } else {
            dialog1 = Utils.alert_view_dialog(OngoingAct.this, NC.getResources().getString(R.string.message), NC.getString(R.string.address_cant_fetch), NC.getString(R.string.retry), null, false, (dialog, which) -> {
                retryCount++;
                //    showLoading(OngoingAct.this);
                getCurrentLocation(LOCATION_REQUEST_TYPE_RETRY);
            }, null, "");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // showLoading(OngoingAct.this);
        String Address;
        Double obtainedlatitude, obtainedlongitude;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 300) {
            if (data != null) {
                Bundle res = data.getExtras();
                Address = res.getString("param_result");
                obtainedlatitude = res.getDouble("lat");
                obtainedlongitude = res.getDouble("lng");
                latitude1 = obtainedlatitude;
                longitude1 = obtainedlongitude;
                SessionSave.saveSession("drop_location", Address, OngoingAct.this);
                if (!SessionSave.getSession(CommonData.LAST_KNOWN_LAT, OngoingAct.this).equals("")) {
                    Double lastknownlatitude = Double.parseDouble(SessionSave.getSession(CommonData.LAST_KNOWN_LAT, OngoingAct.this));
                    Double lastknowlongitude = Double.parseDouble(SessionSave.getSession(CommonData.LAST_KNOWN_LONG, OngoingAct.this));
                    LocalDistanceCalculation.newInstance(OngoingAct.this).haversine(lastknownlatitude, lastknowlongitude, latitude1, longitude1);
                } else {
                    LocalDistanceCalculation.newInstance(OngoingAct.this).haversine(latitude1, longitude1, latitude1, longitude1);
                }
            }
        }
    }

    public void getCurrentLocation(int locationRequestType) {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        handleLastLocation(location, locationRequestType);
                    }
                });
        startLocationUpdates();
    }

    private void handleLastLocation(Location location, int locationRequestType) {
        switch (locationRequestType) {
            case LOCATION_REQUEST_TYPE_RETRY:
                new Handler().postDelayed(() -> {
                    if (location.getAccuracy() < 500) {
                        if (location.getLatitude() != 0.0) {
                            latitude1 = location.getLatitude();
                            longitude1 = location.getLongitude();
                            new GetAddressFromLatLng(OngoingAct.this, new LatLng(latitude1, longitude1), OngoingAct.this, "").execute();
                        }

                    } else {
                        //    cancelLoading();
                        RetryLocationPopUp();
                    }
                }, 2000);
                break;
            case LOCATION_REQUEST_TYPE_INITIAL:
                mLastLocation = location;
                if (mLastLocation != null) {
                    latitude1 = mLastLocation.getLatitude();
                    longitude1 = mLastLocation.getLongitude();
                    viaLatlng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    currentLatLng = new LatLng(latitude1, longitude1);
                    final LatLng coordinate = new LatLng(latitude1, longitude1);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, zoom));
                    mapWrapperLayout.setVisibility(View.VISIBLE);
                }
                break;
            case LOCATION_REQUEST_TYPE_COMPLETE_TRIP:
                mLastLocation = location;
                if (mLastLocation != null && mLastLocation.getAccuracy() <= slabAccuracy) {

                    latitude1 = mLastLocation.getLatitude();
                    longitude1 = mLastLocation.getLongitude();
                    new GetAddressFromLatLng(OngoingAct.this, new LatLng(latitude1, longitude1), OngoingAct.this, "").execute();
                } else {
                    // cancelLoading();
                    RetryLocationPopUp();
                }
                break;
        }
    }

    public float pxtoDp(int px) {
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        Systems.out.println("pxxxxxx" + dp + "___" + px + "__" + metrics.densityDpi);
        return dp;

    }

    /**
     * This method is called once the map initializtion is ready
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {

            try {
// Customise the styling of the base map using a JSON object defined
// in a raw resource file.
                boolean success = map.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                OngoingAct.this, R.raw.map_style));

                if (!success) {
                    Systems.out.println("Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Systems.out.println("Can't find style. Error: ");
            }
            try {
                final int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(OngoingAct.this);
                if (resultCode == ConnectionResult.SUCCESS) {
                    String imagepath = "";
                    if (!SessionSave.getSession("p_image", OngoingAct.this).equals("")) {
                        imagepath = SessionSave.getSession("p_image", OngoingAct.this);
                        Log.i("Imagepath in session", SessionSave.getSession("p_image", OngoingAct.this));
                    } else
                        imagepath = SessionSave.getSession("noimage_base", OngoingAct.this);
                    Picasso.get().load(imagepath).placeholder(getResources().getDrawable(R.drawable.loadingimage)).error(getResources().getDrawable(R.drawable.map_icon_red)).into(proimg);
                    MapsInitializer.initialize(OngoingAct.this);
                    mapWrapperLayout = findViewById(R.id.map_relative_layout);
                    mapWrapperLayout.init(map, getPixelsFromDp(this, 39 + 20));
                    map.getUiSettings().setZoomControlsEnabled(false);
                    map.setOnCameraMoveStartedListener(this);
                    map.getUiSettings().setCompassEnabled(false);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    map.setMyLocationEnabled(false);
                    map.setPadding(0, 0, 0, 120);
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mapsupport_lay.setVisibility(View.VISIBLE);

                    System.err.println("animate camera:" + latitude1 + "lng" + longitude1);

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LocationUpdate.currentLatitude, LocationUpdate.currentLongtitude), zoom));
                    if (mapWrapperLayout != null && !mapWrapperLayout.isShown())
                        mapWrapperLayout.setVisibility(View.VISIBLE);
                } else {
                    mapsupport_lay.setVisibility(View.GONE);
                    nodataTxt.setVisibility(View.VISIBLE);
                    nodataTxt.setText(NC.getResources().getString(R.string.device_not_support_map));
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void haversineResult(Boolean success) {
        if (success) {
            //do the complete trip process....
            CompleteSuccessClick();
        }
    }


    @Override
    public void onCameraMoveStarted(int i) {
        if (!MapWrapperLayout.ismMapIsTouched()) {
            navigator_layout.setVisibility(View.GONE);
            mov_cur_loc.setVisibility(View.VISIBLE);
        } else {
            navigator_layout.setVisibility(View.VISIBLE);
            mov_cur_loc.setVisibility(View.GONE);
        }

    }

    @Override
    public void positiveButtonClick(DialogInterface dialog, int id, String s) {
        switch (s) {
            case "1":
                try {
                    dialog.dismiss();
                    final Intent callIntent = new Intent(Intent.ACTION_VIEW);
                    callIntent.setData(Uri.parse("tel:" +/* MainActivity.mMyStatus.getpassengerphone()*/call_masking_ph_no));
                    /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }*/
                    startActivity(callIntent);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                break;
            case "2":
                dialog.dismiss();
                if (runningFor() > 10 && !LocationUpdate.DISTANCE_CALCULATION_INPROGRESS) {
                    //s//howLoading(OngoingAct.this);
                    getCurrentLocation(LOCATION_REQUEST_TYPE_COMPLETE_TRIP);
                } else {
                    //  cancelLoading();
                    CToast.ShowToast(OngoingAct.this, NC.getString(R.string.distance_calcuation_inprogress));
                }
                break;
            case "3":
                try {
                    dialog.dismiss();
                    // TODO Auto-generated method stub
                    if (SessionSave.getSession("status", OngoingAct.this).equalsIgnoreCase("A"))
                        dialog1 = Utils.alert_view(OngoingAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.you_are_in_trip), NC.getResources().getString(R.string.ok), "", true, OngoingAct.this, "4");
                    else if (SessionSave.getSession("trip_id", OngoingAct.this).length() == 0)
                        finish();
                    else {
                        nonactiityobj.stopServicefromNonActivity(OngoingAct.this);
                        JSONObject j = new JSONObject();
                        j.put("pass_logid", SessionSave.getSession("trip_id", OngoingAct.this));
                        j.put("driver_id", SessionSave.getSession("Id", OngoingAct.this));
                        j.put("taxi_id", SessionSave.getSession("taxi_id", OngoingAct.this));
                        j.put("company_id", SessionSave.getSession("company_id", OngoingAct.this));
                        j.put("driver_reply", "C");
                        j.put("field", "");
                        j.put("flag", "1");
                        if (MainActivity.mMyStatus.getOnstatus().equalsIgnoreCase("Arrivd"))
                            j.put("driver_arrived", 1);
                        else
                            j.put("driver_arrived", 0);
                        final String canceltrip_url = "type=driver_reply";
                        new CancelTrip(canceltrip_url, j);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                break;
            case "4":
                dialog.dismiss();
                break;
            case "5":
                final Intent callIntent = new Intent(Intent.ACTION_VIEW);
                callIntent.setData(Uri.parse("tel:" + MainActivity.mMyStatus.getpassengerphone()));
                startActivity(callIntent);
                break;
            default:
                break;
        }
    }

    @Override
    public void negativeButtonClick(DialogInterface dialog, int id, String s) {
        switch (s) {
            case "1":
                dialog.dismiss();
                break;
            case "2":
                dialog.dismiss();
                break;
            case "3":
                dialog.dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void setaddress(double latitude, double longitude, String Address, String type) {
        if (Address.length() != 0) {
            //  cancelLoading();
            try {
                Address = Address.replaceAll("null", "").replaceAll(", ,", "").replaceAll(", ,", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            SessionSave.saveSession("drop_location", Address, OngoingAct.this);
            haversineResult(true);
          /*  if (!SessionSave.getSession(CommonData.LAST_KNOWN_LAT, OngoingAct.this).equals("")) {
                Double lastknownlatitude = Double.parseDouble(SessionSave.getSession(CommonData.LAST_KNOWN_LAT, OngoingAct.this));
                Double lastknowlongitude = Double.parseDouble(SessionSave.getSession(CommonData.LAST_KNOWN_LONG, OngoingAct.this));
                LocalDistanceCalculation.newInstance(OngoingAct.this).haversine(lastknownlatitude, lastknowlongitude, latitude1, longitude1);
            } else

                LocalDistanceCalculation.newInstance(OngoingAct.this).haversine(latitude1, longitude1, latitude1, longitude1);
      */
        }
    }

    /**
     * This is method for confirmation for complete the trip
     */
    public void CompleteTrip(final AppCompatActivity context) {


        dialog1 = Utils.alert_view(context, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.confirm_complete), NC.getResources().getString(R.string.yes), NC.getResources().getString(R.string.no), false, OngoingAct.this, "2");
    }

    public void CompleteSuccessClick() {
        SessionSave.saveSession("odameter_status", "3", OngoingAct.this);
        System.out.println("Booking_test"+ " "+"1");


        if (booking_Type.equals("2") || booking_Type.equals("3")) {
            showodometer();
        } else {
            loadServices();
          //  completeTripApi();
        }


    }

    private void loadServices() {
        JSONObject j = new JSONObject();
        try {
            if (booking_Type.equals("2") || booking_Type.equals("3")) {
                j.put("booking_type", booking_Type);
            }
            else
            {
                j.put("booking_type", "1");

            }
            j.put("taxi_model",model_id);

            final String services_url = "type=service_charge_list";
            new showServiceList(services_url, j);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Onstart method by default it called when activity is open.
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(OngoingAct.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(OngoingAct.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            dialog1 = Utils.alert_view_dialog(OngoingAct.this, "", NC.getResources().getString(R.string.str_loc), NC.getResources().getString(R.string.yes), NC.getResources().getString(R.string.no), true, (dialog, i) -> {
                ActivityCompat.requestPermissions(OngoingAct.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_GPS);
                dialog.dismiss();
            }, (dialog, i) -> dialog.dismiss(), "");

        } else {
            fusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    protected void createLocationRequest() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        initializeLocationCallback();
        getCurrentLocation(LOCATION_REQUEST_TYPE_INITIAL);
    }

    @Override
    protected void onDestroy() {
        if (dialog1 != null)
            Utils.closeDialog(dialog1);

        super.onDestroy();
        stopLocationUpdates();

        // unregister local broadcast
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);

        if (animator != null && animator.isRunning()) {

            animator.cancel();
            map = null;
            if (c_marker != null) {
                c_marker.setVisible(false);
                c_marker.remove();
            } else if (a_marker != null) {
                a_marker = null;
            }
        }
    }

    /**
     * This method is used to get string details
     */
    synchronized void getValueDetail() {
        Field[] fieldss = R.string.class.getDeclaredFields();
        // fields =new int[fieldss.length];
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

    /**
     * To get current location as address.
     */
    private void location() {

        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            if (mLastLocation != null) {
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
            }
        } catch (final IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            try {
                Address = Address.replaceAll("null", "").replaceAll(", ,", "").replaceAll(", ,", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            Address = "";
    }

    private void pickUpDropMarker() {
        try {
            if (map != null) {
                if (p_latitude != null && p_latitude != 0.0 && p_longtitude != null && p_longtitude != 0.0) {
                    if (p_marker != null)
                        p_marker.remove();
                    p_marker = map.addMarker(new MarkerOptions().position(new LatLng(p_latitude, p_longtitude)).title(NC.getResources().getString(R.string.pickuploc)).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_green)).draggable(true));
                    pickupLatLng = new LatLng(p_latitude, p_longtitude);
                }
                if (d_latitude != null && d_latitude != 0.0 && d_longtitude != null && d_longtitude != 0.0) {
                    if (d_marker != null)
                        d_marker.remove();
                    int px = getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);
                    Bitmap mDotMarkerBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(mDotMarkerBitmap);
                 //   Drawable shape = getResources().getDrawable(R.drawable.cust_progress);
                   // shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
                   // shape.draw(canvas);
                    d_marker = map.addMarker(new MarkerOptions().position(new LatLng(d_latitude, d_longtitude)).title(NC.getResources().getString(R.string.droploc)).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_red)).draggable(true));
                    dropLatLng = new LatLng(d_latitude, d_longtitude);
                }
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to visible and invisible pickup and dropup address.
     */
    public void dropVisible() {
      /*  final float scale = this.getResources().getDisplayMetrics().density;
        int pixels = (int) (95 * scale + 0.5f);
        dropLay.getLayoutParams().height = pixels;
        dropLay.invalidate();

        dropppp.setVisibility(View.VISIBLE);

        pickup_pinlay.setVisibility(View.VISIBLE);
        pickup_pin.setVisibility(View.GONE);
        pickup_drop_Sep.setVisibility(View.VISIBLE);*/

    }

    /**
     * Initially update the trip details based on get_trip_detail response.
     */
    private void init() {


        Systems.out.println("_________________OOOO" + MainActivity.mMyStatus.getOnstatus());
        if (MainActivity.mMyStatus.getOnstatus().equalsIgnoreCase("on")) {
            HeadTitle.setText(NC.getResources().getString(R.string.pickup_passenger));
            HeadTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            butt_onboard.setText("I've Arrived");
            butt_onboard.setVisibility(View.VISIBLE);
            passnameTxt.setText(MainActivity.mMyStatus.getOnpassengerName());
            mobile_txt.setText(MainActivity.mMyStatus.getpassengerphone());
            final String pickup = MainActivity.mMyStatus.getOnpickupLocation();

            CurrentlocationTxt.setText(Html.fromHtml(pickup));
            FontHelper.applyFont(OngoingAct.this, CurrentlocationTxt);

            if (!MainActivity.mMyStatus.getPassengerOndropLocation().equals("")) {
                final String drop = MainActivity.mMyStatus.getPassengerOndropLocation();
                if (!drop.trim().equals("")) {
                    dropVisible();
                    droplocationTxt.setText(Html.fromHtml(drop));
                }
            } else
                droplocationTxt.setVisibility(View.GONE);
            if (!MainActivity.mMyStatus.getpassengerNotes().equals("")) {
                final String notes = MainActivity.mMyStatus.getpassengerNotes();
                tv_notes.setText("Notes : " + Html.fromHtml(notes));
            } else
                tv_notes.setVisibility(View.GONE);

            if (!MystatusData.getPickup_notes().equals("")) {
                final String notes = MystatusData.getPickup_notes();
                pickup_notesTxt.setText("Pickup Area/Landmark : " + Html.fromHtml(notes));
            } else
                pickup_notesTxt.setVisibility(View.GONE);

            if (!MystatusData.getDropoff_notes().equals("")) {
                final String notes = MystatusData.getDropoff_notes();
                drop_notesTxt.setText("Dropoff Area/Landmark : " + Html.fromHtml(notes));
            } else
                drop_notesTxt.setVisibility(View.GONE);

            if (MainActivity.mMyStatus.getOnpickupLatitude().length() != 0)
                p_latitude = Double.parseDouble(MainActivity.mMyStatus.getOnpickupLatitude());
            if (MainActivity.mMyStatus.getOnpickupLongitude().length() != 0)
                p_longtitude = Double.parseDouble(MainActivity.mMyStatus.getOnpickupLongitude());
            if (MainActivity.mMyStatus.getOndropLatitude().length() != 0)
                d_latitude = Double.parseDouble(MainActivity.mMyStatus.getOndropLatitude());
            if (MainActivity.mMyStatus.getOndropLongitude().length() != 0)
                d_longtitude = Double.parseDouble(MainActivity.mMyStatus.getOndropLongitude());
            if (MainActivity.mMyStatus.getOndriverLatitude().length() != 0)
                driver_latitude = Double.parseDouble(MainActivity.mMyStatus.getOndriverLatitude());
            if (MainActivity.mMyStatus.getOndriverLongitude().length() != 0)
                driver_longtitude = Double.parseDouble(MainActivity.mMyStatus.getOndriverLongitude());
            new GetPickdropLoc().execute();
            navigator_layout.setVisibility(View.VISIBLE);
        } else if (MainActivity.mMyStatus.getOnstatus().equalsIgnoreCase("Arrivd")) {
            HeadTitle.setText(NC.getResources().getString(R.string.heading_ongoing));
            HeadTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            // butt_onboard.setText(NC.getResources().getString(R.string.pass_onboard));
            butt_onboard.setText("Enter OTP");
            passnameTxt.setText(MainActivity.mMyStatus.getOnpassengerName());
            mobile_txt.setText(MainActivity.mMyStatus.getpassengerphone());
            butt_onboard.setVisibility(View.VISIBLE);
            cancellay.setVisibility(View.VISIBLE);
            final String pickup = MainActivity.mMyStatus.getOnpickupLocation();
            CurrentlocationTxt.setText(Html.fromHtml(pickup));
            if (!MainActivity.mMyStatus.getPassengerOndropLocation().equals("")) {
                final String drop = MainActivity.mMyStatus.getPassengerOndropLocation();
                if (!drop.trim().equals("")) {
                    dropVisible();
                    droplocationTxt.setText(Html.fromHtml(drop));
                }
            } else
                droplocationTxt.setVisibility(View.GONE);
            if (!MainActivity.mMyStatus.getpassengerNotes().equals("")) {
                final String notes = MainActivity.mMyStatus.getpassengerNotes();
                tv_notes.setText("Notes : " + Html.fromHtml(notes));
            } else
                tv_notes.setVisibility(View.GONE);

            if (!MystatusData.getPickup_notes().equals("")) {
                final String notes = MystatusData.getPickup_notes();
                pickup_notesTxt.setText("Pickup Area/Landmark : " + Html.fromHtml(notes));
            } else
                pickup_notesTxt.setVisibility(View.GONE);

            if (!MystatusData.getDropoff_notes().equals("")) {
                final String notes = MystatusData.getDropoff_notes();
                drop_notesTxt.setText("Dropoff Area/Landmark : " + Html.fromHtml(notes));
            } else
                drop_notesTxt.setVisibility(View.GONE);

            if (MainActivity.mMyStatus.getOnpickupLatitude().length() != 0)
                p_latitude = Double.parseDouble(MainActivity.mMyStatus.getOnpickupLatitude());
            if (MainActivity.mMyStatus.getOnpickupLongitude().length() != 0)
                p_longtitude = Double.parseDouble(MainActivity.mMyStatus.getOnpickupLongitude());
            if (MainActivity.mMyStatus.getOndropLatitude().length() != 0)
                d_latitude = Double.parseDouble(MainActivity.mMyStatus.getOndropLatitude());
            if (MainActivity.mMyStatus.getOndropLongitude().length() != 0)
                d_longtitude = Double.parseDouble(MainActivity.mMyStatus.getOndropLongitude());
            if (MainActivity.mMyStatus.getOndriverLatitude().length() != 0)
                driver_latitude = Double.parseDouble(MainActivity.mMyStatus.getOndriverLatitude());
            if (MainActivity.mMyStatus.getOndriverLongitude().length() != 0)
                driver_longtitude = Double.parseDouble(MainActivity.mMyStatus.getOndriverLongitude());
            new GetPickdropLoc().execute();
        } else if (MainActivity.mMyStatus.getOnstatus().equalsIgnoreCase("Complete")) {
            //   TripcancelTxt.setVisibility(View.GONE);
            timer_lay.setVisibility(View.GONE);

            HeadTitle.setText(NC.getResources().getString(R.string.ongoing_journey));
            HeadTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            tripinprogress_lay.setVisibility(View.GONE);
            pickup_drop_lay.setVisibility(View.GONE);
            contact_lay.setVisibility(View.GONE);
            cancellay.setVisibility(View.VISIBLE);
            phonelay.setVisibility(View.GONE);

            contact_txt.setVisibility(View.VISIBLE);
            //tripDetails_lay.setBackgroundColor(getResources().getColor(R.color.white));
            trip_view.setVisibility(View.VISIBLE);
            butt_onboard.setText(NC.getResources().getString(R.string.arvd_destination));
            endtrip.setVisibility(View.VISIBLE);
            toll_lay.setVisibility(View.VISIBLE);
            start_trip.setVisibility(View.GONE);
            trip_lay.setVisibility(View.GONE);

            passnameTxt.setText(MainActivity.mMyStatus.getOnpassengerName());
            mobile_txt.setText(MainActivity.mMyStatus.getpassengerphone());
            final String pickup = MainActivity.mMyStatus.getOnpickupLocation();
            CurrentlocationTxt.setText(Html.fromHtml(pickup));
            FontHelper.applyFont(OngoingAct.this, CurrentlocationTxt);

            if (!MainActivity.mMyStatus.getPassengerOndropLocation().equals("")) {
                final String drop = MainActivity.mMyStatus.getPassengerOndropLocation();
                if (!drop.trim().equals("")) {
                    dropVisible();
                    droplocationTxt.setText(Html.fromHtml(drop));
                }
            } else
                droplocationTxt.setVisibility(View.GONE);
            if (!MainActivity.mMyStatus.getpassengerNotes().equals("")) {
                final String notes = MainActivity.mMyStatus.getpassengerNotes();
                tv_notes.setText(Html.fromHtml(notes));
            } else
                tv_notes.setVisibility(View.GONE);

            if (!MystatusData.getPickup_notes().equals("")) {
                final String notes = MystatusData.getPickup_notes();
                pickup_notesTxt.setText("Pickup Area/Landmark : " + Html.fromHtml(notes));
            } else
                pickup_notesTxt.setVisibility(View.GONE);

            if (!MystatusData.getDropoff_notes().equals("")) {
                final String notes = MystatusData.getDropoff_notes();
                drop_notesTxt.setText("Dropoff Area/Landmark : " + Html.fromHtml(notes));
            } else
                drop_notesTxt.setVisibility(View.GONE);


            if (MainActivity.mMyStatus.getOnpickupLatitude().length() != 0)
                p_latitude = Double.parseDouble(MainActivity.mMyStatus.getOnpickupLatitude());
            if (MainActivity.mMyStatus.getOnpickupLongitude().length() != 0)
                p_longtitude = Double.parseDouble(MainActivity.mMyStatus.getOnpickupLongitude());
            if (MainActivity.mMyStatus.getOndropLatitude().length() != 0)
                d_latitude = Double.parseDouble(MainActivity.mMyStatus.getOndropLatitude());
            if (MainActivity.mMyStatus.getOndropLongitude().length() != 0)
                d_longtitude = Double.parseDouble(MainActivity.mMyStatus.getOndropLongitude());
            if (MainActivity.mMyStatus.getOndriverLatitude().length() != 0)
                driver_latitude = Double.parseDouble(MainActivity.mMyStatus.getOndriverLatitude());
            if (MainActivity.mMyStatus.getOndriverLongitude().length() != 0)
                driver_longtitude = Double.parseDouble(MainActivity.mMyStatus.getOndriverLongitude());
            new GetPickdropLoc().execute();

            speed_lay.setVisibility(View.GONE);
//            myHandler.postDelayed(r, 0);
            butt_onboard.setVisibility(View.VISIBLE);
        } else {
            new GetPickdropLoc().execute();
            butt_onboard.setVisibility(View.INVISIBLE);
            passnameTxt.setText(NC.getResources().getString(R.string.you));
            nodataTxt.setText(NC.getResources().getString(R.string.nodata));
            nodataTxt.setVisibility(View.VISIBLE);
            mapsupport_lay.setVisibility(View.GONE);
        }
    }

    double roundTwoDecimals(double d) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat formatter = (DecimalFormat) nf;
        formatter.applyPattern("#.##");
        String fString = formatter.format(d);
        return Double.parseDouble(fString);
    }

    /**
     * This method is to check current location is in the route.
     */
    private boolean checkLocationInRoute() {
        boolean inside = true;
        if (Route.line != null) {
            inside = PolyUtil.isLocationOnEdge(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), Route.line.getPoints(), true, 1500);
        }
        return inside;
    }

    @Override
    public void onBackPressed() {
        ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

        if (taskList.get(0).numActivities == 1 &&
                taskList.get(0).topActivity.getClassName().equals(this.getClass().getName())) {
            Log.i(TAG, "This is last activity in the stack");
            startActivity(new Intent(OngoingAct.this,HomePageActivity.class));
            finish();
        } else {
            super.onBackPressed();
        }

    }

    /**
     * convert Speed
     */
    private double convertSpeed(double speed) {
        return ((speed * 3600) * 0.001);
    }

    /**
     * This method is used to round the decimal value
     */
    private double roundDecimal(double value, final int decimalPlace) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
        value = bd.doubleValue();
        return value;
    }

    /**
     * Marker Animation with array of latlng
     */
    public void animateLine(ArrayList<LatLng> Trips, Marker marker, float bearings) {
        _trips.clear();
        _trips.addAll(Trips);
        _marker = marker;
        animteBearing = bearings;
        animateMarker();
    }

    /**
     * Marker Animation with array of latlng
     */
    public void animateMarker() {
        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                return _latLngInterpolator.interpolate(fraction, startValue, endValue);
            }
        };
        Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");


        for (int i = 0; i < _trips.size(); i++) {
            animator = ObjectAnimator.ofObject(_marker, property, typeEvaluator, _trips.get(i));
        }

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                listPoint.clear();
                if (c_marker != null && map != null) {
                    c_marker.setVisible(false);
                    c_marker.remove();

                        a_marker = map.addMarker(new MarkerOptions().position(savedLatLng).rotation(0).anchor(0.5f, 0.5f).title(NC.getResources().getString(R.string.you_are_here)).icon(BitmapDescriptorFactory.fromResource(R.drawable.top)));


                    a_marker.setVisible(true);
                    if (MapWrapperLayout.ismMapIsTouched()) {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(savedLatLng, zoom));
                    }
                    if (savedpoint.size() > 1) {
                        for (int i = 0; i < savedpoint.size(); i++) {
                            listPoint.add(savedpoint.get(i));
                        }
                        savedpoint.clear();
                        animStarted = false;
                        animLocation = true;
                    } else {
                        animStarted = false;
                        animLocation = false;
                    }
                }

            }
        });
        animator.setDuration(5000);
        animator.start();
    }

    private void startSOSService() {
        SessionSave.saveSession("sos_id", SessionSave.getSession("Id", OngoingAct.this), OngoingAct.this);
        SessionSave.saveSession("user_type", "d", OngoingAct.this);
      //  startService(new Intent(OngoingAct.this, SOSService.class));
    }

    private ArrayList<StopData> parseStop(String path) {
        ArrayList<StopData> stopDataArrayList = new ArrayList<>();
        stopListData = new ArrayList<>();
        Gson gson = new Gson();
        Type type = new TypeToken<List<StopData>>() {
        }.getType();

        ArrayList<StopData> stopList = gson.fromJson(path, type);

        for (int i = 0; i < stopList.size(); i++) {
            stopListData.add(stopList.get(i).getLatLng());
            stopDataArrayList.add(stopList.get(i));
        }
      //  pickUpDropView.setData(stopList, "ONGOING", SessionSave.getSession("Lang", OngoingAct.this));
        return stopDataArrayList;

    }

    private void setStopAdapter() {
        if (getStops(stopLists).size() > 0) {
            stop_recyclerView.setVisibility(View.VISIBLE);
            StopListAdapter adapter = new StopListAdapter(OngoingAct.this, getStops(stopLists));
            stop_recyclerView.setAdapter(adapter);
        } else {
            stop_recyclerView.setVisibility(View.GONE);
            stop_recyclerView.setAdapter(null);
        }
    }

    private ArrayList<String> getStops(ArrayList<StopData> stopLists) {
        ArrayList<String> splitStops = new ArrayList<>();
        for (int j = 1; j < stopLists.size(); j++) {
            splitStops.add(stopLists.get(j).getPlaceName());
        }
        return splitStops;
    }

    private void showLowAccuracyAlert() {
        dialog1 = Utils.alert_view_dialog(this, null, NC.getString(R.string.low_gps_alert_message), NC.getString(R.string.ok), NC.getString(R.string.cancel), true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, "");
    }

    /**
     * Method to create views dynamically if ArrayList<StopData> value not available (ie., Normal flow)
     * <p>
     * New ArrayList of StopData values created with pickup and drop(if available) and dynamic views created based on that ArrayList
     *
     * @param p_pickloc
     * @param p_droploc
     */
    private ArrayList<StopData> createPickAndStopView(String p_pickloc, String p_latitude, String p_longtitude, String p_droploc, String d_latitude, String d_longtitude) {
        ArrayList<StopData> pickUpDropList = new ArrayList<>();
        if (p_pickloc != null && !p_pickloc.isEmpty() && p_latitude != null && !p_latitude.isEmpty() && p_longtitude != null && !p_longtitude.isEmpty())
            pickUpDropList.add(new StopData(0, Double.parseDouble(p_latitude), Double.parseDouble(p_longtitude), p_pickloc, "", ""));
        if (p_droploc != null && !p_droploc.isEmpty() && d_latitude != null && !d_latitude.isEmpty() && d_longtitude != null && !d_longtitude.isEmpty())
            pickUpDropList.add(new StopData(0, Double.parseDouble(d_latitude), Double.parseDouble(d_longtitude), p_droploc, "", ""));
    //    pickUpDropView.setData(pickUpDropList, "ONGOING", SessionSave.getSession("Lang", OngoingAct.this));
        return pickUpDropList;
    }

    private void setDelayForCancel() {
        timer_lay.setVisibility(View.GONE);
        //      TripcancelTxt.setVisibility(View.GONE);

        int time_out = 0;

        if (SessionSave.getSession("wait_interval", OngoingAct.this).equalsIgnoreCase("")) {
            time_out = 60;
        } else {
            time_out = Integer.parseInt(SessionSave.getSession("wait_interval", OngoingAct.this));
        }

        countDownTimer = new CountDownTimer((time_out) * 1000L, 1000) {
            int time = 1;

            @Override
            public void onTick(final long millisUntilFinished_) {
                Systems.out.println("NOTIFY onTick");
                long sec = millisUntilFinished_ / 1000;
                long minutes = 0;
                if (sec >= 60) {
                    minutes = sec / 60;
                    sec = sec - (minutes * 60);
                }

                pickup_timerTxt.setText(String.valueOf(millisUntilFinished_ / 1000));
                time++;
            }

            @Override
            public void onFinish() {
                Systems.out.println("NOTIFY onFinish");
                try {
                    countDownTimer.cancel();
                    // TripcancelTxt.setVisibility(View.VISIBLE);
                    timer_lay.setVisibility(View.GONE);

                } catch (Exception e) {
                    countDownTimer.cancel();
                    // TripcancelTxt.setVisibility(View.VISIBLE);
                    timer_lay.setVisibility(View.GONE);

                }
            }
        }.start();

      /*  if (!SessionSave.getSession(CommonData.DRIVER_ARRIVED_TIME, this).isEmpty()) {
            long enableTime = 0L, driverArrivedTime = 0L, currentTime = 0L;
            try {
                driverArrivedTime = Long.parseLong(SessionSave.getSession(CommonData.DRIVER_ARRIVED_TIME, this));
                enableTime = Long.parseLong(SessionSave.getSession(CommonData.SHOW_CANCEL_BUTTON, this)) * (1000 * 60); //300000
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            currentTime = System.currentTimeMillis();
            Systems.out.println("setDelayForCancel(): " + enableTime + "**" + driverArrivedTime + "**" + (currentTime - driverArrivedTime) + "**" + currentTime);
            if ((currentTime - driverArrivedTime) > enableTime) {
                Systems.out.println("setDelayForCancel(): 1");
                TripcancelTxt.setVisibility(View.VISIBLE);
            } else {
                Systems.out.println("setDelayForCancel(): 2 " + (enableTime - (currentTime - driverArrivedTime)));
                TripcancelTxt.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TripcancelTxt.setVisibility(View.VISIBLE);
                    }
                }, enableTime - (currentTime - driverArrivedTime));
            }
        } else
            TripcancelTxt.setVisibility(View.VISIBLE);*/
    }

    /**
     * To get pickup/drip location as address and place the pickup/drop markers on map.
     */
    private class GetPickdropLoc extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(final Void... params) {
            try {
                location();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {

            super.onPostExecute(result);
            try {
                map.clear();
                startLocationUpdates();
                if (mLastLocation != null) {
                    latitude1 = mLastLocation.getLatitude();
                    longitude1 = mLastLocation.getLongitude();
                    bearing = mLastLocation.getBearing();
                    currentLatLng = new LatLng(latitude1, longitude1);
                }
                if (bearing >= 0)
                    bearing = bearing + 90;
                else
                    bearing = bearing - 90;
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                bearing = 0;
                pickUpDropMarker();
                if (driver_latitude != null && driver_latitude != 0.0 && driver_longtitude != null && driver_longtitude != 0.0) {
                    currentLatLng = new LatLng(driver_latitude, driver_longtitude);
                }

                new Handler().postDelayed(() -> {
                    ROUTE_DRAW_ON_START = true;
                    mHandler.sendEmptyMessage(1);
                }, 5000);

                if (!Address.equals("")) {
                    MainActivity.mMyStatus.setOndropLocation(Address);
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Used to call the driver arrived Api and parse the response
     */
    private class DriverArrived implements APIResult {
        DriverArrived(final String url, JSONObject data) {

            try {
                if (isOnline()) {
                    butt_onboard.setEnabled(false);
                    new APIService_Retrofit_JSON(OngoingAct.this, this, data, false).execute(url);
                } else {
                    dialog1 = Utils.alert_view(OngoingAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, OngoingAct.this, "4");
                }
            } catch (Exception e) {
                butt_onboard.setEnabled(true);
                e.printStackTrace();
            }
        }

        /**
         * Parse the response and update the UI.
         */
        @Override
        public void getResult(final boolean isSuccess, final String result) {
            butt_onboard.setEnabled(true);
            try {
                if (isSuccess) {
                    final JSONObject json = new JSONObject(result);
                    LocationUpdate.ClearSession(OngoingAct.this);
                    if (json.getInt("status") == 1) {
                        SessionSave.saveSession(CommonData.DRIVER_ARRIVED_TIME, String.valueOf(System.currentTimeMillis()), OngoingAct.this);
                        setDelayForCancel();
                        SessionSave.saveSession("Ongoing", "ongoing", OngoingAct.this);
                        SessionSave.saveSession("otp_enter", "yes", OngoingAct.this);

                        MainActivity.mMyStatus.setOnstatus("Arrivd");
                        HeadTitle.setText(NC.getResources().getString(R.string.heading_ongoing));
                        HeadTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        // butt_onboard.setText(NC.getResources().getString(R.string.pass_onboard));

                        butt_onboard.setText("Enter Otp");
                        cancellay.setVisibility(View.VISIBLE);
                        SessionSave.saveSession("status", "B", OngoingAct.this);
                        SessionSave.setWaitingTime(0L, OngoingAct.this);
                        nonactiityobj.startServicefromNonActivity(OngoingAct.this);
                        // CancelTxt.setText(String.format(Locale.UK, "00:00:00"));

                        new GetPickdropLoc().execute();
                    } else if (json.getInt("status") == -1) {
                        SessionSave.saveSession("status", "F", OngoingAct.this);
                        MainActivity.mMyStatus.settripId("");
                        SessionSave.saveSession("trip_id", "", OngoingAct.this);
                        MainActivity.mMyStatus.setOnstatus("On");
                        MainActivity.mMyStatus.setOnPassengerImage("");
                        MainActivity.mMyStatus.setOnpassengerName("");
                        MainActivity.mMyStatus.setOndropLocation("");
                        MainActivity.mMyStatus.setOnpickupLatitude("");
                        MainActivity.mMyStatus.setOnpickupLongitude("");
                        MainActivity.mMyStatus.setOndropLatitude("");
                        MainActivity.mMyStatus.setOndropLongitude("");
                        SessionSave.saveSession("Ongoing", "farecal", OngoingAct.this);
                        final Intent jobintent = new Intent(OngoingAct.this,HomePageActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("alert_message", json.getString("message"));
                        jobintent.putExtras(extras);
                        startActivity(jobintent);
                        finish();
                    } else {
                        CancelTxt.setText("00:00:00");
                        nonactiityobj.startServicefromNonActivity(OngoingAct.this);
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {

                            // ShowToast(OngoingAct.this, NC.getString(R.string.server_error));
                        }
                    });
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Used to call the driver_status_update API and parse the response.
     */
    private class Onboard implements APIResult {
        String p_pickloc = "";
        String p_droploc = "";
        String dropLattitue = "";
        String dropLongitute = "";


        public Onboard(final String url, JSONObject data) {
            LocationUpdate.ClearSession(OngoingAct.this);
            LocationUpdate.sLocation = "";
            LocationUpdate.localDistance = 0.0;
            localDistance = 0.0;
            SessionSave.saveSession(CommonData.DRIVER_LOCATION, "", OngoingAct.this);
            try {
                if (isOnline()) {
                    butt_onboard.setEnabled(false);
                    new APIService_Retrofit_JSON(OngoingAct.this, this, data, false).execute(url);
                } else {
                    dialog1 = Utils.alert_view(OngoingAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, OngoingAct.this, "4");
                }
            } catch (Exception e) {
                butt_onboard.setEnabled(true);
                e.printStackTrace();
            }
        }

        /**
         * Parse the response and update the UI.
         */
        @Override
        public void getResult(final boolean isSuccess, final String result) {
            butt_onboard.setEnabled(true);
            try {
                if (isSuccess) {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        if (sendPickupPoints != null) {
                            SessionSave.saveLastLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), OngoingAct.this);
                            sendPickupPoints.pickUpdate(mLastLocation);
                        }
                        final JSONObject detail = json.getJSONObject("detail");
                        SessionSave.saveSession("Metric", detail.getString("metric"), OngoingAct.this);
                        if (SessionSave.getSession("Metric", OngoingAct.this).equalsIgnoreCase("KM")) {
                            total_km.setText("Total km");
                        } else {
                            total_km.setText("Total miles");
                        }
                        SessionSave.saveSession("odameter_status", "", OngoingAct.this);
                        card_view_pickup.setCardElevation(0);
                        card_view_pickup.setUseCompatPadding(false);
                        card_view_pickup.setRadius(0);
                        view_line_trip.setVisibility(View.VISIBLE);
                        HeadTitle.setText(NC.getResources().getString(R.string.ongoing_journey));
                        tripinprogress_lay.setVisibility(View.GONE);
                        pickup_drop_lay.setVisibility(View.GONE);
                        contact_lay.setVisibility(View.GONE);
                        cancellay.setVisibility(View.VISIBLE);
                        phonelay.setVisibility(View.GONE);

                        contact_txt.setVisibility(View.VISIBLE);
                        //  tripDetails_lay.setBackgroundColor(getResources().getColor(R.color.white));
                        trip_view.setVisibility(View.VISIBLE);
                        HeadTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        //    TripcancelTxt.setVisibility(View.GONE);
                        butt_onboard.setText(NC.getResources().getString(R.string.arvd_destination));
                        endtrip.setVisibility(View.VISIBLE);
                        toll_lay.setVisibility(View.VISIBLE);

                        start_trip.setVisibility(View.GONE);
                        trip_lay.setVisibility(View.GONE);
                        speed_lay.setVisibility(View.GONE);

                        waitingTimeTxt.setVisibility(View.VISIBLE);

                        speedTxt.setText(String.format(Locale.UK, "%.2f", LocationUpdate.speed) + metricss.toLowerCase());
                        waitingTimeTxt.setText(String.format(Locale.UK, CommonData.getDateForWaitingTime(SessionSave.getWaitingTime(OngoingAct.this))));

                        SessionSave.setWaitingTime(0L, OngoingAct.this);
                        SessionSave.saveSession("travel_status", "2", OngoingAct.this);
                        MainActivity.mMyStatus.setOnstatus("Complete");
                        MainActivity.mMyStatus.setdistance("");
                        SessionSave.saveSession("status", "A", OngoingAct.this);
                        nonactiityobj.startServicefromNonActivity(OngoingAct.this);
                        p_pickloc = detail.getString("pickup_location");
                        p_droploc = detail.getString("drop_location");
                        dropLattitue = detail.getString("drop_latitude");
                        dropLongitute = detail.getString("drop_longitude");

                        SessionSave.setDistance(0L, OngoingAct.this);
                        SessionSave.setGoogleDistance(0L, OngoingAct.this);
                        SessionSave.setArriveDistance(0L, OngoingAct.this);
                        SessionSave.setDistanceCityLimit(0.0, OngoingAct.this);
                        SessionSave.saveSession("city_limit_distance", "0.0", OngoingAct.this);

                        SessionSave.saveSession("isstart_trip", "true", OngoingAct.this);
                        MainActivity.mMyStatus.setOnpickupLocation(p_pickloc);
                        MainActivity.mMyStatus.setOndropLocation(p_droploc);
                        txt_pickup.setText(p_pickloc);
                        txt_drop.setText(p_droploc);

                        final String pickup = MainActivity.mMyStatus.getOnpickupLocation();
                        String s = Html.fromHtml(pickup).toString();
                        CurrentlocationTxt.setText(s);
                        FontHelper.applyFont(OngoingAct.this, CurrentlocationTxt);
                        if (latitude1 != 0.0) {
                            pickupLatLng = new LatLng(latitude1, longitude1);
                            p_latitude = latitude1;
                            p_longtitude = longitude1;
                        }
                        if (pickupLatLng != null && pickupLatLng.latitude != 0.0 && pickupLatLng.longitude != 0.0) {
                            if (p_marker != null)
                                p_marker.remove();
                            p_marker = map.addMarker(new MarkerOptions().position(new LatLng(pickupLatLng.latitude, pickupLatLng.longitude)).title(NC.getResources().getString(R.string.pickuploc)).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_green)).draggable(true));
                        }
                        if (!MainActivity.mMyStatus.getPassengerOndropLocation().equals("")) {
                            final String drop = MainActivity.mMyStatus.getPassengerOndropLocation();

                            if (!drop.trim().equals("")) {
                                dropVisible();
                                droplocationTxt.setText(Html.fromHtml(drop));
                            }

                            navigator_layout.setVisibility(View.VISIBLE);
                        } else {
                            droplocationTxt.setVisibility(View.GONE);
                        }


                        JSONArray stops = null;
                        if (json.getJSONObject("detail").has("stops"))
                            stops = json.getJSONObject("detail").getJSONArray("stops");

                        if (stops != null && stops.length() > 0)
                            stopLists = parseStop(stops.toString());
                        else
                            stopLists = createPickAndStopView(p_pickloc, p_latitude.toString(), p_longtitude.toString(), p_droploc, dropLattitue, dropLongitute);

                        // setStopAdapter();
                        if (json.getJSONObject("detail").has("route_path")) {
                            mroute = json.getJSONObject("detail").getString("route_path");
                        }


                        new GetPickdropLoc().execute();
                    } else if (json.getInt("status") == -1) {
                        final Intent jobintent = new Intent(OngoingAct.this, TripHistoryAct.class);
                        Bundle extras = new Bundle();
                        extras.putString("alert_message", json.getString("message"));
                        jobintent.putExtras(extras);
                        startActivity(jobintent);
                        finish();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {

                            // ShowToast(OngoingAct.this, NC.getString(R.string.server_error));
                        }
                    });
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
            finally {
                JSONObject j = new JSONObject();
                try {
                    j.put("trip_id", SessionSave.getSession("trip_id", OngoingAct.this));
                    final String Url = "type=get_odometer";
                    new getOdometer(Url, j);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Used to call the driver_fare_update API and parse the response.
     */
    private class FreeUpdate implements APIResult {
        public FreeUpdate(final String url) {

            try {
                if (isOnline()) {
                    butt_onboard.setEnabled(false);
                    new APIService_Retrofit_JSON(OngoingAct.this, this, "", true).execute(url);
                } else {
                    dialog1 = Utils.alert_view(OngoingAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, OngoingAct.this, "4");
                }
            } catch (Exception e) {
                butt_onboard.setEnabled(true);
                e.printStackTrace();
            }
        }

        /**
         * Parse the response and update the UI.
         */
        @Override
        public void getResult(final boolean isSuccess, final String result) {
            butt_onboard.setEnabled(true);
            try {
                if (isSuccess) {
                    SessionSave.saveSession("Ongoing", "farecal", OngoingAct.this);
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // ShowToast(OngoingAct.this, NC.getString(R.string.server_error));
                        }
                    });
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Used to call the complete_trip API and parse the response.
     */
    private class CompleteTrip implements APIResult {
        public CompleteTrip(final String url, final Double latitude, final Double longitude) {
            Systems.out.println("distanceeeeee " + stopLists.size() + "____" + SessionSave.getGoogleDistance(OngoingAct.this));
            try {
                final JSONObject j = new JSONObject();
                CommonData.current_trip_accept = 0;
                if (stopLists.size() >= 2)
                    stopLists.set(stopLists.size() - 1, new StopData((int) (new Date().getTime()), latitude1, longitude1, SessionSave.getSession("", OngoingAct.this).replaceAll("\n", " "), "", ""));
                else
                    stopLists.add(new StopData((int) (new Date().getTime()), latitude1, longitude1, SessionSave.getSession("drop_location", OngoingAct.this).replaceAll("\n", " "), "", ""));
                j.put("trip_id", SessionSave.getSession("trip_id", OngoingAct.this));
                j.put("drop_latitude", Double.toString(latitude1));
                j.put("drop_longitude", Double.toString(longitude1));
                j.put("drop_location", SessionSave.getSession("drop_location", OngoingAct.this).replaceAll("\n", " "));
                Systems.out.println("Nandhini Distance Calculation setGoogleDistance --- 1" + SessionSave.getDistance(OngoingAct.this) + "___" + SessionSave.getGoogleDistance(OngoingAct.this) + "______" + (SessionSave.getDistance(OngoingAct.this) + SessionSave.getGoogleDistance(OngoingAct.this)));
                j.put("distance", SessionSave.getDistance(OngoingAct.this) + SessionSave.getGoogleDistance(OngoingAct.this));
                j.put("actual_distance", SessionSave.getDistance(OngoingAct.this) + SessionSave.getGoogleDistance(OngoingAct.this));
                j.put("waiting_hour", SessionSave.getSession("waitingHr", OngoingAct.this));
                j.put("waypoints", SessionSave.ReadGoogleWaypoints(OngoingAct.this));
                String curVersion = BuildConfig.VERSION_NAME;
                j.put("driver_app_version", curVersion);
                j.put("new_distance", localDistance);
                j.put("stops", new JSONArray(new Gson().toJson(stopLists)));
                if (servicesJsonArray != null && servicesJsonArray.length() > 0) {
                    j.put("service_charge", servicesJsonArray);
                }

                j.put("stops", new JSONArray(new Gson().toJson(stopLists)));


                if (booking_Type.equals("2") || booking_Type.equals("3")) {
                    SessionSave.saveSession("type_book", booking_Type, OngoingAct.this);
                    j.put("km_odameter", SessionSave.getSession("end_odameter", OngoingAct.this));

                }


                if (booking_Type.equals("0")) {
                    j.put("distance_after_citylimitkm", "0.0");

                }

                if(enable_os_waiting_fare)
                {
                    j.put("os_waiting_hr", manual_outstation_hours);


                }

                if(SessionSave.getSession("ishills",OngoingAct.this).equals("false"))
                {
                    j.put("ishills",0);
                }
                else {
                    j.put("ishills",1);
                }

                boolean distanceCalcInprogress = false;

               /* JSONArray wayData = SessionSave.ReadGoogleWaypoints(OngoingAct.this);
                Systems.out.println("WayDistance**" + j);
                try {
                    for (int i = 0; i < wayData.length(); i++) {
                        WayPointsData wayPointsData = new Gson().fromJson(wayData.get(i).toString(), WayPointsData.class);
                        if (wayPointsData.getDist() == 0.0)
                            distanceCalcInprogress = true;
                        Systems.out.println("WayDistance" + wayPointsData.getDist());

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                if (isOnline()) {
                    butt_onboard.setEnabled(false);
                    new APIService_Retrofit_JSON(OngoingAct.this, this, j, false).execute(url);
                }
            } catch (Exception e) {
                butt_onboard.setEnabled(true);
                e.printStackTrace();
            }
        }

        /**
         * Parse the response and update the UI.
         */
        @Override
        public void getResult(final boolean isSuccess, final String result) {
            //  cancelLoading();
            butt_onboard.setEnabled(true);
            if (dialog1 != null) {
                Utils.closeDialog(dialog1);
            }
            try {
                if (isSuccess) {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 4) {
                        SessionSave.saveSession(CommonData.WAITING_TIME, false, OngoingAct.this);
                        MainActivity.mMyStatus.setOnstatus("");

                        MainActivity.mMyStatus.setOnPassengerImage("");
                        MainActivity.mMyStatus.setOnstatus("Complete");
                        MainActivity.mMyStatus.setOnpassengerName("");
                        MainActivity.mMyStatus.setOnpickupLatitude("");
                        MainActivity.mMyStatus.setOnpickupLongitude("");
                        MainActivity.mMyStatus.setOndropLatitude("");
                        MainActivity.mMyStatus.setOndropLongitude("");
                        SessionSave.saveSession("city_limit_distance", "0.0", OngoingAct.this);

                        SessionSave.setDistanceCityLimit(0L, OngoingAct.this);
                        SessionSave.saveSession("Ongoing", "farecal", OngoingAct.this);
                        SessionSave.saveSession("travel_status", "5", OngoingAct.this);
                        if (json.getJSONObject("detail").has("model_fare_type")) {
                            SessionSave.saveSession("model_fare_type", json.getJSONObject("detail").getString("model_fare_type"), OngoingAct.this);
                        }

                        SessionSave.saveSession("odameter_status", "", OngoingAct.this);
                        waitingTimeTxt.setText(String.format(Locale.UK, NC.getResources().getString(R.string.m_timer)));
                        SessionSave.saveSession("speedwaiting", "", OngoingAct.this);
                        SessionSave.setWaitingTime(0L, OngoingAct.this);
//                        Intent i = new Intent(OngoingAct.this, WaitingTimerRun.class);
//                        stopService(i);
//                        myHandler.removeCallbacks(r);
                        MainActivity.mMyStatus.setsaveTime(timeclear);
                        // showLoading(OngoingAct.this);
                        if (SessionSave.getSession(CommonData.IS_CORPORATE_BOOKING, OngoingAct.this).equals("1")) {
                            setFareCalculatorScreen(result);
                        } else {
                            final Intent farecal = new Intent(OngoingAct.this, FarecalcAct.class);
                            farecal.putExtra("from", "direct");
                            farecal.putExtra("message", result);
                            farecal.putExtra("corporate", SessionSave.getSession(CommonData.IS_CORPORATE_BOOKING, OngoingAct.this));
                            startActivity(farecal);
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    } else if (json.getInt("status") == -1) {
                        MainActivity.mMyStatus.setOnstatus("");
                        MainActivity.mMyStatus.setOnPassengerImage("");
                        MainActivity.mMyStatus.setOnstatus("Complete");
                        MainActivity.mMyStatus.setOnpassengerName("");
                        MainActivity.mMyStatus.setOndropLocation("");
                        MainActivity.mMyStatus.setOnpickupLatitude("");
                        MainActivity.mMyStatus.setOnpickupLongitude("");
                        MainActivity.mMyStatus.setOndropLatitude("");
                        MainActivity.mMyStatus.setOndropLongitude("");
                        MainActivity.mMyStatus.setOndriverLatitude("");
                        MainActivity.mMyStatus.setOndriverLongitude("");
                        SessionSave.saveSession("status", "F", OngoingAct.this);
                        SessionSave.saveSession("trip_id", "", OngoingAct.this);
                        final String status_update = "type=driver_status_update&driver_id=" + SessionSave.getSession("Id", OngoingAct.this) + "&latitude=" + latitude1 + "&longitude=" + longitude1 + "&status=" + "F" + "&trip_id=";
                        SessionSave.saveSession("Ongoing", "flagger", OngoingAct.this);
                        new FreeUpdate(status_update);
                        // showLoading(OngoingAct.this);
                        final Intent jobintent = new Intent(OngoingAct.this,HomePageActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("alert_message", json.getString("message"));
                        jobintent.putExtras(extras);
                        startActivity(jobintent);
                        finish();
                    } else {
                        dialog1 = Utils.alert_view(OngoingAct.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", true, OngoingAct.this, "4");
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {

                            ShowToast(OngoingAct.this, NC.getString(R.string.server_error));
                        }
                    });
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * This for update the fare calculator page with API result.
     *
     * @param result
     */
    @SuppressLint("SdCardPath")
    private void setFareCalculatorScreen(String result) {
        // Need to uncommand
        if (result != null) {
            try {
                JSONObject obj = new JSONObject(result);
                JSONObject json = obj.getJSONObject("detail");
                trip_type = json.getString("trip_type");
                if (json.has("promo_type"))
                    promo_type = json.getString("promo_type");
                if (json.has("existing_wallet_amount")) {
                    existing_wallet_amount = json.getString("existing_wallet_amount");
                }
                if (json.has("distance_fare")) {
                    distanceFare = json.getString("distance_fare");
                }

                os_distance = json.getDouble("distance");
                if (json.has("os_duration"))
                    os_duration = (json.getDouble("os_duration") / 60);
                os_fare = Double.parseDouble(json.getString("trip_fare"));

                os_tax = (json.getDouble("company_tax"));


                promo_percentage = (json.getDouble("promo_discount_per"));

                os_plan_fare = json.getDouble("os_plan_fare");
                os_plan_distance = json.getDouble("os_plan_distance");
                os_plan_duration = json.getDouble("os_plan_duration") / 60;
                os_additional_fare_per_distance = json.getDouble("os_additional_fare_per_distance");
                os_additional_fare_per_hour = json.getDouble("os_additional_fare_per_hour");
                os_hr_min = new String[2];
                os_hr_min[0] = String.valueOf((int) json.getDouble("os_duration") / 60);
                os_hr_min[1] = String.valueOf((int) json.getDouble("os_duration") % 60);

                f_metric = json.getString("metric");
                f_tripid = json.getString("trip_id");
                f_distance = json.getString("distance");
                f_totalfare = json.getString("subtotal_fare");
                f_nightfareapplicable = json.getString("nightfare_applicable");
                f_nightfare = json.getString("nightfare");
                f_eveningfare_applicable = json.getString("eveningfare_applicable");
                f_eveningfare = json.getString("eveningfare");
                f_pickup = json.getString("pickup");
                drop_location = json.getString("drop");
                f_waitingtime = json.getString("waiting_time");
                f_waitingcost = json.getString("waiting_cost");
                f_taxamount = json.getString("tax_amount");
                f_tripfare = json.getString("trip_fare");
                f_payamt = json.getString("total_fare");
                f_walletamt = json.getString("wallet_amount_used");
                f_minutes_traveled = json.getString("minutes_traveled");
                f_minutes_fare = json.getString("minutes_fare");
                f_farediscount = json.getString("promodiscount_amount");
                base_fare = json.getString("base_fare");
                cmpTax = json.getString("company_tax");
//
//
//                fare_per_minute = json.getString("fare_per_minute");
//                waiting_fare_minutes = json.getString("waiting_fare_minutes");
//                trip_minutes = json.getString("trip_minutes");
//                min_distance_status = json.getInt("min_distance_status");
//                subtotal = json.getString("subtotal");
//                new_distance_fare = json.getString("new_distance_fare");
//                new_base_fare = json.getString("new_base_fare");
//                distance_fare_metric = json.getString("distance_fare_metric");
//                amt = json.getString("amt");
//                promocode_fare = json.getString("promocode_fare");
//                tax_fare = json.getString("tax_fare");
//                nightfare = json.getString("nightfare");
//                eveningfare = json.getString("eveningfare");

//                if (json.has("pending_cancel_amount"))
//                    cancellation_fee = json.getString("pending_cancel_amount");


                if (json.has("pending_cancel_amount"))
                    pending_cancel_amount = json.getString("pending_cancel_amount");
                fare_calculation_type = json.getString("fare_calculation_type");
                if (f_walletamt.length() != 0)
                    m_walletamt = Double.parseDouble(f_walletamt);
                f_walletamt = String.format(Locale.UK, "%.2f", m_walletamt);
                if (f_payamt.length() != 0)
                    m_payamt = Double.parseDouble(f_payamt);
                f_payamt = String.format(Locale.UK, "%.2f", m_payamt);
                if (f_waitingcost.length() != 0)
                    m_waitingcost = Double.parseDouble(f_waitingcost);
                f_waitingcost = String.format(Locale.UK, "%.2f", m_waitingcost);
                if (f_totalfare.length() != 0)
                    m_totalfare = Double.parseDouble(f_totalfare);
                f_totalfare = String.format(Locale.UK, "%.2f", m_totalfare);
                if (f_distance.length() != 0)
                    m_distance = Double.parseDouble(f_distance);
                f_distance = String.format(Locale.UK, "%.2f", m_distance);
                if (f_tripfare.length() != 0)
                    m_tripfare = Double.parseDouble(f_tripfare);
                f_tripfare = String.format(Locale.UK, "%.2f", m_tripfare);
                if (f_taxamount.length() != 0)
                    m_taxamount = Double.parseDouble(f_taxamount);
                f_taxamount = String.format(Locale.UK, "%.2f", m_taxamount);
                p_dis = String.valueOf(promo_percentage);

                f_fare = m_totalfare;
//                if (tipsTxt.length() != 0) {
//                    f_tips = Double.parseDouble(Uri.decode(tipsTxt.getText().toString()));
//                }
//                f_total = f_fare + f_tips;
                JSONArray ary = new JSONArray(json.getString("gateway_details"));
                int length = ary.length();
                if (trip_type.equals("3")) {
                    calculateAndUpdateFare();
                }
                callurl();
            } catch (JSONException e) {
                Systems.out.println("errorToCovert " + e);
                e.printStackTrace();
            }
        }
    }


    public void calculateAndUpdateFare() {
        double discount_amount = 0.0;
        os_fare = os_plan_fare;
//
//        if (!et_time_hour.getText().toString().isEmpty() && !et_time_mins.getText().toString().isEmpty() && !et_total_disatnce.getText().toString().isEmpty() && !p_dis.isEmpty()) {
//            os_duration = Double.parseDouble(et_time_hour.getText().toString()) + (Double.parseDouble(et_time_mins.getText().toString()) / 60);
//            if (os_duration > os_plan_duration) {
//                os_minute_fare = ((os_duration - (os_plan_duration)) * os_additional_fare_per_hour);
//                os_fare += os_minute_fare;
//            }
//            os_distance = Double.parseDouble(et_total_disatnce.getText().toString());
//            if (os_distance > os_plan_distance) {
//                os_fare += ((os_distance - os_plan_distance) * os_additional_fare_per_distance);
//            }
//
//            if (promo_percentage > 0 && !promo_type.equals("1")) {
//                discount_amount = os_fare * promo_percentage / 100;
//                os_fare -= discount_amount;
//            } else if (promo_type.equals("1")) {
//                discount_amount = Double.parseDouble(f_farediscount);
//                os_fare -= discount_amount;
//            }
//            if (os_tax != 0) {
//                tax = os_fare * os_tax / 100;
//            }
//
//        }
    }

    private void callurl() {

        String url = "type=tripfare_update";
        try {
            JSONObject j = new JSONObject();
//            if (trip_type.equals("3")) {
//                j.put("os_distance", os_distance);
//                j.put("os_actual_amount", "" + amountpayTxt.getText().toString());
//                j.put("os_trip_fare", df.format((os_fare)));
//                j.put("os_promodiscount_amount", b_discount.getText().toString());
//                j.put("os_minutes_traveled", (os_duration * 60));
//                j.put("os_minutes_fare", os_minute_fare);
//            }
            j.put("distance", f_distance);
            j.put("actual_amount", String.valueOf(f_total));
            j.put("trip_fare", f_tripfare);
            j.put("promodiscount_amount", f_farediscount);
            j.put("fare", f_payamt);
            j.put("amount_tobe_paid", amount_tobe_paid);
            j.put("amount_used_from_wallet", amount_used_from_wallet);

            j.put("trip_type", trip_type);
            j.put("trip_id", f_tripid);

            j.put("distance_fare", distanceFare);

            j.put("actual_distance", f_distance);

            j.put("base_fare", base_fare);

            j.put("tips", ""/* + tipsTxt.getText().toString()*/);
            j.put("passenger_promo_discount", promotax);
            j.put("tax_amount", f_taxamount);
            j.put("remarks", "");
            j.put("nightfare_applicable", f_nightfareapplicable);
            j.put("nightfare", f_nightfare);
            j.put("eveningfare_applicable", f_eveningfare_applicable);
            j.put("eveningfare", f_eveningfare);
            j.put("waiting_time", f_waitingtime);
            j.put("waiting_cost", f_waitingcost);
            j.put("creditcard_no", "");
            j.put("creditcard_cvv", Cvv);
            j.put("company_tax", cmpTax);
            j.put("expmonth", "");
            j.put("expyear", "");
            j.put("pay_mod_id", "7");
            j.put("passenger_discount", p_dis);
            j.put("minutes_traveled", f_minutes_traveled);
            j.put("minutes_fare", f_minutes_fare);
            j.put("fare_calculation_type", fare_calculation_type);
            j.put("model_fare_type", SessionSave.getSession("model_fare_type", OngoingAct.this));
            j.put("pending_cancel_amount", pending_cancel_amount);
            new FareUpdate(url, j);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }


    /**
     * This class helps to call the Fare Update API,get the result and parse it.
     */
    private class FareUpdate implements APIResult {
        String msg = "";

        public FareUpdate(String url, JSONObject data) {

            if (isOnline()) {
                new APIService_Retrofit_JSON(OngoingAct.this, this, data, false).execute(url);
            } else {

                dialog1 = Utils.alert_view(OngoingAct.this, "", NC.getResources().getString(R.string.check_internet), NC.getResources().getString(R.string.ok),
                        "", true, OngoingAct.this, "");

            }
        }

        @Override
        public void getResult(boolean isSuccess, final String result) {
            SessionSave.saveSession(CommonData.AMOUNT_USED_FROM_WALLET, "", OngoingAct.this);
            if (isSuccess) {
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        LocationUpdate.ClearSessionwithTrip(getApplicationContext());
                        SessionSave.saveSession("travel_status", "", OngoingAct.this);
                        SessionSave.saveSession("trip_id", "", OngoingAct.this);
                        SessionSave.saveSession("status", "F", OngoingAct.this);
                        MainActivity.mMyStatus.setdistance("");
                        msg = json.getString("message");
                        MainActivity.mMyStatus.setOnstatus("");
                        MainActivity.mMyStatus.setStatus("F");
                        MainActivity.mMyStatus.setOnPassengerImage("");
                        MainActivity.mMyStatus.setOnstatus("On");
                        MainActivity.mMyStatus.setOnstatus("Complete");
                        MainActivity.mMyStatus.setOnpassengerName("");
                        MainActivity.mMyStatus.setOndropLocation("");
                        MainActivity.mMyStatus.setOndropLocation("");
                        MainActivity.mMyStatus.setOnpickupLatitude("");
                        MainActivity.mMyStatus.setOnpickupLongitude("");
                        MainActivity.mMyStatus.setOndropLatitude("");
                        MainActivity.mMyStatus.setOndropLongitude("");
                        JSONObject jsonDriver = json.getJSONObject("driver_statistics");
                        SessionSave.saveSession("driver_statistics", String.valueOf(jsonDriver), OngoingAct.this);
                        LocationUpdate.sTimer = "00:00:00";
                        LocationUpdate.finalTime = 0L;
                        LocationUpdate.timeInMillies = 0L;
                        SessionSave.saveSession("waitingHr", "", OngoingAct.this);
                        CommonData.travel_km = 0;
                        SessionSave.setGoogleDistance(0f, OngoingAct.this);
                        SessionSave.setDistance(0f, OngoingAct.this);
                        SessionSave.setDistanceCityLimit(0L, OngoingAct.this);
                        SessionSave.saveSession("city_limit_distance", "0.0", OngoingAct.this);

                        SessionSave.saveGoogleWaypoints(null, null, "", 0.0, "", OngoingAct.this);
                        SessionSave.saveWaypoints(null, null, "", 0.0, "", OngoingAct.this);
                        Intent jobintent = new Intent(OngoingAct.this, JobdoneAct.class);
                        Bundle bun = new Bundle();
                        bun.putString("message", result);
                        jobintent.putExtras(bun);
                        startActivity(jobintent);
                        finish();
                    } else if (json.getInt("status") == -9) {
                        msg = json.getString("message");
//                        lay_fare.setVisibility(View.VISIBLE);

                        dialog1 = Utils.alert_view(OngoingAct.this, "", msg, NC.getResources().getString(R.string.ok),
                                "", true, OngoingAct.this, "");


                    } else if (json.getInt("status") == 0) {
                        msg = json.getString("message");
//                        lay_fare.setVisibility(View.VISIBLE);
                        dialog1 = Utils.alert_view(OngoingAct.this, "", msg, NC.getResources().getString(R.string.ok),
                                "", true, OngoingAct.this, "");


                    } else if (json.getInt("status") == -1) {
                        msg = json.getString("message");

                        dialog1 = Utils.alert_view(OngoingAct.this, "", msg, NC.getResources().getString(R.string.ok),
                                "", true, OngoingAct.this, "");

                        if (json.has("driver_statistics")) {
                            SessionSave.saveSession("trip_id", "", OngoingAct.this);
                            SessionSave.saveSession("status", "F", OngoingAct.this);
                            MainActivity.mMyStatus.setOnstatus("");
                            MainActivity.mMyStatus.setStatus("F");
                            MainActivity.mMyStatus.setOnPassengerImage("");
                            MainActivity.mMyStatus.setOnstatus("On");
                            MainActivity.mMyStatus.setOnstatus("Complete");
                            MainActivity.mMyStatus.setOnpassengerName("");
                            MainActivity.mMyStatus.setOndropLocation("");
                            MainActivity.mMyStatus.setOndropLocation("");
                            MainActivity.mMyStatus.setOnpickupLatitude("");
                            MainActivity.mMyStatus.setOnpickupLongitude("");
                            MainActivity.mMyStatus.setOndropLatitude("");
                            MainActivity.mMyStatus.setOndropLongitude("");
                            MainActivity.mMyStatus.setOndriverLatitude("");
                            MainActivity.mMyStatus.setOndriverLongitude("");

                            JSONObject jsonDriver = json.getJSONObject("driver_statistics");
                            SessionSave.saveSession("driver_statistics", String.valueOf(jsonDriver), OngoingAct.this);
                        }
                        Intent intent = new Intent(OngoingAct.this,HomePageActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
//                        lay_fare.setVisibility(View.VISIBLE);

                        dialog1 = Utils.alert_view(OngoingAct.this, "", msg, NC.getResources().getString(R.string.ok),
                                "", true, OngoingAct.this, "");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                runOnUiThread(() -> CToast.ShowToast(OngoingAct.this, NC.getString(R.string.server_error)));
//                lay_fare.setVisibility(View.VISIBLE);
            }
        }


    }

    /**
     * Used to call the cancel_trip API and parse the response.
     */
    private class CancelTrip implements APIResult {
        private String msg;

        public CancelTrip(final String url, JSONObject data) {

            try {
                if (isOnline()) {
                    butt_onboard.setEnabled(false);
                    new APIService_Retrofit_JSON(OngoingAct.this, this, data, false).execute(url);
                } else {
                    dialog1 = Utils.alert_view(OngoingAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, OngoingAct.this, "4");
                }
            } catch (Exception e) {
                butt_onboard.setEnabled(true);
                e.printStackTrace();
            }
        }

        /**
         * Parse the response and update the UI.
         */
        @Override
        public void getResult(final boolean isSuccess, final String result) {
            butt_onboard.setEnabled(true);
            try {
                if (isSuccess) {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 3 || json.getInt("status") == 7) {
                        msg = json.getString("message");
                        JSONObject jsonDriver = json.getJSONObject("driver_statistics");
                        SessionSave.saveSession("driver_statistics", String.valueOf(jsonDriver), OngoingAct.this);
                        SessionSave.saveSession("status", "F", OngoingAct.this);
                        MainActivity.mMyStatus.settripId("");
                        SessionSave.saveSession("trip_id", "", OngoingAct.this);
                        MainActivity.mMyStatus.setOnstatus("On");
                        MainActivity.mMyStatus.setOnPassengerImage("");
                        MainActivity.mMyStatus.setOnpassengerName("");
                        MainActivity.mMyStatus.setOndropLocation("");
                        MainActivity.mMyStatus.setPassengerOndropLocation("");
                        MainActivity.mMyStatus.setOnpickupLatitude("");
                        MainActivity.mMyStatus.setOnpickupLongitude("");
                        MainActivity.mMyStatus.setOndropLatitude("");
                        MainActivity.mMyStatus.setOndropLongitude("");
                        MainActivity.mMyStatus.setOndriverLatitude("");
                        MainActivity.mMyStatus.setOndriverLongitude("");
                        SessionSave.saveSession("Ongoing", "farecal", OngoingAct.this);

                        Intent cancelIntent = new Intent();
                        cancelIntent.putExtra("alert_message", json.getString("message"));
                        cancelIntent.setAction(Intent.ACTION_MAIN);
                        cancelIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        cancelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        ComponentName cn = new ComponentName(OngoingAct.this,HomePageActivity.class);
                        cancelIntent.setComponent(cn);

                        startActivity(cancelIntent);
                        finish();
                    } else {
                        msg = json.getString("message");
                        dialog1 = Utils.alert_view(OngoingAct.this, NC.getResources().getString(R.string.message), msg, NC.getResources().getString(R.string.ok), "", true, OngoingAct.this, "4");
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {

                            ShowToast(OngoingAct.this, NC.getString(R.string.server_error));
                        }
                    });
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This class is used to get the trip details when activity in opened, It calls the API and parse the response.
     */
    private class Tripdetails implements APIResult {
        String p_logid = "";
        String p_name = "";
        String p_pickloc = "";
        String p_droploc = "";
        String p_picklat = "";
        String p_picklng = "";
        String p_droplat = "";
        String p_droplng = "";
        String p_driverlat = "";
        String p_driverlng = "";

        private String p_image = "";
        private String p_phone = "";
        private String p_notes = "";
        private String p_driverstatus = "", p_taxi_speed = "", pickup_notes = "", dropoff_notes = "";

        public Tripdetails(final String url, JSONObject data) {

            try {
                if (isOnline()) {
                    butt_onboard.setEnabled(false);
                    new APIService_Retrofit_JSON(OngoingAct.this, this, data, false).execute(url);
                } else {
                    dialog1 = Utils.alert_view(OngoingAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, OngoingAct.this, "4");
                }
            } catch (Exception e) {
                butt_onboard.setEnabled(true);
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            butt_onboard.setEnabled(true);
            try {
                if (isSuccess) {
                    tripInfo.setVisibility(View.VISIBLE);
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        final JSONObject detail = json.getJSONObject("detail");

                        if (detail.getString("street_pickup_trip").trim().equals("1")) {
                           // startActivity(new Intent(OngoingAct.this, StreetPickUpAct.class));
                            Toast.makeText(OngoingAct.this, NC.getString(R.string.you_are_in_trip), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {

                            speedTxt.setText(String.format(Locale.UK, "%.2f", LocationUpdate.speed) + metricss.toLowerCase());
                            waitingTimeTxt.setText(String.format(Locale.UK, CommonData.getDateForWaitingTime(SessionSave.getWaitingTime(OngoingAct.this))));

                            p_logid = detail.getString("trip_id");
                            p_name = detail.getString("passenger_name");
                            p_pickloc = detail.getString("current_location");
                            p_droploc = detail.getString("drop_location");
                            p_picklat = detail.getString("pickup_latitude");
                            p_picklng = detail.getString("pickup_longitude");
                            p_droplat = detail.getString("drop_latitude");
                            p_droplng = detail.getString("drop_longitude");
                            p_driverlat = detail.getString("driver_latitute");
                            p_driverlng = detail.getString("driver_longtitute");
                            p_travelstatus = detail.getString("travel_status");
                            p_driverstatus = detail.getString("driver_status");
                            p_notes = detail.getString("notes");
                            p_phone = detail.getString("passenger_phone");
                            p_image = detail.getString("passenger_image");
                            p_taxi_speed = detail.getString("taxi_min_speed");
                            pickup_notes = detail.getString("pickup_notes");
                            dropoff_notes = detail.getString("dropoff_notes");
                            booking_Type = detail.getString("trip_type");
                            model_id = detail.getString("model_id");

                            SessionSave.saveSession("status", detail.getString("driver_status"), OngoingAct.this);
                            SessionSave.saveSession("Metric", detail.getString("metric"), OngoingAct.this);
                            SessionSave.saveSession("p_image", p_image, OngoingAct.this);
                            SessionSave.saveSession("c", p_travelstatus, OngoingAct.this);
                            if (detail.has(CommonData.IS_CORPORATE_BOOKING)) {
                                SessionSave.saveSession(CommonData.IS_CORPORATE_BOOKING, detail.getString("corporate_booking"), OngoingAct.this);
                            }

                            if (json.getJSONObject("detail").getString("approx_fare").equalsIgnoreCase("0")) {
                                estimatelay.setVisibility(View.GONE);
                            } else {
                                estimatelay.setVisibility(View.GONE);
                                estimateTxt.setText(NC.getResources().getString(R.string.Estimated) + " : " + SessionSave.getSession("site_currency", OngoingAct.this) + " " + json.getJSONObject("detail").getString("approx_fare"));
                            }

                            if (json.getJSONObject("detail").has("route_path"))
                                mroute = json.getJSONObject("detail").getString("route_path");

                            if (json.getJSONObject("detail").has("stops"))
                                stops = json.getJSONObject("detail").getJSONArray("stops");

                            if (stops != null && stops.length() > 0)
                                stopLists = parseStop(stops.toString());
                            else
                                stopLists = createPickAndStopView(p_pickloc, p_picklat, p_picklng, p_droploc, p_droplat, p_droplng);

                            if (json.getJSONObject("detail").has("manual_waiting_time")) {
                                SessionSave.saveSession(CommonData.WAITING_TIME_MANUAL, json.getJSONObject("detail").getString("manual_waiting_time").equals("1"), OngoingAct.this);
                            }
                            if (SessionSave.getSession(CommonData.WAITING_TIME_MANUAL, OngoingAct.this, false)) {
                                ssWaitingTime_img.setVisibility(View.VISIBLE);
                            } else {
                                ssWaitingTime_img.setVisibility(View.GONE);
                            }

                            if (json.getJSONObject("detail").has("enable_os_waiting_fare")) {
                                if (json.getJSONObject("detail").getString("enable_os_waiting_fare").equals("0"))
                                {
                                    SessionSave.saveSession("isonewaytrip","no",OngoingAct.this);

                                    enable_os_waiting_fare = false;
                                }

                                else if (json.getJSONObject("detail").getString("enable_os_waiting_fare").equals("1"))
                                {
                                    SessionSave.saveSession("isonewaytrip","yes",OngoingAct.this);
                                    enable_os_waiting_fare = true;
                                }

                            }
                            else {
                                SessionSave.saveSession("isonewaytrip","no",OngoingAct.this);

                                enable_os_waiting_fare = false;

                            }




                            if (json.getJSONObject("detail").has("is_on_my_way_trip")) {
                                if (json.getJSONObject("detail").getString("is_on_my_way_trip").equals("0"))
                                    trip_types.setText(NC.getString(R.string.triptype) + " : " + NC.getString(R.string.trip_normal));
                                else if (json.getJSONObject("detail").getString("is_on_my_way_trip").equals("1"))
                                    trip_types.setText(NC.getString(R.string.triptype) + " : " + NC.getString(R.string.trip_onmyway));
                            }
                            // Check if the 'detail' object has 'citylimit_data'
                            if (json.has("citylimit_data")) {
                                // Get the 'citylimit_data' array
                                JSONArray citylimitDataArray = json.getJSONArray("citylimit_data");

                                // Loop through the array if there are multiple objects or just get the first element
                                for (int i = 0; i < citylimitDataArray.length(); i++) {
                                    // Get each city limit data as a JSONObject
                                    JSONObject cityLimitData = citylimitDataArray.getJSONObject(i);

                                    // Now you can access individual fields
                                    int id = cityLimitData.getInt("_id");
                                    int cityLimitEnable = cityLimitData.getInt("city_limit_enable");
                                    double cityLatitude = cityLimitData.getDouble("city_latitude");
                                    double cityLongitude = cityLimitData.getDouble("city_longitude");
                                    int cityRadius = cityLimitData.getInt("city_radius");
                                    String farePerDistance = cityLimitData.getString("city_limit_fare_per_distance");
                                    String fareUpto = cityLimitData.getString("city_limit_fare_upto");


                                    SessionSave.saveSession("city_latitude", String.valueOf(cityLatitude), OngoingAct.this);
                                    SessionSave.saveSession("cityLongitude", String.valueOf(cityLongitude), OngoingAct.this);
                                    SessionSave.saveSession("cityRadius", String.valueOf(cityRadius), OngoingAct.this);
                                    // Use the data as needed
                                    System.out.println("citylimit_data: " + cityLatitude);
                                    System.out.println("citylimit_data: " + cityLongitude);
                                    System.out.println("citylimit_data: " + cityRadius);
                                    System.out.println("Latitude: " + cityLatitude + ", Longitude: " + cityLongitude);
                                }
                            }

                            if (p_travelstatus.equalsIgnoreCase("2")) {

                                //i have arrived


                                setStopAdapter();
                            }

                            Systems.out.println("statusss" + p_driverstatus + "__" + p_travelstatus + "___" + SessionSave.getSession(CommonData.IS_CORPORATE_BOOKING, OngoingAct.this));
                            if ((p_driverstatus.equalsIgnoreCase("F")
                                    || p_driverstatus.equalsIgnoreCase("B") ||
                                    (p_driverstatus.equalsIgnoreCase("A")))
                                    && !p_travelstatus.equalsIgnoreCase("5")) {
                                if (p_travelstatus.equalsIgnoreCase("3")) {
                                    HeadTitle.setText(NC.getResources().getString(R.string.waitingpassenger));
                                    view_line_trip.setVisibility(View.VISIBLE);
                                    MainActivity.mMyStatus.setOnstatus("Arrivd");
                                    // setDelayForCancel();
                                } else if (p_travelstatus.equalsIgnoreCase("2")) {
                                    card_view_pickup.setCardElevation(0);
                                    card_view_pickup.setUseCompatPadding(false);
                                    card_view_pickup.setRadius(0);
                                    view_line_trip.setVisibility(View.VISIBLE);
                                    HeadTitle.setText(NC.getResources().getString(R.string.tripprogress));
                                    tripinprogress_lay.setVisibility(View.GONE);
                                    pickup_drop_lay.setVisibility(View.GONE);
                                    contact_lay.setVisibility(View.GONE);
                                    cancellay.setVisibility(View.VISIBLE);
                                    contact_txt.setVisibility(View.VISIBLE);
                                    phonelay.setVisibility(View.GONE);

                                    //  tripDetails_lay.setBackgroundColor(getResources().getColor(R.color.white));
                                    trip_view.setVisibility(View.VISIBLE);
                                    MainActivity.mMyStatus.setOnstatus("Complete");

                                } else if (p_travelstatus.equalsIgnoreCase("9")) {
                                    view_line_trip.setVisibility(View.VISIBLE);
                                    HeadTitle.setText(NC.getResources().getString(R.string.tripdetails));
                                    MainActivity.mMyStatus.setOnstatus("On");
//                                    if (!booking_Type.equals("0")) {
//                                        SessionSave.saveSession("odameter_status", "1", OngoingAct.this);
//                                        showodometer();
//                                    }


                                } else {
                                    HeadTitle.setText(NC.getResources().getString(R.string.tripdetails));
                                }
                                p_pickloc = p_pickloc.trim();
                                if (p_pickloc.length() > 0 && SessionSave.getSession("Lang", OngoingAct.this).equals("en")) {
                                    p_pickloc = Character.toUpperCase(p_pickloc.charAt(0)) + p_pickloc.substring(1);
                                    p_droploc = p_droploc.trim();
                                }
                                if (p_droploc.length() > 0) {
                                    p_droploc = Character.toUpperCase(p_droploc.charAt(0)) + p_droploc.substring(1);
                                }
                                if (p_name.length() > 0) {
                                    p_name = Character.toUpperCase(p_name.charAt(0)) + p_name.substring(1);
                                }
                                if (p_taxi_speed != null && p_taxi_speed.length() > 0) {
                                    SessionSave.saveSession("taxi_speed", p_taxi_speed, OngoingAct.this);
                                }
                                if (p_notes.length() > 0) {
                                    p_notes = Character.toUpperCase(p_notes.charAt(0)) + p_notes.substring(1);
                                }
                                txt_pickup.setText(p_pickloc);
                                txt_drop.setText(p_droploc);


                                MainActivity.mMyStatus.setOnpickupLocation(p_pickloc);
                                MainActivity.mMyStatus.setOndropLocation(p_droploc);
                                MainActivity.mMyStatus.setPassengerOndropLocation(p_droploc);
                                MainActivity.mMyStatus.setOnpickupLatitude(p_picklat);
                                MainActivity.mMyStatus.setOnpickupLongitude(p_picklng);
                                MainActivity.mMyStatus.setOndriverLatitude(p_driverlat);
                                MainActivity.mMyStatus.setOndriverLongitude(p_driverlng);
                                MainActivity.mMyStatus.setOnpassengerName(p_name);
                                MainActivity.mMyStatus.settripId(p_logid);
                                SessionSave.saveSession("trip_id", p_logid, OngoingAct.this);
                                MainActivity.mMyStatus.setpickupLoc(p_pickloc);
                                MainActivity.mMyStatus.setOndropLatitude(p_droplat);
                                MainActivity.mMyStatus.setOndropLongitude(p_droplng);
                                MainActivity.mMyStatus.setdropLoc(p_droploc);
                                MainActivity.mMyStatus.setpassengerId(p_logid);
                                MainActivity.mMyStatus.setphoneNo(p_phone);
                                MainActivity.mMyStatus.setOnPassengerImage(p_image);
                                MainActivity.mMyStatus.setpassengerNotes(p_notes);
                                MainActivity.mMyStatus.setpassengerphone(p_phone);
                                MystatusData.setPickup_notes(pickup_notes);
                                MystatusData.setDropoff_notes(dropoff_notes);

                                init();
                                String imagepath = "";
                                if (!SessionSave.getSession("p_image", OngoingAct.this).equals("")) {
                                    imagepath = SessionSave.getSession("p_image", OngoingAct.this);
                                    Log.i("Imagepath in session", SessionSave.getSession("p_image", OngoingAct.this));
                                } else
                                    imagepath = SessionSave.getSession("noimage_base", OngoingAct.this);
                                Picasso.get().load(imagepath).placeholder(getResources().getDrawable(R.drawable.loadingimage)).error(getResources().getDrawable(R.drawable.map_icon_red)).into(proimg);

                            } else if (p_driverstatus.equalsIgnoreCase("A") && p_travelstatus.equalsIgnoreCase("5")) {

                                if (SessionSave.getSession(CommonData.IS_CORPORATE_BOOKING, OngoingAct.this).equals("1")) {
//                                    setFareCalculatorScreen(result);
                                    CompleteSuccessClick();
                                } else {
                                    Intent i = new Intent(OngoingAct.this, FarecalcAct.class);
                                    i.putExtra("from", "pending");
                                    i.putExtra("lat", detail.getString("drop_latitude"));
                                    i.putExtra("lon", detail.getString("drop_longitude"));
                                    i.putExtra("distance", detail.getString("distance"));
                                    i.putExtra("waitingHr", detail.getString("waiting_time"));
                                    i.putExtra("drop_location", detail.getString("drop_location"));
                                    i.putExtra("stopList", detail.getJSONArray("stops").toString());
                                    i.putExtra("corporate", SessionSave.getSession(CommonData.IS_CORPORATE_BOOKING, OngoingAct.this));
                                    startActivity(i);
                                    overridePendingTransition(0, 0);
                                    finish();
                                }
                            } else {
                                Systems.out.println("haiiiiiiiTriphistory" + p_driverstatus + "___" + p_travelstatus);
                                ShowToast(OngoingAct.this, NC.getResources().getString(R.string.you_are_in_trip));
                                Intent i = new Intent(OngoingAct.this, TripHistoryAct.class);
                                startActivity(i);
                                finish();
                            }
                            tripInfo.post(new Runnable() {
                                @Override
                                public void run() {

                                    layoutheight = tripInfo.getHeight() - 20;
                                    if (map != null) {
                                        map.setPadding(0, layoutheight, 0, 120);
                                    }
                                }
                            });
                        }

                        nodataTxt.setVisibility(View.GONE);
                    } else {
                        Intent i = new Intent(OngoingAct.this, TripHistoryAct.class);
                        startActivity(i);
                        finish();
                    }
                } else {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            ShowToast(OngoingAct.this, NC.getString(R.string.server_error));
                        }
                    });
                    Intent i = new Intent(OngoingAct.this, TripHistoryAct.class);
                    startActivity(i);
                    finish();
                }
            } catch (final Exception e) {
                // TODO: handle exception
                Systems.out.println("pass---j" + e);
                e.printStackTrace();
                Intent i = new Intent(OngoingAct.this, TripHistoryAct.class);
                startActivity(i);
                finish();



            }
            finally {
                if (booking_Type.equals("2") || booking_Type.equals("3")) {

                    JSONObject j = new JSONObject();
                    try {
                        j.put("trip_id", SessionSave.getSession("trip_id", OngoingAct.this));
                        final String Url = "type=get_odometer";
                        new getOdometer(Url, j);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }
    }


    private class getMaskedPhoneNumber implements APIResult {


        public getMaskedPhoneNumber(final String url, JSONObject data) {
            try {
                if (isOnline()) {
                    new APIService_Retrofit_JSON(OngoingAct.this, this, data, false).execute(url);
                } else {
                    dialog1 = Utils.alert_view(OngoingAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, OngoingAct.this, "4");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            try {
                if (isSuccess) {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        if (!TextUtils.isEmpty(json.getString("twilio_number"))) {
                            call_masking_ph_no = json.getString("twilio_number");
                            if (call_masking_ph_no.trim().length() == 0) {
                                dialog1 = Utils.alert_view(OngoingAct.this, NC.getResources().getString(R.string.message), "Please try again later, Lines are busy now", NC.getResources().getString(R.string.ok), "", true, OngoingAct.this, "4");
                            } else {
                                ensureCall();
                            }
                        }
                    } else {
                        if (json.has("message")) {
                            ShowToast(OngoingAct.this, json.getString("message"));
                        }
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ShowToast(OngoingAct.this, NC.getString(R.string.server_error));
                        }
                    });

                }
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();

            }
        }
    }

    public void showodometer() {
        final View view1 = View.inflate(OngoingAct.this, R.layout.odometer_input, null);
        if (myDialog != null && myDialog.isShowing())
            myDialog.cancel();
        myDialog = new Dialog(OngoingAct.this, R.style.NewDialog);
        myDialog.setContentView(view1);
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setCancelable(true);
        myDialog.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(myDialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        myDialog.getWindow().setAttributes(layoutParams);


//
        LinearLayout btn_confirm = myDialog.findViewById(R.id.btn_confirm);

        TextView odameter_heading = myDialog.findViewById(R.id.odameter_heading);
        TextView start_value = myDialog.findViewById(R.id.start_value);
        EditText verifyno1Txt = myDialog.findViewById(R.id.verifyno1Txt);
//        EditText verifyno2Txt = myDialog.findViewById(R.id.verifyno2Txt);
//        EditText verifyno3Txt = myDialog.findViewById(R.id.verifyno3Txt);
//        EditText verifyno4Txt = myDialog.findViewById(R.id.verifyno4Txt);
//        EditText verifyno5Txt = myDialog.findViewById(R.id.verifyno5Txt);
//        EditText verifyno6Txt = myDialog.findViewById(R.id.verifyno6Txt);
        //   EditText verifyno7Txt = myDialog.findViewById(R.id.verifyno7Txt);


        if (SessionSave.getSession("odameter_status", OngoingAct.this).equals("2")) {

            odameter_heading.setText("Start  Reading");
        } else if (SessionSave.getSession("odameter_status", OngoingAct.this).equals("3")) {

            odameter_heading.setText("End Reading");
            start_value.setVisibility(View.VISIBLE);

            start_value.setText("Start reading : " + " "+SessionSave.getSession("acceptReading", OngoingAct.this));
        } else if (SessionSave.getSession("odameter_status", OngoingAct.this).equals("3")) {
            odameter_heading.setText("Accept Reading");
        }
        verifyno1Txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });




        btn_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (verifyno1Txt.getText().toString().equals("")) {
                    Toast.makeText(OngoingAct.this, "Enter first number", Toast.LENGTH_LONG).show();
                } else {
                    myDialog.dismiss();
                    String otpnumber = verifyno1Txt.getText().toString();
                    final String url = "type=new_update_odometer";
                    SessionSave.saveSession("odameter_add_status", "updated", OngoingAct.this);

                    if (SessionSave.getSession("odameter_status", OngoingAct.this).equals("2")) {
                        new updateOdaMeter(url, "2", otpnumber);
                    } else if (SessionSave.getSession("odameter_status", OngoingAct.this).equals("3")) {
                        new updateOdaMeter(url, "3", otpnumber);
                    } else {
                        new updateOdaMeter(url, "1", otpnumber);
                    }


                }


            }
        });

    }


    public void showOtp() {
        final View view1 = View.inflate(OngoingAct.this, R.layout.odometer_otp_input, null);
        if (myOTPDialog != null && myOTPDialog.isShowing())
            myOTPDialog.cancel();
        myOTPDialog = new Dialog(OngoingAct.this, R.style.NewDialog);
        myOTPDialog.setContentView(view1);
        myOTPDialog.setCancelable(false);
        myOTPDialog.setCanceledOnTouchOutside(false);
        myOTPDialog.setCancelable(true);
        myOTPDialog.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(myOTPDialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        myOTPDialog.getWindow().setAttributes(layoutParams);


//
        LinearLayout btn_confirm = myOTPDialog.findViewById(R.id.btn_confirm);

        TextView odameter_heading = myOTPDialog.findViewById(R.id.odameter_heading);
        EditText verifyno1Txt = myOTPDialog.findViewById(R.id.verifyno1Txt);
        EditText verifyno2Txt = myOTPDialog.findViewById(R.id.verifyno2Txt);
        EditText verifyno3Txt = myOTPDialog.findViewById(R.id.verifyno3Txt);
        EditText verifyno4Txt = myOTPDialog.findViewById(R.id.verifyno4Txt);


        verifyno1Txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (s.toString().trim().length() == 1) {
                    verifyno2Txt.requestFocus();
                    verifyno2Txt.setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });


        verifyno2Txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (s.toString().trim().length() == 1) {
                    verifyno3Txt.requestFocus();
                    verifyno3Txt.setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        verifyno3Txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (s.toString().trim().length() == 1) {
                    verifyno4Txt.requestFocus();
                    verifyno4Txt.setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });


        btn_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (verifyno1Txt.getText().toString().equals("")) {
                    Toast.makeText(OngoingAct.this, "Enter first number", Toast.LENGTH_LONG).show();
                } else if (verifyno2Txt.getText().toString().equals("")) {
                    Toast.makeText(OngoingAct.this, "Enter second number", Toast.LENGTH_LONG).show();
                } else if (verifyno3Txt.getText().toString().equals("")) {
                    Toast.makeText(OngoingAct.this, "Enter third number", Toast.LENGTH_LONG).show();

                } else if (verifyno4Txt.getText().toString().equals("")) {
                    Toast.makeText(OngoingAct.this, "Enter fourth number", Toast.LENGTH_LONG).show();

                } else {
                    myOTPDialog.dismiss();
                    String otpnumber = verifyno1Txt.getText().toString() + verifyno2Txt.getText().toString() + verifyno3Txt.getText().toString() + verifyno4Txt.getText().toString();
                    final String url = "type=booking_otp_verify";
                    new updateOTP(url, "3", otpnumber);
                }


            }
        });

    }

    public void startTrip() {
        try {
            TripcancelTxt.setVisibility(View.VISIBLE);
            timer_lay.setVisibility(View.GONE);
            countDownTimer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }


        retryCount = 1;
        //
        //                        nonactiityobj.stopServicefromNonActivity(OngoingAct.this);

        try {


            if (latitude1 != 0.0 && longitude1 != 0.0) {
                JSONObject jstart = new JSONObject();

                jstart.put("driver_id", SessionSave.getSession("Id", OngoingAct.this));

                jstart.put("latitude", latitude1);
                jstart.put("longitude", longitude1);
                jstart.put("status", "A");
                stopLists.get(0).setLat(latitude1);
                stopLists.get(0).setLng(longitude1);
                jstart.put("stops", new JSONArray(new Gson().toJson(stopLists)));
                jstart.put("trip_id", SessionSave.getSession("trip_id", OngoingAct.this));
                jstart.put("driver_id", SessionSave.getSession("Id", OngoingAct.this));
                jstart.put("taxi_id", SessionSave.getSession("taxi_id", OngoingAct.this));
                final String driver_status_update = "type=driver_status_update";
                SessionSave.saveSession("slat", String.valueOf(latitude1), OngoingAct.this);
                SessionSave.saveSession("slng", String.valueOf(longitude1), OngoingAct.this);
                CommonData.last_getlatitude = latitude1;
                CommonData.last_getlongitude = longitude1;
                if (currentAccuracy <= slabAccuracy) {
                    new Onboard(driver_status_update, jstart);
                } else {
                    showLowAccuracyAlert();
                }
            } else {
                JSONObject jstart = new JSONObject();
                jstart.put("driver_id", SessionSave.getSession("Id", OngoingAct.this));
                jstart.put("latitude", "");
                jstart.put("longitude", "");
                jstart.put("status", "A");
                stopLists.get(0).setLat(0.0);
                stopLists.get(0).setLng(0.0);
                jstart.put("stops", new JSONArray(new Gson().toJson(stopLists)));
                jstart.put("trip_id", SessionSave.getSession("trip_id", OngoingAct.this));
                jstart.put("driver_id", SessionSave.getSession("Id", OngoingAct.this));
                jstart.put("taxi_id", SessionSave.getSession("taxi_id", OngoingAct.this));
                final String driver_status_update = "type=driver_status_update";
                if (currentAccuracy <= slabAccuracy) {
                    new Onboard(driver_status_update, jstart);
                } else {
                    showLowAccuracyAlert();
                }
            }

        } catch (Exception e) {

        }

    }


    private class updateOdaMeter implements APIResult {
        updateOdaMeter(final String url, String type, String odometer_number) {
            try {

                JSONObject j = new JSONObject();
                j.put("driver_id", SessionSave.getSession("Id", OngoingAct.this));
                j.put("trip_id", SessionSave.getSession("trip_id", OngoingAct.this));
                j.put("odometer_number", odometer_number);
                j.put("level", type);

                new APIService_Retrofit_JSON(OngoingAct.this, this, j, false).execute(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            try {
                if (isSuccess) {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        {
                            //               SessionSave.saveSession("odameter_status","2",OngoingAct.this);
                            if (SessionSave.getSession("odameter_status", OngoingAct.this).equals("2")) {
                                startTrip();
                            } else if (SessionSave.getSession("odameter_status", OngoingAct.this).equals("3")) {
                                // butt_onboard.performClick();
                                JSONObject mJsonObject = json.getJSONObject("data");
                                String messageValue = "Your trip starting reading is" + " " + mJsonObject.getString("accept_reading") + " " + "and your ending reading is" + " " + mJsonObject.getString("end_reading") + " " + "Overall kms for this trip is" + " " + mJsonObject.getString("overall_kms") + ".";
                                SessionSave.saveSession("end_odameter", mJsonObject.getString("overall_kms"), OngoingAct.this);

                                CompleteTripOdameter(messageValue);
                            }
                        }
                    }

                }
            } catch (final Exception e) {
                e.printStackTrace();

            }
        }
    }


    private class updateOTP implements APIResult {
        updateOTP(final String url, String type, String odometer_number) {
            try {

                JSONObject j = new JSONObject();

                j.put("trip_id", SessionSave.getSession("trip_id", OngoingAct.this));
                j.put("otp", odometer_number);


                new APIService_Retrofit_JSON(OngoingAct.this, this, j, false).execute(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            try {
                if (isSuccess) {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        {
                            SessionSave.saveSession("otp_enter", "no", OngoingAct.this);
                            SessionSave.saveSession("odameter_status", "2", OngoingAct.this);
//showodometer();
                            if (booking_Type.equals("2") || booking_Type.equals("3")) {
                                showodometer();
                            } else {
                                startTrip();
                            }

                            //  startTrip();
                        }
                    } else {
                        showAlertView(json.getString("message"));
                    }

                }
            } catch (final Exception e) {
                e.printStackTrace();

            }
        }
    }

    private void showAlertView(String message) {
        dialogotp = Utils.alert_view_dialog(OngoingAct.this,
                "",
                message,
                NC.getString(R.string.ok),
                "",
                false, (dialog, which) -> dialog.dismiss(), null, "");
    }

    public void completeTripApi() {
        try {
            MainActivity.mMyStatus.setOnstatus("Complete");
//            stopService(new Intent(OngoingAct.this, WaitingTimerRun.class));
//            myHandler.removeCallbacks(r);
            float h = 0.0f;
            waitingTime = CommonData.getDateForWaitingTime(SessionSave.getWaitingTime(OngoingAct.this));
            if (waitingTime.equals(""))
                waitingTime = "00:00:00";
            String waitNoArabic = FontHelper.convertfromArabic(waitingTime);
            Systems.out.println("Errror in okkkk" + waitNoArabic + "---" + waitingTime);
            String[] split = waitNoArabic.split(":");
            int hr = Integer.parseInt(split[0]);
            float min = Integer.parseInt(split[1]);
            float sec = Float.parseFloat(split[2]);
            Systems.out.println("Hour:" + hr + "min:" + min + "sec:" + sec);
            min = min / 60;
            sec = sec / 3600;
            waitingHr = hr + min + sec;
            MainActivity.mMyStatus.setDriverWaitingHr(Float.toString(waitingHr));
            SessionSave.saveSession("waitingHr", Float.toString(waitingHr), OngoingAct.this);
            final String completeUrl = "type=complete_trip";
            new CompleteTrip(completeUrl, latitude1, longitude1);
        } catch (Exception e) {
            e.printStackTrace();
            Systems.out.println("Errror in okkkk" + e);
            // TODO: handle exception
        }
    }

    private void CompleteTripOdameter(String msg) {
        View view = View.inflate(this, R.layout.custom_msg_popup_yn, null);
        Dialog mDialog = new Dialog(this, R.style.dialogwinddow_trans);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.show();
        Window window = mDialog.getWindow();
        if (window != null) {
            window.setLayout(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            );
        }

        AppCompatTextView mail = mDialog.findViewById(R.id.msg_txt);
        mail.setText(msg);

        LinearLayout yesBtn = mDialog.findViewById(R.id.yesbtn);
        LinearLayout noBtn = mDialog.findViewById(R.id.nobtn);

        AppCompatTextView txtyes = mDialog.findViewById(R.id.txtyes);
        AppCompatTextView txtno = mDialog.findViewById(R.id.txtno);

        txtyes.setText("Complete Trip");
        txtno.setText(NC.getString(R.string.cancel));

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mDialog.dismiss();
                loadServices();
                //completeTripApi();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SessionSave.saveSession("odameter_status", "3", OngoingAct.this);
                showodometer();
                mDialog.dismiss();
            }
        });
    }



    private class showServiceList implements APIResult {

        public showServiceList(final String url, JSONObject data) {
            try {
                if (isOnline()) {
                    new APIService_Retrofit_JSON(
                            OngoingAct.this,
                            this,
                            data,
                            false
                    ).execute(url);
                } else {
                    dialog1 = Utils.alert_view(
                            OngoingAct.this,
                            NC.getResources().getString(R.string.message),
                            NC.getResources().getString(R.string.check_net_connection),
                            NC.getResources().getString(R.string.ok),
                            "",
                            true,
                            OngoingAct.this,
                            "4"
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {

            if (!isSuccess || result == null || result.isEmpty()) {
                runOnUiThread(() ->
                        ShowToast(OngoingAct.this, NC.getString(R.string.server_error))
                );
                return;
            }

            try {
                JSONObject json = new JSONObject(result);
                int status = json.optInt("status", 0);

                // ✅ SUCCESS CASE
                if (status == 1) {

                    JSONArray dataArray = json.optJSONArray("data");

                    if (dataArray != null && dataArray.length() > 0) {

                        allServices.clear();

                        for (int i = 0; i < dataArray.length(); i++) {

                            JSONObject serviceJson = dataArray.optJSONObject(i);
                            if (serviceJson == null) continue;

                            int id = serviceJson.optInt("_id", 0);
                            double amount = serviceJson.optDouble("service_amount", 0.0);
                            String serviceStatus = serviceJson.optString("status", "");
                            String serviceType = serviceJson.optString("service_type", "");

                            allServices.add(
                                    new ServiceItem(id, amount, serviceStatus, serviceType)
                            );
                        }

                        runOnUiThread(() -> showServiceSelectionDialog());

                    } else {
                        // ✅ status = 1 but no services
//                        runOnUiThread(() ->
//                                ShowToast(OngoingAct.this, "No service data available")
//                        );
                        completeTripApi();
                    }

                } else {
                    // ✅ status = 0 → No service charge → continue trip
                    Log.d("showServiceList", "No service charge found, continuing trip");
                    completeTripApi();
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        ShowToast(OngoingAct.this, "Invalid server response")
                );
            }
        }
    }

    private void showServiceSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_service_selection, null);
        builder.setView(dialogView);

        Spinner spinnerServiceType = dialogView.findViewById(R.id.spinner_service_type);
        RecyclerView recyclerViewSelectedServices = dialogView.findViewById(R.id.recycler_view_selected_services);
        Button btnAddService = dialogView.findViewById(R.id.btn_add_service);
        Button btnDone = dialogView.findViewById(R.id.btn_done);
        tvTotalAmount = dialogView.findViewById(R.id.tv_total_amount);

        // Spinner Setup
        ArrayAdapter<ServiceItem> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, allServices);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServiceType.setAdapter(spinnerAdapter);

        final ServiceItem[] selectedServiceFromSpinner = {null}; // To hold the currently selected item in spinner
        spinnerServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedServiceFromSpinner[0] = (ServiceItem) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedServiceFromSpinner[0] = null;
            }
        });

        // RecyclerView Setup
        recyclerViewSelectedServices.setLayoutManager(new LinearLayoutManager(this));
        selectedServiceAdapter = new SelectedServiceAdapter(selectedServices, item -> {
            // Remove item logic
            selectedServices.remove(item);
            selectedServiceAdapter.notifyDataSetChanged();
            updateTotalAmount();
            Toast.makeText(OngoingAct.this, item.getService_type() + " removed.", Toast.LENGTH_SHORT).show();
        });
        recyclerViewSelectedServices.setAdapter(selectedServiceAdapter);

        // Add Service Button
        btnAddService.setOnClickListener(v -> {
            if (selectedServiceFromSpinner[0] != null) {
                // Check if the service is already added
                boolean alreadyAdded = false;
                for (ServiceItem item : selectedServices) {
                    if (item.get_id() == selectedServiceFromSpinner[0].get_id()) {
                        alreadyAdded = true;
                        break;
                    }
                }

                if (!alreadyAdded) {
                    selectedServices.add(selectedServiceFromSpinner[0]);
                    selectedServiceAdapter.notifyDataSetChanged();
                    updateTotalAmount();
                    Toast.makeText(this, selectedServiceFromSpinner[0].getService_type() + " added.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, selectedServiceFromSpinner[0].getService_type() + " is already added.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please select a service to add.", Toast.LENGTH_SHORT).show();
            }
        });

        // Update initial total amount
        updateTotalAmount();

        AlertDialog dialog = builder.create();

        // Done Button
        btnDone.setOnClickListener(v -> {
            // Here you can do something with the final selectedServices list and total amount
          //  Toast.makeText(this, "Dialog Closed. Total Services: " + selectedServices.size() + ", Total Amount: " + String.format("%.2f", calculateTotalAmount()), Toast.LENGTH_LONG).show();
            dialog.dismiss();


             servicesJsonArray = new JSONArray();
            for (ServiceItem item : selectedServices) {
                try {
                    JSONObject serviceObject = new JSONObject();
                    serviceObject.put("_id", item.get_id());
                    serviceObject.put("service_type", item.getService_type());
                    serviceObject.put("service_amount", item.getService_amount());
                    servicesJsonArray.put(serviceObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("ServiceDialog", "Error creating JSON object for service: " + item.getService_type());
                }
            }
            System.out.println("servicesJsonArray"+ " "+servicesJsonArray);
            completeTripApi();
        });


        dialog.show();
    }

    private void updateTotalAmount() {
        double total = calculateTotalAmount();
        tvTotalAmount.setText(String.format("%.2f", total));
    }

    private double calculateTotalAmount() {
        double total = 0;
        for (ServiceItem item : selectedServices) {
            total += item.getService_amount();
        }
        return total;
    }

    private class getOdometer implements APIResult {


        public getOdometer(final String url, JSONObject data) {
            try {
                if (isOnline()) {
                    new APIService_Retrofit_JSON(OngoingAct.this, this, data, false).execute(url);
                } else {
                    dialog1 = Utils.alert_view(OngoingAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, OngoingAct.this, "4");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            try {
                if (isSuccess) {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        JSONObject dataObject = json.getJSONObject("data");
                        String acceptReading = dataObject.getString("accept_reading");

                        // Use acceptReading as needed
                        System.out.println("Accept Reading: " + acceptReading);
                        taxitypeTxt.setVisibility(View.VISIBLE);
                        SessionSave.saveSession("acceptReading",acceptReading,OngoingAct.this);

                        taxitypeTxt.setText("Start reading : " + " "+acceptReading);
                    }
                } else {

                }
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();

            }
        }
    }

    private class updateOdaMeterUpdate implements APIResult {
        updateOdaMeterUpdate(final String url, String type, String odometer_number) {
            try {

                JSONObject j = new JSONObject();
                j.put("driver_id", SessionSave.getSession("Id", OngoingAct.this));
                j.put("trip_id", SessionSave.getSession("trip_id", OngoingAct.this));
                j.put("odometer_number", odometer_number);
                j.put("level", type);

                new APIService_Retrofit_JSON(OngoingAct.this, this, j, false).execute(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            try {
                if (isSuccess) {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        {
                            JSONObject j = new JSONObject();
                            try {
                                j.put("trip_id", SessionSave.getSession("trip_id", OngoingAct.this));
                                final String Url = "type=get_odometer";
                                new getOdometer(Url, j);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }

                }
            } catch (final Exception e) {
                e.printStackTrace();

            }
        }
    }
}


