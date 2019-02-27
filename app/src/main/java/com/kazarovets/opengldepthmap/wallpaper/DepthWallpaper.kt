package com.kazarovets.opengldepthmap.wallpaper

import com.kazarovets.opengldepthmap.DepthMapRenderer
import android.view.SurfaceHolder
import android.view.MotionEvent
import com.kazarovets.opengldepthmap.ResourcesRepository


class DepthWallpaper : GLWallpaperService() {

    override fun onCreateEngine(): Engine {
        return DepthEngine()
    }

    inner class DepthEngine : GLWallpaperService.GLEngine() {

        private var renderer: DepthMapRenderer? = null

        var width: Int = 0
        var height: Int = 0

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)


            // Add touch events
            setTouchEventsEnabled(true)

            setEGLContextClientVersion(2)
            val (originalImage, mapImage) = ResourcesRepository.getOriginalAndDepth(3)
            renderer = DepthMapRenderer(this@DepthWallpaper).apply {
                updateImages(originalImage, mapImage)
            }
            setRenderer(renderer)
            renderMode = RENDERMODE_WHEN_DIRTY
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            this.width = width
            this.height = height
        }


        override fun onDestroy() {

            // Kill renderer
            renderer = null

            setTouchEventsEnabled(false)

            super.onDestroy()
        }

        override fun onOffsetsChanged(
            xOffset: Float,
            yOffset: Float,
            xOffsetStep: Float,
            yOffsetStep: Float,
            xPixelOffset: Int,
            yPixelOffset: Int
        ) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset)
            renderer?.handleMove(xOffset * width, yOffset * height)
            requestRender()
        }
    }
}