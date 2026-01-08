package com.onepaytaxi.driver.utils;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.onepaytaxi.driver.MainActivity;
import com.onepaytaxi.driver.data.CommonData;

/**
 * This class is used to get the network status when it is enable/disable.
 */
public class GpsStatus extends BroadcastReceiver {
    public static Dialog mDialog;
    public static int count = 0;
    public static int ischecked = 1;
    public Context mContext;
    private String message;

    @Override
    public void onReceive(Context context, Intent intent) {
        Systems.out.println("Gps Receiver onReceive");
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            mContext = context;
            try {
                if (!isGpsEnabled(mContext)) {
                    if (count == 0) {
                        message = " Gps connection is Disable!!";
                        if (!CommonData.current_act.equals("SplashAct")) {
                            if (CommonData.sContext != null)
                                MainActivity.gpsalert(CommonData.sContext, false);
                        }
                    }
                    count++;
                } else {
                    count = 0;
                    ischecked = 0;
                    message = " Gps connection is Enable!!";
                    if (CommonData.current_act != null)
                        if (!CommonData.current_act.equals("SplashAct")) {
                            if (CommonData.sContext != null)
                                MainActivity.gpsalert(CommonData.sContext, true);
                        }
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        } else {
            Log.v("GpsStatus", "BroadcastReceiver triggers without android.location.PROVIDERS_CHANGED intent");
        }
    }

    public boolean isGpsEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void createGpsLog(Context context) {
//        ErrorLogRepository.getRepository(context).insertGpsLog(new GpsModel("" + System.currentTimeMillis(), new LatLng(currentLatitude, currentLongtitude), isGpsEnabled(context) ? "YES" : "NO", DriverUtils.INSTANCE.driverInfo(context)));
    }
}
