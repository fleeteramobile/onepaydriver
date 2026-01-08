package com.onepaytaxi.driver.service;

//This timer class used to calculate the driver waiting time based on driver below KM
// speed it will automatically start/stop.
public class WaitingTimerRun  {
//    public static String sTimer = "00:00:00";
//    private static long startTime = 0L;
//    private static Handler myHandler = new Handler();
//    public static long timeInMillies = 0L;
//    long timeSwap;
//    public static long finalTime = 0L;
//    public static long saveTime;
//    private String Tag;
//    private int startID;
//
//
//    public static void ClearSession(Context context) {
//        Systems.out.println("nn--ClearSession");
//        timeInMillies = 0L;
//        finalTime = 0L;
//        startTime = 0L;
//        sTimer = "00:00:00";
//        SessionSave.setWaitingTime(0L, context);
//        SessionSave.setDistance(0.0, context);
//        SessionSave.setGoogleDistance(0f, context);
//        SessionSave.saveSession(CommonData.LAST_KNOWN_LAT, "", context);
//        SessionSave.saveGoogleWaypoints(null, null, "", 0.0, "", context);
//        SessionSave.saveWaypoints(null, null, "", 0.0, "", context);
//    }
//
//    public static void ClearSessionwithTrip(Context context) {
//        Systems.out.println("nn--ClearSessionwithTrip");
//        timeInMillies = 0L;
//        finalTime = 0L;
//        startTime = 0L;
//        sTimer = "00:00:00";
//        SessionSave.setWaitingTime(0L, context);
//        SessionSave.setDistance(0.0, context);
//        SessionSave.setGoogleDistance(0f, context);
//        SessionSave.saveSession(CommonData.LAST_KNOWN_LAT, "", context);
//        SessionSave.saveSession("status", "F", context);
//        SessionSave.saveSession("travel_status", "", context);
//        SessionSave.saveSession("trip_id", "", context);
//        SessionSave.saveGoogleWaypoints(null, null, "", 0.0, "", context);
//        SessionSave.saveWaypoints(null, null, "", 0.0, "", context);
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        CommonData.speed_waiting_stop = false;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        startID = startId;
////        startTime();
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//   /* private void startTime() {
//
//        startTime = SystemClock.uptimeMillis();
//
//        timeInMillies = SystemClock.uptimeMillis() - startTime;
//        timeSwap = SessionSave.getWaitingTime(WaitingTimerRun.this);
//        if (myHandler != null) {
//            if (updateTimerMethod != null) {
//                myHandler.removeCallbacks(updateTimerMethod);
//            }
//            myHandler.postDelayed(updateTimerMethod, 1000);
//        }
//    }
//
//    private final Runnable updateTimerMethod = new Runnable() {
//        @Override
//        public void run() {
//
//            CommonData.iswaitingrunning = true;
//            timeInMillies = SystemClock.uptimeMillis() - startTime;
//            if (startTime != 0) {
//                finalTime = timeSwap + timeInMillies;
//                sTimer = getDateForWaitingTime(finalTime);
//
//                *//*int seconds = (int) (finalTime / 1000);
//                int minutes = seconds / 60;
//                seconds = seconds % 60;
//                int hour = minutes / 60;
//                if (minutes >= 60) {
//                    minutes = minutes - (hour * 60);
//                }
//                sTimer = String.format(Locale.UK, "%02d", hour) + ":" + String.format(Locale.UK, "%02d", minutes)
//                        + ":" + String.format(Locale.UK, "%02d", seconds);*//*
//
//                if (finalTime != 0) {
//                    SessionSave.setWaitingTime(finalTime, WaitingTimerRun.this);
//                }
//            }
//            myHandler.postDelayed(this, 1000);
//        }
//    };*/
//    @Override
//    public void onDestroy() {
//        CommonData.speed_waiting_stop = true;
////        stoptime();
//        super.onDestroy();
//
//    }
//    public String getDateForWaitingTime(long d) {
//        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
//        df.setTimeZone(TimeZone.getTimeZone("GMT"));
//        return df.format(d);
//    }
//  /*  private void stoptime() {
//
//        if (myHandler != null) {
//            myHandler.removeCallbacks(updateTimerMethod);
//        }
//        timeSwap += SessionSave.getWaitingTime(WaitingTimerRun.this);
//    }*/
//
//   /* public static void startTimerService(Context context) {
//        if (!CommonData.serviceWaitingIsRunning(context)) {
//            myHandler = new Handler();
//            Intent pushIntent1 = new Intent(context, WaitingTimerRun.class);
//            context.startService(pushIntent1);
//        }
//    }*/
//
///*
//
//    public static boolean checkServiceRunning(Context context){
//        if (!CommonData.serviceWaitingIsRunning(context)) {
//            return false;
//        }else {
//            return true;
//        }
//    }

}