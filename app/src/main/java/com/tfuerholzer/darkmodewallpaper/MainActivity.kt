package com.tfuerholzer.darkmodewallpaper

import android.app.WallpaperManager
import android.app.WallpaperManager.*
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.tfuerholzer.darkmodewallpaper.fragments.SelectImageFragment
import com.tfuerholzer.darkmodewallpaper.preferences.AspectRatio
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
        if (!this.writeGranted || shouldShowRequestPermissionRationale(WRITE_STORAGE)) {
            requestPermissions(arrayOf(WRITE_STORAGE), 0)
        }
        if (!this.readGranted || shouldShowRequestPermissionRationale(READ_STORAGE)) {
            requestPermissions(arrayOf(READ_STORAGE), 0)
        }
    }


    protected open fun initViews() {
        button = findViewById(R.id.setWallpaperButton)
        selectImageFragment = (supportFragmentManager.findFragmentById(R.id.selectImageFrag) as SelectImageFragment?)!!
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        selectImageFragment.changeAspectRatio(AspectRatio(displayMetrics))
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


}