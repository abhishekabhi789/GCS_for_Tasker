package com.abhi.gcsfortasker

import android.content.Context
import android.util.Log
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

/** Call scanNow() to start scanning */
class CodeScanner {
    private val TAG = javaClass.simpleName

    /** Performs scan and returns the scan result as a Pair of the scan result code and Barcode object or error message string */
    fun scanNow(context: Context, callback: (Pair<Int, Any>) -> Unit) {
        val options = GmsBarcodeScannerOptions.Builder().enableAutoZoom().build()
        val scanner = GmsBarcodeScanning.getClient(context, options)
        scanner.startScan()
            .addOnSuccessListener { qrCode ->
                Log.d(
                    TAG,
                    "scanNow: Success- value: ${qrCode.rawValue}, type: ${qrCode.valueType}"
                )
                callback.invoke(Pair(0, qrCode))

            }
            .addOnCanceledListener {
                Log.d(TAG, "scanNow: Scan Cancelled")
                val error = ErrorCodes.SCAN_CANCELLED
                callback.invoke(Pair(error.code, error.message))
            }
            .addOnFailureListener { e ->
                Log.e(
                    TAG,
                    buildString {
                        append("scanNow: failed to scan. ")
                        append("message: " + e.message)
                        append(", cause: " + e.cause)
                        append(", stacktrace: " + e.stackTraceToString())
                    }
                )
                val error = ErrorCodes.ERROR
                callback.invoke(Pair(error.code, e.message ?:error.message))
            }
    }

    enum class ErrorCodes(
        val code: Int, val message: String
    ) {
        ERROR(1, "Unknown error"),
        TIMEOUT(2, "Timeout error"),
        SCAN_CANCELLED(3, "Scan cancelled"),
        MISSING_PERMISSION_ALERT_WINDOW(4, "missing_permission: Display over other apps.")
    }

}