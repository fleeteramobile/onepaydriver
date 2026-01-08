package com.onepaytaxi.driver.service

import android.content.Context
import android.text.TextUtils
import com.onepaytaxi.driver.BuildConfig
import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.data.CommonData
import com.onepaytaxi.driver.utils.InternetSpeedChecker
import com.onepaytaxi.driver.utils.SessionSave
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.security.*
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


object NodeServiceGenerator {
    private lateinit var httpClient: OkHttpClient.Builder
    private lateinit var builder: Retrofit.Builder

    fun nodeGetRetrofitWithTimeOut(context: Context, base_url: String, timeOut: Long): Retrofit {
        val logging = HttpLoggingInterceptor()
        val b = RequestInterceptor(context)
        // set your desired log level
        logging.level = HttpLoggingInterceptor.Level.BODY

        httpClient = OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)/*.certificatePinner(certificatePinner)*/

        val d = DecryptInterceptor(context)
        httpClient.interceptors().add(b)


        httpClient.addInterceptor(d)
        if (BuildConfig.DEBUG)
            httpClient.interceptors().add(logging)
        initSSL(context)

        builder = Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create()).client(httpClient.build())
        return builder.build()
    }


    private fun initSSL(context: Context) {
        var sslContext: SSLContext? = null
        try {
            sslContext = createCertificate(context.resources.openRawResource(R.raw.cert))
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        if (sslContext != null) {
            systemDefaultTrustManager()?.let { httpClient.sslSocketFactory(sslContext.socketFactory, it) }
        }
    }


    @Throws(CertificateException::class, IOException::class, KeyStoreException::class, KeyManagementException::class, NoSuchAlgorithmException::class)
    private fun createCertificate(trustedCertificateIS: InputStream): SSLContext? {
        val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
        val ca: Certificate
        ca = trustedCertificateIS.use { trustedCertificateIS ->
            cf.generateCertificate(trustedCertificateIS)
        }

        // creating a KeyStore containing our trusted CAs
        val keyStoreType: String = KeyStore.getDefaultType()
        val keyStore: KeyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", ca)

        // creating a TrustManager that trusts the CAs in our KeyStore
        val tmfAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm()
        val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm)
        tmf.init(keyStore)

        // creating an SSLSocketFactory that uses our TrustManager
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, tmf.trustManagers, null)
        return sslContext
    }

    private fun systemDefaultTrustManager(): X509TrustManager? {
        return try {
            val trustManagerFactory: TrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers: Array<TrustManager> = trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) { "Unexpected default trust managers:" + trustManagers.contentToString() }
            trustManagers[0] as X509TrustManager
        } catch (e: GeneralSecurityException) {
            throw AssertionError() // The system has no TLS. Just give up.
        }
    }

    class RequestInterceptor internal constructor(internal var c: Context) : Interceptor {


        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            var builder: Request.Builder = originalRequest.newBuilder()
            if (originalRequest.method.equals("POST", ignoreCase = true)) {
                builder = originalRequest.newBuilder()
                        .method(originalRequest.method, originalRequest.body)
            }
            builder.addHeader("domain", SessionSave.getSession(CommonData.NODE_DOMAIN, c))
            builder.addHeader("Authorization", "FNpfuspyEAzhjfoh2ONpWK0rsnClVL6OCaasqDQtWdI=")
            builder.addHeader("Content-type", "application/json")
            builder.addHeader("version", "${BuildConfig.VERSION_CODE}")
            builder.addHeader("token", SessionSave.getSession(CommonData.NODE_TOKEN, c))
            val originalHttpUrl = originalRequest.url


            val url = originalHttpUrl.newBuilder()

                    .addQueryParameter("pv", "" + BuildConfig.VERSION_CODE)
                    .addQueryParameter("i", SessionSave.getSession("Id", c))
                    .addQueryParameter("lang", SessionSave.getSession("Lang", c))
                    .addQueryParameter("dt", "a")
                    .addQueryParameter("s", InternetSpeedChecker.getDownloadSpeed())
                    .build()

            builder.url(url)

            return chain.proceed(builder.build())
        }

    }


    class DecryptInterceptor internal constructor(internal var c: Context) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {

            val response = chain.proceed(chain.request())
            if (response.isSuccessful) {
                val newResponse = response.newBuilder()
                var contentType = response.header("Content-Type")
                if (TextUtils.isEmpty(contentType)) contentType = "application/json"
                val data = response.body
                if (data != null) {
                    val cryptedStream = data.byteStream()
                    var decrypted: String? = null
                    val result = ByteArrayOutputStream()
                    val buffer = ByteArray(1024)
                    var length: Int = cryptedStream.read(buffer)
                    while (length != -1) {
                        result.write(buffer, 0, length)
                        length = cryptedStream.read(buffer)
                    }

                    try {
//                        if (!result.toString("UTF-8").isEmpty())
//                            decrypted = AA().dd(result.toString("UTF-8"))
                        decrypted = result.toString("UTF-8")

                        newResponse.body(decrypted.toResponseBody(contentType?.toMediaTypeOrNull()))
                        val ress = newResponse.build()
                        return if (CheckStatus(JSONObject(decrypted), c).isNormal())
                            ress
                        else
                            response
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }

            }
            return response
        }


    }

//
//    fun <S> createService(serviceClass: Class<S>): S {
//        val retrofit = builder.client(httpClient.build()).build()
//        return retrofit.create(serviceClass)
//    }
}