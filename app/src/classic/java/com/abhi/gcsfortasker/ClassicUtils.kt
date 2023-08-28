package com.abhi.gcsfortasker

import android.content.Context

fun validateInput(
    context: Context, valueFilter: String?, typeFilter: String
): Pair<Boolean, String> {
    val valueValid = true //stub
    val typeValid = if (typeFilter.isEmpty()) true else run {
        val filterArray = typeFilter.split(",").map { it.trim() }
        filterArray.all { it in barcodeTypes }
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
