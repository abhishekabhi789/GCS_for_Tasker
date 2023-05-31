package com.abhi.gcsfortasker

import android.content.Context
import android.util.Log
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import java.util.concurrent.Semaphore

/**This class has two functions. hasScannerModule to check scanner module availability and [scanNow] to start scanning*/
class CodeScanner(private val context: Context) {
    private val TAG = "CodeScanner"
    /**Ensuring API availability with ModuleInstallClient [https://developers.google.com/android/guides/module-install-apis]*/
    /*
        fun hasScannerModule() {
            Log.d("GCS4T:CS-hSC:hasScanner", "Looking for GCS modules")
            val moduleInstallClient = ModuleInstall.getClient(context)
            val optionalModuleApi = TfLite.getClient(context)
            moduleInstallClient.areModulesAvailable(optionalModuleApi).addOnSuccessListener {
                if (it.areModulesAvailable()) {
                    // Modules are present on the device...
                    Log.d("GCS4T:CS-hSC:checkMod", "GCS modules present")

                } else {
                    // Modules are not present on the device...
                    Log.e("GCS4T:CS-hSC:checkMod", "GCS modules absent")
                    Toast.makeText(
                        context,
                        "GCS Modules absent. Allow some time to complete the download",
                        Toast.LENGTH_LONG
                    ).show()


                }
            }.addOnFailureListener {
                Log.e("GCS4T:CS-hSC:failed", "Failed to check for GCS module")
            }
        }
    */

    /**Performs scan and returns the scan result as a triad consisting of the scan result code, the scan result value (or an error message if applicable), and the result value type*/
    fun scanNow(): Pair<Int, Any> {
        val semaphore = Semaphore(0)
        val scanner = GmsBarcodeScanning.getClient(context)
        //initialising the result variable with error message
        var result: Pair<Int, Any> = Pair(5, "Scan did not complete")

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
            result = Pair(3, e.message ?: "")
            Log.e(
                TAG,
                "scanNow: failed to scan. message: ${e.message}, cause: ${e.cause}, stacktrace: ${e.stackTraceToString()}",
            )
            semaphore.release()
        }
        Log.d(TAG, "scanNow: Scanning Completed")
        semaphore.acquire()
        return result
    }
}