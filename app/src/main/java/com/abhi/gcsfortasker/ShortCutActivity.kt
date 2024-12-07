package com.abhi.gcsfortasker

import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.IconCompat
import com.abhi.gcsfortasker.utils.ShortcutUtils
import com.abhi.gcsfortasker.utils.UiUtils.toToast
import net.dinglisch.android.tasker.TaskerIntent

class ShortCutActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private val shortcutLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { handleShortcutInfo(it) }
            } else {
                Log.e(TAG, "onActivityResult: cancelled")
                getString(R.string.no_shortcut_from_tasker).toToast(this)
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            //the default shortcut launches this activity.
            //upon launching, the activity asks tasker to let user choose a task as shortcut.
            getShortCutFromTasker()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun getShortCutFromTasker() {
        val intent = Intent(Intent.ACTION_CREATE_SHORTCUT)
        intent.component = ComponentName(TASKER_PACKAGE_NAME, TASKER_SHORTCUT_ACTIVITY_NAME)
        shortcutLauncher.launch(intent)
        Log.i(TAG, "getShortCutFromTasker: waiting for results")
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun handleShortcutInfo(data: Intent) {
        try {
            val shortcutIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT, Intent::class.java)
            else data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT)

            val uri = shortcutIntent?.toUri(Intent.URI_INTENT_SCHEME)
                ?: data.toUri(Intent.URI_INTENT_SCHEME)

            val shortcutName = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME)!!

            val icon = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON, Bitmap::class.java)
            else data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON)
            val shortcutIcon = IconCompat.createWithBitmap(icon!!)

            val intent = Intent.parseUri(uri, 0)

            Log.i(TAG, "onActivityResult: name $shortcutName")
            Log.d(TAG, "onActivityResult: uri $uri")

            ShortcutUtils.updateShortcut(
                context = this,
                intent = intent,
                icon = shortcutIcon,
                shortLabel = shortcutName,
                longLabel = shortcutName
            ).also { isShortcutAdded ->
                Log.i(TAG, "onActivityResult: shortcut added for $shortcutName")
                if (isShortcutAdded)
                    getString(R.string.shortcut_added_for_task_name, shortcutName).toToast(this)
            }
        } catch (e: Exception) {
            Log.e(TAG, "onActivityResult: failed to retrieve task shortcut info ${e.message}")
        } finally {
            finish()
        }
    }

    companion object {
        private const val TAG = "ShortCutActivity"
        private const val TASKER_PACKAGE_NAME = TaskerIntent.TASKER_PACKAGE_MARKET
        private const val TASKER_SHORTCUT_ACTIVITY_NAME =
            "$TASKER_PACKAGE_NAME.TaskerAppWidgetConfigureShortcut"
    }
}
