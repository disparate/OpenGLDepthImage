package com.kazarovets.opengldepthmap

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ProxyRenderer(var renderers: List<DepthMapRenderer>) : GLSurfaceView.Renderer {

    var current: Int = 0

    fun handleMove(x: Float, y: Float) {
        renderers[current].handleMove(x, y)
    }

    override fun onDrawFrame(gl: GL10?) {
        renderers[current].onDrawFrame(gl!!)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        renderers[current].onSurfaceChanged(gl!!, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        renderers.forEach { it.onSurfaceCreated(gl!!, config!!) }
    }
}