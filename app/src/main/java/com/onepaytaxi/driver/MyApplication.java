package com.onepaytaxi.driver;

import com.google.android.libraries.places.api.Places;
import com.onepaytaxi.driver.data.CommonData;
import com.onepaytaxi.driver.imageupload.RetrofitClient;
import com.onepaytaxi.driver.pushmessage.AppVisibility;
import com.onepaytaxi.driver.service.CoreClient;
import com.onepaytaxi.driver.service.NodeServiceGenerator;
import com.onepaytaxi.driver.service.ServiceGenerator;
import com.onepaytaxi.driver.utils.SessionSave;

import androidx.multidex.MultiDexApplication;



public class MyApplication extends MultiDexApplication {

    private static MyApplication mInstance;


    private CoreClient apiManagerWithBaseUrl, checkCompanyDomainapiManager, googleapiManager, apiManagerWithTimeoutWithEncrypt;
    private CoreClient apiManagerWithTimeoutWithoutEncrypt, nodeApiManagerWithTimeOut;

    private long nodeTimeOut = 0L;


    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        RetrofitClient.INSTANCE.init(this);
        registerActivityLifecycleCallbacks(AppVisibility.INSTANCE);

        setPlaceApiKey(getResources().getString(R.string.googleID));

    }


    public void setPlaceApiKey(String apiKey) {
        SessionSave.saveSession(CommonData.GOOGLE_KEY, apiKey, this);
        Places.initialize(this, apiKey);
    }


    public CoreClient getCheckCompanyDomainapiManager(String url) {
        checkCompanyDomainapiManager = ServiceGenerator.getRetrofitEncryptUrl(this, url).create(CoreClient.class);
        return checkCompanyDomainapiManager;
    }


    public CoreClient getApiManagerWithEncryptBaseUrl() {
        if (apiManagerWithBaseUrl == null) {
            apiManagerWithBaseUrl = ServiceGenerator.getRetrofitWithEncryptBaseUrl(this).create(CoreClient.class);
        }
        return apiManagerWithBaseUrl;
    }

    public CoreClient getApiManagerWithoutEncryptBaseUrl() {
        if (googleapiManager == null) {
            googleapiManager = ServiceGenerator.getRetrofitWithoutEncryptBaseUrl(this).create(CoreClient.class);
        }
        return googleapiManager;
    }

    public CoreClient getNodeApiManagerWithTimeOut(String base_url, long timeOut) {
        if (nodeTimeOut != timeOut) {
            nodeTimeOut = timeOut;
            nodeApiManagerWithTimeOut = null;
        }
        if (nodeApiManagerWithTimeOut == null) {
            nodeApiManagerWithTimeOut = NodeServiceGenerator.INSTANCE.nodeGetRetrofitWithTimeOut(this, base_url, nodeTimeOut).create(CoreClient.class);
        }
        return nodeApiManagerWithTimeOut;
    }

}