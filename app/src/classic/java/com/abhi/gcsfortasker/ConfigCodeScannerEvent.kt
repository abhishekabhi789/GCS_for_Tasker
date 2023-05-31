package com.abhi.gcsfortasker

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import com.abhi.gcsfortasker.tasker.CodeInputFilter
import com.abhi.gcsfortasker.tasker.CodeOutput
import com.abhi.gcsfortasker.tasker.event.CodeScannedEventHelper
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.extensions.requestQuery
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput

class ActivityConfigScanEvent : Activity(), TaskerPluginConfig<CodeInputFilter> {
    override val context: Context get() = applicationContext
    //sent inputs to tasker.
    override val inputForTasker
        get() = TaskerInput(
            CodeInputFilter(
                findViewById<EditText>(R.id.valueFilter).text?.toString(),
                findViewById<EditText>(R.id.typeFilter).text?.toString(),
                findViewById<Switch>(R.id.useRegexSwitch).isChecked
            )
        )

    //assign config input fields from data given by tasker
    override fun assignFromInput(input: TaskerInput<CodeInputFilter>) {
        input.regular.run {
            findViewById<EditText>(R.id.valueFilter).setText(input.regular.valueFilter)
            findViewById<EditText>(R.id.typeFilter).setText(input.regular.typeFilter)
            findViewById<Switch>(R.id.useRegexSwitch).isChecked = input.regular.useRegex
            findViewById<TextView>(R.id.regexSwitchStatus).text =
                if (input.regular.useRegex) getString(R.string.switch_text_on) else getString(R.string.switch_text_off)
        }
    }


    private val taskerHelper by lazy { CodeScannedEventHelper(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.code_filter_input)
        taskerHelper.onCreate()
        findViewById<Switch>(R.id.useRegexSwitch).setOnCheckedChangeListener { _, isChecked ->
            findViewById<TextView>(R.id.regexSwitchStatus).text =
                if (isChecked) getString(R.string.switch_text_on) else getString(R.string.switch_text_off)

        }
        findViewById<Button>(R.id.saveConfigButton).setOnClickListener {
            saveInput(this, taskerHelper)
        }
        findViewById<ImageButton>(R.id.chooseTypesImageButton).setOnClickListener {
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