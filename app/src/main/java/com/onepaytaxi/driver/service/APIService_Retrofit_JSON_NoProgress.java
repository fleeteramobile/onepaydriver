package com.onepaytaxi.driver.service;

import android.content.Context;

import com.onepaytaxi.driver.MyApplication;
import com.onepaytaxi.driver.R;
import com.onepaytaxi.driver.data.CommonData;
import com.onepaytaxi.driver.interfaces.APIResult;
import com.onepaytaxi.driver.utils.NC;
import com.onepaytaxi.driver.utils.NetworkStatus;
import com.onepaytaxi.driver.utils.SessionSave;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * @author developer This AsyncTask used to communicate the application with server through Volley framework. Here the response completely in JSON format. Constructor get the input details List<NameValuePair>,POST or GET then url. In pre execute,Show the progress dialog. In Background,Connect and get the response. In Post execute, Return the result with interface. This class call the API without any progress on UI.
 */
public class APIService_Retrofit_JSON_NoProgress {
    public Context mContext;
    public APIResult response;
    String result = "";
    boolean dont_encode;
    private boolean wholeURL;
    private boolean GetMethod = true;
    private final JSONObject data;
    private String url_type;
    private Call<ResponseBody> coreResponse;


    public APIService_Retrofit_JSON_NoProgress(Context ctx, APIResult res, JSONObject j, boolean getmethod) {
        mContext = ctx;
        response = res;
        this.data = j;
        GetMethod = getmethod;
    }

    public APIService_Retrofit_JSON_NoProgress(Context ctx, APIResult res, String j, boolean getmethod) {
        mContext = ctx;
        response = res;
        JSONObject jobj = null;
        try {
            if (!getmethod)
                jobj = new JSONObject(j);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.data = jobj;
        GetMethod = getmethod;
    }


    public APIService_Retrofit_JSON_NoProgress(Context ctx, APIResult res, JSONObject j, boolean getmethod, String url, boolean dont_encode) {
        mContext = ctx;
        response = res;
        this.data = j;
        this.dont_encode = dont_encode;
        GetMethod = getmethod;
        String[] type = url.split("type=");
        if (type.length > 1)
            url_type = type[1];
        else {
            wholeURL = true;
            url_type = url;
        }
    }

    //@Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        doInBackground();
    }

    //@Override
    protected void doInBackground() {
        // TODO Auto-generated method stub
        if (!NetworkStatus.isOnline(mContext)) {
            response.getResult(false, NC.getString(R.string.check_net_connection));
            result = NC.getString(R.string.check_net_connection);
            //return result;
        } else {
            if (GetMethod) {
                CoreClient client;
                if (dont_encode) {
                    client = MyApplication.getInstance().getApiManagerWithoutEncryptBaseUrl();
                } else {
                    client = MyApplication.getInstance().getApiManagerWithEncryptBaseUrl();
                }

                if (!wholeURL)
                    coreResponse = client.coreDetails("", "no-cache",
                            url_type, "0"
                            , SessionSave.getSession(CommonData.ACCESS_KEY, mContext));
                else
                    coreResponse = client.getWhole("no-cache", url_type);
                coreResponse.enqueue(new RetrofitCallbackClass<>(mContext, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        String data = null;
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    data = response.body().string();
                                    if (APIService_Retrofit_JSON_NoProgress.this.response != null)
                                        APIService_Retrofit_JSON_NoProgress.this.response.getResult(true, data);
                                } else {
                                    if (APIService_Retrofit_JSON_NoProgress.this.response != null)
                                        APIService_Retrofit_JSON_NoProgress.this.response.getResult(false, null);
                                   // CToast.ShowToast(mContext, NC.getString(R.string.server_error));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (APIService_Retrofit_JSON_NoProgress.this.response != null)
                                    APIService_Retrofit_JSON_NoProgress.this.response.getResult(false, null);
                               // CToast.ShowToast(mContext, NC.getString(R.string.server_error));
                            }
                        } else {
                            if (APIService_Retrofit_JSON_NoProgress.this.response != null)
                                APIService_Retrofit_JSON_NoProgress.this.response.getResult(false, null);
                           // CToast.ShowToast(mContext, NC.getString(R.string.server_error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (APIService_Retrofit_JSON_NoProgress.this.response != null)
                            APIService_Retrofit_JSON_NoProgress.this.response.getResult(false, null);
                       // CToast.ShowToast(mContext, NC.getString(R.string.server_error));
                        t.printStackTrace();
                    }
                }));

            } else {
                CoreClient client;
                if (dont_encode) {
                    client = MyApplication.getInstance().getApiManagerWithoutEncryptBaseUrl();
                } else {
                    client = MyApplication.getInstance().getApiManagerWithEncryptBaseUrl();
                }

                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (data).toString());

                Call<ResponseBody> coreResponse = client.updateUser(ServiceGenerator.COMPANY_KEY, body, url_type, SessionSave.getSession("Lang", mContext));
                coreResponse.enqueue(new RetrofitCallbackClass<>(mContext, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        String data = null;
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    data = response.body().string();
                                    if (APIService_Retrofit_JSON_NoProgress.this.response != null)
                                        APIService_Retrofit_JSON_NoProgress.this.response.getResult(true, data);
                                } else {
                                    if (APIService_Retrofit_JSON_NoProgress.this.response != null)
                                        APIService_Retrofit_JSON_NoProgress.this.response.getResult(false, null);
                                    //CToast.ShowToast(mContext, NC.getString(R.string.server_error));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (APIService_Retrofit_JSON_NoProgress.this.response != null)
                                    APIService_Retrofit_JSON_NoProgress.this.response.getResult(false, null);
                               // CToast.ShowToast(mContext, NC.getString(R.string.server_error));
                            }
                        } else {
                            if (APIService_Retrofit_JSON_NoProgress.this.response != null)
                                APIService_Retrofit_JSON_NoProgress.this.response.getResult(false, null);
                            //CToast.ShowToast(mContext, NC.getString(R.string.server_error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (APIService_Retrofit_JSON_NoProgress.this.response != null)
                            APIService_Retrofit_JSON_NoProgress.this.response.getResult(false, null);
                       // CToast.ShowToast(mContext, NC.getString(R.string.server_error));
                        t.printStackTrace();
                    }
                }));
            }

        }

    }

    public void execute(String url) {
        String[] type = url.split("=");
        this.url_type = type[1];
        onPreExecute();

    }

    public void execute() {

        onPreExecute();

    }
}