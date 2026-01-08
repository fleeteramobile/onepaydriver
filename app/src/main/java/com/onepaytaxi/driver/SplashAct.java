package com.onepaytaxi.driver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.onepaytaxi.driver.Login.DriverLoginActivity;
import com.onepaytaxi.driver.bookings.activetrip.ActiveTripActivity;
import com.onepaytaxi.driver.data.CommonData;
import com.onepaytaxi.driver.data.apiData.ApiRequestData;
import com.onepaytaxi.driver.data.apiData.CompanyDomainResponse;
import com.onepaytaxi.driver.homepage.HomePageActivity;
import com.onepaytaxi.driver.interfaces.APIResult;
import com.onepaytaxi.driver.permission.DevicePermissionActivity;
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON_NoProgress;
import com.onepaytaxi.driver.service.BackgroundCoreConfig;
import com.onepaytaxi.driver.service.CoreClient;
import com.onepaytaxi.driver.service.LocationUpdate;
import com.onepaytaxi.driver.service.RetrofitCallbackClass;
import com.onepaytaxi.driver.service.ServiceGenerator;
import com.onepaytaxi.driver.utils.CL;
import com.onepaytaxi.driver.utils.CToast;
import com.onepaytaxi.driver.utils.CommonSettings;
import com.onepaytaxi.driver.utils.FontHelper;
import com.onepaytaxi.driver.utils.LocationDb;
import com.onepaytaxi.driver.utils.NC;
import com.onepaytaxi.driver.utils.NetworkStatus;
import com.onepaytaxi.driver.utils.SessionSave;
import com.onepaytaxi.driver.utils.Systems;
import com.onepaytaxi.driver.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * fleetera
 * This class will be called initially on app launch.Here we will load all basic details from server
 */
public class SplashAct extends MainActivity {
    //country code - this option is used to  high light/set first option in country code dropdown list
    public static final String CURRENT_COUNTRY_CODE = "+91";
    public static final String CURRENT_COUNTRY_ISO_CODE = "IN";

    private static final int MY_PERMISSIONS_REQUEST_GPS = 111;
    public static String REG_ID = "";
    private final int REQUEST_READ_PHONE_STATE = 292;
    private final int ACTION_MANAGE_OVERLAY_PERMISSION = 300;

    LocationDb objLocationDb;
    private String mDeviceid;
    private LocationManager mLocationManager;
    private boolean isGPSEnabled;
    private final int id = 0;
    private Dialog mDialog;
    private long getCore_Utc;
    private String getCoreLangTime;
    private String getCoreColorTime;
    private FrameLayout splashLayout;
    private Dialog myDialog;


    @Override
    public int setLayout() {
        // TODO Auto-generated method stub
        int curVersion = BuildConfig.VERSION_CODE;
        try {

            if (curVersion != 0 && SessionSave.getSession("trip_id", SplashAct.this).equals(""))
                if (SessionSave.getSession(String.valueOf(curVersion), this).trim().equals("")) {
                    SessionSave.saveSession("base_url", "", SplashAct.this);
                    Systems.out.println("chery_chkng_url_base2" + SessionSave.getSession("base_url", SplashAct.this));

                    SessionSave.saveSession("api_key", "", SplashAct.this);
                    SessionSave.saveSession("encode", "", SplashAct.this);
                    SessionSave.saveSession("image_path", "", SplashAct.this);
                    SessionSave.saveSession(String.valueOf(curVersion), "No", SplashAct.this);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return R.layout.splash_lay;
    }

    @SuppressLint("NewApi")
    @Override
    public void Initialize() {
        // TODO Auto-generated method stub
        if (NetworkStatus.isOnline(SplashAct.this)) {
            init();
        } else {
            errorInSplash(NC.getString(R.string.check_net_connection));
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        SessionSave.ClearSessionOneTime(this);
    }

    /**
     * This method is used to load all basic initialisation
     */
    public void init() {
        AtomicInteger c = new AtomicInteger(0);

        FontHelper.applyFont(this, findViewById(R.id.rootlay));
        splashLayout = findViewById(R.id.lay_splash);
        objLocationDb = new LocationDb(SplashAct.this);

        if (SessionSave.getSession("sDevice_id", SplashAct.this).equals("")) {
            if (!UUID.randomUUID().toString().equals("")) {
                mDeviceid = UUID.randomUUID().toString();
            } else {
                mDeviceid = CommonData.mDevice_id_constant + c.incrementAndGet();
            }
            SessionSave.saveSession("sDevice_id", mDeviceid, SplashAct.this);
        }

        if (CommonData.mDevice_id.equals("")) {
            if (!UUID.randomUUID().toString().equals("")) {
                mDeviceid = UUID.randomUUID().toString();
            } else {
                mDeviceid = CommonData.mDevice_id_constant + c.incrementAndGet();
            }
            CommonData.mDevice_id = mDeviceid;
        }

        CommonData.current_act = "SplashAct";
        if (SessionSave.getSession("Lang", SplashAct.this).equals("")) {
            SessionSave.saveSession("Lang", "en", SplashAct.this);
            SessionSave.saveSession("Lang_Country", "en_GB", SplashAct.this);
        }


    }


    private boolean VersionCheck() {
        try {
            String newVersion = SessionSave.getSession("play_store_version", SplashAct.this).equals("") ? "0" : SessionSave.getSession("play_store_version", SplashAct.this);
            int curVersion = BuildConfig.VERSION_CODE;

            System.err.println("New version" + newVersion + "curVersion" + curVersion + "---" + (curVersion < value(newVersion)));
            return curVersion < Integer.parseInt(newVersion);
        } catch (Exception e) {
            // TODO: handle exception

            e.printStackTrace();
        }
        return false;
    }


    /**
     * Method to check is version update is force update or not
     *
     * @return
     */
    private boolean forceUpdateCheck() {
        try {
            String lastForceUpdateVersion = SessionSave.getSession(CommonData.LAST_FORCEUPDATE_VERSION, SplashAct.this).equals("") ? "0" : SessionSave.getSession(CommonData.LAST_FORCEUPDATE_VERSION, SplashAct.this);
            int curVersion = BuildConfig.VERSION_CODE;

            System.err.println("New version" + lastForceUpdateVersion + "curVersion" + curVersion);
            if (curVersion < value(lastForceUpdateVersion)) {
                return true;
            } else return value(lastForceUpdateVersion) <= 0;

        } catch (Exception e) {
            // TODO: handle exception

            e.printStackTrace();
        }
        return false;
    }

    /**
     * Return long value for given string
     */
    private long value(String string) {
        string = string.trim();
        if (string.contains(".")) {
            final int index = string.lastIndexOf(".");
            return value(string.substring(0, index)) * 100 + value(string.substring(index + 1));
        } else {
            return Long.parseLong(string);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
//        String token = FirebaseInstanceId.getInstance().getToken();
//        Systems.out.println("resume1" + token);
        if (getIntent() != null && getIntent().getStringExtra("alert_message") != null)
        {
            String alertSchedule = getIntent().getStringExtra("alert_schedule");
            if (alertSchedule != null && alertSchedule.equals("1")) {
                Intent i = new Intent(SplashAct.this, HomePageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                i.putExtra("alert_message", getIntent().getStringExtra("alert_message"));
                i.putExtra("alert_schedule", "1");
                startActivity(i);
                finish();
            } else
            {
                if (myDialog != null && myDialog.isShowing())
                    myDialog.dismiss();
                myDialog = Utils.alert_view_dialog(SplashAct.this, NC.getString(R.string.message),
                        getIntent().getStringExtra("alert_message"),
                        NC.getString(R.string.ok),
                        "",
                        false, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                SplashAct.this.finish();
                            }
                        }, null, "");
            }
        } else if (!Settings.canDrawOverlays(SplashAct.this)) {
            checkPermissionOverLay();
        } else
            LoadDataForsplash();
    }

    private void checkPermissionOverLay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION);
            }
        }
    }


    private void LoadDataForsplash() {
        String reqString = Build.MANUFACTURER;

        if (SessionSave.getSession("settings_alert", SplashAct.this).isEmpty()) {
            if (reqString.toLowerCase().contains("huawei")) {
                HuaweiDeviceAlert();
            } else if (reqString.toLowerCase().contains("vivo")) {
                vivoDeviceAlert();
            } else if (reqString.toLowerCase().contains("xiaomi")) {
                xiaomiDeviceAlert();
            } else if (reqString.toLowerCase().contains("oppo")) {
                oppoDeviceAlert();
            } else {
                SessionSave.saveSession("settings_alert", "SETTINGS", SplashAct.this);
                LoadDataForsplash();
            }
        } else if (NetworkStatus.isOnline(SplashAct.this)) {
            getGPS();
        }


    }


    public void HuaweiDeviceAlert() {

        Utils.alert_view_dialog(SplashAct.this,
                NC.getResources().getString(R.string.huawei_title),
                String.format(NC.getResources().getString(R.string.huawei_msg)),
                NC.getResources().getString(R.string.ok), "", false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SessionSave.saveSession("settings_alert", "SETTINGS", SplashAct.this);
                        EnableHuaweiProtectedApps();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, "");
    }


    public void vivoDeviceAlert() {
        Utils.alert_view_dialog(SplashAct.this,
                NC.getResources().getString(R.string.auto_start),
                String.format(NC.getResources().getString(R.string.auto_start_msg)),
                NC.getResources().getString(R.string.ok), "", false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SessionSave.saveSession("settings_alert", "SETTINGS", SplashAct.this);
                        autostartVivo();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, "");
    }

    public void xiaomiDeviceAlert() {

        Utils.alert_view_dialog(SplashAct.this,
                NC.getResources().getString(R.string.auto_start),
                String.format(NC.getResources().getString(R.string.auto_start_msg)),
                NC.getResources().getString(R.string.ok), "", false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SessionSave.saveSession("settings_alert", "SETTINGS", SplashAct.this);
                        try {
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.miui.securitycenter",
                                    "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, "");

    }

    public void oppoDeviceAlert() {

        Utils.alert_view_dialog(SplashAct.this,
                NC.getResources().getString(R.string.power_saving),
                String.format(NC.getResources().getString(R.string.power_saving_msg)),
                NC.getResources().getString(R.string.ok), "", false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SessionSave.saveSession("settings_alert", "SETTINGS", SplashAct.this);
                        try {
                            Intent intentBatteryUsage = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
                            context.startActivity(intentBatteryUsage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, "");
    }

    private void EnableHuaweiProtectedApps() {
        try {
            String cmd = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cmd = "am start -n com.huawei.systemmanager/.appcontrol.activity.StartupAppControlActivity";
            } else {
                cmd = "am start -n com.huawei.systemmanager/.optimize.process.ProtectActivity";
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                cmd += " --user " + getUserSerial();
            }

            Runtime.getRuntime().exec(cmd);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void autostartVivo() {

        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"));
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                context.startActivity(intent);
            } catch (Exception ex) {
                try {
                    Intent intent = new Intent();
                    intent.setClassName("com.iqoo.secure",
                            "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager");
                    context.startActivity(intent);
                } catch (Exception exx) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private String getUserSerial() {
//noinspection ResourceType
        @SuppressLint("WrongConstant")
        Object userManager = getSystemService("user");
        if (null == userManager) return "";

        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            Long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);

            if (userSerial != null) {
                return String.valueOf(userSerial);
            } else {
                return "";
            }
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException |
                 IllegalAccessException ignored) {
        }
        return "";
    }


    public void getGPS() {
        if (isOnline()) {
            if (!(VersionCheck())) {
                callApi();
            } else
                versionAlert(SplashAct.this);
        } else {
            CToast.ShowToast(SplashAct.this, NC.getResources().getString(R.string.check_net_connection));
        }
    }

    void callApi() {

        if (SessionSave.getSession("base_url", SplashAct.this).trim().equals(""))
            if (CommonSettings.askDomain)
                getUrl();
            else
                urlApi("", "", "");
        else {
            setLocale();

            if (!SessionSave.getSession("base_url", SplashAct.this).trim().equals("")) {
                ServiceGenerator.API_BASE_URL = SessionSave.getSession("base_url", SplashAct.this);


                MoveToNavigatorPanel();

            } else {
                if (CommonSettings.askDomain)
                    getUrl();
                else
                    urlApi("", "", "");

            }
        }
    }

    private void MoveToNavigatorPanel() {
        startService(new Intent(SplashAct.this, BackgroundCoreConfig.class));
        Intent i;
        if (SessionSave.getSession("user_privacy_policy", SplashAct.this).equals("")) {
          getlocationdilaog();
        } else if (SessionSave.getSession("Id", SplashAct.this).equals("")) {
           // i = new Intent(SplashAct.this, UserLoginAct.class);
            i = new Intent(SplashAct.this, DriverLoginActivity.class);

            startActivity(i);
            finish();
        } else {
            if (SessionSave.getSession("trip_id", SplashAct.this).equals("")) {
                i = new Intent(SplashAct.this, HomePageActivity.class);
                startActivity(i);
                finish();
            } else {
                if (SessionSave.getSession("travel_status", SplashAct.this).equals("5")) {
                    i = new Intent(SplashAct.this, ActiveTripActivity.class);
                    startActivity(i);
                    finish();
                } else {

                        i = new Intent(SplashAct.this, OngoingAct.class);
                        startActivity(i);
                        finish();

                }
            }
        }
    }

    /**
     * Setting Language Configuration
     */
    public void setLocale() {
        if (SessionSave.getSession("Lang", SplashAct.this).equals("")) {
            SessionSave.saveSession("Lang", "en", SplashAct.this);
            SessionSave.saveSession("Lang_Country", "en_GB", SplashAct.this);
        }


        Configuration config = new Configuration();
        String language = SessionSave.getSession("Lang", SplashAct.this);
        String langcountry = SessionSave.getSession("Lang_Country", SplashAct.this);
        String[] arry = langcountry.split("_");
        config.locale = new Locale(language, arry[1]);
        Locale.setDefault(new Locale(language, arry[1]));
        SplashAct.this.getBaseContext().getResources().updateConfiguration(config, SplashAct.this.getResources().getDisplayMetrics());

    }

    public void errorInSplash(String message) {
        dialog1 = Utils.alert_view_dialog(SplashAct.this, NC.getString(R.string.message), message, NC.getString(R.string.c_tryagain), NC.getString(R.string.cancel), false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = getIntent();
                finish();
                startActivity(intent);

            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Activity activity = SplashAct.this;

                final Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.finish();
                dialog.dismiss();
            }
        }, "");
    }


    @Override
    protected void onDestroy() {
        if (dialog1 != null)
            Utils.closeDialog(dialog1);
        super.onDestroy();
    }


    /**
     * Getting Subdomain
     */
    private void getUrl() {
        //if (BuildConfig.DEBUG) {
            final View view1 = View.inflate(SplashAct.this, R.layout.domain_lay, null);
            if (myDialog != null && myDialog.isShowing())
                myDialog.cancel();
            myDialog = new Dialog(SplashAct.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            myDialog.setContentView(view1);
            myDialog.setCancelable(false);
            myDialog.setCanceledOnTouchOutside(false);
            FontHelper.applyFont(SplashAct.this, myDialog.findViewById(R.id.inner_content));
            myDialog.setCancelable(true);
            myDialog.show();

            final EditText edt_url = myDialog.findViewById(R.id.edt_url);
            final EditText edt_domain = myDialog.findViewById(R.id.edt_domain);
            EditText edt_access = myDialog.findViewById(R.id.edt_accesskey);
            edt_access.setText("");
            SessionSave.saveSession("api_key", ((EditText) myDialog.findViewById(R.id.edt_company_key)).getText().toString(), SplashAct.this);
            edt_url.setText("https://demofleetera.ardhas.com/driverapi201/index/");
            if (!CommonSettings.isPointingLive) {
                edt_url.setText(CommonSettings.demo_url);
            }
            else {
                edt_url.setText(CommonSettings.live_url);
            }
            edt_domain.setText("");
            LinearLayout btn_ok = myDialog.findViewById(R.id.btn_ok);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                private String access_key, url, domain;

                @Override
                public void onClick(final View v) {

                    try {
                        access_key = edt_access.getText().toString();
                        url = edt_url.getText().toString();
                        domain = edt_access.getText().toString();
                        if (!access_key.equals("") && !url.equals("") && !domain.equals("")) {
                            urlApi(access_key, url, domain);
                            myDialog.dismiss();
                        } else {
                            CToast.ShowToast(SplashAct.this, "Please enter all details");
                        }

                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
            });

    }

    public void setEditTextMaxLength(int length, EditText edt_text) {
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(length);
        edt_text.setFilters(FilterArray);
    }

    /**
     * Getting base path
     */
    private void urlApi(String keyy, String mUrl, String domain) {
        String url = "";
        String str_domain = "";
        String key = "";
        try {


            if (!mUrl.equals("") && !domain.equals("")) {
                url = mUrl;
                str_domain = domain;
                key = keyy;
            } else {

                if (!CommonSettings.forclient)
                {
                    if (!CommonSettings.isPointingLive) {
                        if(CommonSettings.isPointingUAT)
                        {
                            url = CommonSettings.uat_url;
                            str_domain = CommonSettings.companyMainDomain;
                            key = CommonSettings.uat_key;
                        } else if (CommonSettings.isTestingPonited) {

                            url = CommonSettings.test_url;
                            str_domain = CommonSettings.companyMainDomain;
                            key = CommonSettings.test_key;
                        } else {
                            url = CommonSettings.demo_url;
                            str_domain = CommonSettings.companyMainDomain;
                            key = CommonSettings.demo_key;
                        }

                    } else {
                        url = CommonSettings.live_url;
                        str_domain = CommonSettings.live_key;
                        key = CommonSettings.live_key;
                    }
                }
                else {
                    url = CommonSettings.client_url;
                    str_domain = CommonSettings.client_key;
                    key = CommonSettings.client_key;
                }


//

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SessionSave.saveSession(CommonData.FIREBASE_KEY, "0", SplashAct.this);
        String mUUID = "";
        if (!UUID.randomUUID().toString().equals("")) {
            mUUID = UUID.randomUUID().toString();
        } else {
            mUUID = CommonData.mDevice_id_constant;
        }
//        SessionSave.saveSession(CommonData.DEVICE_ID, Secure.getString(getContentResolver(), Secure.ANDROID_ID), SplashAct.this);
        SessionSave.saveSession(CommonData.DEVICE_ID, mUUID, SplashAct.this);
        //        CoreClient client = new ServiceGenerator(url, SplashAct.this, false).createService(CoreClient.class);
        CoreClient client = MyApplication.getInstance().getCheckCompanyDomainapiManager(url);
        ApiRequestData.BaseUrl request = new ApiRequestData.BaseUrl();
        request.company_domain = key;
        request.company_main_domain = str_domain;
        request.device_type = "1";
        SessionSave.saveSession(CommonData.DOMAIN_URL, "", SplashAct.this);
        SessionSave.saveSession(CommonData.DOMAIN_URL, url, SplashAct.this);
        SessionSave.saveSession(CommonData.COMPANY_DOMAIN, "", SplashAct.this);
        SessionSave.saveSession(CommonData.COMPANY_DOMAIN, str_domain, SplashAct.this);
        SessionSave.saveSession(CommonData.ACCESS_KEY, "", SplashAct.this);
        SessionSave.saveSession(CommonData.ACCESS_KEY, key, SplashAct.this);
        showLoading(SplashAct.this);
        Call<CompanyDomainResponse> response = client.callData(ServiceGenerator.COMPANY_KEY, request);
        response.enqueue(new RetrofitCallbackClass<>(SplashAct.this, new Callback<CompanyDomainResponse>() {
            @Override
            public void onResponse(Call<CompanyDomainResponse> call, Response<CompanyDomainResponse> response) {
                cancelLoading();
                if (response.isSuccessful() && response.body() != null)
                {
                    CompanyDomainResponse cr = response.body();
                    if (cr != null) {
                        if (cr.status.trim().equals("1"))
                        {
                            SessionSave.saveSession("show_hauwai_alert", true, SplashAct.this);

                            if (cr.https_base_url != null)
                                SessionSave.saveSession("base_url", cr.https_base_url, SplashAct.this);
                            Systems.out.println("chery_chkng_url_base1" + SessionSave.getSession("base_url", SplashAct.this));

                            SessionSave.saveSession("api_key", cr.apikey, SplashAct.this);
                            SessionSave.saveSession("encode", cr.encode, SplashAct.this);

                            if (cr.https_base_url != null)
                                ServiceGenerator.API_BASE_URL = cr.https_base_url;
                            SessionSave.saveSession("image_path", cr.androidPaths.static_image, SplashAct.this);
                            String totalLanguage = "";
                            String defaultLanguage = cr.default_language;
                            if (cr.androidPaths.driver_language != null)
                            {

                                for (int i = 0; i < cr.androidPaths.driver_language.size(); i++) {
                                    String key_ = "";
                                    totalLanguage += (cr.androidPaths.driver_language.get(i).language).replaceAll(".xml", "") + "____";
                                    SessionSave.saveSession("LANG" + i, cr.androidPaths.driver_language.get(i).language, SplashAct.this);
                                    SessionSave.saveSession("LANGTemp" + i, cr.androidPaths.driver_language.get(i).design_type, SplashAct.this);

                                    SessionSave.saveSession("LANGCode" + i, cr.androidPaths.driver_language.get(i).language_code, SplashAct.this);

                                    SessionSave.saveSession(cr.androidPaths.driver_language.get(i).language, cr.androidPaths.driver_language.get(i).url, SplashAct.this);
                                    if (cr.androidPaths.driver_language.get(i).language_code.equalsIgnoreCase(defaultLanguage)) {
                                        SessionSave.saveSession("LANGTempDef", cr.androidPaths.driver_language.get(i).design_type, SplashAct.this);
                                        SessionSave.saveSession("LANGDef", cr.androidPaths.driver_language.get(i).language, SplashAct.this);
                                        SessionSave.saveSession("Lang", cr.androidPaths.driver_language.get(i).language_code, SplashAct.this);
                                    }
                                }
                                if (SessionSave.getSession("LANGDef", SplashAct.this).equals(""))
                                    SessionSave.saveSession("LANGDef", SessionSave.getSession("LANG0", SplashAct.this), SplashAct.this);
                                if (SessionSave.getSession("LANGTempDef", SplashAct.this).trim().equals(""))
                                    SessionSave.saveSession("LANGTempDef", SessionSave.getSession("LANGTemp0", SplashAct.this), SplashAct.this);
                                SessionSave.saveSession("lang_json", totalLanguage, SplashAct.this);
                                if (SessionSave.getSession("Lang", SplashAct.this).equals(""))
                                    SessionSave.saveSession("Lang", cr.androidPaths.driver_language.get(0).language_code.replaceAll(".xml", ""), SplashAct.this);
                            }
                            SessionSave.saveSession("colorcode", cr.androidPaths.colorcode, SplashAct.this);
                            if (mDialog != null)
                                mDialog.dismiss();
                            String url = "type=getcoreconfig";
                            new CoreConfigCall(url);

                        } else {
                            alert_view_company(SplashAct.this, NC.getString(R.string.message), cr.message, NC.getString(R.string.ok), NC.getString(R.string.cancel));
                        }
                    } else {
                        alert_view_company(SplashAct.this, NC.getString(R.string.message), NC.getString(R.string.server_error), NC.getString(R.string.ok), NC.getString(R.string.cancel));
                    }
                } else {
                    alert_view_company(SplashAct.this, NC.getString(R.string.message), NC.getString(R.string.server_error), NC.getString(R.string.ok), NC.getString(R.string.cancel));
                }
            }

            @Override
            public void onFailure(Call<CompanyDomainResponse> call, Throwable t) {
                cancelLoading();
//                CToast.ShowToast(SplashAct.this, NC.getString(R.string.server_error));
                alert_view_company(SplashAct.this, NC.getString(R.string.message), NC.getString(R.string.server_error), NC.getString(R.string.ok), NC.getString(R.string.cancel));
            }
        }));
    }

    /**
     * Adding string files to Local hashmap
     */
    private synchronized void getAndStoreStringValues(String result) {
        try {


            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
            Document doc = dBuilder.parse(is);
            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("*");

            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    NC.nfields_byName.put(element2.getAttribute("name"), element2.getTextContent());
                }
            }
            getValueDetail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Getting String values from local
     */
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
            NC.nfields_byID.put(NC.fields_id.get(h), NC.nfields_byName.get(h));
            // do stuff
        }

    }

    /**
     * Getting Color values from local hash map
     */
    synchronized void getColorValueDetail() {
        Field[] fieldss = R.color.class.getDeclaredFields();
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
        }

    }

    /**
     * Adding color files to Local hashmap
     */
    private synchronized void getAndStoreColorValues(String result) {
//        try {
//            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//            InputStream is = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
//            Document doc = dBuilder.parse(is);
//            Element element = doc.getDocumentElement();
//            element.normalize();
//
//            NodeList nList = doc.getElementsByTagName("*");
//
//            Systems.out.println("lislength" + nList.getLength());
//            for (int i = 0; i < nList.getLength(); i++) {
//
//                Node node = nList.item(i);
//                if (node.getNodeType() == Node.ELEMENT_NODE) {
//
//                    Element element2 = (Element) node;
//                    CL.nfields_byName.put(element2.getAttribute("name"), element2.getTextContent());
//
//                }
//            }
//            getColorValueDetail();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void alert_view_company(Context mContext, String title, String message, String success_txt, String failure_txt) {
        dialog1 = Utils.alert_view_dialog(SplashAct.this, title, message, success_txt,
                failure_txt, true, (dialog, which) -> {
                    dialog.dismiss();
                    getUrl();
                }, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                }, "");
    }

    public void alert_view_date(Context mContext, String title, String message, String success_txt, String failure_txt) {
        dialog1 = Utils.alert_view_dialog(SplashAct.this, title, message, success_txt, failure_txt, true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
            }
        }, (dialog, which) -> {
            dialog.dismiss();
            finish();
        }, "");
    }

    /**
     * Alert dialog to show version alert
     */
    public void versionAlert(final Context mContext) {
        String negativeBtnText;
        boolean forceUpdate = forceUpdateCheck();
        if (forceUpdate) {
            negativeBtnText = NC.getResources().getString(R.string.cancel);
        } else {
            negativeBtnText = NC.getResources().getString(R.string.version_up_later);
        }
        dialog1 = Utils.alert_view_dialog(SplashAct.this, NC.getResources().getString(R.string.version_up_title), NC.getResources().getString(R.string.version_up_message), NC.getResources().getString(R.string.version_up_now), negativeBtnText, false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mContext.getPackageName()));
                mContext.startActivity(intent);
                dialog.dismiss();
            }
        }, (dialog, which) -> {
            dialog.dismiss();
            if (forceUpdate) {
                SplashAct.this.finish();
                if (SessionSave.getSession("trip_id", SplashAct.this).equals(""))
                    stopService(new Intent(SplashAct.this, LocationUpdate.class));
            } else {
                callApi();
            }
        }, "");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GPS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    } else {

                        finish();
                    }
                }
                return;
            }
            case REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        getGPS();
                    }
                }
            }
            case ACTION_MANAGE_OVERLAY_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        getGPS();
                    }
                }
            }
        }
    }


    /**
     * Method to logout user if status -101 and redirect to login page
     *
     * @param message - To intimate user by showing alert message
     */
    private void forceLogout(String message) {
        CToast.ShowToast(SplashAct.this, message);
        ServiceGenerator.API_BASE_URL = "";
        SessionSave.saveSession("base_url", "", SplashAct.this);
        SessionSave.saveSession("Id", "", SplashAct.this);
        SessionSave.clearAllSession(SplashAct.this);
        stopService(new Intent(this, LocationUpdate.class));
        finish();
    }


    /**
     * Getting Language Files from Server
     */
    private class callString implements APIResult {
        public callString(final String url) {
            // TODO Auto-generated constructor stub

            String urls = SessionSave.getSession("currentStringUrl", SplashAct.this);
            if (urls.equals("")) {
                urls = SessionSave.getSession(SessionSave.getSession("LANGDef", SplashAct.this), SplashAct.this);
                if (SessionSave.getSession("LANGTempDef", SplashAct.this).trim().equalsIgnoreCase("RTL")) {
                    SessionSave.saveSession("Lang_Country", "ar_EG", SplashAct.this);
                    SessionSave.saveSession("Lang", "ar", SplashAct.this);
                    Configuration config = new Configuration();
                    String language = SessionSave.getSession("Lang", SplashAct.this);
                    String langcountry = SessionSave.getSession("Lang_Country", SplashAct.this);
                    String[] arry = langcountry.split("_");
                    config.locale = new Locale(language, arry[1]);
                    Locale.setDefault(new Locale(language, arry[1]));
                }
            }
            new APIService_Retrofit_JSON_NoProgress(SplashAct.this, this, null, true, urls, true).execute();
        }

        @Override
        public void getResult(boolean isSuccess, String result) {

            if (isSuccess) {

                setLocale();
             //   getAndStoreStringValues(result);
                SessionSave.saveSession("wholekey", result, SplashAct.this);

                if (SessionSave.getSession("wholekeyColor", SplashAct.this).trim().equals("") || !SessionSave.getSession(CommonData.PASSENGER_COLOR_TIME, SplashAct.this).equals(getCoreColorTime))
                    new callColor("");
                else {
                    Intent i = null;
//                    if (CommonData.isCurrentTimeZone(getCore_Utc)) {

                    if (SessionSave.getSession("user_privacy_policy", SplashAct.this).equals("")) {
                        getlocationdilaog();
                    } else {

                        if (SessionSave.getSession("Id", SplashAct.this).equals("")) {
                         //   i = new Intent(SplashAct.this, UserLoginAct.class);
                            i = new Intent(SplashAct.this, DriverLoginActivity.class);

                            startActivity(i);
                            finish();
                        } else {
                            if (SessionSave.getSession("trip_id", SplashAct.this).equals("")) {
                                i = new Intent(SplashAct.this, HomePageActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                if (SessionSave.getSession("travel_status", SplashAct.this).equals("5")) {
                                    i = new Intent(SplashAct.this, TripHistoryAct.class);
                                    startActivity(i);
                                    finish();
                                } else {

                                        i = new Intent(SplashAct.this, OngoingAct.class);
                                        startActivity(i);
                                        finish();

                                }
                            }
                        }

                    }
//                    } else {
//                        cancelLoading();
//                        alert_view_date(SplashAct.this, NC.getString(R.string.message), NC.getString(R.string.date_change), NC.getString(R.string.ok), NC.getString(R.string.cancel));
//                    }
                    if (!SessionSave.getSession("base_url", SplashAct.this).trim().equals("")) {
                        ServiceGenerator.API_BASE_URL = SessionSave.getSession("base_url", SplashAct.this);
                        new callColor("");
                    }
                }

            } else {
                errorInSplash(getString(R.string.error_in_string));
            }
        }
    }

    /**
     * Getting Color Files from Server and response parsing
     */
    private class callColor implements APIResult {
        public callColor(final String url) {

            // TODO Auto-generated constructor stub


            new APIService_Retrofit_JSON_NoProgress(SplashAct.this, this, null, true, SessionSave.getSession("colorcode", SplashAct.this).replace("DriverAppColor", "driverAppColors"), true).execute();


        }

        @Override
        public void getResult(boolean isSuccess, String result) {

            if (isSuccess) {
                getAndStoreColorValues(result);
                SessionSave.saveSession("wholekeyColor", result, SplashAct.this);


                Intent i = null;
                if (SessionSave.getSession("user_privacy_policy", SplashAct.this).equals("")) {
                    getlocationdilaog();
                } else {
                    if (SessionSave.getSession("Id", SplashAct.this).equals("")) {
                      //  i = new Intent(SplashAct.this, UserLoginAct.class);
                        i = new Intent(SplashAct.this, DriverLoginActivity.class);

                        startActivity(i);
                        finish();
                    } else {
                        if (SessionSave.getSession("trip_id", SplashAct.this).equals("")) {
                            i = new Intent(SplashAct.this, HomePageActivity.class);
                            startActivity(i);
                            finish();
                        }  else {
                            i = new Intent(SplashAct.this, OngoingAct.class);
                            startActivity(i);
                            finish();
                        }
                    }

                }

            } else {
                errorInSplash(getString(R.string.error_in_color));
            }

        }
    }

    /**
     * CoreConfig method API call and response parsing.
     */
    public class CoreConfigCall implements APIResult {
        CoreConfigCall(final String url) {
            new APIService_Retrofit_JSON_NoProgress(SplashAct.this, this, "", true).execute(url);
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            // TODO Auto-generated method stub
            Systems.out.println(result);
            if (isSuccess) {

                try {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1)
                    {

                        if (json.has("gt_lst_time"))
                            SessionSave.saveSession(CommonData.GETCORE_LASTUPDATE, json.getString("gt_lst_time"), SplashAct.this);


                        if (json.has(CommonData.ACTIVITY_BG))
                            SessionSave.saveSession(CommonData.ACTIVITY_BG, json.getString(CommonData.ACTIVITY_BG), SplashAct.this);
                        if (json.has(CommonData.ERROR_LOGS))
                            SessionSave.saveSession(CommonData.ERROR_LOGS, json.getString(CommonData.ERROR_LOGS).equals("1"), SplashAct.this);

                        JSONArray jArry = json.getJSONArray("detail");
                       /* if (json.has("mobile_socket_http_url")) {
                            SessionSave.saveSession(CommonData.NODE_URL, json.getString("mobile_socket_http_url"), SplashAct.this);
                        }*/


                        if (json.has("https_node_url")) {
                            SessionSave.saveSession(CommonData.NODE_URL, json.getString("https_node_url"), SplashAct.this);
                            SessionSave.saveSession("driver_phone_number", json.getString("driver_phone_number"), SplashAct.this);
                        }

                        if (json.has("mobile_socket_http_domain")) {
                            SessionSave.saveSession(CommonData.NODE_DOMAIN, json.getString("mobile_socket_http_domain"), SplashAct.this);
                        }

                        if (json.has(CommonData.HELP_URL)) {
                            SessionSave.saveSession(CommonData.HELP_URL, json.getString(CommonData.HELP_URL), SplashAct.this);
                        }


                        SessionSave.saveSession("vehicle_state_list", jArry.getJSONObject(0).getString("vehicle_state_list"), SplashAct.this);
                        SessionSave.saveSession("vehicle_plate_prefix_list", jArry.getJSONObject(0).getString("vehicle_plate_prefix_list"), SplashAct.this);
                        SessionSave.saveSession("model_details", jArry.getJSONObject(0).getString("model_details"), SplashAct.this);
                        SessionSave.saveSession("vehicle_info_list", jArry.getJSONObject(0).getString("vehicle_info_list"), SplashAct.this);
                        SessionSave.saveSession("vehicle_color_list", jArry.getJSONObject(0).getString("vehicle_color_list"), SplashAct.this);


                        SessionSave.saveSession("book_now_tone", jArry.getJSONObject(0).getString("book_now_tone"), SplashAct.this);
                        SessionSave.saveSession("book_later_tone", jArry.getJSONObject(0).getString("book_later_tone"), SplashAct.this);
                        SessionSave.saveSession("razorpay_merchant_key", jArry.getJSONObject(0).getString("razorpay_merchant_key"), SplashAct.this);
                        SessionSave.saveSession("razorpay_secret_key", jArry.getJSONObject(0).getString("razorpay_secret_key"), SplashAct.this);


                        if (jArry.getJSONObject(0).has("manual_waiting_enable")) {
                            SessionSave.saveSession(CommonData.WAITING_TIME_MANUAL, jArry.getJSONObject(0).getString("manual_waiting_enable").equals("1"), SplashAct.this);
                        }
                        if (jArry.getJSONObject(0).has(CommonData.SKIP_DRIVER_EMAIL))
                            SessionSave.saveSession(CommonData.SKIP_DRIVER_EMAIL, jArry.getJSONObject(0).getString(CommonData.SKIP_DRIVER_EMAIL).equals("1"), SplashAct.this);
                        else
                            SessionSave.saveSession(CommonData.SKIP_DRIVER_EMAIL, false, SplashAct.this);
                      //  SessionSave.saveSession("admin_on_my_way_enabled_status", jArry.getJSONObject(0).getString("admin_on_my_way_enabled_status"), SplashAct.this);

                        SessionSave.saveSession("api_base", jArry.getJSONObject(0).getString("api_base"), SplashAct.this);
                        SessionSave.saveSession("isFourSquare", jArry.getJSONObject(0).getString("android_foursquare_status"), SplashAct.this);
                        SessionSave.saveSession("android_foursquare_api_key", jArry.getJSONObject(0).getString("android_foursquare_api_key"), SplashAct.this);
                        SessionSave.saveSession("facebook_key", jArry.getJSONObject(0).getString("facebook_key"), SplashAct.this);
                        SessionSave.saveSession("play_store_version", jArry.getJSONObject(0).getString("android_driver_version"), SplashAct.this);

                        if (jArry.getJSONObject(0).has("playstore_driver"))
                            SessionSave.saveSession(CommonData.PLAY_STORE_LINK, jArry.getJSONObject(0).getString("playstore_driver"), SplashAct.this);

                        if (jArry.getJSONObject(0).has("last_forceupdate_version"))
                            SessionSave.saveSession(CommonData.LAST_FORCEUPDATE_VERSION, jArry.getJSONObject(0).getString("last_forceupdate_version"), SplashAct.this);
                        else
                            SessionSave.saveSession(CommonData.LAST_FORCEUPDATE_VERSION, "0", SplashAct.this);


                        if (jArry.getJSONObject(0).has("is_bank_card_id_mantatory"))
                            SessionSave.saveSession(CommonData.IS_BANKID_MANTATORY, jArry.getJSONObject(0).getString("is_bank_card_id_mantatory").equals("1"), SplashAct.this);
                        else
                            SessionSave.saveSession(CommonData.IS_BANKID_MANTATORY, false, SplashAct.this);


                        SessionSave.saveSession("country_iso_code", jArry.getJSONObject(0).getString("country_iso_code"), SplashAct.this);
                        SessionSave.saveSession("country_code", jArry.getJSONObject(0).getString("country_code"), SplashAct.this);


                        SessionSave.saveSession("android_web_key", jArry.getJSONObject(0).getString("android_google_api_key"), SplashAct.this);

                        if (json.has(CommonData.TIMEZONE)) {
                            SessionSave.saveSession(CommonData.TIMEZONE, json.getString(CommonData.TIMEZONE), SplashAct.this);
                        }

                        int length = jArry.length();

                        String googleApiKey = jArry.getJSONObject(0).getString("android_google_api_key");
                        if (!getString(R.string.googleID).equals(googleApiKey))
                            MyApplication.getInstance().setPlaceApiKey(googleApiKey);

                        SessionSave.saveSession(CommonData.GOOGLE_KEY, googleApiKey, SplashAct.this);

                        if (jArry.getJSONObject(0).has("android_mapbox_key")) {
                            SessionSave.saveSession(CommonData.MAP_BOX_TOKEN, jArry.getJSONObject(0).getString("android_mapbox_key"), SplashAct.this);
                        } else {
                            SessionSave.saveSession(CommonData.MAP_BOX_TOKEN, "pk.eyJ1IjoibmFuZGhpbmlzIiwiYSI6ImNqaGl0M3U0aDI5MXczYW8xZGY3bmxod3gifQ.CsQZTI8nf5ZDh8ES3Iu87g", SplashAct.this);
                        }
                        if (jArry.getJSONObject(0).has("android_local_map_enable")) {
                            SessionSave.saveSession(CommonData.LOCAL_STORAGE, jArry.getJSONObject(0).getString("android_local_map_enable").equals("1"), SplashAct.this);
                        } else {
                            SessionSave.saveSession(CommonData.LOCAL_STORAGE, false, SplashAct.this);
                        }
//                        if (!SessionSave.getSession(CommonData.MAP_BOX_TOKEN, SplashAct.this).equals(""))
//                            Mapbox.getInstance(SplashAct.this, SessionSave.getSession(CommonData.MAP_BOX_TOKEN, SplashAct.this));
                        if (jArry.getJSONObject(0).has("sos_setting"))
                            SessionSave.saveSession(CommonData.SOS_ENABLED, jArry.getJSONObject(0).getString("sos_setting").equals("1"), SplashAct.this);

                        if (jArry.getJSONObject(0).has("call_masking"))
                            SessionSave.saveSession(CommonData.CALL_MASKING_ENABLED, jArry.getJSONObject(0).getString("call_masking").equals("1"), SplashAct.this);

                        if (jArry.getJSONObject(0).has("map_settings") && jArry.getJSONObject(0).getJSONObject("map_settings").has("is_google_distance")) {
                            SessionSave.saveSession(CommonData.isGoogleDistance, jArry.getJSONObject(0).getJSONObject("map_settings").getString("is_google_distance").equals("1"), SplashAct.this);
                            SessionSave.saveSession(CommonData.isGoogleRoute, jArry.getJSONObject(0).getJSONObject("map_settings").getString("is_google_direction").equals("1"), SplashAct.this);
                            SessionSave.saveSession(CommonData.isGoogleGeocoder, jArry.getJSONObject(0).getJSONObject("map_settings").getString("is_google_geocode").equals("1"), SplashAct.this);
                            SessionSave.saveSession(CommonData.isNeedtoDrawRoute, jArry.getJSONObject(0).getJSONObject("map_settings").getString("enable_route").equals("1"), SplashAct.this);
                            SessionSave.saveSession(CommonData.isNeedtofetchAddress, jArry.getJSONObject(0).getJSONObject("map_settings").getString("display_current_location").equals("1"), SplashAct.this);
                        } else {
                            SessionSave.saveSession(CommonData.isGoogleDistance, true, SplashAct.this);
                            SessionSave.saveSession(CommonData.isGoogleRoute, true, SplashAct.this);
                            SessionSave.saveSession(CommonData.isGoogleGeocoder, true, SplashAct.this);
                            SessionSave.saveSession(CommonData.isNeedtoDrawRoute, true, SplashAct.this);
                            SessionSave.saveSession(CommonData.isNeedtofetchAddress, true, SplashAct.this);
                        }


                        if (jArry.getJSONObject(0).has("sos_msg"))
                            SessionSave.saveSession("sos_message", jArry.getJSONObject(0).getString("sos_msg"), SplashAct.this);

                        for (int i = 0; i < length; i++) {
                            SessionSave.saveSession("noimage_base", jArry.getJSONObject(i).getString("noimage_base"), getApplicationContext());
                            SessionSave.saveSession("site_currency", jArry.getJSONObject(i).getString("site_currency") + " ", getApplicationContext());
                            Systems.out.println("chry_str_splash" + jArry.getJSONObject(i).getString("site_currency"));
                            SessionSave.saveSession("invite_txt", jArry.getJSONObject(i).getString("aboutpage_description"), getApplicationContext());
                            SessionSave.saveSession("referal", jArry.getJSONObject(i).getString("driver_referral_settings"), getApplicationContext());
                            SessionSave.saveSession("Metric", jArry.getJSONObject(i).getString("metric"), SplashAct.this);
                        }
                        SessionSave.saveSession("wait_interval", jArry.getJSONObject(0).getString("driver_waiting_time_interval"), SplashAct.this);


//                        try {
//                            getCoreLangTime = json.getJSONObject("language_color_status").getString("android_driver_language");
//                            getCoreColorTime = json.getJSONObject("language_color_status").getString("android_driver_colorcode");
//                            getCore_Utc = jArry.getJSONObject(0).getLong("utc_time");
//
//
//                            boolean deflanAvail = false;
//                            String totalLanguage = "";
//                            JSONArray pArray = json.getJSONObject("language_color").getJSONObject("android").getJSONArray("driver_language");
//                            for (int i = 0; i < pArray.length(); i++) {
//
//                                totalLanguage += pArray.getJSONObject(i).getString("language").replaceAll(".xml", "") + "____";
//
//                                SessionSave.saveSession("LANG" + i, pArray.getJSONObject(i).getString("language"), SplashAct.this);
//                                SessionSave.saveSession("LANGTemp" + i, pArray.getJSONObject(i).getString("design_type"), SplashAct.this);
//                                SessionSave.saveSession("LANGCode" + i, pArray.getJSONObject(i).getString("language_code"), SplashAct.this);
//                                SessionSave.saveSession(pArray.getJSONObject(i).getString("language"), pArray.getJSONObject(i).getString("url"), SplashAct.this);
//                                if (!SessionSave.getSession("LANGDef", SplashAct.this).equals("") && pArray.getJSONObject(i).getString("language").contains(SessionSave.getSession("LANGDef", SplashAct.this))) {
//                                    deflanAvail = true;
//                                }
//
//                            }
//                            Systems.out.println("___________defff" + deflanAvail);
//                            if (SessionSave.getSession("LANGDef", SplashAct.this).trim().equals("") || !deflanAvail) {
//                                SessionSave.saveSession("LANGDef", SessionSave.getSession("LANG0", SplashAct.this), SplashAct.this);
//
//                                SessionSave.saveSession("LANGTempDef", SessionSave.getSession("LANGTemp0", SplashAct.this), SplashAct.this);
//                                SessionSave.saveSession("Lang", pArray.getJSONObject(0).getString("language_code").replaceAll(".xml", ""), SplashAct.this);
//                                String url = SessionSave.getSession(SessionSave.getSession("LANG" + 0, SplashAct.this), SplashAct.this);
//                                SessionSave.saveSession("currentStringUrl", url, SplashAct.this);
//                            }
//
//                            SessionSave.saveSession("lang_json", totalLanguage, SplashAct.this);
//
//                         //   SessionSave.saveSession("colorcode", json.getJSONObject("language_color").getJSONObject("android").getString("driverColorCode"), SplashAct.this);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                        Intent i = null;


//                            if (CommonData.isCurrentTimeZone(jArry.getJSONObject(0).getLong("utc_time"))) {

                            if (SessionSave.getSession("user_privacy_policy", SplashAct.this).equals("")) {
                                getlocationdilaog();
                            } else {
                                if (SessionSave.getSession("Id", SplashAct.this).equals("")) {
                                   // i = new Intent(SplashAct.this,  );
                                    i = new Intent(SplashAct.this, DriverLoginActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    if (SessionSave.getSession("trip_id", SplashAct.this).equals("")) {
                                        i = new Intent(SplashAct.this, HomePageActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        if (SessionSave.getSession("travel_status", SplashAct.this).equals("5")) {
                                            i = new Intent(SplashAct.this, TripHistoryAct.class);
                                            startActivity(i);
                                            finish();
                                        } else {
                                            i = new Intent(SplashAct.this, OngoingAct.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    }
                                }

                            }




                    } else if (json.getInt("status") == 0) {
                        //no changes made
                    } else if (json.getInt("status") == -101) {
                        if (json.has("message"))
                            forceLogout(json.getString("message"));
                        else
                            forceLogout(NC.getString(R.string.server_error));
                    } else {
                        errorInSplash(json.getString("message"));
                    }
                } catch (final JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    errorInSplash(NC.getString(R.string.server_error));
                } catch (final NullPointerException e) {
                    // TODO: handle exception
                    e.printStackTrace();
                    errorInSplash(NC.getString(R.string.server_error));
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                    //errorInSplash(NC.getString(R.string.server_error));
                }
            } else {
                dialog1 = Utils.alert_view_dialog(SplashAct.this, NC.getString(R.string.message), NC.getString(R.string.server_error), NC.getString(R.string.c_tryagain), NC.getString(R.string.cancel), false, (dialog, which) -> {
                    dialog.dismiss();
                    String url = "type=getcoreconfig";
                    new CoreConfigCall(url);
                }, (dialog, which) -> {
                    dialog.dismiss();
                    final Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }, "");
            }
        }

    }

   private void  getlocationdilaog()
    {

        dialog1 = Utils.alert_view_dialog(SplashAct.this, NC.getResources().getString(R.string.version_up_title), "Onepay trip taxi collects location to enable identication current location even when the app is closed or not in use.", "Accept", "Deny", false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(SplashAct.this, DevicePermissionActivity.class);
                startActivity(i);
                finish();
                dialog.dismiss();
            }
        }, (dialog, which) -> {
            dialog.dismiss();
            finish();
        }, "");

    }
}
