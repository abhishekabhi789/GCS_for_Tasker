package com.abhi.gcsfortasker.utils

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.DrawableRes
import com.abhi.gcsfortasker.R

/** Methods related to User interaction*/
object UiUtils {

    /**show the text as toast*/
    fun String.toToast(context: Context) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, this, Toast.LENGTH_LONG).show()
        }
    }

    /**Shows a single choice list*/
    fun showSingleChoiceDialog(
        context: Context,
        title: String,
        @DrawableRes icon: Int,
        items: List<String>,
        onSelection: (String?) -> Unit
    ) {
        val arrayAdapter =
            ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice).apply {
                addAll(items)
            }
        AlertDialog.Builder(context).apply {
            setIcon(icon)
            setTitle(title)
            setAdapter(arrayAdapter) { _, which -> onSelection(arrayAdapter.getItem(which)) }
            setNegativeButton(context.getString(android.R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }

    /**Shows a multiple choice list*/
    fun showMultiChoiceDialog(
        context: Context,
        title: String,
        allItems: List<String>,
        selectedItems: List<String>,
        onSelection: (List<String>) -> Unit
    ) {
        val checkedItems = BooleanArray(allItems.size) { index ->
            selectedItems.contains(allItems[index])
        }
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setIcon(R.mipmap.ic_launcher)
            setMultiChoiceItems(allItems.toTypedArray(), checkedItems) { _, i, isChecked ->
                checkedItems[i] = isChecked
            }
            setPositiveButton(android.R.string.ok) { _, _ ->
                onSelection(allItems.filterIndexed { index, _ -> checkedItems[index] })
            }
            setNegativeButton(android.R.string.cancel) { _, _ -> }
            setNeutralButton(R.string.toggle_all, null)
            setCancelable(false)
        }.create()
            .run {
                show()
                getButton(AlertDialog.BUTTON_NEUTRAL)?.setOnClickListener {
                    ((listView.count - listView.checkedItemCount) != 0).let { newState ->
                        checkedItems.run {
                            fill(newState)//otherwise originally checked item may restore
                            forEachIndexed { i, _ -> listView.setItemChecked(i, newState) }
                        }
                    }
                }
            }
    }
}
