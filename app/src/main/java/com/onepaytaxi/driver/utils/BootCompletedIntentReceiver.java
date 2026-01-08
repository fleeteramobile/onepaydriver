package com.onepaytaxi.driver.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.onepaytaxi.driver.service.LocationUpdate.startLocationService;

/**
 * Created by developer on 22/2/18.
 */

public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            if (!SessionSave.getSession("Id", context).equals("") && SessionSave.getSession("shift_status", context).equals("IN")) {
                startLocationService(context);
                if (SessionSave.getSession("travel_status", context).equalsIgnoreCase("2")) {
                    Systems.out.println("tamilllll " + "BootCompletedIntentReceiver");
//                    WaitingTimerRun.startTimerService(context);
                }

            }
        }
    }
}

