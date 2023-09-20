package com.abhi.gcsfortasker

import android.content.Context
import net.dinglisch.android.tasker.TaskerPlugin

fun validateEventConfigInput(
    context: Context, typeFilter: String, formatFilter: String
): Pair<Boolean, String> {
    val valueValid = true //stub
    val typeValid = if (typeFilter.isEmpty()) true else run {
        val filterArray = typeFilter.split(",").map { it.trim() }
        filterArray.all { it in getCodeFields(false) }
    }
    val formatValid =
        if (formatFilter.isEmpty()) true else run {
            val filterArray = formatFilter.split(",").map { it.trim() }
            filterArray.all { it in getCodeFields(true) }
        }

    return when {
        !valueValid && !typeValid && !formatValid -> Pair(
            false, context.getString(R.string.invalid_input_configs)
        )

        !valueValid -> Pair(false, context.getString(R.string.invalid_value_filter))
        !typeValid -> Pair(false, context.getString(R.string.invalid_type_filter))
        !formatValid -> Pair(false, context.getString(R.string.invalid_format_filter))
        else -> Pair(true, "")
    }
}

fun validateActionConfigInput(
    context: Context, formatFilter: String
): Pair<Boolean, String> {

    val formatValid = if (formatFilter.isEmpty()) true
    else if (TaskerPlugin.variableNameValid(formatFilter)) true
    else run {
        val filterArray = formatFilter.split(",").map { it.trim() }
        filterArray.all { it in getCodeFields(true) }
    }
    return if (!formatValid) Pair(false, context.getString(R.string.invalid_format_filter))
    else Pair(true, "")
}