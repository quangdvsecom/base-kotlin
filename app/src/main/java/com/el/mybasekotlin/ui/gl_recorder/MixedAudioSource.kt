package com.el.mybasekotlin.ui.gl_recorder

/**
 * Mixes two audio sources into one stream.
 * Assumes both sources have the same sample rate and channel count.
 */
class MixedAudioSource(
    private val source1: AudioSource,
    private val source2: AudioSource
) : AudioSource {

    private var buffer1 = ByteArray(16384)
    private var buffer2 = ByteArray(16384)

    override fun getSampleRate(): Int = source1.getSampleRate()
    override fun getChannelCount(): Int = source1.getChannelCount()

    override fun start() {
        source1.start()
        source2.start()
    }

    override fun readPcmData(buffer: ByteArray): Int {
        val size = buffer.size
        // Ensure temp buffers are large enough
        val b1 = if (buffer1.size >= size) buffer1 else ByteArray(size).also { buffer1 = it }
        val b2 = if (buffer2.size >= size) buffer2 else ByteArray(size).also { buffer2 = it }

        // Read directly into part of the buffer if possible, but we need to mix,
        // so we read into temp buffers.
        val read1 = source1.readPcmData(b1)
        val read2 = source2.readPcmData(b2)

        if (read1 <= 0 && read2 <= 0) return -1

        val actualRead = maxOf(read1, read2)

        // Mix PCM 16-bit data
        for (i in 0 until actualRead step 2) {
            if (i + 1 >= actualRead) break

            val s1 = if (i + 1 < read1) {
                ((b1[i + 1].toInt() shl 8) or (b1[i].toInt() and 0xFF)).toShort()
            } else 0

            val s2 = if (i + 1 < read2) {
                ((b2[i + 1].toInt() shl 8) or (b2[i].toInt() and 0xFF)).toShort()
            } else 0

            // Mix with clipping protection
            val mixed = s1.toInt() + s2.toInt()
            val clipped = mixed.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()

            buffer[i] = (clipped.toInt() and 0xFF).toByte()
            buffer[i + 1] = ((clipped.toInt() shr 8) and 0xFF).toByte()
        }

        return actualRead
    }

    override fun stop() {
        source1.stop()
        source2.stop()
    }

    override fun release() {
        source1.release()
        source2.release()
    }
}
