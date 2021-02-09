package com.tfuerholzer.darkmodewallpaper.preferences

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toFile
import com.tfuerholzer.darkmodewallpaper.getAppPrefs
import com.tfuerholzer.darkmodewallpaper.preferences.Theme.*
import java.lang.Integer.parseInt
import java.util.stream.Collectors

class PreferenceManager (private val context : Context){

    private val prefs : SharedPreferences
        get() = context.getAppPrefs()

    var singleScreenMode = prefs.getBoolean(IS_SINGLE_SCREEN_MODE,true)
        set(value){
            field = value
            val editor = prefs.edit()
            editor.putBoolean(IS_SINGLE_SCREEN_MODE,value)
            editor.commit()
        }

    var defaultAspectRatio : AspectRatio? = null

    init {
        cleanupPrefs()
    }

    fun cleanupPrefs() {
        prefs.all.entries.parallelStream().forEach{ (value) ->

        }
    }



    fun put(key : String, value : String){
        if (!value.equals(EMPTY_VALUE)){
            delete(key)
        }
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun put(key : String, value : Uri){
        val uriString = value.toString()
        put(key,uriString)
    }

    fun put(theme: Theme, aspectRatio: AspectRatio, value: String){
        val key = generateKeyString(theme, aspectRatio)
        put(key,value)
    }

    fun delete(key : String){
        val uri = getURI(key)
        uri?.toFile()?.delete()
        put(key, EMPTY_VALUE)
    }

    fun get(key : String?) : String?{
        return prefs.getString(key, null)
    }

    fun delete(theme: Theme, aspectRatio: AspectRatio){
        val key = generateKeyString(theme, aspectRatio)
        delete(key)
    }

    fun get(theme: Theme, aspectRatio: AspectRatio) : String?{
        val key = generateKeyString(theme, aspectRatio)
        return prefs.getString(key, null)
    }


    fun getURI(key : String) : Uri?{
        val value = get(key)
        return if (value == null) null else Uri.parse(value)
    }

    fun getURI(theme: Theme, aspectRatio: AspectRatio) : Uri?{
        val value = get(theme, aspectRatio)
        return if (value == null) null else Uri.parse(value)
    }

    fun getDefault(theme: Theme) : String?{
        val value =  prefs.all.entries.stream()
            .filter { it.value is String && isWallpaperKey(it.key) }
            .map { Pair(parseWallpaperKey(it.key), it.value as String) }
            .filter { it.first.first == theme }
            .sorted { o1, o2 -> o1.first.second.screenHeight - o2.first.second.screenHeight }
            .findFirst()
            .orElse(null)
        return value.second
    }

    fun getDefaultURI(theme: Theme) : Uri?{
        val value = getDefault(theme)
        return if (value == null) null else Uri.parse(value)
    }

    fun getAllWallpaperPrefs(): MutableList<Pair<String, Uri>> {
        return prefs.all.entries.parallelStream()
            .filter { isWallpaperKey(it.key) && it.value is String}
            .map { Pair(it.key, Uri.parse(it.value as String)) }
            .collect(Collectors.toList())
    }

    companion object{
        private const val  WALLPAPER_PREFIX = "WALLPAPER_"
        private const val EMPTY_VALUE = "EMPTY_VALUE"
        private const val IS_SINGLE_SCREEN_MODE = "IS_SINGLE_SCREEN_MODE"
        private const val KEY_PREFIX_LEN = WALLPAPER_PREFIX.length

        fun generateKeyString(theme : Theme, aspectRatio: AspectRatio) : String{
            return "${WALLPAPER_PREFIX}${theme.themeCode}_${aspectRatio}"
        }

        fun isWallpaperKey(potentialKeyString : String) : Boolean = potentialKeyString.startsWith(
            WALLPAPER_PREFIX)

        fun parseWallpaperKey(keyString : String) : Pair<Theme, AspectRatio>{
            val split = keyString.drop(KEY_PREFIX_LEN).split('_')
            val themeCode = parseInt(split[0])
            val theme = if (themeCode == Configuration.UI_MODE_NIGHT_NO) LIGHTMODE else DARKMODE
            val aspectRatio = AspectRatio.parseAspectRatioString(split[1])
            return Pair(theme,aspectRatio)
        }
    }


}