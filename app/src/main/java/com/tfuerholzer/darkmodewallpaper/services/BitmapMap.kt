package com.tfuerholzer.darkmodewallpaper.services

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.tfuerholzer.darkmodewallpaper.R
import com.tfuerholzer.darkmodewallpaper.preferences.AspectRatio
import com.tfuerholzer.darkmodewallpaper.preferences.Theme
import java.util.concurrent.ConcurrentHashMap

class BitmapMap : ConcurrentHashMap<Pair<Theme, AspectRatio?>, Bitmap>(){


    fun get(theme : Theme, ar : AspectRatio): Bitmap? {
        val pair = Pair(theme,ar)
        val value = get(pair)
        return get(pair)
    }

    fun get(theme : Theme) : Bitmap? {
        val res = entries.parallelStream()
            .filter { it.key.first == theme }
            .findFirst().orElse(null)
        return res.value
    }

    fun put(theme: Theme, aspectRatio: AspectRatio?, bitmap: Bitmap){
    }


    companion object{
        fun singleColorBitmap(width: Int, height: Int, color: Int = R.color.amoledBlack): Bitmap {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val paint = Paint()
            paint.color = color
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            return bitmap
        }

    }

}