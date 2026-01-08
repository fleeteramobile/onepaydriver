package com.onepaytaxi.driver.utils;

import com.onepaytaxi.driver.BuildConfig;
/**
 * Created by developer on 31/1/18.
 */

public class Systems {
    public static class out{
        public static void println(String s){
            if (BuildConfig.DEBUG) {

                //It's not a release version.
            }
        }
    }
    public static class err{
        public static void println(String s){
            if (BuildConfig.DEBUG) {
                //It's not a release version.
                System.err.println(s);
            }
        }
    }
}
