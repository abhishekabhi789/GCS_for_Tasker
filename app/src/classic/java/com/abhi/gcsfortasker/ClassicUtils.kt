package com.abhi.gcsfortasker

import android.content.Context

fun validateInput(
    context: Context, valueFilter: String?, typeFilter: String
): Pair<Boolean, String> {
    val valueValid = true //no need to verify
    val typeValid = if (typeFilter.isEmpty()) true else run {
        val filterArray = typeFilter.split(",").map { it.replace(" ", "").trim().toIntOrNull() }
        filterArray.all { it in 0..12 } && filterArray.distinct().size < 13
    }

    return when {
        !valueValid && !typeValid -> Pair(
            false, context.getString(R.string.invalid_value_and_type_filter)
        )

        !valueValid -> Pair(false, context.getString(R.string.invalid_value_filter))
        !typeValid -> Pair(false, context.getString(R.string.invalid_type_filter))
        else -> Pair(true, "")
    }
}
