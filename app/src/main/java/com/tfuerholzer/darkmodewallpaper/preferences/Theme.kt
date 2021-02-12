package com.tfuerholzer.darkmodewallpaper.preferences

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import com.tfuerholzer.darkmodewallpaper.ShortcutCreator
import java.lang.IllegalArgumentException

enum class Theme(val themeCode : Int, val shortcutID : String) {
    LIGHTMODE(UI_MODE_NIGHT_NO,ShortcutCreator.SHORTCUT_LIGHTMODE_ID),
    DARKMODE(UI_MODE_NIGHT_YES,ShortcutCreator.SHORTCUT_DARKMODE_ID);

    companion object {
        fun fromCode(themeCode: Int) : Theme {
            return values().findLast { it.themeCode == themeCode }
                ?: throw IllegalArgumentException("Theme $themeCode doesn't exist!")
        }
    }
}