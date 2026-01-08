package com.onepaytaxi.driver.service

import android.content.Context
import com.onepaytaxi.driver.MyApplication
import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.data.CommonData
import com.onepaytaxi.driver.utils.CToast
import com.onepaytaxi.driver.utils.NC
import com.onepaytaxi.driver.utils.NetworkStatus
import com.onepaytaxi.driver.utils.SessionSave
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.util.*

class NodeAuth private constructor() {


    companion object {
        private var isAuthCallInProgress = false
        // For Singleton instantiation
        @Volatile
        private var instance: NodeAuth? = null

        @JvmStatic
        fun getInstance(): NodeAuth {
            return instance ?: synchronized(this) {
                instance ?: NodeAuth().also { instance = it }
            }
        }
    }

    fun getAuth(context: Context) {
        if (!isAuthCallInProgress) {
            if (NetworkStatus.isOnline(context)) {
                isAuthCallInProgress = true
                val data = JSONObject()
                try {
//                    if (UUID.randomUUID().toString() != "") {
//                        data.put("device_id", UUID.randomUUID().toString())
//                        data.put("device_id", UUID.randomUUID().toString())
//                    } else {
//                        data.put("device_id", CommonData.mDevice_id_constant)
//                    }

                    data.put("device_id", SessionSave.getSession("sDevice_id",  context))


//                    data.put("device_id", Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

//                val client = NodeServiceGenerator(context, false, SessionSave.getSession(CommonData.NODE_URL, context), 30).createService(CoreClient::class.java)
                val client = MyApplication.getInstance().getNodeApiManagerWithTimeOut(SessionSave.getSession(CommonData.NODE_URL, context), 30)

                val body = data.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val coreResponse = client.nodeAuth(body)
                coreResponse.enqueue(RetrofitCallbackClass(context, object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                        val data: String?
                        isAuthCallInProgress = false

                        if (response.isSuccessful) {
                            try {
                                if (response.body() != null) {
                                    data = response.body()!!.string()
                                    val json = JSONObject(data)
                                    SessionSave.saveSession(CommonData.NODE_TOKEN, json.getString("token"), context)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                               // CToast.ShowToast(context, NC.getString(R.string.server_error))
                            }
                        }
                        /*else
                            CToast.ShowToast(context, NC.getString(R.string.server_error))*/

                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        t.printStackTrace()
                        isAuthCallInProgress = false
                       // CToast.ShowToast(context, NC.getString(R.string.server_error))
                    }
                }))
            } else {
                CToast.ShowToast(context, NC.getString(R.string.check_net_connection))
            }
        }
    }

}