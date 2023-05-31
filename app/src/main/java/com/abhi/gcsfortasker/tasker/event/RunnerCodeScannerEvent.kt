package com.abhi.gcsfortasker.tasker.event

import android.content.Context
import android.util.Log
import com.abhi.gcsfortasker.InputMatching
import com.abhi.gcsfortasker.tasker.CodeInputFilter
import com.abhi.gcsfortasker.tasker.CodeOutput
import com.joaomgcd.taskerpluginlibrary.condition.TaskerPluginRunnerConditionEvent
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultCondition
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultConditionSatisfied
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultConditionUnsatisfied

class RunnerCodeScannerEvent :
    TaskerPluginRunnerConditionEvent<CodeInputFilter, CodeOutput, CodeOutput>() {
    override fun getSatisfiedCondition(
        context: Context,
        input: TaskerInput<CodeInputFilter>,
        update: CodeOutput?
    ): TaskerPluginResultCondition<CodeOutput> {

        val valueFilter = input.regular.valueFilter
        val typeFilter = input.regular.typeFilter

        Log.d("GCS4T:EventRunner", "eventValue: ${update?.codeValue} | eventType: ${update?.codeType}")
        Log.d("GCS4T:EventRunner", "valueFilter: $valueFilter | typeFilter: $typeFilter")
        //if value filter is null, no rule for value filter, else match update data with value filter rule
        val valueMatch = if (valueFilter == null) {
            true
        } else {
            //check if the code value from event update matches the filter rules
            InputMatching().matchStrings(
                update?.codeValue.toString(),
                valueFilter.toString(),
                input.regular.useRegex
            )
        }
        //if type filter is null, no rule for type filter, else match update data with type filter rule
        val typeMatch = if (typeFilter == null) {
            true
        } else {
            //check if the code type from event update matches the type filter rule
            val patternList = typeFilter.split("\\s*,\\s*".toRegex()) // split the pattern string into a list of integers
            update?.codeType.toString() in patternList // check if codeType integer is in the type filter
        }

        Log.d("GCS4T:EventRunner", " (valueMatch: $valueMatch | typeMatch: $typeMatch)")
//trigger the event only if both value and type rules are matched
        return if (valueMatch && typeMatch) {
            TaskerPluginResultConditionSatisfied(context, update)
        } else TaskerPluginResultConditionUnsatisfied()
    }
}