package com.onepaytaxi.driver.utils;


import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.onepaytaxi.driver.BaseActivity;
import com.onepaytaxi.driver.R;
import com.onepaytaxi.driver.SplashAct;
import com.onepaytaxi.driver.data.CommonData;
import com.onepaytaxi.driver.interfaces.ClickInterface;

import java.io.ByteArrayOutputStream;

import static com.onepaytaxi.driver.utils.NC.getString;

/**
 * Created by developer on 26/2/18.
 */

public class Utils {
    private static AlertDialog alert;
    private static AlertDialog gpsAlert;

    /**
     * To check Lollipop verison sdk
     *
     * @return true equal and higher , false for lower 5.0
     */
    public static boolean HigherThanLollipop() {

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }







    public static AlertDialog alert_view(final Context mContext, String title, String message,
                                         String success_txt, String failure_txt,
                      Boolean cancelable_val, final ClickInterface dialogInterface, final String s) {



        if(failure_txt.equals(""))
        {
            ExtensionKt.customDialog(mContext,message);
        }else {
            if (mContext != null) {
                ExtensionKt.customDialogyerno(mContext,message,success_txt,failure_txt,dialogInterface,s);

            }
        }










//        if (mContext != null) {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext,R.style.MyDialogTheme);
//            dialog.setCancelable(cancelable_val);
//            dialog.setMessage(message);
//            dialog.setPositiveButton(success_txt, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int id) {
//                    dialogInterface.positiveButtonClick(dialog, id, s);
//                }
//            })
//                    .setNegativeButton(failure_txt, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialogInterface.negativeButtonClick(dialog, id, s);
//                        }
//                    });
//
//            if (alert != null && alert.isShowing())
//                alert.dismiss();
//            alert = dialog.create();
//            alert.setOnShowListener(new DialogInterface.OnShowListener() {
//                @Override
//                public void onShow(DialogInterface arg0) {
//                    if (mContext != null && alert != null) {
//                        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(CL.getColor(R.color.black));
//                        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(CL.getColor(R.color.app_theme_color));
//                    }
//                }
//            });
//            alert.show();
//
//        }
        return null;
    }

    public static void closeDialog(Dialog alert) {
        try {
            Systems.out.println("closeDialogCalling");
            if (alert != null && alert.isShowing()) {
                alert.dismiss();
                alert = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void closeGPSDialog() {
        try {
            Systems.out.println("closeDialogCalling");
            if (gpsAlert != null && gpsAlert.isShowing()) {
                gpsAlert.dismiss();
                gpsAlert = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Dialog alert_view_dialog(final Context mContext, String title, String message,
                                           String success_txt, String failure_txt,


                                           Boolean cancelable_val, final DialogInterface.OnClickListener postive_dialogInterface, final DialogInterface.OnClickListener negative_dialogInterface, final String s) {

        if (mContext != null) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext,R.style.MyDialogTheme);
            dialog.setCancelable(cancelable_val);
            dialog.setMessage(message);
            dialog.setPositiveButton(success_txt, postive_dialogInterface)
                    .setNegativeButton(failure_txt, negative_dialogInterface);
            if (alert != null && alert.isShowing())
                alert.dismiss();
            alert = dialog.create();
            alert.setOnShowListener(
                    arg0 -> {
                        if (mContext != null && alert != null) {
                            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(CL.getColor(R.color.btn_accept_primary));
                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(CL.getColor(R.color.pure_black));
                        }
                    });
            alert.show();

        }
        return alert;
    }


    public static void alert_view_dialog_GPS(final AppCompatActivity mContext, String title, String message,
                                             String success_txt, String failure_txt,
                                             Boolean cancelable_val, final DialogInterface.OnClickListener postive_dialogInterface, final DialogInterface.OnClickListener negative_dialogInterface, final String s) {
        if (mContext != null) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext,R.style.MyDialogTheme);
            dialog.setCancelable(cancelable_val);
            dialog.setMessage(message);
            dialog.setPositiveButton(success_txt, postive_dialogInterface)
                    .setNegativeButton(failure_txt, negative_dialogInterface);

            gpsAlert = dialog.create();
            gpsAlert.setOnShowListener(
                    arg0 -> {
                        if (mContext != null && gpsAlert != null) {
                            gpsAlert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(CL.getColor(R.color.btn_accept_primary));
                            gpsAlert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(CL.getColor(R.color.pure_black));
                        }
                    });
            gpsAlert.show();

        }
    }

    private final static int freeDriverIdleLimit = 3600000;//1hour
    private final static int busyIdleLimit = 900000;//15 mins
    private final static int activeDriverIdleLimit = 3600000;//1 hour
    private final static int alertInterval = 300000;//5 mins


    private final static int idleNotification = 201;
    private static long lastNotifiedTime = -1;

    public static Boolean checkInteraction(Context context) {
//        Systems.out.println("whattttttttttt" + Math.abs(BaseActivity.getLastInteractionTime() - System.currentTimeMillis()));
        if (SessionSave.getSession("status", context).equals("F")) {
            if ((System.currentTimeMillis() - BaseActivity.getLastInteractionTime()) > (freeDriverIdleLimit - alertInterval))
                generateNotifications(context, getString(R.string.idle_stop), SplashAct.class, false, idleNotification);
            if (SessionSave.getSession(CommonData.ACTIVITY_BG, context).equals("1") &&
                    (System.currentTimeMillis() - BaseActivity.getLastInteractionTime()) > (freeDriverIdleLimit))
//                return false;
                return true;
        } else if (SessionSave.getSession("status", context).equals("B")) {
            if ((System.currentTimeMillis() - BaseActivity.getLastInteractionTime()) > busyIdleLimit) {
                if (lastNotifiedTime != -1 && ((System.currentTimeMillis() - lastNotifiedTime) > busyIdleLimit))
                    generateNotifications(context, getString(R.string.trip_stop), SplashAct.class, false, idleNotification);
            }
        } else if (SessionSave.getSession("status", context).equals("A")) {
            if ((System.currentTimeMillis() - BaseActivity.getLastInteractionTime()) > activeDriverIdleLimit) {
                if (lastNotifiedTime != -1 && ((System.currentTimeMillis() - lastNotifiedTime) > activeDriverIdleLimit))
                    generateNotifications(context, getString(R.string.trip_stop), SplashAct.class, false, idleNotification);
            }
        }

        return true;
    }


    public static void generateNotifications(Context context, String message, Class<?> class1,
                                             boolean cancelable, int Notification_ID) {
        lastNotifiedTime = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String title = getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, class1);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
        Notification myNotication;
        Notification.Builder builder = null;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(notificationChannel);

            builder = new Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setContentText(message)
                    .setContentTitle(title)
                    .setOngoing(true)
                    .setSmallIcon(getNotificationIcon())
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.small_logo)).getBitmap())
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(message))
                    .setWhen(System.currentTimeMillis());
        } else {
            builder = new Notification.Builder(context)
                    .setAutoCancel(true)
                    .setTicker(context.getResources().getString(R.string.common_name))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setSmallIcon(getNotificationIcon())
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(message))
                    .setLargeIcon(((BitmapDrawable) context.getResources().getDrawable(R.drawable.small_logo)).getBitmap());

        }

        myNotication = builder.build();

        myNotication.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Notification_ID, myNotication);
        Uri notification1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        try {
            Ringtone r = RingtoneManager.getRingtone(context, notification1);
            r.play();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private static int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.small_logo : R.drawable.small_logo;
    }

    public static String convertToBase64(ImageView imgV) {
        Drawable d = imgV.getDrawable();
        Bitmap bit = ImageUtils.drawableToBitmap(d);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return base64 == null ? "" : base64;
    }


}