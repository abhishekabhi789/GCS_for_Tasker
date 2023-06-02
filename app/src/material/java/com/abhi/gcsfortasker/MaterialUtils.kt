package com.abhi.gcsfortasker

import android.app.Activity
import com.google.android.material.textfield.TextInputLayout

fun validateInput(
    activity: Activity,
    valueFilter: String?,
    typeFilter: String
): Pair<Boolean, String> {
    val valueValid = true //stub

    val typeValid = if (typeFilter.isEmpty()) true else run {
        val filterArray = typeFilter.split(",").map { it.replace(" ", "").trim().toIntOrNull() }
        //type constants are in 0-12 range.
        filterArray.all { it in 0..12 } //&& filterArray.distinct().size < 13
    }

    val valueLayout = activity.findViewById<TextInputLayout>(R.id.valueFilterLayout)
    val typeLayout = activity.findViewById<TextInputLayout>(R.id.typeFilterLayout)

    if (!valueValid) valueLayout.error =
        activity.getString(R.string.value_not_valid_error) else valueLayout.error = null

    if (!typeValid) typeLayout.error =
        activity.getString(R.string.type_not_valid_error) else typeLayout.error = null
    //return as a pair of error(Boolean) and error(reason)
    return when {
        !valueValid && !typeValid -> Pair(
            false,
            activity.getString(R.string.invalid_value_and_type_filter)
        )
        !valueValid -> Pair(false, activity.getString(R.string.invalid_value_filter))
        !typeValid -> Pair(false, activity.getString(R.string.invalid_type_filter))
        else -> Pair(true, "")
    }
}