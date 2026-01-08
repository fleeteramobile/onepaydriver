package com.mayan.sospluginmodlue.service;

import android.content.Context;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.mayan.sospluginmodlue.interfaces.GetAddress;
import com.mayan.sospluginmodlue.util.SessionSave;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AddressFromLatLng extends AsyncTask<String, String, String> {
    public static final String GOOGLE_KEY = "android_web_key";
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

    public AddressFromLatLng(Context context, LatLng position, GetAddress getAddress_listener, String type) {
        this.mContext = context;
        type_str = type;
        mPosition = position;
        latitude = mPosition.latitude;
        longitude = mPosition.longitude;

        this.getAddress_listener = getAddress_listener;

        geocoder = new Geocoder(context, Locale.getDefault());
    }


    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        if (Geocoder.isPresent()) {
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 3);
                if (addresses != null) {
                    if (addresses.size() == 0) {
                        if (isOnline(mContext)) {
                            convertLatLngtoAddressApi(latitude, longitude);
                        } else {
                            return "";
                        }
                    } else {
                        for (int i = 0; i < addresses.size(); i++) {
                            Address += addresses.get(0).getAddressLine(i) + ", ";
                        }
                        if (Address.length() > 0) {
                            Address = Address.substring(0, Address.length() - 2);

                            return Address;
                        }
                    }
                } else {
                    if (isOnline(mContext)) {
                        convertLatLngtoAddressApi(latitude, longitude);
                    } else {
                        return "";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (isOnline(mContext)) {
                    convertLatLngtoAddressApi(latitude, longitude);
                } else {
                    return "";
                }
            }
        } else {
            if (isOnline(mContext)) {
                convertLatLngtoAddressApi(latitude, longitude);
            } else {
                return "";
            }
        }

        return "";
    }


    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        if (result != null)
            if (getAddress_listener != null) {
                getAddress_listener.setaddress(latitude, longitude, result.replaceAll("null", "").replaceAll(", ,", "").replaceAll(", ,", ""));
            }

    }


    private void convertLatLngtoAddressApi(final double lati, final double longi) {
        if (isOnline(mContext)) {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(SessionSave.getSession("base_url", mContext))
                    .addConverterFactory(GsonConverterFactory.create());
            String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lati + "," + longi + "&sensor=false" + "&key=" + SessionSave.getSession(GOOGLE_KEY, mContext);
            CoreClient polyline = builder.build().create(CoreClient.class);
            polyline.getJsonbyWholeUrl("no-cache", url)
                    .enqueue(new RetrofitCallbackClass<>(mContext, new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            if (response.isSuccessful()) {
                                String result = response.body().toString();
                                if (result != null && result.length() > 0)
                                    if (getAddress_listener != null) {
                                        try {
                                            JSONObject object = new JSONObject(result);
                                            JSONArray array = object.getJSONArray("results");
                                            object = array.getJSONObject(0);
                                            String address = null;
                                            if (!object.getString("formatted_address").equalsIgnoreCase(""))
                                                address = object.getString("formatted_address").replaceAll("null", "").replaceAll(", ,", "").replaceAll(", ,", "");
                                            getAddress_listener.setaddress(lati, longi, address);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            new AddressFromLatLng(mContext, mPosition, getAddress_listener, type_str).execute("mapbox");
                                        }
                                    }
                            } else {
                                new AddressFromLatLng(mContext, mPosition, getAddress_listener, type_str).execute("mapbox");
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonObject> call, Throwable t) {
                            t.printStackTrace();
                        }
                    }));
        } else {
            new AddressFromLatLng(mContext, mPosition, getAddress_listener, type_str).execute("mapbox");
        }
    }

    public static boolean isOnline(Context mContext2) {
        if (mContext2 != null) {
            ConnectivityManager connectivity = (ConnectivityManager) mContext2.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (NetworkInfo networkInfo : info)
                        return networkInfo.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }

}