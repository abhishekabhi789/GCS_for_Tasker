package com.abhi.gcsfortasker

import android.content.Context
import android.util.Log
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

/** Call scanNow() to start scanning */
class CodeScanner {
    private val TAG = "CodeScanner"

    /** Performs scan and returns the scan result as a Pair of the scan result code and Barcode object or error message string */
    fun scanNow(context: Context, callback: (Pair<Int, Any>) -> Unit) {
        val scanner = GmsBarcodeScanning.getClient(context)
        scanner.startScan()
            .addOnSuccessListener { qrCode ->
                    Log.d(
                        TAG,
                        "scanNow: Success- value: ${qrCode.rawValue}, type: ${qrCode.valueType}"
                    )
                    callback.invoke(Pair(1, qrCode))

            }
            .addOnCanceledListener {
                Log.d(TAG, "scanNow: Scan Cancelled")
                callback.invoke(Pair(2, "Scan cancelled"))
            }
            .addOnFailureListener { e ->
                Log.e(
                    TAG,
                    "scanNow: failed to scan. message: ${e.message}, cause: ${e.cause}, stacktrace: ${e.stackTraceToString()}"
                )
                callback.invoke(Pair(3, e.message ?: "Unknown error"))
            }
    }
}