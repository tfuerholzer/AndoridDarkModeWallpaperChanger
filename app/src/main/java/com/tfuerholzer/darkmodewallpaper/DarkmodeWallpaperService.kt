package com.tfuerholzer.darkmodewallpaper

import android.content.res.Configuration
import android.graphics.*
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.util.DisplayMetrics
import android.view.SurfaceHolder
import androidx.core.net.toFile
import java.lang.Exception

fun singleColorBitmap(width: Int, height: Int, color: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()
    paint.color = color
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    return bitmap
}


class DarkmodeWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return DarkmodeWallpaperEngine()
    }

    inner class DarkmodeWallpaperEngine : WallpaperService.Engine() {

        private lateinit var holder: SurfaceHolder
        private var darkmodeBitmap: Bitmap? = null
        private var lightmodeBitmap: Bitmap? = null
        private var darkmodeLandscapeBitmap: Bitmap? = null
        private var lightmodeLandscapeBitmap: Bitmap? = null
        private var lastOrientation = 0
        private var hadNightmodeEnabled: Boolean = false
        private var lastRatio: Float = -1f
        private val runnable = Runnable { draw() }
        private val singleRun = Runnable { draw(false) }
        private val handler = Handler()
        private var firstDraw = true
        val DELAY = 2000L
        private val ratio: Float
            get() = {
                val metrics: DisplayMetrics = baseContext.resources.displayMetrics
                metrics.heightPixels.toFloat() / metrics.widthPixels.toFloat()
            }()
        private val ratioString get() = "1000 : " + (ratio * 1000).toInt()

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            if (surfaceHolder != null) {
                holder = surfaceHolder
            }
            hadNightmodeEnabled = isNightmodeEnabled
            lastOrientation = resources.configuration.orientation
            initBitmaps()
        }

        private fun initBitmaps() {
            val darkmodeUri = retrieveUri(DARKMODE_IMAGE_URI)
            val lightmodeUri = retrieveUri(LIGHTMODE_IMAGE_URI)
            if (darkmodeUri.exists()) {
                darkmodeBitmap = BitmapFactory.decodeStream(darkmodeUri!!.toFile().inputStream())
            }
            if (lightmodeUri.exists()) {
                lightmodeBitmap = BitmapFactory.decodeStream(lightmodeUri!!.toFile().inputStream())
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible || needsRedraw()) {
                handler.post(runnable)
            } else {
                handler.removeCallbacks(runnable)
            }
        }

        override fun onSurfaceRedrawNeeded(holder: SurfaceHolder?) {
            super.onSurfaceRedrawNeeded(holder)
            this.handler.post(singleRun)
        }

        fun draw(continueLoop: Boolean = true) {
            val needsRedraw = needsRedraw()
            if (needsRedraw) {
                firstDraw = false
                val canvas = surfaceHolder.lockCanvas()
                val isInLandscape = lastOrientation == Configuration.ORIENTATION_LANDSCAPE
                var bitmap = if (isNightmodeEnabled) darkmodeBitmap else lightmodeBitmap
                val color = if (isNightmodeEnabled) Color.BLACK else Color.BLUE
                if (bitmap == null) {
                    bitmap = singleColorBitmap(canvas.width, canvas.height, color)
                }
                if (isInLandscape) {
                    if (isNightmodeEnabled && darkmodeLandscapeBitmap != null) {
                        bitmap = darkmodeLandscapeBitmap
                    } else if (lightmodeLandscapeBitmap != null) {
                        bitmap = lightmodeLandscapeBitmap
                    }
                }
                canvas.drawBitmap(bitmap!!, null, surfaceHolder.surfaceFrame, Paint(color))
                canvas.save()
                if (isNightmodeEnabled && darkmodeLandscapeBitmap == null && isInLandscape) {
                    darkmodeLandscapeBitmap = bitmap
                }
                if (!isNightmodeEnabled && lightmodeLandscapeBitmap == null && isInLandscape) {
                    lightmodeLandscapeBitmap = bitmap
                }
                try {
                    surfaceHolder.unlockCanvasAndPost(canvas)
                } catch (ignored: Exception) {
                }
            }
            handler.removeCallbacks(runnable)
            if (continueLoop) {
                handler.postDelayed(runnable, DELAY)
            }
        }

        private fun needsRedraw(): Boolean {
            var returnValue = false

            if (isNightmodeEnabled != hadNightmodeEnabled) {
                returnValue = true
                hadNightmodeEnabled = isNightmodeEnabled

            }
            if (lastOrientation != resources.configuration.orientation) {
                returnValue = true
                lastOrientation = resources.configuration.orientation
            }
            if (ratio != lastRatio) {
                returnValue = true;
                lastRatio = ratio

            }

            return returnValue || firstDraw
        }
    }
}