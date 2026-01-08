package com.onepaytaxi.driver;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.onepaytaxi.driver.Login.DriverLoginActivity;
import com.onepaytaxi.driver.data.CommonData;
import com.onepaytaxi.driver.data.MystatusData;
import com.onepaytaxi.driver.interfaces.APIResult;
import com.onepaytaxi.driver.interfaces.ClickInterface;
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON;
import com.onepaytaxi.driver.service.LocationUpdate;
import com.onepaytaxi.driver.service.ServiceGenerator;
import com.onepaytaxi.driver.utils.CL;

import com.onepaytaxi.driver.utils.FontHelper;
import com.onepaytaxi.driver.utils.GpsStatus;
import com.onepaytaxi.driver.utils.NC;
import com.onepaytaxi.driver.utils.NetworkStatus;
import com.onepaytaxi.driver.utils.SessionSave;
import com.onepaytaxi.driver.utils.Systems;
import com.onepaytaxi.driver.utils.Utils;


import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is the parent abstract class for all other activities
 */
public abstract class MainActivity extends BaseActivity implements ClickInterface {
    public static MystatusData mMyStatus;
    public static Dialog mgpsDialog;
    public static Dialog mshowDialog;
    public static String APP_VERSION = "";
    public static MainActivity context;
    public final String TAG = getClass().getSimpleName();
    public NetworkStatus networkStatus;
    public GpsStatus gpsStatus;
    public Dialog alertDialog;
    Bundle BsavedInstanceState;
    Dialog dialog1;

    private static final int MY_PERMISSIONS_REQUEST_GPS = 111;


    /**
     * clear all driver session variables used except getcoreconfig details
     *
     * @param ctx - Context
     */
    public static void clearsession(Context ctx) {

        try {
            SessionSave.saveSession("status", "", ctx);
            SessionSave.saveSession("Id", "", ctx);
            SessionSave.saveSession("Driver_locations", "", ctx);
            SessionSave.saveSession("driver_id", "", ctx);
            SessionSave.saveSession("Name", "", ctx);
            SessionSave.saveSession("company_id", "", ctx);
            SessionSave.saveSession("bookedby", "", ctx);
            SessionSave.saveSession("p_image", "", ctx);
            SessionSave.saveSession("Email", "", ctx);
            SessionSave.saveSession("phone_number", "", ctx);
            SessionSave.saveSession("driver_password", "", ctx);
            SessionSave.saveSession("trip_id", "", ctx);
            SessionSave.setWaitingTime(0L, ctx);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static boolean isNetworkEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }

    /**
     * Showing gps alert enable
     */
    public static void gpsalert(final AppCompatActivity mContext, boolean isconnect) {

        if (!isconnect) {


            if (mContext instanceof SplashAct) {
                LinearLayout sub_can = mgpsDialog.findViewById(R.id.sub_can);
                sub_can.setPadding(0, 10, 0, 10);
            }
            String message = "";
            if (!isNetworkEnabled(mContext))
                message = NC.getString(R.string.location_enable);
            else
                message = NC.getString(R.string.change_network);
            Utils.alert_view_dialog_GPS(mContext, NC.getResources().getString(R.string.location_disable),
                    message,
                    NC.getResources().getString(R.string.enable),
                    "", false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent mIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            mContext.startActivity(mIntent);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    }, "");

        } else {
            try {
                Systems.out.println("________called" + SessionSave.getSession("trip_id", mContext));
                if (!SessionSave.getSession("Id", mContext).trim().equals("")) {
                    LocationUpdate.startLocationService(mContext);
                }
                Utils.closeGPSDialog();
                mgpsDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_GPS) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MainActivity.this, LocationUpdate.class);
                    stopService(intent);
                    final Intent i = new Intent(MainActivity.this, SplashAct.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } else {
                    finish();
                }
            }
        }
    }


    public static boolean isGpsEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Storing color values from server to local hashmap
     *
     * @param result -> response from color file url got from company domain response
     */
    private synchronized void getAndStoreColorValues(String result) {
//        try {
//
//
//            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//            InputStream is = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
//
//
//            Document doc = dBuilder.parse(is);
//            Element element = doc.getDocumentElement();
//            element.normalize();
//
//            NodeList nList = doc.getElementsByTagName("*");
//
//            Systems.out.println("lislength" + nList.getLength());
//            for (int i = 0; i < nList.getLength(); i++) {
//                Node node = nList.item(i);
//                if (node.getNodeType() == Node.ELEMENT_NODE) {
//                    Element element2 = (Element) node;
//                    CL.nfields_byName.put(element2.getAttribute("name"), element2.getTextContent());
//                }
//            }
//
//            getColorValueDetail();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Storing String values from server to local hashmap
     *
     * @param result -> response from String file url got from company domain response
     */
    private synchronized void getAndStoreStringValues(String result) {
//        try {
//
//
//            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//            InputStream is = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
//            Document doc = dBuilder.parse(is);
//            Element element = doc.getDocumentElement();
//            element.normalize();
//
//            NodeList nList = doc.getElementsByTagName("*");
//
//            for (int i = 0; i < nList.getLength(); i++) {
//
//                Node node = nList.item(i);
//                if (node.getNodeType() == Node.ELEMENT_NODE) {
//                    Element element2 = (Element) node;
//                    NC.nfields_byName.put(element2.getAttribute("name"), element2.getTextContent());
//                }
//            }
//            getValueDetail();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Getting String values Splits string , key ,id and save in hashmap.
     */
    synchronized void getValueDetail() {
//        Field[] fieldss = R.string.class.getDeclaredFields();
//        for (Field field : fieldss) {
//            int id = getResources().getIdentifier(field.getName(), "string", getPackageName());
//            if (NC.nfields_byName.containsKey(field.getName())) {
//                NC.fields.add(field.getName());
//                NC.fields_value.add(getResources().getString(id));
//                NC.fields_id.put(field.getName(), id);
//
//            }
//        }
//
//
//        for (Map.Entry<String, String> entry : NC.nfields_byName.entrySet()) {
//            String h = entry.getKey();
//            NC.nfields_byID.put(NC.fields_id.get(h), NC.nfields_byName.get(h));
//            // do stuff
//        }

    }

    /**
     * Getting Color values Splits color , key ,id and save in hashmap.
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
            // do stuff
        }

    }

    /**
     * This is method for set up the base data for the child activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        NetworkStatus.appContext = this;
        mMyStatus = new MystatusData(MainActivity.this);
        BsavedInstanceState = savedInstanceState;
        context = this;
        networkStatus = new NetworkStatus();
        gpsStatus = new GpsStatus();
        registerReceiver(networkStatus, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(gpsStatus, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        // TODO: Move this to where you establish a user session
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {


                if (!(SessionSave.getSession("wholekeyColor", MainActivity.this).trim().equals(""))) {
                    getAndStoreStringValues(SessionSave.getSession("wholekey", MainActivity.this));
                    getAndStoreColorValues(SessionSave.getSession("wholekeyColor", MainActivity.this));
                }
                if (SessionSave.getSession("base_url", MainActivity.this).trim().equals("")) {
                    ServiceGenerator.API_BASE_URL = SessionSave.getSession("base_url", MainActivity.this);
                    getAndStoreStringValues(SessionSave.getSession("wholekey", MainActivity.this));
                    getAndStoreColorValues(SessionSave.getSession("wholekeyColor", MainActivity.this));
                }

/*
                if (APP_VERSION == null) {
                    APP_VERSION = BuildConfig.VERSION_NAME;
                }*/

            }
        }, 200);

        //  requestWindowFeature(Window.FEATURE_NO_TITLE);
        int view = setLayout();
        setLocale();
        if (view != 0) {
            setContentView(view);
            if (MainActivity.this != null) {
                Initialize();
                try {
                    if (mshowDialog != null && mshowDialog.isShowing())
                        mshowDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Abstract method to set layout
     */
    public abstract int setLayout();

    /**
     * Abstract method to initialize variable
     */
    public abstract void Initialize();

    /**
     * This is method for show the toast
     */
    public void ShowToast(Context contex, String message) {
        if (contex != null && message != null) {
            Toast toast = Toast.makeText(contex, message, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * This is method for show the Log
     */
    public void showLog(String msg) {

        Log.i(TAG, msg);
    }

    /**
     * This is method for check the mail is valid by the use of regex class.
     */
    public boolean validdmail(String string) {
        // TODO Auto-generated method stub
        boolean isValid = false;
        String expression = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@" + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\." + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|" + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(string);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * This is method for check the Internet connection
     */
    public boolean isOnline() {

        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo networkInfo : info)
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    /**
     * This is method for show progress bar over all activity
     */
    public void showLoading(Context context) {

        try {
            if (mshowDialog != null)
                if (mshowDialog.isShowing())
                    mshowDialog.dismiss();
            View view = View.inflate(context, R.layout.progress_bar, null);
            mshowDialog = new Dialog(context, R.style.dialogwinddow);
            mshowDialog.setContentView(view);
            mshowDialog.setCancelable(false);

            mshowDialog.show();

            ImageView iv = mshowDialog.findViewById(R.id.giff);
            DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(iv);
            Glide.with(MainActivity.this)
                    .load(R.raw.loading_anim)
                    .into(imageViewTarget);

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * This is method for convert the string value into MD5
     *
     * @param pass - String to convert to MD5
     */
    public String convertPassMd5(String pass) {

        String password = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(pass.getBytes(), 0, pass.length());
            pass = new BigInteger(1, mdEnc.digest()).toString(16);
            while (pass.length() < 32) {
                pass = "0" + pass;
            }
            password = pass;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return password;
    }

    /**
     * This is method for logout the user from their current session.
     *
     * @param context
     */
    public void logout(final Context context) {

        dialog1 = Utils.alert_view(MainActivity.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.confirmlogout), NC.getResources().getString(R.string.m_logout), NC.getResources().getString(R.string.cancel), true, MainActivity.this, "5");





    }

    @Override
    public void positiveButtonClick(DialogInterface dialog, int id, String s) {
        switch (s) {
            case "1":
                dialog.dismiss();
                JSONObject j = new JSONObject();
                try {
                    j.put("driver_id", SessionSave.getSession("Id", context));
                    j.put("shiftupdate_id", SessionSave.getSession("Shiftupdate_Id", context));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String url = "type=user_logout";
                new Logout(url, j);
                break;
            case "2":
                dialog.dismiss();
                break;
            case "3":
                dialog.dismiss();
                Intent i = new Intent(MainActivity.this, OngoingAct.class);
                startActivity(i);
                break;
            case "5":
                dialog.dismiss();
                try {
                    JSONObject js = new JSONObject();

                    js.put("driver_id", SessionSave.getSession("Id", context));
                    js.put("shiftupdate_id", SessionSave.getSession("Shiftupdate_Id", context));
                    String urls = "type=user_logout";
                    new Logout(urls, js);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case "7":
                dialog.dismiss();
//                int length = CommonData.mActivitylist.size();
//                if (length != 0) {
//                    for (int jv = 0; jv < length;jv++) {
//                        CommonData.mActivitylist.get(jv).finish();
//                    }
//                }
                Intent intent = new Intent(MainActivity.this, DriverLoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void negativeButtonClick(DialogInterface dialog, int id, String s) {
        dialog.dismiss();
    }

    /**
     * This is method for set the language configuration.
     */
    public void setLocale() {
        if (SessionSave.getSession("Lang", MainActivity.this).equals("")) {
            SessionSave.saveSession("Lang", "en", MainActivity.this);
            SessionSave.saveSession("Lang_Country", "en_GB", MainActivity.this);
        }
        Systems.out.println("Lang" + SessionSave.getSession("Lang", MainActivity.this));
        Systems.out.println("Lang_Country" + SessionSave.getSession("Lang_Country", MainActivity.this));
        Configuration config = new Configuration();
        String langcountry = SessionSave.getSession("Lang_Country", MainActivity.this);
        String language = SessionSave.getSession("Lang", MainActivity.this);
        String[] arry = langcountry.split("_");
        config.locale = new Locale(language, arry[1]);
        Locale.setDefault(new Locale(language, arry[1]));
        MainActivity.this.getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    /**
     * Method to show Gcm notification
     */
    public void checkGCM() {
        String dialogMessage = SessionSave.getSession("GCMnotification", this);
        try {
            if (dialogMessage != null && !dialogMessage.trim().equals("")) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the activity comes to onresume state.
     * Check whether driver is logged in or not
     * if id is empty userlogin activity is called.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (MainActivity.this != null) {

            NetworkStatus.isOnline(MainActivity.this);


        }


    }

    /**
     * Custom alert dialog used in entire project.can call from anywhere with the following param Context,title,message,success and failure button text.
     */
    //not used...
    public void alert_view(Context mContext, String title, String message, String success_txt, String failure_txt) {
        if (alertDialog != null)
            if (alertDialog.isShowing())
                alertDialog.dismiss();
        final View view = View.inflate(mContext, R.layout.alert_view, null);

        alertDialog = new Dialog(mContext, R.style.NewDialog);
        alertDialog.setContentView(view);
        alertDialog.setCancelable(true);
        FontHelper.applyFont(mContext, alertDialog.findViewById(R.id.alert_id));

        alertDialog.show();
        final TextView title_text = alertDialog.findViewById(R.id.title_text);
        final TextView message_text = alertDialog.findViewById(R.id.message_text);
        final Button button_success = alertDialog.findViewById(R.id.button_success);
        final Button button_failure = alertDialog.findViewById(R.id.button_failure);
        button_failure.setVisibility(View.GONE);
        title_text.setText(title);
        message_text.setText(message);
        button_success.setText(success_txt);
        button_success.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(networkStatus);
        unregisterReceiver(gpsStatus);
        if (dialog1 != null)
            Utils.closeDialog(dialog1);
        super.onDestroy();
    }

    /**
     * Cancel dialog Loading
     */
    public void cancelLoading() {
        try {
            if (mshowDialog != null)
                if (mshowDialog.isShowing() && MainActivity.this != null)
                    mshowDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This is method to validate the field like Mail,Password,Name,Salutation etc and show the appropriate alert message.
     *
     * @param con              -context
     * @param VA               -validation action element
     * @param stringtovalidate -String to validate
     */
    public boolean validations(ValidateAction VA, AppCompatActivity con, String stringtovalidate) {

        String message = "";
        boolean result = false;
        switch (VA) {
            case isValueNULL:
                if (TextUtils.isEmpty(stringtovalidate))
                    message = NC.getResources().getString(R.string.enter_mobile_number);
                else
                    result = true;
                break;
            case isValidPassword:
                if (TextUtils.isEmpty(stringtovalidate))
                    message = NC.getResources().getString(R.string.enter_the_password);
                else if (stringtovalidate.length() < 5)
                    message = NC.getResources().getString(R.string.pwd_min);
                else if (stringtovalidate.length() > 32)
                    message = NC.getResources().getString(R.string.s_pass_max);
                else
                    result = true;
                break;
            case isValidFirstname:
                if (TextUtils.isEmpty(stringtovalidate))
                    message = NC.getResources().getString(R.string.enter_the_first_name);
                else
                    result = true;
                break;
            case isValidLastname:
                if (TextUtils.isEmpty(stringtovalidate))
                    message = NC.getResources().getString(R.string.enter_the_last_name);
                else
                    result = true;
                break;
            case isValidCard:
                if (TextUtils.isEmpty(stringtovalidate))
                    message = NC.getResources().getString(R.string.enter_the_card_number);
                else if (stringtovalidate.length() < 9 || stringtovalidate.length() > 16)
                    message = NC.getResources().getString(R.string.enter_the_valid_card_number);
                else
                    result = true;
                break;
            case isValidExpiry:
                if (TextUtils.isEmpty(stringtovalidate))
                    message = NC.getResources().getString(R.string.enter_the_expiry_date);
                else
                    result = true;
                break;
            case isValidMail:
                if (TextUtils.isEmpty(stringtovalidate))
                    message = NC.getResources().getString(R.string.enter_the_email);
                else if (!validdmail(stringtovalidate))
                    message = NC.getResources().getString(R.string.enter_the_valid_email);
                else
                    result = true;
                break;
            case isValidConfirmPassword:
                if (TextUtils.isEmpty(stringtovalidate))
                    message = NC.getResources().getString(R.string.enter_the_confirmation_password);
                else
                    result = true;
                break;
        }
        if (!message.equals("")) {
            dialog1 = Utils.alert_view(con, NC.getResources().getString(R.string.message), message, NC.getResources().getString(R.string.ok), "", true, MainActivity.this, "2");
        }
        return result;
    }

    /**
     * Called when activity is
     * Close all dialog to avoid memory leakage error
     */
    @Override
    protected void onStop() {
        Utils.closeDialog(mgpsDialog);
        Utils.closeDialog(mshowDialog);
        super.onStop();
    }

    /**
     * Enum class for validation
     */
    public enum ValidateAction {
        NONE, isValueNULL, isValidPassword, isValidSalutation, isValidFirstname, isValidLastname, isValidCard, isValidExpiry, isValidMail, isValidConfirmPassword
    }

    /**
     * This is class for logout API call and process the response
     * Clear their current session.
     */
    private class Logout implements APIResult {
        public Logout(String url, JSONObject data) {

            Systems.out.println(url);
            Systems.out.println(String.valueOf(data));
            if (isOnline()) {
                new APIService_Retrofit_JSON(MainActivity.this, this, data, false).execute(url);
            } else {
                dialog1 = Utils.alert_view(MainActivity.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.please_check_internet), NC.getResources().getString(R.string.ok), "", true, MainActivity.this, "2");
            }
        }

        @Override
        public void getResult(boolean isSuccess, final String result) {

            if (isSuccess) {
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        Intent locationService = new Intent(MainActivity.this, LocationUpdate.class);
                        stopService(new Intent(locationService));
                        clearsession(MainActivity.this);
                     //   dialog1 = Utils.alert_view(MainActivity.this, NC.getResources().getString(R.string.message),json.getString("message"), NC.getResources().getString(R.string.ok), "", true, MainActivity.this, "7");
                int length = CommonData.mActivitylist.size();
                if (length != 0) {
                    for (int jv = 0; jv < length;jv++) {
                        CommonData.mActivitylist.get(jv).finish();
                    }
                }
                        Intent intent = new Intent(MainActivity.this, DriverLoginActivity.class);
                        startActivity(intent);
                        finish();
//                        dialog1 = Utils.alert_view_dialog(MainActivity.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", false, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        }, (dialog, which) -> dialog.dismiss(), "");
                    } else if (json.getInt("status") == -4) {
                        if (json.has("trip_id")) {
                            SessionSave.saveSession("trip_id", json.getString("trip_id"), MainActivity.this);
                            dialog1 = Utils.alert_view(MainActivity.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", true, MainActivity.this, "3");
                        }
                    } else {
                        dialog1 = Utils.alert_view(MainActivity.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", true, MainActivity.this, "2");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
               // runOnUiThread(() -> ShowToast(MainActivity.this, NC.getString(R.string.server_error)));
            }
        }
    }


}