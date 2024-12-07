package com.abhi.gcsfortasker

import android.content.Context
import android.util.Log
import com.abhi.gcsfortasker.tasker.CodeOutput
import com.abhi.gcsfortasker.utils.BarcodeFieldUtils
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultErrorWithOutput

/** Call scanNow() to start scanning */
object CodeScanner {
    private const val TAG = "CodeScanner"

    /** Performs scan by launching GCS, results are returned through callbacks].*/
    fun scanNow(
        context: Context,
        scannerOptions: GmsBarcodeScannerOptions? = null,
        onSuccess: (Barcode) -> Unit,
        onFail: (Failure) -> Unit
    ) {
        val options = scannerOptions ?: GmsBarcodeScannerOptions.Builder().enableAutoZoom().build()
        val scanner = GmsBarcodeScanning.getClient(context, options)
        scanner.startScan()
            .addOnSuccessListener { qrCode ->
                Log.d(TAG, buildString {
                    append("scanNow: Success- value: ", qrCode.rawValue)
                    append(", type: ", qrCode.valueType)
                    append(", format: ", qrCode.format)
                })
                onSuccess(qrCode)
            }
            .addOnCanceledListener {
                Log.d(TAG, "scanNow: Scan Cancelled")
                onFail(Failure.SCAN_CANCELLED)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "scanNow: failed to scan", e)
                onFail(Failure.ERROR.apply { extraInfo = e.message })
            }
    }

    /**call this method from [GmsBarcodeScannerOptions.Builder] scope to set barcode formats*/
    fun GmsBarcodeScannerOptions.Builder.setBarcodeFormatsFromFilter(formatFilter: String) {
        val formatNames = formatFilter.split(",").map { it.trim() }
        val formats = formatNames.mapNotNull { name ->
            BarcodeFieldUtils.getValueForField(name, BarcodeFieldUtils.Field.FORMAT)
        }
        when {
            formats.isEmpty() -> return
            formats.size == 1 -> this.setBarcodeFormats(formats.first())
            else -> {
                //if user selected All formats along with any others, ignore other formats and detect all codes
                if (formats.contains(Barcode.FORMAT_ALL_FORMATS)) this.setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                else this.setBarcodeFormats(formats.first(), *formats.drop(1).toIntArray())
            }
        }
    }

    /**Error code and error messages for a failure. Can be sent to tasker as `%err` and `%errmsg`*/
    enum class Failure(
        private val code: Int,
        private val message: String,
        var extraInfo: String? = null
    ) {
        ERROR(1, "error"),
        TIMEOUT(2, "timeout error"),
        SCAN_CANCELLED(3, "scan cancelled"),
        MISSING_PERMISSION_ALERT_WINDOW(4, "missing_permission: display over other apps.");

        /** Returns the message as string with extra info if any*/
        fun getErrorMessage(): String {
            return listOfNotNull(this.message, this.extraInfo).joinToString(" ")
        }

        /** Returns the [Failure] code and message as [TaskerPluginResult]*/
        fun asTaskerErrorOutput(): TaskerPluginResult<CodeOutput> {
            return TaskerPluginResultErrorWithOutput(this.code, getErrorMessage())
        }
    }
}
