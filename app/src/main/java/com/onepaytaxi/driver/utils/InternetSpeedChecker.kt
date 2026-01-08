package com.onepaytaxi.driver.utils

import android.net.TrafficStats
import java.util.*

object InternetSpeedChecker {
    private lateinit var mDownloadSpeedOutput: String
    private lateinit var mUnits: String
    fun getDownloadSpeed():String {

        val mRxBytesPrevious = TrafficStats.getTotalRxBytes()
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        val mRxBytesCurrent = TrafficStats.getTotalRxBytes()

        val mDownloadSpeed = mRxBytesCurrent - mRxBytesPrevious

        val mDownloadSpeedWithDecimals: Float

        when {
            mDownloadSpeed >= 1000000000 -> {
                mDownloadSpeedWithDecimals = mDownloadSpeed.toFloat() / 1000000000.toFloat()
                mUnits = " GB"
            }
            mDownloadSpeed >= 1000000 -> {
                mDownloadSpeedWithDecimals = mDownloadSpeed.toFloat() / 1000000.toFloat()
                mUnits = " MB"

            }
            else -> {
                mDownloadSpeedWithDecimals = mDownloadSpeed.toFloat() / 1000.toFloat()
                mUnits = " KB"
            }
        }


        mDownloadSpeedOutput = if (mUnits != " KB" && mDownloadSpeedWithDecimals < 100) {
            String.format(Locale.US, "%.1f", mDownloadSpeedWithDecimals)
        } else {
            mDownloadSpeedWithDecimals.toInt().toString()
        }
        println("mDownloadSpeedOutput$mDownloadSpeedOutput")
        return mDownloadSpeedOutput
    }
}