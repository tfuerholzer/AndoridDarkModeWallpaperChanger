package com.tfuerholzer.darkmodewallpaper

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Color
import android.graphics.ColorFilter
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.tfuerholzer.darkmodewallpaper.preferences.AspectRatio
import com.tfuerholzer.darkmodewallpaper.preferences.PreferenceManager
import com.tfuerholzer.darkmodewallpaper.preferences.Theme
import java.util.stream.Collectors

object ShortcutCreator {

    const val SHORTCUT_THEME = "SHORTCUT_THEME"
    const val SHORTCUT_DARKMODE_ID = "SHORTCUT_DARKMODE"
    const val SHORTCUT_LIGHTMODE_ID = "SHORTCUT_LIGHTMODE"

    private fun getIntent(context: Context, theme: Theme): Intent {
        val newIntent = Intent(context, MainActivity::class.java)
        newIntent.action = "LOCATION_SHORTCUT"
        newIntent.putExtra(SHORTCUT_THEME, theme.themeCode)
        return newIntent
    }

    fun createDarkmodeShortcut(
        context: Context,
        imageview: ImageView,
        dynamicIcon: Boolean
    ): ShortcutInfo {
        val icon = imageview.drawable.toIcon(true, dynamicIcon)
        return ShortcutInfo.Builder(context, SHORTCUT_DARKMODE_ID)
            .setShortLabel("Change Darkmode")
            .setLongLabel("Change the Darkmode-Wallpaper")
            .setIcon(icon)
            .setIntent(getIntent(context, Theme.DARKMODE))
            .build()
    }

    fun createLightmodeShortcut(
        context: Context,
        imageview: ImageView,
        dynamicIcon: Boolean
    ): ShortcutInfo {
        val icon = imageview.drawable.toIcon(true, dynamicIcon)
        return ShortcutInfo.Builder(context, SHORTCUT_LIGHTMODE_ID)
            .setShortLabel("Change Lightmode")
            .setLongLabel("Change the Lightmode-Wallpaper")
            .setIcon(icon)
            .setIntent(getIntent(context, Theme.LIGHTMODE))
            .build()
    }

    fun getShortcutManager(context: Context) =
        ContextCompat.getSystemService<ShortcutManager>(context, ShortcutManager::class.java)

    @Deprecated("Shouldn't be used")
    fun createShortcuts(context: Context, darkmodeImage: ImageView, lightmodeImage: ImageView) {
        Thread {
            val shortcutManager = getShortcutManager(context)
            if (shortcutManager != null && shortcutManager.dynamicShortcuts.size != 2) {
                val lightmodeShortcut = createLightmodeShortcut(context, lightmodeImage, false)
                val darkmodeShortcut = createDarkmodeShortcut(context, darkmodeImage, false)
                shortcutManager!!.dynamicShortcuts = listOf(lightmodeShortcut, darkmodeShortcut)
            }
        }.start()
    }

    fun updateShortcut(
        context: Context, imageview: ImageView, theme: Theme, dynamicIcon: Boolean
    ) {
        Thread {
            val manager = getShortcutManager(context)
            if (manager != null) {
                val shortcut = if (theme == Theme.LIGHTMODE) {
                    createLightmodeShortcut(context, imageview, dynamicIcon)
                } else {
                    createDarkmodeShortcut(context, imageview, dynamicIcon)
                }
                updateDynamicShortcut(manager, shortcut)
            }
        }.start()
    }

    @Synchronized
    private fun updateDynamicShortcut(manager: ShortcutManager, shortcut: ShortcutInfo) {
        manager.addDynamicShortcuts(listOf(shortcut))
    }
}