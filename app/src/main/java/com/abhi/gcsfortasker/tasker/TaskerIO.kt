package com.abhi.gcsfortasker.tasker

import com.abhi.gcsfortasker.R
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputField
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputRoot
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputObject
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputVariable

/**Event configs*/
@TaskerInputRoot
class EventInputFilter @JvmOverloads constructor(
    @field:TaskerInputField("valueFilter") val valueFilter: String? = null,
    @field:TaskerInputField("typeFilter") val typeFilter: String? = null,
    @field:TaskerInputField("useRegex") val useRegex: Boolean = false, //default value
    @field:TaskerInputField("formatFilter") val formatFilter:String? = null
)
/**Action configs*/
@TaskerInputRoot
class ActionInputFilter @JvmOverloads constructor(
    @field:TaskerInputField("formatFilter") val formatFilter:String? = null,
    @field:TaskerInputField("allowZoom") val allowZooming:Boolean = true, //default value
    @field:TaskerInputField("allowManualInput") val allowManualInput:Boolean = false //default value
)
/**Both event and action uses this output class.*/
@TaskerInputRoot
@TaskerOutputObject()
class CodeOutput @JvmOverloads constructor(
    @field:TaskerInputField("gcs_rawvalue") @get:TaskerOutputVariable("gcs_value_raw", R.string.io_out_raw_value_label,R.string.io_out_raw_value_description) var rawValue: String? = null,
    @field:TaskerInputField("gcs_displayvalue") @get:TaskerOutputVariable("gcs_value_display",R.string.io_out_display_value_label,R.string.io_out_display_value_description) var displayValue: String? = null,
    @field:TaskerInputField("gcs_codetype") @get:TaskerOutputVariable("gcs_code_type",R.string.io_out_code_type_label,R.string.io_out_code_type_description) var codeType: String? = null,
    @field:TaskerInputField("gcs_codeformat") @get:TaskerOutputVariable("gcs_code_format",R.string.io_out_code_format_label, R.string.io_out_code_format_description) var codFormat:String? = null
)