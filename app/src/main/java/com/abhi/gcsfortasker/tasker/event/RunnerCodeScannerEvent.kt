package com.abhi.gcsfortasker.tasker.event

import android.content.Context
import android.util.Log
import com.abhi.gcsfortasker.InputMatching
import com.abhi.gcsfortasker.tasker.CodeOutput
import com.abhi.gcsfortasker.tasker.EventInputFilter
import com.joaomgcd.taskerpluginlibrary.condition.TaskerPluginRunnerConditionEvent
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultCondition
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultConditionSatisfied
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultConditionUnsatisfied

class RunnerCodeScannerEvent :
    TaskerPluginRunnerConditionEvent<EventInputFilter, CodeOutput, CodeOutput>() {
    override fun getSatisfiedCondition(
        context: Context,
        input: TaskerInput<EventInputFilter>,
        update: CodeOutput?
    ): TaskerPluginResultCondition<CodeOutput> {

        val valueFilter = input.regular.valueFilter
        val typeFilter = input.regular.typeFilter
        val formatFilter = input.regular.formatFilter

        Log.d(TAG, buildString {
            append("filterValues: ")
            append(valueFilter)
            append(" | typeFilter: ")
            append(typeFilter)
            append(" | formatFilter: ")
            append(formatFilter)
        })
        //if value filter is null, no rule for value filter, else match update data with value filter rule
        val valueMatch = if (valueFilter == null) true else {
            //check if the code value from event update matches the filter rules
            InputMatching.matchStrings(
                input = update?.rawValue.toString(),
                pattern = valueFilter.toString(),
                useRegex = input.regular.useRegex
            )
        }
        //if type filter is null, no rule for type filter, else match update data with type filter rule
        val typeMatch = if (typeFilter == null) true else {
            //check if the code type from event update matches the type filter rule
            val allTypes = typeFilter.split(",").map { it.trim() }
            update?.codeType.toString() in allTypes
        }
        val formatMatch = if (formatFilter == null) true else {
            val allFormats = formatFilter.split(",").map { it.trim() }
            update?.codFormat.toString() in allFormats
        }
        Log.d(TAG, buildString {
            append("valueMatch: ")
            append(valueMatch)
            append(" | typeMatch: ")
            append(typeMatch)
            append(" | formatMatch: ")
            append(formatMatch)
        })
        Log.d(TAG, buildString {
            append("scanNow: Success- value: ")
            append(update?.rawValue)
            append(", type: ")
            append(update?.codeType)
            append(", format: ")
            append(update?.codFormat)
        })
        //trigger the event only if both value and type rules are matched
        return if (valueMatch && typeMatch && formatMatch) {
            TaskerPluginResultConditionSatisfied(context, update)
        } else TaskerPluginResultConditionUnsatisfied()
    }

    companion object {
        private const val TAG = "RunnerCodeScannerEvent"
    }
}
