package com.abhi.gcsfortasker.tasker.event

import com.abhi.gcsfortasker.tasker.CodeInputFilter
import com.abhi.gcsfortasker.tasker.CodeOutput
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelper
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput

class CodeScannedEventHelper(config: TaskerPluginConfig<CodeInputFilter>) :
    TaskerPluginConfigHelper<CodeInputFilter, CodeOutput, RunnerCodeScannerEvent>(config) {
    override val inputClass: Class<CodeInputFilter>
        get() = CodeInputFilter::class.java
    override val outputClass: Class<CodeOutput>
        get() = CodeOutput::class.java
    override val runnerClass: Class<RunnerCodeScannerEvent>
        get() = RunnerCodeScannerEvent::class.java

    override fun addToStringBlurb(
        input: TaskerInput<CodeInputFilter>,
        blurbBuilder: StringBuilder
    ) {
        blurbBuilder.append("\nEvent triggered upon successful scan from launcher shortcut.")
    }
}
