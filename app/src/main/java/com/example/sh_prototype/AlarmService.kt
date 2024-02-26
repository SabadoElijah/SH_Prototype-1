package com.example.sh_prototype

// AlarmService.kt
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat

class AlarmService : Service() {

    companion object {
        const val NOTIFICATION_ID = 1 // Replace with your desired notification ID
        private const val CHANNEL_ID = "YourChannelId"
    }

    // Declare wakeLock and mediaPlayer as class-level variables
    private var wakeLock: PowerManager.WakeLock? = null
    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        Log.d(TAG, "Foreground Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "AlarmService started")

        // Inside AlarmService.onStartCommand()
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "AlarmService:WakeLock"
        )
        wakeLock?.acquire()

        // Handle the alarm trigger logic here
        Log.d(TAG, "Alarm Triggered")



        // Return START_STICKY to restart the service if it gets terminated
        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Foreground Service destroyed")

        // Release the wakeLock
        wakeLock?.release()

        // Release the mediaPlayer
        mediaPlayer?.release()
    }

    private fun createNotificationChannel() {
        // Create a NotificationChannel
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Alarm Notification Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        // Register the channel with the system
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        // Create an intent for the notification to launch when tapped
        val notificationIntent = Intent(this, NavBar::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Alarm Notification")
            .setContentText("Your alarm is active.")
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentIntent(pendingIntent)
            .build()

        return notification
    }

    private fun playAlarmSound() {
        // Initialize the mediaPlayer if not already initialized
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)
        }

        // Start playing the alarm sound
        mediaPlayer?.start()
    }
}
