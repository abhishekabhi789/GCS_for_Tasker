package com.abhi.gcsfortasker.tasker.action

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.abhi.gcsfortasker.CodeScanner
import com.abhi.gcsfortasker.barcodeTypes
import com.abhi.gcsfortasker.tasker.CodeOutput
import com.google.mlkit.vision.barcode.common.Barcode
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerActionNoInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultErrorWithOutput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeoutException

class RunnerCodeScannerAction : TaskerPluginRunnerActionNoInput<CodeOutput>() {
    private val TAG = "RunnerCodeScannerAction"
    override fun run(context: Context, input: TaskerInput<Unit>): TaskerPluginResult<CodeOutput> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(context)) {
            Log.e(TAG, "run: Permission - SYSTEM_ALERT_WINDOW not granted")
            requestOverlayPermission(context)
            return TaskerPluginResultErrorWithOutput(
                0, "missing_permission: Display over other apps."
            )
        }
        val scanner = CodeScanner()
        val deferred = CompletableDeferred<Pair<Int, Any>>()
        runBlocking {
            scanner.scanNow(context) { result ->
                deferred.complete(result)
            }
        }

        var id = -1
        var output: Any? = null
        try {
            val result = runBlocking { withTimeout(60_000L) { deferred.await() } }
            id = result.first
            output = result.second
        } catch (e: TimeoutException) {
            Log.e(TAG, "run: timedOut. ${e.message} | ${e.cause}")
            Toast.makeText(context, "action timeout", Toast.LENGTH_SHORT).show()
        }

        return if (id == 1 && output is Barcode) {
            val qrcode = output
            Log.d(
                TAG, "received data. id: $id, value: ${qrcode.rawValue}, type: ${qrcode.valueType}"
            )
            TaskerPluginResultSucess(
                CodeOutput(qrcode.rawValue, qrcode.displayValue, barcodeTypes[qrcode.valueType])
            )
        } else {
            val message = output?.toString() ?: "Unknown error"
            Log.d(TAG, "run: id: $id, output: $message")
            TaskerPluginResultErrorWithOutput(id, message)
        }
    }

    private fun requestOverlayPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.packageName)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            Log.d(TAG, "requestOverlayPermission: Permission requested")
        }
    }
}