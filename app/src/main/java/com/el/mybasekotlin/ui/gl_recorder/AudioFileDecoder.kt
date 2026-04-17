package com.el.mybasekotlin.ui.gl_recorder

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import java.io.IOException
import java.nio.ByteBuffer

/**
 * Decodes audio file (MP3, AAC, etc.) to PCM format for encoding.
 * Now implements AudioSource interface.
 */
class AudioFileDecoder(
    private val audioFilePath: String,
    private val loop: Boolean = true
) : AudioSource {
    private var mExtractor: MediaExtractor? = null
    private var mDecoder: MediaCodec? = null
    private var mAudioTrackIndex: Int = -1
    private var mIsDecoding: Boolean = false
    private var mInputEOF: Boolean = false
    private var mOutputEOS: Boolean = false
    private var mSampleRate: Int = 0
    private var mChannelCount: Int = 0
    private var mDurationUs: Long = 0
    private var mCurrentPositionUs: Long = 0
    private var mDecodedSamples: Long = 0

    // Buffer to store leftover data that didn't fit in caller's buffer
    private var mLeftoverBuffer: ByteBuffer? = null

    init {
        setupExtractor()
    }

    private fun setupExtractor() {
        try {
            val extractor = MediaExtractor()
            mExtractor = extractor
            extractor.setDataSource(audioFilePath)

            val numTracks = extractor.trackCount
            for (i in 0 until numTracks) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)

                if (mime != null && mime.startsWith("audio/")) {
                    mAudioTrackIndex = i
                    mSampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                    mChannelCount = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
                    mDurationUs = format.getLong(MediaFormat.KEY_DURATION)
                    break
                }
            }

            if (mAudioTrackIndex < 0) {
                throw IOException("No audio track found in file: $audioFilePath")
            }

            extractor.selectTrack(mAudioTrackIndex)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to setup extractor: ${e.message}", e)
            release()
            throw RuntimeException("Failed to setup audio extractor: ${e.message}", e)
        }
    }

    private fun setupDecoder() {
        try {
            val extractor = mExtractor ?: return
            val format = extractor.getTrackFormat(mAudioTrackIndex)
            val mime = format.getString(MediaFormat.KEY_MIME) ?: throw RuntimeException("No MIME")

            val decoder = MediaCodec.createDecoderByType(mime)
            mDecoder = decoder
            decoder.configure(format, null, null, 0)
            decoder.start()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to setup decoder: ${e.message}", e)
            release()
            throw RuntimeException("Failed to setup audio decoder: ${e.message}", e)
        }
    }

    override fun start() {
        if (mIsDecoding) return
        if (mDecoder == null) setupDecoder()

        mIsDecoding = true
        mInputEOF = false
        mOutputEOS = false
        mCurrentPositionUs = 0
        mDecodedSamples = 0
        try {
            mExtractor?.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
            Log.d(TAG, "start: Seeked to beginning successfully")
        } catch (e: Exception) {
            Log.e(TAG, "start: Failed to seek: ${e.message}")
        }
    }

    override fun readPcmData(buffer: ByteArray): Int {
        Log.d(TAG, "readPcmData: called")
        if (!mIsDecoding || mDecoder == null) return -1

        var totalBytesRead = 0
        val targetBytes = buffer.size

        mLeftoverBuffer?.let { leftover ->
            if (leftover.hasRemaining()) {
                val toRead = minOf(leftover.remaining(), targetBytes)
                leftover.get(buffer, 0, toRead)
                totalBytesRead += toRead
            }
        }

        while (totalBytesRead < targetBytes && mIsDecoding) {
            val decoder = mDecoder ?: break
            val extractor = mExtractor ?: break
            
            if (!mInputEOF) {
                val inputBufferIndex = decoder.dequeueInputBuffer(1000)
                if (inputBufferIndex >= 0) {
                    val inputBuffer = decoder.getInputBuffer(inputBufferIndex)
                    val sampleSize = inputBuffer?.let { extractor.readSampleData(it, 0) } ?: -1

                    if (sampleSize < 0) {
                        decoder.queueInputBuffer(
                            inputBufferIndex,
                            0,
                            0,
                            0,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                        mInputEOF = true
                    } else {
                        val presentationTimeUs = extractor.sampleTime
                        decoder.queueInputBuffer(
                            inputBufferIndex,
                            0,
                            sampleSize,
                            presentationTimeUs,
                            0
                        )
                        extractor.advance()
                        mCurrentPositionUs = presentationTimeUs
                    }
                }
            }

            val bufferInfo = MediaCodec.BufferInfo()
            val outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, 1000)

            if (outputBufferIndex >= 0) {
                val outputBuffer = decoder.getOutputBuffer(outputBufferIndex)
                if (outputBuffer != null && bufferInfo.size > 0) {
                    val bytesToRead = minOf(bufferInfo.size, targetBytes - totalBytesRead)
                    outputBuffer.position(bufferInfo.offset)
                    outputBuffer.limit(bufferInfo.offset + bufferInfo.size)
                    outputBuffer.get(buffer, totalBytesRead, bytesToRead)

                    totalBytesRead += bytesToRead
                    mDecodedSamples += bytesToRead / (mChannelCount * 2)

                    if (outputBuffer.hasRemaining()) {
                        val remaining = outputBuffer.remaining()
                        val leftover = mLeftoverBuffer
                        if (leftover == null || leftover.capacity() < remaining) {
                            mLeftoverBuffer = ByteBuffer.allocateDirect(maxOf(remaining * 2, 16384))
                        }
                        mLeftoverBuffer?.let {
                            it.clear()
                            it.put(outputBuffer)
                            it.flip()
                        }
                    }
                }
                decoder.releaseOutputBuffer(outputBufferIndex, false)

                if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (loop && mIsDecoding) {
                        decoder.flush()
                        extractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
                        mInputEOF = false
                        mOutputEOS = false
                    } else {
                        mOutputEOS = true
                        break
                    }
                }
            } else if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (mInputEOF && !loop) {
                    mOutputEOS = true
                    break
                }
                break
            }
        }

        return if (totalBytesRead > 0) totalBytesRead else (if (mOutputEOS) -1 else 0)
    }

    override fun stop() {
        mIsDecoding = false
    }

    override fun release() {
        stop()
        try {
            mDecoder?.stop()
        } catch (e: Exception) {
        }
        try {
            mDecoder?.release()
        } catch (e: Exception) {
        }
        mDecoder = null
        try {
            mExtractor?.release()
        } catch (e: Exception) {
        }
        mExtractor = null
    }

    override fun getSampleRate(): Int = mSampleRate
    override fun getChannelCount(): Int = mChannelCount

    companion object {
        private const val TAG = "AudioFileDecoder"
    }
}
