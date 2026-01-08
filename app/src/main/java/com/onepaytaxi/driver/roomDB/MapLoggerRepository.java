package com.onepaytaxi.driver.roomDB;

import androidx.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.onepaytaxi.driver.data.CommonData;
import com.onepaytaxi.driver.utils.SessionSave;
import com.onepaytaxi.driver.utils.Systems;

import java.util.List;

public class MapLoggerRepository {
    private final MapLoggerDao mLoggerDao;
    private final Context mContext;

    public MapLoggerRepository(Context application) {
        MapLoggerDatabase db = MapLoggerDatabase.getDatabase(application);
        mLoggerDao = db.loggerDao();
        mContext = application;
        SessionSave.saveSession(CommonData.LOCAL_STORAGE, false, mContext);

    }

    public void insertGoogleLog(GoogleMapModel model) {
        Systems.out.println("nan---insertLog");
        if (SessionSave.getSession(CommonData.LOCAL_STORAGE, mContext, false))
            new insertAsyncTask(mLoggerDao).execute(model);
    }

    public LiveData<String> getRouteResult(String keyy) {
        if (SessionSave.getSession(CommonData.LOCAL_STORAGE, mContext, false)) {
            LiveData<String> route = mLoggerDao.getRouteResult(keyy);
            return route;
        } else
            return null;
    }

    public LiveData<Double> getDistanceAndTime(String keyy) {
        if (SessionSave.getSession(CommonData.LOCAL_STORAGE, mContext, false))
            return mLoggerDao.getDistanceAndTime(keyy);
        else return null;
    }

    public LiveData<String> getDistanceResult(String keyy) {
        if (SessionSave.getSession(CommonData.LOCAL_STORAGE, mContext, false)) {
            LiveData<String> route = mLoggerDao.getDistanceResult(keyy);
            return route;
        } else return null;
    }

    public LiveData<List<GoogleMapModel>> getAll(String fromTo) {
        if (SessionSave.getSession(CommonData.LOCAL_STORAGE, mContext, false)) {
            return mLoggerDao.loadAll(fromTo);
        } else
            return null;
    }

    public GoogleMapModel getGoogleModel(String keyy) {
        if (SessionSave.getSession(CommonData.LOCAL_STORAGE, mContext, false))
            return mLoggerDao.getGoogleModel(keyy);
        else
            return null;
    }

//    public LiveData<GoogleMapModel> getGoogleModel(String keyy){
//        return mLoggerDao.getGoogleModel(keyy);
//    }

    public void insertMapboxLog(MapboxModel model) {
        if (SessionSave.getSession(CommonData.LOCAL_STORAGE, mContext, false))
            new insertMapboxLog(mLoggerDao).execute(model);
    }

    public MapboxModel getMapboxModel(String keyy) {
        if (SessionSave.getSession(CommonData.LOCAL_STORAGE, mContext, false))
            return mLoggerDao.getMapboxModel(keyy);
        else
            return null;
    }

//    public LiveData<MapboxModel> getMapboxModel(String keyy){
//        return mLoggerDao.getMapboxModel(keyy);
//    }

    public void insertGeocodeLog(GeocoderModel model) {
        if (SessionSave.getSession(CommonData.LOCAL_STORAGE, mContext, false))
            new insertGeocodeLog(mLoggerDao).execute(model);
    }

    public GeocoderModel getGeocodeModel(String keyy, int type) {
        if (SessionSave.getSession(CommonData.LOCAL_STORAGE, mContext, false))
            return mLoggerDao.getGeocodeModel(keyy, type);
        else
            return null;
    }

    private class insertMapboxLog extends AsyncTask<MapboxModel, Void, Void> {

        private final MapLoggerDao mLoggerDao;

        insertMapboxLog(MapLoggerDao dao) {
            mLoggerDao = dao;
        }

        @Override
        protected Void doInBackground(final MapboxModel... params) {
            if (SessionSave.getSession(CommonData.LOCAL_STORAGE, mContext, false))
                mLoggerDao.insertMapboxLog(params[0]);
            return null;
        }
    }

    private class insertGeocodeLog extends AsyncTask<GeocoderModel, Void, Void> {

        private final MapLoggerDao mLoggerDao;

        insertGeocodeLog(MapLoggerDao dao) {
            mLoggerDao = dao;
        }

        @Override
        protected Void doInBackground(final GeocoderModel... params) {
            if (SessionSave.getSession(CommonData.LOCAL_STORAGE, mContext, false))
                mLoggerDao.insertGeocodeLog(params[0]);
            return null;
        }
    }

    private class insertAsyncTask extends AsyncTask<GoogleMapModel, Void, Void> {

        private final MapLoggerDao mLoggerDao;

        insertAsyncTask(MapLoggerDao dao) {
            mLoggerDao = dao;
        }

        @Override
        protected Void doInBackground(final GoogleMapModel... params) {
            if (SessionSave.getSession(CommonData.LOCAL_STORAGE, mContext, false))
                mLoggerDao.insertGoogleLog(params[0]);
            Systems.out.println("nan---insertLog*****");
            return null;
        }
    }
}