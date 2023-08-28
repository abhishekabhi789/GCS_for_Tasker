package com.abhi.gcsfortasker.tasker.action

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.abhi.gcsfortasker.R
import com.abhi.gcsfortasker.tasker.CodeOutput
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelperNoInput
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigNoInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput

class CodeScannerActionHelper(config: TaskerPluginConfig<Unit>) :
    TaskerPluginConfigHelperNoInput<CodeOutput, RunnerCodeScannerAction>(config) {
    override val outputClass: Class<CodeOutput> get() = CodeOutput::class.java
    override val runnerClass: Class<RunnerCodeScannerAction> get() = RunnerCodeScannerAction::class.java
    override fun addToStringBlurb(input: TaskerInput<Unit>, blurbBuilder: StringBuilder) {
        blurbBuilder.append(context.getString(R.string.configuration_saved))
    }
}

class ActivityConfigScanAction : Activity(), TaskerPluginConfigNoInput {
    override val context get() = applicationContext
    private val taskerHelper by lazy { CodeScannerActionHelper(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(context, R.string.configuration_saved, Toast.LENGTH_SHORT).show()
        taskerHelper.finishForTasker()
    }
}