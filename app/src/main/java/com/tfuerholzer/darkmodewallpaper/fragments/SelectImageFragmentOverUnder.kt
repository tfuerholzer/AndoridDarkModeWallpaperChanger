package com.tfuerholzer.darkmodewallpaper.fragments

import android.app.WallpaperManager
import android.os.Bundle
import android.view.View
import com.tfuerholzer.darkmodewallpaper.R
import com.tfuerholzer.darkmodewallpaper.preferences.AspectRatio

class SelectImageFragmentOverUnder : SelectImageFragment(R.layout.select_image_fragment_over_under){

    override fun initElements(){
        val view = this.view
        if (view != null){
            darkmodeImage = view.findViewById(R.id.wallpaperDarkSw)
            lightmodeImage = view.findViewById(R.id.wallpaperLightSw)
        }
        val wallManager = WallpaperManager.getInstance(requireContext())
        aspectRatio = AspectRatio(wallManager.desiredMinimumHeight,wallManager.desiredMinimumWidth)
    }
}