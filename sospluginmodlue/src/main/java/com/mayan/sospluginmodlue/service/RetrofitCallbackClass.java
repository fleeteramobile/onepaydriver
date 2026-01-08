package com.mayan.sospluginmodlue.service;

import android.content.Context;
import androidx.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RetrofitCallbackClass<T> implements Callback<T> {

    private String mKey;
    private Call<T> mCall;
    private Response<T> mResponse;
    private Throwable mThrowable;
    private Callback<T> mCallback;
    private boolean mCallFinished;
    private boolean mCallEnqueued;

    Context activity;
//    Fragment fragment;

    public RetrofitCallbackClass(/*BaseView view,*/ Context dummyActivity, Callback<T> callback) {
//            mView = view;
        activity = dummyActivity;
        mCallback = callback;
        mCallFinished = false;
    }



//
//        public void attachView(BaseView view) {
//            mView = view;
//            // If there was a response or throwable cached, use the callback now
//            if (mCallFinished && mCallback != null && mCall != null) {
//                if (mResponse != null) {
//                    mCallback.onResponse(mCall, mResponse);
//                    onDestroy();
//                } else {
//                    if (mThrowable != null) {
//                        mCallback.onFailure(mCall, mThrowable);
//                        onDestroy();
//                    }
//                }
//            }
//        }
//
//        public void detachView() {
//            mView = null;
//        }

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

    public void setKey(String key) {
        mKey = key;
    }

    public String getKey() {
        return mKey;
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

        }else {
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
        }

        mCallFinished = true;
    }
}