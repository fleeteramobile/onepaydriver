//package com.taximobility.driver.roomDB;
//
//import android.app.Application;
//import androidx.lifecycle.AndroidViewModel;
//import androidx.lifecycle.LiveData;
//
///**
// * Created by developer on 28/5/18.
// */
//
//public class MapLoggerViewModel extends AndroidViewModel {
//    MapLoggerRepository mRepository;
//
//    public MapLoggerViewModel(Application application) {
//        super(application);
//        mRepository = new MapLoggerRepository(application);
//    }
//
//    public void insertLog(GoogleMapModel model) {
//        mRepository.insertGoogleLog(model);
//    }
//
//    public LiveData<String> getRouteResult(String keyy) {
//        return mRepository.getRouteResult(keyy);
//    }
//
//    public LiveData<Double> getDistanceAndTime(String keyy) {
//        return mRepository.getDistanceAndTime(keyy);
//    }
//
//    public LiveData<String> getDistanceResult(String keyy) {
//        return mRepository.getDistanceResult(keyy);
//    }
//}
