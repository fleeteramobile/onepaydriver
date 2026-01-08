package com.mayan.sospluginmodlue.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.mayan.sospluginmodlue.util.CToast
import com.mayan.sospluginmodlue.util.SessionSave
import org.json.JSONObject

//Nodejs Log Code
//408				=>	Header not contains domains					(Need Alert)
//408				=>  DB Not connected,Site Config not retrived 	(Need Alert)
//408				=>  Timezone issue								(Need Alert)
//408				=>  Invalid Auth 								(Need Alert)
//408				=>  MongoDB down.
//408				=>  TryCatch Error								(Need Alert)
//
//Return data
//811				=>  Driver Get Current Information
//
//Message without action service stop only
//
//409				=>  Force to Update Build
//412				=>  Request to uninstall current build.
//
//Action with message move to login screen
//
//
//410				=>  Driver App Destory like as new build
//411				=>  Driver Logout
//
//Only action
//601 =>  Web Domain Url Change Request
//602 =>  Node Url Change Request
//603 =>  Token Expired

class CheckStatus(val json: JSONObject, val context: Context) {

    fun isNormal(): Boolean {
        var normal = true

        var statusCode = 0
        if (json.has("status"))
            statusCode = json.getInt("status")

        var message: String? = null
        if (json.has("message")) {
            message = json.getString("message")
        }


        when (statusCode) {
            410 -> {
                normal = false
                SessionSave.clearAllSession(context)
                forceLogout("")
                val cancelIntent = Intent()
                val bun = Bundle()
                bun.putString("alert_message", message ?: "")
                cancelIntent.putExtras(bun)
                cancelIntent.action = Intent.ACTION_MAIN
                cancelIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                cancelIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_SINGLE_TOP
                val cn = ComponentName(context, Class.forName("com.blackertaxi.driver.SplashAct"))
                cancelIntent.component = cn
                context.startActivity(cancelIntent)
            }

            411 -> {
                normal = false
                SessionSave.clearSession(context)
                val cancelIntent = Intent()
                val bun = Bundle()
                bun.putString("message", message ?: "")
                bun.putString("alert_message", message ?: "")
                cancelIntent.putExtras(bun)
                cancelIntent.action = Intent.ACTION_MAIN
                cancelIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                cancelIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_SINGLE_TOP
                val cn = ComponentName(context, Class.forName("com.blackertaxi.driver.UserLoginAct"))
                cancelIntent.component = cn
                context.startActivity(cancelIntent)
            }
            else -> normal = true
        }


        return normal
    }


    /**
     * Method to logout user if status -101 and redirect to login page
     * @param message - To intimate user by showing alert message
     */
    private fun forceLogout(message: String) {
        SessionSave.saveSession("base_url", "", context)
        SessionSave.saveSession("Id", "", context)
        SessionSave.clearAllSession(context)
    }


    fun updateAuthKey() {

        if (json.has("auth_key")) {
            val authKey = json.getString("auth_key")
            if (authKey != null && authKey != "")
                SessionSave.saveSession("auth_key", authKey, context)
        }
        var statusCode = ""
        if (json.has("status"))
            statusCode = json.getString("status")
        var message = ""
        if (json.has("message")) {
            message = json.getString("message")
        }
        when (statusCode) {
            "296" -> {
                handlingInvalidToken(message)
            }
            "128" -> {
                handlingInvalidUserKey(message)
            }
        }
    }


    private fun handlingInvalidToken(message: String) {
        clearSession(context)
        val cancelIntent = Intent()
        if (message != "") {
            val handler = Handler(Looper.getMainLooper())
            handler.post {
                CToast.ShowToast(context, message)
            }
        }
        CheckCompanyDomain().callCheckCompanyDomain(context)
        cancelIntent.action = Intent.ACTION_MAIN
        cancelIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        cancelIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val cn = ComponentName(context, Class.forName("com.blackertaxi.driver.UserLoginAct"))
        cancelIntent.component = cn
        context.startActivity(cancelIntent)
    }

    private fun handlingInvalidUserKey(message: String) {
        clearSession(context)
        val cancelIntent = Intent()
        if (message != "") {
            val handler = Handler(Looper.getMainLooper())
            handler.post {
                CToast.ShowToast(context, message)
            }
        }
        cancelIntent.action = Intent.ACTION_MAIN
        cancelIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        cancelIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val cn = ComponentName(context, Class.forName("com.blackertaxi.driver.UserLoginAct"))
        cancelIntent.component = cn
        context.startActivity(cancelIntent)
    }

    private fun clearSession(ctx: Context) {

        try {
            SessionSave.saveSession("status", "", ctx)
            SessionSave.saveSession("Id", "", ctx)
            SessionSave.saveSession("Driver_locations", "", ctx)
            SessionSave.saveSession("driver_id", "", ctx)
            SessionSave.saveSession("Name", "", ctx)
            SessionSave.saveSession("company_id", "", ctx)
            SessionSave.saveSession("bookedby", "", ctx)
            SessionSave.saveSession("p_image", "", ctx)
            SessionSave.saveSession("Email", "", ctx)
            SessionSave.saveSession("phone_number", "", ctx)
            SessionSave.saveSession("driver_password", "", ctx)
            SessionSave.saveSession("trip_id", "", ctx)
            SessionSave.setWaitingTime(0L, ctx)

        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
        }

    }
}