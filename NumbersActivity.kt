package com.marwadiuniversity.abckids

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GestureDetectorCompat
import com.marwadiuniversity.abckids.data.NumberData
import com.marwadiuniversity.abckids.data.NumberItem
import java.util.*
import kotlin.math.abs

class NumbersActivity : AppCompatActivity(), TextToSpeech.OnInitListener, GestureDetector.OnGestureListener {

    private lateinit var singleNumberCard: CardView
    private lateinit var numberImage: ImageView
    private lateinit var numberText: TextView
    private lateinit var numberWord: TextView
    private lateinit var progressText: TextView
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var backgroundLayout: View

    // Gesture detector for swipe functionality
    private lateinit var gestureDetector: GestureDetectorCompat

    private var currentIndex = 0
    private var ttsInitialized = false
    private var isSoundEnabled = true
    private var isTransitioning = false
    private var isCurrentlySpeaking = false
    private var shouldAutoSpeakOnInit = true // Flag for initial auto-speech
    private val mainHandler = Handler(Looper.getMainLooper())

    // Swipe detection thresholds - Made more sensitive
    private val swipeThreshold = 80  // Reduced from 100
    private val swipeVelocityThreshold = 80  // Reduced from 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numbers)

        initializeViews()
        setupToolbar()
        initGestureDetector()
        initializeTextToSpeech()
        displayCurrentNumber()
        setupClickListeners()
    }

    private fun initializeViews() {
        val numberCardView = findViewById<View>(R.id.number_card_view)
        singleNumberCard = numberCardView as CardView
        numberImage = numberCardView.findViewById(R.id.number_image)
        numberText = numberCardView.findViewById(R.id.number_text)
        numberWord = numberCardView.findViewById(R.id.number_word)
        progressText = findViewById(R.id.progress_text)
        backgroundLayout = findViewById(R.id.main_content_area)
    }

    private fun setupToolbar() {
        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }
    }

    private fun initGestureDetector() {
        gestureDetector = GestureDetectorCompat(this, this)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupClickListeners() {
        // Card click for voice
        singleNumberCard.setOnClickListener {
            handleCardClick()
        }

        // Touch listener for swipe gestures - IMPORTANT: return true to consume swipe events
        singleNumberCard.setOnTouchListener { _, event ->
            val gestureResult = gestureDetector.onTouchEvent(event)
            if (gestureResult) {
                true // Consume the event if it was a swipe
            } else {
                false // Allow click events to pass through
            }
        }

        // Background touch listener
        backgroundLayout.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true // Consume background touches
        }
    }

    // Gesture Detector Methods
    override fun onDown(e: MotionEvent): Boolean = true

    override fun onShowPress(e: MotionEvent) {}

    override fun onSingleTapUp(e: MotionEvent): Boolean = false

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean = false

    override fun onLongPress(e: MotionEvent) {
        handleCardClick()
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        if (e1 == null || isTransitioning) return false

        val diffX = e2.x - e1.x
        val diffY = e2.y - e1.y

        // Check if horizontal swipe is dominant
        if (abs(diffX) > abs(diffY)) {
            if (abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
                if (diffX > 0) {
                    // Right swipe - previous
                    moveToPreviousNumber()
                    return true
                } else {
                    // Left swipe - next
                    moveToNextNumber()
                    return true
                }
            }
        }
        return false
    }

    private fun handleCardClick() {
        if (!ttsInitialized || !isSoundEnabled || isTransitioning || isCurrentlySpeaking) {
            // If TTS not ready, try to reinitialize
            if (!ttsInitialized) {
                Toast.makeText(this, "Voice initializing...", Toast.LENGTH_SHORT).show()
                return
            }
            return
        }

        startImmediateSpeech()
    }

    private fun startImmediateSpeech() {
        val currentNumber = NumberData.numberList[currentIndex]

        singleNumberCard.animate()
            .scaleX(1.05f)
            .scaleY(1.05f)
            .setDuration(100)
            .withEndAction {
                singleNumberCard.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(100)
                    .start()
            }
            .start()

        executeImmediateTTS(currentNumber)
    }

    private fun executeImmediateTTS(numberItem: NumberItem) {
        if (!ttsInitialized) return

        val text = numberItem.word
        textToSpeech.stop()

        // Add a small delay to ensure TTS is ready
        mainHandler.postDelayed({
            if (ttsInitialized) {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "number_speech")
            }
        }, 100)
    }

    private fun displayCurrentNumber() {
        try {
            val currentNumber = NumberData.numberList[currentIndex]

            // Update progress first
            progressText.text = "Number ${currentIndex + 1} of ${NumberData.numberList.size}"

            // Update all the content immediately
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(
                    Color.parseColor(currentNumber.colorStart),
                    Color.parseColor(currentNumber.colorEnd)
                )
            ).apply {
                cornerRadius = 40f * resources.displayMetrics.density
            }

            singleNumberCard.background = gradientDrawable
            singleNumberCard.cardElevation = 20f * resources.displayMetrics.density
            singleNumberCard.radius = 40f * resources.displayMetrics.density

            numberImage.setImageResource(currentNumber.fingerImageRes)
            numberImage.clearColorFilter()
            numberImage.visibility = View.VISIBLE
            numberImage.alpha = 1.0f
            numberImage.setBackgroundColor(Color.WHITE)

            numberText.text = currentNumber.number
            numberWord.text = currentNumber.word
            numberText.visibility = View.VISIBLE
            numberWord.visibility = View.VISIBLE
            numberText.setShadowLayer(8f, 0f, 4f, Color.parseColor("#80000000"))
            numberWord.setShadowLayer(4f, 0f, 2f, Color.parseColor("#80000000"))

            // Handle fallback for empty word text
            Handler(Looper.getMainLooper()).postDelayed({
                if (numberWord.text.isNullOrEmpty() || numberWord.text == "-") {
                    numberWord.text = getNumberWordFallback(currentIndex)
                }
            }, 50)

        } catch (e: Exception) {
            Toast.makeText(this, "Display error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getNumberWordFallback(index: Int): String {
        return when(index) {
            0 -> "One"; 1 -> "Two"; 2 -> "Three"; 3 -> "Four"; 4 -> "Five"
            5 -> "Six"; 6 -> "Seven"; 7 -> "Eight"; 8 -> "Nine"; 9 -> "Ten"
            else -> "Number"
        }
    }

    private fun moveToNextNumber() {
        if (currentIndex >= NumberData.numberList.size - 1 || isTransitioning) return

        isTransitioning = true
        stopAllSpeech()

        // First animate out
        singleNumberCard.animate()
            .alpha(0f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(150)
            .withEndAction {
                // Update index and display new content
                currentIndex++
                displayCurrentNumber()

                // Then animate in
                singleNumberCard.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .withEndAction {
                        isTransitioning = false
                        // Auto-speak after transition
                        mainHandler.postDelayed({
                            if (ttsInitialized) {
                                handleCardClick()
                            }
                        }, 200)
                    }
                    .start()
            }
            .start()
    }

    private fun moveToPreviousNumber() {
        if (currentIndex <= 0 || isTransitioning) return

        isTransitioning = true
        stopAllSpeech()

        // First animate out
        singleNumberCard.animate()
            .alpha(0f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(150)
            .withEndAction {
                // Update index and display new content
                currentIndex--
                displayCurrentNumber()

                // Then animate in
                singleNumberCard.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .withEndAction {
                        isTransitioning = false
                        // Auto-speak after transition
                        mainHandler.postDelayed({
                            if (ttsInitialized) {
                                handleCardClick()
                            }
                        }, 200)
                    }
                    .start()
            }
            .start()
    }

    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        try {
            if (status == TextToSpeech.SUCCESS) {
                val langResult = textToSpeech.setLanguage(Locale.US)
                if (langResult == TextToSpeech.LANG_MISSING_DATA ||
                    langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech.setLanguage(Locale.getDefault())
                }

                textToSpeech.setSpeechRate(0.7f)
                textToSpeech.setPitch(1.3f)

                textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
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

                ttsInitialized = true

                // AUTO-SPEAK THE FIRST NUMBER when TTS is ready
                if (shouldAutoSpeakOnInit) {
                    shouldAutoSpeakOnInit = false
                    mainHandler.postDelayed({
                        if (ttsInitialized && !isDestroyed && !isFinishing) {
                            startImmediateSpeech()
                        }
                    }, 500) // Wait 500ms to ensure everything is loaded
                }

            } else {
                ttsInitialized = false
                Toast.makeText(this, "Voice not available", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "TTS Error: ${e.message}", Toast.LENGTH_SHORT).show()
            ttsInitialized = false
        }
    }

    private fun stopAllSpeech() {
        if (::textToSpeech.isInitialized && ttsInitialized) {
            textToSpeech.stop()
            isCurrentlySpeaking = false
        }
    }

    override fun onDestroy() {
        stopAllSpeech()
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        stopAllSpeech()
        isTransitioning = false
    }

    override fun onResume() {
        super.onResume()
        stopAllSpeech()
        isTransitioning = false

        // If returning from pause and TTS is ready, speak current number
        if (ttsInitialized) {
            mainHandler.postDelayed({
                if (ttsInitialized && !isCurrentlySpeaking && !isTransitioning) {
                    startImmediateSpeech()
                }
            }, 300)
        }
    }
}