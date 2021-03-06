package com.kazarovets.opengldepthmap

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kazarovets.opengldepthmap.wallpaper.DepthWallpaper
import kotlinx.android.synthetic.main.activity_depth.*

class OpenGlActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_depth)

        if (!supportES2()) {
            Toast.makeText(this, "OpenGL ES 2.0 is not supported", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val id = intent.getIntExtra("id", 0)

        val (originalImage, mapImage) = ResourcesRepository.getOriginalAndDepth(id)

        glSurfaceView.setEGLContextClientVersion(2)
        val renderer = DepthMapRenderer(this)
        renderer.updateImages(originalImage, mapImage)
        glSurfaceView.setRenderer(renderer)

        glSurfaceView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event == null) return false

                renderer.handleMove(event.x, event.y)

                return true
            }

        })

        setButton.setOnClickListener {
                        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
            intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this, DepthWallpaper::class.java)
            )
            startActivity(intent)

        }
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView!!.onPause()
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView!!.onResume()
    }

    private fun supportES2(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        return configurationInfo.reqGlEsVersion >= 0x20000
    }


    companion object {
        fun getIntent(context: Context, id: Int): Intent {
            return Intent(context, OpenGlActivity::class.java).putExtra("id", id)
        }
    }
}