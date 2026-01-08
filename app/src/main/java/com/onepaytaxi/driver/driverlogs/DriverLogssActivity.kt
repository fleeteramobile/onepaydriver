package com.onepaytaxi.driver.driverlogs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.interfaces.APIResult
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON
import com.onepaytaxi.driver.utils.SessionSave
import org.json.JSONException
import org.json.JSONObject

class DriverLogssActivity : AppCompatActivity() {
    private val mExampleData: ArrayList<ExampleData>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_logss)
        try {
            val j = JSONObject()
            j.put("driver_id", SessionSave.getSession("Id", this@DriverLogssActivity))
            val url = "type=driver_report_list"
            callWalletHistory(url, j)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    inner class callWalletHistory(url: String?, data: JSONObject?) : APIResult {
        init {

            APIService_Retrofit_JSON(this@DriverLogssActivity, this, data, false).execute(
                url
            )

        }

        override fun getResult(isSuccess: Boolean, result: String) {
            if (isSuccess) {
                try {
                    val json = JSONObject(result)
                    val json2 = json.getJSONObject("detail")
                    val mWalletArray = json2.getJSONArray("driver_report")
                    for (i in 0 until mWalletArray.length()) {
                        val mJsonObject = mWalletArray.getJSONObject(i)


                        mExampleData!!.add(
                            ExampleData(
                                mJsonObject.getString("_id"),

                                mJsonObject.getString("driver_id"),
                                mJsonObject.getString("wallet_item"),
                                mJsonObject.getString("description"),
                                mJsonObject.getString("added_amount"),
                                mJsonObject.getString("balance"),
                                mJsonObject.getString("createdate"),
                                mJsonObject.getString("passenger_name"),
                                mJsonObject.getString("plus_minus")


                            )
                        )
                        // StatementData mStatementData = new StatementData();

                        //  mStatementData.setCreatedate(mJsonObject.getString("createdate"));

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                } finally {
                    //mExampleData!!.groupBy { it.createdate }
                    println("Group_data"+" "+mExampleData!!.groupBy { it.createdate })
                    // Set adapter to recyclerView

                    //     mAdapter.notifyDataSetChanged();
                }
            } else {
                runOnUiThread(Runnable {
                })
            }
        }
    }

}