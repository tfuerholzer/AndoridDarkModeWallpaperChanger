package com.tfuerholzer.darkmodewallpaper.preferences

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
import androidx.core.net.toFile
import com.tfuerholzer.darkmodewallpaper.getAppPrefs

class PreferenceManager (val context : Context){

    init {
        cleanupPrefs()
    }

    fun cleanupPrefs() {
        prefs.all.entries.parallelStream().forEach{ (value) ->

        }
    }

    private val prefs : SharedPreferences
        get() = context.getAppPrefs()

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

    companion object{
        private const val  KEY_PREFIX = "DARKMODE_WP_"
        private const val EMPTY_VALUE = "EMPTY_VALUE"
        fun generateKeyString(theme : Theme, aspectRatio: AspectRatio) : String{
            return "${KEY_PREFIX}_${theme.themeCode}_${aspectRatio.aspectRatioDataString}"
        }
    }


}