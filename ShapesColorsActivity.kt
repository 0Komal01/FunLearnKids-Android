package com.marwadiuniversity.abckids

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.AnimatorSet
import android.view.View
import android.content.pm.ActivityInfo
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.Gravity
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.util.*

class ShapesColorsActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var btnBack: ImageView
    private lateinit var tabShapes: CardView
    private lateinit var tabColors: CardView
    private lateinit var contentContainer: GridLayout
    private lateinit var progressText: TextView
    private lateinit var mainScrollView: ScrollView

    private var textToSpeech: TextToSpeech? = null
    private var isTTSReady = false
    private var isCurrentlySpeaking = false
    private var currentTab = "shapes"
    private val mainHandler = Handler(Looper.getMainLooper())
    private var shouldAutoSpeakOnInit = true

    // Enhanced shapes data with uniform sizing
    private val shapesData = listOf(
        ShapeItem("Circle", "●", "#FF6B6B", "#FF8E8E", 85f),
        ShapeItem("Square", "■", "#4ECDC4", "#7EDDD7", 75f),
        ShapeItem("Triangle", "▲", "#45B7D1", "#74C7E3", 90f),
        ShapeItem("Rectangle", "▬", "#96CEB4", "#B8DBC4", 70f),
        ShapeItem("Star", "★", "#FFEAA7", "#FFF2C7", 80f),
        ShapeItem("Heart", "♥", "#FD79A8", "#FE9BB8", 80f),
        ShapeItem("Diamond", "♦", "#FDCB6E", "#FEDC8E", 85f),
        ShapeItem("Oval", "⬭", "#6C5CE7", "#8B7EEA", 75f),
        ShapeItem("Pentagon", "⬟", "#A29BFE", "#B8B2FE", 80f),
        ShapeItem("Hexagon", "⬢", "#74B9FF", "#95C9FF", 80f),
        ShapeItem("Moon", "☽", "#81ECEC", "#A1F0F0", 85f),
        ShapeItem("Arrow", "→", "#00B894", "#33C7A9", 75f)
    )

    // Enhanced colors with gradients and better descriptions
    private val colorsData = listOf(
        ColorItem("Red", "#E91E63", "#E74C3C", "🔴", "Like a juicy strawberry!"),
        ColorItem("Blue", "#2980B9", "#3498DB", "🔵", "Like the beautiful sky!"),
        ColorItem("Green", "#27AE60", "#2ECC71", "🟢", "Like fresh grass!"),
        ColorItem("Yellow", "#F1C40F", "#F9E79F", "🟡", "Like bright sunshine!"),
        ColorItem("Orange", "#D35400", "#E67E22", "🟠", "Like a sweet orange!"),
        ColorItem("Purple", "#8E44AD", "#9B59B6", "🟣", "Like magical flowers!"),
        ColorItem("Pink", "#C2185B", "#E91E63", "🩷", "Like cotton candy!"),
        ColorItem("Brown", "#4E342E", "#6D4C41", "🟤", "Like chocolate cake!"),
        ColorItem("Black", "#000000", "#2C3E50", "⚫", "Like a moonless night!"),
        ColorItem("White", "#FFFFFF", "#ECF0F1", "⚪", "Like fluffy clouds!"),
        ColorItem("Gold", "#B7950B", "#F1C40F", "🥇", "Like a shining medal!"),
        ColorItem("Silver", "#7F8C8D", "#BDC3C7", "🥈", "Like sparkling stars!")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Force portrait orientation
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            // Hide action bar
            supportActionBar?.hide()

            setContentView(R.layout.activity_shapes_colors)

            initViews()
            setupBeautifulBackground()
            initTextToSpeech()
            showShapes()

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupBeautifulBackground() {
        // Create a beautiful gradient background compatible with teal header
        val gradientDrawable = GradientDrawable().apply {
            colors = intArrayOf(
                Color.parseColor("#E8F8F5"), // Very light mint - complements teal header
                Color.parseColor("#A7E6D7"), // Soft aqua
                Color.parseColor("#52D4A3")  // Medium mint green
            )
            orientation = GradientDrawable.Orientation.TOP_BOTTOM
        }
        mainScrollView.background = gradientDrawable
    }

    private fun initViews() {
        try {
            btnBack = findViewById(R.id.btn_back)
            tabShapes = findViewById(R.id.tab_shapes)
            tabColors = findViewById(R.id.tab_colors)
            contentContainer = findViewById(R.id.content_container)
            progressText = findViewById(R.id.progress_text)
            mainScrollView = findViewById(R.id.main_scroll_view)

            setupClickListeners()
            enhanceTabsAppearance()

        } catch (e: Exception) {
            Toast.makeText(this, "View init error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun enhanceTabsAppearance() {
        // Enhance tabs with better styling
        tabShapes.apply {
            radius = 30f
            cardElevation = 12f
            useCompatPadding = true
        }

        tabColors.apply {
            radius = 30f
            cardElevation = 12f
            useCompatPadding = true
        }

        // Style the progress text
        progressText.apply {
            textSize = 20f
            setTextColor(Color.WHITE)
            setPadding(0, 16, 0, 16)
        }
    }

    private fun setupClickListeners() {
        // Back button
        btnBack.setOnClickListener {
            finish()
        }

        // Tab listeners with enhanced animations
        tabShapes.setOnClickListener {
            if (currentTab != "shapes") {
                currentTab = "shapes"
                updateTabs()
                showShapes()
                speakText("Shapes")
            }
        }

        tabColors.setOnClickListener {
            if (currentTab != "colors") {
                currentTab = "colors"
                updateTabs()
                showColors()
                speakText("Colors")
            }
        }
    }

    private fun updateTabs() {
        if (currentTab == "shapes") {
            // Beautiful gradient for active shapes tab
            val shapesGradient = createBeautifulGradient("#4CAF50", "#66BB6A")
            val colorsGradient = createBeautifulGradient("#F5F5F5", "#EEEEEE")

            tabShapes.background = shapesGradient
            tabColors.background = colorsGradient
            progressText.text = "✨ Learning Shapes! ✨"
        } else {
            val shapesGradient = createBeautifulGradient("#F5F5F5", "#EEEEEE")
            val colorsGradient = createBeautifulGradient("#FF9800", "#FFB74D")

            tabShapes.background = shapesGradient
            tabColors.background = colorsGradient
            progressText.text = "🌈 Learning Colors! 🌈"
        }

        // Animate tab change with glow effect
        val activeTab = if (currentTab == "shapes") tabShapes else tabColors
        animateTabSelection(activeTab)
    }

    private fun createBeautifulGradient(startColor: String, endColor: String): GradientDrawable {
        return GradientDrawable().apply {
            colors = intArrayOf(
                Color.parseColor(startColor),
                Color.parseColor(endColor)
            )
            orientation = GradientDrawable.Orientation.LEFT_RIGHT
            cornerRadius = 30f
        }
    }

    private fun animateTabSelection(tab: CardView) {
        // Enhanced tab animation with glow effect
        tab.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(200)
            .withEndAction {
                tab.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .start()
            }
            .start()

        // Add elevation animation for glow effect
        val elevationAnimator = ObjectAnimator.ofFloat(tab, "cardElevation", 12f, 20f, 12f)
        elevationAnimator.duration = 400
        elevationAnimator.start()
    }

    private fun showShapes() {
        contentContainer.removeAllViews()
        contentContainer.columnCount = 1

        shapesData.forEachIndexed { index, shapeItem ->
            val card = createBeautifulShapeCard(shapeItem)
            val params = GridLayout.LayoutParams().apply {
                width = GridLayout.LayoutParams.MATCH_PARENT
                height = GridLayout.LayoutParams.WRAP_CONTENT
                setMargins(20, 16, 20, 16)
            }
            card.layoutParams = params
            contentContainer.addView(card)

            // Enhanced entrance animation
            card.scaleX = 0.2f
            card.scaleY = 0.2f
            card.alpha = 0f
            card.rotationY = 180f
            card.translationY = 100f

            card.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .rotationY(0f)
                .translationY(0f)
                .setDuration(800)
                .setStartDelay((index * 150).toLong())
                .setInterpolator(BounceInterpolator())
                .start()
        }
    }

    private fun showColors() {
        contentContainer.removeAllViews()
        contentContainer.columnCount = 2

        colorsData.forEachIndexed { index, colorItem ->
            val card = createBeautifulColorCard(colorItem)
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(12, 12, 12, 12)
            }
            card.layoutParams = params
            contentContainer.addView(card)

            // Rainbow entrance animation
            card.scaleX = 0.1f
            card.scaleY = 0.1f
            card.alpha = 0f
            card.rotation = 360f
            card.translationY = 200f

            card.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .rotation(0f)
                .translationY(0f)
                .setDuration(900)
                .setStartDelay((index * 120).toLong())
                .setInterpolator(BounceInterpolator())
                .start()
        }
    }

    private fun createBeautifulShapeCard(shapeItem: ShapeItem): CardView {
        val card = CardView(this).apply {
            radius = 28f
            cardElevation = 16f
            setCardBackgroundColor(Color.WHITE)
            useCompatPadding = true
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 36, 24, 36)
        }

        // Beautiful gradient shape container with UNIFORM SIZE
        val shapeContainer = CardView(this).apply {
            radius = 150f // More rounded
            cardElevation = 12f
            layoutParams = LinearLayout.LayoutParams(300, 300).apply { // FIXED UNIFORM SIZE
                bottomMargin = 20
                gravity = Gravity.CENTER_HORIZONTAL
            }
            // Create beautiful gradient background
            background = createShapeGradient(shapeItem.primaryColor, shapeItem.secondaryColor)
        }

        // Add subtle inner shadow effect
        val shadowView = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            background = createInnerShadow()
        }

        val shapeView = TextView(this).apply {
            text = shapeItem.symbol
            textSize = shapeItem.textSize // USE INDIVIDUAL TEXT SIZE FOR VISUAL UNIFORMITY
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            // Add text shadow for depth
            setShadowLayer(8f, 2f, 2f, Color.parseColor("#40000000"))
        }

        val nameText = TextView(this).apply {
            text = shapeItem.name
            textSize = 22f
            setTextColor(Color.parseColor("#2C3E50"))
            gravity = Gravity.CENTER
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            typeface = Typeface.DEFAULT_BOLD
            // Add subtle text shadow
            setShadowLayer(2f, 1f, 1f, Color.parseColor("#20000000"))
        }

        // Create overlay container for shape and shadow
        val overlayContainer = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        overlayContainer.addView(shadowView)
        overlayContainer.addView(shapeView)
        shapeContainer.addView(overlayContainer)
        layout.addView(shapeContainer)
        layout.addView(nameText)
        card.addView(layout)

        card.setOnClickListener {
            animateBeautifulShapeClick(shapeContainer, shapeView, shapeItem)
            animateCardClick(card, shapeItem.primaryColor)
            speakText("${shapeItem.name} shape")
        }
        return card
    }

    private fun createShapeGradient(primaryColor: String, secondaryColor: String): GradientDrawable {
        return GradientDrawable().apply {
            colors = intArrayOf(
                Color.parseColor(primaryColor),
                Color.parseColor(secondaryColor)
            )
            orientation = GradientDrawable.Orientation.TL_BR // Top-left to bottom-right
            shape = GradientDrawable.OVAL
        }
    }

    private fun createInnerShadow(): GradientDrawable {
        return GradientDrawable().apply {
            colors = intArrayOf(
                Color.parseColor("#10000000"),
                Color.TRANSPARENT,
                Color.parseColor("#10000000")
            )
            orientation = GradientDrawable.Orientation.TOP_BOTTOM
            shape = GradientDrawable.OVAL
        }
    }

    private fun animateBeautifulShapeClick(shapeContainer: CardView, shapeView: TextView, shapeItem: ShapeItem) {
        val animatorSet = AnimatorSet()

        // Enhanced bounce with overshoot
        val bounceScaleX = ObjectAnimator.ofFloat(shapeContainer, "scaleX", 1f, 1.4f, 0.95f, 1f)
        val bounceScaleY = ObjectAnimator.ofFloat(shapeContainer, "scaleY", 1f, 1.4f, 0.95f, 1f)

        // Multiple rotation with bounce back
        val rotation = ObjectAnimator.ofFloat(shapeView, "rotation", 0f, 360f, -30f, 0f)

        // Enhanced color animation with multiple colors
        val colorPulse = createRainbowPulseAnimation(shapeContainer, shapeItem)

        // Dramatic elevation changes
        val elevationUp = ObjectAnimator.ofFloat(shapeContainer, "cardElevation", 12f, 30f)
        val elevationDown = ObjectAnimator.ofFloat(shapeContainer, "cardElevation", 30f, 12f)

        // Set enhanced durations
        bounceScaleX.duration = 1000
        bounceScaleY.duration = 1000
        rotation.duration = 1200
        colorPulse.duration = 1000
        elevationUp.duration = 400
        elevationDown.duration = 600

        bounceScaleX.interpolator = BounceInterpolator()
        bounceScaleY.interpolator = BounceInterpolator()
        rotation.interpolator = OvershootInterpolator()

        animatorSet.playTogether(bounceScaleX, bounceScaleY, rotation, colorPulse)

        elevationUp.start()
        mainHandler.postDelayed({ elevationDown.start() }, 400)

        animatorSet.start()

        // Add multiple sparkle effects
        createEnhancedSparkleEffect(shapeContainer)
        createFloatingParticleEffect(shapeContainer)
    }

    private fun createRainbowPulseAnimation(shapeContainer: CardView, shapeItem: ShapeItem): ValueAnimator {
        val colors = intArrayOf(
            Color.parseColor(shapeItem.primaryColor),
            Color.parseColor("#FFD700"), // Gold
            Color.parseColor("#FF69B4"), // Hot Pink
            Color.parseColor("#00CED1"), // Dark Turquoise
            Color.parseColor(shapeItem.primaryColor)
        )

        return ValueAnimator().apply {
            setIntValues(*colors)
            setEvaluator(android.animation.ArgbEvaluator())
            addUpdateListener { animator ->
                try {
                    val gradient = GradientDrawable().apply {
                        setColors(
                            intArrayOf(
                                animator.animatedValue as Int,
                                Color.parseColor(shapeItem.secondaryColor)
                            )
                        )
                        orientation = GradientDrawable.Orientation.TL_BR
                        shape = GradientDrawable.OVAL
                    }
                    shapeContainer.background = gradient
                } catch (e: Exception) {
                    // Ignore animation errors
                }
            }
            repeatCount = 0
        }
    }

    private fun createEnhancedSparkleEffect(view: CardView) {
        // Create multiple sparkle animations
        val sparkleValues = floatArrayOf(12f, 35f, 20f, 40f, 15f, 12f)

        val sparkleAnimator = ValueAnimator.ofFloat(*sparkleValues)
        sparkleAnimator.duration = 800
        sparkleAnimator.addUpdateListener { animator ->
            try {
                view.cardElevation = animator.animatedValue as Float
            } catch (e: Exception) {
                // Ignore
            }
        }

        mainHandler.postDelayed({
            sparkleAnimator.start()
        }, 300)
    }

    private fun createFloatingParticleEffect(view: CardView) {
        // Simulate floating particle effect with translation
        val floatUp = ObjectAnimator.ofFloat(view, "translationY", 0f, -20f, -10f, 0f)
        floatUp.duration = 1000
        floatUp.interpolator = OvershootInterpolator()

        mainHandler.postDelayed({
            floatUp.start()
        }, 200)
    }

    private fun createBeautifulColorCard(colorItem: ColorItem): CardView {
        val card = CardView(this).apply {
            radius = 28f
            cardElevation = 16f
            setCardBackgroundColor(Color.WHITE)
            useCompatPadding = true
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 36, 24, 36)
        }

        // Enhanced color preview with beautiful gradients
        val colorContainer = CardView(this).apply {
            radius = 70f
            cardElevation = 12f
            layoutParams = LinearLayout.LayoutParams(140, 140).apply {
                bottomMargin = 20
                gravity = Gravity.CENTER_HORIZONTAL
            }
        }

        val colorView = LinearLayout(this).apply {
            background = createColorGradient(colorItem.primaryColor, colorItem.secondaryColor)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            gravity = Gravity.CENTER
        }

        val emojiView = TextView(this).apply {
            text = colorItem.emoji
            textSize = 42f
            gravity = Gravity.CENTER
            // Add emoji glow effect
            setShadowLayer(8f, 0f, 0f, Color.WHITE)
        }

        val nameText = TextView(this).apply {
            text = colorItem.name
            textSize = 18f
            setTextColor(Color.parseColor("#2C3E50"))
            gravity = Gravity.CENTER
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            typeface = Typeface.DEFAULT_BOLD
            setShadowLayer(1f, 1f, 1f, Color.parseColor("#20000000"))
        }

        val descriptionText = TextView(this).apply {
            text = colorItem.description
            textSize = 13f
            setTextColor(Color.parseColor("#7F8C8D"))
            gravity = Gravity.CENTER
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 8 }
        }

        colorView.addView(emojiView)
        colorContainer.addView(colorView)
        layout.addView(colorContainer)
        layout.addView(nameText)
        layout.addView(descriptionText)
        card.addView(layout)

        card.setOnClickListener {
            animateBeautifulColorClick(colorContainer, emojiView, colorItem)
            animateCardClick(card, colorItem.primaryColor)
            speakText("${colorItem.name} color. ${colorItem.description}")
        }
        return card
    }

    private fun createColorGradient(primaryColor: String, secondaryColor: String): GradientDrawable {
        return GradientDrawable().apply {
            colors = intArrayOf(
                Color.parseColor(primaryColor),
                Color.parseColor(secondaryColor)
            )
            orientation = GradientDrawable.Orientation.TOP_BOTTOM
            cornerRadius = 70f
        }
    }

    private fun animateBeautifulColorClick(colorContainer: CardView, emojiView: TextView, colorItem: ColorItem) {
        val animatorSet = AnimatorSet()

        // Enhanced bounce animation
        val bounceScaleX = ObjectAnimator.ofFloat(colorContainer, "scaleX", 1f, 1.5f, 0.9f, 1f)
        val bounceScaleY = ObjectAnimator.ofFloat(colorContainer, "scaleY", 1f, 1.5f, 0.9f, 1f)
        val spin = ObjectAnimator.ofFloat(emojiView, "rotation", 0f, 720f, -45f, 0f) // Double spin

        // Add bounce to emoji
        val emojiJump = ObjectAnimator.ofFloat(emojiView, "translationY", 0f, -30f, 10f, 0f)

        bounceScaleX.duration = 800
        bounceScaleY.duration = 800
        spin.duration = 1000
        emojiJump.duration = 600

        bounceScaleX.interpolator = BounceInterpolator()
        bounceScaleY.interpolator = BounceInterpolator()
        spin.interpolator = OvershootInterpolator()
        emojiJump.interpolator = BounceInterpolator()

        animatorSet.playTogether(bounceScaleX, bounceScaleY, spin, emojiJump)
        animatorSet.start()

        // Add sparkle effect to color cards too
        createEnhancedSparkleEffect(colorContainer)
    }

    private fun lightenColor(color: Int, factor: Float): Int {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        val newRed = (red + (255 - red) * factor).toInt().coerceAtMost(255)
        val newGreen = (green + (255 - green) * factor).toInt().coerceAtMost(255)
        val newBlue = (blue + (255 - blue) * factor).toInt().coerceAtMost(255)

        return Color.rgb(newRed, newGreen, newBlue)
    }

    private fun animateCardClick(card: CardView, colorHex: String) {
        // Enhanced card click with multiple effects
        val scaleUp = ObjectAnimator.ofFloat(card, "scaleX", 1f, 1.08f)
        val scaleUpY = ObjectAnimator.ofFloat(card, "scaleY", 1f, 1.08f)
        val scaleDown = ObjectAnimator.ofFloat(card, "scaleX", 1.08f, 1f)
        val scaleDownY = ObjectAnimator.ofFloat(card, "scaleY", 1.08f, 1f)

        scaleUp.duration = 200
        scaleUpY.duration = 200
        scaleDown.duration = 200
        scaleDownY.duration = 200

        val upSet = AnimatorSet()
        upSet.playTogether(scaleUp, scaleUpY)

        val downSet = AnimatorSet()
        downSet.playTogether(scaleDown, scaleDownY)

        upSet.start()
        mainHandler.postDelayed({ downSet.start() }, 200)

        createBeautifulBackgroundFlash(colorHex)
    }

    private fun createBeautifulBackgroundFlash(colorHex: String) {
        try {
            val colors = intArrayOf(
                Color.parseColor("#E8F8F5"),
                Color.parseColor(colorHex + "40"), // Add transparency
                Color.parseColor("#52D4A3"),
                Color.parseColor("#E8F8F5")
            )

            val flashAnimator = ValueAnimator().apply {
                setIntValues(*colors)
                setEvaluator(android.animation.ArgbEvaluator())
                duration = 800
                addUpdateListener { animator ->
                    try {
                        val gradient = GradientDrawable().apply {
                            this.colors = intArrayOf(
                                animator.animatedValue as Int,
                                Color.parseColor("#52D4A3")
                            )
                            orientation = GradientDrawable.Orientation.TOP_BOTTOM
                        }
                        mainScrollView.background = gradient
                    } catch (e: Exception) {
                        // Ignore
                    }
                }
            }
            flashAnimator.start()

        } catch (e: Exception) {
            // Ignore flash errors
        }
    }

    // ENHANCED TTS SYSTEM FROM ALPHABET ACTIVITY
    private fun initTextToSpeech() {
        try {
            textToSpeech = TextToSpeech(this, this)
        } catch (e: Exception) {
            Toast.makeText(this, "TTS init error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onInit(status: Int) {
        try {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.let { tts ->
                    val result = tts.setLanguage(Locale.US)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts.setLanguage(Locale.getDefault())
                    }

                    tts.setSpeechRate(0.7f)  // Slightly slower for better understanding
                    tts.setPitch(1.2f)       // Higher pitch for abckids

                    tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            isCurrentlySpeaking = true
                        }

                        override fun onDone(utteranceId: String?) {
                            isCurrentlySpeaking = false
                        }

                        override fun onError(utteranceId: String?) {
                            isCurrentlySpeaking = false
                        }
                    })

                    isTTSReady = true


                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "TTS Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speakText(text: String) {
        if (!isTTSReady || textToSpeech == null || isCurrentlySpeaking) return

        try {
            textToSpeech?.let { tts ->
                tts.stop()

                // Quick speech response (similar to AlphabetActivity)
                mainHandler.postDelayed({
                    if (isTTSReady && tts != null && !isDestroyed && !isFinishing) {
                        try {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                val params = Bundle()
                                params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ShapeColorSpeak_${System.currentTimeMillis()}")
                                val result = tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "ShapeColorSpeak_${System.currentTimeMillis()}")
                                if (result == TextToSpeech.ERROR) {
                                    isCurrentlySpeaking = false
                                }
                            } else {
                                val params = HashMap<String, String>()
                                params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "ShapeColorSpeak_${System.currentTimeMillis()}"
                                @Suppress("DEPRECATION")
                                val result = tts.speak(text, TextToSpeech.QUEUE_FLUSH, params)
                                if (result == TextToSpeech.ERROR) {
                                    isCurrentlySpeaking = false
                                }
                            }
                        } catch (e: Exception) {
                            isCurrentlySpeaking = false
                            e.printStackTrace()
                        }
                    }
                }, 30) // Very short delay for faster speech response
            }
        } catch (e: Exception) {
            // Ignore speech errors
        }
    }

    private fun stopAllSpeech() {
        try {
            textToSpeech?.stop()
            isCurrentlySpeaking = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        try {
            stopAllSpeech()
            mainHandler.removeCallbacksAndMessages(null) // Clear all pending callbacks
            textToSpeech?.shutdown()
        } catch (e: Exception) {
            // Ignore
        }
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        try {
            stopAllSpeech()
            mainHandler.removeCallbacksAndMessages(null) // Clear pending callbacks
        } catch (e: Exception) {
            // Ignore
        }
    }

    override fun onResume() {
        super.onResume()
        stopAllSpeech()
    }

    data class ShapeItem(
        val name: String,
        val symbol: String,
        val primaryColor: String,
        val secondaryColor: String,
        val textSize: Float // Added text size parameter for uniform visual appearance
    )

    data class ColorItem(
        val name: String,
        val primaryColor: String,
        val secondaryColor: String,
        val emoji: String,
        val description: String
    )
}