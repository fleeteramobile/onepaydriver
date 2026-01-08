package com.onepaytaxi.driver.utils;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.onepaytaxi.driver.BaseActivity;
import com.onepaytaxi.driver.R;


/**
 * Created by developer on 19/2/18.
 */

public class NoInternetScreen extends BaseActivity {
    public static NoInternetScreen mtag;
    public static TextView Network_state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_internet_lay);
        mtag = this;
        Network_state = findViewById(R.id.tv_check_connection);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SessionSave.saveSession("no_internet_screen",true,NoInternetScreen.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SessionSave.saveSession("no_internet_screen",false,NoInternetScreen.this);
    }

    public static void CloseNoInternetStatus(){
        Network_state.setText("Internet Connection Established");
        Network_state.setBackgroundColor(mtag.getResources().getColor(R.color.trip_cash_highlight));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mtag.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(mtag.getResources().getColor(R.color.trip_cash_highlight));
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mtag.finish();
            }
        },2000);
    }

    @Override
    public void onBackPressed() {
        onBackPressed();
        mtag.finish();
    }
}
