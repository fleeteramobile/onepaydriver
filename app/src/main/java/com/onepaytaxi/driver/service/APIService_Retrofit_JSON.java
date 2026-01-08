package com.onepaytaxi.driver.service;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.onepaytaxi.driver.MyApplication;
import com.onepaytaxi.driver.R;
import com.onepaytaxi.driver.data.CommonData;
import com.onepaytaxi.driver.interfaces.APIResult;

import com.onepaytaxi.driver.utils.NC;
import com.onepaytaxi.driver.utils.NetworkStatus;
import com.onepaytaxi.driver.utils.SessionSave;
import com.onepaytaxi.driver.utils.Systems;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * @author developer This AsyncTask used to communicate the application with server through Volley framework. Here the response completely in JSON format. Constructor get the input details List<NameValuePair>,POST or GET then url. In pre execute,Show the progress dialog. In Background,Connect and get the response. In Post execute, Return the result with interface. This class call the API without any progress on UI.
 */
public class APIService_Retrofit_JSON {
    private boolean dont_encode;
    public Context mContext;
    private boolean isSuccess = true;
    private final boolean GetMethod;
    private Dialog mDialog;
    private JSONObject data;
    public APIResult response;
    public boolean wholeURL;
    String result = "";
    private String url_type;

    public APIService_Retrofit_JSON(Context ctx, APIResult res, JSONObject j, boolean getmethod) {
        mContext = ctx;
        response = res;
        this.data = j;
        GetMethod = getmethod;
    }

    public APIService_Retrofit_JSON(Context ctx, APIResult res, boolean getmethod, String url) {
        mContext = ctx;
        response = res;
        this.data = null;
        GetMethod = getmethod;
        dont_encode = true;
        wholeURL = true;
        url_type = url;
    }

    public APIService_Retrofit_JSON(Context ctx, APIResult res, String j, boolean getmethod) {
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


    public APIService_Retrofit_JSON(Context ctx, APIResult res, JSONObject j, boolean getmethod, String url, boolean dont_encode) {
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


    public APIService_Retrofit_JSON(Context ctx, APIResult res, boolean getmethod) {
        mContext = ctx;
        response = res;
        GetMethod = getmethod;
    }

    //@Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        showDialog();
        doInBackground();
    }

    public void showDialog() {
        try {
            if (NetworkStatus.isOnline(mContext)) {
                if (mDialog != null && mContext != null)
                    mDialog.dismiss();
                View view = View.inflate(mContext, R.layout.progress_bar, null);
                mDialog = new Dialog(mContext, R.style.dialogwinddow);

                mDialog.setContentView(view);
                mDialog.setCancelable(false);
                try {
                    if (mContext != null)
                        mDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                ImageView iv = mDialog.findViewById(R.id.giff);
                DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(iv);
                Glide.with(mContext)
                        .load(R.raw.loading_anim)
                        .into(imageViewTarget);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void closeDialog() {
        try {
            if (mDialog != null)
                if (mDialog.isShowing())
                    mDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@Override
    protected void doInBackground() {
        // TODO Auto-generated method stub
        if (!NetworkStatus.isOnline(mContext)) {
            isSuccess = false;
            response.getResult(false, NC.getResources().getString(R.string.please_check_internet));
            result = NC.getString(R.string.please_check_internet);
            //return result;
        } else {
            if (GetMethod) {
                CoreClient client;
                Systems.out.println("rrc_____get" + url_type + "___" + wholeURL + "___" + data);
                if(dont_encode){
                    client = MyApplication.getInstance().getApiManagerWithoutEncryptBaseUrl();
                }else{
                    client = MyApplication.getInstance().getApiManagerWithEncryptBaseUrl();
                }
//                client = new ServiceGenerator(mContext, dont_encode).createService(CoreClient.class);
                Call<ResponseBody> coreResponse = null;
                if (!wholeURL)
                    coreResponse = client.coreDetails("","no-cache",
                            url_type, "0"
                            , SessionSave.getSession(CommonData.ACCESS_KEY, mContext));
                else
                    coreResponse = client.getWhole("no-cache", url_type);
                coreResponse.enqueue(new RetrofitCallbackClass<>(mContext, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        String data = null;
                        closeDialog();
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    data = response.body().string();
                                    if (APIService_Retrofit_JSON.this.response != null)
                                        APIService_Retrofit_JSON.this.response.getResult(true, data);
                                } else {
                                    if (APIService_Retrofit_JSON.this.response != null)
                                        APIService_Retrofit_JSON.this.response.getResult(false, null);
                                   // CToast.ShowToast(mContext, NC.getString(R.string.server_error));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (APIService_Retrofit_JSON.this.response != null)
                                    APIService_Retrofit_JSON.this.response.getResult(false, null);
                               // CToast.ShowToast(mContext, NC.getString(R.string.server_error));
                            }
                        } else {
                            if (APIService_Retrofit_JSON.this.response != null)
                                APIService_Retrofit_JSON.this.response.getResult(false, null);
                            //CToast.ShowToast(mContext, NC.getString(R.string.server_error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (APIService_Retrofit_JSON.this.response != null)
                            APIService_Retrofit_JSON.this.response.getResult(false, null);
                      //  CToast.ShowToast(mContext, NC.getString(R.string.server_error));
                        t.printStackTrace();
                        closeDialog();
                    }
                }));

            } else {
//                CoreClient client = new ServiceGenerator(mContext, dont_encode).createService(CoreClient.class);

                CoreClient client;

                if(dont_encode){
                    client = MyApplication.getInstance().getApiManagerWithoutEncryptBaseUrl();
                }else{
                    client = MyApplication.getInstance().getApiManagerWithEncryptBaseUrl();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (data).toString());

                Call<ResponseBody> coreResponse = client.updateUser(ServiceGenerator.COMPANY_KEY, body, url_type,  SessionSave.getSession("Lang", mContext));
                coreResponse.enqueue(new RetrofitCallbackClass<>(mContext, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        String data = null;
                        closeDialog();
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    data = response.body().string();
                                    if (APIService_Retrofit_JSON.this.response != null)
                                        APIService_Retrofit_JSON.this.response.getResult(true, data);
                                } else {
                                    if (APIService_Retrofit_JSON.this.response != null)
                                        APIService_Retrofit_JSON.this.response.getResult(false, null);
                                   // CToast.ShowToast(mContext, NC.getString(R.string.server_error));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (APIService_Retrofit_JSON.this.response != null)
                                    APIService_Retrofit_JSON.this.response.getResult(false, null);
                               // CToast.ShowToast(mContext, NC.getString(R.string.server_error));
                            }
                        } else {
                            if (APIService_Retrofit_JSON.this.response != null)
                                APIService_Retrofit_JSON.this.response.getResult(false, null);
                            //CToast.ShowToast(mContext, NC.getString(R.string.server_error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (APIService_Retrofit_JSON.this.response != null)
                            APIService_Retrofit_JSON.this.response.getResult(false, null);
                        //CToast.ShowToast(mContext, NC.getString(R.string.server_error));
                        t.printStackTrace();
                        closeDialog();
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