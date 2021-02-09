package com.tfuerholzer.darkmodewallpaper.services

import android.graphics.Bitmap
import android.graphics.Paint
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.tfuerholzer.darkmodewallpaper.R
import com.tfuerholzer.darkmodewallpaper.currentTheme
import com.tfuerholzer.darkmodewallpaper.preferences.AspectRatio
import com.tfuerholzer.darkmodewallpaper.preferences.Theme
import java.lang.Exception

class DarkmodeWallpaperService : WallpaperService() {

    override fun onCreateEngine(): DarkModeWallpaperEngine {
        return DarkModeWallpaperEngine()
    }

    inner class DarkModeWallpaperEngine : Engine(){
        private lateinit var cache : BitmapCache

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            cache = BitmapCache(baseContext)
            Thread{
                cache.initFoundBitmaps(baseContext)
            }.start()
        }

        override fun onSurfaceRedrawNeeded(holder: SurfaceHolder?) {
            super.onSurfaceRedrawNeeded(holder)
            if (holder != null && holder.surfaceFrame != null){
                val ar = AspectRatio(surfaceHolder.surfaceFrame)
                val bitmap = cache.get(baseContext, baseContext.currentTheme,ar)
                val canvas = holder.lockHardwareCanvas()
                canvas.drawBitmap(bitmap!!, null, surfaceHolder.surfaceFrame, Paint(R.color.amoledBlack))
                canvas.save()
                try {
                    surfaceHolder.unlockCanvasAndPost(canvas)
                } catch (ignored: Exception) {
                }
            }
        }
    }
}