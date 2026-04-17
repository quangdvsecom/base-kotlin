package com.el.mybasekotlin.ui.gl_recorder

import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.util.Log
import android.view.Surface

/**
 * Recordable EGL window surface.
 *
 * It's good practice to explicitly release() the surface, rather than rely on the GC.
 */
class WindowSurface(
    private val mEglCore: EglCore,
    surface: Any,
    releaseSurface: Boolean
) {
    private var mEGLSurface: EGLSurface = EGL14.EGL_NO_SURFACE
    val eglSurface: EGLSurface
        get() = mEGLSurface

    private var mSurface: Surface? = null
    private var mReleaseSurface = false
    private var mEglDisplay: EGLDisplay? = null

    /**
     * Returns the width of the surface.
     */
    val width: Int
        get() {
            if (mEGLSurface == EGL14.EGL_NO_SURFACE || mEglDisplay == null) {
                return -1
            }
            val value = IntArray(1)
            mEglDisplay?.let {
                EGL14.eglQuerySurface(it, mEGLSurface, EGL14.EGL_WIDTH, value, 0)
            }
            return value[0]
        }

    /**
     * Returns the height of the surface.
     */
    val height: Int
        get() {
            if (mEGLSurface == EGL14.EGL_NO_SURFACE || mEglDisplay == null) {
                return -1
            }
            val value = IntArray(1)
            mEglDisplay?.let {
                EGL14.eglQuerySurface(it, mEGLSurface, EGL14.EGL_HEIGHT, value, 0)
            }
            return value[0]
        }

    init {
        mEGLSurface = mEglCore.createWindowSurface(surface)
        mEglDisplay = mEglCore.getDisplay()

        if (surface is Surface) {
            mSurface = surface
        }
        mReleaseSurface = releaseSurface
    }

    constructor(eglCore: EglCore, surface: Surface) : this(eglCore, surface, true)
    constructor(eglCore: EglCore, surfaceTexture: SurfaceTexture) : this(
        eglCore,
        surfaceTexture,
        true
    )

    /**
     * Discards the EGL surface.
     */
    fun release() {
        mEglCore.releaseSurface(mEGLSurface)
        mEGLSurface = EGL14.EGL_NO_SURFACE
        if (mReleaseSurface) {
            mSurface?.release()
        }
        mSurface = null
    }

    /**
     * Makes our EGL context and surface current.
     */
    fun makeCurrent() {
        if (mEGLSurface == EGL14.EGL_NO_SURFACE) {
            Log.w("WindowSurface", "makeCurrent: surface is EGL_NO_SURFACE, skipping.")
            return
        }
        try {
            mEglCore.makeCurrent(mEGLSurface)
            val display = EGL14.eglGetCurrentDisplay()
            if (display != EGL14.EGL_NO_DISPLAY) {
                mEglDisplay = display
            }
        } catch (e: Exception) {
            Log.e("WindowSurface", "makeCurrent failed: ${e.message}")
        }
    }

    /**
     * Calls eglSwapBuffers.  Use this to "publish" the current frame.
     */
    fun swapBuffers(): Boolean {
        if (mEGLSurface == EGL14.EGL_NO_SURFACE) {
            return false
        }
        return mEglCore.swapBuffers(mEGLSurface)
    }

    /**
     * Sends the presentation time stamp to EGL.
     *
     * @param nsecs Timestamp, in nanoseconds.
     */
    fun setPresentationTime(nsecs: Long) {
        mEglCore.setPresentationTime(mEGLSurface, nsecs)
    }
}
