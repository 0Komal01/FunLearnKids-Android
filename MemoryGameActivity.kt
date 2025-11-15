package com.marwadiuniversity.abckids

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.marwadiuniversity.abckids.utils.AnimationHelper
import com.marwadiuniversity.abckids.utils.SoundManager
import java.util.*

class MemoryGameActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var gameGrid: GridLayout
    private lateinit var scoreText: TextView
    private lateinit var movesText: TextView
    private lateinit var soundManager: SoundManager
    private var tts: TextToSpeech? = null

    private var flippedCards = mutableListOf<Int>()
    private var matchedPairs = 0
    private var moves = 0
    private var score = 0

    private val emojis = mutableListOf(
        "🐶", "🐱", "🐰", "🐸", "🦁", "🐘", "🐵", "🐧",
        "🐶", "🐱", "🐰", "🐸", "🦁", "🐘", "🐵", "🐧"
    ).apply { shuffle() }

    private val cardStates = MutableList(16) { CardState.HIDDEN }

    // Animal names for TTS
    private val animalNames = mapOf(
        "🐶" to "Dog",
        "🐱" to "Cat",
        "🐰" to "Rabbit",
        "🐸" to "Frog",
        "🦁" to "Lion",
        "🐘" to "Elephant",
        "🐵" to "Monkey",
        "🐧" to "Penguin"
    )

    private enum class CardState {
        HIDDEN, FLIPPED, MATCHED
    }

    companion object {
        private const val FLIP_DELAY = 1000L
        private const val TOTAL_PAIRS = 8
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        initializeViews()
        setupToolbar()
        setupTTS()
        setupGame()
        animateCards()
    }

    private fun initializeViews() {
        gameGrid = findViewById(R.id.memory_game_grid)
        scoreText = findViewById(R.id.score_text)
        movesText = findViewById(R.id.moves_text)
        soundManager = SoundManager(this)

        // Show memory game components and hide quiz components
        findViewById<View>(R.id.memory_stats_layout).visibility = View.VISIBLE
        findViewById<View>(R.id.memory_game_container).visibility = View.VISIBLE
        findViewById<View>(R.id.restart_button).visibility = View.VISIBLE

        findViewById<View>(R.id.quiz_buttons_layout).visibility = View.GONE
        findViewById<View>(R.id.question_text).visibility = View.GONE
        findViewById<View>(R.id.animal_emoji).visibility = View.GONE

        updateUI()
    }

    private fun setupTTS() {
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language to US English
            val result = tts?.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to default locale if US English not available
                tts?.setLanguage(Locale.getDefault())
            }

            // Configure TTS for child-friendly voice with anti-cracking settings
            tts?.setSpeechRate(0.7f)      // Slower speech rate for clarity
            tts?.setPitch(1.2f)           // Slightly higher pitch but not too high to avoid cracking

            // Speak welcome message
            speakText("Welcome to Memory Game! Find matching pairs!")

        } else {
            Toast.makeText(this, "Text to Speech initialization failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speakText(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun setupToolbar() {
        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<View>(R.id.restart_button).setOnClickListener { restartGame() }
    }

    private fun setupGame() {
        for (i in 0 until 16) {
            val cardView = createMemoryCard(i)
            gameGrid.addView(cardView)
        }
    }

    private fun createMemoryCard(index: Int): CardView {
        val cardView = CardView(this).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 200
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            radius = 16f
            cardElevation = 8f
            setCardBackgroundColor(ContextCompat.getColor(this@MemoryGameActivity, android.R.color.white))
        }

        val textView = TextView(this).apply {
            text = "?"
            textSize = 32f
            gravity = android.view.Gravity.CENTER
            setTextColor(ContextCompat.getColor(this@MemoryGameActivity, android.R.color.black))
        }

        cardView.addView(textView)
        cardView.setOnClickListener { flipCard(index) }

        return cardView
    }

    private fun flipCard(index: Int) {
        if (cardStates[index] != CardState.HIDDEN || flippedCards.size >= 2) return

        cardStates[index] = CardState.FLIPPED
        flippedCards.add(index)

        // Show emoji
        val cardView = gameGrid.getChildAt(index) as CardView
        val textView = cardView.getChildAt(0) as TextView
        val emoji = emojis[index]
        textView.text = emoji

        // Speak the animal name
        animalNames[emoji]?.let { animalName ->
            speakText(animalName)
        }

        // Animate flip
        val bounceAnimation = AnimationHelper.bounceAnimation(this)
        cardView.startAnimation(bounceAnimation)

        if (flippedCards.size == 2) {
            moves++
            updateUI()
            checkForMatch()
        }
    }

    private fun checkForMatch() {
        val firstIndex = flippedCards[0]
        val secondIndex = flippedCards[1]

        if (emojis[firstIndex] == emojis[secondIndex]) {
            // Match found
            cardStates[firstIndex] = CardState.MATCHED
            cardStates[secondIndex] = CardState.MATCHED
            matchedPairs++
            score += 10

            soundManager.playSuccessSound()

            // Speak success message
            val animalName = animalNames[emojis[firstIndex]]
            speakText("Great! You found a pair of ${animalName}s!")

            animateMatch(firstIndex, secondIndex)

            flippedCards.clear()
            updateUI()

            if (matchedPairs == TOTAL_PAIRS) {
                gameCompleted()
            }
        } else {
            // No match
            soundManager.playErrorSound()
            speakText("Not a match! Try again!")

            Handler(Looper.getMainLooper()).postDelayed({
                hideCards(firstIndex, secondIndex)
            }, FLIP_DELAY)
        }
    }

    private fun hideCards(firstIndex: Int, secondIndex: Int) {
        cardStates[firstIndex] = CardState.HIDDEN
        cardStates[secondIndex] = CardState.HIDDEN

        val firstCard = gameGrid.getChildAt(firstIndex) as CardView
        val secondCard = gameGrid.getChildAt(secondIndex) as CardView

        (firstCard.getChildAt(0) as TextView).text = "?"
        (secondCard.getChildAt(0) as TextView).text = "?"

        flippedCards.clear()
    }

    private fun animateMatch(firstIndex: Int, secondIndex: Int) {
        val firstCard = gameGrid.getChildAt(firstIndex)
        val secondCard = gameGrid.getChildAt(secondIndex)

        val pulseAnimation = AnimationHelper.correctPulseAnimation(this)
        firstCard.startAnimation(pulseAnimation)
        secondCard.startAnimation(pulseAnimation)
    }

    private fun updateUI() {
        scoreText.text = "Score: $score"
        movesText.text = "Moves: $moves"
    }

    private fun gameCompleted() {
        val finalScore = score - (moves * 2) + 100 // Bonus for completion
        val congratsMessage = "Congratulations! You completed the memory game! Final Score: $finalScore"

        Toast.makeText(this, congratsMessage, Toast.LENGTH_LONG).show()
        speakText("Excellent! You found all the pairs! Well done!")

        val bounceAnimation = AnimationHelper.bounceAnimation(this)
        scoreText.startAnimation(bounceAnimation)
    }

    private fun restartGame() {
        matchedPairs = 0
        moves = 0
        score = 0
        flippedCards.clear()
        cardStates.fill(CardState.HIDDEN)

        emojis.shuffle()

        // Reset all cards
        for (i in 0 until gameGrid.childCount) {
            val cardView = gameGrid.getChildAt(i) as CardView
            (cardView.getChildAt(0) as TextView).text = "?"
        }

        updateUI()
        animateCards()
        speakText("New game started! Find the matching pairs!")
    }

    private fun animateCards() {
        val slideInAnimation = AnimationHelper.slideInBottomAnimation(this)

        for (i in 0 until gameGrid.childCount) {
            val card = gameGrid.getChildAt(i)
            card.animateWithDelay(slideInAnimation, i * 50L)
        }
    }

    private fun View.animateWithDelay(animation: android.view.animation.Animation, delay: Long) {
        animation.startOffset = delay
        startAnimation(animation)
    }

    override fun onDestroy() {
        // Clean up TTS
        tts?.stop()
        tts?.shutdown()
        tts = null

        soundManager.release()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        // Stop TTS when activity is paused to avoid conflicts
        tts?.stop()
    }
}