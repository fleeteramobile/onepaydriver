package com.onepaytaxi.driver.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mayan.sospluginmodlue.service.SOSService;
import com.onepaytaxi.driver.R;
import com.onepaytaxi.driver.SplashAct;
import com.onepaytaxi.driver.data.CommonData;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

/**
 * This BroadcastReceiver to update network connections is Available/Not.
 */
public class NetworkStatus extends BroadcastReceiver {
    public static Context appContext;
    public static Dialog errorDialog;
    private final int REQUEST_READ_PHONE_STATE = 292;
    public Context mContext;
    private String message;
    private Dialog sDialog;

    public static boolean isOnline(Context mContext2) {
        ConnectivityManager connectivity = (ConnectivityManager) mContext2.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo networkInfo : info) {
                    Systems.out.println("STATE " + networkInfo);
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        CloseNoInternetScreen();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void CloseNoInternetScreen() {
        if (errorDialog != null && errorDialog.isShowing()) {
            errorDialog.dismiss();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.net.conn.CONNECTIVITY_CHANGE")) {
            mContext = context;
            try {
                if (!isOnline(mContext)) {
                    if (mContext instanceof SplashAct) {
                    } else {
                        Systems.out.println("NetworkStatus_____ onReceive 1");
                        DivertToNoInternetScreen();
                    }

                } else {
                    CloseNoInternetScreen();
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }


    }

    public void isConnect(final Context mContext, final boolean isconnect) {

        try {

            if (!isconnect) {
                if (sDialog != null)
                    sDialog.dismiss();
                sDialog = null;
                //netcon_lay
                final View view = View.inflate(mContext, R.layout.netcon_lay, null);
                sDialog = new Dialog(mContext, R.style.dialogwinddow);


                sDialog.setContentView(view);
                sDialog.setCancelable(false);
                FontHelper.applyFont(mContext, sDialog.findViewById(R.id.alert_id));
                if (!((AppCompatActivity) mContext).isFinishing()) {
                    //show dialog
                    sDialog.show();
                }


                final TextView title_text = sDialog.findViewById(R.id.title_text);
                final TextView message_text = sDialog.findViewById(R.id.message_text);
                final Button button_success = sDialog.findViewById(R.id.button_success);
                final Button button_failure = sDialog.findViewById(R.id.button_failure);
                title_text.setText(NC.getResources().getString(R.string.message));
                message_text.setText(NC.getResources().getString(R.string.check_net_connection));
                button_success.setText(NC.getResources().getString(R.string.c_tryagain));
                button_failure.setText(NC.getResources().getString(R.string.cancell));
                button_success.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        // TODO Auto-generated method stub
                        if (NetworkStatus.isOnline(mContext)) {
                            sDialog.dismiss();
                            if (!SessionSave.getSession("Email", mContext).equals("")) {

                                Intent intent = new Intent(mContext, mContext.getClass());
                                Activity activity = (Activity) mContext;
                                activity.finish();
                                mContext.startActivity(intent);
                            }
                        } else {
                            CToast.ShowToast(mContext, NC.getResources().getString(R.string.check_net_connection));
                        }


                    }
                });
                button_failure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        // TODO Auto-generated method stub
                        sDialog.dismiss();
                        Activity activity = (Activity) mContext;
                        activity.finish();
                        final Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    }
                });
            } else {
                try {
                    if (sDialog != null)
                        sDialog.dismiss();
                    sDialog = null;
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public boolean getConnectivityStatus(Context context) {
        boolean conn = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                //					if(context!=null)
                //						new URLReachable(context).execute();
                conn = activeNetwork.isConnected();
            }
        } else {
            conn = false;
        }
        return conn;
    }

    private void errorInSplash(String message) {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (appContext instanceof AppCompatActivity && !((AppCompatActivity) appContext).isFinishing()) {
                        if (errorDialog != null && errorDialog.isShowing()) {
                            errorDialog.dismiss();
                        }
                        final View view = View.inflate(appContext, R.layout.no_internet_lay, null);
                        errorDialog = new Dialog(appContext, R.style.Theme_Transparent1);
                        errorDialog.setContentView(view);
                        errorDialog.setCancelable(true);
                        errorDialog.setCanceledOnTouchOutside(true);
                        Window window = errorDialog.getWindow();
                        window.setGravity(Gravity.TOP);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(Color.BLACK);
                        }
                        AppCompatButton btn_emergency = errorDialog.findViewById(R.id.btn_emergency);
                        if (SessionSave.getSession(CommonData.SOS_ENABLED, appContext, false)) {
                            btn_emergency.setVisibility(View.VISIBLE);
                        }
                        btn_emergency.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startSOSService(appContext);
                            }
                        });
                        errorDialog.show();
                    } else {
                        try {
                            errorDialog.dismiss();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                }
            });

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }


    public void DivertToNoInternetScreen() {
        Systems.out.println("NetworkStatus_____ onReceive 2");
        errorInSplash(NC.getString(R.string.check_net_connection));
    }

    private void startSOSService(Context context) {
        if (!SessionSave.getSession("Id", context).equals("")) {
            SessionSave.saveSession("sos_id", SessionSave.getSession("Id", context), context);
            SessionSave.saveSession("user_type", "d", context);
            context.startService(new Intent(context, SOSService.class));
        }
    }

    private void createNetworkLog(Context context) {
//        ErrorLogRepository.getRepository(context).insertNetworkLog(new NetworkModel("" + System.currentTimeMillis(), new LatLng(currentLatitude, currentLongtitude), isOnline(context) ? "YES" : "NO", DriverUtils.INSTANCE.driverInfo(context)));
    }

    private class URLReachable extends AsyncTask<URL, Boolean, Boolean> {
        Context mContext;

        URLReachable(Context mContext) {
            this.mContext = mContext;
        }

        protected Boolean doInBackground(URL... urls) {
            try {
                URL url = new URL("http://google.com");   // Change to "http://google.com" for www  test.
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(10 * 1000);
                urlc.setReadTimeout(10 * 1000);
                // 10 s.

                urlc.connect();
                if (urlc.getResponseCode() == 200) {        // 200 = "OK" code (http connection is fine).
                    Log.wtf("Connection", "Success !");
                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
            // return isURLReachable(mContext);
        }


        protected void onPostExecute(Boolean result) {
            Systems.out.println("connection_reachable " + result);
            if (mContext != null && mContext instanceof Activity && ((Activity) mContext).getCurrentFocus() != null)
                isConnect(mContext, result);
        }
    }
}