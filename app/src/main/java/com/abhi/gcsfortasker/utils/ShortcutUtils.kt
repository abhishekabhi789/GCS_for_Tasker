package com.abhi.gcsfortasker.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.abhi.gcsfortasker.R
import com.abhi.gcsfortasker.ShortCutActivity

/** Handles shortcut related operations*/
object ShortcutUtils {

    private const val TAG = "ShortcutUtils"
    private const val SHORTCUT_ID = "dynamic_shortcut_1"
    private const val SHORTCUT_ACTIVITY_INTENT = "com.abhi.gcsfortasker.ADD_SHORTCUT"

    /**
     * Checks if the shortcut is already initialized.
     */
    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun isShortcutInitialized(context: Context): Boolean {
        return ShortcutManagerCompat.getDynamicShortcuts(context).any { it.id == SHORTCUT_ID }
    }

    /**Creates the initial shortcut, which launches [ShortCutActivity] to retrieve a task shortcut from tasker*/
    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun initializeShortcut(context: Context): Boolean {
        return if (!isShortcutInitialized(context)) {
            val intent =
                Intent(SHORTCUT_ACTIVITY_INTENT, null, context, ShortCutActivity::class.java)
            val icon = IconCompat.createWithResource(context, R.drawable.ic_select)
            val shortLabel = context.getString(R.string.initial_shortcut_short_label)
            val longLabel = context.getString(R.string.initial_shortcut_long_label)
            updateShortcut(context, intent, icon, shortLabel, longLabel)
        } else false.also { Log.i(TAG, "createShortcut: shortcut already added") }
    }

    /**Updates the existing shortcut.*/
    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun updateShortcut(
        context: Context,
        intent: Intent,
        icon: IconCompat,
        shortLabel: String,
        longLabel: String
    ): Boolean {
        val shortcutInfo = ShortcutInfoCompat.Builder(context, SHORTCUT_ID).run {
            setIntent(intent)
            setShortLabel(shortLabel)
            setLongLabel(longLabel)
            setIcon(icon)
            build()
        }
        return ShortcutManagerCompat.pushDynamicShortcut(context, shortcutInfo).also {
            Log.i(TAG, "updateShortcut: updated $shortLabel")
        }
    }
}
