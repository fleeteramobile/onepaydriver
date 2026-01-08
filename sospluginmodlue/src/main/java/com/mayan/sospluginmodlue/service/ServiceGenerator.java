package com.mayan.sospluginmodlue.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.mayan.sospluginmodlue.BuildConfig;
import com.mayan.sospluginmodlue.R;
import com.mayan.sospluginmodlue.util.SessionSave;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by developer on 8/31/16.
 */
public class ServiceGenerator {
    public static final String DYNAMIC_AUTH_KEY = "";
    public static final String COMPANY_KEY = "=";
    public static String API_BASE_URL = "";
    private final OkHttpClient.Builder httpClient;
    private final Retrofit.Builder builder;
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

    //  public static String API_BASE_URL = "http://revamp.taximobility.com/mobileapi117/index/";
//http://revamp.taximobility.com/


    public ServiceGenerator(Context c,String url) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        Base64EncodeRequestInterceptor b = new Base64EncodeRequestInterceptor(SessionSave.getSession("api_key", c), c);
        DecryptedPayloadInterceptor d = new DecryptedPayloadInterceptor(c);

        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS);

        httpClient.interceptors().add(b);
        httpClient.addInterceptor(d);
        if (BuildConfig.DEBUG)
            httpClient.interceptors().add(logging);
        String baseUrl = "";
        if (url.equals("")) {
            baseUrl = SessionSave.getSession("base_url", c);
        } else {
            baseUrl = url;
        }
        builder =
                new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create());
    }

    public <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }


    public class DecryptedPayloadInterceptor implements Interceptor {


        Context c;

        public DecryptedPayloadInterceptor(Context c) {
            this.c = c;
        }

        @Override
        public Response intercept(final Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            //   Systems.out.println("!!!!*******__" + response.body());
            if (response.isSuccessful()) {
                Response.Builder newResponse = response.newBuilder();
                String contentType = response.header("Content-Type");
                if (TextUtils.isEmpty(contentType)) contentType = "application/json";
                InputStream cryptedStream = response.body().byteStream();
                String decrypted = null;
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = cryptedStream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }

                try {
                    if (!result.toString("UTF-8").isEmpty())
//                        decrypted = new AA().dd(result.toString("UTF-8"));
                        decrypted = result.toString("UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (decrypted == null || decrypted.trim().isEmpty()) {

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {

                        @Override
                        public void run() {

                            Toast.makeText(c.getApplicationContext(), c.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    try {
                        new CheckStatus(new JSONObject(decrypted),c).updateAuthKey();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    newResponse.body(ResponseBody.create(MediaType.parse(contentType), decrypted));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cryptedStream != null)
                        cryptedStream.close();
                }
                Response ress = newResponse.build();
                String url_type = String.valueOf(ress.request().url());
                try {
                    if (url_type.contains("driverapi")) {
                        if (new CheckStatus(new JSONObject(decrypted), c).isNormal())
                            return ress;
                        else
                            return response;
                    } else return ress;
                } catch (Exception e) {
                    e.printStackTrace();
                    return ress;
                }
            }
            return response;
        }
    }

    public class Base64EncodeRequestInterceptor implements Interceptor {
        String companyKey = "FNpfuspyEAzhjfoh2ONpWK0rsnClVL6OCaasqDQtWdI=";
        Context mContext;
//        String companyKey = "eBU2X1fY+P5G7/nR1S2AsUW7dOaU6KXM3S+b4vYYFs4=";

        Base64EncodeRequestInterceptor(String key, Context c) {
            if (!key.trim().isEmpty())
                companyKey = key;

            mContext = c;

        }


        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request.Builder builder = originalRequest.newBuilder();
            if (originalRequest.method().equalsIgnoreCase("POST")) {
                builder = originalRequest.newBuilder()
                        .method(originalRequest.method(), originalRequest.body());
            }

            builder.addHeader("authkey", SessionSave.getSession("auth_key", mContext));
            builder.addHeader("token", SessionSave.getSession("device_id", mContext));
            builder.addHeader("userAuth", SessionSave.getSession("user_key", mContext));

            HttpUrl originalHttpUrl = originalRequest.url();


            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("dt", "a")
                    .addQueryParameter("i", SessionSave.getSession("Id", mContext))
                    .addQueryParameter("pv", "1")
                    .build();

            builder.url(url);

            return chain.proceed(builder.build());
        }

    }


}







