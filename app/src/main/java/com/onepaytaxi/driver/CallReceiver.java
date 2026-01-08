package com.onepaytaxi.driver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.onepaytaxi.driver.utils.Systems;

public class CallReceiver extends BroadcastReceiver {

    static String phoneState = "";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.intent.action.PHONE_STATE")) {
            phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            Systems.out.println("phoneState" + phoneState);
            phoneState();
        }

    }

    public static boolean phoneState() {
        if (phoneState == null || phoneState == "")
            return true;
        return phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE);
    }
}
