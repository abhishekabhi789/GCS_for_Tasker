package com.abhi.gcsfortasker.utils

import android.app.Activity
import android.widget.EditText
import android.widget.Toast
import com.abhi.gcsfortasker.R
import com.abhi.gcsfortasker.tasker.action.CodeScannerActionHelper
import com.abhi.gcsfortasker.tasker.event.CodeScannedEventHelper
import com.abhi.gcsfortasker.utils.AppUtils.showSingleChoiceDialog
import com.abhi.gcsfortasker.utils.AppUtils.toToast
import com.abhi.gcsfortasker.validateActionConfigInput
import com.abhi.gcsfortasker.validateEventConfigInput

object TaskerUtils {

    /**Helps to choose a tasker variable
     * @param activity needed to ask user input and set the chosen value on the field.
     * @param taskerHelper taskerHelper instance associated with the plugin configuration.*/
    fun chooseVariable(activity: Activity, taskerHelper: CodeScannerActionHelper) {
        val relevantVariables = taskerHelper.relevantVariables.toList()
        if (relevantVariables.isEmpty()) {
            activity.getString(R.string.no_variable_to_show).toToast(activity)
            return
        }
        activity.showSingleChoiceDialog(
            activity.getString(R.string.choose_a_variable),
            relevantVariables
        ) {
            if (!it.isNullOrEmpty()) activity.findViewById<EditText>(R.id.format_filter).setText(it)
        }
    }

/**Verifies and saves the plugin configuration
 * @param activity needed to get and update the user input while validating.
 * @param helper taskerHelper instance associated with the plugin configuration.*/
    fun savePluginConfig(activity: Activity, helper: Any) {
        lateinit var taskerHelper: Any
        taskerHelper = when (helper) {
            is CodeScannerActionHelper -> helper
            is CodeScannedEventHelper -> helper
            else -> return
        }
        val formatFilterField = activity.findViewById<EditText>(R.id.format_filter)
        formatFilterField.setText(formatFilterField.text.toString().replace(" ", ""))
        val (isValid, reason) = when (taskerHelper) {
            is CodeScannerActionHelper -> {
                validateActionConfigInput(activity, formatFilterField.text.toString())
            }
            is CodeScannedEventHelper -> {
                val typeFilterField = activity.findViewById<EditText>(R.id.type_filter)
                typeFilterField.setText(typeFilterField.text.toString().replace(" ", ""))
                validateEventConfigInput(
                    activity,
                    typeFilterField.text.toString(),
                    formatFilterField.text.toString()
                )
            }
            else -> return
        }
        if (isValid && taskerHelper.onBackPressed().success) {
            taskerHelper.finishForTasker()
            Toast.makeText(activity, R.string.configuration_saved, Toast.LENGTH_SHORT).show()
        } else Toast.makeText(activity, "Error: $reason", Toast.LENGTH_SHORT).show()
    }
}