package com.mayan.sospluginmodlue.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * This common class to store the require data by using SharedPreferences.
 */
public class SessionSave {
    public static void saveSession(String key, String value, Context context) {
        if (context != null) {
            Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();

            editor.putString(key, value);
            editor.commit();
        }
    }
    public static void clearSession(Context context) {
        Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }
    public static void clearAllSession(Context context) {
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE);
            prefs.getAll().clear();
        } else {
        }
    }
    public static String getSession(String key, Context context) {
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE);

            return prefs.getString(key, "");
        }
        return "";
    }

    public static void setDistance(double distance, Context con) {
        if (con != null) {
            Editor editor = con.getSharedPreferences("DIS", Context.MODE_PRIVATE).edit();
            editor.putFloat("DISTANCE", (float) distance);
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
//		SharedPreferences sharedPreferences=con.getSharedPreferences("DIS", con.MODE_PRIVATE);
        return Float.parseFloat((getGoogleDistanceString(con)));
    }

    //	public static float getDistance(Context con)
//	{
//		SharedPreferences sharedPreferences=con.getSharedPreferences("DIS", con.MODE_PRIVATE);
//		return sharedPreferences.getFloat("DISTANCE", 0);
//
//	}
    public static float getDistance(Context con) {
        DecimalFormat df = new DecimalFormat(".###");
//		SharedPreferences sharedPreferences=con.getSharedPreferences("DIS", con.MODE_PRIVATE);
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
        Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getSession(String key, Context context, boolean a) {
        SharedPreferences prefs = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE);
        return prefs.getBoolean(key, false);
    }

    public static long getWaitingTime(Context con) {
        SharedPreferences sharedPreferences = con.getSharedPreferences("long", Context.MODE_PRIVATE);
        return sharedPreferences.getLong("LONG", 0);

    }








    public static void saveSessionOneTime(String key, String value, Context context) {
        if (context != null) {
            Editor editor = context.getSharedPreferences("KEY_ONE_TIME", Activity.MODE_PRIVATE).edit();
            editor.putString(key, value);
            editor.commit();
        }
    }

    public static String getSessionOneTime(String key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("KEY_ONE_TIME", Activity.MODE_PRIVATE);
        String s = prefs.getString(key, "");
        return s == null ? "" : s;
    }

    public static void ClearSessionOneTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("KEY_ONE_TIME", Activity.MODE_PRIVATE);
        prefs.edit().clear();
    }

}
