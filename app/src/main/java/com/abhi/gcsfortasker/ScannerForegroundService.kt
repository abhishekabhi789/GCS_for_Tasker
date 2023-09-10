package com.abhi.gcsfortasker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.abhi.gcsfortasker.tasker.CodeOutput
import com.abhi.gcsfortasker.tasker.event.ActivityConfigScanEvent
import com.google.mlkit.vision.barcode.common.Barcode
import com.joaomgcd.taskerpluginlibrary.extensions.requestQuery


class ScannerForegroundService : Service() {
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

        val notificationIntent = Intent(this, ScanFromLauncher::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification =
            NotificationCompat.Builder(this, notificationChannelID)
                .setContentTitle("Scanning is in action")
                .setContentText("You can hide this notification if possible")
                .setSmallIcon(R.drawable.scanner_icon)
                .setContentIntent(pendingIntent)
                .build()
        Log.d(TAG, "onCreate: starting service")
        startForeground(foregroundServiceID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val scanner = CodeScanner()
        scanner.scanNow(this) { result ->
            val (id, output) = result
            when (id) {
                1 -> {
                    try {
                        val qrcode = output as Barcode
                        ActivityConfigScanEvent::class.java.requestQuery(
                            this,
                            CodeOutput(
                                qrcode.rawValue,
                                qrcode.displayValue,
                                barcodeTypes[qrcode.valueType]
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                else -> {
                    val message = output as String
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error returned from scanner: $message")
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                this.stopForeground(STOP_FOREGROUND_REMOVE)
            } else this.stopForeground(true)
            stopSelf()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}