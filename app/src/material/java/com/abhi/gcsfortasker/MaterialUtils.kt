package com.abhi.gcsfortasker

import android.app.Activity
import com.google.android.material.textfield.TextInputLayout

fun validateEventConfigInput(
    activity: Activity,
    typeFilter: String,
    formatFilter: String
): Pair<Boolean, String> {
    val valueValid = true //stub
    val typeValid = if (typeFilter.isEmpty()) true else run {
        val filterArray = typeFilter.split(",").map { it.trim() }
        filterArray.all { it in getCodeFields(false) }
    }
    val formatValid = isValidFormatFilter(formatFilter)
    val valueLayout = activity.findViewById<TextInputLayout>(R.id.value_filter_layout)
    val typeLayout = activity.findViewById<TextInputLayout>(R.id.type_filter_layout)
    val formatLayout = activity.findViewById<TextInputLayout>(R.id.format_filter_layout)

    if (!valueValid) valueLayout.error =
        activity.getString(R.string.value_not_valid_error) else valueLayout.error = null

    if (!typeValid) typeLayout.error =
        activity.getString(R.string.type_not_valid_error) else typeLayout.error = null
    if (!formatValid) formatLayout.error =
        activity.getString(R.string.format_not_valid_error) else formatLayout.error = null

    //return as a pair of error(Boolean) and error(reason)
    return when {
        !valueValid && !typeValid && !formatValid -> Pair(
            false,
            activity.getString(R.string.invalid_input_configs)
        )

        !valueValid -> Pair(false, activity.getString(R.string.invalid_value_filter))
        !typeValid -> Pair(false, activity.getString(R.string.invalid_type_filter))
        !formatValid -> Pair(false, activity.getString(R.string.invalid_format_filter))
        else -> Pair(true, "")
    }
}

fun validateActionConfigInput(activity: Activity, formatFilter: String): Pair<Boolean, String> {
    val formatValid = isValidFormatConfig(formatFilter)
    val formatLayout = activity.findViewById<TextInputLayout>(R.id.format_filter_layout)
    if (!formatValid) formatLayout.error =
        activity.getString(R.string.format_not_valid_error) else formatLayout.error = null
    return if (!formatValid) Pair(false, activity.getString(R.string.invalid_format_filter))
    else Pair(true, "")
}