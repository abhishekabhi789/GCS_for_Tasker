package com.abhi.gcsfortasker.tasker.action

import com.abhi.gcsfortasker.tasker.ActionInputFilter
import com.abhi.gcsfortasker.tasker.CodeOutput
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelper

class CodeScannerActionHelper(config: TaskerPluginConfig<ActionInputFilter>) :
    TaskerPluginConfigHelper<ActionInputFilter, CodeOutput, RunnerCodeScannerAction>(config) {
    override val inputClass: Class<ActionInputFilter> get() = ActionInputFilter::class.java
    override val outputClass: Class<CodeOutput> get() = CodeOutput::class.java
    override val runnerClass: Class<RunnerCodeScannerAction> get() = RunnerCodeScannerAction::class.java
}
