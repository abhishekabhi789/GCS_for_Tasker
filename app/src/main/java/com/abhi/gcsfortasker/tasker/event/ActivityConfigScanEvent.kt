package com.abhi.gcsfortasker.tasker.event

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import com.abhi.gcsfortasker.R
import com.abhi.gcsfortasker.chooseFromList
import com.abhi.gcsfortasker.databinding.EventConfigInputBinding
import com.abhi.gcsfortasker.getCodeFields
import com.abhi.gcsfortasker.saveEventConfig
import com.abhi.gcsfortasker.tasker.EventInputFilter
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
        binding.codeTypesList.text = getCodeFields(false).joinToString(",")
        binding.codeFormatsList.text = getCodeFields(true).joinToString(",")
        binding.useRegexSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.regexSwitchStatus.text =
                if (isChecked) getString(R.string.switch_text_on) else getString(R.string.switch_text_off)

        }
        binding.saveConfigButton.setOnClickListener {
            saveEventConfig(this, taskerHelper)
        }
        binding.chooseTypesButton.setOnClickListener {
            chooseFromList(this, false)
        }
        binding.chooseFormatsButton.setOnClickListener {
            chooseFromList(this, true)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
            saveEventConfig(this, taskerHelper)
        }
        return super.onKeyDown(keyCode, event)
    }
}

