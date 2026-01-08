package com.mayan.sospluginmodlue.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by developer on 4/25/16.
 * display the toast at the center of the screen
 */
public class CToast {
    private static Toast toast;

    public static void ShowToast(Context context, String s) {
        if (s != null && !s.equals("") && context != null) {
            if (toast != null)
                toast.cancel();
            toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
