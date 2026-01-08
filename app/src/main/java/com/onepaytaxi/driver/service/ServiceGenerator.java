package com.onepaytaxi.driver.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.onepaytaxi.driver.BuildConfig;
import com.onepaytaxi.driver.R;
import com.onepaytaxi.driver.data.CommonData;
import com.onepaytaxi.driver.utils.InternetSpeedChecker;
import com.onepaytaxi.driver.utils.SessionSave;

import org.jetbrains.annotations.NotNull;
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
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.onepaytaxi.driver.data.CommonData.AUTH_KEY;
import static com.onepaytaxi.driver.data.CommonData.USER_KEY;

/**
 * Created by developer on 8/31/16.
 */
public class ServiceGenerator {
    public static final String COMPANY_KEY = "=";
    public static String API_BASE_URL = "";
    private static OkHttpClient.Builder httpClient;
    private static Retrofit.Builder builder;


    public static Retrofit getRetrofitWithoutEncryptBaseUrl(Context context) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        Base64EncodeRequestInterceptor requestInterceptor = new Base64EncodeRequestInterceptor(SessionSave.getSession("api_key", context), context);
        DecryptedPayloadInterceptor d = new DecryptedPayloadInterceptor(context);
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS);

        httpClient.addInterceptor(requestInterceptor);
        if (BuildConfig.DEBUG)
            httpClient.interceptors().add(logging);
        builder = new Retrofit.Builder()
                .baseUrl(SessionSave.getSession("base_url", context))
                .addConverterFactory(GsonConverterFactory.create()).client(httpClient.build());
        return builder.build();
    }


    public static Retrofit getRetrofitWithEncryptBaseUrl(Context context) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        Base64EncodeRequestInterceptor requestInterceptor = new Base64EncodeRequestInterceptor(SessionSave.getSession("api_key", context), context);
        DecryptedPayloadInterceptor responseInterceptor = new DecryptedPayloadInterceptor(context);
        httpClient = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS);

        httpClient.addInterceptor(responseInterceptor);
        httpClient.addInterceptor(requestInterceptor);


        if (BuildConfig.DEBUG)
            httpClient.interceptors().add(logging);
        builder = new Retrofit.Builder()
                .baseUrl(SessionSave.getSession("base_url", context))
                .addConverterFactory(GsonConverterFactory.create()).client(httpClient.build());
        return builder.build();
    }


    public static Retrofit getRetrofitEncryptUrl(Context context, String url) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        Base64EncodeRequestInterceptor requestInterceptor = new Base64EncodeRequestInterceptor(SessionSave.getSession("api_key", context), context);
        DecryptedPayloadInterceptor responseInterceptor = new DecryptedPayloadInterceptor(context);

        httpClient = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS);

    /*    if (BuildConfig.DEBUG) {
            httpClient.addNetworkInterceptor(new StethoInterceptor());
            httpClient.interceptors().add(logging);
        }*/
        if (BuildConfig.DEBUG)
        httpClient.interceptors().add(logging);

        httpClient.addInterceptor(responseInterceptor);
        httpClient.addInterceptor(requestInterceptor);

        builder = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create()).client(httpClient.build());
        return builder.build();
    }


    public static class DecryptedPayloadInterceptor implements Interceptor {
        Context c;

        DecryptedPayloadInterceptor(Context c) {
            this.c = c;
        }

        @NotNull
        @Override
        public Response intercept(final Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
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
                        decrypted = result.toString("UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (decrypted == null || decrypted.trim().isEmpty()) {

                    Handler handler = new Handler(Looper.getMainLooper());
                   // handler.post(() -> CToast.ShowToast(c.getApplicationContext(), NC.getString(R.string.server_error)));
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


    public static class Base64EncodeRequestInterceptor implements Interceptor {
        String companyKey = "FNpfuspyEAzhjfoh2ONpWK0rsnClVL6OCaasqDQtWdI=";
        //        String companyKey = "eBU2X1fY+P5G7/nR1S2AsUW7dOaU6KXM3S+b4vYYFs4=";
        private final Context mContext;

        Base64EncodeRequestInterceptor(String key, Context mContext) {
            if (!key.trim().isEmpty())
                companyKey = key;

            this.mContext = mContext;
        }


        @NotNull
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request.Builder builder = originalRequest.newBuilder();
            if (originalRequest.method().equalsIgnoreCase("POST")) {
                builder = originalRequest.newBuilder()
                        .method(originalRequest.method(), originalRequest.body());
            }

            builder.addHeader("authkey", SessionSave.getSession(AUTH_KEY, mContext));
//            builder.addHeader("token", SessionSave.getSession(DEVICE_ID, mContext));
            builder.addHeader("userAuth", SessionSave.getSession(USER_KEY, mContext));




            HttpUrl originalHttpUrl = originalRequest.url();
            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("dt", "a")
                    .addQueryParameter("i", SessionSave.getSession("Id", mContext))
                    .addQueryParameter("pv", "" + BuildConfig.VERSION_CODE)
                    .addQueryParameter("k", SessionSave.getSession(CommonData.FIREBASE_KEY, mContext))
                    .addQueryParameter("s", InternetSpeedChecker.INSTANCE.getDownloadSpeed())
                    .build();

            builder.url(url);


            return chain.proceed(builder.build());
        }

    }


}
