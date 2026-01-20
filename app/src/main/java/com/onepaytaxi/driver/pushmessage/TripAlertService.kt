package com.onepaytaxi.driver.pushmessage



import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.onepaytaxi.driver.R
import kotlinx.coroutines.NonCancellable.start


class TripAlertService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()

        // 🔴 ABSOLUTELY REQUIRED
        startForeground(1, createEmptyNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val duration = intent?.getIntExtra("duration_sec", 20) ?: 20
        playSoundRepeatedly(duration)

        return START_NOT_STICKY
    }

    private fun createEmptyNotification(): Notification {
        val channelId = "service_sound_channel"

        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (manager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    "Trip Alert Service",
                    NotificationManager.IMPORTANCE_LOW
                )
                manager.createNotificationChannel(channel)
            }
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Trip Alert Active")
            .setContentText("Incoming trip request")
            .setSmallIcon(R.drawable.small_logo)
            .setOngoing(true)
            .build()
    }

    private fun playSoundRepeatedly(durationSec: Int) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this, R.raw.onepaytripsound).apply {
                isLooping = true
                start()
            }

            Handler(Looper.getMainLooper()).postDelayed({
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                stopSelf()
            }, durationSec * 1000L)

        } catch (e: Exception) {
            stopSelf()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
}
