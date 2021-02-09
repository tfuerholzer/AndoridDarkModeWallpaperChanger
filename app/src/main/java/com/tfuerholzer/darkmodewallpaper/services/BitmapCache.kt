package com.tfuerholzer.darkmodewallpaper.services

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.tfuerholzer.darkmodewallpaper.R
import com.tfuerholzer.darkmodewallpaper.preferences.AspectRatio
import com.tfuerholzer.darkmodewallpaper.preferences.PreferenceManager
import com.tfuerholzer.darkmodewallpaper.preferences.Theme
import com.tfuerholzer.darkmodewallpaper.readBitmap
import com.tfuerholzer.darkmodewallpaper.services.BitmapMap.Companion.singleColorBitmap

class BitmapCache (private val preferenceManager: PreferenceManager){

    private val NOT_LOADED_BITMAP = singleColorBitmap(1,1)
    private val NOT_FOUND_BITMAP = singleColorBitmap(1,1, R.color.white)

    private val bitmapMap = BitmapMap()

    constructor(context : Context) : this(PreferenceManager(context))

    fun initFoundBitmaps(context: Context){
        preferenceManager.getAllWallpaperPrefs().parallelStream()
            .map{ Pair(PreferenceManager.parseWallpaperKey(it.first),it.second)}
            .filter { bitmapMap[it.first] == null }
            .map { Pair(it.first, it.second.readBitmap(context)) }
            .forEach{ bitmapMap[it.first] = it.second}
    }

    fun get(context : Context, theme : Theme, aspectRatio: AspectRatio) : Bitmap{
        val result = bitmapMap.get(theme,aspectRatio)
        if (result != null){
            return result
        }else{
            val uri = preferenceManager.getURI(theme, aspectRatio)
            if (uri != null){
                val image = uri.readBitmap(context)
                this.bitmapMap.put(theme,aspectRatio,image)
                return image
            }else{
                val uri = preferenceManager.getDefaultURI(theme)
                val image = uri?.readBitmap(context) ?: NOT_FOUND_BITMAP
                this.bitmapMap.put(theme,aspectRatio,image)
                return image
            }
        }
    }

}