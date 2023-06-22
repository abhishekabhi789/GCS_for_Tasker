package com.abhi.gcsfortasker

import android.content.Context
import android.util.Log
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import java.util.concurrent.Semaphore

/**Call scanNow() to start scan*/
class CodeScanner(private val context: Context) {
    private val TAG = "CodeScanner"

    /**Performs scan and returns the scan result as a Pair of the scan result code and  Barcode object or error message string*/
    fun scanNow(): Pair<Int, Any> {
        val semaphore = Semaphore(0)
        val scanner = GmsBarcodeScanning.getClient(context)
        //initialising the result variable
        var result: Pair<Int, Any> = Pair(5, "Scanner prepared, waiting for result")

        scanner.startScan().addOnSuccessListener { qrCode ->
            if (qrCode.rawValue!!.isNotEmpty()) {
                Log.d(TAG, "scanNow: Success- value: ${qrCode.rawValue}, type: ${qrCode.valueType}")
                result = Pair(1, qrCode)
            } else {
                result = Pair(4, "No qrCode found")
                Log.d(TAG, "scanNow: No QR code value found")
            }
            semaphore.release()
        }.addOnCanceledListener {
            result = Pair(2, "Scan cancelled")
            Log.d(TAG, "scanNow: Scan Cancelled")
            semaphore.release()
        }.addOnFailureListener { e ->
            result = Pair(3, e.message ?: "Unknown error")
            Log.e(
                TAG,
                "scanNow: failed to scan. message: ${e.message}, cause: ${e.cause}, stacktrace: ${e.stackTraceToString()}",
            )
            semaphore.release()
        }
        Log.d(TAG, "scanNow: ${result.first} : ${result.second}")
        semaphore.acquire()
        return result
    }
}