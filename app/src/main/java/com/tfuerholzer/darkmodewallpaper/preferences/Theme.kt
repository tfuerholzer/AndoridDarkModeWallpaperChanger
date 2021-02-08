package com.tfuerholzer.darkmodewallpaper.preferences

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES

enum class Theme(val themeCode : Int) {
    LIGHTMODE(UI_MODE_NIGHT_NO),
    DARKMODE(UI_MODE_NIGHT_YES)
}