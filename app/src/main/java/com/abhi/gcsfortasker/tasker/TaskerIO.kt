package com.abhi.gcsfortasker.tasker

import com.abhi.gcsfortasker.R
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputField
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputRoot
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputObject
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputVariable

/**Event configs*/
@TaskerInputRoot
class EventInputFilter @JvmOverloads constructor(

    @field:TaskerInputField("valueFilter", labelResId = R.string.value_filter)
    val valueFilter: String? = null,

    @field:TaskerInputField("typeFilter", labelResId = R.string.type_filter)
    val typeFilter: String? = null,

    @field:TaskerInputField("useRegex", labelResId = R.string.use_regex)
    val useRegex: Boolean = false, //default value

    @field:TaskerInputField("formatFilter", labelResId = R.string.format_filter)
    val formatFilter: String? = null

)

/**Action configs*/
@TaskerInputRoot
class ActionInputFilter @JvmOverloads constructor(

    @field:TaskerInputField("formatFilter", labelResId = R.string.format_filter)
    val formatFilter: String? = null,

    @field:TaskerInputField("allowZoom", labelResId = R.string.enable_auto_zoom)
    val allowZooming: Boolean = true, //default value

    @field:TaskerInputField("allowManualInput", labelResId = R.string.allow_manual_input)
    val allowManualInput: Boolean = false //default value

)

/**Both event and action uses this output class.*/
@TaskerInputRoot
@TaskerOutputObject
class CodeOutput @JvmOverloads constructor(

    @field:TaskerInputField("gcs_rawvalue")
    @get:TaskerOutputVariable(
        "gcs_value_raw",
        R.string.variable_raw_value_label,
        R.string.variable_raw_value_description
    )
    val rawValue: String? = null,

    @field:TaskerInputField("gcs_displayvalue")
    @get:TaskerOutputVariable(
        "gcs_value_display",
        R.string.variable_display_value_label,
        R.string.variable_display_value_description
    )
    val displayValue: String? = null,

    @field:TaskerInputField("gcs_codetype")
    @get:TaskerOutputVariable(
        "gcs_code_type",
        R.string.variable_code_type_label,
        R.string.variable_code_type_description
    )
    val codeType: String? = null,

    @field:TaskerInputField("gcs_codeformat")
    @get:TaskerOutputVariable(
        "gcs_code_format",
        R.string.variable_code_format_label,
        R.string.variable_code_format_description
    )
    val codFormat: String? = null

)
