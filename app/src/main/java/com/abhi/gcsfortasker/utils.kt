package com.abhi.gcsfortasker

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.EditText
import android.widget.Toast
import com.abhi.gcsfortasker.tasker.event.CodeScannedEventHelper
import java.util.Arrays
/**An array of all code types*/
val barcodeTypes = arrayOf(
    "unknown",
    "contact_info",
    "email",
    "isbn",
    "phone",
    "product",
    "sms",
    "text",
    "url",
    "wifi",
    "geo",
    "calendar_event",
    "driver_license"
)

/**Returns an array of all code types formatted*/
fun getCodeTypesNames(): Array<String> {
    return barcodeTypes.map { it ->
        it.split("_").joinToString(" ") { it.replaceFirstChar { c: Char -> c.uppercaseChar() } }
    }.toTypedArray()
}
/**This alert dialog shows a multiple choice list of QR code types and add selected codes to the type filter input field */

fun showTypeDialog(
    context: Context,
    selectedCodeTypes: Array<String>?,
    onSelected: (Array<String>) -> Unit
) {
    val allCodeTypes = barcodeTypes
    val checkedItems = BooleanArray(allCodeTypes.size) { index ->
        selectedCodeTypes?.contains(allCodeTypes[index]) ?: false
    }

    val builder = AlertDialog.Builder(context)
    builder.setTitle(context.getString(R.string.choose_code_type_dialog_title))
    builder.setIcon(R.mipmap.ic_launcher)

    builder.setMultiChoiceItems(getCodeTypesNames(), checkedItems) { _, which, isChecked ->
        checkedItems[which] = isChecked
    }

    builder.setPositiveButton(android.R.string.ok) { _, _ ->
        val selectedTypes = allCodeTypes.filterIndexed { index, _ ->
            checkedItems[index]
        }.toTypedArray()
        onSelected(selectedTypes)
    }

    builder.setNegativeButton(android.R.string.cancel) { _, _ -> }
    builder.setNeutralButton(R.string.clear) { _: DialogInterface?, _: Int ->
        Arrays.fill(checkedItems, false)
        onSelected(emptyArray())
    }
    builder.setCancelable(false)
    builder.create().show()
}

fun chooseTypes(activity: Activity) {
    val typeFilterField = activity.findViewById<EditText>(R.id.typeFilter)
    val valuesFromField = typeFilterField.text.toString()
    val inputValues = valuesFromField.split(",").map { it.trim() }
    val codeTypes = barcodeTypes
    val validInputValues = inputValues.filter { codeTypes.contains(it) }.toTypedArray()
    showTypeDialog(activity, validInputValues) { value ->
        typeFilterField.setText(value.joinToString(","))
        typeFilterField.error = null
    }
}

fun saveInput(activity: Activity, taskerHelper: CodeScannedEventHelper) {
    val typeFilterField = activity.findViewById<EditText>(R.id.typeFilter)
    typeFilterField.setText(typeFilterField.text.toString().replace(" ", ""))
    val (isValid, reason) = validateInput(
        activity,
        null,
        typeFilterField.text.toString()
    )

    if (isValid && taskerHelper.onBackPressed().success) {
        taskerHelper.finishForTasker()
        Toast.makeText(activity, R.string.configuration_saved, Toast.LENGTH_SHORT).show()
    } else Toast.makeText(activity, "Error: $reason", Toast.LENGTH_SHORT).show()
}

