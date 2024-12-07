package com.abhi.gcsfortasker

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.abhi.gcsfortasker.CodeScanner.setBarcodeFormatsFromFilter
import com.abhi.gcsfortasker.tasker.CodeOutput
import com.abhi.gcsfortasker.tasker.action.RunnerCodeScannerAction
import com.abhi.gcsfortasker.tasker.event.ActivityConfigScanEvent
import com.abhi.gcsfortasker.utils.BarcodeFieldUtils.Field
import com.abhi.gcsfortasker.utils.BarcodeFieldUtils.getNameOfTheField
import com.abhi.gcsfortasker.utils.ShortcutUtils
import com.abhi.gcsfortasker.utils.UiUtils.toToast
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.joaomgcd.taskerpluginlibrary.extensions.requestQuery
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

/** Activity to handle scanning. */
class ScannerActivity : AppCompatActivity() {
    /**The intent to start and stop [ScannerService]*/
    private lateinit var serviceIntent: Intent

    /** reference to the timeout job, this is to prevent unnecessary flow emitting*/
    private var timeoutJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        /** The source which called the scanner this time,
         * @see [LaunchSource]*/
        val launchSource = intent.getStringExtra(KEY_EXTRA_LAUNCH_SOURCE)?.let { name ->
            LaunchSource.valueOf(name)
        } ?: LaunchSource.LAUNCHER
        Log.i(TAG, "onCreate: scanner launched from $launchSource")
        if (launchSource == LaunchSource.LAUNCHER && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            //initializing shortcut if launched from Launcher on a supported device
            ShortcutUtils.initializeShortcut(applicationContext).let { added ->
                if (added) getString(R.string.initial_shortcut_added_toast).toToast(this)
            }
        }
        if (launchSource == LaunchSource.TASKER_ACTION) {
            val actionTimeout = intent.getLongExtra(SCANNER_TIMEOUT, 0L)
            if (actionTimeout > 0) {
                //this job must be cancelled after sending a result.
                timeoutJob = CoroutineScope(Dispatchers.IO).launch {
                    delay(actionTimeout)
                    ensureActive()// if job cancelled, it means result sent then stop here else send timeout error
                    Log.e(TAG, "onCreate: scanner timeout")
                    sendOutputToRunner(CodeScanner.Failure.TIMEOUT.asTaskerErrorOutput())
                    exit()
                }
            }
        }

        serviceIntent = Intent(this, ScannerService::class.java)
        this.startService(serviceIntent)

        val scannerOptions = GmsBarcodeScannerOptions.Builder().apply {
            when (launchSource) {
                LaunchSource.LAUNCHER -> enableAutoZoom()
                LaunchSource.TASKER_ACTION -> {
                    intent.run {
                        if (getBooleanExtra(CONFIG_AUTO_ZOOM, true)) enableAutoZoom()
                        if (getBooleanExtra(CONFIG_MANUAL_INPUT, false)) allowManualInput()
                        getStringExtra(CONFIG_FORMAT_FILTER)?.let { setBarcodeFormatsFromFilter(it) }
                    }
                }
            }
        }.build()
        CodeScanner.scanNow(
            context = this@ScannerActivity,
            scannerOptions = scannerOptions,
            onSuccess = { barcode: Barcode ->
                val codeOutput = CodeOutput(
                    rawValue = barcode.rawValue,
                    displayValue = barcode.displayValue,
                    codeType = getNameOfTheField(barcode.valueType, Field.TYPE),
                    codFormat = getNameOfTheField(barcode.format, Field.FORMAT)
                )
                when (launchSource) {
                    LaunchSource.LAUNCHER -> {
                        //trigger event
                        ActivityConfigScanEvent::class.java.requestQuery(this, codeOutput)
                    }

                    LaunchSource.TASKER_ACTION -> {
                        sendOutputToRunner(TaskerPluginResultSucess(codeOutput))
                    }
                }
                exit()
            },
            onFail = { failure: CodeScanner.Failure ->
                val errorMessage = failure.getErrorMessage()
                Log.e(TAG, "onCreate: error from scanner $failure $errorMessage")
                when (launchSource) {
                    LaunchSource.LAUNCHER -> {
                        if (failure != CodeScanner.Failure.SCAN_CANCELLED)
                            errorMessage.toToast(this)
                    }

                    LaunchSource.TASKER_ACTION -> sendOutputToRunner(failure.asTaskerErrorOutput())
                }
                exit()
            }
        )
    }

    /** emits a flow of [TaskerPluginResult] to runner when [timeoutJob] is active. also cancels the [timeoutJob] */
    private fun sendOutputToRunner(output: TaskerPluginResult<CodeOutput>) {
        Log.d(TAG, "sendOutputToRunner: output is success ${output.success}")
        if (timeoutJob?.isActive == true) {
            timeoutJob?.cancel()
            CoroutineScope(Dispatchers.IO).launch {
                RunnerCodeScannerAction.scanResultFlow.emit(output)
            }
        }
    }

    /** stops the [serviceIntent] and finishes the activity along with CodeScanner UI*/
    private fun exit() {
        stopService(serviceIntent)
        finishAndRemoveTask() //removing task also closes GCS UI
        Log.d(TAG, "exit: stopped")
    }

    /**Possible launch sources for this activity. The scan result is handled based on their choice*/
    enum class LaunchSource {
        /**scanner is launched from launcher icon, should trigger event after scan*/
        LAUNCHER,

        /** scanner is launched from tasker action, should emit a flow to the action runner*/
        TASKER_ACTION
    }

    companion object {
        private const val TAG = "ScannerActivity"

        /** name of the [LaunchSource]*/
        const val KEY_EXTRA_LAUNCH_SOURCE = "launch_source"

        /** [Boolean] should enable auto zoom feature*/
        const val CONFIG_AUTO_ZOOM = "enable_autozoom"

        /** [Boolean] should allow manual input*/
        const val CONFIG_MANUAL_INPUT = "manual_input"

        /** [String] format filter for scanner*/
        const val CONFIG_FORMAT_FILTER = "format_filter"

        /** [Long] timeout for the action in millis*/
        const val SCANNER_TIMEOUT = "scanner_timeout"
    }
}
