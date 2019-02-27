package com.kazarovets.opengldepthmap


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView.Renderer
import android.opengl.GLUtils
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.roundToInt


class DepthMapRenderer(private val context: Context, val original: Int, val depth: Int) : Renderer {

    private var vertexData: FloatBuffer? = null


    private var aPositionLocation: Int = 0
    private var depthImageLocation: Int = 0
    private var originalImageLocation: Int = 0
    private var thresholdLocation: Int = 0
    private var mouseLocation: Int = 0
    private var resolutionLocation: Int = 0

    private var programId: Int = 0


    private var width: Float = 0f
    private var height: Float = 0f

    private val horizontalThreshold = 20
    private val verticalThreshold = 25

    var mouseX: Float = 0f
    var mouseY: Float = 0f

    var mouseTargetX = 0f
    var mouseTargetY = 0f

    private val textureIds = IntArray(2)

    override fun onSurfaceCreated(arg0: GL10, arg1: EGLConfig) {
        glClearColor(0f, 0f, 0f, 1f)
        glEnable(GL_DEPTH_TEST)

        createAndUseProgram()
        getLocations()
        prepareData()
        bindTextures(original, depth)
        bindData()
    }

    override fun onSurfaceChanged(arg0: GL10, width: Int, height: Int) {
        this.width = width.toFloat()
        this.height = height.toFloat()

        changeSize(width, height)
    }

    private fun changeSize(width: Int, height: Int) {
        glUniform2f(resolutionLocation, width.toFloat(), height.toFloat())
        glUniform2f(thresholdLocation, horizontalThreshold.toFloat(), verticalThreshold.toFloat())
        glViewport(0, 0, (width), (height))
    }

    private fun prepareData() {
        val vertices = floatArrayOf(
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
        )

        vertexData = ByteBuffer
            .allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()


        vertexData!!.put(vertices)
    }

    private fun createAndUseProgram() {
        val vertexShaderId = ShaderUtils.createShader(context, GL_VERTEX_SHADER, R.raw.vertex_shader_depth)
        val fragmentShaderId = ShaderUtils.createShader(context, GL_FRAGMENT_SHADER, R.raw.fragment_shader_depth)
        programId = ShaderUtils.createProgram(vertexShaderId, fragmentShaderId)
        glUseProgram(programId)
    }

    private fun getLocations() {
        aPositionLocation = glGetAttribLocation(programId, "position")

        resolutionLocation = glGetUniformLocation(programId, "resolution")
        mouseLocation = glGetUniformLocation(programId, "mouse")
        thresholdLocation = glGetUniformLocation(programId, "threshold")
        depthImageLocation = glGetUniformLocation(programId, "depthImage")
        originalImageLocation = glGetUniformLocation(programId, "originalImage")
    }

    private fun bindData() {
        vertexData!!.position(0)
        glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 0, vertexData)
        glEnableVertexAttribArray(aPositionLocation)
    }

    fun bindTextures(original: Int, depth: Int) {
        val images = listOf<Int>(original, depth)

        // получение Bitmap
        val options = BitmapFactory.Options()
        options.inScaled = false

        glGenTextures(1, textureIds, 0)
        for (i in 0 until images.size) {

            val bitmap = BitmapFactory.decodeResource(
                context.resources, images[i], options
            )

            val metrics = context.resources.displayMetrics
            val aspectRatio = metrics.widthPixels.toFloat() / metrics.heightPixels
            val imageAspectRatio = bitmap.width.toFloat() / bitmap.height

            val oldWidth = bitmap.width
            val widthMult = Math.min(1f, aspectRatio / imageAspectRatio)
            val newWidth = bitmap.width * widthMult

            val oldHeight = bitmap.height
            val heightMult = Math.min(1f, imageAspectRatio / aspectRatio)
            val newHeight = bitmap.height * heightMult

            val cropped = Bitmap.createBitmap(
                bitmap,
                (oldWidth - newWidth.roundToInt()) / 2,
                (oldHeight - newHeight.roundToInt()) / 2,
                newWidth.roundToInt(),
                newHeight.roundToInt()
            )

            glBindTexture(GL_TEXTURE_2D, textureIds[i])

            // Set the parameters so we can render any size image.
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, cropped, 0)
            // Upload the image into the texture.
            glFlush()

            bitmap?.recycle()
            cropped?.recycle()

        }

        // set which texture units to render with.
        glUniform1i(originalImageLocation, 0) // texture unit 0
        glUniform1i(depthImageLocation, 1) // texture unit 1

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureIds[0])
        glActiveTexture(GL_TEXTURE1)
        glBindTexture(GL_TEXTURE_2D, textureIds[1])

    }

    fun handleMove(x: Float, y: Float) {
        val halfX = width / 2
        val halfY = height / 2

        if(halfX < 1f || halfY < 1f) return

        mouseTargetX = (halfX - x) / halfX
        mouseTargetY = (halfY - y) / halfY
    }

    override fun onDrawFrame(arg0: GL10) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        this.mouseX += (this.mouseTargetX - this.mouseX) * 0.05f
        this.mouseY += (this.mouseTargetY - this.mouseY) * 0.05f

        glUniform2f(mouseLocation, mouseX, mouseY)

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
    }
}