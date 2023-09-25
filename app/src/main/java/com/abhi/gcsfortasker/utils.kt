package com.abhi.gcsfortasker

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.abhi.gcsfortasker.tasker.action.CodeScannerActionHelper
import com.abhi.gcsfortasker.tasker.event.CodeScannedEventHelper
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions


/**Get the name of the field. isFormat should be true if values is from [Barcode.getFormat]*/
fun getNameOfTheField(value: Int, isFormat: Boolean): String? {
    val prefix = if (isFormat) "FORMAT_" else "TYPE_"
    val fields = Barcode::class.java.fields
    for (field in fields) {
        if (field.name.startsWith(prefix) && field.type == Int::class.javaPrimitiveType && field.getInt(
                null
            ) == value
        ) {
            return field.name.removePrefix(prefix).lowercase()
        }
    }
    return null
}

/**Returns an formatted string array of all code formats if [isFormat == true] otherwise code types */
fun getCodeFieldNames(isFormat: Boolean): Array<String> {
    return getCodeFields(isFormat).map { it ->
        it.split("_")
            .joinToString(" ") { it.replaceFirstChar { c: Char -> c.uppercaseChar() } }
    }.toTypedArray()
}

/**Returns an array of all code types*/
fun getCodeFields(isFormat: Boolean): Array<String> {
    val prefix = if (isFormat) "FORMAT_" else "TYPE_"
    return Barcode::class.java.fields.filter { it.name.startsWith(prefix) }.map {
        it.name.replaceFirst(prefix, "").lowercase()
    }.toTypedArray()
}

/**show the text as toast*/
fun String.toToast(context: Context) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(context, this, Toast.LENGTH_LONG).show()
    }
}
/**Shows a single choice list*/
fun Activity.showSingleChoiceDialog(
    title: String,
    variables: List<String>,
    callback: (String?) -> Unit
) {
    AlertDialog.Builder(this).apply {
        setIcon(R.mipmap.ic_launcher)
        setTitle(title)
        val arrayAdapter = ArrayAdapter<String>(
            this@showSingleChoiceDialog,
            android.R.layout.select_dialog_singlechoice
        ).apply {
            addAll(variables)
        }
        setAdapter(arrayAdapter) { _, which -> callback(arrayAdapter.getItem(which)) }
        setNegativeButton(getString(android.R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
    }.show()
}

/**Shows a multiple choice list*/
fun showMultiChoiceDialog(
    context: Context,
    title: String,
    allItems: Array<String>,
    checkedItems: BooleanArray?,
    onSelected: (BooleanArray) -> Unit
) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setIcon(R.mipmap.ic_launcher)

    builder.setMultiChoiceItems(allItems, checkedItems) { _, which, isChecked ->
        checkedItems?.set(which, isChecked)
    }

    builder.setPositiveButton(android.R.string.ok) { _, _ ->
        onSelected(checkedItems ?: BooleanArray(allItems.size) { false })
    }
    builder.setNegativeButton(android.R.string.cancel) { _, _ -> }
    builder.setNeutralButton(R.string.clear_all) { _: DialogInterface?, _: Int ->
        onSelected(BooleanArray(allItems.size) { false })
    }
    builder.setCancelable(false)
    builder.create().show()
}

/**This function generates list dialog for code format and code type*/
fun chooseFromList(activity: Activity, isFormat: Boolean) {
    val inputField: EditText =
        if (isFormat) activity.findViewById(R.id.format_filter) else activity.findViewById(R.id.type_filter)
    val valuesFromField = inputField.text.toString()
    val inputValues = valuesFromField.split(",").map { it.trim() }
    val codeTypes = getCodeFields(isFormat)
    val validInputValues = inputValues.filter { codeTypes.contains(it) }.toTypedArray()
    val checkedItems = BooleanArray(codeTypes.size) { index ->
        validInputValues.contains(codeTypes[index])
    }
    val title =
        if (isFormat) R.string.choose_code_format_dialog_title else R.string.choose_code_type_dialog_title
    showMultiChoiceDialog(
        activity,
        activity.getString(title),
        getCodeFieldNames(isFormat),
        checkedItems
    ) { isCheckedItems ->
        val selectedTexts = codeTypes.filterIndexed { index, _ ->
            isCheckedItems[index]
        }
        inputField.setText(selectedTexts.joinToString(","))
        inputField.error = null
    }
}

/**Helps to choose a tasker variable*/
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

/**Save Event configuration after validating*/
fun saveEventConfig(activity: Activity, taskerHelper: CodeScannedEventHelper) {
    val typeFilterField = activity.findViewById<EditText>(R.id.type_filter)
    typeFilterField.setText(typeFilterField.text.toString().replace(" ",""))
    val formatFilterField = activity.findViewById<EditText>(R.id.format_filter)
    formatFilterField.setText(formatFilterField.text.toString().replace(" ",""))
    val (isValid, reason) = validateEventConfigInput(
        activity,
        typeFilterField.text.toString(),
        formatFilterField.text.toString()
    )

    if (isValid && taskerHelper.onBackPressed().success) {
        taskerHelper.finishForTasker()
        Toast.makeText(activity, R.string.configuration_saved, Toast.LENGTH_SHORT).show()
    } else Toast.makeText(activity, "Error: $reason", Toast.LENGTH_SHORT).show()
}

/**Save Action configuration after validating*/
fun saveActionConfig(activity: Activity, taskerHelper: CodeScannerActionHelper) {
    val formatFilterField = activity.findViewById<EditText>(R.id.format_filter)
    formatFilterField.setText(formatFilterField.text.toString().replace(" ",""))
    val (isValid, reason) = validateActionConfigInput(activity, formatFilterField.text.toString())

    if (isValid && taskerHelper.onBackPressed().success) {
        taskerHelper.finishForTasker()
        Toast.makeText(activity, R.string.configuration_saved, Toast.LENGTH_SHORT).show()
    } else Toast.makeText(activity, "Error: $reason", Toast.LENGTH_SHORT).show()
}

/**sets formats for scanner option from action input filter*/
fun setBarcodeFormatsFromString(builder: GmsBarcodeScannerOptions.Builder, formatFilter: String) {
    val formatNames = formatFilter.split(",").map { it.trim() }
    val formats = mutableListOf<Int>()
    for (formatName in formatNames) {
        try {
            val constantName = "FORMAT_${formatName.uppercase()}"
            Log.d("TAG", "setBarcodeFormatsFromString: $constantName")
            val field = Barcode::class.java.getDeclaredField(constantName)
            val constantValue = field.getInt(null)
            if (constantValue < 1) continue //to avoid conflicts
            formats.add(constantValue)
        } catch (e: NoSuchFieldException) {
            // Handle the case when the field is not found
            Log.e("util:formatFromString", "Field not found for formatName: $formatName")
        } catch (e: IllegalAccessException) {
            // Handle the case when access to the field is not allowed
            Log.e("util:formatFromString", "Access to field denied for formatName: $formatName")
        }
    }
    if (formats.size == 1) builder.setBarcodeFormats(formats[0]) else
        builder.setBarcodeFormats(formats[0], *formats.subList(1, formats.size).toIntArray())
}
