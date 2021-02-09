package com.tfuerholzer.darkmodewallpaper.services

import android.content.res.Configuration
import android.graphics.Paint
import android.os.Build
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.tfuerholzer.darkmodewallpaper.BuildConfig
import com.tfuerholzer.darkmodewallpaper.R
import com.tfuerholzer.darkmodewallpaper.currentTheme
import com.tfuerholzer.darkmodewallpaper.preferences.AspectRatio
import com.tfuerholzer.darkmodewallpaper.preferences.Theme

class DarkmodeWallpaperService : WallpaperService() {

    private var configChangedCallback: (Configuration) -> Unit = {}

    override fun onCreateEngine(): DarkModeWallpaperEngine {
        return DarkModeWallpaperEngine()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configChangedCallback(newConfig)
    }


    inner class DarkModeWallpaperEngine : WallpaperService.Engine() {
        private lateinit var cache: BitmapCache
        private var holder: SurfaceHolder? = null

        private lateinit var lastTheme: Theme

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            cache = BitmapCache(baseContext)
            Thread {
                cache.initFoundBitmaps(baseContext)
            }.start()
            lastTheme = currentTheme
            this.holder = surfaceHolder

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                configChangedCallback = this::onConfigChanged
            }
        }

        fun onConfigChanged(config: Configuration) {
            if (lastTheme != currentTheme) {
                lastTheme = currentTheme
                onSurfaceRedrawNeeded(holder)
            }
        }

        override fun onSurfaceRedrawNeeded(holder: SurfaceHolder?) {
            super.onSurfaceRedrawNeeded(holder)
            this.holder = holder
            draw(holder)
        }

        private fun draw(holder: SurfaceHolder?, async: Boolean = true) {
            val job = Thread {
                if (holder != null && holder.surfaceFrame != null) {
                    val ar = AspectRatio(surfaceHolder.surfaceFrame)
                    val bitmap = cache.get(baseContext, baseContext.currentTheme, ar)
                    val canvas = holder.lockHardwareCanvas()
                    canvas.drawBitmap(
                        bitmap!!,
                        null,
                        surfaceHolder.surfaceFrame,
                        Paint(R.color.amoledBlack)
                    )
                    canvas.save()
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (ignored: Exception) {
                    }
                }
            }
            if (async){
                job.start()
            }else{
                job.run()
            }
        }
    }
}