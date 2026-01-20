package com.onepaytaxi.driver.pushmessage

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat



import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.onepaytaxi.driver.Login.DriverLoginActivity
import com.onepaytaxi.driver.Login.LoginActivity
import com.onepaytaxi.driver.MainActivity
import com.onepaytaxi.driver.OngoingAct
import com.onepaytaxi.driver.R
import com.onepaytaxi.driver.SplashAct
import com.onepaytaxi.driver.data.CommonData
import com.onepaytaxi.driver.homepage.HomePageActivity
import com.onepaytaxi.driver.service.LocationUpdate
import com.onepaytaxi.driver.utils.SessionSave

class FirebasePushMessage: FirebaseMessagingService() {

    companion object {
        const val NOTIFICATION_ID = 123
        var MAIN_ACT: MainActivity? = null
    }

    private var mNotificationManager: NotificationManager? = null

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (token.isNotEmpty()) {
            SessionSave.saveSession(CommonData.DEVICE_TOKEN, token, this)
        }
    }

//    override fun onMessageReceived(message: RemoteMessage) {
//        super.onMessageReceived(message)
//
//        val msg = message.data["message"] ?: "New Trip"
//
//        if (AppVisibility.isAppInForeground()) {
//            // 🔊 App alive → play sound via service
//            startTripAlertService()
//            showSilentNotification(msg)
//        } else {
//            // 🔔 App killed → notification sound only
//            showSimpleNotification(msg)
//        }
//    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val msg = message.data["message"]
            ?: message.notification?.body
            ?: "New Trip"

        if (AppVisibility.isAppInForeground()) {
            // ✅ App OPEN → manually show notification
            showSilentNotification(msg)

            // 🔊 Play sound via service
            startTripAlertService()
        } else {
            // ✅ App BACKGROUND / KILLED → system notification
            showSimpleNotification(msg)
        }
    }


    private fun showSilentNotification(msg: String) {

        val channelId = "channel_trip_silent"

        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            nm.getNotificationChannel(channelId) == null) {

            val channel = NotificationChannel(
                channelId,
                "Trip Silent",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                setSound(null, null)
            }

            nm.createNotificationChannel(channel)
        }

        val intent = Intent(this, SplashAct::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.small_logo)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(msg)
            .setSilent(true)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        nm.notify(2001, notification)
    }

    private fun startTripAlertService() {
        val intent = Intent(this, TripAlertService::class.java).apply {
            putExtra("duration_sec", 20) // optional
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun handleFCM(remoteMessage: RemoteMessage) {




        // Handling gcm message from
        var messageType: String? = remoteMessage.getMessageType()
        var message: String? = ""
        var status: String? = ""
        var tripId: String? = ""
        var title: String? = ""
        var expiry_date: String? = ""
        var display: String? = ""
        var unique = ""
        var data = remoteMessage.data

        try {
            val data: Map<String, String> = remoteMessage.data

            message = data["message"]
            status = data["status"]
//            val intent = Intent(this, TripAlertService::class.java)
//            intent.putExtra("duration_sec", 6) // Play sound for 20 seconds
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(intent)
//            } else {
//                startService(intent)
//            }
            if (data.containsKey("trip_id")) {
                tripId = data["trip_id"]
                println("Received trip_id: $tripId")
            } else {
                println("trip_id not received in the payload.")
            }

            if (data.containsKey("expiry_date")) {
                expiry_date = data["expiry_date"]
            }

            if (data.containsKey("title")) {
                title = data["title"]
            }

            if (data.containsKey("display")) {
                display = data["display"]
            }

            // Log extracted fields
            println("Message: $message")
            println("Status: $status")

        } catch (e: Exception) {
            e.printStackTrace()
        }



        try {
            when (status) {

                "14" -> {   // BOOK LATER
//                    val i = Intent(this, HomeScreenActivity::class.java).apply {
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
//                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
//                                Intent.FLAG_ACTIVITY_TASK_ON_HOME
//                        putExtra("alert_message", message)
//                        putExtra("alert_trip_id", tripId)
//                        putExtra("alert_schedule", "1")
//                    }
//                    startActivity(i)
                    showSimpleNotification(message!!)


                    //showBookLaterNotification(message!!)
                }
                "11" -> {
                    showSimpleNotification(message ?: "Updated")

                    goToHomeAndRefresh()
                }

                "15", "25", "200" -> { // LOGOUT & CLEAR SESSION
                    clearSession()
                    showSimpleNotification(message!!)

                    val intent = Intent(this, DriverLoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }

                "7", "10" -> { // CANCEL TRIP
                    cancelTripFlow(message!!)
                }

                "41", "42", "44", "45", "500" -> {
                    showSimpleNotification(message!!)
                }

                "99" -> {
                    showSimpleNotification(message!!)
                    openActivity(OngoingAct::class.java, message, status)
                }

                else -> {
                    // Default — show popup + notification
                    showSimpleNotification(message!!)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun goToHomeAndRefresh() {

        val intent = Intent(this, HomePageActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("refresh_home", true)
        }

        startActivity(intent)
    }



    private fun showSimpleNotification(msg: String) {

        val channelId = "channel_trip_alert_v2" // 🔥 NEW ID
        val soundUri = Uri.parse("android.resource://${packageName}/${R.raw.onepaytripsound}")

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (notificationManager.getNotificationChannel(channelId) == null) {

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()

                val channel = NotificationChannel(
                    channelId,
                    "Trip Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    setSound(soundUri, audioAttributes)
                    enableVibration(true)
                    enableLights(true)
                }

                notificationManager.createNotificationChannel(channel)
            }
        }

        val intent = Intent(this, SplashAct::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.small_logo)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(msg)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }




    private fun showBookLaterNotification(msg: String) {
        val channelId = "channel_later"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                channelId,
                "Book Later",
                NotificationManager.IMPORTANCE_HIGH
            )
            mNotificationManager?.createNotificationChannel(ch)
        }

        val i = Intent(this, SplashAct::class.java)
        i.putExtra("alert_schedule", "1")

        val pending = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            i,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.small_logo)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(msg)
            .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
            .setAutoCancel(true)
            .setContentIntent(pending)

      //  mNotificationManager?.notify(BOOKLATER_NOTIFICATION_ID, builder.build())
    }

    private fun clearSession() {
        val keys = listOf(
            "status", "Id", "Driver_locations", "driver_id", "Name",
            "company_id", "bookedby", "p_image", "Email", "trip_id",
            "phone_number", "driver_password"
        )

        keys.forEach { SessionSave.saveSession(it, "", this) }

        SessionSave.setWaitingTime(0L, this)
        SessionSave.saveSession(CommonData.USER_KEY, "", this)
    }

    private fun openActivity(cls: Class<*>, msg: String, status: String) {
        val i = Intent(this, cls)
        i.putExtra("alert_message", msg)
        i.putExtra("status", status)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    private fun cancelTripFlow(message: String) {

        MainActivity.mMyStatus.apply {
            setStatus("F")
            settripId("")
            setOnstatus("")
            setOnPassengerImage("")
            setOnpassengerName("")
            setOndropLocation("")
            setOnpickupLatitude("")
            setOnpickupLongitude("")
            setOndropLatitude("")
            setOndropLongitude("")
            setOndriverLatitude("")
            setOndriverLongitude("")
        }

        SessionSave.saveSession("status", "F", this)
        SessionSave.saveSession("trip_id", "", this)
        SessionSave.setWaitingTime(0L, this)

        LocationUpdate.ClearSessionwithTrip(this)

        val intent = Intent(this, HomePageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)

        showSimpleNotification(message)
    }

    private fun playFiveSecondAlert() {
        try {
            val mp = MediaPlayer.create(this, R.raw.onepaytripsound)

            // Set to max volume
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0
            )

            mp.start()

            // Stop after 5 sec
            Handler(Looper.getMainLooper()).postDelayed({
                if (mp.isPlaying) {
                    mp.stop()
                }
                mp.release()
            }, 5000)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}