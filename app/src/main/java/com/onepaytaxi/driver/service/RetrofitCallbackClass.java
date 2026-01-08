package com.onepaytaxi.driver.service;

import android.content.Context;
import androidx.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RetrofitCallbackClass<T> implements Callback<T> {

    Context activity;
    private String mKey;
    private Call<T> mCall;
    private Response<T> mResponse;
    private Throwable mThrowable;
    private Callback<T> mCallback;
    private boolean mCallFinished;
    private boolean mCallEnqueued;
//    Fragment fragment;

    public RetrofitCallbackClass(/*BaseView view,*/ Context dummyActivity, Callback<T> callback) {
//            mView = view;
        activity = dummyActivity;
        mCallback = callback;
        mCallFinished = false;
    }

    public void onDestroy() {
        mCallback = null;
        mResponse = null;
        mThrowable = null;
//            mView = null;
    }

    public void enqueue(Call<T> call) {
        if (call != null) {
            mCall = call;
            mCall.enqueue(this);
            mCallEnqueued = true;
        }
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public boolean isCallFinished() {
        return mCallFinished;
    }

    public boolean isCallEnqueued() {
        return mCallEnqueued;
    }

    @Nullable
    public Response<T> getResponse() {
        return mResponse;
    }

    @Nullable
    public Call<T> getCall() {
        return mCall;
    }

    @Nullable
    public Throwable getThrowable() {
        return mThrowable;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        // Cache this response in case the view isn't available yet
        mResponse = response;
        mCall = call;

        if (activity != null /*&& activity.getCurrentFocus() !=null*/) {
            mCallback.onResponse(call, response);
            onDestroy();
        }

        mCallFinished = true;
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        // Cache this throwable, in case the view isn't available yet
        mThrowable = t;
        mCall = call;

        if (activity != null/* && activity.getCurrentFocus() != null*/) {
            mCallback.onFailure(call, t);
            onDestroy();

           /* HttpUrl requestedUrl = call.request().url();
            String apiCase = "";
            if (requestedUrl.queryParameter("type") != null && !requestedUrl.queryParameter("type").equals("")) {
                apiCase = requestedUrl.queryParameter("type");
            } else if (requestedUrl.pathSegments() != null)
                apiCase = requestedUrl.pathSegments().toString();

            createApiErrorLog(activity, apiCase, "Failure " + t.getMessage());*/
        }

        mCallFinished = true;
    }

    private void createApiErrorLog(Context context, String apiCase, String error) {
//        ErrorLogRepository.getRepository(context).insertApiErrorLog(new ApiErrorModel(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date().getTime()), apiCase, error, DriverUtils.INSTANCE.driverInfo(context)));
    }
}