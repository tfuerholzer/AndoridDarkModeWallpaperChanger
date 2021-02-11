package com.tfuerholzer.darkmodewallpaper

import android.app.WallpaperManager
import android.app.WallpaperManager.*
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.tfuerholzer.darkmodewallpaper.fragments.SelectImageFragment
import com.tfuerholzer.darkmodewallpaper.preferences.AspectRatio
import com.tfuerholzer.darkmodewallpaper.preferences.Theme.DARKMODE
import com.tfuerholzer.darkmodewallpaper.preferences.Theme.LIGHTMODE
import java.util.*
import android.Manifest.permission.READ_EXTERNAL_STORAGE as READ_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE as WRITE_STORAGE
import android.content.pm.PackageManager.PERMISSION_GRANTED as GRANTED
import com.tfuerholzer.darkmodewallpaper.services.DarkmodeWallpaperService as WallpaperService

open class MainActivity : AppCompatActivity() {

    protected lateinit var button: Button
    protected lateinit var selectImageFragment: SelectImageFragment

    protected val writeGranted
        get() =
            checkSelfPermission(WRITE_STORAGE) == GRANTED
    protected val readGranted
        get() =
            checkSelfPermission(READ_STORAGE) == GRANTED

    protected val systemSettingsGranted
        get() = Settings.System.canWrite(applicationContext) //checkSelfPermission(ACTION_MANAGE_WRITE_SETTINGS)  == GRANTED

    protected lateinit var wallpaperManager: WallpaperManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getInstance(baseContext)
        setContentView(R.layout.activity_main)
        forcePortraitMode()
        initViews()
        checkAndGetPermissions()
    }


    protected fun forcePortraitMode() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR
    }

    protected fun checkAndGetPermissions() {
        if (!this.writeGranted && shouldShowRequestPermissionRationale(WRITE_STORAGE)) {
            requestPermissions(arrayOf(WRITE_STORAGE), 0)
        }
        if (!this.readGranted && shouldShowRequestPermissionRationale(READ_STORAGE)) {
            requestPermissions(arrayOf(READ_STORAGE), 0)
        }
        if (!this.systemSettingsGranted && Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1){
            showManageSettingsScreen()
        }
    }


    protected open fun initViews() {
        button = findViewById(R.id.setWallpaperButton)
        selectImageFragment = (supportFragmentManager.findFragmentById(R.id.selectImageFrag) as SelectImageFragment?)!!
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        selectImageFragment.changeAspectRatio(AspectRatio(displayMetrics))
        button.setOnLongClickListener(this::handleLongButtonClick)
        button.setOnClickListener(this::handleButtonClick)
    }


    protected fun handleButtonClick(button : View){
        val intent = Intent()
        intent.action = ACTION_CHANGE_LIVE_WALLPAPER
        val packagename = packageName
        val classname = WallpaperService::class.java.canonicalName!!
        val component = ComponentName(packagename,classname)
        intent.putExtra(EXTRA_LIVE_WALLPAPER_COMPONENT,component)
        startActivity(intent)
    }

    protected fun handleLongButtonClick(button : View?): Boolean{
        if (systemSettingsGranted){
            val themeCode = if (currentTheme == DARKMODE) LIGHTMODE.themeCode else DARKMODE.themeCode
            val config = Configuration()
            Settings.System.getConfiguration(contentResolver,config)
            if (config != null){
                config.uiMode = themeCode
                Settings.System.putConfiguration(contentResolver, config)
            }
        }
        return true;
    }

    protected fun showManageSettingsScreen(){
        val intent = Intent()
        intent.action = Settings.ACTION_MANAGE_WRITE_SETTINGS
        val packagename = "com.tfuerholzer.darkmodewallpaper"
        intent.data = Uri.parse("package:"+packagename)
        startActivity(intent)
    }

}