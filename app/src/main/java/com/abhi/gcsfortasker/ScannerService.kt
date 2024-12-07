package com.abhi.gcsfortasker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat

/** This service is to prevent the app getting killed by system on low memory devices while going background during scan.
 * This may be unnecessary for modern devices, yet keeping it for old low memory devices*/
class ScannerService : Service() {
    private val scannerServiceNotificationId = 789
    private val notificationChannelID = "GCS4TScannerService"
    override fun onCreate() {
        super.onCreate()
        val notificationChannelName = getString(R.string.notification_scanning_service_channel_name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //creating notification channel for o+ devices
            val notificationChannel = NotificationChannel(
                notificationChannelID,
                notificationChannelName,
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                //this'll be helpful if user opens the app settings directly
                description = getString(R.string.notification_scanning_service_content_text)
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notification: Notification =
            NotificationCompat.Builder(this, notificationChannelID).apply {
                setContentTitle(getString(R.string.notification_scanning_service_title))
                setContentText(getString(R.string.notification_scanning_service_content_text))
                setSmallIcon(R.drawable.scanner_icon)
                setContentIntent(getNotificationPendingIntent())
                setSilent(true)
            }.build()
        Log.d(TAG, "onCreate: starting service")
        //notification permission is not declared in manifest
        startForeground(scannerServiceNotificationId, notification)
    }

    private fun getNotificationPendingIntent(): PendingIntent {
        val notificationIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, notificationChannelID)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                setData(Uri.fromParts("packge", packageName, null))
            }
        }
        return PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Service destroyed")
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val TAG = "ScannerService"
    }
}
