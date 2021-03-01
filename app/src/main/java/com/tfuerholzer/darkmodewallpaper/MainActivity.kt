package com.tfuerholzer.darkmodewallpaper

import android.app.WallpaperManager
import android.app.WallpaperManager.*
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import com.tfuerholzer.darkmodewallpaper.fragments.SelectImageFragment
import com.tfuerholzer.darkmodewallpaper.fragments.SelectImageFragmentOverUnder
import com.tfuerholzer.darkmodewallpaper.preferences.AspectRatio
import com.tfuerholzer.darkmodewallpaper.preferences.PreferenceManager
import com.tfuerholzer.darkmodewallpaper.preferences.Theme
import com.tfuerholzer.darkmodewallpaper.preferences.Theme.DARKMODE
import com.tfuerholzer.darkmodewallpaper.preferences.Theme.LIGHTMODE
import kotlinx.android.synthetic.main.activity_main.*
import android.Manifest.permission.READ_EXTERNAL_STORAGE as READ_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE as WRITE_STORAGE
import android.content.pm.PackageManager.PERMISSION_GRANTED as GRANTED
import com.tfuerholzer.darkmodewallpaper.services.DarkmodeWallpaperService as WallpaperService


/**
 * Hauptacitivity,
 */
open class MainActivity : AppCompatActivity() {

    protected lateinit var button: Button
    protected lateinit var selectImageFragment: SelectImageFragment
    protected lateinit var selectImageFragmentOverUnder: SelectImageFragmentOverUnder
    protected lateinit var landscapeCheckBox: CheckBox
    protected lateinit var preferenceManager: PreferenceManager
    protected lateinit var group : Group
    protected lateinit var layout : ConstraintLayout

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
        checkIfLaunchedFromShortcut()
        initCheckboxValue()
    }

    protected fun checkIfLaunchedFromShortcut() {
        val extras = intent.extras
        if (extras != null){
            val themeCode = extras.getInt(ShortcutCreator.SHORTCUT_THEME)
            if (themeCode > 0){
                val theme = Theme.fromCode(themeCode)
                val imageview = if (theme == LIGHTMODE)
                    selectImageFragment.lightmodeImage
                else
                    selectImageFragment.darkmodeImage
                imageview.callOnClick()
            }
        }
    }


    protected fun forcePortraitMode() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR
    }

    protected fun checkAndGetPermissions() {
        if (!this.writeGranted && shouldShowRequestPermissionRationale(WRITE_STORAGE)) {
            requestPermissions(arrayOf(WRITE_STORAGE), 0)
        }
        if (!this.readGranted && shouldShowRequestPermissionRationale(READ_STORAGE)) {
            requestPermissions(arrayOf(READ_STORAGE),0)
        }

    }


    protected open fun initViews() {
        this.preferenceManager = PreferenceManager(baseContext)
        layout = findViewById(R.id.mainConstraintLayout)
        button = findViewById(R.id.setWallpaperButton)
        landscapeCheckBox = findViewById(R.id.landscapeCheckBox)
        selectImageFragment = (supportFragmentManager.findFragmentById(R.id.selectImageFrag) as SelectImageFragment?)!!
        selectImageFragmentOverUnder = (supportFragmentManager.findFragmentById(R.id.selectImageOverUnderFrag) as SelectImageFragmentOverUnder?)!!
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        val aspectRatio = AspectRatio(displayMetrics)
        selectImageFragment.changeAspectRatio(aspectRatio)
        selectImageFragmentOverUnder.changeAspectRatio(aspectRatio.inverted())
        button.setOnClickListener(this::handleButtonClick)
        landscapeCheckBox.setOnClickListener { handleCheckboxClick(it) }
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
        return true;
    }

    protected fun showManageSettingsScreen(){
        val intent = Intent()
        intent.action = Settings.ACTION_MANAGE_WRITE_SETTINGS
        val packagename = "com.tfuerholzer.darkmodewallpaper"
        intent.data = Uri.parse("package:"+packagename)
        startActivity(intent)
    }

    protected fun handleCheckboxClick(checkboxView : View) : Boolean{
        val checkbox = checkboxView as CheckBox
        preferenceManager.singleScreenMode = !checkbox.isChecked
        changeVisibility(checkbox.isChecked)
        return true
    }


    protected fun initCheckboxValue(){
        val shouldBeVisible = preferenceManager.singleScreenMode
        landscapeCheckBox.isChecked = shouldBeVisible
        changeVisibility(shouldBeVisible)
    }

    protected fun changeVisibility(isVisible : Boolean){
        TransitionManager.beginDelayedTransition(layout,AutoTransition())
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        selectImageFragmentOverUnder.view?.visibility = visibility
    }
}