package com.abhi.gcsfortasker

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.abhi.gcsfortasker.tasker.event.CodeScannedEventHelper
import java.util.Arrays

/**This alert dialog shows a multiple choice list of QR code types and add selected codes to the type filter input field */
fun showTypeDialog(context: Context, selectedIndex: IntArray?, onSelected: (IntArray) -> Unit) {
    // TODO: replace this
    val listItems = arrayOf(
        "TYPE_UNKNOWN",
        "TYPE_CONTACT_INFO",
        "TYPE_EMAIL",
        "TYPE_ISBN",
        "TYPE_PHONE",
        "TYPE_PRODUCT",
        "TYPE_SMS",
        "TYPE_TEXT",
        "TYPE_URL",
        "TYPE_WIFI",
        "TYPE_GEO",
        "TYPE_CALENDAR_EVENT",
        "TYPE_DRIVER_LICENSE"
    )

    val checkedItems = BooleanArray(listItems.size) { index ->
        selectedIndex?.contains(index) ?: false
    }

    val builder = AlertDialog.Builder(context)
    builder.setTitle("Choose Types")
    builder.setIcon(R.mipmap.ic_launcher)

    builder.setMultiChoiceItems(listItems, checkedItems) { _, which, isChecked ->
        checkedItems[which] = isChecked
    }

    builder.setPositiveButton("Done") { dialog, which ->
        val selectedIndices = checkedItems
            .mapIndexed { index, checked -> if (checked) index else null }
            .filterNotNull()
            .toIntArray()
        onSelected(selectedIndices)
    }

    builder.setNegativeButton("CANCEL") { dialog, which -> }
    builder.setNeutralButton("CLEAR ALL") { dialog: DialogInterface?, which: Int ->
        Arrays.fill(checkedItems, false)
        onSelected(IntArray(0))
    }
    builder.setCancelable(false)
    builder.create().show()
}

fun chooseTypes(activity: Activity) {
    val typeFilterField = activity.findViewById<EditText>(R.id.typeFilter)
    var indicesFromTextField = typeFilterField.text.toString()

    val (isValid, reason) = validateInput(activity, null, indicesFromTextField)
    //clearing the invalid type input to avoid crash
    if (!isValid && reason.contains("type")) indicesFromTextField = ""
    var selectedIndices: IntArray? = null
    if (indicesFromTextField.isNotEmpty()) {
        selectedIndices =
            indicesFromTextField.split(",").map { it.replace(" ", "").trim().toInt() }
                .toIntArray()
    }
    showTypeDialog(activity, selectedIndices) { selectedIndices ->
        // do something with the selected indices, e.g. save them to a variable
        Log.d("SelectedIndices", selectedIndices.contentToString())
        typeFilterField.setText(selectedIndices.joinToString(","))
        typeFilterField.error = null
    }
}

fun saveInput(activity: Activity, taskerHelper: CodeScannedEventHelper) {
    val typeFilterField = activity.findViewById<EditText>(R.id.typeFilter)
    val (isValid, reason) = validateInput(
        activity,
        null,
        typeFilterField.text.toString()
    )

    if (isValid && taskerHelper.onBackPressed().success) {
        taskerHelper.finishForTasker()
        Toast.makeText(activity, "Configuration Saved", Toast.LENGTH_SHORT).show()
    } else Toast.makeText(activity, "Error: $reason", Toast.LENGTH_SHORT).show()
}

