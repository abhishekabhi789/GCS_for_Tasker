package com.abhi.gcsfortasker

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import com.abhi.gcsfortasker.tasker.CodeInputFilter
import com.abhi.gcsfortasker.tasker.CodeOutput
import com.abhi.gcsfortasker.tasker.event.CodeScannedEventHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textview.MaterialTextView
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.extensions.requestQuery
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput

class ActivityConfigScanEvent : Activity(), TaskerPluginConfig<CodeInputFilter> {
    override val context: Context get() = applicationContext

    //get input data to plugin
    override val inputForTasker
        get() = TaskerInput(
            CodeInputFilter(
                findViewById<EditText>(R.id.valueFilter).text?.toString(),
                findViewById<EditText>(R.id.typeFilter).text?.toString(),
                findViewById<MaterialSwitch>(R.id.useRegexSwitch).isChecked
            )
        )

    //set data to plugin config activity screen
    override fun assignFromInput(input: TaskerInput<CodeInputFilter>) {
        input.regular.run {
            findViewById<EditText>(R.id.valueFilter).setText(input.regular.valueFilter)
            findViewById<EditText>(R.id.typeFilter).setText(input.regular.typeFilter)
            findViewById<MaterialSwitch>(R.id.useRegexSwitch).isChecked = input.regular.useRegex
            findViewById<MaterialTextView>(R.id.regexSwitchStatus).text =
                if (input.regular.useRegex) getString(R.string.switch_text_on) else getString(R.string.switch_text_off)
        }
    }

    private val isConfigurable = true

    private val taskerHelper by lazy { CodeScannedEventHelper(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isConfigurable) {
            taskerHelper.finishForTasker()
            return
        }
        setContentView(R.layout.code_filter_input)
        taskerHelper.onCreate()
        //change useRegex button status when user toggles the switch
        findViewById<MaterialSwitch>(R.id.useRegexSwitch).setOnCheckedChangeListener { _, isChecked ->
            findViewById<MaterialTextView>(R.id.regexSwitchStatus).text =
                if (isChecked) getString(R.string.switch_text_on) else getString(R.string.switch_text_off)
        }
        //handling save button clicks
        findViewById<MaterialButton>(R.id.saveConfigButton).setOnClickListener {
            saveInput(this, taskerHelper)
        }
        //if user clicks the chooseType button to select type filters
        findViewById<MaterialButton>(R.id.chooseTypesImageButton).setOnClickListener {
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


fun Context.triggerTaskerEvent(output: CodeOutput) {
    ActivityConfigScanEvent::class.java.requestQuery(this, output)
}

