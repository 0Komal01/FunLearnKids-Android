package com.marwadiuniversity.abckids

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.marwadiuniversity.abckids.data.GameData
import com.marwadiuniversity.abckids.data.QuizQuestion
import com.marwadiuniversity.abckids.utils.AnimationHelper
import com.marwadiuniversity.abckids.utils.SoundManager
import java.util.*
import kotlin.collections.ArrayList

class GameActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var questionText: TextView
    private lateinit var animalEmoji: TextView
    private lateinit var answerButtons: Array<Button>
    private lateinit var soundManager: SoundManager
    private var textToSpeech: TextToSpeech? = null
    private var isTTSReady = false
    private var isCurrentlySpeaking = false
    private var animalSoundPlayer: MediaPlayer? = null

    private var currentQuestionIndex = 0
    private var score = 0
    // Create a shuffled copy of questions
    private val questions = ArrayList<QuizQuestion>()

    // Animal sound mapping
    private val animalSounds = mapOf(
        "🐶" to R.raw.dog_sound,      // Dog bark
        "🐱" to R.raw.cat_sound,      // Cat meow
        "🐮" to R.raw.cow_sound,      // Cow moo
        "🐷" to R.raw.pig_sound,      // Pig oink
        "🐴" to R.raw.horse_sound,    // Horse neigh
        "🐑" to R.raw.sheep_sound,    // Sheep baa
        "🐸" to R.raw.frog_sound,     // Frog croak
        "🐘" to R.raw.elephant_sound, // Elephant trumpet
        "🦁" to R.raw.lion_sound,     // Lion roar
        "🐯" to R.raw.tiger_sound,    // Tiger roar
        "🐻" to R.raw.bear_sound,     // Bear growl
        "🐵" to R.raw.monkey_sound,   // Monkey chatter
        "🐔" to R.raw.chicken_sound,  // Chicken cluck
        "🐦" to R.raw.bird_sound,     // Bird chirp
        "🦆" to R.raw.duck_sound,     // Duck quack
        "🐺" to R.raw.wolf_sound,     // Wolf howl
        "🦅" to R.raw.eagle_sound,    // Eagle screech
        "🐙" to R.raw.generic_sound,  // Octopus (generic)
        "🐧" to R.raw.penguin_sound,  // Penguin call
        "🦉" to R.raw.owl_sound       // Owl hoot
    )
    companion object {
        private const val NEXT_QUESTION_DELAY = 5000L // Increased for animal sound
        private const val ANIMAL_SOUND_DELAY = 1000L
        private const val TTS_TAG = "GameTTS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        initializeViews()
        initializeTextToSpeech()
        setupToolbar()
        shuffleQuestions() // Shuffle questions before starting
        loadQuestion()
    }

    private fun shuffleQuestions() {
        try {
            // Create a copy of the original questions and shuffle them
            questions.clear()

            // Create new QuizQuestion objects with shuffled options
            val shuffledQuestions = GameData.animalQuestions.map { originalQuestion ->
                val correctAnswer = originalQuestion.correctAnswer
                val optionsList = originalQuestion.options.toMutableList()
                optionsList.shuffle()

                // Create a new QuizQuestion with shuffled options
                QuizQuestion(
                    emoji = originalQuestion.emoji,
                    correctAnswer = correctAnswer,
                    options = optionsList // Keep as List<String>
                )
            }.toMutableList()

            // Shuffle the order of questions
            shuffledQuestions.shuffle()

            questions.addAll(shuffledQuestions)

            Log.d(TTS_TAG, "Questions shuffled successfully. Total questions: ${questions.size}")
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error shuffling questions: ${e.message}")
            // Fallback to original questions if shuffling fails
            questions.clear()
            questions.addAll(GameData.animalQuestions)
        }
    }

    private fun initializeViews() {
        try {
            questionText = findViewById(R.id.question_text)
            animalEmoji = findViewById(R.id.animal_emoji)
            answerButtons = arrayOf(
                findViewById(R.id.answer_btn_1),
                findViewById(R.id.answer_btn_2),
                findViewById(R.id.answer_btn_3),
                findViewById(R.id.answer_btn_4)
            )
            soundManager = SoundManager(this)
            setupAnswerButtons()
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error initializing views: ${e.message}")
        }
    }

    private fun initializeTextToSpeech() {
        try {
            textToSpeech = TextToSpeech(this, this)
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Failed to initialize TTS: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                try {
                    // Try different languages for better compatibility
                    var result = tts.setLanguage(Locale.US)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        result = tts.setLanguage(Locale.UK)
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            result = tts.setLanguage(Locale.getDefault())
                        }
                    }

                    if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Configure for stable, clear speech - reduced pitch to prevent cracking
                        tts.setSpeechRate(0.7f) // Slower for clarity
                        tts.setPitch(1.0f) // Normal pitch to prevent distortion

                        // Set up listener to track speech state
                        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                            override fun onStart(utteranceId: String?) {
                                isCurrentlySpeaking = true
                                Log.d(TTS_TAG, "Speech started: $utteranceId")
                            }

                            override fun onDone(utteranceId: String?) {
                                isCurrentlySpeaking = false
                                Log.d(TTS_TAG, "Speech completed: $utteranceId")
                            }

                            override fun onError(utteranceId: String?) {
                                isCurrentlySpeaking = false
                                Log.e(TTS_TAG, "Speech error: $utteranceId")
                            }
                        })

                        isTTSReady = true
                        Log.d(TTS_TAG, "TTS initialized successfully")

                    } else {
                        Log.w(TTS_TAG, "Language not supported")
                    }
                } catch (e: Exception) {
                    Log.e(TTS_TAG, "Error configuring TTS: ${e.message}")
                }
            }
        } else {
            Log.e(TTS_TAG, "TTS initialization failed with status: $status")
        }
    }

    private fun setupToolbar() {
        try {
            findViewById<View>(R.id.btn_back)?.setOnClickListener {
                stopSpeaking()
                stopAnimalSound()
                finish()
            }
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error setting up toolbar: ${e.message}")
        }
    }

    private fun setupAnswerButtons() {
        try {
            answerButtons.forEachIndexed { index, button ->
                button.setOnClickListener { checkAnswer(index) }
            }
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error setting up answer buttons: ${e.message}")
        }
    }

    private fun loadQuestion() {
        try {
            if (currentQuestionIndex < questions.size) {
                val currentQuestion = questions[currentQuestionIndex]
                displayQuestion(currentQuestion)
            } else {
                showFinalScore()
            }
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error loading question: ${e.message}")
        }
    }

    private fun displayQuestion(question: QuizQuestion) {
        try {
            animalEmoji.text = question.emoji

            val bounceAnimation = AnimationHelper.bounceAnimation(this)
            animalEmoji.startAnimation(bounceAnimation)

            answerButtons.forEachIndexed { index, button ->
                button.apply {
                    text = question.options[index]
                    isEnabled = true
                    visibility = View.VISIBLE
                    background = ContextCompat.getDrawable(this@GameActivity, R.drawable.button_default)
                    setTextColor(ContextCompat.getColor(this@GameActivity, R.color.text_primary))
                }
            }
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error displaying question: ${e.message}")
        }
    }

    private fun checkAnswer(selectedIndex: Int) {
        try {
            val currentQuestion = questions[currentQuestionIndex]
            val correctIndex = currentQuestion.options.indexOf(currentQuestion.correctAnswer)
            val isCorrect = selectedIndex == correctIndex

            answerButtons.forEach { it.isEnabled = false }

            handleAnswerResult(selectedIndex, correctIndex, isCorrect, currentQuestion.emoji)

            Handler(Looper.getMainLooper()).postDelayed({
                currentQuestionIndex++
                loadQuestion()
            }, NEXT_QUESTION_DELAY)
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error checking answer: ${e.message}")
        }
    }

    private fun handleAnswerResult(selectedIndex: Int, correctIndex: Int, isCorrect: Boolean, animalEmoji: String) {
        try {
            if (isCorrect) {
                score++
                showCorrectAnswer(selectedIndex)
                playSuccessSound()

                // Play animal sound after a short delay
                Handler(Looper.getMainLooper()).postDelayed({
                    playAnimalSound(animalEmoji)
                }, ANIMAL_SOUND_DELAY)

            } else {
                showIncorrectAnswer(selectedIndex, correctIndex)

                // Use TTS for incorrect answers instead of sound file
                val incorrectMessages = arrayOf(
                    "Try again!",
                    "Oops! Wrong answer!",
                    "Not quite right!",
                    "Good try!",
                    "Keep trying!"
                )
                speakText(incorrectMessages.random())
            }
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error handling answer result: ${e.message}")
        }
    }

    private fun playAnimalSound(animalEmoji: String) {
        try {
            val soundResourceId = animalSounds[animalEmoji]
            if (soundResourceId != null) {
                // Stop any currently playing animal sound
                stopAnimalSound()

                animalSoundPlayer = MediaPlayer.create(this, soundResourceId)
                animalSoundPlayer?.let { player ->
                    player.start()
                    Log.d(TTS_TAG, "Playing animal sound for: $animalEmoji")

                    // Stop after fixed duration (3 sec)
                    Handler(Looper.getMainLooper()).postDelayed({
                        stopAnimalSound()
                    }, 3000L)
                }
            } else {
                Log.w(TTS_TAG, "No sound found for animal: $animalEmoji")
                playSuccessSound()
            }
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error playing animal sound: ${e.message}")
            playSuccessSound()
        }
    }

    private fun stopAnimalSound() {
        try {
            animalSoundPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            }
            animalSoundPlayer = null
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error stopping animal sound: ${e.message}")
        }
    }

    private fun showCorrectAnswer(index: Int) {
        try {
            answerButtons[index].apply {
                background = ContextCompat.getDrawable(this@GameActivity, R.drawable.button_correct)
                setTextColor(Color.WHITE)
                startAnimation(AnimationHelper.correctPulseAnimation(this@GameActivity))
            }
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error showing correct answer: ${e.message}")
        }
    }

    private fun showIncorrectAnswer(selected: Int, correct: Int) {
        try {
            answerButtons[selected].apply {
                background = ContextCompat.getDrawable(this@GameActivity, R.drawable.button_incorrect)
                setTextColor(Color.WHITE)
                startAnimation(AnimationHelper.incorrectShakeAnimation(this@GameActivity))
            }

            answerButtons[correct].apply {
                background = ContextCompat.getDrawable(this@GameActivity, R.drawable.button_correct)
                setTextColor(Color.WHITE)
            }
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error showing incorrect answer: ${e.message}")
        }
    }

    private fun showFinalScore() {
        try {
            questionText.text = "Quiz Complete!"
            animalEmoji.text = "🏆"
            animalEmoji.startAnimation(AnimationHelper.bounceAnimation(this))

            answerButtons.forEach { it.visibility = View.GONE }

            // Final TTS message and toast - only at quiz completion
            val finalMessage = when {
                score == questions.size -> "Perfect score! Amazing work!"
                score >= questions.size * 0.8 -> "Excellent job! You did great!"
                score >= questions.size * 0.6 -> "Good work! Keep practicing!"
                else -> "Nice try! Practice makes perfect!"
            }

            // Show toast with final score
            Toast.makeText(this, "Final Score: $score/${questions.size} - $finalMessage", Toast.LENGTH_LONG).show()

            // Speak the final message
            Log.d(TTS_TAG, "Speaking final message: $finalMessage")
            speakText(finalMessage)
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error showing final score: ${e.message}")
        }
    }

    private fun speakText(text: String) {
        if (!isTTSReady || textToSpeech == null) {
            Log.w(TTS_TAG, "TTS not ready")
            return
        }

        try {
            textToSpeech?.let { tts ->
                // Stop current speech to prevent overlapping
                if (isCurrentlySpeaking) {
                    tts.stop()
                }

                // Create parameters for better speech quality
                val params = Bundle()
                params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "GameTTS")
                params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)

                val result = tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "GameTTS")

                if (result == TextToSpeech.ERROR) {
                    Log.e(TTS_TAG, "Error speaking text: $text")
                } else {
                    Log.d(TTS_TAG, "Speaking: $text")
                }
            }
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Exception while speaking: ${e.message}")
        }
    }

    private fun stopSpeaking() {
        try {
            textToSpeech?.stop()
            isCurrentlySpeaking = false
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error stopping speech: ${e.message}")
        }
    }

    private fun playSuccessSound() {
        try {
            if (::soundManager.isInitialized) {
                soundManager.playSuccessSound()
            }
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error playing success sound: ${e.message}")
        }
    }

    private fun playErrorSound() {
        try {
            if (::soundManager.isInitialized) {
                soundManager.playErrorSound() // This will now play incorrect_sound.mp3
            }
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error playing error sound: ${e.message}")
        }
    }

    override fun onPause() {
        try {
            stopSpeaking()
            stopAnimalSound()
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error in onPause: ${e.message}")
        }
        super.onPause()
    }

    override fun onDestroy() {
        try {
            stopSpeaking()
            stopAnimalSound()
            textToSpeech?.let { tts ->
                tts.shutdown()
            }
            textToSpeech = null
            isTTSReady = false

            if (::soundManager.isInitialized) {
                soundManager.release()
            }
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error in onDestroy: ${e.message}")
        }
        super.onDestroy()
    }
}