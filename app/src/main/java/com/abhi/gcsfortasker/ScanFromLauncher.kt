package com.abhi.gcsfortasker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.abhi.gcsfortasker.tasker.CodeOutput
import com.abhi.gcsfortasker.tasker.event.ActivityConfigScanEvent
import com.google.mlkit.vision.barcode.common.Barcode
import com.joaomgcd.taskerpluginlibrary.extensions.requestQuery

/**This activity initiates a scan whenever the plugin app is opened from launcher. It will only trigger an event if a value is successfully detected from the scan.*/
class ScanFromLauncher : Activity() {
    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        val serviceIntent = Intent(this, ScannerService::class.java)
        this.startService(serviceIntent)
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
            this.stopService(serviceIntent)
        }
        finish()
    }
}