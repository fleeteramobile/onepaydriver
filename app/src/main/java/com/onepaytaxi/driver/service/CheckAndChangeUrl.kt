package com.onepaytaxi.driver.service

import android.content.Context
import com.onepaytaxi.driver.MyApplication
import com.onepaytaxi.driver.data.CommonData
import com.onepaytaxi.driver.utils.SessionSave
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


open class CheckUrl {

    fun update(context: Context, newUrl: String, testUrl: String, urlFor: String) {
        val data = JSONObject()
        try {
            val c = AtomicInteger(0)

            var mUUID = ""
            if (CommonData.mDevice_id == "") {
                if (UUID.randomUUID().toString() != "") {
                    mUUID = UUID.randomUUID().toString()
                } else {
                    mUUID = CommonData.mDevice_id_constant + c.incrementAndGet()
                }
                CommonData.mDevice_id = mUUID
            }

            if (SessionSave.getSession("sDevice_id", context) == "") {
                if (UUID.randomUUID().toString() != "") {
                    mUUID = UUID.randomUUID().toString()
                } else {
                    mUUID = CommonData.mDevice_id_constant + c.incrementAndGet()
                }
                SessionSave.saveSession("sDevice_id", mUUID, context)
            }

            data.put("device_id", SessionSave.getSession("sDevice_id", context))

//            data.put("device_id", Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID))
        } catch (e: JSONException) {
            e.printStackTrace()
        }

//        val client = NodeServiceGenerator(context, false, testUrl, 30).createService(CoreClient::class.java)
        val client = MyApplication.getInstance().getNodeApiManagerWithTimeOut(testUrl,30)

        val body = data.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val coreResponse = client.urlCheck(testUrl, body)
        coreResponse.enqueue(RetrofitCallbackClass(context, object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                val data: String?

                try {
                    try {

                        data = response.body()!!.string()
                        val json = JSONObject(data)
                        if (data != null && json.getString("status") == "1") {

                            SessionSave.saveSession(urlFor, newUrl, context)
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        }))
    }
}