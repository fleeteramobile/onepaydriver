package com.onepaytaxi.driver.imageupload

import android.content.Context
import com.onepaytaxi.driver.data.CommonData.AUTH_KEY
import com.onepaytaxi.driver.utils.CommonSettings
import com.onepaytaxi.driver.utils.SessionSave

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private  var BASE_URL = CommonSettings.demo_url


    init {
        BASE_URL = when {
            CommonSettings.isPointingLive -> CommonSettings.demo_url
            CommonSettings.isTestingPonited -> CommonSettings.demo_url
            CommonSettings.isPointingUAT -> CommonSettings.demo_url
            else -> CommonSettings.demo_url
        }


    }

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 🔽 Add a custom interceptor to inject headers
    private val headerInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("authkey", SessionSave.getSession(AUTH_KEY, appContext)) // example from shared preferences
            .addHeader("userAuth", "") // example from shared preferences
            // Add more headers as needed
            .build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor) // 🔁 Add header interceptor before logging
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
