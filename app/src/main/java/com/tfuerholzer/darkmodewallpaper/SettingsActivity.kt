package com.tfuerholzer.darkmodewallpaper

import android.view.View

class SettingsActivity : MainActivity(){
    override fun initViews(){
        super.initViews()
        this.button.visibility = View.GONE
    }
}