package com.onepaytaxi.driver;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.android.gms.maps.model.LatLng;
import com.onepaytaxi.driver.data.CommonData;
import com.onepaytaxi.driver.interfaces.DistanceMatrixInterface;
import com.onepaytaxi.driver.interfaces.LocalDistanceInterface;
import com.onepaytaxi.driver.route.FindApproxDistance;
import com.onepaytaxi.driver.utils.DistanceMatrixUtil;
import com.onepaytaxi.driver.utils.SessionSave;
import com.onepaytaxi.driver.utils.Systems;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by developer on 14/2/18.
 */

public class LocalDistanceCalculation implements DistanceMatrixInterface {

    public static LocalDistanceInterface Localdistanceinterface;
    public static Context localcontext;
    private final double slabDistance = 100;

    public static void registerDistanceInterface(LocalDistanceInterface distanceInterface) {
        Localdistanceinterface = distanceInterface;
    }

    public static LocalDistanceCalculation newInstance(Context context) {
        localcontext = context;
        LocalDistanceCalculation localdistance = new LocalDistanceCalculation();
        return localdistance;
    }

    /**
     * This Function is used for calculate the distance travelled
     */
    public synchronized void haversine(final double lat1, final double lon1, final double lat2, final double lon2) {
        // TODO Auto-generated method stub
        //Getting both the coordinates
        LatLng from = new LatLng(lat1, lon1);
        LatLng to = new LatLng(lat2, lon2);


        //Calculating the distance in meters

        double distance = DistanceMatrixUtil.INSTANCE.calculateDistance(SessionSave.getSession("Metric", localcontext).trim(), from, to);

        Systems.out.println("Haversine Distance" + (distance));
        if ((distance * 1000) > slabDistance) {
            new FindApproxDistance(LocalDistanceCalculation.this).getDistance(localcontext, from.latitude, from.longitude, to.latitude, to.longitude);
        } else {
            distance += SessionSave.getDistance(localcontext);
            SessionSave.setDistance(distance, localcontext);
            Localdistanceinterface.haversineResult(true);
            Handler mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    // This is where you do your work in the UI thread.
                    // Your worker tells you in the message what to do.
                    String savingTripDetail = "";
                    savingTripDetail += SessionSave.getSession(SessionSave.getSession("trip_id", localcontext) + "data", localcontext) + "\n\n\n<br><br>" + "Distance**85#&nbsp;" + SessionSave.getDistance(localcontext) + "&nbsp;&nbsp;Trip&nbsp;" + SessionSave.getSession("trip_id", localcontext) + "&nbsp;&nbsp;Speed&nbsp;" + "&nbsp;&nbsp;Time&nbsp;" + DateFormat.getTimeInstance().format(new Date()) +
                            "&nbsp;&nbsp;old&nbsp;&nbsp;" + lat1 + "&nbsp;" + lon1 + "&nbsp;&nbsp;New&nbsp;&nbsp;" + lat2
                            + "&nbsp;" + lon2
                            + "&nbsp;&nbsp;Read way&nbsp;&nbsp;" + SessionSave.ReadGoogleWaypoints(localcontext);

                    SessionSave.saveSession(SessionSave.getSession("trip_id", localcontext) + "data", savingTripDetail, localcontext);

                }
            };
            mHandler.sendEmptyMessage(0);
        }

    }

    @Override
    public void onDistanceCalled(LatLng pick, LatLng drop, double distance, double time, String result, String status) {


        if (status.equalsIgnoreCase("OK")) {
            SessionSave.setGoogleDistance(SessionSave.getGoogleDistance(localcontext) + distance, localcontext);
            SessionSave.saveSession(CommonData.LAST_KNOWN_LAT, String.valueOf(drop.latitude), localcontext);
            SessionSave.saveSession(CommonData.LAST_KNOWN_LONG, String.valueOf(drop.longitude), localcontext);
            if (SessionSave.getSession(CommonData.isGoogleDistance, localcontext, true)) {
                SessionSave.saveGoogleWaypoints(pick, drop, "google", distance, "___" + "LOCAL DISTANCE" + "____" + System.currentTimeMillis(), localcontext);
                SessionSave.saveWaypoints(pick, drop, "google", distance, "server" + "___" + "LOCAL DISTANCE", localcontext);
            } else {
                SessionSave.saveGoogleWaypoints(pick, drop, "mapbox", distance, "___" + "LOCAL DISTANCE", localcontext);
                SessionSave.saveWaypoints(pick, drop, "mapbox", distance, "server" + "___" + "LOCAL DISTANCE", localcontext);
            }
            Localdistanceinterface.haversineResult(true);
        } else {
            SessionSave.setGoogleDistance(distance, localcontext);
            SessionSave.saveGoogleWaypoints(pick, drop, "haversine", distance, "UNKNOWN" + result, localcontext);
            SessionSave.saveSession(CommonData.LAST_KNOWN_LAT, String.valueOf(drop.latitude), localcontext);
            SessionSave.saveSession(CommonData.LAST_KNOWN_LONG, String.valueOf(drop.longitude), localcontext);
            SessionSave.saveWaypoints(pick, drop, "google_haversine", distance, "___" + "LOCAL DISTANCE", localcontext);
            Localdistanceinterface.haversineResult(true);
        }
/*
//        Systems.out.println("haiiiiiii " + "LocalDistanceCalculation " + pick.latitude + "__" + pick.longitude + "_____" + drop.latitude + "__" + drop.longitude + "___" + distance + "____" + status);
        if (status.equalsIgnoreCase("OK")) {
//            SessionSave.setGoogleDistance(SessionSave.getGoogleDistance(localcontext) + distance, localcontext);
            LogUtils.debug("googledistanceee " + "6_" + pick.latitude + "__" + pick.longitude + "_____" + drop.latitude + "__" + drop.longitude);

            String savingTripDetail = "";
            savingTripDetail += SessionSave.getSession(SessionSave.getSession("trip_id", localcontext) + "data", localcontext) + "\n\n\n<br><br>" + "Distance**195#&nbsp;" + SessionSave.getDistance(localcontext) + "&nbsp;&nbsp;Trip&nbsp;" + SessionSave.getSession("trip_id", localcontext) + "&nbsp;&nbsp;Speed&nbsp;" + "&nbsp;&nbsp;Time&nbsp;" + DateFormat.getTimeInstance().format(new Date()) +
                    "&nbsp;&nbsp;old&nbsp;&nbsp;" + pick.latitude + "&nbsp;" + pick.longitude + "&nbsp;&nbsp;New&nbsp;&nbsp;" + drop.latitude
                    + "&nbsp;" + drop.longitude
                    + "&nbsp;&nbsp;Read way&nbsp;&nbsp;" + SessionSave.ReadGoogleWaypoints(localcontext);

            SessionSave.saveSession(SessionSave.getSession("trip_id", localcontext) + "data", savingTripDetail, localcontext);
            if (SessionSave.getSession(CommonData.isGoogleDistance, localcontext, true)) {
                SessionSave.saveGoogleWaypoints(pick, drop, "google", distance, "", localcontext);
                SessionSave.saveWaypoints(pick, drop, "google", distance, "", localcontext);
            } else {
                SessionSave.saveGoogleWaypoints(pick, drop, "mapbox", distance, "", localcontext);
                SessionSave.saveWaypoints(pick, drop, "mapbox", distance, "", localcontext);
            }
            Localdistanceinterface.haversineResult(true);
        } else {
//            SessionSave.setGoogleDistance(distance, localcontext);
            LogUtils.debug("googledistanceee " + "5_" + pick.latitude + "__" + pick.longitude + "_____" + drop.latitude + "__" + drop.longitude);
            SessionSave.saveGoogleWaypoints(pick, drop, "haversine", distance, "UNKNOWN" + result, localcontext);
            SessionSave.saveWaypoints(pick, drop, "haversine", distance, "", localcontext);
            String savingTripDetail = "";
            savingTripDetail += SessionSave.getSession(SessionSave.getSession("trip_id", localcontext) + "data", localcontext) + "\n\n\n<br><br>" + "Distance**168#&nbsp;" + SessionSave.getDistance(localcontext) + "&nbsp;&nbsp;Trip&nbsp;" + SessionSave.getSession("trip_id", localcontext) + "&nbsp;&nbsp;Speed&nbsp;" + "&nbsp;&nbsp;Time&nbsp;" + DateFormat.getTimeInstance().format(new Date()) +
                    "&nbsp;&nbsp;old&nbsp;&nbsp;" + pick.latitude + "&nbsp;" + pick.longitude + "&nbsp;&nbsp;New&nbsp;&nbsp;" + drop.latitude
                    + "&nbsp;" + drop.longitude
                    + "&nbsp;&nbsp;Read way&nbsp;&nbsp;" + SessionSave.ReadGoogleWaypoints(localcontext);

            SessionSave.saveSession(SessionSave.getSession("trip_id", localcontext) + "data", savingTripDetail, localcontext);


            Localdistanceinterface.haversineResult(true);
        }*/
    }
}
