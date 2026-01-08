package com.onepaytaxi.driver.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by developer on 22/3/18.
 */

public class CToast {
    private static Toast mToast = null;

    public static void ShowToast(Context context, String s) {
        if (context != null && s != null) {
            if (mToast != null)
                mToast.cancel();

            mToast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
            mToast.show();
        }
    }
}
