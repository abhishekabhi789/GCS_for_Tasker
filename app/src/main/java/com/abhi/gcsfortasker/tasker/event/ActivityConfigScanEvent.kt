package com.abhi.gcsfortasker.tasker.event

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import com.abhi.gcsfortasker.R
import com.abhi.gcsfortasker.databinding.EventConfigInputBinding
import com.abhi.gcsfortasker.tasker.EventInputFilter
import com.abhi.gcsfortasker.utils.BarcodeFieldUtils
import com.abhi.gcsfortasker.utils.BarcodeFieldUtils.Field
import com.abhi.gcsfortasker.utils.UiUtils
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput

class ActivityConfigScanEvent : Activity(), TaskerPluginConfig<EventInputFilter> {
    private lateinit var binding: EventConfigInputBinding
    override val context: Context get() = applicationContext

    //sent inputs to tasker.
    override val inputForTasker
        get() = TaskerInput(
            EventInputFilter(
                binding.valueFilter.text?.toString(),
                binding.typeFilter.text?.toString(),
                binding.useRegexSwitch.isChecked,
                binding.formatFilter.text?.toString()
            )
        )

    //assign config input fields from data given by tasker
    override fun assignFromInput(input: TaskerInput<EventInputFilter>) {
        input.regular.run {
            binding.valueFilter.setText(input.regular.valueFilter)
            binding.typeFilter.setText(input.regular.typeFilter)
            binding.useRegexSwitch.isChecked = input.regular.useRegex
            binding.regexSwitchStatus.text =
                if (input.regular.useRegex) getString(R.string.switch_text_on) else getString(R.string.switch_text_off)
            binding.formatFilter.setText(input.regular.formatFilter)
        }
    }

    private val taskerHelper by lazy { CodeScannedEventHelper(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EventConfigInputBinding.inflate(layoutInflater)
        setContentView(binding.root)
        taskerHelper.onCreate()

        binding.useRegexSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.regexSwitchStatus.text =
                if (isChecked) getString(R.string.switch_text_on) else getString(R.string.switch_text_off)
        }
        binding.typeFilter.setOnClickListener {
            val input = binding.typeFilter.text?.split(",")?.map { it.trim() }
            selectBarcodeFilters(Field.TYPE, input ?: emptyList()) {
                binding.typeFilter.setText(it.joinToString())
            }
        }
        binding.formatFilter.setOnClickListener {
            val input = binding.formatFilter.text?.split(",")?.map { it.trim() }
            selectBarcodeFilters(Field.FORMAT, input ?: emptyList()) {
                binding.formatFilter.setText(it.joinToString())
            }
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
        if (validateEventConfigInput()) taskerHelper.finishForTasker()
    }

    private fun validateEventConfigInput(): Boolean {
        val typeFilter = binding.typeFilter.text.toString()
        val allTypes = BarcodeFieldUtils.getNamesForFieldType(Field.TYPE)
        val isTypeFilterValid = if (typeFilter.isBlank()) true else {
            typeFilter.split(",").all { it.trim() in allTypes }
        }
        val formatFilter = binding.formatFilter.text.toString()
        val allFormats = BarcodeFieldUtils.getNamesForFieldType(Field.FORMAT)
        val isFormatFilterValid = if (formatFilter.isBlank()) true else {
            formatFilter.split(",").all { it.trim() in allFormats }
        }

        return (isTypeFilterValid && isFormatFilterValid).also {
            Log.d(
                TAG,
                "validateEventConfigInput:" +
                        " isTypeFilterValid: $isTypeFilterValid , isFormatFilterValid $isFormatFilterValid "
            )
            binding.typeFilter.error =
                if (isTypeFilterValid) null else getString(R.string.type_not_valid_error)
            binding.formatFilter.error =
                if (isFormatFilterValid) null else getString(R.string.format_not_valid_error)
        }
    }

    private fun selectBarcodeFilters(
        type: Field,
        inputFilter: List<String>,
        onSelection: (List<String>) -> Unit
    ) {
        val dialogTitle = when (type) {
            Field.FORMAT -> R.string.choose_code_format_dialog_title
            Field.TYPE -> R.string.choose_code_type_dialog_title
        }
        val codeTypes = BarcodeFieldUtils.getNamesForFieldType(type)
        UiUtils.showMultiChoiceDialog(
            context = this,
            title = getString(dialogTitle),
            allItems = codeTypes,
            selectedItems = inputFilter,
            onSelection = onSelection
        )
    }

    companion object {
        private const val TAG = "ActivityConfigScanEvent"
    }
}
