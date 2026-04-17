package com.el.mybasekotlin.ui.gl_recorder

/**
 * Interface for audio data sources (Mic, File, Mixed).
 */
interface AudioSource {
    fun getSampleRate(): Int
    fun getChannelCount(): Int
    fun start()
    fun readPcmData(buffer: ByteArray): Int
    fun stop()
    fun release()
}
