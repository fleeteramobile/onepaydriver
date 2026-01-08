package com.onepaytaxi.driver.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.android.gms.maps.model.LatLng;
import com.onepaytaxi.driver.data.WayPointsData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This common class to store the require data by using SharedPreferences.
 */
public class SessionSave {
    public static void saveSession(String key, String value, Context context) {
        if (context != null) {
            Editor editor = context.getSharedPreferences("KEY", AppCompatActivity.MODE_PRIVATE).edit();

            editor.putString(key, value);
            editor.apply();
        }
    }

    public static void clearAllSession(Context context) {
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences("KEY", AppCompatActivity.MODE_PRIVATE);
            prefs.getAll().clear();
        }
    }

    public static String getSession(String key, Context context) {
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences("KEY", AppCompatActivity.MODE_PRIVATE);

            return prefs.getString(key, "");
        }
        return "";
    }

    public static void clearSession(Context context) {
        Editor editor = context.getSharedPreferences("KEY", AppCompatActivity.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    public static void setDistance(double distance, Context con) {
        if (con != null) {
            Editor editor = con.getSharedPreferences("DIS", Context.MODE_PRIVATE).edit();
            editor.putFloat("DISTANCE", (float) distance);
            editor.commit();
        }
    }

    public static void setDistanceCityLimit(double distance, Context con) {
        if (con != null) {
            Editor editor = con.getSharedPreferences("DIS", Context.MODE_PRIVATE).edit();
            editor.putFloat("DISTANCE", (float) distance);
            editor.commit();
        }
    }

    public static void setArriveDistance(double distance, Context con) {
        if (con != null) {
            Editor editor = con.getSharedPreferences("DIS", Context.MODE_PRIVATE).edit();
            editor.putFloat("ARR_DISTANCE", (float) distance);
            editor.commit();
        }
    }

    public static void setGoogleDistance(double distance, Context con) {
        if (con != null) {
            Editor editor = con.getSharedPreferences("GDIS", Context.MODE_PRIVATE).edit();
            editor.putFloat("GDISTANCE", (float) distance);
            editor.commit();
        }
    }

    public static float getGoogleDistance(Context con) {
        DecimalFormat df = new DecimalFormat(".###");
        return Float.parseFloat((getGoogleDistanceString(con)));
    }

    public static float getDistance(Context con) {
        DecimalFormat df = new DecimalFormat(".###");
        return Float.parseFloat((getDistanceString(con)));
    }

    public static float getDistanceCityLimit(Context con) {
        DecimalFormat df = new DecimalFormat(".###");
        return Float.parseFloat((getDistanceString(con)));
    }


    public static float getArriveDistance(Context con) {
        DecimalFormat df = new DecimalFormat(".###");
        return Float.parseFloat((getDistanceString(con)));
    }


    public static String getGoogleDistanceString(Context con) {
        SharedPreferences sharedPreferences = con.getSharedPreferences("GDIS", Context.MODE_PRIVATE);
        return String.format(Locale.UK, "%.2f", sharedPreferences.getFloat("GDISTANCE", 0));
    }

    public static String getDistanceString(Context con) {
        SharedPreferences sharedPreferences = con.getSharedPreferences("DIS", Context.MODE_PRIVATE);
        return String.format(Locale.UK, "%.2f", sharedPreferences.getFloat("DISTANCE", 0));
    }

    public static void setWaitingTime(Long time, Context con) {
        Editor editor = con.getSharedPreferences("long", Context.MODE_PRIVATE).edit();
        editor.putLong("LONG", time);
        editor.commit();
    }

    public static void saveSession(String key, boolean value, Context context) {
        Editor editor = context.getSharedPreferences("KEY", AppCompatActivity.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getSession(String key, Context context, boolean a) {
        SharedPreferences prefs = context.getSharedPreferences("KEY", AppCompatActivity.MODE_PRIVATE);
        return prefs.getBoolean(key, false);
    }

    public static long getWaitingTime(Context con) {
        SharedPreferences sharedPreferences = con.getSharedPreferences("long", Context.MODE_PRIVATE);
        return sharedPreferences.getLong("LONG", 0);

    }


    public static void saveGoogleWaypoints(LatLng start, LatLng dest, String source, double dist, String error, Context mContext) {


        SharedPreferences prefs = mContext.getSharedPreferences("wayPoints", 0);

        Editor editor = prefs.edit();
        if (start != null) {


            JSONArray jsonArray = ReadGoogleWaypoints(mContext);

            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dist", dist);
                jsonObject.put("error", error);
                jsonObject.put("source", source);
                jsonObject.put("pickuplat", start.latitude);
                jsonObject.put("pickuplng", start.longitude);
                jsonObject.put("droplat", dest.latitude);
                jsonObject.put("droplng", dest.longitude);
                jsonArray.put(jsonObject);
                Systems.out.println("waypoints storing" + jsonArray);
                editor.putString("wayPoints", jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else
            editor.clear();
        editor.commit();
    }

    public static JSONArray ReadGoogleWaypoints(Context mContext) {

        JSONArray jsonArray = new JSONArray();
        try {
            WayPointsData[] wayPointsData;
            SharedPreferences prefs = mContext.getSharedPreferences("wayPoints", 0);
            jsonArray = new JSONArray(prefs.getString("wayPoints", "[]"));
            Systems.out.println("waypoints reading" + jsonArray);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    public static void saveWaypoints(LatLng start, LatLng dest, String source, double dist, String error, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("localwayPoints", 0);

        Editor editor = prefs.edit();
        if (start != null) {

            JSONArray jsonArray = ReadWaypoints(mContext);

            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dist", dist);
                jsonObject.put("error", error);
                jsonObject.put("source", source);
                jsonObject.put("pickuplat", start.latitude);
                jsonObject.put("pickuplng", start.longitude);
                jsonObject.put("droplat", dest.latitude);
                jsonObject.put("droplng", dest.longitude);

                jsonObject.put("trip_id", SessionSave.getSession("trip_id", mContext));
                jsonObject.put("time", DateFormat.getTimeInstance().format(new Date()));
                jsonArray.put(jsonObject);
                Systems.out.println("waypoints storing" + jsonArray);
                editor.putString("localwayPoints", jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else
            editor.clear();
        editor.commit();
    }

    public static JSONArray ReadWaypoints(Context mContext) {

        JSONArray jsonArray = new JSONArray();
        try {
            SharedPreferences prefs = mContext.getSharedPreferences("localwayPoints", 0);
            jsonArray = new JSONArray(prefs.getString("localwayPoints", "[]"));
            Systems.out.println("waypoints reading" + jsonArray);
            for (int i = 0; i < ReadGoogleWaypoints(mContext).length(); i++) {
                JSONObject jj = ReadGoogleWaypoints(mContext).getJSONObject(i);
                jsonArray.put(jj);
            }

            String savingTripDetail = jsonArray.toString();
            SessionSave.saveSession(SessionSave.getSession("trip_id", mContext) + "data", savingTripDetail, mContext);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    public static void saveLastLng(LatLng ll, Context con) {
        if (ll != null && ll.latitude != 0.0) {
            double lastLat = ll.latitude;
            double lastLng = ll.longitude;
            Editor editor = con.getSharedPreferences("nlastlong", Context.MODE_PRIVATE).edit();
            editor.putString("nLat", String.valueOf(lastLat));
            editor.putString("nLng", String.valueOf(lastLng));
            editor.commit();
        }
    }

    public static LatLng getLastLng(Context con) {

        double nLat = 0.0;
        double nLng = 0.0;
        SharedPreferences preferences = con.getSharedPreferences("nlastlong", Context.MODE_PRIVATE);
        if (!preferences.getString("nLat", "").equals("")) {
            Systems.out.println("LassstString" + SessionSave.getSession("nLat", con));
            nLat = Double.parseDouble(preferences.getString("nLat", ""));
            nLng = Double.parseDouble(preferences.getString("nLng", ""));
        }
        Systems.out.println("getLastLat" + preferences.getString("nLat", ""));
        return new LatLng(nLat, nLng);
    }

    public static void saveSessionOneTime(String key, String value, Context context) {
        if (context != null) {
            Editor editor = context.getSharedPreferences("KEY_ONE_TIME", AppCompatActivity.MODE_PRIVATE).edit();
            editor.putString(key, value);
            editor.commit();
        }
    }

    public static String getSessionOneTime(String key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("KEY_ONE_TIME", AppCompatActivity.MODE_PRIVATE);
        String s = prefs.getString(key, "");
        return s == null ? "" : s;
    }

    public static void ClearSessionOneTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("KEY_ONE_TIME", AppCompatActivity.MODE_PRIVATE);
        prefs.edit().clear();
    }


    public static JSONArray ReadGoogleWaypointsWithId(Context mContext, String id) {

        JSONArray jsonArray = new JSONArray();
        try {
            WayPointsData[] wayPointsData;
            SharedPreferences prefs = mContext.getSharedPreferences("wayPoints", 0);
            jsonArray = new JSONArray(prefs.getString("wayPoints" + id, "[]"));
            Systems.out.println("waypoints reading" + jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    public static void saveGoogleWaypointsWithId(LatLng start, LatLng dest, String source, double dist, String id, Context mContext) {


        SharedPreferences prefs = mContext.getSharedPreferences("wayPoints", 0);

        Editor editor = prefs.edit();
        if (start != null) {
            JSONArray jsonArray = ReadGoogleWaypointsWithId(mContext, id);

            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dist", dist);
                jsonObject.put("ID", id);
                jsonObject.put("source", source);
                jsonObject.put("pickuplat", start.latitude);
                jsonObject.put("pickuplng", start.longitude);
                jsonObject.put("droplat", dest.latitude);
                jsonObject.put("droplng", dest.longitude);
                jsonArray.put(jsonObject);
                Systems.out.println("waypoints storing" + jsonArray);
                editor.putString("wayPoints" + id, jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else
            editor.clear();
        editor.commit();
    }

}
