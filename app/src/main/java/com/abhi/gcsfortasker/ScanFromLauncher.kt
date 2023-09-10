package com.abhi.gcsfortasker

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle

/**This activity initiates a scan whenever the plugin app is opened from launcher. It will only trigger an event if a value is successfully detected from the scan.*/
class ScanFromLauncher : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val serviceIntent = Intent(this, ScannerForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        finish()
    }
}