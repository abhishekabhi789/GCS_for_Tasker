package com.abhi.gcsfortasker.tasker.action

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import com.abhi.gcsfortasker.databinding.ActionConfigInputBinding
import com.abhi.gcsfortasker.tasker.ActionInputFilter
import com.abhi.gcsfortasker.utils.AppUtils.chooseFromList
import com.abhi.gcsfortasker.utils.BarcodeUtils.getCodeFields
import com.abhi.gcsfortasker.utils.TaskerUtils.chooseVariable
import com.abhi.gcsfortasker.utils.TaskerUtils.savePluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput

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
        binding.chooseFormatsButton.setOnClickListener { chooseFromList(this, true) }
        binding.chooseVariableButton.setOnClickListener { chooseVariable(this, taskerHelper) }
        binding.codeTypesList.text = getCodeFields(false).joinToString(",")
        binding.codeFormatsList.text = getCodeFields(true).joinToString(",")
        binding.saveConfigButton.setOnClickListener { savePluginConfig(this, taskerHelper) }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
            savePluginConfig(this, taskerHelper)
        }
        return super.onKeyDown(keyCode, event)
    }
}