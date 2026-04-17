package com.el.mybasekotlin.ui.gl_recorder

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import java.nio.ByteBuffer

/**
 * Audio encoder core for encoding audio from an AudioSource.
 */
class AudioEncoderCore(
    private val muxer: MediaMuxer,
    private val audioSource: AudioSource,
    private val bitrate: Int = AUDIO_BITRATE,
    private val mimeType: String = AUDIO_MIME_TYPE,
    private val onTrackAdded: (Int) -> Unit,
    private val isMuxerStarted: () -> Boolean
) {
    private var mEncoder: MediaCodec? = null
    private val mPendingBuffers = mutableListOf<PendingBuffer>()

    private var mBufferInfo: MediaCodec.BufferInfo? = null
    private var mTrackIndex: Int = -1
    private var mIsRecording: Boolean = false
    private var mRecordingThread: Thread? = null

    // Timestamp tracking
    private var mStartTimeNs: Long = 0
    private var mSamplesWritten: Long = 0

    // Audio format from source
    private var mSampleRate: Int = 0
    private var mChannelCount: Int = 0

    // Leftover PCM data
    private var mLeftoverPcm: ByteBuffer? = null
    private var mLeftoverPcmTimestampUs: Long = -1

    private data class PendingBuffer(
        val data: ByteBuffer,
        val info: MediaCodec.BufferInfo
    )

    init {
        mBufferInfo = MediaCodec.BufferInfo()
        mSampleRate = audioSource.getSampleRate()
        mChannelCount = audioSource.getChannelCount()
        setupEncoder()
    }

    private fun setupEncoder() {
        try {
            val audioFormat = MediaFormat.createAudioFormat(mimeType, mSampleRate, mChannelCount)
            audioFormat.setInteger(
                MediaFormat.KEY_AAC_PROFILE,
                MediaCodecInfo.CodecProfileLevel.AACObjectLC
            )
            audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate)
            audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 16384)

            val encoderName =
                selectAudioCodec(mimeType) ?: throw RuntimeException("No encoder for $mimeType")
            val encoder = MediaCodec.createByCodecName(encoderName)
            encoder.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            mEncoder = encoder
        } catch (e: Exception) {
            Log.e(TAG, "Failed to setup audio encoder: ${e.message}", e)
            throw RuntimeException(e)
        }
    }

    fun startRecording(startTimeNs: Long) {
        if (mIsRecording) {
            Log.w(TAG, "startRecording called but already recording")
            return
        }
        mStartTimeNs = startTimeNs
        mSamplesWritten = 0

        synchronized(mPendingBuffers) { mPendingBuffers.clear() }

        try {
            audioSource.start()
            mEncoder?.start()
            mIsRecording = true

            val thread = Thread({ recordAudio() }, "AudioEncoderThread")
            mRecordingThread = thread
            thread.start()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting audio recording: ${e.message}", e)
            mIsRecording = false
        }
    }

    fun stopRecording() {
        if (!mIsRecording) return
        mIsRecording = false
        try {
            mRecordingThread?.join(3000)
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping audio thread: ${e.message}")
        }
    }

    private fun recordAudio() {
        val buffer = ByteArray(16384)
        while (mIsRecording) {
            try {
                // Sync check
                val elapsedRealTimeUs = (System.nanoTime() - mStartTimeNs) / 1000
                val audioPosUs = (mSamplesWritten * 1_000_000L) / mSampleRate

                if (audioPosUs > elapsedRealTimeUs + 500_000L) {
                    Thread.sleep(50)
                    continue
                }

                // Feed leftover
                mLeftoverPcm?.let { leftover ->
                    mEncoder?.let { encoder ->
                        val inputBufferIndex = encoder.dequeueInputBuffer(0)
                        if (inputBufferIndex >= 0) {
                            val inputBuffer = encoder.getInputBuffer(inputBufferIndex)
                            inputBuffer?.clear()
                            val copySize =
                                minOf(leftover.remaining(), inputBuffer?.remaining() ?: 0)
                            if (copySize > 0) {
                                val oldLimit = leftover.limit()
                                leftover.limit(leftover.position() + copySize)
                                inputBuffer?.put(leftover)
                                leftover.limit(oldLimit)
                            }
                            encoder.queueInputBuffer(
                                inputBufferIndex,
                                0,
                                copySize,
                                mLeftoverPcmTimestampUs,
                                0
                            )
                            if (!leftover.hasRemaining()) {
                                mLeftoverPcm = null
                                mLeftoverPcmTimestampUs = -1
                            }
                        }
                    }
                }

                if (mLeftoverPcm != null) {
                    drainEncoder(false)
                    Thread.sleep(5)
                    continue
                }

                val bytesRead = audioSource.readPcmData(buffer)
                if (bytesRead > 0) {
                    mEncoder?.let { encoder ->
                        val inputBufferIndex = encoder.dequeueInputBuffer(1000)
                        if (inputBufferIndex >= 0) {
                            val inputBuffer = encoder.getInputBuffer(inputBufferIndex)
                            inputBuffer?.clear()
                            val copySize = minOf(bytesRead, inputBuffer?.remaining() ?: 0)
                            inputBuffer?.put(buffer, 0, copySize)

                            // 🛡️ Cải thiện đồng bộ: Dùng nanoTime để khớp với VideoEncoder nếu có mStartTimeNs
                            val presentationTimeUs = if (mStartTimeNs > 0) {
                                (System.nanoTime() - mStartTimeNs) / 1000
                            } else {
                                (mSamplesWritten * 1_000_000L) / mSampleRate
                            }

                            encoder.queueInputBuffer(
                                inputBufferIndex,
                                0,
                                copySize,
                                presentationTimeUs,
                                0
                            )
                            mSamplesWritten += copySize / (mChannelCount * 2)
                        } else {
                            Log.w(TAG, "No audio input buffer available, storing to leftover")
                            val leftover = mLeftoverPcm
                            if (leftover == null || leftover.capacity() < bytesRead) {
                                mLeftoverPcm = ByteBuffer.allocateDirect(bytesRead * 2)
                            }
                            mLeftoverPcm?.let {
                                it.clear()
                                it.put(buffer, 0, bytesRead)
                                it.flip()
                            }
                            mLeftoverPcmTimestampUs = if (mStartTimeNs > 0) {
                                (System.nanoTime() - mStartTimeNs) / 1000
                            } else {
                                (mSamplesWritten * 1_000_000L) / mSampleRate
                            }
                            mSamplesWritten += bytesRead / (mChannelCount * 2)
                        }
                    }
                    drainEncoder(false)
                } else if (bytesRead < 0) {
                    Log.i(TAG, "Audio source reached end of stream")
                    break
                } else {
                    Thread.sleep(1)
                    drainEncoder(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in audio recordAudio loop: ${e.message}", e)
                break
            }
        }

        // EOS
        Log.i(TAG, "Signaling audio EOS")
        try {
            mEncoder?.let { encoder ->
                val inputBufferIndex = encoder.dequeueInputBuffer(10000)
                if (inputBufferIndex >= 0) {
                    encoder.queueInputBuffer(
                        inputBufferIndex,
                        0,
                        0,
                        (mSamplesWritten * 1_000_000L) / mSampleRate,
                        MediaCodec.BUFFER_FLAG_END_OF_STREAM
                    )
                }
            }
            drainEncoder(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error signaling audio EOS: ${e.message}")
        }
        Log.i(TAG, "Audio recording thread exiting")
    }

    private fun drainEncoder(endOfStream: Boolean) {
        val encoder = mEncoder ?: return
        val bufferInfo = mBufferInfo ?: return
        val TIMEOUT_USEC = if (endOfStream) 10000L else 0L
        var retryCount = 0
        val maxRetries = if (endOfStream) 100 else 0

        while (true) {
            try {
                val encoderStatus = encoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC)
                if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    if (!endOfStream || retryCount >= maxRetries) break
                    retryCount++
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    if (mTrackIndex >= 0) {
                        Log.w(TAG, "Audio track index already set, format changed again?")
                        break
                    }
                    val newFormat = encoder.outputFormat
                    synchronized(muxer) {
                        mTrackIndex = muxer.addTrack(newFormat)
                        onTrackAdded(mTrackIndex)
                    }
                } else if (encoderStatus >= 0) {
                    val outputBuffer = encoder.getOutputBuffer(encoderStatus)
                    if (outputBuffer != null && bufferInfo.size > 0) {
                        writeEncodedSample(outputBuffer, bufferInfo, encoderStatus)
                    } else {
                        encoder.releaseOutputBuffer(encoderStatus, false)
                    }
                    if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) break
                }
            } catch (e: Exception) {
                break
            }
        }
    }

    private fun writeEncodedSample(
        encodedData: ByteBuffer,
        bufferInfo: MediaCodec.BufferInfo,
        encoderStatus: Int
    ) {
        if (isMuxerStarted() && mTrackIndex >= 0) {
            synchronized(mPendingBuffers) {
                if (mPendingBuffers.isNotEmpty()) {
                    for (pending in mPendingBuffers) {
                        try {
                            synchronized(muxer) {
                                muxer.writeSampleData(
                                    mTrackIndex,
                                    pending.data,
                                    pending.info
                                )
                            }
                        } catch (e: Exception) {
                        }
                    }
                    mPendingBuffers.clear()
                }
            }
            encodedData.position(bufferInfo.offset)
            encodedData.limit(bufferInfo.offset + bufferInfo.size)
            try {
                synchronized(muxer) { muxer.writeSampleData(mTrackIndex, encodedData, bufferInfo) }
            } catch (e: Exception) {
            }
            mEncoder?.releaseOutputBuffer(encoderStatus, false)
        } else {
            val copy = ByteBuffer.allocateDirect(bufferInfo.size)
            encodedData.position(bufferInfo.offset)
            encodedData.limit(bufferInfo.offset + bufferInfo.size)
            copy.put(encodedData)
            copy.flip()
            val infoCopy = MediaCodec.BufferInfo()
            infoCopy.set(0, bufferInfo.size, bufferInfo.presentationTimeUs, bufferInfo.flags)
            synchronized(mPendingBuffers) { mPendingBuffers.add(PendingBuffer(copy, infoCopy)) }
            mEncoder?.releaseOutputBuffer(encoderStatus, false)
        }
    }

    fun release() {
        stopRecording()
        mEncoder?.let {
            try {
                it.stop(); it.release()
            } catch (e: Exception) {
            }
        }
        mEncoder = null
        try {
            audioSource.release()
        } catch (e: Exception) {
        }
        synchronized(mPendingBuffers) { mPendingBuffers.clear() }
    }

    companion object {
        private const val TAG = "AudioEncoderCore"
        const val AUDIO_SAMPLE_RATE = 44100
        const val AUDIO_BITRATE = 128000
        const val AUDIO_MIME_TYPE = "audio/mp4a-latm"

        private fun selectAudioCodec(mimeType: String): String? {
            val numCodecs = MediaCodecList.getCodecCount()
            for (i in 0 until numCodecs) {
                val codecInfo = MediaCodecList.getCodecInfoAt(i)
                if (!codecInfo.isEncoder) continue
                val types = codecInfo.supportedTypes
                for (type in types) {
                    if (type.equals(mimeType, ignoreCase = true)) return codecInfo.name
                }
            }
            return null
        }
    }
}
