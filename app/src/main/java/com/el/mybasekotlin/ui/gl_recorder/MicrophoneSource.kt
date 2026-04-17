package com.el.mybasekotlin.ui.gl_recorder

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log

/**
 * Audio source that captures audio from the microphone.
 */
class MicrophoneSource(
    private val sampleRate: Int = 44100,
    private val channelCount: Int = 1
) : AudioSource {
    private var mAudioRecord: AudioRecord? = null
    private var mIsRecording = false
    private val mBufferSize: Int

    init {
        val channelConfig =
            if (channelCount == 1) AudioFormat.CHANNEL_IN_MONO else AudioFormat.CHANNEL_IN_STEREO
        mBufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            channelConfig,
            AudioFormat.ENCODING_PCM_16BIT
        ) * 2

        setupAudioRecord(channelConfig)
    }

    @SuppressLint("MissingPermission")
    private fun setupAudioRecord(channelConfig: Int) {
        try {
            mAudioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                AudioFormat.ENCODING_PCM_16BIT,
                mBufferSize
            )

            if (mAudioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "AudioRecord initialization failed")
                mAudioRecord = null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to setup microphone: ${e.message}")
        }
    }

    override fun getSampleRate(): Int = sampleRate

    override fun getChannelCount(): Int = channelCount

    override fun start() {
        if (mIsRecording) return
        try {
            mAudioRecord?.startRecording()
            mIsRecording = true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start microphone: ${e.message}")
        }
    }

    override fun readPcmData(buffer: ByteArray): Int {
        val record = mAudioRecord ?: return -1
        if (!mIsRecording) return -1

        // AudioRecord.read is blocking if there's no data
        return record.read(buffer, 0, buffer.size)
    }

    override fun stop() {
        if (!mIsRecording) return
        mIsRecording = false
        try {
            mAudioRecord?.stop()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop microphone: ${e.message}")
        }
    }

    override fun release() {
        stop()
        mAudioRecord?.release()
        mAudioRecord = null
    }

    companion object {
        private const val TAG = "MicrophoneSource"
    }
}
