package com.kazarovets.opengldepthmap


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLUtils

object TextureUtils {

    fun loadTexture(context: Context, resourceId: Int, glTextureInt: Int): Int {
        // создание объекта текстуры
        val textureIds = IntArray(1)
        glGenTextures(1, textureIds, 0)
        if (textureIds[0] == 0) {
            return 0
        }

        // получение Bitmap
        val options = BitmapFactory.Options()
        options.inScaled = false

        val bitmap = BitmapFactory.decodeResource(
            context.resources, resourceId, options
        )

        if (bitmap == null) {
            glDeleteTextures(1, textureIds, 0)
            return 0
        }

        // настройка объекта текстуры
        glActiveTexture(glTextureInt)
        glBindTexture(GL_TEXTURE_2D, textureIds[0])

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)


        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        bitmap.recycle()

        // сброс target
        glBindTexture(GL_TEXTURE_2D, 0)

        return textureIds[0]
    }
}