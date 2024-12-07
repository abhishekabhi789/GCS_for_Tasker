package com.abhi.gcsfortasker.tasker.action

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.abhi.gcsfortasker.CodeScanner
import com.abhi.gcsfortasker.ScannerActivity
import com.abhi.gcsfortasker.tasker.ActionInputFilter
import com.abhi.gcsfortasker.tasker.CodeOutput
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerAction
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class RunnerCodeScannerAction : TaskerPluginRunnerAction<ActionInputFilter, CodeOutput>() {

    override fun run(
        context: Context,
        input: TaskerInput<ActionInputFilter>
    ): TaskerPluginResult<CodeOutput> {
        Log.i(TAG, "run: scanning action request received")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(context)) {
            //permission needed for start activities from background on android 10+
            Log.e(TAG, "run: Permission - SYSTEM_ALERT_WINDOW not granted")
            return CodeScanner.Failure.MISSING_PERMISSION_ALERT_WINDOW.asTaskerErrorOutput()
        }
        //timeout from tasker action config screen or default timeout
        val actionTimeout: Long = (requestedTimeout ?: DEFAULT_ACTION_TIMEOUT).toLong()

        //starting the ScannerActivity with all the configs from tasker including timeout
        Intent(context, ScannerActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            // should set launchSource for ScannerActivity as TASKER_ACTION otherwise it won't emit the flow
            putExtra(
                ScannerActivity.KEY_EXTRA_LAUNCH_SOURCE,
                ScannerActivity.LaunchSource.TASKER_ACTION.name
            )
            putExtra(ScannerActivity.CONFIG_AUTO_ZOOM, input.regular.allowZooming)
            putExtra(ScannerActivity.CONFIG_MANUAL_INPUT, input.regular.allowManualInput)
            putExtra(ScannerActivity.CONFIG_FORMAT_FILTER, input.regular.formatFilter)
            putExtra(ScannerActivity.SCANNER_TIMEOUT, actionTimeout)
        }.also {
            Log.i(TAG, "run: staring scanner activity")
            context.startActivity(it)
        }
        return runBlocking { scanResultFlow.first() }
    }

    companion object {
        private const val TAG = "RunnerCodeScannerAction"
        private const val DEFAULT_ACTION_TIMEOUT = 60_000L

        /** flow to emit the output([TaskerPluginResult]) from  [ScannerActivity]*/
        val scanResultFlow = MutableSharedFlow<TaskerPluginResult<CodeOutput>>()
    }
}
