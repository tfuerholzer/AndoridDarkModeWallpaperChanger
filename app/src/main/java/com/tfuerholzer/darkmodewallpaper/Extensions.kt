package com.tfuerholzer.darkmodewallpaper

import android.R.attr.data
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toFile
import com.tfuerholzer.darkmodewallpaper.preferences.Theme


fun Uri?.exists(): Boolean {
    return this != null && this.toFile() != null && this.toFile().exists()
}

fun Uri.readBitmap(context: Context) : Bitmap{
    return ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver,this))
}

fun Context.getAppPrefs() = getSharedPreferences("app", Context.MODE_PRIVATE)

fun Context.retrieveUri(uriKeyString: String): Uri? {
    val valueNotFound = "VALUE_NOT_FOUND"
    val result = getAppPrefs().getString(uriKeyString, valueNotFound)
    if (result == valueNotFound) {
        return null
    } else {
        return Uri.parse(result)
    }
}

val Context.isNightmodeEnabled : Boolean
    get() = {
        val configuration = resources.configuration
        val currentNightMode = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }()

val Context.currentTheme : Theme
    get() = if(this.isNightmodeEnabled) Theme.DARKMODE else Theme.LIGHTMODE



