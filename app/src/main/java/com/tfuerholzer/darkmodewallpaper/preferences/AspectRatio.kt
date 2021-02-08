package com.tfuerholzer.darkmodewallpaper.preferences

import android.content.pm.ActivityInfo
import android.content.pm.ActivityInfo.*
import android.hardware.display.DisplayManager
import android.util.DisplayMetrics
import android.view.Display
import com.tfuerholzer.darkmodewallpaper.math.gcd

data class AspectRatio(val screenHeight: Int, val screenWidth: Int) {

    constructor(display: DisplayMetrics) :
            this (screenHeight = display.heightPixels, screenWidth = display.widthPixels)

    val heightToWidthRatio: Float
        get() = screenHeight.toFloat() / screenWidth.toFloat()

    val widthToHeightRatio: Float
        get() = screenWidth.toFloat() / screenHeight.toFloat()

    val aspectRatioString: String
        get() = generateAspectRatioString(":")

    val aspectRatioDataString: String
        get() = generateAspectRatioString("-")

    val orientation: Int
        get() = orientation()

    private fun generateAspectRatioString(seperationChar: String): String {
        val gcd = gcd(screenHeight, screenWidth)
        return "" + (screenWidth / gcd) + seperationChar + (screenHeight / gcd)
    }

    private fun orientation(): Int {
        return if (screenHeight > screenWidth) SCREEN_ORIENTATION_PORTRAIT else if (screenHeight < screenWidth) SCREEN_ORIENTATION_LANDSCAPE else -1
    }

    fun inverted() : AspectRatio = AspectRatio(screenHeight = screenWidth, screenWidth = screenHeight)
}