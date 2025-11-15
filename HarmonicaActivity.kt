package com.marwadiuniversity.abckids

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.marwadiuniversity.abckids.utils.InstrumentSynthesizer

class HarmonicaActivity : AppCompatActivity() {

    private lateinit var synthesizer: InstrumentSynthesizer
    private val harmonicaButtons = mutableListOf<Button>()
    private val buttonOriginalBackgrounds = mutableMapOf<Button, GradientDrawable>()
    private val pressedButtons = mutableSetOf<Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            synthesizer = InstrumentSynthesizer(this)
            setContentView(createHarmonicaLayout())
        } catch (e: Exception) {
            Log.e("HarmonicaActivity", "Error in onCreate", e)
            finish()
        }
    }

    private fun createHarmonicaLayout(): LinearLayout {
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            // Enhanced vibrant background with gradient
            background = createMainBackground()
        }

        try {
            // Create header
            val header = createHeader()
            mainLayout.addView(header)

            // Main harmonica content container
            val contentContainer = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1f // Take remaining space after header
                )
                gravity = Gravity.CENTER
            }

            // Main harmonica area
            val harmonicaContainer = createEnhancedHarmonicaLandscape().apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(30, 20, 30, 40)
                }
            }
            contentContainer.addView(harmonicaContainer)

            mainLayout.addView(contentContainer)
        } catch (e: Exception) {
            Log.e("HarmonicaActivity", "Error creating layout", e)
        }

        return mainLayout
    }

    private fun createMainBackground(): GradientDrawable = GradientDrawable().apply {
        orientation = GradientDrawable.Orientation.TOP_BOTTOM
        colors = intArrayOf(
            Color.parseColor("#E1BEE7"), // Light purple
            Color.parseColor("#CE93D8"), // Medium purple
            Color.parseColor("#BA68C8")  // Darker purple
        )
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
                        Log.e("HarmonicaActivity", "Error handling back button", e)
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
            Log.e("HarmonicaActivity", "Error creating header", e)
        }

        return headerContainer
    }

    private fun createEnhancedHarmonicaLandscape(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(25, 35, 25, 35)
            background = createEnhancedHarmonicaBodyBackground()
            gravity = Gravity.CENTER
            elevation = 25f

            try {
                // Enhanced top cover with colorful screws
                val topCover = LinearLayout(this@HarmonicaActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        100
                    )
                    orientation = LinearLayout.HORIZONTAL
                    background = createEnhancedMetallicBackground()
                    gravity = Gravity.CENTER
                    setPadding(20, 15, 20, 15)
                }

                val screwColors = listOf("#FF5722", "#4CAF50", "#2196F3", "#FF9800")
                repeat(4) { index ->
                    val screw = TextView(this@HarmonicaActivity).apply {
                        layoutParams = LinearLayout.LayoutParams(25, 25).apply {
                            setMargins(12, 0, 12, 0)
                        }
                        text = "●"
                        textSize = 16f
                        setTextColor(Color.parseColor(screwColors[index]))
                        gravity = Gravity.CENTER
                        background = createScrewBackground()
                    }
                    topCover.addView(screw)
                }

                addView(topCover)

                // Enhanced playing area with vibrant colors
                val playingArea = LinearLayout(this@HarmonicaActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        280
                    ).apply {
                        topMargin = 15
                        bottomMargin = 15
                    }
                    orientation = LinearLayout.HORIZONTAL
                    background = createPlayingAreaBackground()
                    setPadding(8, 25, 8, 25)
                    gravity = Gravity.CENTER
                }

                val harmonicaNotes = listOf(
                    Pair(1, "G"), Pair(2, "B"), Pair(3, "D"), Pair(4, "G"),
                    Pair(5, "B"), Pair(6, "D"), Pair(7, "F#"), Pair(8, "A")
                )

                val buttonColors = listOf(
                    "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4",
                    "#FECA57", "#FF9FF3", "#54A0FF", "#5F27CD"
                )

                harmonicaNotes.forEachIndexed { index, (number, note) ->
                    val holeButton = Button(this@HarmonicaActivity).apply {
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).apply {
                            setMargins(6, 0, 6, 0)
                        }
                        text = "$number\n$note"
                        textSize = 20f
                        setTextColor(Color.WHITE)
                        typeface = Typeface.DEFAULT_BOLD

                        val buttonBackground = createEnhancedHoleBackground(buttonColors[index])
                        background = buttonBackground
                        buttonOriginalBackgrounds[this] = buttonBackground
                        elevation = 12f

                        // Store note in tag for multi-touch handling
                        tag = note

                        // Enhanced touch handling for multi-touch support
                        setOnTouchListener { v, event ->
                            handleButtonTouch(this, event, note)
                        }
                    }
                    harmonicaButtons.add(holeButton)
                    playingArea.addView(holeButton)
                }

                // Set up multi-touch listener on the playing area
                setupMultiTouchListener(playingArea)
                addView(playingArea)

                // Enhanced bottom decoration
                val bottomCover = LinearLayout(this@HarmonicaActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        80
                    )
                    background = createEnhancedBottomBackground()
                    gravity = Gravity.CENTER
                }

                // Add decorative elements to bottom
                repeat(6) { index ->
                    val decoration = View(this@HarmonicaActivity).apply {
                        layoutParams = LinearLayout.LayoutParams(8, 40).apply {
                            setMargins(5, 0, 5, 0)
                        }
                        setBackgroundColor(Color.parseColor("#37474F"))
                    }
                    bottomCover.addView(decoration)
                }

                addView(bottomCover)
            } catch (e: Exception) {
                Log.e("HarmonicaActivity", "Error creating harmonica layout", e)
            }
        }
    }

    private fun setupMultiTouchListener(playingArea: LinearLayout) {
        playingArea.setOnTouchListener { _, event ->
            try {
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                        val pointerIndex = event.actionIndex
                        val x = event.getX(pointerIndex)
                        val y = event.getY(pointerIndex)
                        findButtonAtPosition(x, y)?.let { button ->
                            if (!pressedButtons.contains(button)) {
                                handleButtonPress(button, true)
                            }
                        }
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                        val pointerIndex = event.actionIndex
                        val x = event.getX(pointerIndex)
                        val y = event.getY(pointerIndex)
                        findButtonAtPosition(x, y)?.let { button ->
                            handleButtonPress(button, false)
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        // Handle drag across multiple buttons
                        for (i in 0 until event.pointerCount) {
                            val x = event.getX(i)
                            val y = event.getY(i)
                            findButtonAtPosition(x, y)?.let { button ->
                                if (!pressedButtons.contains(button)) {
                                    handleButtonPress(button, true)
                                }
                            }
                        }
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        // Release all pressed buttons
                        pressedButtons.toList().forEach { button ->
                            handleButtonPress(button, false)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("HarmonicaActivity", "Error in multi-touch listener", e)
            }
            true
        }
    }

    private fun findButtonAtPosition(x: Float, y: Float): Button? {
        try {
            harmonicaButtons.forEach { button ->
                val location = IntArray(2)
                button.getLocationInWindow(location)
                val buttonLeft = location[0]
                val buttonTop = location[1]
                val buttonRight = buttonLeft + button.width
                val buttonBottom = buttonTop + button.height

                if (x >= buttonLeft && x <= buttonRight && y >= buttonTop && y <= buttonBottom) {
                    return button
                }
            }
        } catch (e: Exception) {
            Log.e("HarmonicaActivity", "Error finding button at position", e)
        }
        return null
    }

    private fun handleButtonTouch(button: Button, event: MotionEvent, note: String): Boolean {
        try {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!pressedButtons.contains(button)) {
                        handleButtonPress(button, true)
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    handleButtonPress(button, false)
                }
            }
        } catch (e: Exception) {
            Log.e("HarmonicaActivity", "Error handling button touch", e)
        }
        return true
    }

    private fun handleButtonPress(button: Button, isPressed: Boolean) {
        try {
            if (isPressed) {
                if (!pressedButtons.contains(button)) {
                    pressedButtons.add(button)
                    button.applyPressedVisual()
                    button.addSparkleEffect()
                    val note = button.tag as? String
                    note?.let { synthesizer.playHarmonicaSound(it) }
                }
            } else {
                if (pressedButtons.contains(button)) {
                    pressedButtons.remove(button)
                    button.resetButtonVisual()
                }
            }
        } catch (e: Exception) {
            Log.e("HarmonicaActivity", "Error handling button press", e)
        }
    }

    private fun Button.applyPressedVisual() {
        try {
            scaleX = 0.95f
            scaleY = 0.95f
            alpha = 0.8f
        } catch (e: Exception) {
            Log.e("HarmonicaActivity", "Error applying pressed visual", e)
        }
    }

    private fun Button.resetButtonVisual() {
        try {
            scaleX = 1.0f
            scaleY = 1.0f
            alpha = 1.0f
            buttonOriginalBackgrounds[this]?.let { originalBackground ->
                background = originalBackground
            }
        } catch (e: Exception) {
            Log.e("HarmonicaActivity", "Error resetting button visual", e)
        }
    }

    private fun Button.addSparkleEffect() {
        try {
            val currentBackground = background

            val sparkleDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.WHITE)
                cornerRadius = 15f
                alpha = 200
            }

            background = sparkleDrawable

            postDelayed({
                try {
                    buttonOriginalBackgrounds[this]?.let { originalBackground ->
                        background = originalBackground
                    }
                } catch (e: Exception) {
                    Log.e("HarmonicaActivity", "Error resetting sparkle effect", e)
                    background = currentBackground
                }
            }, 150)
        } catch (e: Exception) {
            Log.e("HarmonicaActivity", "Error adding sparkle effect", e)
        }
    }

    private fun createEnhancedHarmonicaBodyBackground(): GradientDrawable = GradientDrawable().apply {
        try {
            orientation = GradientDrawable.Orientation.TOP_BOTTOM
            colors = intArrayOf(
                Color.parseColor("#2C3E50"), // Dark blue-gray
                Color.parseColor("#34495E"), // Medium blue-gray
                Color.parseColor("#2C3E50")  // Dark blue-gray
            )
            cornerRadius = 25f
            setStroke(4, Color.parseColor("#1ABC9C")) // Teal border
        } catch (e: Exception) {
            Log.e("HarmonicaActivity", "Error creating harmonica body background", e)
        }
    }

    private fun createEnhancedMetallicBackground(): GradientDrawable = GradientDrawable().apply {
        try {
            orientation = GradientDrawable.Orientation.TOP_BOTTOM
            colors = intArrayOf(
                Color.parseColor("#ECF0F1"), // Light gray
                Color.parseColor("#BDC3C7"), // Medium gray
                Color.parseColor("#95A5A6")  // Darker gray
            )
            cornerRadius = 12f
            setStroke(2, Color.parseColor("#7F8C8D"))
        } catch (e: Exception) {
            Log.e("HarmonicaActivity", "Error creating metallic background", e)
        }
    }

    private fun createScrewBackground(): GradientDrawable = GradientDrawable().apply {
        try {
            shape = GradientDrawable.OVAL
            colors = intArrayOf(
                Color.parseColor("#ECF0F1"),
                Color.parseColor("#95A5A6")
            )
            setStroke(1, Color.parseColor("#7F8C8D"))
        } catch (e: Exception) {
            Log.e("HarmonicaActivity", "Error creating screw background", e)
        }
    }

    private fun createPlayingAreaBackground(): GradientDrawable = GradientDrawable().apply {
        try {
            orientation = GradientDrawable.Orientation.TOP_BOTTOM
            colors = intArrayOf(
                Color.parseColor("#F39C12"), // Orange
                Color.parseColor("#E67E22"), // Darker orange
                Color.parseColor("#D35400")  // Deep orange
            )
            cornerRadius = 15f
            setStroke(3, Color.parseColor("#A0522D"))
        } catch (e: Exception) {
            Log.e("HarmonicaActivity", "Error creating playing area background", e)
        }
    }

    private fun createEnhancedHoleBackground(colorHex: String): GradientDrawable = GradientDrawable().apply {
        try {
            shape = GradientDrawable.RECTANGLE
            val baseColor = Color.parseColor(colorHex)
            colors = intArrayOf(
                baseColor,
                adjustBrightness(baseColor, 0.7f)
            )
            cornerRadius = 12f
            setStroke(2, Color.WHITE)
        } catch (e: Exception) {
            Log.e("HarmonicaActivity", "Error creating hole background", e)
        }
    }

    private fun createEnhancedBottomBackground(): GradientDrawable = GradientDrawable().apply {
        try {
            orientation = GradientDrawable.Orientation.TOP_BOTTOM
            colors = intArrayOf(
                Color.parseColor("#95A5A6"), // Light gray
                Color.parseColor("#7F8C8D"), // Medium gray
                Color.parseColor("#95A5A6")  // Light gray
            )
            cornerRadius = 12f
        } catch (e: Exception) {
            Log.e("HarmonicaActivity", "Error creating bottom background", e)
        }
    }

    private fun adjustBrightness(color: Int, factor: Float): Int {
        return try {
            val r = (Color.red(color) * factor).toInt().coerceIn(0, 255)
            val g = (Color.green(color) * factor).toInt().coerceIn(0, 255)
            val b = (Color.blue(color) * factor).toInt().coerceIn(0, 255)
            Color.rgb(r, g, b)
        } catch (e: Exception) {
            Log.e("HarmonicaActivity", "Error adjusting brightness", e)
            color
        }
    }

    private fun dpToPx(dp: Int): Int {
        return try {
            (dp * resources.displayMetrics.density).toInt()
        } catch (e: Exception) {
            Log.e("HarmonicaActivity", "Error converting dp to px", e)
            dp
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            synthesizer.release()
            harmonicaButtons.clear()
            buttonOriginalBackgrounds.clear()
            pressedButtons.clear()
        } catch (e: Exception) {
            Log.e("HarmonicaActivity", "Error in onDestroy", e)
        }
    }
}