package com.abhi.gcsfortasker.tasker.action

import android.content.Context
import android.util.Log
import com.abhi.gcsfortasker.CodeScanner
import com.abhi.gcsfortasker.tasker.CodeOutput
import com.google.mlkit.vision.barcode.common.Barcode
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerActionNoInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultErrorWithOutput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess

class RunnerCodeScannerAction : TaskerPluginRunnerActionNoInput<CodeOutput>() {
    private val TAG = "RunnerCodeScannerAction"
    override fun run(context: Context, input: TaskerInput<Unit>): TaskerPluginResult<CodeOutput> {
        val scanner = CodeScanner(context)
        val (id, output) = scanner.scanNow()

        return if (id == 1) {
            val qrcode = output as Barcode
            Log.d(
                TAG,
                "runner received data. id: $id, value: ${qrcode.rawValue}, type: ${qrcode.valueType}, display value: ${qrcode.displayValue}"
            )
            TaskerPluginResultSucess(
                CodeOutput(
                    qrcode.rawValue,
                    qrcode.valueType.toString(),
                    qrcode.displayValue
                )
            )
        } else {
            val message = output as String
            Log.e(TAG, "run: id: $id, output: $message")
            TaskerPluginResultErrorWithOutput(id, message)
        }
    }
}