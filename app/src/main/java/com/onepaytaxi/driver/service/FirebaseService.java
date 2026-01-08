package com.onepaytaxi.driver.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.onepaytaxi.driver.Login.DriverLoginActivity;
import com.onepaytaxi.driver.MainActivity;
import com.onepaytaxi.driver.MyApplication;

import com.onepaytaxi.driver.OngoingAct;
import com.onepaytaxi.driver.R;
import com.onepaytaxi.driver.SplashAct;
import com.onepaytaxi.driver.data.CommonData;
import com.onepaytaxi.driver.data.apiData.DetailInfo;
import com.onepaytaxi.driver.errorLog.ApiErrorModel;
import com.onepaytaxi.driver.errorLog.ErrorLogRepository;
import com.onepaytaxi.driver.homepage.HomePageActivity;
import com.onepaytaxi.driver.utils.DriverUtils;
import com.onepaytaxi.driver.utils.ExceptionConverter;
import com.onepaytaxi.driver.utils.NC;
import com.onepaytaxi.driver.utils.SessionSave;
import com.onepaytaxi.driver.utils.Systems;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.taximobility.driver.utils.DeviceUtils;

/**
 * Created by developer on 30/8/17.
 */

/**
 * Created by developer on 30/8/17.
 */

public class FirebaseService extends FirebaseMessagingService {
    public static final int NOTIFICATION_ID = 123;
    public static MainActivity MAIN_ACT;
    Notification.Builder builder;
    private NotificationManager mNotificationManager;

    public static final int BOOKLATER_NOTIFICATION_ID = 123;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Systems.out.println("MyFirebaseIIDServices" + "onNewToken");
        if (s != null && !TextUtils.isEmpty(s)) {
            Systems.out.println("MyFirebaseIIDServices" + "__________" + s);
            SessionSave.saveSession(CommonData.DEVICE_TOKEN, s, this);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Systems.out.println("MyFirebaseIIDServices" + remoteMessage.getData());
        onHandleIntent(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            Log.d("MyFirebaseIIDService", "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("sssss", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }


    protected void onHandleIntent(RemoteMessage remoteMessage) {// Handling gcm message from



        String messageType = remoteMessage.getMessageType();
        String message = "";
        String status = "";
        String tripId = "";
        String title = "";
        String expiry_date = "";
        String display = "";
        String unique = "";

        try {
            System.out.println("remoteMessage.getData"+ " "+remoteMessage.getData());
            System.out.println("remoteMessage.getData"+ " "+remoteMessage.getData());

            Map<String, String> data = remoteMessage.getData();
            message = data.get("message");
            status = data.get("status");

            if (data.containsKey("trip_id")) {
                tripId = data.get("trip_id");
                System.out.println("Received trip_id: " + tripId);
            } else {
                System.out.println("trip_id not received in the payload.");
            }

            if (data.containsKey("expiry_date")) {
                expiry_date = data.get("expiry_date");

            }
            if (data.containsKey("title")) {
                title = data.get("title");

            }
            if (data.containsKey("display")) {
                display = data.get("display");

            }

            // Log extracted fields
            System.out.println("Message: " + message);
            System.out.println("Status: " + status);

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {


            if (!message.isEmpty()) {


                generateNotification(this, message,tripId, status, DriverLoginActivity.class);// to handle book later schedule

                if (status.equals("14")) {
                    if (display.equals("1")) {

                        generateNotification(this, message,tripId, status,DriverLoginActivity.class);// to handle book later schedule

                    }
                } else if (status.equals("15")) {

                    SessionSave.saveSession("status", "", getApplicationContext());
                    SessionSave.saveSession("Id", "", getApplicationContext());
                    SessionSave.saveSession("Driver_locations", "", getApplicationContext());
                    SessionSave.saveSession("driver_id", "", getApplicationContext());
                    SessionSave.saveSession("Name", "", getApplicationContext());
                    SessionSave.saveSession("company_id", "", getApplicationContext());
                    SessionSave.saveSession("bookedby", "", getApplicationContext());
                    SessionSave.saveSession("p_image", "", getApplicationContext());
                    SessionSave.saveSession("Email", "", getApplicationContext());
                    SessionSave.saveSession("trip_id", "", getApplicationContext());
                    SessionSave.saveSession("phone_number", "", getApplicationContext());
                    SessionSave.saveSession("driver_password", "", getApplicationContext());
                    SessionSave.saveSession("shift_status", "", getApplicationContext());
                    SessionSave.setWaitingTime(0L, getApplicationContext());

                    if (MAIN_ACT != null) {
                        Intent i = new Intent(MAIN_ACT, DriverLoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(getApplicationContext(), DriverLoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
                    }
                    if (!tripId.equals("")) {
                        generateNotification(this, message, tripId, status, DriverLoginActivity.class);
                    }
                } else if (status.equals("7") || status.equals("10") ) {
                    String cancelmsg = "";
                    cancelmsg = message;
                    MainActivity.mMyStatus.setStatus("F");
                    SessionSave.saveSession("status", "F", getApplicationContext());
                    MainActivity.mMyStatus.settripId("");
                    SessionSave.saveSession("trip_id", "", getApplicationContext());
                    SessionSave.setWaitingTime(0L, getApplicationContext());
                    MainActivity.mMyStatus.setOnstatus("");
                    MainActivity.mMyStatus.setOnPassengerImage("");
                    MainActivity.mMyStatus.setOnpassengerName("");
                    MainActivity.mMyStatus.setOndropLocation("");
                    MainActivity.mMyStatus.setOnpickupLatitude("");
                    MainActivity.mMyStatus.setOnpickupLongitude("");
                    MainActivity.mMyStatus.setOndropLatitude("");
                    MainActivity.mMyStatus.setOndropLongitude("");
                    MainActivity.mMyStatus.setOndriverLatitude("");
                    MainActivity.mMyStatus.setOndriverLongitude("");
                    LocationUpdate.ClearSessionwithTrip(getApplicationContext());
                    SessionSave.saveSession(CommonData.ST_WAITING_TIME, false, getApplicationContext());
                    SessionSave.saveSession(CommonData.WAITING_TIME, false, getApplicationContext());

                    Toast.makeText(getApplicationContext(), cancelmsg, Toast.LENGTH_LONG).show();

                    Intent cancelIntent = new Intent();
                    // Bundle bun = new Bundle();
                    // bun.putString("message", cancelmsg);
                    // cancelIntent.putExtras(bun);
                    cancelIntent.setAction(Intent.ACTION_MAIN);
                    cancelIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    cancelIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    ComponentName cn = new ComponentName(getApplicationContext(), HomePageActivity.class);

                    cancelIntent.setComponent(cn);
                    startActivity(cancelIntent);


                } else if (status.equals("99")) {

                    sendNotification(message);
                    Intent ongoing = new Intent();
                    Bundle extras = new Bundle();
                    String lTaximobilityutlmsg = "";
                    lTaximobilityutlmsg = message;
                    extras.putString("alert_message", lTaximobilityutlmsg);
                    extras.putString("status", status);
                    ongoing.putExtras(extras);
                    ongoing.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    ComponentName cn = new ComponentName(getApplicationContext(), OngoingAct.class);
                    ongoing.setComponent(cn);
                    getApplication().startActivity(ongoing);
                } else if (status.equals("41")) {

                    sendInfo(getApplicationContext(), unique);
                    if (!tripId.equals("")) {
                        generateNotification(this, message, tripId, status, SplashAct.class);

                    }
                } else if (status.equals("42")) {
                    sendInfo(getApplicationContext(), unique);
                    final Intent i = new Intent(getApplicationContext(), SplashAct.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    SessionSave.saveSession("need_animation", true, getApplicationContext());
                    if (!tripId.equals(""))
                    {
                        generateNotification(this, message, tripId, status, SplashAct.class);
                    }

                } else if (status.equals("43")) {
                    sendInfo(getApplicationContext(), unique);

                } else if (status.equals("44")) {
                    sendInfo(getApplicationContext(), unique);
                    if (!tripId.equals("")) {
                        generateNotification(this, message, tripId, status, SplashAct.class);
                    }
                    final Intent i = new Intent(getApplicationContext(), LocationUpdate.class);
                    stopService(i);
                    if (!SessionSave.getSession("driver_type", getApplicationContext()).equals("D")
                            && !SessionSave.getSession(CommonData.SHIFT_OUT, getApplicationContext(), false)
                            && !SessionSave.getSession(CommonData.LOGOUT, getApplicationContext(), false))
                        LocationUpdate.startLocationService(getApplicationContext());
                } else
                if (!tripId.equals("")) {
                    generateNotification(this, message, tripId, status, SplashAct.class);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLogRepository.getRepository(getApplicationContext()).insertAllApiErrorLogs(new ApiErrorModel(0, CommonData.getCurrentTimeForLogger(), "type=FirebaseService", ExceptionConverter.INSTANCE.buildStackTraceString(e.getStackTrace()), DriverUtils.INSTANCE.driverInfo(getApplicationContext()), null, getApplicationContext().getClass().getSimpleName(), 0));
        }
    }

    private void sendNotification(String msg) {
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_10";
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);



        PendingIntent contentIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            System.out.println("received_notufication_inside"+ " "+"2");

            contentIntent = PendingIntent.getActivity(this,
                    0, new Intent(this, HomePageActivity.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        }else {
            System.out.println("received_notufication_inside"+ " "+"3");

            contentIntent =  PendingIntent.getActivity(this, 0, new Intent(this, HomePageActivity.class), PendingIntent.FLAG_IMMUTABLE);

        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).setSmallIcon(R.drawable.small_logo).setContentTitle(getString(R.string.app_name)).setStyle(new NotificationCompat.BigTextStyle().bigText(msg)).setContentText(msg);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Uri notification1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        try {
            Ringtone r = RingtoneManager.getRingtone(getBaseContext(), notification1);
            r.play();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public void generateNotification(Context context, String message, String tripId, String status, Class<?> class1) {

        String Message = "";
        try {

            if (status.equals("25") || status.equals("15")||status.equals("200")) {


                SessionSave.saveSession("status", "", FirebaseService.this);
                SessionSave.saveSession("Id", "", FirebaseService.this);
                SessionSave.saveSession("Driver_locations", "", FirebaseService.this);
                SessionSave.saveSession("driver_id", "", FirebaseService.this);
                SessionSave.saveSession("Name", "", FirebaseService.this);
                SessionSave.saveSession("company_id", "", FirebaseService.this);
                SessionSave.saveSession("bookedby", "", FirebaseService.this);
                SessionSave.saveSession("p_image", "", FirebaseService.this);
                SessionSave.saveSession("Email", "", FirebaseService.this);
                SessionSave.saveSession("trip_id", "", FirebaseService.this);
                SessionSave.saveSession("phone_number", "", FirebaseService.this);
                SessionSave.saveSession("driver_password", "", FirebaseService.this);
                SessionSave.setWaitingTime(0L, FirebaseService.this);
                Message =message;
                SessionSave.saveSession(CommonData.USER_KEY, "", FirebaseService.this);
                showNotification(context, Message, message);
                if (MAIN_ACT != null) {
                    Handler h = new Handler();
                    if (h != null)
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                MAIN_ACT.checkGCM();
                            }
                        });

                }
                Intent i = new Intent(FirebaseService.this, DriverLoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            } else if (status.equals("42") || status.equals("45")
                    || status.equals("41")
                    || status.equals("45")
                    || status.equals("44") || status.equals("500")) {
                Message =message;
                showNotification(context, Message, message);
            }  else if (status.equals("14")) {
                Message = message;
                Intent i = new Intent(FirebaseService.this, HomePageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_TASK_ON_HOME| Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                i.putExtra("alert_message",message);
                i.putExtra("alert_trip_id",tripId);
                i.putExtra("alert_schedule","1");
                startActivity(i);
                showNotificationBookLater(context,Message,message,i);
            }else {
//                if (NotificationAct.notificationObject != null) {
//                    Systems.out.println("_________sddsfdsfdsfs");
//                    if (status.equals("7")) {
//                        SessionSave.saveSession("trip_id", "", FirebaseService.this);
//                    }
//                    NotificationAct.notificationObject.stopTimerAndNavigateToHome(message);
//                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            ErrorLogRepository.getRepository(getApplicationContext()).insertAllApiErrorLogs(new ApiErrorModel(0,CommonData.getCurrentTimeForLogger(), "type=FirebaseService", ExceptionConverter.INSTANCE.buildStackTraceString(e.getStackTrace()), DriverUtils.INSTANCE.driverInfo(getApplicationContext()), null, getApplicationContext().getClass().getSimpleName(), 0));
        }


    }



    private void showNotification(Context context, String Message, String data) {

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(this, SplashAct.class);
        notificationIntent.putExtra("GCMnotification", data);
        SessionSave.saveSession("GCMnotification", data, context);
        Systems.out.println("GGGGGGGGG" + data);
        int requestID = (int) System.currentTimeMillis();
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        SessionSave.saveSession("LogoutMessage", Message, FirebaseService.this);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            mNotificationManager.createNotificationChannel(notificationChannel);

            builder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                    //.setContentText(Message)
                    .setStyle(new Notification.BigTextStyle().bigText(Message))
                    .setContentTitle(title)
                    .setOngoing(true)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setSmallIcon(R.drawable.small_logo)
                    .setContentIntent(pendingIntent)
                    //    .setLargeIcon(((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.ic_app_logo_new)).getBitmap())
                    .setWhen(System.currentTimeMillis());
        } else {
            builder = new Notification.Builder(context);
            builder.setAutoCancel(false);
            builder.setTicker(NC.getString(R.string.app_name));
            builder.setContentTitle(title);
            // builder.setContentText(Message);
            builder.setStyle(new Notification.BigTextStyle().bigText(Message));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setSmallIcon(R.drawable.small_logo);
                builder.setColor(ContextCompat.getColor(getBaseContext(), R.color.btn_accept_primary));
            } else {
                builder.setSmallIcon(R.drawable.small_logo);
            }
            builder.setContentIntent(pendingIntent);
            builder.setOngoing(false);
        }
        Notification myNotication = builder.build();
        myNotication.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(NOTIFICATION_ID, myNotication);
        Uri notification1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        try {
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification1);
            r.play();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }



    private void showNotificationBookLater(Context context, String Message, String message,Intent intent) {

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(this, SplashAct.class);
        int requestID = (int) System.currentTimeMillis();
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra("alert_message",message);
        notificationIntent.putExtra("alert_schedule","1");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            mNotificationManager.createNotificationChannel(notificationChannel);

            builder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                    // .setContentText(Message)
                    .setStyle(new Notification.BigTextStyle().bigText(Message))

                    .setContentTitle(title)
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.small_logo)
                    .setContentIntent(pendingIntent)
                    // .setLargeIcon(((BitmapDrawable) getResources().getDrawable(R.drawable.ic_app_logo_new)).getBitmap())
                    .setWhen(System.currentTimeMillis());
        } else {
            builder = new Notification.Builder(context);
            builder.setAutoCancel(true);
            builder.setTicker(NC.getString(R.string.app_name));
            builder.setContentTitle(title);
            //builder.setContentText(Message);
            builder.setStyle(new Notification.BigTextStyle().bigText(Message));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setSmallIcon(R.drawable.small_logo);
                builder.setColor(ContextCompat.getColor(getBaseContext(), R.color.btn_accept_primary));
            } else {
                builder.setSmallIcon(R.drawable.small_logo);
            }
            builder.setContentIntent(pendingIntent);
            builder.setOngoing(false);
        }
        builder.build();
        Notification myNotication = builder.getNotification();
        myNotication.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(BOOKLATER_NOTIFICATION_ID, myNotication);
        Uri notification1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        try {
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification1);
            r.play();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }


    private void sendInfo(Context context, String unique) {

        String base_url = SessionSave.getSession("base_url", context);
        Uri uri = Uri.parse(base_url);
        String path = uri.getPath();
//        CoreClient client = new ServiceGenerator(context, false, base_url.replaceAll(path, "")).createService(CoreClient.class);
        String url = base_url.replaceAll(path, "") + "/taxidispatch/report_push_notification";
        CoreClient client = MyApplication.getInstance().getApiManagerWithEncryptBaseUrl();
        Call<ResponseBody> detail_infoCall = client.detail_infoCall(url, new DetailInfo(/*DeviceUtils.INSTANCE.getAllInfo(context), */DriverUtils.INSTANCE.driverInfo(context), unique), SessionSave.getSession("Lang", context));

        detail_infoCall.enqueue(new Callback<ResponseBody>() {

            @Override

            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override

            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();

            }

        });

    }
}