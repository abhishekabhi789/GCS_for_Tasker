package com.abhi.gcsfortasker.tasker.action

import android.content.Context
import android.util.Log
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeoutException

class RunnerCodeScannerAction : TaskerPluginRunnerActionNoInput<CodeOutput>() {
    private val TAG = "RunnerCodeScannerAction"
    override fun run(context: Context, input: TaskerInput<Unit>): TaskerPluginResult<CodeOutput> {
        val scanner = CodeScanner()
        val deferred = CompletableDeferred<Pair<Int, Any>>()
        GlobalScope.launch {
            scanner.scanNow(context) { result ->
                deferred.complete(result)
            }
        }

        var id = -1
        var output: Any? = null
        try {
            val result = runBlocking { withTimeout(10_000L) { deferred.await() } }
            id = result.first
            output = result.second
        } catch (e: TimeoutException) {
            Log.d(TAG, "run: timedOut. ${e.message} | ${e.cause}")
        }

        return if (id == 1 && output is Barcode) {
            val qrcode = output
            Log.d(
                TAG,
                "runner received data. id: $id, value: ${qrcode.rawValue}, type: ${qrcode.valueType}"
            )
            TaskerPluginResultSucess(
                CodeOutput(
                    qrcode.rawValue,
                    qrcode.displayValue,
                    barcodeTypes[qrcode.valueType]
                )
            )
        } else {
            val message = output?.toString() ?: "Unknown error"
            Log.e(TAG, "run: id: $id, output: $message")
            TaskerPluginResultErrorWithOutput(id, message)
        }
    }
}