package com.jorgelillo.decisionwheel.data

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.jorgelillo.decisionwheel.MainActivity
import com.jorgelillo.decisionwheel.R
import com.jorgelillo.decisionwheel.domain.Wheel

/** Long-press on the app icon: favourite and recent wheels, straight to spinning. */
object Shortcuts {
    const val EXTRA_WHEEL_ID = "wheel_id"

    fun update(context: Context, wheels: List<Wheel>) {
        val shortcuts = wheels.mapIndexed { rank, wheel ->
            val intent = Intent(context, MainActivity::class.java)
                .setAction(Intent.ACTION_VIEW)
                .putExtra(EXTRA_WHEEL_ID, wheel.id)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            ShortcutInfoCompat.Builder(context, "wheel-${wheel.id}")
                .setShortLabel(wheel.name.take(SHORT_LABEL_MAX))
                .setLongLabel(wheel.name)
                .setIcon(IconCompat.createWithResource(context, R.mipmap.ic_launcher))
                .setIntent(intent)
                .setRank(rank)
                .build()
        }
        // Best effort: some launchers limit or refuse dynamic shortcuts.
        runCatching { ShortcutManagerCompat.setDynamicShortcuts(context, shortcuts) }
    }

    /** Tells the launcher a wheel was opened, so it can rank its shortcut. */
    fun reportUsed(context: Context, wheelId: String) {
        runCatching { ShortcutManagerCompat.reportShortcutUsed(context, "wheel-$wheelId") }
    }

    private const val SHORT_LABEL_MAX = 25
}
