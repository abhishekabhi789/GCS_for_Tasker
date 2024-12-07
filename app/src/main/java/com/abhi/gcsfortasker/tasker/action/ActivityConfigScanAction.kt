package com.abhi.gcsfortasker.tasker.action

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import androidx.annotation.RequiresApi
import com.abhi.gcsfortasker.R
import com.abhi.gcsfortasker.databinding.ActionConfigInputBinding
import com.abhi.gcsfortasker.tasker.ActionInputFilter
import com.abhi.gcsfortasker.utils.BarcodeFieldUtils
import com.abhi.gcsfortasker.utils.BarcodeFieldUtils.Field
import com.abhi.gcsfortasker.utils.UiUtils
import com.abhi.gcsfortasker.utils.UiUtils.showSingleChoiceDialog
import com.abhi.gcsfortasker.utils.UiUtils.toToast
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import net.dinglisch.android.tasker.TaskerPlugin

class ActivityConfigScanAction : Activity(), TaskerPluginConfig<ActionInputFilter> {
    private lateinit var binding: ActionConfigInputBinding
    override val context: Context get() = applicationContext
    override val inputForTasker
        get() = TaskerInput(
            ActionInputFilter(
                binding.formatFilter.text.toString().trim(),
                binding.allowAutoZoom.isChecked,
                binding.allowManualInput.isChecked
            )
        )

    override fun assignFromInput(input: TaskerInput<ActionInputFilter>) {
        input.regular.run {
            binding.formatFilter.setText(input.regular.formatFilter)
            binding.allowAutoZoom.isChecked = input.regular.allowZooming
            binding.allowManualInput.isChecked = input.regular.allowManualInput
        }
    }

    private val taskerHelper by lazy { CodeScannerActionHelper(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActionConfigInputBinding.inflate(layoutInflater)
        setContentView(binding.root)
        taskerHelper.onCreate()
        binding.chooseFormatsButton.setOnClickListener {
            val inputFilter = binding.formatFilter.text?.split(",")?.map { it.trim() }
            selectBarcodeFormatFilters(inputFilter ?: emptyList()) {
                binding.formatFilter.setText(it.joinToString())
            }
        }
        binding.chooseVariableButton.setOnClickListener {
            chooseVariable { selectedVariable -> binding.formatFilter.setText(selectedVariable) }
        }
        binding.saveConfigButton.setOnClickListener { saveConfig() }
        //these two text views are for letting user to copy them for variable matching purpose in tasker
        //so the items are separated by exactly a comma.
        binding.codeTypesList.text =
            BarcodeFieldUtils.getNamesForFieldType(Field.TYPE).joinToString(",")
        binding.codeFormatsList.text =
            BarcodeFieldUtils.getNamesForFieldType(Field.FORMAT).joinToString(",")

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) saveConfig()
        return super.onKeyDown(keyCode, event)
    }

    private fun saveConfig() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(context)) {
            Log.i(TAG, "saveConfig: missing draw overlay permission")
            getString(R.string.permission_missing_toast_draw_overlay).toToast(this)
            requestOverlayPermission(this)
            return
        }
        if (validateConfig()) taskerHelper.finishForTasker()
    }

    private fun validateConfig(): Boolean {
        val formatFilter = binding.formatFilter.text.toString()
        val isFormatFilterValid = when {
            formatFilter.isBlank() -> true
            formatFilter.startsWith(TaskerPlugin.VARIABLE_PREFIX) -> {
                //if it contains '()' at the ned, its an array
                TaskerPlugin.variableNameValid(formatFilter.removeSuffix("()"))
            }

            else -> {
                val allFormats = BarcodeFieldUtils.getNamesForFieldType(Field.FORMAT)
                formatFilter.split(",").all { it.trim() in allFormats }
            }
        }

        return (isFormatFilterValid).also {
            Log.d(TAG, "validateEventConfigInput: isFormatFilterValid $isFormatFilterValid")
            binding.formatFilter.error = if (isFormatFilterValid) null
            else getString(R.string.format_not_valid_error)
        }
    }

    private fun chooseVariable(onSelection: (String?) -> Unit) {
        taskerHelper.relevantVariables.toList().let { variables ->
            if (variables.isEmpty()) getString(R.string.no_variable_to_show).toToast(this)
            else showSingleChoiceDialog(
                context = this,
                title = getString(R.string.choose_a_variable),
                icon = R.drawable.ic_variable,
                items = variables,
                onSelection = onSelection,
            )
        }
    }

    private fun selectBarcodeFormatFilters(
        inputFilter: List<String>,
        onSelection: (List<String>) -> Unit
    ) {
        val dialogTitle = getString(R.string.choose_code_format_dialog_title)
        val codeTypes = BarcodeFieldUtils.getNamesForFieldType(Field.FORMAT)
        UiUtils.showMultiChoiceDialog(
            context = this,
            title = dialogTitle,
            allItems = codeTypes,
            selectedItems = inputFilter,
            onSelection = onSelection
        )
    }

    /**Added to fix [issue #1](https://github.com/abhishekabhi789/GCS_for_Tasker/issues/1)
     * MoreInfo: [Restrictions on starting activities from the background](https://developer.android.com/guide/components/activities/background-starts)
     **/
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestOverlayPermission(context: Context) {
        Intent().apply {
            setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            setData(Uri.parse("package:${context.packageName}"))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { context.startActivity(it) }
        Log.i(TAG, "requestOverlayPermission: Permission requested")
    }

    companion object {
        //tag has length limit and class can't be renamed anymore
        private const val TAG = "ActivityConfigAction"
    }
}
