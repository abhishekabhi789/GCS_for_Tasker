package com.abhi.gcsfortasker

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.abhi.gcsfortasker.tasker.CodeOutput
import com.abhi.gcsfortasker.tasker.event.triggerTaskerEvent
import com.google.mlkit.vision.barcode.common.Barcode

/**This activity initiates a scan whenever the plugin app is opened from launcher. It will only trigger an event if a value is successfully detected from the scan.*/
class ScanFromLauncher : Activity() {
    private val TAG = "ScanFromLauncher"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this
        val scanner = CodeScanner()
        scanner.scanNow(context) { result ->
            val (id, output) = result
            when (id) {
                1 -> {
                    try {
                        val qrcode = output as Barcode
                        triggerTaskerEvent(
                            CodeOutput(
                                qrcode.rawValue,
                                qrcode.valueType.toString(),
                                qrcode.displayValue
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                else -> {
                    val message = output as String
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error returned from scanner: $message")
                }
            }
            finish()
        }
    }
}