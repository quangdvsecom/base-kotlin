package com.el.mybasekotlin.ui.gl_recorder

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import android.view.Surface
import java.nio.ByteBuffer

/**
 * This class wraps up the core components used for surface-input video encoding.
 */
class VideoEncoderCore(
    width: Int,
    height: Int,
    bitRate: Int,
    private val isMuxerStarted: () -> Boolean
) {
    private var mEncoder: MediaCodec? = null
    private var mInputSurface: Surface? = null
    private var mBufferInfo: MediaCodec.BufferInfo? = null
    private var mTrackIndex: Int = -1
    private var mOutputFormat: MediaFormat? = null
    private val mPendingBuffers = mutableListOf<PendingBuffer>()

    private data class PendingBuffer(
        val data: ByteBuffer,
        val info: MediaCodec.BufferInfo
    )

    init {
        mBufferInfo = MediaCodec.BufferInfo()
        
        // 🛡️ [FIX CRASH] Sử dụng createEncoderByType thay vì pick name thủ công.
        // Điều này giúp Android tự chọn Hardware Encoder tốt nhất và ổn định nhất.
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE)

        val format = MediaFormat.createVideoFormat(MIME_TYPE, width, height)
        format.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL)

        Log.d(TAG, "Configuring encoder with format: $format")
        
        mEncoder?.let { encoder ->
            try {
                encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
                mInputSurface = encoder.createInputSurface()
                encoder.start()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to configure encoder: ${e.message}. Format: $format")
                throw e
            }
        }
    }

    fun getInputSurface(): Surface? = mInputSurface

    fun getVideoTrackIndex(): Int = mTrackIndex

    fun release() {
        mEncoder?.stop()
        mEncoder?.release()
        mEncoder = null
        mInputSurface?.release()
        mInputSurface = null
        synchronized(mPendingBuffers) {
            mPendingBuffers.clear()
        }
    }

    fun drainEncoder(muxer: MediaMuxer, endOfStream: Boolean) {
        val encoder = mEncoder ?: return
        val bufferInfo = mBufferInfo ?: return

        if (endOfStream) {
            try {
                encoder.signalEndOfInputStream()
            } catch (e: IllegalStateException) {
                Log.w(TAG, "signalEndOfInputStream failed: ${e.message}. Encoder might be already released.")
            }
        }

        while (true) {
            val encoderStatus = encoder.dequeueOutputBuffer(bufferInfo, 10000)
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!endOfStream) break
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (mTrackIndex >= 0) {
                    Log.e(TAG, "❌ ERROR: Format changed twice! TrackIndex was already $mTrackIndex")
                    throw RuntimeException("format changed twice")
                }
                mOutputFormat = encoder.outputFormat
                mOutputFormat?.let {
                    mTrackIndex = muxer.addTrack(it)
                }
            } else if (encoderStatus >= 0) {
                val encodedData = encoder.getOutputBuffer(encoderStatus)
                    ?: throw RuntimeException("encoderOutputBuffer $encoderStatus was null")

                if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    bufferInfo.size = 0
                }

                if (bufferInfo.size != 0) {
                    val isKeyFrame = (bufferInfo.flags and MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0
                    if (isMuxerStarted()) {
                        // Flush pending buffers first
                        synchronized(mPendingBuffers) {
                            if (mPendingBuffers.isNotEmpty()) {
                                for (pending in mPendingBuffers) {
                                    muxer.writeSampleData(mTrackIndex, pending.data, pending.info)
                                }
                                mPendingBuffers.clear()
                            }
                        }

                        encodedData.position(bufferInfo.offset)
                        encodedData.limit(bufferInfo.offset + bufferInfo.size)
                        muxer.writeSampleData(mTrackIndex, encodedData, bufferInfo)

                        if (isKeyFrame) {
                            Log.v(
                                TAG,
                                "Wrote keyframe: size=${bufferInfo.size}, pts=${bufferInfo.presentationTimeUs}us"
                            )
                        }
                    } else {
                        // Buffer the frame
                        val frameType = if (isKeyFrame) "KEYFRAME" else "P-FRAME"
                        val copy = ByteBuffer.allocateDirect(bufferInfo.size)
                        encodedData.position(bufferInfo.offset)
                        encodedData.limit(bufferInfo.offset + bufferInfo.size)
                        copy.put(encodedData)
                        copy.flip()

                        val infoCopy = MediaCodec.BufferInfo()
                        infoCopy.set(
                            0,
                            bufferInfo.size,
                            bufferInfo.presentationTimeUs,
                            bufferInfo.flags
                        )
                        synchronized(mPendingBuffers) {
                            mPendingBuffers.add(PendingBuffer(copy, infoCopy))
                        }
                    }
                } else {
                    Log.v(TAG, "Skipping empty buffer (size=0)")
                }

                encoder.releaseOutputBuffer(encoderStatus, false)
                if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) break
            }
        }
    }

    /**
     * Flush pending buffers to muxer (call this after muxer is started)
     */
    fun flushPendingBuffers(muxer: MediaMuxer) {
        if (mTrackIndex < 0) {
            Log.w(TAG, "Cannot flush pending buffers: track index not set")
            return
        }
        synchronized(mPendingBuffers) {
            if (mPendingBuffers.isNotEmpty()) {
                for (pending in mPendingBuffers) {
                    muxer.writeSampleData(mTrackIndex, pending.data, pending.info)
                }
                mPendingBuffers.clear()
            }
        }
    }

    companion object {
        private const val TAG = "VideoEncoderCore"
        private const val MIME_TYPE = "video/avc"
        private const val FRAME_RATE = 30
        private const val IFRAME_INTERVAL = 5
    }
}
