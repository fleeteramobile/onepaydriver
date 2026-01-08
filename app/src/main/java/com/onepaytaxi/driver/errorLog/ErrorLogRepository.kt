package com.onepaytaxi.driver.errorLog

import android.content.Context
import android.os.AsyncTask
import com.google.gson.Gson
import com.onepaytaxi.driver.MyApplication
import com.onepaytaxi.driver.data.CommonData
import com.onepaytaxi.driver.service.RetrofitCallbackClass
import com.onepaytaxi.driver.utils.SessionSave
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ErrorLogRepository private constructor(val mContext: Context) {
    private val errorLogDao = ErrorLogDatabase.getDatabase(mContext).errorLogDao()

    companion object {
        @Volatile
        private var errorLogRepository: ErrorLogRepository? = null

        @JvmStatic
        fun getRepository(mContext: Context): ErrorLogRepository? {
            if (errorLogRepository == null) {
                synchronized(ErrorLogRepository::class.java) {
                    if (errorLogRepository == null) {
                        errorLogRepository = ErrorLogRepository(mContext)
                    }
                }
            }
            return errorLogRepository
        }
    }
/*

    fun getAllErrorLogs() {
        GetAllGpsErrorLogs().execute()
    }

    fun insertLocationLog(locationModel: LocationModel) {
        InsertLocationLog(locationModel).execute()
    }

    fun insertNetworkLog(networkModel: NetworkModel) {
        InsertNetworkLog(networkModel).execute()
    }

    fun insertGpsLog(gpsModel: GpsModel) {
        InsertGpsLog(gpsModel).execute()
        GetAllGpsErrorLogs().execute()
    }

    fun insertApiErrorLog(apiErrorModel: ApiErrorModel) {
        InsertApiErrorLog(apiErrorModel).execute()
    }
*/


    fun insertAllApiErrorLogs(apiErrorModel: ApiErrorModel) {
        println("Log check   ___1")
        if(SessionSave.getSession(CommonData.ERROR_LOGS,mContext,false)) {
            GetCount(apiErrorModel, CommonData.getCurrentTimeForLogger()).execute()
        }
    }

    private inner class GetCount(val apiErrorModel: ApiErrorModel, val timeStamp:String) : AsyncTask<Unit, Unit, Unit>() {
        var count: Int = 0
        override fun doInBackground(vararg params: Unit?) {
            errorLogDao.run {
                println("Log check   ___3")
                count = getCount(apiErrorModel.error,timeStamp)
            }
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            println("Log check   ___3")
            if (count == 0)
                InsertApiErrorLogs(apiErrorModel).execute()
        }
    }


    private inner class GetAllApiErrorLogs : AsyncTask<Unit, Unit, List<ApiErrorModel>?>() {
        override fun doInBackground(vararg params: Unit?): List<ApiErrorModel>? {
            var allErrorLogs: List<ApiErrorModel>? = null
            errorLogDao.run {
                allErrorLogs = getAllApiErrorLogs()
                println("Log check   ___4" + allErrorLogs!!.size)
            }
            return allErrorLogs
        }

        override fun onPostExecute(result: List<ApiErrorModel>?) {
            super.onPostExecute(result)
            println("Log check   ___5")
            DeleteApiErrorLogs(CommonData.getCurrentTimeForLogger()).execute()
            callSubmitErrorLogsApi(result)
        }
    }

    private inner class InsertApiErrorLogs(private val apiErrorModel: ApiErrorModel) : AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg params: Unit?) {
            errorLogDao.run {
                insertApiErrorLog(apiErrorModel)
                println("Log check   ___6")
            }
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            GetAllApiErrorLogs().execute()
            println("Log check   ___7")
        }
    }



    private inner class UpdateApiErrorLogs(val status: Int, val ids: Int) : AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg params: Unit?) {
            errorLogDao.run {
                println("Log check   ___8")
                updateSendStatus(status,ids)
            }
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            println("Log check   ___9")
        }
    }


    private inner class DeleteApiErrorLogs(val date:String) : AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg params: Unit?) {
            errorLogDao.run {
                deleteAllApiErrorLogs(date)
                println("Log check   ___10")
            }
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            println("Log check   ___11")
        }
    }

    private fun callSubmitErrorLogsApi(apiErrorLogs: List<ApiErrorModel>?) {
        val data1 = JSONObject()
        println("List  size ${apiErrorLogs!!.size}")
//        for (i in 0..apiErrorLogs!!.size - 1) {
        val data = JSONObject(Gson().toJson(apiErrorLogs[0]))
            data1.put("ERROR", data)
        println("Log check   ___12")
//        }
//        val client = ServiceGenerator(mContext, false).createService(CoreClient::class.java)
        val client = MyApplication.getInstance().apiManagerWithEncryptBaseUrl
        val body = data1.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val coreResponse = client.errorLogUpdate(body)
        coreResponse.enqueue(RetrofitCallbackClass(mContext, object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                CToast.ShowToast(mContext, "callSubmitErrorLogApi onResponse ${response.isSuccessful}")
//                errorLogDao.run {
//                    println("Log check   ___5")
//                    deleteApiErrorLogs().execute()
//                }

                UpdateApiErrorLogs(1,apiErrorLogs[0].ids).execute()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
//                CToast.ShowToast(mContext, "callSubmitErrorLogApi onFailure ${t.message}")
            }
        }))
    }
/*

    private inner class GetAllGpsErrorLogs() : AsyncTask<Unit, Unit, List<GpsModel>?>() {
        override fun doInBackground(vararg params: Unit?): List<GpsModel>? {
            var allErrorLogs: List<GpsModel>? = null
            errorLogDao?.run {
                allErrorLogs = getAllGpsErrorLogs()
                println("GetAllGpsErrorLogs doInBackground $allErrorLogs")
            }
            return allErrorLogs
        }

        override fun onPostExecute(result: List<GpsModel>?) {
            super.onPostExecute(result)
            GetAllNetworkErrorLogs(result).execute()
        }
    }



*/


/*


    private inner class GetAllNetworkErrorLogs(val gpsErrorLogs: List<GpsModel>?) : AsyncTask<Unit, Unit, List<NetworkModel>?>() {
        override fun doInBackground(vararg params: Unit?): List<NetworkModel>? {
            var allErrorLogs: List<NetworkModel>? = null
            errorLogDao?.run {
                allErrorLogs = getAllNetworkErrorLogs()
                println("GetAllNetworkErrorLogs doInBackground $allErrorLogs")
            }
            return allErrorLogs
        }

        override fun onPostExecute(result: List<NetworkModel>?) {
            super.onPostExecute(result)
            GetAllLocationErrorLogs(gpsErrorLogs, result).execute()
        }
    }

    private inner class GetAllLocationErrorLogs(val gpsErrorLogs: List<GpsModel>?, val networkErrorLogs: List<NetworkModel>?) : AsyncTask<Unit, Unit, List<LocationModel>?>() {
        override fun doInBackground(vararg params: Unit?): List<LocationModel>? {
            var allErrorLogs: List<LocationModel>? = null
            errorLogDao?.run {
                allErrorLogs = getAllLocationErrorLogs()
                println("GetAllLocationErrorLogs doInBackground $allErrorLogs")
            }
            return allErrorLogs
        }

        override fun onPostExecute(result: List<LocationModel>?) {
            super.onPostExecute(result)
            GetAllApiErrorLog(gpsErrorLogs, networkErrorLogs, result).execute()
        }
    }

    private inner class GetAllApiErrorLog(val gpsErrorLogs: List<GpsModel>?, val networkErrorLogs: List<NetworkModel>?, val locationErrorLogs: List<LocationModel>?) : AsyncTask<Unit, Unit, List<ApiErrorModel>?>() {
        override fun doInBackground(vararg params: Unit?): List<ApiErrorModel>? {
            var allErrorLogs: List<ApiErrorModel>? = null
            errorLogDao?.run {
                allErrorLogs = getAllApiErrorLogs()
                println("GetAllApiErrorLog doInBackground $allErrorLogs")
            }
            return allErrorLogs
        }

        override fun onPostExecute(result: List<ApiErrorModel>?) {
            super.onPostExecute(result)
            callSubmitErrorLogApi(gpsErrorLogs, networkErrorLogs, locationErrorLogs, result)
        }
    }

    private fun callSubmitErrorLogApi(gpsErrorLogs: List<GpsModel>?, networkErrorLogs: List<NetworkModel>?, locationErrorLogs: List<LocationModel>?, apiErrorLogs: List<ApiErrorModel>?) {
        val errorModel = AllErrorModel(gpsErrorLogs, networkErrorLogs, locationErrorLogs, apiErrorLogs)
        val data = JSONObject(Gson().toJson(errorModel))
        val client = ServiceGenerator(mContext, false).createService(CoreClient::class.java)
        val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), data.toString())

        val coreResponse = client.errorLogUpdate(body)
        coreResponse.enqueue(RetrofitCallbackClass(mContext, object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                CToast.ShowToast(mContext, "callSubmitErrorLogApi onResponse ${response.isSuccessful}")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                CToast.ShowToast(mContext, "callSubmitErrorLogApi onFailure ${t.message}")
            }
        }))
    }

    private inner class InsertNetworkLog(private val networkModel: NetworkModel) : AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg params: Unit?) {

            errorLogDao?.run {
                deleteNetworkLog()
                insertNetworkLog(networkModel)
            }
        }
    }

    private inner class InsertGpsLog(private val gpsModel: GpsModel) : AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg params: Unit?) {
            errorLogDao?.run {
                deleteGpsLog()
                insertGpsLog(gpsModel)
            }
        }
    }

    private inner class InsertApiErrorLog(private val apiErrorModel: ApiErrorModel) : AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg params: Unit?) {
            errorLogDao?.run {
                deleteApiErrorLog()
                insertApiErrorLog(apiErrorModel)
            }
        }
    }

    private inner class InsertLocationLog(private val locationModel: LocationModel) : AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg params: Unit?) {
            errorLogDao?.run {
                deleteLocationLog()
                insertLocationLog(locationModel)
            }
        }
    }*/
}