package com.onepaytaxi.driver.utils;

import com.onepaytaxi.driver.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by developer on 29/11/16.
 */

/**
 * Used to Store and get string files from Hash map
 */
public class NC {
    public static HashMap<Integer, String> nfields_byID = new HashMap<>();
    public static HashMap<String, String> nfields_byName = new HashMap<>();


    public static ArrayList<String> fields = new ArrayList<>();
    public static ArrayList<String> fields_value = new ArrayList<>();

    public static HashMap<String, Integer> fields_id = new HashMap<>();


    static NC NC = null;

    static NC getInstance() {
        if (NC == null)
            NC = new NC();
        else
            NC = NC;
        return NC;
    }

    public static NC getResources() {
        return getInstance();
    }

    public static NC getActivity() {
        return getInstance();
    }

    public static String getString(int c) {

        if (nfields_byID.get(c) == null && MainActivity.context != null) {
            try {
                return MainActivity.context.getString(c);
            } catch (Exception e) {

                e.printStackTrace();
                return "";
            }
        } else
            return nfields_byID.get(c);
    }


}
