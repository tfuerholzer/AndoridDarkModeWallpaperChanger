package com.tfuerholzer.darkmodewallpaper.fragments

import android.app.WallpaperManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.tfuerholzer.darkmodewallpaper.R
import com.tfuerholzer.darkmodewallpaper.ShortcutCreator
import com.tfuerholzer.darkmodewallpaper.preferences.AspectRatio
import com.tfuerholzer.darkmodewallpaper.preferences.PreferenceManager
import com.tfuerholzer.darkmodewallpaper.preferences.Theme.DARKMODE
import com.tfuerholzer.darkmodewallpaper.preferences.Theme.LIGHTMODE
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

open class SelectImageFragment(layoutID: Int = R.layout.select_image_fragment) : Fragment(layoutID) {

    lateinit var darkmodeImage : ImageView
    lateinit var lightmodeImage : ImageView
    protected lateinit var preferenceManager: PreferenceManager
    protected var overrideShortcuts : Boolean = true
    var aspectRatio: AspectRatio = AspectRatio(1,2)
        protected set


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.preferenceManager = PreferenceManager(requireContext())
        this.initElements();
        this.initEventHandler();
    }

    protected open fun initElements() {
        val view = this.view
        if (view != null){
            darkmodeImage = view.findViewById(R.id.wallpaperDark)
            lightmodeImage = view.findViewById(R.id.wallpaperLight)
        }
        val wallManager = WallpaperManager.getInstance(requireContext())
        aspectRatio = AspectRatio(wallManager.desiredMinimumHeight,wallManager.desiredMinimumWidth)
    }

    private fun initEventHandler() {
        darkmodeImage.setOnClickListener{handleImageviewOnclick(it, DARKMODE.themeCode)}
        lightmodeImage.setOnClickListener{handleImageviewOnclick(it, LIGHTMODE.themeCode)}
    }

    protected fun handleImageviewOnclick(view: View, code: Int): Boolean {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select your wallpaper"), code)
        return true
    }

    protected fun updateImages(which: ImageView? = null) {
        val lightmodeUri = preferenceManager.getURI(LIGHTMODE,aspectRatio)
        val darkmodeUri = preferenceManager.getURI(DARKMODE,aspectRatio)
        when (which) {
            lightmodeImage -> updateImage(which, lightmodeUri)
            darkmodeImage -> updateImage(which, darkmodeUri)
            else -> {
                updateImage(darkmodeImage, darkmodeUri)
                updateImage(lightmodeImage, lightmodeUri)
            }
        }
    }

    private fun updateImage(imageview: ImageView, imageUri: Uri?) {
        imageview.setImageURI(imageUri)
        if (overrideShortcuts){
            val dynamic = imageUri != null
            val theme = if(imageview == darkmodeImage) DARKMODE else LIGHTMODE
            ShortcutCreator.updateShortcut(requireContext(), imageview, theme, dynamic)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode < 100 && data != null) {
            val uri: Uri = data.data as Uri
            startActivityForResult(generateCropIntent(uri), requestCode + 100)
        } else if (data != null) {
            val uri = CropImage.getActivityResult(data).uri
            if (uri != null) {
                val uriString = uri.toString()
                val theme = if(requestCode == 100 + LIGHTMODE.themeCode) LIGHTMODE else DARKMODE
                preferenceManager.put(theme, aspectRatio,uriString)
                updateImages(if (theme == LIGHTMODE) lightmodeImage else darkmodeImage)
            }
        }
    }

    protected fun generateCropIntent(imageUri: Uri): Intent {
        val activity = CropImage.activity(imageUri)
        activity.setCropShape(CropImageView.CropShape.RECTANGLE)
        activity.setFixAspectRatio(true)
        activity.setAspectRatio(aspectRatio.screenWidth, aspectRatio.screenHeight)
        activity.setInitialCropWindowPaddingRatio(0f)
        return activity.getIntent(requireContext())
    }

    fun changeAspectRatio(aspectRatio: AspectRatio) {
        this.aspectRatio = aspectRatio;
        listOf(darkmodeImage,lightmodeImage).forEach {
            val params = it.layoutParams as ConstraintLayout.LayoutParams
            params.dimensionRatio = aspectRatio.aspectRatioString
        }
        this.updateImages();
    }

}

