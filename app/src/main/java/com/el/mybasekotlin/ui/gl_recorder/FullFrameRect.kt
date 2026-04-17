package com.el.mybasekotlin.ui.gl_recorder

import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Utility class for drawing a full-screen quad with a texture.
 * Supports both sampler2D and samplerExternalOES.
 */
class FullFrameRect() {
    private val vertexShaderCode = """
        attribute vec4 aPosition;
        attribute vec4 aTextureCoord;
        uniform mat4 uTexMatrix;
        varying vec2 vTextureCoord;
        void main() {
            gl_Position = aPosition;
            vTextureCoord = (uTexMatrix * aTextureCoord).xy;
        }
    """.trimIndent()

    private val fragmentShaderCode2D = """
        precision mediump float;
        varying vec2 vTextureCoord;
        uniform sampler2D sTexture;
        void main() {
            gl_FragColor = texture2D(sTexture, vTextureCoord);
        }
    """.trimIndent()

    private val fragmentShaderCodeOES = """
        #extension GL_OES_EGL_image_external : require
        precision mediump float;
        varying vec2 vTextureCoord;
        uniform samplerExternalOES sTexture;
        void main() {
            gl_FragColor = texture2D(sTexture, vTextureCoord);
        }
    """.trimIndent()

    private var mProgram2D = 0
    private var mProgramOES = 0
    private var maPositionLoc = 0
    private var maTextureCoordLoc = 0
    private var muSamplerLoc = 0
    private var muTexMatrixLoc = 0

    private val vertexBuffer: FloatBuffer
    private val texCoordBuffer: FloatBuffer

    private val squareCoords = floatArrayOf(
        -1.0f, -1.0f,
        1.0f, -1.0f,
        -1.0f, 1.0f,
        1.0f, 1.0f
    )

    private val texCoords = floatArrayOf(
        0.0f, 0.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f
    )

    init {
        vertexBuffer = ByteBuffer.allocateDirect(squareCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply { put(squareCoords); position(0) }

        texCoordBuffer = ByteBuffer.allocateDirect(texCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply { put(texCoords); position(0) }

        mProgram2D = createProgram(vertexShaderCode, fragmentShaderCode2D)
        mProgramOES = createProgram(vertexShaderCode, fragmentShaderCodeOES)
    }

    private fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        return program
    }

    private fun loadShader(shaderType: Int, source: String): Int {
        val shader = GLES20.glCreateShader(shaderType)
        GLES20.glShaderSource(shader, source)
        GLES20.glCompileShader(shader)
        return shader
    }

    fun draw(textureId: Int, isOES: Boolean, texMatrix: FloatArray? = null) {
        val program = if (isOES) mProgramOES else mProgram2D
        GLES20.glUseProgram(program)

        maPositionLoc = GLES20.glGetAttribLocation(program, "aPosition")
        maTextureCoordLoc = GLES20.glGetAttribLocation(program, "aTextureCoord")
        muSamplerLoc = GLES20.glGetUniformLocation(program, "sTexture")
        muTexMatrixLoc = GLES20.glGetUniformLocation(program, "uTexMatrix")

        GLES20.glEnableVertexAttribArray(maPositionLoc)
        GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 8, vertexBuffer)

        GLES20.glEnableVertexAttribArray(maTextureCoordLoc)
        GLES20.glVertexAttribPointer(
            maTextureCoordLoc,
            2,
            GLES20.GL_FLOAT,
            false,
            8,
            texCoordBuffer
        )

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(
            if (isOES) GLES11Ext.GL_TEXTURE_EXTERNAL_OES else GLES20.GL_TEXTURE_2D,
            textureId
        )
        GLES20.glUniform1i(muSamplerLoc, 0)

        val matrix =
            texMatrix ?: FloatArray(16).apply { Matrix.setIdentityM(this, 0) }
        GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, matrix, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(maPositionLoc)
        GLES20.glDisableVertexAttribArray(maTextureCoordLoc)
        GLES20.glBindTexture(
            if (isOES) GLES11Ext.GL_TEXTURE_EXTERNAL_OES else GLES20.GL_TEXTURE_2D,
            0
        )
        GLES20.glUseProgram(0)
    }

    fun release() {
        GLES20.glDeleteProgram(mProgram2D)
        GLES20.glDeleteProgram(mProgramOES)
    }
}
