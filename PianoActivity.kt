package com.marwadiuniversity.abckids

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.util.Log

class PianoActivity : Activity() {

    private var backgroundMusicPlayer: MediaPlayer? = null
    private var isBackgroundPlaying = false
    private var currentMusicTrack = -1
    private var selectedMusicButton: TextView? = null
    private val musicButtons = mutableListOf<TextView>()

    // Piano sound players - reuse to avoid memory issues
    private val pianoPlayers = mutableMapOf<String, MediaPlayer?>()

    // Piano keys references for drag detection
    private val pianoKeys = mutableListOf<TextView>()
    private var lastTouchedKey: TextView? = null
    private var isDragging = false

    // Store original backgrounds for each key
    private val keyOriginalBackgrounds = mutableMapOf<TextView, GradientDrawable>()

    private val noteFiles = mapOf(
        "C" to R.raw.piano_c,
        "D" to R.raw.piano_d,
        "E" to R.raw.piano_e,
        "F" to R.raw.piano_f,
        "G" to R.raw.piano_g,
        "A" to R.raw.piano_a,
        "B" to R.raw.piano_b,
        "C2" to R.raw.piano_c2
    )

    private val musicTracks = listOf(
        R.raw.happy_song,
        R.raw.classical_song,
        R.raw.kids_song,
        R.raw.lullaby_song,
        R.raw.upbeat_song,
        R.raw.calm_song
    )

    private val buttonColors = listOf("#FF4444", "#44FF44", "#AA44FF", "#44AAFF", "#FFAA44", "#FF44AA")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            // Initialize piano sound players
            initializePianoPlayers()

            setupUI()
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error in onCreate", e)
            finish()
        }
    }

    private fun initializePianoPlayers() {
        noteFiles.forEach { (note, resourceId) ->
            try {
                val mediaPlayer = MediaPlayer.create(this, resourceId)
                if (mediaPlayer != null) {
                    mediaPlayer.setOnCompletionListener {
                        try {
                            it.seekTo(0) // Reset to beginning for next play
                        } catch (e: Exception) {
                            Log.e("PianoActivity", "Error resetting player for $note", e)
                        }
                    }
                    pianoPlayers[note] = mediaPlayer
                } else {
                    Log.w("PianoActivity", "Could not create MediaPlayer for $note")
                    pianoPlayers[note] = null
                }
            } catch (e: Exception) {
                Log.e("PianoActivity", "Error initializing sound for $note", e)
                pianoPlayers[note] = null
            }
        }
    }

    private fun setupUI() {
        try {
            val mainContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.parseColor("#8A2BE2")) // Purple background
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            // Create header
            val header = createHeader()
            mainContainer.addView(header)

            // Create piano container with rounded corners and gradient
            val pianoContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                val drawable = GradientDrawable().apply {
                    cornerRadius = 30f
                    colors = intArrayOf(
                        Color.parseColor("#FFD700"), // Gold
                        Color.parseColor("#FFA500")  // Orange
                    )
                }
                background = drawable
                setPadding(25, 25, 25, 25)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1f // Take remaining space after header
                ).apply {
                    setMargins(40, 0, 40, 30)
                }
            }

            // Top control buttons
            val topButtonsContainer = createTopButtons()
            pianoContainer.addView(topButtonsContainer)

            // Piano keys container
            val keysContainer = createPianoKeys()
            pianoContainer.addView(keysContainer)

            mainContainer.addView(pianoContainer)
            setContentView(mainContainer)
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error setting up UI", e)
        }
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
                id = View.generateViewId() // equivalent to android:id="@+id/btn_back"
                layoutParams = LinearLayout.LayoutParams(
                    dpToPx(40),
                    dpToPx(40)
                )
                setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))

                // Set back arrow as text
                text = "←"
                textSize = 20f
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER

                // No background - just the arrow on teal background
                background = null

                // Add ripple effect equivalent to selectableItemBackgroundBorderless
                isClickable = true
                isFocusable = true

                setOnClickListener {
                    try {
                        finish() // Close activity and go back
                    } catch (e: Exception) {
                        Log.e("PianoActivity", "Error handling back button", e)
                    }
                }
            }

            // You can add a title or other header content here if needed
            val spacer = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f // Take remaining space
                )
            }

            headerContainer.addView(backButton)
            headerContainer.addView(spacer)

        } catch (e: Exception) {
            Log.e("PianoActivity", "Error creating header", e)
        }

        return headerContainer
    }

    private fun createTopButtons(): LinearLayout {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 20
            }
        }

        try {
            // Speaker grilles on sides
            val leftSpeaker = createSpeakerGrille()
            val rightSpeaker = createSpeakerGrille()

            // Control buttons
            val buttonIcons = listOf("×", "♪", "♫", "♪", "♫", "♪")

            val buttonsContainer = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            buttonColors.forEachIndexed { index, color ->
                val button = createControlButton(color, buttonIcons[index], index)
                buttonsContainer.addView(button)
                musicButtons.add(button)
            }

            container.addView(leftSpeaker)
            container.addView(buttonsContainer)
            container.addView(rightSpeaker)
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error creating top buttons", e)
        }

        return container
    }

    private fun createSpeakerGrille(): LinearLayout {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(60),
                dpToPx(80)
            )
            gravity = Gravity.CENTER
        }

        try {
            repeat(6) {
                val line = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        dpToPx(40),
                        dpToPx(4)
                    ).apply {
                        setMargins(0, dpToPx(2), 0, dpToPx(2))
                    }
                    setBackgroundColor(Color.parseColor("#8B4513"))
                }
                container.addView(line)
            }
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error creating speaker grille", e)
        }

        return container
    }

    private fun createControlButton(colorHex: String, icon: String, index: Int): TextView {
        return TextView(this).apply {
            text = icon
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            tag = index // Store index for later reference
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(50),
                dpToPx(50)
            ).apply {
                setMargins(dpToPx(5), 0, dpToPx(5), 0)
            }

            updateButtonAppearance(colorHex, false)

            setOnTouchListener { v, event ->
                try {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            scaleX = 0.9f
                            scaleY = 0.9f
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            scaleX = 1.0f
                            scaleY = 1.0f
                            handleMusicButtonClick(this, index, colorHex)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("PianoActivity", "Error handling button touch", e)
                }
                true
            }
        }
    }

    private fun TextView.updateButtonAppearance(colorHex: String, isSelected: Boolean) {
        try {
            val drawable = GradientDrawable().apply {
                cornerRadius = 15f
                if (isSelected) {
                    setColor(Color.WHITE)
                    setStroke(4, Color.parseColor(colorHex))
                } else {
                    setColor(Color.parseColor(colorHex))
                    setStroke(3, Color.parseColor("#FF8C00"))
                }
            }
            background = drawable
            setTextColor(if (isSelected) Color.parseColor(colorHex) else Color.WHITE)
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error updating button appearance", e)
        }
    }

    private fun handleMusicButtonClick(button: TextView, buttonIndex: Int, colorHex: String) {
        try {
            if (buttonIndex == 0) {
                // Stop button clicked
                stopBackgroundMusic()
                return
            }

            val musicIndex = buttonIndex - 1
            if (currentMusicTrack == musicIndex && isBackgroundPlaying) {
                // Currently playing track button clicked - stop it
                stopBackgroundMusic()
            } else {
                // Different track or no music playing - start new track
                stopBackgroundMusic()
                playBackgroundMusic(musicIndex, button, colorHex)
            }
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error handling music button click", e)
        }
    }

    private fun playBackgroundMusic(musicIndex: Int, button: TextView, colorHex: String) {
        try {
            if (musicIndex < musicTracks.size) {
                backgroundMusicPlayer = MediaPlayer.create(this, musicTracks[musicIndex])
                backgroundMusicPlayer?.let { player ->
                    player.isLooping = true
                    player.setOnPreparedListener {
                        try {
                            it.start()
                            isBackgroundPlaying = true
                            currentMusicTrack = musicIndex

                            // Update button appearance - reset previous button
                            selectedMusicButton?.let { prevButton ->
                                val prevIndex = prevButton.tag as? Int ?: 0
                                prevButton.updateButtonAppearance(getButtonColorByIndex(prevIndex), false)
                            }

                            // Update current button
                            button.updateButtonAppearance(colorHex, true)
                            selectedMusicButton = button
                        } catch (e: Exception) {
                            Log.e("PianoActivity", "Error starting background music", e)
                        }
                    }
                    player.setOnErrorListener { _, what, extra ->
                        Log.e("PianoActivity", "MediaPlayer error: what=$what, extra=$extra")
                        stopBackgroundMusic()
                        true
                    }
                } ?: run {
                    Log.e("PianoActivity", "Could not create MediaPlayer for track $musicIndex")
                }
            }
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error starting background music", e)
            stopBackgroundMusic()
        }
    }

    private fun stopBackgroundMusic() {
        try {
            backgroundMusicPlayer?.let {
                try {
                    if (it.isPlaying) {
                        it.stop()
                    }
                    it.release()
                } catch (e: Exception) {
                    Log.e("PianoActivity", "Error stopping background music", e)
                }
            }
            backgroundMusicPlayer = null
            isBackgroundPlaying = false

            // Reset button appearance
            selectedMusicButton?.let { button ->
                val index = button.tag as? Int ?: 0
                button.updateButtonAppearance(getButtonColorByIndex(index), false)
            }
            selectedMusicButton = null
            currentMusicTrack = -1
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error in stopBackgroundMusic", e)
        }
    }

    private fun getButtonColorByIndex(index: Int): String {
        return if (index in buttonColors.indices) buttonColors[index] else "#FF4444"
    }

    private fun createPianoKeys(): LinearLayout {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )

            try {
                val drawable = GradientDrawable().apply {
                    // Set corner radius for curved corners
                    cornerRadii = floatArrayOf(
                        dpToPx(25).toFloat(), dpToPx(25).toFloat(), // top left
                        dpToPx(25).toFloat(), dpToPx(25).toFloat(), // top right
                        dpToPx(8).toFloat(), dpToPx(8).toFloat(),   // bottom right
                        dpToPx(8).toFloat(), dpToPx(8).toFloat()    // bottom left
                    )
                    colors = intArrayOf(
                        Color.parseColor("#FF4500"),
                        Color.parseColor("#FF6347")
                    )
                }
                background = drawable
                setPadding(15, 15, 15, 15)
            } catch (e: Exception) {
                Log.e("PianoActivity", "Error setting up piano keys container", e)
            }
        }

        try {
            val notes = listOf("C", "D", "E", "F", "G", "A", "B", "C2")
            val keyColors = listOf(
                "#FF4444", "#FF8844", "#FFFF44", "#88FF88",
                "#44FF44", "#44FFFF", "#4488FF", "#8844FF"
            )

            notes.forEachIndexed { index, note ->
                val key = createPianoKey(note, keyColors[index])
                container.addView(key)
                pianoKeys.add(key) // Add to our list for drag detection
            }

            // Set up global touch listener for drag detection
            setupGlobalTouchListener(container)
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error creating piano keys", e)
        }

        return container
    }

    private fun setupGlobalTouchListener(container: LinearLayout) {
        container.setOnTouchListener { _, event ->
            try {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isDragging = true
                        handleTouchAtPosition(event.x, event.y, true)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (isDragging) {
                            handleTouchAtPosition(event.x, event.y, false)
                        }
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        isDragging = false
                        resetAllKeysVisual()
                        lastTouchedKey = null
                    }
                }
            } catch (e: Exception) {
                Log.e("PianoActivity", "Error in global touch listener", e)
            }
            true
        }
    }

    private fun handleTouchAtPosition(x: Float, y: Float, isInitialTouch: Boolean) {
        try {
            // Find which key is at this position
            val touchedKey = findKeyAtPosition(x, y)

            if (touchedKey != null && touchedKey != lastTouchedKey) {
                // Reset previous key visual if exists
                lastTouchedKey?.resetKeyVisual()

                // Play sound and apply visual effect to new key
                val note = touchedKey.text.toString()
                playPianoNote(note)
                touchedKey.applyPressedVisual()
                touchedKey.addSparkleEffect()

                lastTouchedKey = touchedKey
            }
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error handling touch at position", e)
        }
    }

    private fun findKeyAtPosition(x: Float, y: Float): TextView? {
        try {
            pianoKeys.forEach { key ->
                val location = IntArray(2)
                key.getLocationOnScreen(location)
                val keyLeft = location[0]
                val keyTop = location[1]
                val keyRight = keyLeft + key.width
                val keyBottom = keyTop + key.height

                // Convert screen coordinates to relative coordinates
                val containerLocation = IntArray(2)
                key.parent?.let { parent ->
                    (parent as View).getLocationOnScreen(containerLocation)
                    val relativeX = x + containerLocation[0]
                    val relativeY = y + containerLocation[1]

                    if (relativeX >= keyLeft && relativeX <= keyRight &&
                        relativeY >= keyTop && relativeY <= keyBottom) {
                        return key
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error finding key at position", e)
        }
        return null
    }

    private fun createPianoKey(note: String, colorHex: String): TextView {
        return TextView(this).apply {
            text = note
            textSize = 24f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            ).apply {
                setMargins(dpToPx(4), 0, dpToPx(4), 0)
            }

            // Store original color in tag for reference
            tag = colorHex

            try {
                val drawable = GradientDrawable().apply {
                    cornerRadius = 15f
                    colors = intArrayOf(
                        Color.parseColor(colorHex),
                        adjustBrightness(Color.parseColor(colorHex), 0.8f)
                    )
                    setStroke(2, Color.WHITE)
                }
                background = drawable
                // Store the original background for later restoration
                keyOriginalBackgrounds[this] = drawable
            } catch (e: Exception) {
                Log.e("PianoActivity", "Error setting up piano key background", e)
            }

            // Individual key touch listener (fallback for single touches)
            setOnTouchListener { v, event ->
                try {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            if (!isDragging) {
                                applyPressedVisual()
                                playPianoNote(note)
                                addSparkleEffect()
                            }
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            if (!isDragging) {
                                resetKeyVisual()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("PianoActivity", "Error handling piano key touch", e)
                }
                false // Allow parent to handle drag events
            }
        }
    }

    private fun TextView.applyPressedVisual() {
        try {
            scaleX = 0.95f
            scaleY = 0.95f
            alpha = 0.8f
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error applying pressed visual", e)
        }
    }

    private fun TextView.resetKeyVisual() {
        try {
            scaleX = 1.0f
            scaleY = 1.0f
            alpha = 1.0f
            // Restore the original background
            keyOriginalBackgrounds[this]?.let { originalBackground ->
                background = originalBackground
            }
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error resetting key visual", e)
        }
    }

    private fun resetAllKeysVisual() {
        try {
            pianoKeys.forEach { key ->
                key.resetKeyVisual()
            }
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error resetting all keys visual", e)
        }
    }

    private fun playPianoNote(note: String) {
        try {
            pianoPlayers[note]?.let { player ->
                if (player.isPlaying) {
                    player.seekTo(0)
                } else {
                    player.start()
                }
            } ?: run {
                Log.w("PianoActivity", "No player available for note: $note")
            }
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error playing piano note: $note", e)
        }
    }

    private fun TextView.addSparkleEffect() {
        try {
            // Store the current background before applying sparkle
            val currentBackground = background

            // Create sparkle effect
            val sparkleDrawable = GradientDrawable().apply {
                cornerRadius = 15f
                setColor(Color.WHITE)
                alpha = 180
            }

            background = sparkleDrawable

            // Restore the original background after delay
            postDelayed({
                try {
                    // Restore the original gradient background, not the current one
                    keyOriginalBackgrounds[this]?.let { originalBackground ->
                        background = originalBackground
                    }
                } catch (e: Exception) {
                    Log.e("PianoActivity", "Error resetting sparkle effect", e)
                    // Fallback: restore whatever background was there before
                    background = currentBackground
                }
            }, 100)
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error adding sparkle effect", e)
        }
    }

    private fun adjustBrightness(color: Int, factor: Float): Int {
        return try {
            val r = (Color.red(color) * factor).toInt().coerceIn(0, 255)
            val g = (Color.green(color) * factor).toInt().coerceIn(0, 255)
            val b = (Color.blue(color) * factor).toInt().coerceIn(0, 255)
            Color.rgb(r, g, b)
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error adjusting brightness", e)
            color // Return original color on error
        }
    }

    private fun dpToPx(dp: Int): Int {
        return try {
            (dp * resources.displayMetrics.density).toInt()
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error converting dp to px", e)
            dp // Return dp value as fallback
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            // Pause background music when activity is paused
            backgroundMusicPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                }
            }
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error in onPause", e)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            // Resume background music if it was playing
            if (isBackgroundPlaying && backgroundMusicPlayer != null) {
                backgroundMusicPlayer?.start()
            }
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error in onResume", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            // Clean up background music
            stopBackgroundMusic()

            // Clean up piano sounds
            pianoPlayers.values.forEach { player ->
                try {
                    player?.release()
                } catch (e: Exception) {
                    Log.e("PianoActivity", "Error releasing piano player", e)
                }
            }
            pianoPlayers.clear()
            musicButtons.clear()
            pianoKeys.clear()
            keyOriginalBackgrounds.clear()
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error in onDestroy", e)
        }
    }
}