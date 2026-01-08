package com.onepaytaxi.driver.earnings

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.onepaytaxi.driver.MyApplication
import com.onepaytaxi.driver.data.apiData.ApiRequestData
import com.onepaytaxi.driver.databinding.ActivityEarningsBinding
import com.onepaytaxi.driver.service.ServiceGenerator
import com.onepaytaxi.driver.utils.SessionSave
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EarningsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEarningsBinding
    private var weeklyList: List<ResponseEarnings.WeeklyEarning> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEarningsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        loadEarnings()
    }


    fun loadEarnings() {
        val client =
            MyApplication.getInstance().getApiManagerWithEncryptBaseUrl()

        val request = ApiRequestData.Earnings().apply {
            driver_id = SessionSave.getSession("Id", this@EarningsActivity)
        }

        client.callDatanew(ServiceGenerator.COMPANY_KEY, request)
            .enqueue(object : Callback<ResponseEarnings> {

                override fun onResponse(
                    call: Call<ResponseEarnings>,
                    response: Response<ResponseEarnings>
                ) {
                    val data = response.body() ?: return
                    if (data!!.status != 1) return
                    bindWalletAmount(data)     // ✅ HERE

                    weeklyList = data.weekly_earnings
                    setupWeekTabs()
                    selectWeek(0)
                }

                override fun onFailure(call: Call<ResponseEarnings>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    private fun bindWalletAmount(data: ResponseEarnings) {

        val withdraw = data.withdraw_array.firstOrNull() ?: return

        val currency = SessionSave.getSession("site_currency", this)
        val walletBalance = withdraw.wallet_balance

        binding.tvWalletAmount.text = "$currency $walletBalance"
    }

    private fun setupWeekTabs() {
        binding.weekContainer.removeAllViews()

        weeklyList.forEachIndexed { index, week ->
            val tv = TextView(this).apply {
                text = week.date_text
                textSize = 14f
                setPadding(32, 16, 32, 16)
                setTextColor(Color.BLACK)
                setOnClickListener { selectWeek(index) }
            }
            binding.weekContainer.addView(tv)
        }


    }

    private fun selectWeek(position: Int) {

        for (i in 0 until binding.weekContainer.childCount) {
            val tv = binding.weekContainer.getChildAt(i) as TextView
            tv.setTextColor(
                if (i == position) Color.parseColor("#7B5CFF")
                else Color.BLACK
            )
        }

        val week = weeklyList[position]

        binding.tvWeekAmount.text = "₹${week.this_week_earnings}"

        // 🔥 CONVERT String → Float SAFELY
        val amountList = week.trip_amount.map {
            it.toFloatOrNull() ?: 0f
        }

        setupBarChart(week.day_list, amountList)
    }

    private fun setupBarChart(days: List<String>, amounts: List<Float>) {

        val entries = ArrayList<BarEntry>()

        amounts.forEachIndexed { index, value ->
            entries.add(BarEntry(index.toFloat(), value))
        }

        val dataSet = BarDataSet(entries, "").apply {
            color = Color.parseColor("#6A5AE0")
            valueTextSize = 10f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "₹${value.toInt()}"
                }
            }
        }

        binding.barChart.apply {
            data = BarData(dataSet).apply { barWidth = 0.5f }
            description.isEnabled = false
            legend.isEnabled = false
            setFitBars(true)
            animateY(800)

            axisRight.isEnabled = false
            axisLeft.axisMinimum = 0f

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(days)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
            }

            invalidate()
        }
    }


}
