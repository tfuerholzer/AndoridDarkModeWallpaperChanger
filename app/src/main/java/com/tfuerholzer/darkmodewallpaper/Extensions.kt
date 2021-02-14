package com.tfuerholzer.darkmodewallpaper

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toFile
import com.tfuerholzer.darkmodewallpaper.preferences.AspectRatio
import com.tfuerholzer.darkmodewallpaper.preferences.Theme


fun Uri?.exists(): Boolean {
    return this != null && this.toFile() != null && this.toFile().exists()
}

fun Uri.readBitmap(context: Context, aspectRatio: AspectRatio) : Bitmap{
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && false){
        return ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver,this))
    }else{
        return BitmapFactory.decodeFile(this.toFile().absolutePath).scale(aspectRatio)
    }

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

fun Drawable.toIcon(asSquare : Boolean = false, adaptive : Boolean = true): Icon {
    val bitmap = if (asSquare) toBitmap(64,64) else toBitmap()
    return if (adaptive) Icon.createWithAdaptiveBitmap(bitmap) else Icon.createWithBitmap(bitmap)
}

fun Bitmap.scale(aspectRatio: AspectRatio, filter : Boolean = true) =
    Bitmap.createScaledBitmap(this,aspectRatio.screenWidth,aspectRatio.screenHeight,filter)



