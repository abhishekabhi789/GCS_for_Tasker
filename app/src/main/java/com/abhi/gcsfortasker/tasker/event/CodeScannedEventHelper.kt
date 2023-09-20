package com.abhi.gcsfortasker.tasker.event

import com.abhi.gcsfortasker.R
import com.abhi.gcsfortasker.tasker.CodeOutput
import com.abhi.gcsfortasker.tasker.EventInputFilter
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelper
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput

class CodeScannedEventHelper(config: TaskerPluginConfig<EventInputFilter>) :
    TaskerPluginConfigHelper<EventInputFilter, CodeOutput, RunnerCodeScannerEvent>(config) {
    override val inputClass: Class<EventInputFilter>
        get() = EventInputFilter::class.java
    override val outputClass: Class<CodeOutput>
        get() = CodeOutput::class.java
    override val runnerClass: Class<RunnerCodeScannerEvent>
        get() = RunnerCodeScannerEvent::class.java

    override fun addToStringBlurb(
        input: TaskerInput<EventInputFilter>,
        blurbBuilder: StringBuilder
    ) {
        blurbBuilder.append(context.getString(R.string.event_config_blurb_message))
    }
}
