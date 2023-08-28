package com.abhi.gcsfortasker.tasker.event

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import com.abhi.gcsfortasker.R
import com.abhi.gcsfortasker.barcodeTypes
import com.abhi.gcsfortasker.chooseTypes
import com.abhi.gcsfortasker.databinding.EventConfigInputBinding
import com.abhi.gcsfortasker.saveInput
import com.abhi.gcsfortasker.tasker.CodeInputFilter
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput

class ActivityConfigScanEvent : Activity(), TaskerPluginConfig<CodeInputFilter> {
    private lateinit var binding: EventConfigInputBinding
    override val context: Context get() = applicationContext

    //sent inputs to tasker.
    override val inputForTasker
        get() = TaskerInput(
            CodeInputFilter(
                binding.valueFilter.text?.toString(),
                binding.typeFilter.text?.toString(),
                binding.useRegexSwitch.isChecked
            )
        )

    //assign config input fields from data given by tasker
    override fun assignFromInput(input: TaskerInput<CodeInputFilter>) {
        input.regular.run {
            binding.valueFilter.setText(input.regular.valueFilter)
            binding.typeFilter.setText(input.regular.typeFilter)
            binding.useRegexSwitch.isChecked = input.regular.useRegex
            binding.regexSwitchStatus.text =
                if (input.regular.useRegex) getString(R.string.switch_text_on) else getString(R.string.switch_text_off)
        }
    }


    private val taskerHelper by lazy { CodeScannedEventHelper(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EventConfigInputBinding.inflate(layoutInflater)
        setContentView(binding.root)
        taskerHelper.onCreate()
        binding.codeTypesList.text = barcodeTypes.joinToString(",")
        binding.useRegexSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.regexSwitchStatus.text =
                if (isChecked) getString(R.string.switch_text_on) else getString(R.string.switch_text_off)

        }
        binding.saveConfigButton.setOnClickListener {
            saveInput(this, taskerHelper)
        }
        binding.chooseTypesImageButton.setOnClickListener {
            chooseTypes(this)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
            saveInput(this, taskerHelper)
        }
        return super.onKeyDown(keyCode, event)
    }
}

