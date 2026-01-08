package com.onepaytaxi.driver.data;

import android.app.ActivityManager;
import android.content.Context;

import com.onepaytaxi.driver.service.LocationUpdate;
import com.onepaytaxi.driver.tracklocation.DriverData;
import com.onepaytaxi.driver.utils.SessionSave;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;

//Class for common variable which used in entire project.
public class CommonData {
    public static final String DRIVER_LOCATION_STATIC = "driver_location";
    public static final String IS_FROM_EARNINGS = "from_earnings";
    public static final String SHOW_TOOLTIP = "show_tooltip";
    public static final String LAST_KNOWN_LAT = "lastknowlats";
    public static final String LAST_KNOWN_LONG = "lastknowlngs";
    public static final String SOS_ENABLED = "enable_emergency_button";
    public static final String CALL_MASKING_ENABLED = "enable_call_masking";

    public static final String SOS_LAST_LAT = "sos_last_lat";
    public static final String SOS_LAST_LNG = "sos_last_lng";
    public static final String LAST_FORCEUPDATE_VERSION = "last_forceupdate_version";
    public static final String IS_BANKID_MANTATORY = "is_bank_card_id_mantatory";
    public static final String PLAY_STORE_LINK = "play_store_link";
    public static final String SHOW_PROFILE_TOOLTIP = "show_profile_tooltip";
    public static final String GETCORE_LASTUPDATE = "getcore_lastupdate";
    public static final String ACTIVITY_BG = "activity_bg";
    public static final String ACCESS_KEY = "access_keyu";
    public static final String TIMEZONE = "timezone";
    public static final String SHOW_CANCEL_BUTTON = "driver_noshow_time";
    public static final String DRIVER_ARRIVED_TIME = "driver_arrived_time";
    public static final String FIREBASE_KEY = "firebase_token";
    public static final String ERROR_LOGS = "error_logs";
    public static String isNeedtoDrawRoute = "isNeedtoDrawRoute";
    public static String isNeedtofetchAddress = "isNeedtofetchAddress";
    public static String isGoogleRoute = "isGoogleRoute";
    public static String isGoogleGeocoder = "isGoogleGeocoder";
    public static final String PASSENGER_LANGUAGE_TIME = "passenger_lang";
    public static final String PASSENGER_COLOR_TIME = "driver_lang";
    public static final String PLAN_TYPE = "commission_subscription";
    public static final String IS_STREET_PICKUP = "is_street_pickupp";
    public static final String SHIFT_OUT = "shift_out";
    public static final String LOGOUT = "log_out";
    public static final String isGoogleDistance = "isgoogle_distance";
    public static final String MAP_BOX_TOKEN = "MAP_BOX_TOKEN";
    public static final String GOOGLE_KEY = "android_web_key";
    public static final String LOCAL_STORAGE = "local_storage";
    public static final String NODE_URL = "node_url";
    public static final String NODE_DOMAIN = "node_domain";
    public static final String NODE_TOKEN = "node_token";
    public static final String AMOUNT_USED_FROM_WALLET = "amount_used_from_wallet";
    public static final String SERVICE_STOPPED_TIME = "serviceStoppedTime";
    public static final String HELP_URL = "driver_app_help_url";
    public static final String SKIP_DRIVER_EMAIL = "skip_driver_email";
    public static final String DEVICE_TOKEN = "device_token";
    public static final String DRIVER_LOCATION = "Driver_locations";
    public static final String WAITING_TIME = "waitingtime_check";
    public static final String ST_WAITING_TIME = "streetPickup_waitingtime_check";
    //auto=1 manual=0
    public static final String WAITING_TIME_MANUAL = "streetPickup_waitingtime_manual";
    public static final String WAITING_TIME_START_STOP = "start_stop_waiting_time";
    public static final String WAITING_TIME_START = "START";
    public static final String WAITING_TIME_STOP = "STOP";
    public static final String FINAL_WAITING_TIME = "finalTime";
    public static final String HIDE_OTP = "sms_status";


    public static String mDevice_id =  "";
    public static String mDevice_id_constant = "123456";
    public static double last_getlatitude = 0.0;
    public static double last_getlongitude = 0.0;
    public static double travel_km = 0.0;
    public static String current_act;
    public static int km_calc = 0;
    public static AppCompatActivity sContext;
    public static ArrayList<AppCompatActivity> mActivitylist = new ArrayList<>();
    public static int LocationResult = 420;
    public static int isGoogleGeocode = 1;
    public static int isMapboxGeocode = 2;

    public static final String AUTH_KEY = "auth_key";
    public static final String DEVICE_ID = "device_id";
    public static final String COMPANY_DOMAIN = "company_main_domain";
    public static final String DOMAIN_URL = "domain_url";
    public static final String USER_KEY = "user_key";

    public static final String IS_CORPORATE_BOOKING = "corporate_booking";


    public static final String CURRENT_LAT = "LAST_LAT";
    public static final String CURRENT_LNG = "LAST_LNG";

    public static final String DRIVER_RESULT = "driver_result";
    public static int current_trip_accept = 0;
    public static ArrayList<DriverData> mDrivermovementdata = new ArrayList<>();
    public static ArrayList<DriverData> mDriverdata = new ArrayList<DriverData>();


    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public static boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (LocationUpdate.class.getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }
/*

    public static boolean isCurrentTimeZone(long s) {
//        long dateInMillis = System.currentTimeMillis() / 1000;
//        Systems.out.println("____________LLL" + s + "__" + dateInMillis + "__" + TimeUnit.MILLISECONDS.toSeconds(Math.abs(s - dateInMillis)));
//        return TimeUnit.MILLISECONDS.toSeconds(Math.abs(s - dateInMillis)) < 24;

        return true;
    }
*/
    public static String getTime(Context context) {
        TimeZone.setDefault(TimeZone.getTimeZone(SessionSave.getSession(CommonData.TIMEZONE, context)));
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH", Locale.ENGLISH);
        return sdf2.format(date);
    }

    public static String getCurrentTimeForLogger() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        return df.format(c.getTime());
    }

    public static String getCurrentTimeForDatePickerInMillis() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        return df.format(c.getTimeInMillis());
    }

    public static String getCurrentTimeForFourSquare() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.UK);
        return df.format(c.getTime());
    }

    public static String getDateForWaitingTime(long d) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(d);
    }

    public static String getDateForCreateImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "JPEG_" + timeStamp + "_";
    }

    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.UK);
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }


    public static String getTimeStampFromDateTime(String startTime) {

        StringTokenizer tk = new StringTokenizer(startTime);
        String date = tk.nextToken();
        String time = tk.nextToken();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a");
        Date dt;
        try {
            dt = sdf.parse(time);
            return sdfs.format(dt);
        } catch (ParseException e) {
            e.printStackTrace();
            return startTime;
        }


    }

}