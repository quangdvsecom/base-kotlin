package com.el.mybasekotlin.ui.gl_recorder

import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLExt
import android.opengl.EGLSurface
import android.util.Log
import android.view.Surface

/**
 * Core EGL state (display, context, config).
 *
 * The EGLContext must only be attached to one thread at a time.  This class is not thread-safe.
 */
class EglCore(sharedContext: EGLContext? = null, flags: Int = 0) {

    private var mEGLDisplay: EGLDisplay = EGL14.EGL_NO_DISPLAY
    private var mEGLContext: EGLContext = EGL14.EGL_NO_CONTEXT
    private var mEGLConfig: EGLConfig? = null

    init {
        var sharedCtx = sharedContext
        if (mEGLDisplay !== EGL14.EGL_NO_DISPLAY) {
            throw RuntimeException("EGL already set up")
        }

        if (sharedCtx == null) {
            sharedCtx = EGL14.EGL_NO_CONTEXT
        } else {
        }

        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        if (mEGLDisplay === EGL14.EGL_NO_DISPLAY) {
            val error = EGL14.eglGetError()
            Log.e(TAG, "Unable to get EGL14 display, error: 0x${Integer.toHexString(error)}")
            throw RuntimeException("unable to get EGL14 display")
        }

        val version = IntArray(2)
        if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
            val error = EGL14.eglGetError()
            Log.e(TAG, "Unable to initialize EGL14, error: 0x${Integer.toHexString(error)}")
            mEGLDisplay = EGL14.EGL_NO_DISPLAY
            throw RuntimeException("unable to initialize EGL14")
        }

        // Try to get a config that matches what we want.
        val configAttribs = intArrayOf(
            EGL14.EGL_RED_SIZE, 8,
            EGL14.EGL_GREEN_SIZE, 8,
            EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_ALPHA_SIZE, 8,
            EGL14.EGL_DEPTH_SIZE, 16,
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
            EGL14.EGL_NONE, 0, // placeholder for FLAG_RECORDABLE
            EGL14.EGL_NONE
        )

        if ((flags and FLAG_RECORDABLE) != 0) {
            configAttribs[configAttribs.size - 3] = EGL_RECORDABLE_ANDROID
            configAttribs[configAttribs.size - 2] = 1
        }

        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)
        if (!EGL14.eglChooseConfig(
                mEGLDisplay, configAttribs, 0, configs, 0, configs.size,
                numConfigs, 0
            )
        ) {
            val error = EGL14.eglGetError()
            Log.e(TAG, "eglChooseConfig failed, error: 0x${Integer.toHexString(error)}")
            throw RuntimeException("unable to find RGB8888 / $version EGLConfig")
        }
        val config = configs[0]

        // Configure context attributes.
        val contextAttribs = intArrayOf(
            EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL14.EGL_NONE
        )
        val context = EGL14.eglCreateContext(
            mEGLDisplay, config, sharedCtx, contextAttribs, 0
        )
        checkEglError("eglCreateContext")

        if (context == EGL14.EGL_NO_CONTEXT) {
            val error = EGL14.eglGetError()
            Log.e(
                TAG,
                "eglCreateContext returned EGL_NO_CONTEXT, error: 0x${Integer.toHexString(error)}"
            )
            throw RuntimeException("eglCreateContext failed")
        }

        mEGLConfig = config
        mEGLContext = context
    }

    fun release() {
        if (mEGLDisplay !== EGL14.EGL_NO_DISPLAY) {
            EGL14.eglMakeCurrent(
                mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                EGL14.EGL_NO_CONTEXT
            )
            EGL14.eglDestroyContext(mEGLDisplay, mEGLContext)
            EGL14.eglReleaseThread()
            EGL14.eglTerminate(mEGLDisplay)
        }

        mEGLDisplay = EGL14.EGL_NO_DISPLAY
        mEGLContext = EGL14.EGL_NO_CONTEXT
        mEGLConfig = null
    }

    fun createWindowSurface(surface: Any): EGLSurface {
        if (surface !is Surface && surface !is SurfaceTexture) {
            Log.e(TAG, "invalid surface: $surface")
            return EGL14.EGL_NO_SURFACE
        }

        // 🛡️ Kiểm tra tính hợp lệ của Surface trước khi tạo (trên Android)
        if (surface is Surface && !surface.isValid) {
            Log.w(TAG, "createWindowSurface: Surface is invalid, skipping.")
            return EGL14.EGL_NO_SURFACE
        }

        val surfaceAttribs = intArrayOf(
            EGL14.EGL_NONE
        )
        val eglSurface = EGL14.eglCreateWindowSurface(
            mEGLDisplay, mEGLConfig, surface,
            surfaceAttribs, 0
        )
        
        // 🛡️ [AN TOÀN] Không crash app nếu lỗi 0x3003 (thường do surface đang bị hủy)
        val error = EGL14.eglGetError()
        if (error != EGL14.EGL_SUCCESS) {
            val errorHex = "0x${Integer.toHexString(error)}"
            Log.w(TAG, "eglCreateWindowSurface failed (EGL error: $errorHex). Skipping.")
            return EGL14.EGL_NO_SURFACE
        }

        if (eglSurface == null || eglSurface == EGL14.EGL_NO_SURFACE) {
            Log.w(TAG, "eglCreateWindowSurface returned null or EGL_NO_SURFACE")
            return EGL14.EGL_NO_SURFACE
        }
        return eglSurface
    }

    fun getDisplay(): EGLDisplay {
        return mEGLDisplay
    }

    fun makeCurrent(eglSurface: EGLSurface) {
        if (mEGLDisplay === EGL14.EGL_NO_DISPLAY) {
            throw RuntimeException("EGL display is invalid")
        }
        val result = EGL14.eglMakeCurrent(mEGLDisplay, eglSurface, eglSurface, mEGLContext)
        if (!result) {
            val error = EGL14.eglGetError()
            throw RuntimeException("eglMakeCurrent failed: error=0x${Integer.toHexString(error)}")
        }
    }

    fun swapBuffers(eglSurface: EGLSurface): Boolean {
        return EGL14.eglSwapBuffers(mEGLDisplay, eglSurface)
    }

    fun setPresentationTime(eglSurface: EGLSurface, nsecs: Long) {
        EGLExt.eglPresentationTimeANDROID(mEGLDisplay, eglSurface, nsecs)
    }

    fun releaseSurface(eglSurface: EGLSurface) {
        EGL14.eglDestroySurface(mEGLDisplay, eglSurface)
    }

    private fun checkEglError(msg: String) {
        val error = EGL14.eglGetError()
        if (error != EGL14.EGL_SUCCESS) {
            throw RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error))
        }
    }

    companion object {
        private const val TAG = "EglCore"
        const val FLAG_RECORDABLE = 0x01
        private const val EGL_RECORDABLE_ANDROID = 0x3142
    }
}
