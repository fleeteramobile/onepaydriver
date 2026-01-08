package com.onepaytaxi.driver.utils;

import android.content.Context;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.onepaytaxi.driver.MyApplication;
import com.onepaytaxi.driver.R;
import com.onepaytaxi.driver.data.CommonData;
import com.onepaytaxi.driver.interfaces.GetAddress;
import com.onepaytaxi.driver.roomDB.GeocoderModel;
import com.onepaytaxi.driver.roomDB.MapLoggerRepository;
import com.onepaytaxi.driver.service.CoreClient;
import com.onepaytaxi.driver.service.RetrofitCallbackClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.onepaytaxi.driver.utils.NC.getResources;


public class GetAddressFromLatLng extends AsyncTask<String, String, GeocoderModel> {
    private final MapLoggerRepository mRepository;
    public Context mContext;
    LatLng mPosition;
    String Address = "";
    Geocoder geocoder;
    List<android.location.Address> addresses = null;
    List<android.location.Address> list = null;
    private final double latitude;
    private final double longitude;

    private String type_str = "";


    private final GetAddress getAddress_listener;

    public GetAddressFromLatLng(Context context, LatLng position, GetAddress getAddress_listener, String type) {

        this.mContext = context;
        type_str = type;
        mPosition = position;
        latitude = mPosition.latitude;
        longitude = mPosition.longitude;
        //   geocoder = new Geocoder(BookTaxiAct.mTag, Locale.getDefault());
        this.getAddress_listener = getAddress_listener;
        mRepository = new MapLoggerRepository(mContext);
            geocoder = new Geocoder(context, Locale.getDefault());
    }


    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        Systems.out.println("map_box_fir" + Thread.currentThread().getId());
    }

    @Override
    protected GeocoderModel doInBackground(String... params) {
        // TODO Auto-generated method stub
        boolean isStrictMapBox = false;
        if (params != null && params.length > 0)
            isStrictMapBox = params[0].equals("mapbox");
        GeocoderModel model = null;
        if (SessionSave.getSession(CommonData.isGoogleGeocoder, mContext, false))
            model = mRepository.getGeocodeModel(latitude + "," + longitude, CommonData.isGoogleGeocode);
        else
            model = mRepository.getGeocodeModel(latitude + "," + longitude, CommonData.isMapboxGeocode);


        if (model != null) {
            Systems.out.println("haiiiiiiii geocodeeee already available: " + model.result);
            return model;

        } else {
            Systems.out.println("haiiiiiiii geocodeeee new value " + isStrictMapBox);
                if (Geocoder.isPresent()) {
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 3);
                        if (addresses != null) {
                            if (addresses.size() == 0) {
                                convertLatLngtoAddressApi(latitude, longitude);
                            } else {
                                for (int i = 0; i < addresses.size(); i++) {
                                    Address += addresses.get(0).getAddressLine(i) + ", ";
                                }
                                if (Address.length() > 0) {
                                    Address = Address.substring(0, Address.length() - 2);

                                    return (saveGeocodeLog(latitude + "," + longitude, Address, CommonData.isGoogleGeocode));
                                }
                            }
                        } else {
                            convertLatLngtoAddressApi(latitude, longitude);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (NetworkStatus.isOnline(mContext)) {
                            Systems.out.println("haiiiiiiii geocodeeee IOException--1");
                            Systems.out.println("map_box_sec" + Thread.currentThread().getId());
                            convertLatLngtoAddressApi(latitude, longitude);
                        }
                    }
                } else {
                    if (NetworkStatus.isOnline(mContext)) {
                        Systems.out.println("haiiiiiiii geocodeeee Geocoder not available");
                        Systems.out.println("map_box_thr" + Thread.currentThread().getId());
                        convertLatLngtoAddressApi(latitude, longitude);
                    }
                }
        }

        return null;
    }


    @Override
    protected void onPostExecute(GeocoderModel result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        if (result != null && !result.result.equalsIgnoreCase(""))
            if (getAddress_listener != null) {
                getAddress_listener.setaddress(latitude, longitude, result.result.replaceAll("null", "").replaceAll(", ,", "").replaceAll(", ,", ""), type_str);
            }

    }

    private void convertLatLngtoAddressApi(double lati, double longi) {
        Systems.out.println("geocodeeee convertLatLngtoAddressApi ");
    //    String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lati + "," + longi + "&sensor=false" + "&key=" + SessionSave.getSession(CommonData.GOOGLE_KEY, mContext);
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lati + "," + longi + "&sensor=false" + "&key=" + getResources().getString(R.string.googleID);
//        CoreClient polyline = new ServiceGenerator(mContext, true).createService(CoreClient.class);
        CoreClient polyline = MyApplication.getInstance().getApiManagerWithoutEncryptBaseUrl();
        polyline.getJsonbyWholeUrl("no-cache", url)
                .enqueue(new RetrofitCallbackClass<>(mContext, new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            String result = response.body().toString();
                            Systems.out.println("geocodeeee result " + result);
                            if (result != null && result.length() > 0)
                                if (getAddress_listener != null) {
                                    try {
                                        JSONObject object = new JSONObject(result);
                                        JSONArray array = object.getJSONArray("results");
                                        object = array.getJSONObject(0);
                                        String address = null;
                                        if (!object.getString("formatted_address").equalsIgnoreCase(""))
                                            address = object.getString("formatted_address").replaceAll("null", "").replaceAll(", ,", "").replaceAll(", ,", "");
                                        Systems.out.println("formatted_address " + address);
                                        saveGeocodeLog(lati + "," + longi, address, CommonData.isGoogleGeocode);
                                        getAddress_listener.setaddress(lati, longi, address, type_str);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Systems.out.println("map_box" + Thread.currentThread().getId());
                                        new GetAddressFromLatLng(mContext, mPosition, getAddress_listener, type_str).execute("mapbox");
                                    }
                                }
                        } else {
                            new GetAddressFromLatLng(mContext, mPosition, getAddress_listener, type_str).execute("mapbox");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonObject> call, Throwable t) {
                        t.printStackTrace();
                    }
                }));
    }

    private GeocoderModel saveGeocodeLog(String latLngKey, String result, int type) {
        Systems.out.println("haiiiiiiii geocodeeee saveGeocodeLog" + latLngKey + "***" + result + "*****" + type);
        GeocoderModel model = new GeocoderModel();
        model.latLng = latLngKey;
        model.result = result;
        model.type = type;

        mRepository.insertGeocodeLog(model);
        return model;
    }

    private class GetGeocodeLog extends AsyncTask<Void, Void, GeocoderModel> {

        private final double P_latitude;
        private final double P_longitude;
        private final int type;

        public GetGeocodeLog(double p_latitude, double p_longitude, int type) {
            this.P_latitude = p_latitude;
            this.P_longitude = p_longitude;
            this.type = type;
        }

        @Override
        protected GeocoderModel doInBackground(Void... voids) {
            GeocoderModel model = mRepository.getGeocodeModel(P_latitude + "," + P_longitude, type);
            return model;
        }

        @Override
        protected void onPostExecute(GeocoderModel model) {
            super.onPostExecute(model);

        }
    }

}
