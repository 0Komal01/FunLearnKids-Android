package com.marwadiuniversity.abckids.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.*

class InstrumentSynthesizer(private val context: Context) {

    private var audioManager: AudioManager? = null
    private val sampleRate = 44100
    private val scope = CoroutineScope(Dispatchers.IO)

    // Frequency mappings for all instruments
    private val pianoFrequencies = mapOf(
        "C" to 261.63f, "D" to 293.66f, "E" to 329.63f, "F" to 349.23f,
        "G" to 392.00f, "A" to 440.00f, "B" to 493.88f, "C2" to 523.25f,
        "C#" to 277.18f, "D#" to 311.13f, "F#" to 369.99f, "G#" to 415.30f, "A#" to 466.16f
    )

    private val fluteFrequencies = mapOf(
        "C" to 261.63f, "D" to 293.66f, "E" to 329.63f, "F" to 349.23f,
        "G" to 392.00f, "A" to 440.00f, "B" to 493.88f, "C2" to 523.25f,
        "E♭" to 311.13f, "F#" to 369.99f, "B♭" to 466.16f
    )

    private val harmonicaFrequencies = mapOf(
        "G" to 392.00f, "B" to 493.88f, "D" to 587.33f, "F#" to 739.99f,
        "A" to 880.00f, "C" to 523.25f, "E" to 659.25f, "F" to 698.46f
    )

    private val trumpetFrequencies = mapOf(
        "C" to 261.63f, "D" to 293.66f, "E" to 329.63f, "F" to 349.23f,
        "G" to 392.00f, "A" to 440.00f, "B" to 493.88f, "C2" to 523.25f,
        "Bb" to 466.16f, "F#" to 369.99f
    )

    private val xylophoneFrequencies = mapOf(
        "C" to 523.25f, "D" to 587.33f, "E" to 659.25f, "F" to 698.46f,
        "G" to 783.99f, "A" to 880.00f, "B" to 987.77f, "C2" to 1046.50f
    )

    private val harpFrequencies = mapOf(
        "Sa" to 261.63f, "Re" to 293.66f, "Ga" to 329.63f, "Ma" to 349.23f,
        "Pa" to 392.00f, "Dha" to 440.00f, "Ni" to 493.88f
    )

    init {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    // Piano sounds with rich harmonics
    fun playPianoSound(note: String) {
        val frequency = pianoFrequencies[note] ?: 440.0f
        scope.launch {
            generatePianoSound(frequency, 1200)
        }
    }

    // Flute sounds with breathy characteristics
    fun playFluteSound(note: String) {
        val frequency = fluteFrequencies[note] ?: 523.25f
        scope.launch {
            generateFluteSound(frequency, 1000)
        }
    }

    // Harmonica sounds with reed characteristics
    fun playHarmonicaSound(note: String) {
        val frequency = harmonicaFrequencies[note] ?: 440.0f
        scope.launch {
            generateHarmonicaSound(frequency, 800)
        }
    }

    // Trumpet sounds with brass characteristics
    fun playTrumpetSound(note: String) {
        val frequency = trumpetFrequencies[note] ?: 440.0f
        scope.launch {
            generateTrumpetSound(frequency, 1000)
        }
    }

    // Xylophone sounds with percussive characteristics
    fun playXylophoneSound(note: String) {
        val frequency = xylophoneFrequencies[note] ?: 523.25f
        scope.launch {
            generateXylophoneSound(frequency, 800)
        }
    }

    // Harp sounds with string characteristics (Indian Classical Sargam)
    fun playHarpSound(note: String) {
        val frequency = harpFrequencies[note] ?: 440.0f
        scope.launch {
            generateHarpSound(frequency, 1500)
        }
    }

    // Maraca sounds with percussion characteristics
    fun playMaracaSound(side: String) {
        scope.launch {
            when (side.lowercase()) {
                "left" -> generateMaracaSound(800f, 200)
                "right" -> generateMaracaSound(1000f, 200)
                "both" -> {
                    generateMaracaSound(800f, 200)
                    generateMaracaSound(1000f, 200)
                }
                else -> generateMaracaSound(900f, 200)
            }
        }
    }

    fun playRhythmPattern(pattern: String) {
        scope.launch {
            when (pattern.lowercase()) {
                "salsa" -> playSalsaPattern()
                "samba" -> playSambaPattern()
                "rumba" -> playRumbaPattern()
                "cha-cha" -> playChaChaPattern()
                else -> playMaracaSound("both")
            }
        }
    }

    // Piano sound generation
    private fun generatePianoSound(frequency: Float, durationMs: Int) {
        val samples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(samples)

        for (i in 0 until samples) {
            val time = i.toFloat() / sampleRate
            val envelope = when {
                time < 0.01f -> time / 0.01f
                else -> exp(-(time - 0.01f) * 2.5).toFloat()
            }

            val fundamental = sin(2 * PI.toFloat() * frequency * time)
            val harmonic2 = sin(2 * PI.toFloat() * frequency * 2.01f * time) * 0.4f
            val harmonic3 = sin(2 * PI.toFloat() * frequency * 3.02f * time) * 0.25f
            val harmonic4 = sin(2 * PI.toFloat() * frequency * 4.03f * time) * 0.15f

            val sample = (fundamental + harmonic2 + harmonic3 + harmonic4) * envelope * 0.3f
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playAudio(buffer)
    }

    // Flute sound generation
    private fun generateFluteSound(frequency: Float, durationMs: Int) {
        val samples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(samples)

        for (i in 0 until samples) {
            val time = i.toFloat() / sampleRate
            val envelope = when {
                time < 0.08f -> sin(PI.toFloat() * time / 0.16f)
                time < (durationMs / 1000f) * 0.75f -> 1.0f - 0.1f * sin(2 * PI.toFloat() * 2f * time)
                else -> {
                    val decayStart = (durationMs / 1000f) * 0.75f
                    val decayTime = time - decayStart
                    val decayDuration = (durationMs / 1000f) * 0.25f
                    cos(PI.toFloat() * decayTime / (2f * decayDuration)).coerceAtLeast(0f)
                }
            }.coerceIn(0f, 1f)

            val fundamental = sin(2 * PI.toFloat() * frequency * time)
            val harmonic3 = sin(2 * PI.toFloat() * frequency * 3f * time) * 0.25f
            val harmonic5 = sin(2 * PI.toFloat() * frequency * 5f * time) * 0.15f
            val breathNoise = (Math.random() - 0.5).toFloat() * 0.06f * envelope

            val sample = (fundamental + harmonic3 + harmonic5 + breathNoise) * envelope * 0.35f
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playAudio(buffer)
    }

    // Harmonica sound generation
    private fun generateHarmonicaSound(frequency: Float, durationMs: Int) {
        val samples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(samples)

        for (i in 0 until samples) {
            val time = i.toFloat() / sampleRate
            val envelope = when {
                time < 0.05f -> time / 0.05f
                time < (durationMs / 1000f) * 0.8f -> 1.0f
                else -> {
                    val decayStart = (durationMs / 1000f) * 0.8f
                    val decayTime = time - decayStart
                    val decayDuration = (durationMs / 1000f) * 0.2f
                    exp(-(decayTime / decayDuration) * 5).toFloat()
                }
            }

            val fundamental = sin(2 * PI.toFloat() * frequency * time)
            val harmonic2 = sin(2 * PI.toFloat() * frequency * 2f * time) * 0.7f
            val harmonic3 = sin(2 * PI.toFloat() * frequency * 3f * time) * 0.5f
            val reedBuzz = sin(2 * PI.toFloat() * frequency * time) * (1 + 0.1f * sin(2 * PI.toFloat() * frequency * 8 * time))
            val tremolo = 1 + 0.03f * sin(2 * PI.toFloat() * 4.5f * time)

            val sample = (reedBuzz + harmonic2 + harmonic3) * envelope * tremolo * 0.35f
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playAudio(buffer)
    }

    // Trumpet sound generation
    private fun generateTrumpetSound(frequency: Float, durationMs: Int) {
        val samples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(samples)

        for (i in 0 until samples) {
            val time = i.toFloat() / sampleRate
            val envelope = when {
                time < 0.1f -> time / 0.1f
                time < (durationMs / 1000f) * 0.7f -> 1.0f
                else -> {
                    val decayStart = (durationMs / 1000f) * 0.7f
                    val decayTime = time - decayStart
                    val decayDuration = (durationMs / 1000f) * 0.3f
                    exp(-(decayTime / decayDuration) * 3).toFloat()
                }
            }

            val fundamental = sin(2 * PI.toFloat() * frequency * time)
            val harmonic2 = sin(2 * PI.toFloat() * frequency * 2f * time) * 0.8f
            val harmonic3 = sin(2 * PI.toFloat() * frequency * 3f * time) * 0.6f
            val harmonic4 = sin(2 * PI.toFloat() * frequency * 4f * time) * 0.4f
            val harmonic5 = sin(2 * PI.toFloat() * frequency * 5f * time) * 0.3f

            // Brass brightness
            val brightness = sin(2 * PI.toFloat() * frequency * 6f * time) * 0.2f * envelope

            val sample = (fundamental + harmonic2 + harmonic3 + harmonic4 + harmonic5 + brightness) * envelope * 0.4f
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playAudio(buffer)
    }

    // Xylophone sound generation
    private fun generateXylophoneSound(frequency: Float, durationMs: Int) {
        val samples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(samples)

        for (i in 0 until samples) {
            val time = i.toFloat() / sampleRate
            val envelope = exp(-time * 3.5).toFloat() // Quick decay like wooden percussion

            val fundamental = sin(2 * PI.toFloat() * frequency * time)
            val harmonic2 = sin(2 * PI.toFloat() * frequency * 2f * time) * 0.3f
            val harmonic3 = sin(2 * PI.toFloat() * frequency * 3f * time) * 0.2f
            val harmonic4 = sin(2 * PI.toFloat() * frequency * 4f * time) * 0.15f

            // Wood resonance
            val woodResonance = sin(2 * PI.toFloat() * frequency * 0.5f * time) * 0.1f * envelope

            val sample = (fundamental + harmonic2 + harmonic3 + harmonic4 + woodResonance) * envelope * 0.5f
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playAudio(buffer)
    }

    // Harp sound generation
    private fun generateHarpSound(frequency: Float, durationMs: Int) {
        val samples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(samples)

        for (i in 0 until samples) {
            val time = i.toFloat() / sampleRate
            val envelope = exp(-time * 2.2).toFloat()

            val fundamental = sin(2 * PI.toFloat() * frequency * time)
            val harmonic2 = sin(2 * PI.toFloat() * frequency * 2f * time) * 0.5f
            val harmonic3 = sin(2 * PI.toFloat() * frequency * 3f * time) * 0.33f
            val harmonic4 = sin(2 * PI.toFloat() * frequency * 4f * time) * 0.25f

            // String pluck characteristic
            val pitchBend = 1 + 0.02f * exp(-time * 10).toFloat()
            val bendedFundamental = sin(2 * PI.toFloat() * frequency * pitchBend * time)
            val tremolo = 1 + 0.05f * sin(2 * PI.toFloat() * 6 * time) * exp(-time * 3).toFloat()

            val sample = (bendedFundamental + harmonic2 + harmonic3 + harmonic4) * envelope * tremolo * 0.4f
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playAudio(buffer)
    }

    // Maraca sound generation
    private fun generateMaracaSound(frequency: Float, durationMs: Int) {
        val samples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(samples)

        for (i in 0 until samples) {
            val time = i.toFloat() / sampleRate
            val envelope = exp(-time * 8).toFloat() // Very quick decay

            // Generate noise-based percussion sound
            val noise = (Math.random() - 0.5).toFloat()
            val filteredNoise = noise * sin(2 * PI.toFloat() * frequency * time) * 0.7f
            val rattle = sin(2 * PI.toFloat() * frequency * 2f * time) * 0.3f * (Math.random().toFloat())

            val sample = (filteredNoise + rattle) * envelope * 0.6f
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playAudio(buffer)
    }

    // Rhythm patterns
    private fun playSalsaPattern() {
        val pattern = listOf(0L, 200L, 400L, 700L, 900L, 1100L)
        pattern.forEach { delay ->
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                generateMaracaSound(900f, 150)
            }, delay)
        }
    }

    private fun playSambaPattern() {
        val pattern = listOf(0L, 150L, 300L, 600L, 750L, 900L, 1200L)
        pattern.forEach { delay ->
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                generateMaracaSound(850f, 120)
            }, delay)
        }
    }

    private fun playRumbaPattern() {
        val pattern = listOf(0L, 300L, 600L, 800L, 1100L)
        pattern.forEach { delay ->
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                generateMaracaSound(950f, 180)
            }, delay)
        }
    }

    private fun playChaChaPattern() {
        val pattern = listOf(0L, 200L, 400L, 500L, 700L, 900L, 1000L, 1200L)
        pattern.forEach { delay ->
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                generateMaracaSound(800f, 100)
            }, delay)
        }
    }

    // Bell sound for special effects
    fun playBellSound(note: String) {
        val frequency = pianoFrequencies[note] ?: 523.25f
        scope.launch {
            generateBellSound(frequency, 2000)
        }
    }

    private fun generateBellSound(frequency: Float, durationMs: Int) {
        val samples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(samples)

        for (i in 0 until samples) {
            val time = i.toFloat() / sampleRate
            val envelope = exp(-time * 0.8).toFloat()

            val fundamental = sin(2 * PI.toFloat() * frequency * time)
            val harmonic2 = sin(2 * PI.toFloat() * frequency * 2.76f * time) * 0.6f
            val harmonic3 = sin(2 * PI.toFloat() * frequency * 5.40f * time) * 0.4f
            val shimmer = sin(2 * PI.toFloat() * frequency * 16.2f * time) * 0.15f * sin(2 * PI.toFloat() * 5 * time)

            val sample = (fundamental + harmonic2 + harmonic3 + shimmer) * envelope * 0.4f
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playAudio(buffer)
    }

    // Success sound
    fun playSuccessSound() {
        scope.launch {
            generateSuccessBeep()
        }
    }

    private fun generateSuccessBeep() {
        val samples = (sampleRate * 300 / 1000)
        val buffer = ShortArray(samples)

        for (i in 0 until samples) {
            val time = i.toFloat() / sampleRate
            val envelope = 1 - time / 0.3f

            val note1 = sin(2 * PI.toFloat() * 523.25f * time) // C5
            val note2 = sin(2 * PI.toFloat() * 659.25f * time) // E5
            val note3 = sin(2 * PI.toFloat() * 783.99f * time) // G5

            val sample = (note1 + note2 + note3) * envelope * 0.2f
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playAudio(buffer)
    }

    // Play generated audio buffer
    private fun playAudio(buffer: ShortArray) {
        try {
            val audioTrack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .build()
            } else {
                @Suppress("DEPRECATION")
                AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    buffer.size * 2,
                    AudioTrack.MODE_STATIC
                )
            }

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()

            Thread {
                Thread.sleep((buffer.size * 1000L / sampleRate) + 100)
                audioTrack.stop()
                audioTrack.release()
            }.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        // Cleanup resources if needed
    }
}