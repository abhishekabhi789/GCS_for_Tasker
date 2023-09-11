package com.abhi.gcsfortasker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat


class ScannerService : Service() {
    private val TAG = javaClass.simpleName
    private val foregroundServiceID = 789
    private val notificationChannelID = "GCS4TScannerService"
    private val notificationChannelName = "Scanner service notification"
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                notificationChannelID,
                notificationChannelName,
                NotificationManager.IMPORTANCE_MIN
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notification: Notification =
            NotificationCompat.Builder(this, notificationChannelID)
                .setContentTitle("Scanning is in action")
                .setContentText("You can hide this notification if possible")
                .setSmallIcon(R.drawable.scanner_icon)
                .build()
        Log.d(TAG, "onCreate: starting service")
        startForeground(foregroundServiceID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Service destroyed")
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}