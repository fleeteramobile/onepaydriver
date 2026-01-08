package com.onepaytaxi.driver.service;

import android.content.Context;
import android.content.Intent;

import static com.onepaytaxi.driver.service.LocationUpdate.startLocationService;

//This class used to make the service stop/start functions easily.
public class NonActivity {
    public Context context;

    // Constructor
    public NonActivity() {
    }

    public void startServicefromNonActivity(Context context) {
        startLocationService(context);

        //Intent intent = new Intent(context, LocationUpdate.class);
        //context.stopService(intent);
    }

    public void stopServicefromNonActivity(Context context) {
        Intent intent = new Intent(context, LocationUpdate.class);
        context.stopService(intent);
    }
}
