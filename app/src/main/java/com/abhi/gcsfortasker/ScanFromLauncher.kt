package com.abhi.gcsfortasker

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.abhi.gcsfortasker.tasker.CodeOutput
import com.abhi.gcsfortasker.tasker.event.triggerTaskerEvent
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**This activity initiates a scan whenever the plugin app is opened from launcher. It will only trigger an event if a successful value is detected from the scan.*/
class ScanFromLauncher : Activity() {
    private val TAG = "ScanFromLauncher"
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this
        val scanner = CodeScanner(this)
        coroutineScope.launch(Dispatchers.IO) {
            val (id, output) = scanner.scanNow()
            withContext(Dispatchers.Main) {
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

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}