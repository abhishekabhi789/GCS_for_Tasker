package com.abhi.gcsfortasker

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat

class ShortCutActivity : Activity() {
    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            getShortCutFromTasker()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun updateShortcut(
        context: Context,
        intent: Intent,
        icon: IconCompat,
        shortLabel: String,
        longLabel: String
    ) {
        val shortcutInfo = ShortcutInfoCompat.Builder(context, SHORTCUT_ID)
            .setIntent(intent)
            .setShortLabel(shortLabel)
            .setLongLabel(longLabel)
            .setIcon(icon)
            .build()

        val shortcutInfoList = mutableListOf(shortcutInfo)
        ShortcutManagerCompat.addDynamicShortcuts(context, shortcutInfoList)
        Log.d(TAG, "updateShortcut: updated")
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun createShortcut(context: Context) {
        if (!isShortcutAdded(context)) {
            val intent = Intent(
                "com.abhi.gcsfortasker.ADD_SHORTCUT",
                null,
                context,
                ShortCutActivity::class.java
            )
            val icon = IconCompat.createWithResource(context, R.drawable.ic_select)
            val shortLabel = context.getString(R.string.initial_shortcut_short_label)
            val longLabel = context.getString(R.string.initial_shortcut_long_label)
            updateShortcut(context, intent, icon, shortLabel, longLabel)
        }
    }

    private fun isShortcutAdded(context: Context): Boolean {
        val shortcuts = ShortcutManagerCompat.getDynamicShortcuts(context)
        for (shortcut in shortcuts) {
            if (shortcut.id == SHORTCUT_ID) return true
        }
        return false
    }

    private fun getShortCutFromTasker() {
        val componentName = ComponentName(TASKER_PACKAGE_NAME, TASKER_SHORTCUT_ACTIVITY_NAME)
        val intent = Intent(Intent.ACTION_CREATE_SHORTCUT)
        intent.component = componentName
        startActivityForResult(intent, CREATE_SHORTCUT_REQUEST_CODE)
        Log.d(TAG, "getShortCutFromTasker: waiting for results")
    }

    @Suppress("DEPRECATION")
    @RequiresApi(Build.VERSION_CODES.N_MR1)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_SHORTCUT_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                val uri =
                    if (data?.extras != null && data.extras!!.containsKey(Intent.EXTRA_SHORTCUT_INTENT)) {
                        val shortcutIntent =
                            data.extras!!.get(Intent.EXTRA_SHORTCUT_INTENT) as Intent
                        shortcutIntent.toUri(Intent.URI_INTENT_SCHEME)
                    } else {
                        data?.toUri(Intent.URI_INTENT_SCHEME) ?: return
                    }
                val shortcutName = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME)!!
                val icon = data.getParcelableExtra<Bitmap>(Intent.EXTRA_SHORTCUT_ICON)
                val shortcutIcon = IconCompat.createWithBitmap(icon!!)
                val intent = Intent.parseUri(uri, 0)
                Log.d(TAG, "onActivityResult: name $shortcutName")
                Log.d(TAG, "onActivityResult: uri $uri")
                updateShortcut(applicationContext, intent, shortcutIcon, shortcutName, shortcutName)
            } catch (e: Exception) {
                Log.e(TAG, "onActivityResult: ${e.message}")
            } finally {
                finish()
            }
        } else if (requestCode == CREATE_SHORTCUT_REQUEST_CODE && resultCode == RESULT_CANCELED) {
            Log.e(TAG, "onActivityResult: cancelled")
            getString(R.string.no_shortcut_from_tasker).toToast(this)
            finish()
        }
    }

    companion object {
        private const val SHORTCUT_ID = "dynamic_shortcut_1"
        private const val CREATE_SHORTCUT_REQUEST_CODE = 789
        private const val TASKER_PACKAGE_NAME = "net.dinglisch.android.taskerm"
        private const val TASKER_SHORTCUT_ACTIVITY_NAME =
            "$TASKER_PACKAGE_NAME.TaskerAppWidgetConfigureShortcut"
    }
}
