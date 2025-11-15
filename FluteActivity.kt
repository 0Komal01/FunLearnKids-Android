package com.marwadiuniversity.abckids

import android.content.pm.ActivityInfo
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.*

class FluteActivity : AppCompatActivity() {

    private lateinit var synthesizer: FluteSynthesizer

    // Indian classical sargam notes with their frequencies (in Hz)
    private val sargamNotes = mapOf(
        "Sa" to 261.63,   // C4
        "Re" to 293.66,   // D4
        "Ga" to 329.63,   // E4
        "Ma" to 349.23,   // F4
        "Pa" to 392.00,   // G4
        "Dha" to 440.00,  // A4
        "Ni" to 493.88,   // B4
        "Sa'" to 523.25,  // C5
        "Re'" to 587.33,  // D5
        "Ga'" to 659.25,  // E5
        "Ma'" to 698.46,  // F5
        "Pa'" to 783.99   // G5
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            synthesizer = FluteSynthesizer()
            setContentView(createFluteLayoutWithHeader())
        } catch (e: Exception) {
            Log.e("FluteActivity", "Error in onCreate", e)
            finish()
        }
    }

    private fun createFluteLayoutWithHeader(): LinearLayout {
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            background = createEnhancedWoodBackground()
        }

        try {
            // Create header
            val header = createHeader()
            mainLayout.addView(header)

            // Content container that takes remaining space
            val contentContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1f // Take remaining space after header
                )
            }

            // Create the flute content
            val fluteContent = createRealisticFluteContent()
            contentContainer.addView(fluteContent)

            mainLayout.addView(contentContainer)
        } catch (e: Exception) {
            Log.e("FluteActivity", "Error creating layout with header", e)
        }

        return mainLayout
    }

    private fun createHeader(): LinearLayout {
        val headerContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(30)
            )
            setPadding(20, 10, 20, 10)

            // Create gradient background similar to the XML
            val drawable = GradientDrawable().apply {
                colors = intArrayOf(
                    Color.parseColor("#6FC9C6"),
                    Color.parseColor("#6FC9C6")
                )
                orientation = GradientDrawable.Orientation.TL_BR // 45 degree angle
            }
            background = drawable
        }

        try {
            // Back button
            val backButton = TextView(this).apply {
                id = View.generateViewId()
                layoutParams = LinearLayout.LayoutParams(
                    dpToPx(40),
                    dpToPx(40)
                )
                setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))

                text = "←"
                textSize = 20f
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                background = null
                isClickable = true
                isFocusable = true

                setOnClickListener {
                    try {
                        finish()
                    } catch (e: Exception) {
                        Log.e("FluteActivity", "Error handling back button", e)
                    }
                }
            }

            val spacer = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            headerContainer.addView(backButton)
            headerContainer.addView(spacer)

        } catch (e: Exception) {
            Log.e("FluteActivity", "Error creating header", e)
        }

        return headerContainer
    }

    private fun createRealisticFluteContent(): RelativeLayout {
        val mainLayout = RelativeLayout(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        }

        try {
            // Title
            val titleText = TextView(this).apply {
                id = View.generateViewId()
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    addRule(RelativeLayout.CENTER_HORIZONTAL)
                    topMargin = dpToPx(20) // Reduced margin since we have header
                }
                text = "बांसुरी - Indian Flute"
                textSize = 22f
                setTextColor(Color.parseColor("#3E2723"))
                typeface = Typeface.DEFAULT_BOLD
            }

            // Flute container
            val fluteContainer = RelativeLayout(this).apply {
                id = View.generateViewId()
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(150)
                ).apply {
                    addRule(RelativeLayout.CENTER_IN_PARENT)
                    setMargins(dpToPx(20), 0, dpToPx(20), 0)
                }
            }

            // Create flute body (main bamboo tube)
            createEnhancedFluteComponents(fluteContainer)

            // Instructions
            val instructionText = TextView(this).apply {
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    addRule(RelativeLayout.CENTER_HORIZONTAL)
                    addRule(RelativeLayout.BELOW, fluteContainer.id)
                    topMargin = dpToPx(25) // Reduced margin
                }
                text = "Tap the holes to play sargam notes"
                textSize = 16f
                setTextColor(Color.parseColor("#5D4037"))
            }

            mainLayout.addView(titleText)
            mainLayout.addView(fluteContainer)
            mainLayout.addView(instructionText)
        } catch (e: Exception) {
            Log.e("FluteActivity", "Error creating flute content", e)
        }

        return mainLayout
    }

    private fun createEnhancedWoodBackground(): GradientDrawable {
        return GradientDrawable().apply {
            orientation = GradientDrawable.Orientation.TOP_BOTTOM
            colors = intArrayOf(
                Color.parseColor("#FFF3E0"), // Light cream
                Color.parseColor("#FFE0B2"), // Light orange
                Color.parseColor("#FFCC02"), // Golden yellow
                Color.parseColor("#FFB74D")  // Warm orange
            )
        }
    }

    private fun createEnhancedFluteComponents(container: RelativeLayout) {
        try {
            // Enhanced main flute body with better gradient
            val fluteBody = View(this).apply {
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(45)
                ).apply {
                    addRule(RelativeLayout.CENTER_VERTICAL)
                }
                background = createEnhancedBambooTexture()
                elevation = dpToPx(6).toFloat()
            }
            container.addView(fluteBody)

            // Enhanced mouthpiece with better styling
            val mouthpiece = View(this).apply {
                layoutParams = RelativeLayout.LayoutParams(dpToPx(35), dpToPx(15)).apply {
                    leftMargin = dpToPx(30)
                    addRule(RelativeLayout.CENTER_VERTICAL)
                    topMargin = -dpToPx(30)
                }
                background = createEnhancedMouthpieceDrawable()
                elevation = dpToPx(8).toFloat()
            }
            container.addView(mouthpiece)

            // Create enhanced finger holes with notes
            createEnhancedFingerHoles(container)

            // Add enhanced bamboo decorations
            createEnhancedDecorations(container)
        } catch (e: Exception) {
            Log.e("FluteActivity", "Error creating flute components", e)
        }
    }

    private fun createEnhancedBambooTexture(): GradientDrawable {
        return GradientDrawable().apply {
            orientation = GradientDrawable.Orientation.TOP_BOTTOM
            colors = intArrayOf(
                Color.parseColor("#DEB887"), // Burlywood
                Color.parseColor("#D2B48C"), // Tan
                Color.parseColor("#CD853F"), // Peru
                Color.parseColor("#A0522D"), // Sienna
                Color.parseColor("#CD853F")  // Peru
            )
            cornerRadius = dpToPx(22).toFloat()
            setStroke(dpToPx(3), Color.parseColor("#8B4513"))
        }
    }

    private fun createEnhancedMouthpieceDrawable(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            colors = intArrayOf(
                Color.parseColor("#2E2E2E"),
                Color.parseColor("#1A1A1A"),
                Color.parseColor("#000000")
            )
            orientation = GradientDrawable.Orientation.TOP_BOTTOM
            setStroke(dpToPx(2), Color.parseColor("#8B4513"))
        }
    }

    private fun createEnhancedFingerHoles(container: RelativeLayout) {
        val notes = sargamNotes.keys.toList()
        val screenWidth = resources.displayMetrics.widthPixels
        val availableWidth = screenWidth - dpToPx(100) // Account for margins
        val holeSpacing = availableWidth / (notes.size + 1)

        val holeColors = listOf(
            "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4",
            "#FECA57", "#FF9FF3", "#54A0FF", "#5F27CD",
            "#FF6348", "#2ECC71", "#3742FA", "#F0932B"
        )

        notes.forEachIndexed { index, note ->
            try {
                val hole = Button(this).apply {
                    layoutParams = RelativeLayout.LayoutParams(dpToPx(30), dpToPx(30)).apply {
                        leftMargin = dpToPx(50) + (holeSpacing * (index + 1)) - dpToPx(15)
                        addRule(RelativeLayout.CENTER_VERTICAL)
                    }
                    background = createEnhancedFingerHole(holeColors[index % holeColors.size])
                    elevation = dpToPx(4).toFloat()
                    setOnClickListener {
                        playNote(note)
                        animateEnhancedHolePress(this)
                    }
                    isHapticFeedbackEnabled = true
                }

                // Enhanced note label with better styling
                val label = TextView(this).apply {
                    layoutParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        leftMargin = dpToPx(50) + (holeSpacing * (index + 1)) - dpToPx(12)
                        topMargin = dpToPx(95)
                    }
                    text = note
                    textSize = 11f
                    setTextColor(Color.parseColor("#2E2E2E"))
                    typeface = Typeface.DEFAULT_BOLD
                    background = createLabelBackground()
                    setPadding(dpToPx(4), dpToPx(2), dpToPx(4), dpToPx(2))
                }

                container.addView(hole)
                container.addView(label)
            } catch (e: Exception) {
                Log.e("FluteActivity", "Error creating finger hole $index", e)
            }
        }
    }

    private fun createEnhancedFingerHole(colorHex: String): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            val baseColor = Color.parseColor(colorHex)
            colors = intArrayOf(
                baseColor,
                adjustBrightness(baseColor, 0.6f),
                Color.parseColor("#1A1A1A")
            )
            orientation = GradientDrawable.Orientation.TOP_BOTTOM
            setStroke(dpToPx(2), Color.parseColor("#8B4513"))
        }
    }

    private fun createLabelBackground(): GradientDrawable {
        return GradientDrawable().apply {
            colors = intArrayOf(
                Color.parseColor("#FFFFFF"),
                Color.parseColor("#F5F5F5")
            )
            cornerRadius = dpToPx(8).toFloat()
            setStroke(dpToPx(1), Color.parseColor("#CCCCCC"))
        }
    }

    private fun createEnhancedDecorations(container: RelativeLayout) {
        try {
            // Add enhanced bamboo node lines
            val nodeCount = 4
            val screenWidth = resources.displayMetrics.widthPixels - dpToPx(40)
            val nodeSpacing = screenWidth / (nodeCount + 1)

            for (i in 1..nodeCount) {
                val node = View(this).apply {
                    layoutParams = RelativeLayout.LayoutParams(dpToPx(3), dpToPx(45)).apply {
                        leftMargin = nodeSpacing * i
                        addRule(RelativeLayout.CENTER_VERTICAL)
                    }
                    background = createNodeBackground()
                    elevation = dpToPx(7).toFloat()
                }
                container.addView(node)
            }

            // Add enhanced end caps
            val leftCap = View(this).apply {
                layoutParams = RelativeLayout.LayoutParams(dpToPx(12), dpToPx(45)).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                    addRule(RelativeLayout.CENTER_VERTICAL)
                }
                background = createEnhancedEndCap()
                elevation = dpToPx(7).toFloat()
            }

            val rightCap = View(this).apply {
                layoutParams = RelativeLayout.LayoutParams(dpToPx(12), dpToPx(45)).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    addRule(RelativeLayout.CENTER_VERTICAL)
                }
                background = createEnhancedEndCap()
                elevation = dpToPx(7).toFloat()
            }

            container.addView(leftCap)
            container.addView(rightCap)
        } catch (e: Exception) {
            Log.e("FluteActivity", "Error creating decorations", e)
        }
    }

    private fun createNodeBackground(): GradientDrawable {
        return GradientDrawable().apply {
            orientation = GradientDrawable.Orientation.LEFT_RIGHT
            colors = intArrayOf(
                Color.parseColor("#654321"),
                Color.parseColor("#8B4513"),
                Color.parseColor("#654321")
            )
            cornerRadius = dpToPx(2).toFloat()
        }
    }

    private fun createEnhancedEndCap(): GradientDrawable {
        return GradientDrawable().apply {
            orientation = GradientDrawable.Orientation.TOP_BOTTOM
            colors = intArrayOf(
                Color.parseColor("#A0522D"),
                Color.parseColor("#8B4513"),
                Color.parseColor("#654321"),
                Color.parseColor("#8B4513")
            )
            cornerRadius = dpToPx(22).toFloat()
            setStroke(dpToPx(2), Color.parseColor("#5D4037"))
        }
    }

    private fun adjustBrightness(color: Int, factor: Float): Int {
        return try {
            val r = (Color.red(color) * factor).toInt().coerceIn(0, 255)
            val g = (Color.green(color) * factor).toInt().coerceIn(0, 255)
            val b = (Color.blue(color) * factor).toInt().coerceIn(0, 255)
            Color.rgb(r, g, b)
        } catch (e: Exception) {
            Log.e("FluteActivity", "Error adjusting brightness", e)
            color
        }
    }

    private fun dpToPx(dp: Int): Int {
        return try {
            (dp * resources.displayMetrics.density).toInt()
        } catch (e: Exception) {
            Log.e("FluteActivity", "Error converting dp to px", e)
            dp
        }
    }

    private fun playNote(note: String) {
        try {
            sargamNotes[note]?.let { frequency ->
                synthesizer.playFlute(frequency, 2500)
            }
        } catch (e: Exception) {
            Log.e("FluteActivity", "Error playing note $note", e)
        }
    }

    private fun animateEnhancedHolePress(button: Button) {
        try {
            button.animate()
                .scaleX(0.85f)
                .scaleY(0.85f)
                .alpha(0.8f)
                .setDuration(100)
                .withEndAction {
                    button.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .alpha(1.0f)
                        .duration = 150
                }
        } catch (e: Exception) {
            Log.e("FluteActivity", "Error animating hole press", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            synthesizer.release()
        } catch (e: Exception) {
            Log.e("FluteActivity", "Error in onDestroy", e)
        }
    }

    // Optimized flute synthesizer - crash prevention
    inner class FluteSynthesizer {
        private var audioTrack: AudioTrack? = null
        private val sampleRate = 44100
        private var isPlaying = false
        private var playbackThread: Thread? = null

        fun playFlute(frequency: Double, durationMs: Long) {
            try {
                // Stop any current playback to prevent overlapping and crashes
                stopCurrentNote()

                playbackThread = Thread {
                    try {
                        isPlaying = true

                        // Smaller buffer size for better responsiveness and less memory usage
                        val minBufferSize = AudioTrack.getMinBufferSize(
                            sampleRate,
                            AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT
                        )

                        // Use 2x minimum buffer instead of 4x to reduce memory
                        val bufferSize = minBufferSize * 2

                        audioTrack = AudioTrack(
                            AudioManager.STREAM_MUSIC,
                            sampleRate,
                            AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            bufferSize,
                            AudioTrack.MODE_STREAM
                        )

                        if (audioTrack?.state != AudioTrack.STATE_INITIALIZED) {
                            return@Thread
                        }

                        audioTrack?.play()

                        // Smaller chunk size for better performance
                        val chunkSize = minBufferSize / 8  // Smaller chunks
                        val chunk = ShortArray(chunkSize)
                        var totalSamples = 0
                        val maxSamples = (sampleRate * durationMs / 1000).toInt()

                        while (isPlaying && totalSamples < maxSamples && !Thread.currentThread().isInterrupted) {
                            val samplesToWrite = minOf(chunkSize, maxSamples - totalSamples)

                            for (i in 0 until samplesToWrite) {
                                val time = (totalSamples + i).toDouble() / sampleRate
                                val duration = durationMs / 1000.0

                                // Generate flute sound
                                var sample = generateBeautifulFlute(frequency, time, duration)

                                // Convert to 16-bit with reduced amplitude to prevent distortion
                                chunk[i] = (sample * 16000).toInt().coerceIn(-32767, 32767).toShort()
                            }

                            if (isPlaying && !Thread.currentThread().isInterrupted) {
                                val written = audioTrack?.write(chunk, 0, samplesToWrite) ?: -1
                                if (written < 0) break // Error occurred
                            }

                            totalSamples += samplesToWrite

                            // Small delay to prevent CPU overload
                            Thread.sleep(1)
                        }

                    } catch (e: Exception) {
                        Log.e("FluteSynthesizer", "Error in playback thread", e)
                    } finally {
                        cleanupAudioTrack()
                    }
                }

                playbackThread?.start()
            } catch (e: Exception) {
                Log.e("FluteSynthesizer", "Error starting flute playback", e)
            }
        }

        private fun generateBeautifulFlute(frequency: Double, time: Double, totalDuration: Double): Double {
            try {
                // Create envelope
                val envelope = createSmoothEnvelope(time, totalDuration)

                // Simplified flute generation for better performance
                var flute = sin(2 * PI * frequency * time) * 0.8

                // Add gentle second harmonic
                flute += sin(2 * PI * frequency * 2 * time) * 0.12 * envelope

                // Reduced vibrato for stability
                val vibratoAmount = 0.008 * min(1.0, time / 0.8)
                val vibratoFreq = 5.0
                val vibrato = 1.0 + vibratoAmount * sin(2 * PI * vibratoFreq * time)

                flute *= vibrato

                // Apply envelope
                flute *= envelope

                return flute
            } catch (e: Exception) {
                Log.e("FluteSynthesizer", "Error generating flute sound", e)
                return 0.0
            }
        }

        private fun createSmoothEnvelope(time: Double, duration: Double): Double {
            val attackTime = 0.4   // Faster attack for shorter notes
            val sustainStart = attackTime
            val releaseStart = duration - 0.6  // Start fade 0.6 seconds before end

            return when {
                time < attackTime -> {
                    val t = time / attackTime
                    t * t * (3.0 - 2.0 * t)
                }
                time < releaseStart -> {
                    1.0 + 0.01 * sin(2 * PI * 0.3 * time)
                }
                time < duration -> {
                    val t = (duration - time) / (duration - releaseStart)
                    t * t * (3.0 - 2.0 * t)
                }
                else -> 0.0
            }
        }

        private fun stopCurrentNote() {
            try {
                isPlaying = false
                playbackThread?.interrupt()
                try {
                    playbackThread?.join(50) // Reduced wait time
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
                cleanupAudioTrack()
            } catch (e: Exception) {
                Log.e("FluteSynthesizer", "Error stopping current note", e)
            }
        }

        private fun cleanupAudioTrack() {
            try {
                audioTrack?.apply {
                    if (state == AudioTrack.STATE_INITIALIZED) {
                        stop()
                    }
                    release()
                }
            } catch (e: Exception) {
                Log.e("FluteSynthesizer", "Error cleaning up audio track", e)
            } finally {
                audioTrack = null
                isPlaying = false
            }
        }

        fun release() {
            try {
                stopCurrentNote()
            } catch (e: Exception) {
                Log.e("FluteSynthesizer", "Error releasing synthesizer", e)
            }
        }
    }
}