package com.example.sh_prototype

// AlarmReceiver.kt
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Start the AlarmService when the alarm is triggered
        val serviceIntent = Intent(context, AlarmService::class.java)
        context?.startService(serviceIntent)

        // Play the alarm sound
        playAlarmSound(context)
    }

    private fun playAlarmSound(context: Context?) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound)
        mediaPlayer.start()
    }
}
