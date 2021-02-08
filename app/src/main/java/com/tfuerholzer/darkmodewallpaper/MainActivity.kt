package com.tfuerholzer.darkmodewallpaper

import android.app.WallpaperManager
import android.app.WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER
import android.app.WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toFile
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import android.Manifest.permission.READ_EXTERNAL_STORAGE as READ_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE as WRITE_STORAGE
import android.content.pm.PackageManager.PERMISSION_GRANTED as GRANTED

const val SELECT_IMAGE_DARKMODE = 1
const val SELECT_IMAGE_LIGHTMODE = 2
const val LIGHTMODE_IMAGE_URI: String = "LIGHTMODE_IMAGE_URI"
const val DARKMODE_IMAGE_URI: String = "DARKMODE_IMAGE_URI"

open class MainActivity : AppCompatActivity() {

    protected lateinit var darkmodeWallpaperImageview: ImageView
    protected lateinit var lightmodeWallpaperImageview: ImageView
    protected lateinit var button: Button
    protected val writeGranted
        get() =
            checkSelfPermission(WRITE_STORAGE) == GRANTED
    protected val readGranted
        get() =
            checkSelfPermission(READ_STORAGE) == GRANTED
    protected val prefs get() = getAppPrefs()
    protected val ratio: Float
        get() = {
            val metrics: DisplayMetrics = baseContext.resources.displayMetrics
            metrics.heightPixels.toFloat() / metrics.widthPixels.toFloat()
        }()
    protected val ratioString get() = "1000 : " + (ratio * 1000).toInt()
    protected lateinit var wallpaperManager : WallpaperManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cleanPrefs()
        initViews()
        initVariables()
        checkAndGetPermissions()
    }

    protected fun cleanPrefs() {
        val editor = prefs.edit()
        if (!retrieveUri(DARKMODE_IMAGE_URI).exists()) {
            editor.remove(DARKMODE_IMAGE_URI)
        }
        if (!retrieveUri(LIGHTMODE_IMAGE_URI).exists()) {
            editor.remove(LIGHTMODE_IMAGE_URI)
        }
        editor.commit()
    }

    protected fun checkAndGetPermissions() {
        if (!this.writeGranted || shouldShowRequestPermissionRationale(WRITE_STORAGE)) {
            requestPermissions(arrayOf(WRITE_STORAGE), 0)
        }
        if (!this.readGranted || shouldShowRequestPermissionRationale(READ_STORAGE)) {
            requestPermissions(arrayOf(READ_STORAGE), 0)
        }
    }

    protected fun initVariables() {
        wallpaperManager = WallpaperManager.getInstance(baseContext)
    }

    protected open fun initViews() {
        darkmodeWallpaperImageview = findViewById(R.id.wallpaperDark)
        lightmodeWallpaperImageview = findViewById(R.id.wallpaperLight)
        button = findViewById(R.id.setWallpaperButton)
        button.setOnClickListener(this::handleButtonClick)
        with(darkmodeWallpaperImageview) {
            (layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = ratioString
            setOnClickListener {
                handleImageviewOnclick(
                    it,
                    SELECT_IMAGE_DARKMODE
                )
            }
            with(lightmodeWallpaperImageview) {
                (layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = ratioString
                setOnClickListener {
                    handleImageviewOnclick(
                        it,
                        SELECT_IMAGE_LIGHTMODE
                    )
                }

            }
            updateImages()
        }
    }

    protected fun updateImages(which: ImageView? = null) {
        val lightmodeUri = retrieveUri(LIGHTMODE_IMAGE_URI)
        val darkmodeUri = retrieveUri(DARKMODE_IMAGE_URI)
        when (which) {
            lightmodeWallpaperImageview -> setImageURI(lightmodeUri, lightmodeWallpaperImageview)
            darkmodeWallpaperImageview -> setImageURI(darkmodeUri, darkmodeWallpaperImageview)
            else -> {
                setImageURI(darkmodeUri, darkmodeWallpaperImageview)
                setImageURI(lightmodeUri, lightmodeWallpaperImageview)
            }
        }
    }

    protected fun setImageURI(uri: Uri?, imageview: ImageView) {
        imageview.setImageURI(uri)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode < 10 && data != null) {
            val uri: Uri = data.data as Uri
            startActivityForResult(generateCropIntent(uri), requestCode + 10)
        } else if (data != null) {
            val uri = CropImage.getActivityResult(data).uri
            if (uri != null) {
                val uriString = uri.toString()
                //scaleSaveAndShow(uri)
                val key =
                    if (requestCode == 10 + SELECT_IMAGE_LIGHTMODE) LIGHTMODE_IMAGE_URI else DARKMODE_IMAGE_URI
                purgeURI(key)
                val editor = prefs.edit()
                editor.putString(key, uriString)
                editor.commit()
                updateImages()
            }

        }
    }

    private fun purgeURI(key: String): Boolean {
        val uri = retrieveUri(key)
        var retval = false
        if(uri.exists()){
           retval =  uri!!.toFile().delete()
        }
        return retval
    }


    protected fun handleImageviewOnclick(view: View, code: Int): Boolean {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select your wallpaper"), code)
        return true
    }

    protected fun generateCropIntent(imageUri: Uri): Intent {
        val activity = CropImage.activity(imageUri)
        activity.setCropShape(CropImageView.CropShape.RECTANGLE)
        activity.setFixAspectRatio(true)
        activity.setAspectRatio(1000, (1000 * ratio).toInt())
        activity.setInitialCropWindowPaddingRatio(0f)
        return activity.getIntent(baseContext)
    }

    protected fun handleButtonClick(button : View){
        val intent = Intent()
        intent.action = WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER
        val packagename = DarkmodeWallpaperService::class.java.`package`!!.name
        val classname = DarkmodeWallpaperService::class.java.canonicalName!!
        val component = ComponentName(packagename,classname)
        intent.putExtra(EXTRA_LIVE_WALLPAPER_COMPONENT,component)
        startActivity(intent)
    }


}