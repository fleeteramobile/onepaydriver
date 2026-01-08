package com.onepaytaxi.driver.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.IBinder;

import com.onepaytaxi.driver.R;
import com.onepaytaxi.driver.SplashAct;
import com.onepaytaxi.driver.utils.NC;

public class ForegroundEnablingService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (LocationUpdate.instance == null)
            try {
                stopForeground(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*throw new RuntimeException(LocationUpdate.class.getSimpleName() + " not running");*/
        else {
            //Set both services to foreground using the same notification id, resulting in just one notification
            startForeground(LocationUpdate.instance);
            startForeground(this);

            //Cancel this service's notification, resulting in zero notifications
            // stopForeground(true);

            //Stop this service so we don't waste RAM.
            //Must only be called after doing the work or the notification won't be hidden.
            // stopSelf();}
        }
        return START_STICKY;
    }


    private void startForeground(Service service) {
        //service.startForeground(10, getNotification());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification getNotification() {

        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, SplashAct.class), 0);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
        int notifyId = 10;
        Notification notification;
        Notification.Builder builder = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Action action = new Notification.Action.Builder(Icon.createWithResource(this,R.drawable.small_logo), NC.getString(R.string.notiy_lanch_app), activityPendingIntent).build();
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .addAction(action)
                    .setContentText(NC.getString(R.string.app_running))
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.small_logo)
                    .setWhen(System.currentTimeMillis());
        } else {
            builder = new Notification.Builder(this)
                    .addAction(0, NC.getString(R.string.notiy_lanch_app)/* + getTripStatus()*/,
                            activityPendingIntent)
                    .setContentText(NC.getString(R.string.app_running))
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSmallIcon(R.drawable.small_logo)
                    .setWhen(System.currentTimeMillis());
        }

        notification = builder.build();
        notificationManager.notify(notifyId, notification);
        return notification;
    }


}
