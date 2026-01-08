package com.onepaytaxi.driver.settlement

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.TimePicker

import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onepaytaxi.driver.R

import com.onepaytaxi.driver.adapter.StatementListAdapter
import com.onepaytaxi.driver.data.apiData.StatementData
import com.onepaytaxi.driver.interfaces.APIResult
import com.onepaytaxi.driver.service.APIService_Retrofit_JSON
import com.onepaytaxi.driver.utils.SessionSave
import org.json.JSONException
import org.json.JSONObject
import java.util.Calendar
import java.util.Locale

class SettlementActivity : AppCompatActivity() {

    var recyclerView: RecyclerView? = null
    var txt_credit_val: TextView? = null
    var btn_req: CardView? = null

    var leftIcon: TextView? = null
    var header_titleTxt: TextView? = null
    var txt_nodata: TextView? = null
    var showProgress: FrameLayout? = null
    var stat_adapter: StatementListAdapter? = null
    private var statData: ArrayList<StatementData?> = ArrayList<StatementData?>()

    var history_recyclerView: RecyclerView? = null


    var no_data: TextView? = null
    var header_titleTxt1: TextView? = null
    var header_titleTxt2: TextView? = null
    var back_text: TextView? = null
    var wallet_amount: TextView? = null

    var callText: ImageView? = null
    var back_menu: CardView? = null

    var filter_icon: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settlement)

        history_recyclerView = findViewById<RecyclerView?>(R.id.corporate_recyclerView)
        val llm = LinearLayoutManager(this)
        llm.setOrientation(LinearLayoutManager.VERTICAL)
        history_recyclerView!!.setLayoutManager(llm)
        stat_adapter = StatementListAdapter(this@SettlementActivity, statData)
        history_recyclerView!!.setAdapter(stat_adapter)

        back_text = findViewById<TextView?>(R.id.back_text)
        header_titleTxt1 = findViewById<TextView?>(R.id.header_titleTxt1)
        header_titleTxt2 = findViewById<TextView?>(R.id.header_titleTxt2)
        callText = findViewById<ImageView?>(R.id.callText)
        no_data = findViewById<TextView?>(R.id.nodataTxt)
        wallet_amount = findViewById<TextView?>(R.id.wallet_amount)

        filter_icon = findViewById<ImageView?>(R.id.filter_icon)
        header_titleTxt1!!.setVisibility(View.VISIBLE)
        header_titleTxt2!!.setVisibility(View.GONE)
        filter_icon!!.setVisibility(View.VISIBLE)

        back_text!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                onBackPressed()
            }
        })
        callText!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                onBackPressed()
            }
        })
        filter_icon!!.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(api = Build.VERSION_CODES.N)
            override fun onClick(view: View?) {
                filterDate()
            }


        })


        try {
            val j = JSONObject()
            j.put("driver_id", SessionSave.getSession("Id", this@SettlementActivity))

            // j.put("start", start);
            // j.put("limit", limit);
            val url = "type=driver_report_list"
            callWalletHistory(url, j)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    fun filterDate() {
        try {
            val view = View.inflate(this@SettlementActivity, R.layout.date_filter_dialog, null)
            val mcancelDialog = Dialog(this@SettlementActivity, R.style.dialogwinddow)
            mcancelDialog.setContentView(view)
            mcancelDialog.setCancelable(true)
            mcancelDialog.show()



            val button_success = mcancelDialog.findViewById<Button?>(R.id.okbtn)
            val button_failure = mcancelDialog.findViewById<Button?>(R.id.cancelbtn)

            val ToDate = mcancelDialog.findViewById<EditText?>(R.id.ToDate)
            val FromDate = mcancelDialog.findViewById<EditText?>(R.id.FromDate)
            val dateFormatter: SimpleDateFormat?
            var FilterToDate: String?
            var FilterFromDate: String?
            ToDate.setInputType(InputType.TYPE_NULL)
            FromDate.setInputType(InputType.TYPE_NULL)
            ToDate.requestFocus()
            dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

            FromDate.setOnClickListener(object : View.OnClickListener {
                @RequiresApi(api = Build.VERSION_CODES.O)
                override fun onClick(view: View?) {
                    val calendar = Calendar.getInstance(Locale.getDefault())
                    val newDate = Calendar.getInstance()
                    val datePickerDialog = DatePickerDialog(
                        this@SettlementActivity,
                        object : OnDateSetListener {
                            override fun onDateSet(
                                view: DatePicker?,
                                year: Int,
                                month: Int,
                                dayOfMonth: Int
                            ) {
                                //todo
                                newDate.set(year, month, dayOfMonth)
                                //                                    FromDate.setText(dateFormatter.format(newDate.getTime()));
                                val timePickerDialog = TimePickerDialog(
                                    this@SettlementActivity,
                                    object : OnTimeSetListener {
                                        override fun onTimeSet(
                                            timePicker: TimePicker?,
                                            hour: Int,
                                            minute: Int
                                        ) {
                                            newDate.set(Calendar.HOUR_OF_DAY, hour)
                                            newDate.set(Calendar.MINUTE, minute)

                                            FromDate.setText(dateFormatter.format(newDate.getTime()) + ":00")


                                        }
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    false
                                )
                                timePickerDialog.show()
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    datePickerDialog.show()
                }
            })

            ToDate.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                    val calendar = Calendar.getInstance(Locale.getDefault())
                    val newDate = Calendar.getInstance()
                    val datePickerDialog = DatePickerDialog(
                        this@SettlementActivity,
                        object : OnDateSetListener {
                            override fun onDateSet(
                                view: DatePicker?,
                                year: Int,
                                month: Int,
                                dayOfMonth: Int
                            ) {
                                //todo

                                newDate.set(year, month, dayOfMonth)

                                //                                    ToDate.setText(dateFormatter.format(newDate.getTime()));
                                val timePickerDialog = TimePickerDialog(
                                    this@SettlementActivity,
                                    object : OnTimeSetListener {
                                        override fun onTimeSet(
                                            timePicker: TimePicker?,
                                            hour: Int,
                                            minute: Int
                                        ) {
                                            newDate.set(Calendar.HOUR_OF_DAY, hour)
                                            newDate.set(Calendar.MINUTE, minute)

                                            ToDate.setText(dateFormatter.format(newDate.getTime()) + ":00")

                                        }
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    false
                                )
                                timePickerDialog.show()
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    datePickerDialog.show()
                }
            })

            button_success.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    mcancelDialog.dismiss()
                    try {
                        val j = JSONObject()
                        j.put("driver_id", SessionSave.getSession("Id", this@SettlementActivity))
                        j.put("from_date", ToDate.getText().toString())
                        j.put("to_date", FromDate.getText().toString())

                        val url = "type=driver_report_list"
                        callWalletHistory(url, j)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })


            button_failure.setVisibility(View.VISIBLE)
            button_failure.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    mcancelDialog.dismiss()
                }
            })
        } catch (e: java.lang.Exception) {
            // TODO: handle exception
            e.printStackTrace()
        }
    }
    inner class callWalletHistory(url: String?, data: JSONObject) : APIResult {
        init {

            APIService_Retrofit_JSON(applicationContext, this, data, false).execute(url)

        }

        override fun getResult(isSuccess: Boolean, result: String) {
            if (isSuccess) {

                try {
                    statData.clear()

                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {
                        val json2 = json.getJSONObject("detail")

                        val mWalletArray = json2.getJSONArray("driver_report")

                        wallet_amount!!.setText(
                            SessionSave.getSession(
                                "site_currency",
                                this@SettlementActivity
                            ) + " " + mWalletArray.getJSONObject(0).getString("balance")
                        )

                        for (i in 0 until mWalletArray.length()) {
                            val jsonObject = mWalletArray.getJSONObject(i)
                            val statementData = StatementData().apply {
                                added_amount = jsonObject.getString("added_amount")
                                balance = jsonObject.getString("balance")
                                createdate = jsonObject.getString("createdate")
                                description = jsonObject.getString("description")
                                wallet_item = jsonObject.getString("wallet_item")
                            }
                            statData.add(statementData)
                        }


                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }finally {
                    if (statData.size == 0) {
                        no_data!!.setVisibility(View.VISIBLE)
                    } else {
                        no_data!!.setVisibility(View.GONE)
                        stat_adapter = StatementListAdapter(this@SettlementActivity, statData)
                        history_recyclerView!!.setAdapter(stat_adapter)
                        stat_adapter!!.notifyDataSetChanged()

                    }
                }


            } else {
                // runOnUiThread(() -> ShowToast(MainActivity.this, NC.getString(R.string.server_error)));
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}