package com.yirmi3on5.textapp.raw

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import com.yirmi3on5.textapp.MainActivity
import java.util.Timer
import java.util.TimerTask
import kotlin.random.Random

class ForebackgroundService: Service() {
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private var channelName = "com.yirmi3on5.textapp.raw.ForegroundBackService"
    private var channelID = "com.yirmi3on5.textapp.raw.ForegroundBackService"
    private var notificationID = 1

    private val INTERVAL_IN_MILLISECONDS: Long = 10 * 1000 // 1 saat

    override fun onBind(intent: Intent?): IBinder? {
        Log.i("ForegroundBackService", "onBind: Service started")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ForebackgroundService::class.java)
        pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("ForegroundBackService", "onStartCommand: Service started")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, channelName, importance).apply {
                description = "This channel is used for the foreground service notification."
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification: Notification = NotificationCompat.Builder(this, channelID)
            .setContentIntent(pendingIntent)
            .setContentTitle("My Foreground Service")
            .setContentText("This is a foreground service.")
            .build()

        startForeground(notificationID, notification)
        sendValueToMainActivity()
        scheduleNextUpdate()

        return START_STICKY
    }

    private fun sendValueToMainActivity() {
        val myVariable = Random.nextInt(100).toString()
        Log.i("ForegroundBackService", "222 onStartCommand: myVariable: $myVariable")
        val intentToMainActivity = Intent("myBroadcast").apply {
            putExtra("icDegisken", myVariable)
        }
        sendBroadcast(intentToMainActivity)
    }

    private fun scheduleNextUpdate() {
        val currentTime = System.currentTimeMillis()
        val nextHourTime = (currentTime / INTERVAL_IN_MILLISECONDS + 1) * INTERVAL_IN_MILLISECONDS
        val delay: Long = nextHourTime - currentTime

        alarmManager.setExact(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + delay,
            pendingIntent
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        alarmManager.cancel(pendingIntent)
    }
}