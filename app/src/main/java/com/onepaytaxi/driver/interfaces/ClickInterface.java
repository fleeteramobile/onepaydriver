package com.onepaytaxi.driver.interfaces;

import android.content.DialogInterface;

/**
 * Created by developer on 27/2/18.
 */

public interface ClickInterface {
    void positiveButtonClick(DialogInterface dialog, int id, String s);

    void negativeButtonClick(DialogInterface dialog, int id, String s);
}
