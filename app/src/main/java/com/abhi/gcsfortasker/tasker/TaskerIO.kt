package com.abhi.gcsfortasker.tasker

import com.joaomgcd.taskerpluginlibrary.input.TaskerInputField
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputRoot
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputObject
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputVariable

/**Both event and action uses this output class.*/
@TaskerInputRoot
class CodeInputFilter @JvmOverloads constructor(
    @field:TaskerInputField("valueFilter") val valueFilter: String? = null,
    @field:TaskerInputField("typeFilter") val typeFilter: String? = null,
    @field:TaskerInputField("useRegex") val useRegex: Boolean = false//default value
)

@TaskerInputRoot
@TaskerOutputObject()
class CodeOutput @JvmOverloads constructor(
    @field:TaskerInputField("gcs_rawvalue") @get:TaskerOutputVariable("gcs_value_raw") var codeValue: String? = null,
    @field:TaskerInputField("gcs_codetype") @get:TaskerOutputVariable("gcs_code_type") var codeType: String? = null,
    @field:TaskerInputField("gcs_displayvalue") @get:TaskerOutputVariable("gcs_value_display") var displayValue: String? = null
    )