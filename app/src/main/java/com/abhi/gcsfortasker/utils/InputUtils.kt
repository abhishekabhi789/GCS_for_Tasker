package com.abhi.gcsfortasker.utils

import com.abhi.gcsfortasker.utils.BarcodeUtils.getCodeFields
import com.google.mlkit.vision.barcode.common.Barcode
import net.dinglisch.android.tasker.TaskerPlugin

object InputUtils {
    /**A valid format filter will either empty or belongs to [Barcode.getFormat]*/
    fun isValidFormatFilter(input: String): Boolean {
        return if (input.isEmpty()) true
        else run {
            val filterArray = input.split(",").map { it.trim() }
            filterArray.all { it in getCodeFields(true) }
        }
    }

    /**Check whether the format filter input config is valid or not, if it is not a variable input, it will be checked at [isValidFormatFilter]*/
    fun isValidFormatConfig(input: String): Boolean {
        return if (input.startsWith("%")) {
            TaskerPlugin.variableNameValid(input.replace("()", ""))
        } else isValidFormatFilter(input)
    }
}