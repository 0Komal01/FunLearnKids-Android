package com.marwadiuniversity.abckids

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
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
import java.util.*
import kotlin.math.abs

class AlphabetActivity : AppCompatActivity(), TextToSpeech.OnInitListener, GestureDetector.OnGestureListener {

    private lateinit var btnBack: ImageView
    private lateinit var letterCard: CardView
    private lateinit var letterText: TextView
    private lateinit var alphabetImage: ImageView
    private lateinit var wordText: TextView
    private lateinit var progressText: TextView
    private lateinit var backgroundLayout: View

    private lateinit var gestureDetector: GestureDetectorCompat

    private var tts: TextToSpeech? = null
    private var isTTSReady = false
    private var isCurrentlySpeaking = false
    private var shouldAutoSpeakOnInit = true
    private var currentIndex = 0
    private var isTransitioning = false
    private var pendingSpeechAfterTransition = false // New flag for pending speech
    private val mainHandler = Handler(Looper.getMainLooper())

    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100

    private val alphabetData = arrayOf(
        AlphabetItem("A", "Apple", R.drawable.a, "#FF6B6B", "#FF8E53"),
        AlphabetItem("B", "Ball", R.drawable.b, "#4ECDC4", "#556270"),
        AlphabetItem("C", "Cat", R.drawable.c, "#45B7D1", "#5AD2F4"),
        AlphabetItem("D", "Dog", R.drawable.d, "#96CEB4", "#FFEEAD"),
        AlphabetItem("E", "Eagle", R.drawable.e, "#FFEAA7", "#FFC371"),
        AlphabetItem("F", "Fish", R.drawable.f, "#DDA0DD", "#BA55D3"),
        AlphabetItem("G", "Giraffe", R.drawable.g, "#98D8C8", "#6FC9C6"),
        AlphabetItem("H", "Horse", R.drawable.h, "#F7DC6F", "#F5B041"),
        AlphabetItem("I", "Ice Cream", R.drawable.i, "#BB8FCE", "#8E44AD"),
        AlphabetItem("J", "Jet", R.drawable.j, "#85C1E9", "#3498DB"),
        AlphabetItem("K", "Kite", R.drawable.k, "#F8C471", "#F39C12"),
        AlphabetItem("L", "Lion", R.drawable.l, "#F1948A", "#E74C3C"),
        AlphabetItem("M", "Monkey", R.drawable.m, "#82E0AA", "#27AE60"),
        AlphabetItem("N", "Nest", R.drawable.n, "#D2B4DE", "#9B59B6"),
        AlphabetItem("O", "Orange", R.drawable.o, "#F9E79F", "#F39C12"),
        AlphabetItem("P", "Parrot", R.drawable.p, "#AED6F1", "#5DADE2"),
        AlphabetItem("Q", "Queen", R.drawable.q, "#F5B7B1", "#EC7063"),
        AlphabetItem("R", "Rabbit", R.drawable.r, "#A9DFBF", "#2ECC71"),
        AlphabetItem("S", "Sunflower", R.drawable.s, "#F9E79F", "#F1C40F"),
        AlphabetItem("T", "Tiger", R.drawable.t, "#FADBD8", "#E74C3C"),
        AlphabetItem("U", "Umbrella", R.drawable.u, "#D5A6BD", "#AF7AC5"),
        AlphabetItem("V", "Vegetables", R.drawable.v, "#AED6F1", "#2980B9"),
        AlphabetItem("W", "Wolf", R.drawable.w, "#A3E4D7", "#16A085"),
        AlphabetItem("X", "Xylophone", R.drawable.x, "#F8D7DA", "#E57373"),
        AlphabetItem("Y", "Yacht", R.drawable.y, "#CCE5FF", "#3498DB"),
        AlphabetItem("Z", "Zebra", R.drawable.z, "#E8F5E8", "#2ECC71")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        supportActionBar?.hide()
        setContentView(R.layout.activity_alphabet)

        initViews()
        initGestureDetector()
        initTextToSpeech()
        displayCurrentAlphabet()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btn_back)
        letterCard = findViewById(R.id.alphabet_card_main)
        letterText = findViewById(R.id.letter_text)
        alphabetImage = findViewById(R.id.alphabet_image)
        wordText = findViewById(R.id.word_text)
        progressText = findViewById(R.id.progress_text)
        backgroundLayout = findViewById(R.id.main_content_area)

        setupClickListeners()
    }

    private fun initGestureDetector() {
        gestureDetector = GestureDetectorCompat(this, this)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }

        letterCard.setOnClickListener { handleCardInteraction() }

        letterCard.setOnTouchListener { _, event ->
            val gestureResult = gestureDetector.onTouchEvent(event)
            if (gestureResult) {
                true // Consume the event if it was a swipe
            } else {
                false // Allow click events to pass through
            }
        }

        backgroundLayout.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true // Consume background touches
        }
    }

    override fun onDown(e: MotionEvent): Boolean = true
    override fun onShowPress(e: MotionEvent) {}
    override fun onSingleTapUp(e: MotionEvent): Boolean = false
    override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean = false
    override fun onLongPress(e: MotionEvent) { handleCardInteraction() }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        if (e1 == null || isTransitioning) return false
        val diffX = e2.x - e1.x
        val diffY = e2.y - e1.y
        if (abs(diffX) > abs(diffY)) {
            if (abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
                if (diffX > 0) {
                    navigateToPrevious()
                    return true
                } else {
                    navigateToNext()
                    return true
                }
            }
        }
        return false
    }

    private fun navigateToPrevious() {
        if (currentIndex > 0 && !isTransitioning) {
            isTransitioning = true
            pendingSpeechAfterTransition = true // Set pending speech flag
            stopAllSpeech()
            currentIndex--
            animateTransition {
                displayCurrentAlphabet()
            }
        }
    }

    private fun navigateToNext() {
        if (currentIndex < alphabetData.size - 1 && !isTransitioning) {
            isTransitioning = true
            pendingSpeechAfterTransition = true // Set pending speech flag
            stopAllSpeech()
            currentIndex++
            animateTransition {
                displayCurrentAlphabet()
            }
        }
    }

    private fun animateTransition(onComplete: () -> Unit) {
        letterCard.animate()
            .alpha(0f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(100) // Faster transition animation
            .withEndAction {
                onComplete()
            }
            .start()
    }

    private fun handleCardInteraction() {
        if (isCurrentlySpeaking || isTransitioning) return

        if (!isTTSReady) {
            Toast.makeText(this, "Voice initializing...", Toast.LENGTH_SHORT).show()
            return
        }

        val currentItem = alphabetData[currentIndex]
        speakAlphabet(currentItem)

        // Add visual feedback
        letterCard.animate()
            .scaleX(1.05f)
            .scaleY(1.05f)
            .setDuration(100)
            .withEndAction {
                letterCard.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

    private fun initTextToSpeech() {
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let {
                val result = it.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    it.setLanguage(Locale.getDefault())
                }

                it.setSpeechRate(0.7f)  // slightly slower for better understanding
                it.setPitch(1.2f)
                it.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
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

                // AUTO-SPEAK THE FIRST ALPHABET when TTS is ready
                if (shouldAutoSpeakOnInit) {
                    shouldAutoSpeakOnInit = false
                    mainHandler.postDelayed({
                        if (isTTSReady && !isDestroyed && !isFinishing) {
                            val currentItem = alphabetData[currentIndex]
                            speakAlphabet(currentItem)
                        }
                    }, 500)
                }
            }
        } else {
            isTTSReady = false
            Toast.makeText(this, "Voice not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayCurrentAlphabet() {
        val currentItem = alphabetData[currentIndex]
        progressText.text = "Letter ${currentIndex + 1} of ${alphabetData.size}"

        letterCard.alpha = 0f
        letterCard.scaleX = 0.8f
        letterCard.scaleY = 0.8f

        letterText.text = currentItem.letter
        alphabetImage.setImageResource(currentItem.imageResId)
        wordText.text = "for ${currentItem.word}"

        // Unique gradient background
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(Color.parseColor(currentItem.startColor), Color.parseColor(currentItem.endColor))
        )
        gradientDrawable.cornerRadius = 40f * resources.displayMetrics.density
        letterCard.background = gradientDrawable

        letterCard.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300) // Faster animation for quicker speech
            .withEndAction {
                // Mark transition as complete and handle pending speech
                isTransitioning = false

                if (pendingSpeechAfterTransition && isTTSReady) {
                    pendingSpeechAfterTransition = false
                    // Quick speech after swipe transitions
                    mainHandler.postDelayed({
                        if (isTTSReady && !isCurrentlySpeaking && !isTransitioning && !isDestroyed && !isFinishing) {
                            speakAlphabet(currentItem)
                        }
                    }, 50) // Reduced delay for faster response
                }
            }
            .start()

        // Glow animation
        createBackgroundGlow(currentItem)
    }

    private fun createBackgroundGlow(currentItem: AlphabetItem) {
        val originalColor = Color.parseColor("#F0F4F8")
        val startColor = Color.parseColor(currentItem.startColor)
        val endColor = Color.parseColor(currentItem.endColor)
        val avgColor = Color.rgb(
            (Color.red(startColor) + Color.red(endColor)) / 2,
            (Color.green(startColor) + Color.green(endColor)) / 2,
            (Color.blue(startColor) + Color.blue(endColor)) / 2
        )
        val glowColor = Color.argb(80, Color.red(avgColor), Color.green(avgColor), Color.blue(avgColor))
        val animator = ValueAnimator.ofArgb(originalColor, glowColor, originalColor)
        animator.duration = 1200
        animator.addUpdateListener { backgroundLayout.setBackgroundColor(it.animatedValue as Int) }
        animator.start()
    }

    private fun speakAlphabet(item: AlphabetItem) {
        if (!isTTSReady || isDestroyed || isFinishing) return

        // Stop any current speech
        tts?.stop()
        isCurrentlySpeaking = false

        val textToSpeak = "${item.letter} for ${item.word}"

        // Minimal delay for immediate speech response
        mainHandler.postDelayed({
            if (isTTSReady && tts != null && !isDestroyed && !isFinishing) {
                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        val params = Bundle()
                        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "AlphabetSpeak_${System.currentTimeMillis()}")
                        val result = tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, "AlphabetSpeak_${System.currentTimeMillis()}")
                        if (result == TextToSpeech.ERROR) {
                            isCurrentlySpeaking = false
                        }
                    } else {
                        val params = HashMap<String, String>()
                        params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "AlphabetSpeak_${System.currentTimeMillis()}"
                        @Suppress("DEPRECATION")
                        val result = tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params)
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

    private fun stopAllSpeech() {
        try {
            tts?.stop()
            isCurrentlySpeaking = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        stopAllSpeech()
        mainHandler.removeCallbacksAndMessages(null) // Clear all pending callbacks
        tts?.shutdown()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        stopAllSpeech()
        pendingSpeechAfterTransition = false
        mainHandler.removeCallbacksAndMessages(null) // Clear pending callbacks
    }

    override fun onResume() {
        super.onResume()
        stopAllSpeech()
        isTransitioning = false
        pendingSpeechAfterTransition = false

        // If returning from pause and TTS is ready, speak current alphabet
        if (isTTSReady) {
            mainHandler.postDelayed({
                if (isTTSReady && !isCurrentlySpeaking && !isTransitioning && !isDestroyed && !isFinishing) {
                    val currentItem = alphabetData[currentIndex]
                    speakAlphabet(currentItem)
                }
            }, 200) // Faster resume speech
        }
    }

    data class AlphabetItem(
        val letter: String,
        val word: String,
        val imageResId: Int,
        val startColor: String,
        val endColor: String
    )
}