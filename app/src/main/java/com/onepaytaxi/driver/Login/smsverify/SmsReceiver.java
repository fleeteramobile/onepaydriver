package com.onepaytaxi.driver.Login.smsverify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

/**
 * Created by product team on 23/2/18.
 */

public class SmsReceiver extends BroadcastReceiver {
    private OnSmsCatchListener<String> callback;
    private String phoneNumberFilter;
    private String filter;

    /**
     * Set result callback
     *
     * @param callback OnSmsCatchListener
     */
    public void setCallback(OnSmsCatchListener<String> callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                switch (status != null ? status.getStatusCode() : 15) {
                    case CommonStatusCodes.SUCCESS:
                        // Get SMS message contents
                        String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                        if (callback != null) {
                            callback.onSmsCatch(message);
                        }
                        break;
                    case CommonStatusCodes.TIMEOUT:
                        break;
                }
            }

        }
    }

    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSMS;
    }

    /**
     * Set phone number filter
     *
     * @param phoneNumberFilter phone number
     */
    public void setPhoneNumberFilter(String phoneNumberFilter) {
        this.phoneNumberFilter = phoneNumberFilter;
    }

    /**
     * set message filter with regexp
     *
     * @param regularExpression regexp
     */
    public void setFilter(String regularExpression) {
        this.filter = regularExpression;
    }
}