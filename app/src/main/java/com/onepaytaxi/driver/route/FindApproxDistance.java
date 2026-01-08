package com.onepaytaxi.driver.route;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.onepaytaxi.driver.MyApplication;
import com.onepaytaxi.driver.R;
import com.onepaytaxi.driver.interfaces.DistanceMatrixInterface;
import com.onepaytaxi.driver.roomDB.GoogleMapModel;
import com.onepaytaxi.driver.roomDB.MapLoggerRepository;
import com.onepaytaxi.driver.service.CoreClient;
import com.onepaytaxi.driver.service.RetrofitCallbackClass;
import com.onepaytaxi.driver.utils.CToast;
import com.onepaytaxi.driver.utils.DistanceMatrixUtil;
import com.onepaytaxi.driver.utils.SessionSave;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.onepaytaxi.driver.utils.NC.getResources;


/**
 * Created by developer on 23/5/18.
 */

public class FindApproxDistance {
    String from = "";
    String to = "";
    Context mContext;
    private LatLng pickUp, drop;
    private final DistanceMatrixInterface matrixInterface;

    private MapLoggerRepository mRepository;

    public FindApproxDistance(DistanceMatrixInterface matrixInterface) {
        this.matrixInterface = matrixInterface;
    }

    public void getDistance(Context c, double P_latitude, double P_longitude, double D_latitude, double D_longitude) {
        this.mContext = c;
        this.from = P_latitude + "," + P_longitude;
        this.to = D_latitude + "," + D_longitude;
        pickUp = new LatLng(P_latitude, P_longitude);
        drop = new LatLng(D_latitude, D_longitude);

        mRepository = new MapLoggerRepository(mContext);

        new GetGoogleLog(P_latitude, P_longitude, D_latitude, D_longitude).execute();
    }

    private void makeGoogleApiCall(double P_latitude, double P_longitude, double D_latitude, double D_longitude) {
  //      String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + P_latitude + "," + P_longitude + "&destinations=" + D_latitude + "," + D_longitude + "&key=" + SessionSave.getSession(CommonData.GOOGLE_KEY, mContext);
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + P_latitude + "," + P_longitude + "&destinations=" + D_latitude + "," + D_longitude + "&key=" + getResources().getString(R.string.googleID);
        CoreClient client = MyApplication.getInstance().getApiManagerWithoutEncryptBaseUrl();
        client.getJsonbyWholeUrl("no-cache", url)
                .enqueue(new RetrofitCallbackClass<>(mContext, new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            try {
                                String result = response.body().toString();
                                SessionSave.saveSessionOneTime(from.trim() + to.trim() + "D", result, mContext);
                                JSONObject obj = new JSONObject(result).getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
                                JSONObject ds = obj.getJSONObject("distance");
                                String dis = ds.getString("value");
                                JSONObject timee = obj.getJSONObject("duration");
                                String time = timee.getString("value");
                                double times = Double.parseDouble(time) / 60;
                                double dist = Double.parseDouble(dis) / 1000;
                                if (SessionSave.getSession("Metric", mContext).trim().equalsIgnoreCase("miles")) {
                                    dist = dist / 1.60934;
                                }
                                saveGoogleLog(from.trim() + to.trim(), times, dist, "", result);
                                matrixInterface.onDistanceCalled(pickUp, drop, dist, times, result, "OK");

                            } catch (Exception e) {
                                e.printStackTrace();
                                setFailureDistance(e.getLocalizedMessage());
                            }

                        } else setFailureDistance("Api Failed");
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonObject> call, Throwable t) {
                        CToast.ShowToast(mContext, t.getLocalizedMessage());
                        setFailureDistance(t.getLocalizedMessage());
                    }
                }));
    }

    private void setFailureDistance(String message) {
        double distance = DistanceMatrixUtil.INSTANCE.calculateDistance(SessionSave.getSession("Metric", mContext).trim(), pickUp, drop);
        distance += SessionSave.getGoogleDistance(mContext);
        matrixInterface.onDistanceCalled(pickUp, drop, distance, 0.0, message, "Error");
    }

    private void saveGoogleLog(String latLngKey, double time, double distance, String routeResult, String distanceResult) {
        GoogleMapModel model = new GoogleMapModel();
        model.fromTo = latLngKey;
        model.time = time;
        model.distance = distance;
        model.routeResult = routeResult;
        model.distanceResult = distanceResult;

        mRepository.insertGoogleLog(model);
    }

    private class GetGoogleLog extends AsyncTask<Void, Void, GoogleMapModel> {

        private final double P_latitude;
        private final double P_longitude;
        private final double D_latitude;
        private final double D_longitude;

        public GetGoogleLog(double p_latitude, double p_longitude, double d_latitude, double d_longitude) {
            this.P_latitude = p_latitude;
            this.P_longitude = p_longitude;
            this.D_latitude = d_latitude;
            this.D_longitude = d_longitude;
        }


        @Override
        protected GoogleMapModel doInBackground(Void... voids) {
            GoogleMapModel model = mRepository.getGoogleModel(from.trim() + to.trim());

            return model;
        }

        @Override
        protected void onPostExecute(GoogleMapModel model) {
            super.onPostExecute(model);
            if (model != null) {
                matrixInterface.onDistanceCalled(new LatLng(P_latitude, P_longitude), new LatLng(D_latitude, D_longitude), model.distance, model.time, model.distanceResult, "OK");
            } else {
                makeGoogleApiCall(P_latitude, P_longitude, D_latitude, D_longitude);
            }
        }
    }


}