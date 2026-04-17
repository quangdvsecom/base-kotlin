package com.el.mybasekotlin.ui.gl_recorder

import android.graphics.SurfaceTexture
import android.media.MediaMuxer
import android.opengl.EGL14
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.Matrix
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import java.io.File

class GLRecorder(
    private val width: Int,
    private val height: Int
) : SurfaceTexture.OnFrameAvailableListener {

    private var mEglCore: EglCore? = null
    private var mDisplaySurface: WindowSurface? = null
    private var mEncoderSurface: WindowSurface? = null
    private var mVideoEncoder: VideoEncoderCore? = null
    private var mAudioEncoder: AudioEncoderCore? = null
    private var mMuxer: MediaMuxer? = null
    private var mIsRecording = false

    fun isRecording(): Boolean = mIsRecording
    private var mIsMuxerStarted = false
    private var mVideoTrackIndex = -1
    private var mAudioTrackIndex = -1
    private var mAudioSource: AudioSource? = null
    private var mRecordingStartTimeNs: Long = 0

    private var mCameraTextureId = 0
    private var mCameraSurfaceTexture: SurfaceTexture? = null
    private var mCameraSurface: Surface? = null

    private var mSceneTextureId = 0
    private var mSceneSurfaceTexture: SurfaceTexture? = null
    private var mSceneSurface: Surface? = null

    private var mOverlayTextureId = 0
    private var mOverlaySurfaceTexture: SurfaceTexture? = null
    private var mOverlaySurface: Surface? = null


    private var mFullFrameRect: FullFrameRect? = null

    private var mHandlerThread: HandlerThread? = null
    private var mHandler: Handler? = null
    private var mIsReleased = false

    private var mCameraWidth = 0
    private var mCameraHeight = 0
    private val mTmpMatrix = FloatArray(16)
    private val mFinalMatrix = FloatArray(16)
    private val mCropMatrix = FloatArray(16)

    fun start(previewSurface: Surface, onReady: (() -> Unit)? = null) {
        mHandlerThread = HandlerThread("GLRecorder").apply { start() }
        mHandler = Handler(mHandlerThread?.looper ?: return)

        mHandler?.post {
            val eglCore = EglCore(null, EglCore.FLAG_RECORDABLE)
            mEglCore = eglCore
            mDisplaySurface = WindowSurface(eglCore, previewSurface, false)
            mDisplaySurface?.makeCurrent()

            mFullFrameRect = FullFrameRect()

            // Setup Camera texture
            mCameraTextureId = createTextureOES()
            mCameraSurfaceTexture = SurfaceTexture(mCameraTextureId)

            // 🔧 FIX BLUR: Force camera to render at screen resolution
            // This makes CAMERA DRIVER do the upscaling (better quality)
            // instead of OpenGL texture sampling (causes blur)
            mCameraSurfaceTexture?.setDefaultBufferSize(width, height)

            mCameraSurfaceTexture?.setOnFrameAvailableListener(this)
            mCameraSurface = Surface(mCameraSurfaceTexture)

            // Setup SceneView Mirror texture
            mSceneTextureId = createTextureOES()
            mSceneSurfaceTexture = SurfaceTexture(mSceneTextureId)
            mSceneSurfaceTexture?.setDefaultBufferSize(width, height)
            mSceneSurfaceTexture?.setOnFrameAvailableListener(this)
            mSceneSurface = Surface(mSceneSurfaceTexture)

            // Setup Overlay texture
            mOverlayTextureId = createTextureOES()
            mOverlaySurfaceTexture = SurfaceTexture(mOverlayTextureId)
            mOverlaySurfaceTexture?.setDefaultBufferSize(width, height)
            mOverlaySurfaceTexture?.setOnFrameAvailableListener(this)
            mOverlaySurface = Surface(mOverlaySurfaceTexture)

            onReady?.invoke()
        }
    }

    fun getCameraSurface(): Surface? = mCameraSurface
    fun getSceneSurface(): Surface? = mSceneSurface
    fun getOverlaySurface(): Surface? = mOverlaySurface
    fun setCameraResolution(width: Int, height: Int) {
        val oldWidth = mCameraWidth
        val oldHeight = mCameraHeight

        mCameraWidth = width
        mCameraHeight = height

        if (oldWidth != width || oldHeight != height) {

            val viewAspect = this.width.toFloat() / this.height
            val cameraAspect = width.toFloat() / height

            val scaleRatioW = this.width.toFloat() / width
            val scaleRatioH = this.height.toFloat() / height

            if (scaleRatioW > 1.0f || scaleRatioH > 1.0f) {
                Log.w(
                    "GLRecorder",
                    "⚠️ UPSCALING REQUIRED! Camera ${width}x${height} < Preview ${this.width}x${this.height}"
                )
            }
        }
    }

    fun setAudioSource(source: AudioSource?) {
        mAudioSource = source
    }

    private var mOnMuxerReady: (() -> Unit)? = null

    fun startRecording(outputFile: File, onMuxerReady: (() -> Unit)? = null) {
        mHandler?.post {
            if (mIsRecording) {
                Log.w("GLRecorder", "startRecording called but already recording")
                return@post
            }

            mOnMuxerReady = onMuxerReady


            try {
                // 🛡️ [FIX CRASH 0xfffffc0e] Giới hạn kích thước Video ghi hình
                // Hầu hết Hardware Codec chỉ hỗ trợ chiều cao tối đa là 1920.
                // Chiều cao 2205 quá cao sẽ gây crash MediaCodec.configure().
                var finalWidth = width
                var finalHeight = height

                val MAX_DIM = 1920
                if (finalHeight > MAX_DIM) {
                    val scale = MAX_DIM.toFloat() / finalHeight
                    finalHeight = MAX_DIM
                    finalWidth = (finalWidth * scale).toInt()
                }

                // Đảm bảo chia hết cho 16 để bộ mã hóa hoạt động ổn định nhất
                val recordWidth = (finalWidth / 16) * 16
                val recordHeight = (finalHeight / 16) * 16

                Log.d(
                    "GLRecorder",
                    "startRecording: Screen=${width}x${height} -> Video=${recordWidth}x${recordHeight}"
                )

                mMuxer =
                    MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

                val eglCore = mEglCore ?: return@post
                mVideoEncoder = VideoEncoderCore(
                    recordWidth, recordHeight, 6_000_000,
                    isMuxerStarted = { mIsMuxerStarted }
                )

                val videoEncoder = mVideoEncoder ?: return@post
                val inputSurface = videoEncoder.getInputSurface() ?: return@post
                mEncoderSurface = WindowSurface(eglCore, inputSurface, true)

                // Reset state
                mIsMuxerStarted = false
                mVideoTrackIndex = -1
                mAudioTrackIndex = -1

                // Setup audio encoder if needed
                mAudioSource?.let { source ->
                    val muxer = mMuxer ?: return@let
                    mAudioEncoder = AudioEncoderCore(
                        muxer = muxer,
                        audioSource = source,
                        onTrackAdded = { index ->
                            mAudioTrackIndex = index
                            checkAndStartMuxer()
                        },
                        isMuxerStarted = { mIsMuxerStarted }
                    )
                } ?: run {
                }

                mIsRecording = true
                mRecordingStartTimeNs = System.nanoTime()

                // Start audio recording if audio encoder exists
                mAudioEncoder?.let {
                    it.startRecording(mRecordingStartTimeNs)
                }

            } catch (e: Exception) {
                Log.e("GLRecorder", "❌ FATAL: Failed to start recording", e)
                releaseEncoder()
                mIsRecording = false
                throw e
            }
        }
    }

    fun stopRecording(onStop: (() -> Unit)? = null) {
        mHandler?.post {
            stopRecordingInternal()
            onStop?.invoke()
        }
    }

    private fun stopRecordingInternal() {
        if (!mIsRecording) {
            Log.w("GLRecorder", "stopRecordingInternal called but not recording")
            return
        }

        mIsRecording = false

        // Stop audio encoder first
        mAudioEncoder?.stopRecording()

        // Drain video encoder (this will signal end of stream)
        val videoEncoder = mVideoEncoder
        val muxer = mMuxer
        if (videoEncoder != null && muxer != null) {
            videoEncoder.drainEncoder(muxer, true)

            // CRITICAL: Update video track index after drain (it may have been added during drain)
            val newVideoTrackIndex = videoEncoder.getVideoTrackIndex()
            if (newVideoTrackIndex >= 0) {
                if (mVideoTrackIndex < 0) {
                    mVideoTrackIndex = newVideoTrackIndex
                } else if (mVideoTrackIndex != newVideoTrackIndex) {
                    Log.w(
                        "GLRecorder",
                        "⚠️ Video track index mismatch: stored=$mVideoTrackIndex, encoder=$newVideoTrackIndex, using encoder value"
                    )
                    mVideoTrackIndex = newVideoTrackIndex
                }
            } else {
                Log.w(
                    "GLRecorder",
                    "⚠️ Video track index still -1 after drain, track may not have been added"
                )
            }
        } else {
            Log.w(
                "GLRecorder",
                "Cannot drain video encoder: videoEncoder=${mVideoEncoder != null}, muxer=${mMuxer != null}"
            )
        }

        // CRITICAL FIX: Check and start muxer AFTER draining (tracks may be added during drain)
        // This is especially important for short recordings where tracks are added late
        if (mMuxer != null && !mIsMuxerStarted) {
            // Re-check if we can start muxer now (tracks may have been added during drain)
            val hasAudio = mAudioSource != null
            val videoTrackReady = mVideoTrackIndex >= 0
            val audioTrackReady = if (hasAudio) mAudioTrackIndex >= 0 else true


            if (videoTrackReady && audioTrackReady) {
                try {
                    mMuxer?.start()
                    mIsMuxerStarted = true

                    // Notify that muxer is ready (late start case)
                    mOnMuxerReady?.let { callback ->
                        callback()
                        mOnMuxerReady = null
                    }

                    // CRITICAL: Flush any pending video buffers that were buffered before muxer started
                    val videoEnc = mVideoEncoder
                    val muxerVal = mMuxer
                    if (videoEnc != null && mVideoTrackIndex >= 0 && muxerVal != null) {
                        videoEnc.flushPendingBuffers(muxerVal)
                    }
                } catch (e: Exception) {
                    Log.e("GLRecorder", "Failed to start muxer: ${e.message}", e)
                }
            } else {
                Log.e(
                    "GLRecorder",
                    "⚠️ Cannot start muxer: videoReady=$videoTrackReady (index=$mVideoTrackIndex), audioReady=$audioTrackReady (index=$mAudioTrackIndex)"
                )
            }
        }

        // Validate muxer state before release
        if (mMuxer != null) {
            if (!mIsMuxerStarted) {
                Log.e("GLRecorder", "⚠️ WARNING: Muxer was never started! File may be invalid")
            } else if (mVideoTrackIndex < 0) {
                Log.e(
                    "GLRecorder",
                    "⚠️ WARNING: Video track was never added! File may be invalid"
                )
            }
        }

        releaseEncoder()
    }

    private fun checkAndStartMuxer() {
        val muxer = mMuxer ?: return
        synchronized(muxer) {
            if (mIsMuxerStarted) return

            val hasAudio = mAudioSource != null
            val videoTrackReady = mVideoTrackIndex >= 0
            val audioTrackReady = if (hasAudio) mAudioTrackIndex >= 0 else true


            if (videoTrackReady && audioTrackReady) {
                try {
                    mMuxer?.start()
                    mIsMuxerStarted = true

                    // Notify that muxer is ready
                    mOnMuxerReady?.let { callback ->
                        callback()
                        mOnMuxerReady = null // Clear callback after calling
                    }
                } catch (e: Exception) {
                    Log.e("GLRecorder", "Failed to start MediaMuxer: ${e.message}", e)
                }
            } else {
            }
        }
    }

    private fun releaseEncoder() {

        // Release audio encoder
        if (mAudioEncoder != null) {
            mAudioEncoder?.release()
            mAudioEncoder = null
        }

        // Release video encoder
        if (mVideoEncoder != null) {
            mVideoEncoder?.release()
            mVideoEncoder = null
        }

        // Release encoder surface
        if (mEncoderSurface != null) {
            mEncoderSurface?.release()
            mEncoderSurface = null
        }

        // Release muxer (MUST be last, after all encoders are released)
        if (mMuxer != null) {
            try {
                if (mIsMuxerStarted) {
                    mMuxer?.stop()
                } else {
                    Log.w("GLRecorder", "⚠️ MediaMuxer was never started - file may be invalid")
                }
                mMuxer?.release()
            } catch (e: Exception) {
                Log.e("GLRecorder", "❌ Error releasing muxer: ${e.message}", e)
            }
            mMuxer = null
        } else {
            Log.w("GLRecorder", "Muxer was null, nothing to release")
        }

        // Reset state
        mIsMuxerStarted = false
        mVideoTrackIndex = -1
        mAudioTrackIndex = -1
    }

    private var mCameraFrameAvailable = false
    private var mSceneFrameAvailable = false
    private var mOverlayFrameAvailable = false

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        mHandler?.post {
            if (mIsReleased) {
                Log.v("GLRecorder", "onFrameAvailable: ignored (released)")
                return@post
            }
            if (surfaceTexture == mCameraSurfaceTexture) {
//                Log.v("GLRecorder", "onFrameAvailable: CAMERA")
            } else if (surfaceTexture == mSceneSurfaceTexture) {
//                Log.v("GLRecorder", "onFrameAvailable: SCENE")
            } else {
                Log.v("GLRecorder", "onFrameAvailable: UNKNOWN surface=$surfaceTexture")
            }
            if (surfaceTexture == mCameraSurfaceTexture) {
                mCameraFrameAvailable = true
            } else if (surfaceTexture == mSceneSurfaceTexture) {
                mSceneFrameAvailable = true
            } else if (surfaceTexture == mOverlaySurfaceTexture) {
                mOverlayFrameAvailable = true
            }
            draw()
        }
    }

    private fun draw() {
        if (mIsReleased || mEglCore == null || mDisplaySurface == null) {
            return
        }

        // 1. Chuẩn bị Surface để vẽ lên màn hình Preview
        try {
            mDisplaySurface?.makeCurrent()
        } catch (e: RuntimeException) {
            Log.w("GLRecorder", "draw() failed: surface invalid, stopping")
            mIsReleased = true
            if (mIsRecording) stopRecording()
            return
        }

        // 2. Cập nhật các Texture nếu có Frame mới
        try {
            if (mCameraFrameAvailable) {
                mCameraSurfaceTexture?.updateTexImage()
                mCameraFrameAvailable = false
            }
            if (mSceneFrameAvailable) {
                mSceneSurfaceTexture?.updateTexImage()
                mSceneFrameAvailable = false
            }
            if (mOverlayFrameAvailable) {
                mOverlaySurfaceTexture?.updateTexImage()
                mOverlayFrameAvailable = false
            }
        } catch (e: Exception) {
            Log.e("GLRecorder", "Error updateTexImage: ${e.message}")
        }

        // 3. Thiết lập Viewport và Clear nền
        GLES20.glViewport(0, 0, width, height)
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // --- BẮT ĐẦU VẼ CÁC LỚP ---

        // LỚP 1: Camera (Nền - Background)
        mCameraSurfaceTexture?.getTransformMatrix(mTmpMatrix)
        // Tính toán Crop Matrix (Giữ nguyên logic Center Crop của bạn)
        Matrix.setIdentityM(mCropMatrix, 0)
        if (mCameraWidth > 0 && mCameraHeight > 0) {
            val viewAspect = width.toFloat() / height
            val cameraAspect = mCameraWidth.toFloat() / mCameraHeight
            var scaleX = 1f
            var scaleY = 1f
            if (viewAspect > cameraAspect) {
                scaleY = cameraAspect / viewAspect
            } else {
                scaleX = viewAspect / cameraAspect
            }
            Matrix.translateM(mCropMatrix, 0, (1f - scaleX) / 2f, (1f - scaleY) / 2f, 0f)
            Matrix.scaleM(mCropMatrix, 0, scaleX, scaleY, 1f)
            Matrix.multiplyMM(mFinalMatrix, 0, mTmpMatrix, 0, mCropMatrix, 0)
        } else {
            System.arraycopy(mTmpMatrix, 0, mFinalMatrix, 0, 16)
        }
        mFullFrameRect?.draw(mCameraTextureId, true, mFinalMatrix)

        // Bật chế độ Trộn (Alpha Blending) cho các lớp phía trên
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        // LỚP 2: SceneView (Con nhện/3D)
        mSceneSurfaceTexture?.getTransformMatrix(mTmpMatrix)
        mFullFrameRect?.draw(mSceneTextureId, true, mTmpMatrix)

        // LỚP 3: Overlay (Custom View/UI)
        mOverlaySurfaceTexture?.getTransformMatrix(mTmpMatrix)
        mFullFrameRect?.draw(mOverlayTextureId, true, mTmpMatrix)

        // Tắt Blending sau khi vẽ xong
        GLES20.glDisable(GLES20.GL_BLEND)

        // Swap buffers để hiển thị lên màn hình (recordingPreview)
        mDisplaySurface?.let {
            if (it.eglSurface != EGL14.EGL_NO_SURFACE) {
                it.swapBuffers()
            }
        }

        // 4. XỬ LÝ GHI HÌNH (RECORDING)
        if (mIsRecording && mEncoderSurface != null && !mIsReleased) {
            try {
                val encoderSurface = mEncoderSurface ?: return
                encoderSurface.makeCurrent()
                GLES20.glViewport(0, 0, encoderSurface.width, encoderSurface.height)

                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

                // Vẽ lại 3 lớp y hệt cho Encoder Surface
                // Vẽ Camera
                mFullFrameRect?.draw(mCameraTextureId, true, mFinalMatrix)

                // Vẽ Scene & Overlay với Blending
                GLES20.glEnable(GLES20.GL_BLEND)
                GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

                mSceneSurfaceTexture?.getTransformMatrix(mTmpMatrix)
                mFullFrameRect?.draw(mSceneTextureId, true, mTmpMatrix)

                mOverlaySurfaceTexture?.getTransformMatrix(mTmpMatrix)
                mFullFrameRect?.draw(mOverlayTextureId, true, mTmpMatrix)

                GLES20.glDisable(GLES20.GL_BLEND)

                // Gửi frame đã trộn vào Video Encoder
                val ptsNs = System.nanoTime() - mRecordingStartTimeNs
                encoderSurface.setPresentationTime(ptsNs)
                encoderSurface.swapBuffers()

                // Đẩy dữ liệu vào Muxer
                val videoEnc = mVideoEncoder
                val muxerVal = mMuxer
                if (videoEnc != null && muxerVal != null) {
                    videoEnc.drainEncoder(muxerVal, false)
                }

                // Kiểm tra Track
                if (mVideoTrackIndex < 0) {
                    val newTrackIndex = mVideoEncoder?.getVideoTrackIndex() ?: -1
                    if (newTrackIndex >= 0) {
                        mVideoTrackIndex = newTrackIndex
                        checkAndStartMuxer()
                    }
                }
            } catch (e: Exception) {
                Log.e("GLRecorder", "Error during recording draw: ${e.message}")
            }
        }
    }

    private fun createTextureOES(): Int {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0])

        // 🎨 TEXTURE FILTERING - Back to GL_LINEAR
        // Since camera driver now upscales to screen resolution,
        // GL_LINEAR provides smoother rendering without blur
        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )

        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )

        return textures[0]
    }


    fun release() {
        mHandler?.post {
            mIsReleased = true
            if (mIsRecording) {
                stopRecordingInternal()
            }
            mCameraSurface?.release()
            mCameraSurfaceTexture?.release()
            mSceneSurface?.release()
            mSceneSurfaceTexture?.release()
            mFullFrameRect?.release()
            mDisplaySurface?.release()
            mEglCore?.release()
            mHandlerThread?.quitSafely()
        }
    }
}
