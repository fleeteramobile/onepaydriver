package com.onepaytaxi.driver;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.onepaytaxi.driver.adapter.SelectedServiceAdapter;
import com.onepaytaxi.driver.data.CommonData;
import com.onepaytaxi.driver.data.apiData.ApiRequestData;
import com.onepaytaxi.driver.data.apiData.StreetCompleteResponse;
import com.onepaytaxi.driver.data.apiData.TripDetailResponse;
import com.onepaytaxi.driver.homepage.HomePageActivity;
import com.onepaytaxi.driver.interfaces.APIResult;
import com.onepaytaxi.driver.interfaces.ClickInterface;
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON;
import com.onepaytaxi.driver.service.CoreClient;
import com.onepaytaxi.driver.service.LocationUpdate;
import com.onepaytaxi.driver.service.RetrofitCallbackClass;
import com.onepaytaxi.driver.service.ServiceGenerator;
import com.onepaytaxi.driver.utils.CToast;
import com.onepaytaxi.driver.utils.FontHelper;
import com.onepaytaxi.driver.utils.LocationDb;
import com.onepaytaxi.driver.utils.NC;
import com.onepaytaxi.driver.utils.NetworkStatus;
import com.onepaytaxi.driver.utils.ServiceItem;
import com.onepaytaxi.driver.utils.SessionSave;
import com.onepaytaxi.driver.utils.Systems;
import com.onepaytaxi.driver.utils.Utils;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This class is used to calculate the trip fare.
 */

public class FarecalcAct extends MainActivity implements ClickInterface, PaymentResultWithDataListener {
    public static AppCompatActivity mFlagger;
    public static FarecalcAct activity;
    private final int REQUEST_READ_PHONE_STATE = 292;
    double os_distance, os_duration, os_fare, promo_percentage = 0.0;
    Intent details;
    CardView radiowalletButton,radiocashButton;
    CardView radiocardButton, radiouncardButton;
    View vid_discount;
    LocationDb objLocationDb;
    TextView eve_fare;
    DecimalFormat df = new DecimalFormat("####0.00");
    double tax = 0.0;
    // Class members declarations.
    private String message;
    private String f_tripid;
    private double os_plan_fare, os_plan_distance;
    private double os_plan_duration;
    private double os_additional_fare_per_distance, os_additional_fare_per_hour;
    private String f_distance;
    private String f_metric;
    private String f_totalfare;
    private String f_nightfareapplicable;
    private String f_nightfare;
    private String f_eveningfare_applicable = "0";
    private String f_eveningfare;
    private String f_pickup = "", drop_location = "";
    private String f_waitingtime;
    private String f_waitingcost;
    private String f_taxamount;
    private String f_tripfare;
    private String f_farediscount = "";
    private final String promotax = "";
    private final String promoamt = "";
    private String f_paymodid = "";
    private String p_dis = "";
    private String f_walletamt = "";
    private String f_payamt = "";
    private double m_distance;
    private double m_tripfare;
    private double m_totalfare;
    private double m_taxamount;
    private double m_waitingcost;
    private double m_walletamt;
    private double m_payamt;
    private double f_fare;
    private double f_tips;
    private double f_total;
    private EditText farecalTxt;
    private EditText tipsTxt;
    private TextView HeadTitle;
    private TextView tv_startTime;
    private TextView tv_dropTime;
    private TextView tv_tripFare;
    private TextView tv_total_disatnce,pickup_time,drop_time;
    private EditText et_tripFare;
    private EditText et_total_disatnce;
    private EditText et_time_hour;
    private EditText et_time_mins;
    private LinearLayout layoutNormal;
    private LinearLayout layoutOutstation;
    private TextView totalamountTxt;
    private TextView actdistanceTxt;
    private TextView metricTxt, os_metricTxt;
    private TextView promopercentTxt;
    private TextView b_farecalCurrency;
    private TextView b_tipsCurrency;
    private TextView b_pickuplocation;
    private TextView b_droplocation;
    private TextView b_total_amt_curency;
    private TextView b_waitingcost, b_tax, b_discount, b_roundtrip, v_trip_fare;
    private TextView remarks;
    private TextView walletamountTxt,totalamt;
    private TextView amountpayTxt;
    private TextView idwaitingcost;
    private Dialog mDialog;
    private LinearLayout lay_fare;
    private LinearLayout walletlay;
    private RelativeLayout paylay,cancel_lay,city_limit_km_lay,city_limit_fare_lay,convenience_fee_lay;
    private String cmpTax = "";
    private LinearLayout promoLayout, tax_lay;
    private TextView txtCmp;

  //  CardView slideImg;
    private String f_minutes_traveled;
    private String f_minutes_fare;
    private String Cvv;
    private String base_fare = "";
    private boolean fromStreetPickUp;
    private String fare_calculation_type = "3";
    private LinearLayout distance_lay, minutes_lay, waiting_lay;
    private TextView minutes_value;
    private TextView walletamountCurrency;
    private TextView amountpayCurrency,    hills_fee_txt_currency,totalamtCurrency,cancel_fee,cancelcurrency,convenience_fee_txt_currency,convenience_fee_fare_txt;
    private LinearLayout eve_fare_lay;
    private String[] os_hr_min = new String[2];


    private double os_tax,trip_fare;
    private String trip_type = "1";
    private double os_minute_fare;
    private String promo_type;
    private ScrollView scrollview;
    private ViewGroup payment_layout;
    private ViewGroup rootLay;
    private boolean keyboardListenersAttached = false;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = null;
    private TextView night_fare,payment;
    private LinearLayout night_fare_lay;
    private LinearLayout totalamountTxt_lay;
    private AppCompatButton btn_emergency;
    private Dialog dialog1;
    private String distanceFare = "";
    private ImageView fabInfo,img_wallet,img_cash,img_card,img_new_card;
    private int partial_payment_status=0;
    private String partial_payment_amount="";
    private String paymethod = "cash";
    private int min_distance_status;
    private String fare_per_minute, waiting_fare_minutes, trip_minutes, subtotal, new_distance_fare, new_base_fare, distance_fare_metric, amt, promocode_fare,
            tax_fare, nightfare, eveningfare, cancellation_fee,drop_time_text,pickup_time_text,trip_duration;

    private String existing_wallet_amount = "";
    Double amount_used_from_wallet = 0.0;
    Double amount_tobe_paid = 0.0;
    private String pending_cancel_amount = "";
    private String amount_received="0";

    private String isCorporate = "";
    private String tollFareTxt = "";
    private String parkingFareTxt = "";
    private String bookingFareTxt = "";
    private String os_driver_beta = "";
    private Double os_addtinal_km = 0.0;

    private String hillsfaretxt = "",roundtrip = "",service_charges= "";

    private List<ServiceItem> allServices;
    private List<ServiceItem> selectedServices;
    private SelectedServiceAdapter selectedServiceAdapter;
    private TextView tvTotalAmount;
    private JSONArray servicesJsonArray;

    private TextView approxamountTxt,approxamtCurrency,couponamountTxt,couponcurrency,distance,city_limit_km_txt,city_limit_fare_txt,city_limit_fare_txt_currency;
    private TextView parkingCurrency,parkingTxt,tollCurrency,tollTxt;

    private RadioButton wallet_radio,cash_radio;

    // Set the layout to activity.
    @Override
    public int setLayout() {

        setLocale();
        return R.layout.farecalc_lay2;
    }

    public String getStringFromTime(long mins) {
        String s = "0 " + NC.getString(R.string.hrs) + " 00" + NC.getString(R.string.mins) + " ";
        if (mins > 0) {
            s = mins / 60 + " " + NC.getString(R.string.hrs) + " " + mins % 60 + " " + NC.getString(R.string.mins) + " ";
        }
        return s;
    }

    public void calculateAndUpdateFare() {
        double discount_amount = 0.0;
        os_fare = os_plan_fare;

        if (!et_time_hour.getText().toString().isEmpty() && !et_time_mins.getText().toString().isEmpty() && !et_total_disatnce.getText().toString().isEmpty() && !p_dis.isEmpty()) {
            os_duration = Double.parseDouble(et_time_hour.getText().toString()) + (Double.parseDouble(et_time_mins.getText().toString()) / 60);
            if (os_duration > os_plan_duration) {
                os_minute_fare = ((os_duration - (os_plan_duration)) * os_additional_fare_per_hour);
                os_fare += os_minute_fare;
            }
            os_distance = Double.parseDouble(et_total_disatnce.getText().toString());
            if (os_distance > os_plan_distance) {
                os_fare += ((os_distance - os_plan_distance) * os_additional_fare_per_distance);
            }

            if (promo_percentage > 0 && !promo_type.equals("1")) {
                discount_amount = os_fare * promo_percentage / 100;
                os_fare -= discount_amount;
                // b_discount.setText(String.format(Locale.UK, "%.2d", (os_fare * promo_percentage / 100)+" "));
                b_discount.setText(SessionSave.getSession("site_currency", FarecalcAct.this) + " " + df.format(discount_amount));
            } else if (promo_type.equals("1")) {
                discount_amount = Double.parseDouble(f_farediscount);
                os_fare -= discount_amount;
                Systems.out.println("os_fareee__*" + os_fare + "___" + discount_amount);
                b_discount.setText(SessionSave.getSession("site_currency", FarecalcAct.this) + " " + df.format(discount_amount));
            }
            if (os_tax != 0) {
                tax = os_fare * os_tax / 100;
                b_tax.setText(SessionSave.getSession("site_currency", FarecalcAct.this) + " " + df.format(((tax))));
            }


            totalamountTxt.setText(String.format(Locale.ENGLISH, df.format((os_fare + tax))));
            amountpayTxt.setText(String.format(String.valueOf(trip_fare)));
            System.out.println("total_amt_"+ " "+"1" + " "+String.format(Locale.ENGLISH, df.format(((os_fare + tax) - m_walletamt))));
            totalamt.setText(f_payamt);
            et_tripFare.setText(FontHelper.convertfromArabic((df.format(Double.parseDouble(FontHelper.convertfromArabic(String.valueOf(os_fare))) + discount_amount))));
        }
    }

    protected void attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return;
        }

        rootLay.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        keyboardListenersAttached = true;
    }

    // Initialize the views on layout
    @Override
    public void Initialize() {

        View root = findViewById(R.id.id_farelay);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );
            return insets;
        });


        btn_emergency = findViewById(R.id.btn_emergency);
      /*  if (SessionSave.getSession(CommonData.SOS_ENABLED, this, false)) {
            btn_emergency.setVisibility(View.VISIBLE);
        }*/

        btn_emergency.setOnClickListener(view -> {
            final View view1 = View.inflate(FarecalcAct.this, R.layout.emergency_alert, null);
            Dialog emergency_dialog = new Dialog(FarecalcAct.this, R.style.dialogwinddow);
            emergency_dialog.setContentView(view1);
            emergency_dialog.setCancelable(true);
            emergency_dialog.show();
            final Button button_success = emergency_dialog.findViewById(R.id.button_success);
            final Button button_failure = emergency_dialog.findViewById(R.id.button_failure);
            button_success.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    emergency_dialog.dismiss();
                    startSOSService();
                }
            });
            button_failure.setOnClickListener(view2 -> emergency_dialog.dismiss());
        });
        CommonData.sContext = this;
        CommonData.current_act = "FarecalcAct";
        CommonData.mActivitylist.add(this);
        activity = this;
        mFlagger = this;


        objLocationDb = new LocationDb(FarecalcAct.this);
        FontHelper.applyFont(this, findViewById(R.id.id_farelay));
        fabInfo = findViewById(R.id.fabInfo);
        img_wallet = findViewById(R.id.img_wallet);
        img_cash = findViewById(R.id.img_cash);
        img_card= findViewById(R.id.img_card);
        img_new_card= findViewById(R.id.img_new_card);
        rootLay = findViewById(R.id.id_farelay);
        payment_layout = findViewById(R.id.payment_layout);
        // outstation initialization
        tv_startTime = findViewById(R.id.tv_startTime);
        tv_dropTime = findViewById(R.id.tv_dropTime);
        tv_tripFare = findViewById(R.id.tv_trip_fare);
        tv_total_disatnce = findViewById(R.id.tv_total_distance);
        distance = findViewById(R.id.distance);
        city_limit_km_txt = findViewById(R.id.city_limit_km_txt);
        city_limit_fare_txt = findViewById(R.id.city_limit_fare_txt);
        city_limit_fare_txt_currency = findViewById(R.id.city_limit_fare_txt_currency);

                pickup_time = findViewById(R.id.pickup_time);
        drop_time = findViewById(R.id.drop_time);



        et_time_hour = findViewById(R.id.ed_time_hour);
        et_time_mins = findViewById(R.id.ed_time_mins);
        et_tripFare = findViewById(R.id.ed_trip_fare);
        et_total_disatnce = findViewById(R.id.ed_total_distance);
        layoutNormal = findViewById(R.id.normal_fare_layout);
        cancel_lay = findViewById(R.id.cancel_lay);
        city_limit_km_lay = findViewById(R.id.city_limit_km_lay);
        city_limit_fare_lay = findViewById(R.id.city_limit_fare_lay);
        convenience_fee_lay = findViewById(R.id.convenience_fee_lay);
        layoutOutstation = findViewById(R.id.outstation_fare_layout);

        approxamountTxt=findViewById(R.id.approxamountTxt);
        approxamtCurrency=findViewById(R.id.approxamtCurrency);

        parkingCurrency=findViewById(R.id.parkingCurrency);
        parkingTxt=findViewById(R.id.parkingTxt);
        tollCurrency=findViewById(R.id.tollCurrency);
        tollTxt=findViewById(R.id.tollTxt);


        couponamountTxt=findViewById(R.id.couponamountTxt);
        couponcurrency=findViewById(R.id.couponcurrency);

        b_pickuplocation = findViewById(R.id.pickuplocTxt);
        b_droplocation = findViewById(R.id.droplocTxt);
        b_farecalCurrency = findViewById(R.id.farecalCurrency);
        actdistanceTxt = findViewById(R.id.actdistanceTxt);
        metricTxt = findViewById(R.id.metricTxt);
        os_metricTxt = findViewById(R.id.os_metricTxt);
        b_tipsCurrency = findViewById(R.id.tipsCurrency);
        farecalTxt = findViewById(R.id.farecalTxt);
        tipsTxt = findViewById(R.id.tipsTxt);
        HeadTitle = findViewById(R.id.headerTxt);
        distance_lay = findViewById(R.id.distance_lay);
        minutes_lay = findViewById(R.id.minutes_lay);
        waiting_lay = findViewById(R.id.waiting_lay);
        minutes_value = findViewById(R.id.min_value);
        eve_fare_lay = findViewById(R.id.eve_fare_lay);
        night_fare_lay = findViewById(R.id.night_fare_lay);
        totalamountTxt_lay = findViewById(R.id.totalamountTxt_lay);
        scrollview = findViewById(R.id.scrollview);
        eve_fare = findViewById(R.id.eve_fare);
        night_fare = findViewById(R.id.night_fare);
        payment = findViewById(R.id.payment);
        totalamountTxt = findViewById(R.id.totalamountTxt);
        promopercentTxt = findViewById(R.id.promopercentage);
        walletamountTxt = findViewById(R.id.walletamountTxt);
        totalamt = findViewById(R.id.totalamt);
        walletamountCurrency = findViewById(R.id.walletamountCurrency);
        amountpayCurrency = findViewById(R.id.amountpayCurrency);
        hills_fee_txt_currency = findViewById(R.id.hills_fee_txt_currency);
        totalamtCurrency =  findViewById(R.id.totalamtCurrency);
        cancel_fee =  findViewById(R.id.cancel_fee);
        cancelcurrency =  findViewById(R.id.cancelcurrency);
        convenience_fee_txt_currency =  findViewById(R.id.convenience_fee_txt_currency);
        convenience_fee_fare_txt =  findViewById(R.id.convenience_fee_fare_txt);
        amountpayTxt = findViewById(R.id.amountpayTxt);
        tax_lay = findViewById(R.id.tax_lay);
        txtCmp = findViewById(R.id.txtcmpTax);
       // slideImg = findViewById(R.id.slideImg);
        walletlay = findViewById(R.id.walletlay);
        paylay = findViewById(R.id.paylay);
       // slideImg.setVisibility(View.VISIBLE);
        promoLayout = findViewById(R.id.discountlayout);
        lay_fare = findViewById(R.id.lay_fare);
        remarks = findViewById(R.id.remarks);
        b_total_amt_curency = findViewById(R.id.toatalamtCurrency);
        b_waitingcost = findViewById(R.id.waitingcost);
        idwaitingcost = findViewById(R.id.idwaitingcost);
        b_tax = findViewById(R.id.tax);
        b_discount = findViewById(R.id.discount);
        b_roundtrip = findViewById(R.id.roundtrip);
        v_trip_fare = findViewById(R.id.v_trip_fare);
        vid_discount = findViewById(R.id.vid_discount);
        radiocashButton = findViewById(R.id.rbtn_cash);
        radiowalletButton = findViewById(R.id.rbtn_wall);
        radiocardButton = findViewById(R.id.rbtn_card);
        radiouncardButton = findViewById(R.id.rbtn_uncard);

        allServices = new ArrayList<>();
        selectedServices = new ArrayList<>();
        radiocashButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                paymethod = "cash";
                img_cash.setImageResource(R.drawable.ic_new_radio_selected);

                img_wallet.setImageResource(R.drawable.ic_new_radio_un_selected);
                img_card.setImageResource(R.drawable.ic_new_radio_un_selected);

            }
        });
        radiowalletButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                paymethod = "wallet";

                img_cash.setImageResource(R.drawable.ic_new_radio_un_selected);
                img_wallet.setImageResource(R.drawable.ic_new_radio_selected);
                img_card.setImageResource(R.drawable.ic_new_radio_un_selected);

            }
        });


        radiocardButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                paymethod = "card";

                img_cash.setImageResource(R.drawable.ic_new_radio_un_selected);
                img_wallet.setImageResource(R.drawable.ic_new_radio_un_selected);
                img_card.setImageResource(R.drawable.ic_new_radio_selected);



            }
        });



        img_new_card.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                radiouncardButton.performClick();
            }
        });
        HeadTitle.setText(NC.getResources().getString(R.string.fare_calculator));
        details = getIntent();


        keyboardLayoutListener = () -> {
            // navigation bar height
            int navigationBarHeight = 0;
            int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                navigationBarHeight = getResources().getDimensionPixelSize(resourceId);
            }

            // status bar height
            int statusBarHeight = 0;
            resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }

            // display window size for the app layout
            Rect rect = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

            // screen height - (user app height + status + nav) ..... if non-zero, then there is a soft keyboard
            int keyboardHeight = rootLay.getRootView().getHeight() - (statusBarHeight + navigationBarHeight + rect.height());
            Systems.out.println("scrollllview overall" + payment_layout.getHeight() + "__" + keyboardHeight + "___" + rootLay.getRootView().getHeight() + "__" + statusBarHeight + "____" + navigationBarHeight + "__" + rect.height() + "__" + scrollview.getHeight() + "__" + rootLay.getHeight());
            if (keyboardHeight <= 0) {
                // onHideKeyboard();
                Systems.out.println("scrollllview hide");
                scrollview.fullScroll(View.FOCUS_DOWN);
            } else {
                Systems.out.println("scrollllview show" + scrollview.getHeight());
                scrollview.smoothScrollTo(0, rootLay.getRootView().getHeight() - (payment_layout.getHeight() + keyboardHeight));
                //   onShowKeyboard(keyboardHeight);
            }
        };

        attachKeyboardListeners();

//        slideImg.setOnClickListener(v -> {
//            Intent intent = new Intent(FarecalcAct.this, HomePageActivity.class);
//            startActivity(intent);
//            finish();
//        });

        if(details.getStringExtra("corporate") != null ){
            isCorporate = details.getStringExtra("corporate");
            if(isCorporate.equals("1")){
                message = details.getStringExtra("message");
                setFareCalculatorScreen();
            }else{
                try {

//            // If Directly comes from end trip page(OngoingAct)
                    if (details.getStringExtra("from") != null && details.getStringExtra("from").equalsIgnoreCase("direct")) {
                        message = details.getStringExtra("message");

                        // This for update the fare calculator page with API result.
                        setFareCalculatorScreen();
                        if (details.getBooleanExtra("from_split", false))
                            fromStreetPickUp = true;
                    }
                    // If comes from Pending bookings(JobsAct).
                    else {
                        loadServices();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{
            try {

//            // If Directly comes from end trip page(OngoingAct)
                if (details.getStringExtra("from") != null && details.getStringExtra("from").equalsIgnoreCase("direct")) {
                    message = details.getStringExtra("message");

                    // This for update the fare calculator page with API result.
                    setFareCalculatorScreen();
                    if (details.getBooleanExtra("from_split", false))
                        fromStreetPickUp = true;
                }
                // If comes from Pending bookings(JobsAct).
                else {
//                    String lat = details.getStringExtra("lat");
//                    String lon = details.getStringExtra("lon");
//                    String distance = details.getStringExtra("distance");
//                    String waitingHr = details.getStringExtra("waitingHr");
//                    String drop_location = details.getStringExtra("drop_location");
//                    String stopList = details.getStringExtra("stopList");
//                    String url = "type=complete_trip";
//                    new CompleteTrip(url, lat, lon, distance, waitingHr, drop_location, stopList);
                    loadServices();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void loadServices() {
        JSONObject j = new JSONObject();
      String new_trip_type = String.valueOf(SessionSave.getSession("type_book", FarecalcAct.this).equals("2"));
        try {
            if (new_trip_type.equals("2") || new_trip_type.equals("3")) {
                j.put("booking_type", new_trip_type);
            }
            else
            {
                j.put("booking_type", "1");

            }
            j.put("taxi_model","1");

            final String services_url = "type=service_charge_list";
            new showServiceList(services_url, j);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        NetworkStatus.isOnline(FarecalcAct.this);

        et_time_hour.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                try {
                    if (!hasFocus) {
                        if (TextUtils.isEmpty(et_time_hour.getText().toString())) {
                            et_time_hour.setText("00");
                            os_hr_min[0] = et_time_hour.getText().toString();
                            calculateAndUpdateFare();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        et_time_mins.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                try {
                    if (!hasFocus) {
                        if (TextUtils.isEmpty(et_time_mins.getText().toString())) {


                            et_time_mins.setText("00");
                            os_hr_min[1] = et_time_mins.getText().toString();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        et_total_disatnce.setOnFocusChangeListener((view, hasFocus) -> {

            try {
                if (trip_type.equals("3")) {
                    if (!hasFocus) {
                        String text = et_total_disatnce.getText().toString();
                        if (TextUtils.isEmpty(text)) {
                            et_total_disatnce.setText("0.00");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        et_time_hour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (trip_type.equals("3")) {
                        if (!et_time_hour.getText().toString().isEmpty())
                            os_hr_min[0] = et_time_hour.getText().toString();
                        if (!et_time_hour.getText().toString().isEmpty()) {
                            calculateAndUpdateFare();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_time_mins.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (trip_type.equals("3")) {
                        if (!et_time_mins.getText().toString().isEmpty())
                            os_hr_min[1] = et_time_mins.getText().toString();

                        if (!et_time_mins.getText().toString().isEmpty()) {
                            if (Integer.parseInt(os_hr_min[1]) > 59) {
                                os_hr_min[1] = "59";
                                et_time_mins.setText(os_hr_min[1]);
                            }
                            calculateAndUpdateFare();
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_total_disatnce.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (trip_type.equals("3")) {
                        if (!et_total_disatnce.getText().toString().isEmpty()) {
                            try {
                                os_distance = Double.parseDouble(et_total_disatnce.getText().toString());
                                calculateAndUpdateFare();
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        et_tripFare.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (trip_type.equals("3")) {
                        if (!et_tripFare.getText().toString().isEmpty()) {
                            double discount_amount = 0;
                            double faree = Double.parseDouble(FontHelper.convertfromArabic(et_tripFare.getText().toString()));

                            if (promo_percentage > 0 && !promo_type.equals("1")) {
                                discount_amount = faree * promo_percentage / 100;
                                b_discount.setText(SessionSave.getSession("site_currency", FarecalcAct.this) + " " + df.format(discount_amount));
                            } else if (promo_type.equals("1")) {
                                discount_amount = Double.parseDouble(f_farediscount);
                            }


                            os_fare = faree - discount_amount;
                            if (os_tax != 0) {
                                tax = os_fare * os_tax / 100;
                                b_tax.setText(SessionSave.getSession("site_currency", FarecalcAct.this) + " " + df.format(((tax))));
                            }
                            totalamountTxt.setText(String.valueOf(os_fare + tax));
                            amountpayTxt.setText(String.valueOf(trip_fare));
                            System.out.println("total_amt_"+ " "+"2" + " "+String.valueOf(os_fare + tax - m_walletamt));
                            totalamt.setText(f_payamt);
                            //                    calculateAndUpdateFare();}
                        } else {
                            totalamountTxt.setText("0.00");
                            amountpayTxt.setText("0.00");
                            totalamt.setText("0.00");

                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
        });

        et_tripFare.setOnFocusChangeListener((view, hasFocus) -> {
            try {
                if (trip_type.equals("3")) {
                    if (!hasFocus) {
                        String text = et_tripFare.getText().toString();
                        if (TextUtils.isEmpty(text)) {
                            et_tripFare.setText("0.00");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        fabInfo.setOnClickListener(v -> {
            Bundle mBundle = new Bundle();
            mBundle.putBoolean("isFromFareScreen", true);
            mBundle.putString("trip_id", f_tripid);
            mBundle.putString("tripDetailResponse", makeInfo());
            Intent intent = new Intent(FarecalcAct.this, TripHistoryAct.class);
            intent.putExtras(mBundle);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (dialog1 != null)
            Utils.closeDialog(dialog1);
        super.onDestroy();

        if (keyboardListenersAttached) {
            rootLay.getViewTreeObserver().removeOnGlobalLayoutListener(keyboardLayoutListener);
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        Utils.closeDialog(mDialog);
        super.onStop();
    }

    /**
     * This for update the fare calculator page with API result.
     */
    @SuppressLint("SdCardPath")
    private void setFareCalculatorScreen() {
        // Need to uncommand
        if (details != null) {
            try {
                JSONObject obj = new JSONObject(message);
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
                if (trip_type.equals("3")) {
                    layoutNormal.setVisibility(View.GONE);
                    layoutOutstation.setVisibility(View.GONE);
                    waiting_lay.setVisibility(View.GONE);
                  //  fabInfo.setVisibility(View.GONE);
                } else {
                    layoutNormal.setVisibility(View.GONE);
                    layoutOutstation.setVisibility(View.GONE);
                    waiting_lay.setVisibility(View.GONE);
                 //   fabInfo.setVisibility(View.GONE);
                    if (!trip_type.equals("2"))
                        setNormalTripFareScreen();
                }

                os_distance = json.getDouble("distance");
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
                et_time_hour.setText(os_hr_min[0]);
                et_time_mins.setText(os_hr_min[1]);
                et_total_disatnce.setText(String.valueOf(os_distance));


                f_metric = json.getString("metric");
                distance.setText(os_distance + " "+f_metric );
                if(trip_type.equals("1"))
                {
                    if(!json.getString("city_limit_traved_km").equals("0.0"))
                    {
                        String cityLimitTraveledKm = json.getString("city_limit_traved_km");
                        double cityLimitKmDouble = Double.parseDouble(cityLimitTraveledKm);
                        String roundedCityLimitKm = String.format("%.2f", cityLimitKmDouble);
                        city_limit_km_txt.setText(roundedCityLimitKm + " " + f_metric);
                        city_limit_km_lay.setVisibility(View.VISIBLE);



                        String cityLimitTraveledFare = json.getString("additional_citylimit_fare");
                        double cityLimitFareDouble = Double.parseDouble(cityLimitTraveledFare);
                        String roundedCityLimitFare = String.format("%.2f", cityLimitFareDouble);
                        city_limit_fare_txt.setText(roundedCityLimitFare);
                        city_limit_fare_txt_currency.setText(SessionSave.getSession("site_currency", FarecalcAct.this));
                        city_limit_km_lay.setVisibility(View.GONE);
                        city_limit_fare_lay.setVisibility(View.GONE);
                    }
                    else {
                        city_limit_km_lay.setVisibility(View.GONE);
                        city_limit_fare_lay.setVisibility(View.GONE);
                    }
                }
                else {
                    city_limit_km_lay.setVisibility(View.GONE);
                    city_limit_fare_lay.setVisibility(View.GONE);
                }




                //     city_limit_km_txt.setText(json.getDouble("city_limit_traved_km") + " "+f_metric );

                tv_total_disatnce.setText(NC.getResources().getString(R.string.total_distance) + " (" + f_metric.toLowerCase() + ")");
                tv_tripFare.setText(NC.getResources().getString(R.string.trip_fare) + "(" + SessionSave.getSession("site_currency", FarecalcAct.this) + ")");
                et_tripFare.setText(FontHelper.convertfromArabic(json.getString("trip_fare")));
                tv_startTime.setText(json.getString("trip_start_time"));
                tv_dropTime.setText(json.getString("trip_end_time"));
                f_tripid = json.getString("trip_id");
                f_distance = json.getString("distance");
                f_totalfare = json.getString("total_fare");
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
                 trip_fare = os_fare + Double.parseDouble(f_waitingcost);

                approxamountTxt.setText( json.getString("approx_fare"));
                couponamountTxt.setText( json.getString("promodiscount_amount"));

                double distance = Double.parseDouble(json.getString("distance"));
                double osPlanDistance = Double.parseDouble(json.getString("os_plan_distance"));

               double total_add_distance =  distance - osPlanDistance;
                System.out.println("total_add_distance"+ " "+total_add_distance);


if(trip_type.equals("2") || trip_type.equals("3") )
{
    if (osPlanDistance > distance) {
        os_addtinal_km = 0.0;
    } else {
        // Additional checks if distance exceeds osPlanDistance
        double additionalDistance = calculateAdditionalDistance(distance, osPlanDistance);
        os_addtinal_km = additionalDistance;
    }
}
else {
    os_addtinal_km = 0.0;
}




                tollFareTxt = json.getString("toll_amount");
                parkingFareTxt = json.getString("parking_amount");
                bookingFareTxt = json.getString("booking_fare");
                hillsfaretxt = json.getString("hills_fare");
                service_charges = json.getString("service_fare");
                tollCurrency.setText(SessionSave.getSession("site_currency", FarecalcAct.this));
                parkingCurrency.setText(SessionSave.getSession("site_currency", FarecalcAct.this));


                if(json.has("booking_fare"))
                {
                    convenience_fee_lay.setVisibility(View.VISIBLE);
                    convenience_fee_txt_currency.setText(SessionSave.getSession("site_currency", FarecalcAct.this));
                    convenience_fee_fare_txt.setText(json.getString("booking_fare"));
                }
                else
                {
                    convenience_fee_lay.setVisibility(View.GONE);
                }




                if(json.has("os_driver_beta"))
                {

                    os_driver_beta = json.getString("os_driver_beta");

                }
                else
                {
                    os_driver_beta = "0";

                }

                if(json.has("toll_amount"))
                {


                    if (!json.getString("toll_amount").equals("0"))
                    {

                        tollTxt.setText(json.getString("toll_amount"));
                    }

                }

                if(json.has("parking_amount"))
                {

                    if (!json.getString("parking_amount").equals("0"))
                    {

                        parkingTxt.setText(json.getString("parking_amount"));

                    }

                }
                if (json.has("roundtrip"))
                {
                    roundtrip = json.getString("roundtrip");
                }
                else {
                    roundtrip = "";
                }

                fare_per_minute = json.getString("fare_per_minute");
                waiting_fare_minutes = json.getString("waiting_fare_minutes");
                trip_minutes = json.getString("trip_minutes");
                min_distance_status = json.getInt("min_distance_status");
                subtotal = json.getString("subtotal");

                if (trip_type.equals("2") || trip_type.equals("3"))
                {
                    new_distance_fare = String.valueOf(os_additional_fare_per_distance);

                }
                else {
                    new_distance_fare = json.getString("new_distance_fare");

                }
                new_base_fare = json.getString("new_base_fare");
                distance_fare_metric = json.getString("distance_fare_metric");
                amt = json.getString("amt");
                promocode_fare = json.getString("promocode_fare");
                tax_fare = json.getString("tax_fare");
                nightfare = json.getString("nightfare");
                eveningfare = json.getString("eveningfare");

                pickup_time_text = json.getString("pickup_time_text");
                drop_time_text = json.getString("drop_time_text");
//                trip_duration = json.getString("trip_duration");

                if (json.has("pending_cancel_amount"))
                {

                    if (!json.getString("pending_cancel_amount").equals("0"))
                    {
                        cancel_lay.setVisibility(View.VISIBLE);
                        cancellation_fee = json.getString("pending_cancel_amount");
                        cancel_fee.setText(cancellation_fee);
                        cancelcurrency.setText(SessionSave.getSession("site_currency", FarecalcAct.this));

                    }
                    else {
                        cancel_lay.setVisibility(View.GONE);
                    }

                }
                else {
                    cancel_lay.setVisibility(View.GONE);
                }


//                if (json.has("pending_cancel_amount"))
//                    pending_cancel_amount = json.getString("pending_cancel_amount");

                if (f_eveningfare_applicable.equalsIgnoreCase("1") && (trip_type.equals("2") || trip_type.equals("3"))) {
                    eve_fare.setText(f_eveningfare);
                    eve_fare_lay.setVisibility(View.VISIBLE);
                } else
                    eve_fare_lay.setVisibility(View.GONE);

                if (f_nightfareapplicable.equalsIgnoreCase("1") && (trip_type.equals("2") || trip_type.equals("3"))) {
                    night_fare.setText(f_nightfare);
                    night_fare_lay.setVisibility(View.VISIBLE);
                } else
                    night_fare_lay.setVisibility(View.GONE);

                try {

                    fare_calculation_type = json.getString("fare_calculation_type");
                    if (fare_calculation_type.trim().equals("1"))
                        minutes_lay.setVisibility(View.GONE);
                    else if (fare_calculation_type.trim().equals("2"))
                        distance_lay.setVisibility(View.GONE);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                if (f_waitingtime.equals("0")) {
                    idwaitingcost.setText(NC.getResources().getString(R.string.waiting_cost) + "(" + "00:00" + ")");
                } else {
                    idwaitingcost.setText(NC.getResources().getString(R.string.waiting_cost) + "(" + f_waitingtime + ")");
                }
                if (!cmpTax.trim().equals("0"))
                    txtCmp.setText(NC.getResources().getString(R.string.tax) + cmpTax + NC.getResources().getString(R.string.tax_percent));
                else
                    tax_lay.setVisibility(View.GONE);
                farecalTxt.setText(f_totalfare);
                v_trip_fare.setText(SessionSave.getSession("site_currency", FarecalcAct.this) + " " + f_tripfare);
                p_dis = String.valueOf(promo_percentage);

                Systems.out.println("promo_dis" + p_dis);
                if (!p_dis.trim().equals("")) {
                    if (!promo_type.equals("1") && !p_dis.equals("0")) {
                        if (promo_type.equals("2"))
                            promopercentTxt.setText(NC.getResources().getString(R.string.discount) + "(" + p_dis + NC.getResources().getString(R.string.tax_percent));
                        else
                            promopercentTxt.setText(NC.getResources().getString(R.string.discount));
                        if (Double.parseDouble(f_farediscount) > 0.0) {
                            Systems.out.println("aaaaaaaaaaa1" + f_farediscount + "_____________" + Double.parseDouble(f_farediscount));
                            b_discount.setText(SessionSave.getSession("site_currency", FarecalcAct.this) + " " + f_farediscount);
                        } else {
                            Systems.out.println("aaaaaaaaaaa2");
                            promoLayout.setVisibility(View.GONE);
                        }
                    } else {
                        if (Double.parseDouble(json.getString("promodiscount_amount")) >= 0.0) {
                            b_discount.setText(SessionSave.getSession("site_currency", FarecalcAct.this) + " " + json.getString("promodiscount_amount"));
                        } else {
                            promoLayout.setVisibility(View.GONE);
                        }
                    }

                    vid_discount.setVisibility(View.GONE);

                } else {
                    promoLayout.setVisibility(View.GONE);
                    vid_discount.setVisibility(View.GONE);
                }
                if (promoamt.equals("0")) {
                    promoLayout.setVisibility(View.GONE);
                }
                metricTxt.setText(f_metric.toLowerCase());
                actdistanceTxt.setText(f_distance);
                minutes_value.setText(getStringFromTime(Long.parseLong(f_minutes_traveled)));

                b_pickuplocation.setText(f_pickup);
                b_droplocation.setText(drop_location);

                pickup_time.setText(pickup_time_text);
                drop_time.setText(drop_time_text);
                b_total_amt_curency.setText(SessionSave.getSession("site_currency", FarecalcAct.this) + " ");
                if (!f_waitingcost.equals("0")) {
                    b_waitingcost.setText(SessionSave.getSession("site_currency", FarecalcAct.this) + " " + f_waitingcost);
                } else {
                    waiting_lay.setVisibility(View.GONE);
                }
                b_tax.setText(SessionSave.getSession("site_currency", FarecalcAct.this) + " " + f_taxamount);
                b_roundtrip.setText(json.getString("roundtrip"));
                tipsTxt.setHint("0");
                remarks.setText(objLocationDb.getdistance(f_tripid));
                if (SessionSave.getSession("site_currency", FarecalcAct.this) != null) {
                    b_farecalCurrency.setText(SessionSave.getSession("site_currency", FarecalcAct.this) + " ");
                    b_tipsCurrency.setText(SessionSave.getSession("site_currency", FarecalcAct.this) + " ");
                    approxamtCurrency.setText(SessionSave.getSession("site_currency", FarecalcAct.this) + " ");
                    couponcurrency.setText(SessionSave.getSession("site_currency", FarecalcAct.this) + " ");
                }


                f_fare = m_totalfare;
                if (tipsTxt.length() != 0) {
                    f_tips = Double.parseDouble(Uri.decode(tipsTxt.getText().toString()));
                }
                f_total = f_fare + f_tips;
                if (f_total != 0.0) {
                    totalamountTxt.setText(String.format(Locale.UK, "%.2f", f_total));
                } else {
                    totalamountTxt_lay.setVisibility(View.GONE);
                }
                Systems.out.println("gateway_details" + json.getString("gateway_details"));
                JSONArray ary = new JSONArray(json.getString("gateway_details"));
                // the following code for handle the payment mode dynamically.
                int length = ary.length();
                Systems.out.println("ary lenght" + length);
                walletamountCurrency.setText(SessionSave.getSession("site_currency", FarecalcAct.this) + " ");
                amountpayCurrency.setText(SessionSave.getSession("site_currency", FarecalcAct.this) + " ");
                hills_fee_txt_currency.setText(SessionSave.getSession("site_currency", FarecalcAct.this) + " ");
                totalamtCurrency.setText(SessionSave.getSession("site_currency", FarecalcAct.this) + " ");
                if (m_walletamt > 0) {
                    walletlay.setVisibility(View.VISIBLE);
                    walletamountTxt.setText(f_walletamt);
                    paylay.setVisibility(View.VISIBLE);
                    amountpayTxt.setText(String.valueOf(trip_fare));
                    System.out.println("total_amt_"+ " "+"3" + " "+f_payamt);
                    totalamt.setText(f_payamt);
                }
                amountpayTxt.setText(String.valueOf(trip_fare));
                System.out.println("total_amt_"+ " "+"4" + " "+f_payamt);
                totalamt.setText(f_payamt);
                for (int i = 0; i < length; i++) {
                    String paymentModeDefault = ary.getJSONObject(i).getString("pay_mod_default");
                    String paymentMode_Id = ary.getJSONObject(i).getString("pay_mod_id");
                    if (paymentMode_Id.equalsIgnoreCase("5")) {
                        radiowalletButton.setVisibility(View.GONE);
                        if (paymentModeDefault.equals("1")) {
                           // radiowalletButton.setTextColor(Color.DKGRAY);
                        }
                    } else if (paymentMode_Id.equalsIgnoreCase("1")) {
                        radiocashButton.setVisibility(View.VISIBLE);

                    } else if (paymentMode_Id.equalsIgnoreCase("2")) {
                       radiocardButton.setVisibility(View.VISIBLE);

                    } else if (paymentMode_Id.equalsIgnoreCase("3")) {

                        radiouncardButton.setVisibility(View.VISIBLE);

                    } else if (paymentMode_Id.equalsIgnoreCase("4")) {

                    }
                }
                if (trip_type.equals("3")) {
                    calculateAndUpdateFare();
                }
            } catch (JSONException e) {
                Systems.out.println("errorToCovert " + e);
                e.printStackTrace();
            }
        }

        payment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paymethod.equalsIgnoreCase("cash"))
                {
                    if (fromStreetPickUp) {
                        dialog1 = Utils.alert_view_dialog(FarecalcAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.confir_complete_payment), NC.getResources().getString(R.string.ok), NC.getResources().getString(R.string.cancell), false, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                completeStreetTrip();
                            }
                        }, (dialog, which) -> dialog.dismiss(), "");
                    } else {

                        if (farecalTxt.length() != 0) {
                            f_fare = Double.parseDouble(((FontHelper.convertfromArabic(f_totalfare)).replace(",", ".")));
                        }
                        if (tipsTxt.length() != 0) {
                            f_tips = Double.parseDouble((FontHelper.convertfromArabic((tipsTxt.getText().toString())).replace(",", ".")));
                        }
                        f_total = f_fare + f_tips;
                        f_paymodid = "1";
                        //confirmCompleteTrip(FarecalcAct.this);

                        amount_received=amountpayTxt.getText().toString();
                        confirmCompleteTrip(FarecalcAct.this);
                      //  payment_handling_dialog();
                    }
                }
                else if (paymethod.equalsIgnoreCase("wallet"))
                {
                    if (farecalTxt.length() != 0) {
                        f_fare = Double.parseDouble(FontHelper.convertfromArabic(farecalTxt.getText().toString()));
                    }
                    if (tipsTxt.length() != 0) {
                        f_tips = Double.parseDouble(FontHelper.convertfromArabic(tipsTxt.getText().toString()));
                    }
                    f_total = f_fare + f_tips;
                    totalamountTxt.setText(String.format(Locale.UK, "%.2f", f_total));
                    f_paymodid = "5";

                    if (Double.parseDouble(f_payamt) <=Double.parseDouble(existing_wallet_amount)) {
                        amount_used_from_wallet = Double.parseDouble(f_payamt);
                        SessionSave.saveSession(CommonData.AMOUNT_USED_FROM_WALLET, String.valueOf(amount_used_from_wallet), FarecalcAct.this);
                        confirmCompleteTrip(FarecalcAct.this);
                    } else {
                        amount_tobe_paid = Double.parseDouble(f_payamt) - Double.parseDouble(existing_wallet_amount);
                        amount_used_from_wallet = Double.parseDouble(f_payamt);
                        SessionSave.saveSession(CommonData.AMOUNT_USED_FROM_WALLET, String.valueOf(amount_used_from_wallet), FarecalcAct.this);
                        dialog1 = Utils.alert_view_dialog(FarecalcAct.this, NC.getResources().getString(R.string.message), "Customer doesn't have enough money in wallet. " +
                                "So "+SessionSave.getSession("site_currency", FarecalcAct.this) + " "+Double.parseDouble(existing_wallet_amount)+" will be deducted from wallet, balance  " +SessionSave.getSession("site_currency", FarecalcAct.this) + " "+amount_tobe_paid+" you can collect through cash", NC.getResources().getString(R.string.ok), NC.getResources().getString(R.string.cancell), false, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                partial_payment_status=1;
                                partial_payment_amount= String.valueOf(amount_tobe_paid);

                                confirmCompleteTrip(FarecalcAct.this);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, "");
                    }
                }
                else if (paymethod.equalsIgnoreCase("card"))
                {
                    PaymentApiCallForGetOrderId(totalamt.getText().toString());
                    f_paymodid = "2";

//                    if (farecalTxt.length() != 0) {
//                        f_fare = Double.parseDouble(FontHelper.convertfromArabic(farecalTxt.getText().toString()));
//                    }
//                    if (tipsTxt.length() != 0) {
//                        f_tips = Double.parseDouble(FontHelper.convertfromArabic(tipsTxt.getText().toString()));
//                    }
//                    f_total = f_fare + f_tips;
//                    totalamountTxt.setText(String.format(Locale.UK, "%.2f", f_total));
//                    f_paymodid = "2";
//
//
//                    if (Double.parseDouble(f_payamt) <=Double.parseDouble(existing_wallet_amount)) {
//                        amount_used_from_wallet = Double.parseDouble(f_payamt);
//                        SessionSave.saveSession(CommonData.AMOUNT_USED_FROM_WALLET, String.valueOf(amount_used_from_wallet), FarecalcAct.this);
//                        confirmCompleteTrip(FarecalcAct.this);
//                    } else {
//                        amount_tobe_paid = Double.parseDouble(f_payamt) - Double.parseDouble(existing_wallet_amount);
//                        amount_used_from_wallet = Double.parseDouble(f_payamt);
//                        SessionSave.saveSession(CommonData.AMOUNT_USED_FROM_WALLET, String.valueOf(amount_used_from_wallet), FarecalcAct.this);
//                        dialog1 = Utils.alert_view_dialog(FarecalcAct.this, NC.getResources().getString(R.string.message), "Customer doesn't have enough money in wallet. " +
//                                "So "+SessionSave.getSession("site_currency", FarecalcAct.this) + " "+Double.parseDouble(existing_wallet_amount)+" will be deducted from wallet, balance  " +SessionSave.getSession("site_currency", FarecalcAct.this) + " "+amount_tobe_paid+" you can collect through cash", NC.getResources().getString(R.string.ok), NC.getResources().getString(R.string.cancell), false, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//
//                                partial_payment_status=1;
//                                partial_payment_amount= String.valueOf(amount_tobe_paid);
//
//                                confirmCompleteTrip(FarecalcAct.this);
//                            }
//                        }, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }, "");
//                    }
                }
            }
        });
        // The following process will done while select the payment mode as cash.
//        radiocashButton.setOnClickListener(v -> {
//            if (fromStreetPickUp) {
//                dialog1 = Utils.alert_view_dialog(FarecalcAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.confir_complete_payment), NC.getResources().getString(R.string.ok), NC.getResources().getString(R.string.cancell), false, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        completeStreetTrip();
//                    }
//                }, (dialog, which) -> dialog.dismiss(), "");
//            } else {
//              //  radiocardButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.credit_card_unfocus, 0, 0);
//             //   radiocashButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cash_unfocus, 0, 0);
//               // radiouncardButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.newcard_unfocus, 0, 0);
//
//            //    radiocashButton.setTextColor(Color.DKGRAY);
//              //  radiocardButton.setTextColor(Color.LTGRAY);
//              //  radiouncardButton.setTextColor(Color.LTGRAY);
//                if (farecalTxt.length() != 0) {
//                    f_fare = Double.parseDouble(((FontHelper.convertfromArabic(f_totalfare)).replace(",", ".")));
//                }
//                if (tipsTxt.length() != 0) {
//                    f_tips = Double.parseDouble((FontHelper.convertfromArabic((tipsTxt.getText().toString())).replace(",", ".")));
//                }
//                f_total = f_fare + f_tips;
//                f_paymodid = "1";
//                //confirmCompleteTrip(FarecalcAct.this);
//                payment_handling_dialog();
//            }
//        });
        // The following process will done while select the payment mode as wallet.
//        radiowalletButton.setOnClickListener(v -> {
//
//         //   radiowalletButton.setTextColor(Color.DKGRAY);
//          //  radiocashButton.setTextColor(Color.LTGRAY);
//          //  radiocardButton.setTextColor(Color.LTGRAY);
//           // radiouncardButton.setTextColor(Color.LTGRAY);
////            if (farecalTxt.length() != 0) {
////                f_fare = Double.parseDouble(FontHelper.convertfromArabic(farecalTxt.getText().toString()));
////            }
////            if (tipsTxt.length() != 0) {
////                f_tips = Double.parseDouble(FontHelper.convertfromArabic(tipsTxt.getText().toString()));
////            }
////            f_total = f_fare + f_tips;
////            totalamountTxt.setText("" + String.format(Locale.UK, "%.2f", f_total));
////            f_paymodid = "5";
////
////            if (Double.parseDouble(f_payamt) <=Double.parseDouble(existing_wallet_amount)) {
////                amount_used_from_wallet = Double.parseDouble(f_payamt);
////                SessionSave.saveSession(CommonData.AMOUNT_USED_FROM_WALLET, "" + amount_used_from_wallet, FarecalcAct.this);
////                confirmCompleteTrip(FarecalcAct.this);
////            } else {
////                amount_tobe_paid = Double.parseDouble(f_payamt) - Double.parseDouble(existing_wallet_amount);
////                amount_used_from_wallet = Double.parseDouble(f_payamt);
////                SessionSave.saveSession(CommonData.AMOUNT_USED_FROM_WALLET, "" + amount_used_from_wallet, FarecalcAct.this);
////                dialog1 = Utils.alert_view_dialog(FarecalcAct.this, NC.getResources().getString(R.string.message), "Customer doesn't have enough money in wallet. " +
////                        "So "+SessionSave.getSession("site_currency", FarecalcAct.this) + " "+Double.parseDouble(existing_wallet_amount)+" will be deducted from wallet, balance  " +SessionSave.getSession("site_currency", FarecalcAct.this) + " "+amount_tobe_paid+" you can collect through cash", NC.getResources().getString(R.string.ok), NC.getResources().getString(R.string.cancell), false, new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
//////                        Intent in = new Intent(FarecalcAct.this, WebviewAct.class);
//////                        in.putExtra("type", "1");
//////                        in.putExtra(CommonData.IS_FROM_EARNINGS, false);
//////                        startActivity(in);
////
////                        partial_payment_status=1;
////                        partial_payment_amount=""+amount_tobe_paid;
////
////                        confirmCompleteTrip(FarecalcAct.this);
////                    }
////                }, new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                        dialog.dismiss();
////                    }
////                }, "");
////            }
//        });
        // The following process will done while select the payment mode as card. And it shows the dialog to get the CVV number.
//        radiocardButton.setOnClickListener(v -> {
//
//           // radiocashButton.setTextColor(Color.LTGRAY);
//           // radiocardButton.setTextColor(Color.DKGRAY);
//          //  radiouncardButton.setTextColor(Color.LTGRAY);
//
//          //  radiocardButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.credit_card_unfocus, 0, 0);
//           // radiocashButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cash_unfocus, 0, 0);
//          //  radiouncardButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.newcard_unfocus, 0, 0);
////            if (farecalTxt.length() != 0) {
////                f_fare = Double.parseDouble(FontHelper.convertfromArabic(farecalTxt.getText().toString()));
////            }
////            if (tipsTxt.length() != 0) {
////                f_tips = Double.parseDouble(FontHelper.convertfromArabic(tipsTxt.getText().toString()));
////            }
////            f_total = f_fare + f_tips;
////            f_paymodid = "2";
////            confirmCompleteTrip(FarecalcAct.this);
//        });
        // The following process will done while select the payment mode as uncard.
//        radiouncardButton.setOnClickListener(v -> {
//
//           // radiocashButton.setTextColor(Color.LTGRAY);
//           // radiocardButton.setTextColor(Color.LTGRAY);
//           // radiouncardButton.setTextColor(Color.DKGRAY);
//           // radiocardButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.credit_card_unfocus, 0, 0);
//          //  radiocashButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cash_unfocus, 0, 0);
//           // radiouncardButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.newcard_unfocus, 0, 0);
//
//            Double fare = Double.parseDouble(et_tripFare.getText().toString());
//
//            if (trip_type.equals("3") && fare <= 0) {
//                CToast.ShowToast(FarecalcAct.this, NC.getString(R.string.endter_valid_fare));
//            } else {
//                if (f_total > 0) {
//
//                    dialog1 = Utils.alert_view_dialog(FarecalcAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.confir_complete_payment), NC.getResources().getString(R.string.ok), NC.getResources().getString(R.string.cancell), false, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//
//                       //     callnewcardurl();
//                        //  cancelLoading();
//
//
//                            Bundle bun = new Bundle();
//                            bun.putString("info", "Uncard");
//                            bun.putString("message", message);
//                            if (trip_type.equals("3")) {
//                                if (!SessionSave.getSession("Lang", FarecalcAct.this).equals("en")) {
//                                    bun.putString("f_fare", amountpayTxt.getText().toString());
//                                    bun.putString("f_tips", Double.toString(f_tips));
//                                    bun.putString("f_total", amountpayTxt.getText().toString());
//                                } else {
//                                    bun.putString("f_fare", FontHelper.convertfromArabic(amountpayTxt.getText().toString()));
//                                    bun.putString("f_tips", FontHelper.convertfromArabic(Double.toString(f_tips)));
//                                    bun.putString("f_total", FontHelper.convertfromArabic(amountpayTxt.getText().toString()));
//                                }
//                            } else {
//                                if (!SessionSave.getSession("Lang", FarecalcAct.this).equals("en")) {
//                                    bun.putString("f_fare", FontHelper.convertfromArabic(f_payamt));
//                                    bun.putString("f_tips", FontHelper.convertfromArabic(Double.toString(f_tips)));
//                                    bun.putString("f_total", FontHelper.convertfromArabic(Double.toString(f_total)));
//                                } else {
//                                    bun.putString("f_fare", f_payamt);
//                                    bun.putString("f_tips", Double.toString(f_tips));
//                                    bun.putString("f_total", Double.toString(f_total));
//                                }
//                            }
//                            payintent.putExtras(bun);
//                            startActivity(payintent);
//                        }
//                    }, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }, "");
//
//                }
//            }
//        });

    }

    public static double calculateAdditionalDistance(double distance, double osPlanDistance) {
        return distance - osPlanDistance;
    }


    private void PaymentApiCallForGetOrderId(String amount) {

        try {
            JSONObject j = new JSONObject();
            j.put("amount",amount);

            final String url = "type=get_order_id";
            new GetOrderid(url, j,amount);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }


    private class GetOrderid implements APIResult {
        String addMoneys ;
        private GetOrderid(final String url, JSONObject data, String addMoney) {
            addMoneys = addMoney;
            new APIService_Retrofit_JSON(FarecalcAct.this, this, data, false).execute(url);

        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            // TODO Auto-generated method stub
            if (isSuccess) {
                try {
                    final JSONObject json = new JSONObject(result);

                    System.out.println("getOrder_id"+ " "+result);
                    if (json.getInt("status") == 1) {
                        paymentFlow(json.getString("order_id"),addMoneys);
                    }


                } catch (final JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
         CToast.ShowToast(FarecalcAct.this, "Connection Error");
            }
        }
    }


    private void payment_handling_dialog() {

        if (alertDialog != null)
            if (alertDialog.isShowing())
                alertDialog.dismiss();
        final View view = View.inflate(FarecalcAct.this, R.layout.payment_alert_view, null);

        alertDialog = new Dialog(FarecalcAct.this, R.style.NewDialog);
        alertDialog.setContentView(view);
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        FontHelper.applyFont(FarecalcAct.this, alertDialog.findViewById(R.id.alert_id));

        alertDialog.show();
        final TextView title_text = alertDialog.findViewById(R.id.title_text);
        final TextView message_text = alertDialog.findViewById(R.id.message_text);


        final EditText amt_Edt = alertDialog.findViewById(R.id.amt_Edt);
        final LinearLayout button_success = alertDialog.findViewById(R.id.button_success);
        final Button button_failure = alertDialog.findViewById(R.id.button_failure);
        final TextView tv_actual_fare = alertDialog.findViewById(R.id.tv_actual_fare);
        final ImageView close_fare_dialog = alertDialog.findViewById(R.id.close_fare_dialog);

        close_fare_dialog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        tv_actual_fare.setText(SessionSave.getSession("site_currency", FarecalcAct.this) + " "+amountpayTxt.getText().toString());

        amt_Edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        // amtTxt.setText(getResources().getString(R.string.f_currsymbol) +" "+numberFormat.format(Double.parseDouble(f_payamt)));

/*
        if (SessionSave.getSession("Lang", FarecalcAct.this).equalsIgnoreCase("en")) {
            amtTxt.setText(" " + numberFormat.format(Double.parseDouble(f_payamt)) + " " + SessionSave.getSession("site_currency", FarecalcAct.this));

        } else {
            amtTxt.setText(" " + FontHelper.convertfromArabic(numberFormat.format(Double.parseDouble(f_payamt))).replace("،", ",").replace("٬", ",") + " " + SessionSave.getSession("site_currency", FarecalcAct.this));

        }*/

        button_failure.setVisibility(View.GONE);
        //title_text.setText("Payment Return By");
        // message_text.setText(message);




        button_success.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                // TODO Auto-generated method stub


                if (amt_Edt.getText().toString().equalsIgnoreCase("")) {
                    CToast.ShowToast(FarecalcAct.this, NC.getString(R.string.enter_collected_amount));
                }else{

                    double trips_amt=0;
                    double receive_amt=0;

                    trips_amt=Double.parseDouble(amountpayTxt.getText().toString());
                    receive_amt=Double.parseDouble(amt_Edt.getText().toString());

                    if (trips_amt<=receive_amt) {

                        alertDialog.dismiss();
                    }else{
                        CToast.ShowToast(FarecalcAct.this, NC.getString(R.string.endter_valid_amount));

                    }
                }




            }
        });

    }

    private void setNormalTripFareScreen() {
        layoutNormal.setVisibility(View.GONE);
        paylay.setVisibility(View.VISIBLE);
        promoLayout.setVisibility(View.GONE);
        tax_lay.setVisibility(View.GONE);
        totalamountTxt_lay.setVisibility(View.GONE);
  //      fabInfo.setVisibility(View.GONE);
    }

    private void confirmCompleteTrip(final AppCompatActivity mContext) {
        double fare = 0;
        try {
            if (!et_tripFare.getText().toString().trim().equals(""))
                fare = Double.parseDouble(et_tripFare.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (trip_type.equals("3") && fare <= 0) {
            CToast.ShowToast(mContext, NC.getString(R.string.endter_valid_fare));
        } else {
            dialog1 = Utils.alert_view_dialog(mContext, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.confir_complete_payment), NC.getResources().getString(R.string.ok), NC.getResources().getString(R.string.cancell), false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (NetworkStatus.isOnline(mContext)) {
                        dialog.dismiss();
                        if (f_paymodid.equals("2")) {
                            callurl();
                            //paymentFlow(String.valueOf(f_payamt));
                        }
                        else {
                            callurl();
                        }

                    } else {
                        CToast.ShowToast(mContext, NC.getResources().getString(R.string.check_net_connection));
                    }
                }
            }, (dialog, which) -> dialog.dismiss(), "");
        }
    }

    /**
     * Common API for fareupdate the following method for arrange the inputs and calls the API.
     */
    private void callurl() {

        String url = "type=tripfare_update";
        try {
            JSONObject j = new JSONObject();

            if (trip_type.equals("3")) {
                j.put("os_distance", os_distance);
                j.put("os_actual_amount", String.valueOf(f_total));
                j.put("os_trip_fare", df.format((os_fare)));
                j.put("os_promodiscount_amount", b_discount.getText().toString());
                j.put("os_minutes_traveled", (os_duration * 60));
                j.put("os_minutes_fare", os_minute_fare);
                j.put("os_driver_beta", os_driver_beta);
            }
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

            j.put("tips", tipsTxt.getText().toString());
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
            j.put("pay_mod_id", f_paymodid);
            j.put("passenger_discount", p_dis);
            j.put("minutes_traveled", f_minutes_traveled);
            j.put("minutes_fare", f_minutes_fare);
            j.put("fare_calculation_type", fare_calculation_type);
            j.put("model_fare_type", SessionSave.getSession("model_fare_type", FarecalcAct.this));
            j.put("pending_cancel_amount", pending_cancel_amount);
            j.put("collected_amount", amount_received);
            j.put("partial_payment_status", partial_payment_status);
            j.put("partial_payment_amount", partial_payment_amount);
            j.put("toll_amount", tollFareTxt);
            j.put("parking_amount", parkingFareTxt);
            j.put("booking_fare", bookingFareTxt);
            j.put("service_fare", service_charges);
            j.put("hills_fare", hillsfaretxt);


            if(f_paymodid.equals("2"))
            {
                j.put("payment_type", "30");
                j.put("order_id", SessionSave.getSession("razepay_orderId", FarecalcAct.this));


            }
            new FareUpdate(url, j);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }


    private void callnewcardurl() {

        String url = "type=tripfare_update";
        try {
            JSONObject j = new JSONObject();

            if (trip_type.equals("3")) {
                j.put("os_distance", os_distance);
                j.put("os_actual_amount", amountpayTxt.getText().toString());
                j.put("os_trip_fare", df.format((os_fare)));
                j.put("os_promodiscount_amount", b_discount.getText().toString());
                j.put("os_minutes_traveled", (os_duration * 60));
                j.put("os_minutes_fare", os_minute_fare);
            }
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

            j.put("tips", tipsTxt.getText().toString());
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
            j.put("pay_mod_id", 3);
            j.put("passenger_discount", p_dis);
            j.put("minutes_traveled", f_minutes_traveled);
            j.put("minutes_fare", f_minutes_fare);
            j.put("fare_calculation_type", fare_calculation_type);
            j.put("model_fare_type", SessionSave.getSession("model_fare_type", FarecalcAct.this));
            j.put("pending_cancel_amount", pending_cancel_amount);
            j.put("collected_amount", amount_received);
            j.put("partial_payment_status", partial_payment_status);
            j.put("partial_payment_amount", partial_payment_amount);


            new FareUpdate(url, j);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * completeStreetTrip API response parsing.
     */

    private void completeStreetTrip() {
//        CoreClient client = new ServiceGenerator(FarecalcAct.this).createService(CoreClient.class);
        CoreClient client = MyApplication.getInstance().getApiManagerWithEncryptBaseUrl();

        ApiRequestData.StreetPickComplete request = new ApiRequestData.StreetPickComplete();
        request.pay_mod_id = "1";
        request.trip_fare = f_tripfare;

        request.eveningfare_applicable = f_eveningfare_applicable;
        request.eveningfare = f_eveningfare;
        request.waiting_cost = f_waitingcost;
        request.fare = String.valueOf(f_fare);
        request.minutes_traveled = f_minutes_traveled;
        request.remarks = "";
        request.actual_amount = String.valueOf(f_total);
        request.trip_id = f_tripid;
        request.distance = f_distance;
        request.base_fare = base_fare;
        request.company_tax = cmpTax;
        request.actual_distance = MainActivity.mMyStatus.getdistance();
        request.tax_amount = f_taxamount;
        request.minutes_fare = f_minutes_fare;
        request.nightfare = f_nightfare;
        request.tips = tipsTxt.getText().toString();
        request.nightfare_applicable = f_nightfareapplicable;
        request.waiting_time = f_waitingtime;

        if (NetworkStatus.isOnline(this)) {
            Call<StreetCompleteResponse> response = client.completeStreetPickUpdate(ServiceGenerator.COMPANY_KEY, request, "en");
            showDialog();
            response.enqueue(new RetrofitCallbackClass<>(FarecalcAct.this, new Callback<StreetCompleteResponse>() {
                @Override
                public void onResponse(Call<StreetCompleteResponse> call, Response<StreetCompleteResponse> response) {
                    closeDialog();
                    if (response.isSuccessful()) {
                        StreetCompleteResponse data = response.body();
                        if (data != null) {
                            String msg = data.message;
                            if (data.status.trim().equals("1")) {
                                SessionSave.saveSession("travel_status", "", FarecalcAct.this);
                                SessionSave.saveSession("trip_id", "", FarecalcAct.this);
                                SessionSave.saveSession("status", "F", FarecalcAct.this);
                                MainActivity.mMyStatus.setdistance("");
                                SessionSave.saveSession("street_completed", "", FarecalcAct.this);
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
                                LocationUpdate.sTimer = "00:00:00";
                                LocationUpdate.finalTime = 0L;
                                LocationUpdate.timeInMillies = 0L;
                                SessionSave.saveSession("waitingHr", "", FarecalcAct.this);
                                CommonData.travel_km = 0;
                                SessionSave.setGoogleDistance(0f, FarecalcAct.this);
                                SessionSave.setDistance(0f, FarecalcAct.this);
                                SessionSave.saveGoogleWaypoints(null, null, "", 0.0, "", FarecalcAct.this);
                                SessionSave.saveWaypoints(null, null, "", 0.0, "", FarecalcAct.this);
                                Intent jobintent = new Intent(FarecalcAct.this, JobdoneAct.class);
                                Bundle bun = new Bundle();
                                Gson gson = new GsonBuilder().create();
                                String result = gson.toJson(data);
                                bun.putString("message", result);
                                jobintent.putExtras(bun);
                                startActivity(jobintent);
                                finish();


                            } else {
                                CToast.ShowToast(FarecalcAct.this, msg);
                            }
                        } else {
                            CToast.ShowToast(FarecalcAct.this, NC.getString(R.string.server_error));
                        }

                    } else {
                        CToast.ShowToast(FarecalcAct.this, NC.getString(R.string.server_error));
                    }
                }

                @Override
                public void onFailure(Call<StreetCompleteResponse> call, Throwable t) {
                    closeDialog();
                }
            }));
        } else
            CToast.ShowToast(FarecalcAct.this, NC.getString(R.string.check_net_connection));
    }

    /**
     * Closing the alert dialog.
     */
    public void closeDialog() {
        try {
            if (mDialog != null)
                if (mDialog.isShowing())
                    mDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Showing the alert dialog
     */
    public void showDialog() {
//        try {
//            if (NetworkStatus.isOnline(FarecalcAct.this)) {
//                View view = View.inflate(FarecalcAct.this, R.layout.progress_bar, null);
//                mDialog = new Dialog(FarecalcAct.this, R.style.dialogwinddow);
//                mDialog.setContentView(view);
//                mDialog.setCancelable(false);
//                mDialog.show();
//
//                ImageView iv = mDialog.findViewById(R.id.giff);
//                DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(iv);
//                Glide.with(FarecalcAct.this)
//                        .load(R.raw.loading_anim)
//                        .into(imageViewTarget);
//
//            }
//        } catch (Exception e) {
//e.printStackTrace();
//        }

    }

    @Override
    public void positiveButtonClick(DialogInterface dialog, int id, String s) {
        dialog.dismiss();
    }

    @Override
    public void negativeButtonClick(DialogInterface dialog, int id, String s) {
        dialog.dismiss();
    }

    private void startSOSService() {
        SessionSave.saveSession("sos_id", SessionSave.getSession("Id", FarecalcAct.this), FarecalcAct.this);
        SessionSave.saveSession("user_type", "d", FarecalcAct.this);


      //  startService(new Intent(FarecalcAct.this, SOSService.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_READ_PHONE_STATE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startSOSService();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(FarecalcAct.this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }

    private String makeInfo() {
        TripDetailResponse tripDetailResponse = new TripDetailResponse();
        tripDetailResponse.status = 1;
        tripDetailResponse.message = "Success";
        TripDetailResponse.Detail detail = tripDetailResponse.new Detail();
        detail.amt = amt;
        detail.passenger_name = "";
        detail.passenger_image = null;
        detail.map_image = null;
        detail.new_base_fare = new_base_fare;
        detail.new_distance_fare = new_distance_fare;
        detail.fare_per_minute = fare_per_minute;
        detail.distance_fare_metric = distance_fare_metric;
        detail.waiting_fare_minutes = waiting_fare_minutes;
        detail.waiting_fare = f_waitingcost;
        detail.subtotal = subtotal;
        detail.tax_fare = tax_fare;
        detail.tax_percentage = cmpTax;
        detail.promocode_fare = f_farediscount;
        detail.used_wallet_amount = f_walletamt;
        detail.min_distance_status = min_distance_status;
        detail.payment_type_label = null;
        detail.payment_type = null;
        detail.rating = null;
        detail.stops = null;
        detail.trip_minutes = trip_minutes;
        detail.promocode_fare = promocode_fare;
        detail.actual_paid_amount = null;
        detail.eveningfare = eveningfare;
        detail.nightfare = nightfare;


        detail.trip_id = f_tripid;
        detail.distance = f_distance;
        detail.metric = f_metric;
        detail.distance_fare = distanceFare;
        detail.minutes_fare = f_minutes_fare;
        detail.waiting_time = f_waitingtime;
        detail.fare_calculation_type = fare_calculation_type;
        detail.pending_cancel_amount = cancellation_fee;
        detail.trip_duration = f_minutes_traveled;

        detail.toll_amount = tollFareTxt;
        detail.parking_amount = parkingFareTxt;
        detail.booking_fare = bookingFareTxt;
        detail.trip_type = trip_type;
        detail.os_driver_beta = os_driver_beta;
        detail.os_addtinal_km = os_addtinal_km;
        detail.roundtrip = roundtrip;
        detail.service_fare = service_charges;
        tripDetailResponse.detail = detail;
        String stringData = new Gson().toJson(tripDetailResponse);
        return stringData;
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {

        System.out.println("Payment_success"+ " "+"3");
        System.out.println("Payment_success"+ " "+s.toString());
        System.out.println("Payment_success"+ " "+paymentData.getPaymentId().toString());
        System.out.println("Payment_success"+ " "+paymentData.getOrderId().toString());

        SessionSave.saveSession("razepay_orderId",paymentData.getPaymentId().toString(),FarecalcAct.this);

        callurl();
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

    }

    void paymentFlow(String order_id, String addMoneys)
    {
        String samount = String.valueOf(addMoneys);

        // rounding off the amount.
        int amount = Math.round(Float.parseFloat(addMoneys) * 100);

        // initialize Razorpay account.
        Checkout checkout = new Checkout();

        // set your id as below
        checkout.setKeyID("rzp_test_nvgZh1lC6p2Ide");
        //  checkout.setKeyID("rzp_live_R9cblN3GDHjSRp");

        // set image
        checkout.setImage(R.mipmap.ic_launcher_ride_logic);

        // initialize json object
        JSONObject object = new JSONObject();
        try {
            // to put name
            object.put("name", "Onewaytriptaxi");

            // put description
            object.put("description", "One taxi wallet recharege");
            object.put("image", R.mipmap.ic_launcher_ride_logic);
            object.put("theme.color", getResources().getColor(R.color.app_theme_main));
            object.put("currency", "INR");
            object.put("amount", amount);
            object.put("order_id", order_id);
            // put mobile number
            //    object.put("prefill.contact", SessionSave.getSession("Phone",requireContext()));
            object.put("prefill.contact", "908055398700");

            // put email
            object.put("prefill.email", "vinoth.S@ardhas.com");

            // open razorpay to checkout activity
            checkout.open(FarecalcAct.this, object);
        } catch (JSONException e) {
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
                new APIService_Retrofit_JSON(FarecalcAct.this, this, data, false).execute(url);
            } else {

                dialog1 = Utils.alert_view(FarecalcAct.this, "", NC.getResources().getString(R.string.check_internet), NC.getResources().getString(R.string.ok),
                        "", true, FarecalcAct.this, "");

            }
        }

        @Override
        public void getResult(boolean isSuccess, final String result) {
            SessionSave.saveSession(CommonData.AMOUNT_USED_FROM_WALLET, "", FarecalcAct.this);
            if (isSuccess) {
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        SessionSave.saveSession("travel_status", "", FarecalcAct.this);
                        SessionSave.saveSession("trip_id", "", FarecalcAct.this);
                        SessionSave.saveSession("type_book", "", FarecalcAct.this);
                        SessionSave.saveSession("status", "F", FarecalcAct.this);

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
                        SessionSave.saveSession("driver_statistics", String.valueOf(jsonDriver), FarecalcAct.this);
                        LocationUpdate.sTimer = "00:00:00";
                        LocationUpdate.finalTime = 0L;
                        LocationUpdate.timeInMillies = 0L;
                        SessionSave.saveSession("waitingHr", "", FarecalcAct.this);
                        CommonData.travel_km = 0;
                        SessionSave.setGoogleDistance(0f, FarecalcAct.this);
                        SessionSave.setDistance(0f, FarecalcAct.this);
                        SessionSave.setArriveDistance(0f, FarecalcAct.this);
                        SessionSave.saveGoogleWaypoints(null, null, "", 0.0, "", FarecalcAct.this);
                        SessionSave.saveWaypoints(null, null, "", 0.0, "", FarecalcAct.this);
                        Intent jobintent = new Intent(FarecalcAct.this, JobdoneAct.class);
                        Bundle bun = new Bundle();
                        bun.putString("message", result);
                        jobintent.putExtras(bun);
                        startActivity(jobintent);
                        finish();
                    }else if (json.getInt("status") == 18){

                        final JSONObject paymentresponse = json.getJSONObject("gateway_response");

                        SessionSave.saveSession("payment_url", paymentresponse.getString("payment_url"),FarecalcAct.this);
                        SessionSave.saveSession("transaction_id",paymentresponse.getString("TRANSACTIONID"),FarecalcAct.this);


                    }


                    else if (json.getInt("status") == -9) {
                        msg = json.getString("message");
                        lay_fare.setVisibility(View.VISIBLE);

                        dialog1 = Utils.alert_view(FarecalcAct.this, "", msg, NC.getResources().getString(R.string.ok),
                                "", true, FarecalcAct.this, "");


                    } else if (json.getInt("status") == 0) {
                        msg = json.getString("message");
                        lay_fare.setVisibility(View.VISIBLE);
                        dialog1 = Utils.alert_view(FarecalcAct.this, "", msg, NC.getResources().getString(R.string.ok),
                                "", true, FarecalcAct.this, "");


                    } else if (json.getInt("status") == -1) {
                        msg = json.getString("message");

                        dialog1 = Utils.alert_view(FarecalcAct.this, "", msg, NC.getResources().getString(R.string.ok),
                                "", true, FarecalcAct.this, "");

                        if (json.has("driver_statistics")) {
                            SessionSave.saveSession("trip_id", "", FarecalcAct.this);
                            SessionSave.saveSession("status", "F", FarecalcAct.this);
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
                            SessionSave.saveSession("driver_statistics", String.valueOf(jsonDriver), FarecalcAct.this);
                        }
                        Intent intent = new Intent(FarecalcAct.this, HomePageActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        msg = json.getString("message");
                        lay_fare.setVisibility(View.VISIBLE);

                        dialog1 = Utils.alert_view(FarecalcAct.this, "", msg, NC.getResources().getString(R.string.ok),
                                "", true, FarecalcAct.this, "");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                runOnUiThread(() -> CToast.ShowToast(FarecalcAct.this, NC.getString(R.string.server_error)));
                lay_fare.setVisibility(View.VISIBLE);
            }
        }


    }

    /**
     * CompleteTrip API response parsing.
     */
    private class CompleteTrip implements APIResult {
        public CompleteTrip(String url, String latitude, String longitude, String distance, String waitingHr, String drop_location, String stopList) {

            try {
                System.out.println("Trip_type_get"+ " "+" "+trip_type);
                JSONObject j = new JSONObject();
                j.put("trip_id", SessionSave.getSession("trip_id", FarecalcAct.this));
                j.put("drop_latitude", latitude);
                j.put("drop_longitude", longitude);
                j.put("drop_location", drop_location);
                j.put("distance", distance);
                j.put("actual_distance", "");
                j.put("waiting_hour", waitingHr);
                j.put("driver_app_version", BuildConfig.VERSION_NAME);
                j.put("stops", new JSONArray(stopList));
                //                    SessionSave.saveSession("type_book",booking_Type,FarecalcAct.this);

                if(  SessionSave.getSession("type_book",FarecalcAct.this).equals("2") ||   SessionSave.getSession("type_book",FarecalcAct.this).equals("3") )
                {
                    j.put("km_odameter", SessionSave.getSession("end_odameter", FarecalcAct.this));

                }
                if (servicesJsonArray != null && servicesJsonArray.length() > 0) {
                    j.put("service_charge", servicesJsonArray);
                }
                if(SessionSave.getSession("ishills",FarecalcAct.this).equals("false"))
                {
                    j.put("ishills", 0);

                }
                else {
                    j.put("ishills", 1);
                }

                if(SessionSave.getSession("isonewaytrip",FarecalcAct.this).equals("yes"))
                {

                    j.put("os_waiting_hr", Double.parseDouble(SessionSave.getSession("manual_outstation_hours",FarecalcAct.this)));


                }
                new APIService_Retrofit_JSON(FarecalcAct.this, this, j, false).execute(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(boolean isSuccess, String result) {

            if (isSuccess) {
                try {
                    message = result;
                    setFareCalculatorScreen();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class showServiceList implements APIResult {

        public showServiceList(final String url, JSONObject data) {
            try {
                if (isOnline()) {
                    new APIService_Retrofit_JSON(
                            FarecalcAct.this,
                            this,
                            data,
                            false
                    ).execute(url);
                } else {
                    dialog1 = Utils.alert_view(
                            FarecalcAct.this,
                            NC.getResources().getString(R.string.message),
                            NC.getResources().getString(R.string.check_net_connection),
                            NC.getResources().getString(R.string.ok),
                            "",
                            true,
                            FarecalcAct.this,
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
                        ShowToast(FarecalcAct.this, NC.getString(R.string.server_error))
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
//                                ShowToast(FarecalcAct.this, "No service data available")
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
                        ShowToast(FarecalcAct.this, "Invalid server response")
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
            Toast.makeText(FarecalcAct.this, item.getService_type() + " removed.", Toast.LENGTH_SHORT).show();
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

    private void completeTripApi() {
        String lat = details.getStringExtra("lat");
        String lon = details.getStringExtra("lon");
        String distance = details.getStringExtra("distance");
        String waitingHr = details.getStringExtra("waitingHr");
        String drop_location = details.getStringExtra("drop_location");
        String stopList = details.getStringExtra("stopList");
        String url = "type=complete_trip";
        new CompleteTrip(url, lat, lon, distance, waitingHr, drop_location, stopList);
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

}