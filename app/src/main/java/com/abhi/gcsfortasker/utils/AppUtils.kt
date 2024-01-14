package com.abhi.gcsfortasker.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.abhi.gcsfortasker.R
import com.abhi.gcsfortasker.utils.BarcodeUtils.getCodeFieldNames
import com.abhi.gcsfortasker.utils.BarcodeUtils.getCodeFields

object AppUtils {

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
}