package com.marwadiuniversity.abckids

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var categoryGrid: GridLayout
    private lateinit var menuIcon: TextView
    private var textToSpeech: TextToSpeech? = null
    private var isTTSReady = false
    private var mediaPlayer: MediaPlayer? = null

    private lateinit var sharedPreferences: SharedPreferences
    private var isSoundEnabled = true

    companion object {
        private const val PREFS_NAME = "FunLearnKidsPrefs"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
    }

    private val categoryCards by lazy {
        mapOf(
            R.id.card_alphabets to Triple(AlphabetActivity::class.java, "Alphabets", "Alphabets"),
            R.id.card_numbers to Triple(NumbersActivity::class.java, "Numbers", "Numbers"),
            R.id.card_art to Triple(ArtModeSelectionActivity::class.java, "Art", "Art"),
            R.id.card_music to Triple(MusicSelectionActivity::class.java, "Music", "Music"),
            R.id.card_shapes to Triple(ShapesColorsActivity::class.java, "Shapes & Colors", "Shapes and Colors"),
            R.id.card_games to Triple(GameActivity::class.java, "Games", "Games"),
            R.id.card_youtube to Triple(YouTubeActivity::class.java, "Videos", "Videos"),
            R.id.card_learning to Triple(LearningActivity::class.java, "Learning", "Learning")
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        initializeSharedPreferences()
        loadSettings()
        initializeTextToSpeech()
        initializeBackgroundMusic()
        setupClickListeners()
        setupMenuClick()
        animateCategories()
    }

    private fun initializeViews() {
        categoryGrid = findViewById(R.id.category_grid)
        menuIcon = findViewById(R.id.menu_icon)
    }

    private fun initializeSharedPreferences() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun loadSettings() {
        isSoundEnabled = sharedPreferences.getBoolean(KEY_SOUND_ENABLED, true)
    }

    private fun saveSettings() {
        with(sharedPreferences.edit()) {
            putBoolean(KEY_SOUND_ENABLED, isSoundEnabled)
            apply()
        }
    }

    private fun initializeBackgroundMusic() {
        try {
            val musicResId = resources.getIdentifier("kids_background_music", "raw", packageName)

            if (musicResId != 0) {
                mediaPlayer = MediaPlayer.create(this, musicResId)
                mediaPlayer?.apply {
                    isLooping = true
                    setVolume(0.3f, 0.3f)
                    if (isSoundEnabled) start()
                }
            } else {
                mediaPlayer = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mediaPlayer = null
        }
    }

    private fun setupMenuClick() {
        menuIcon.setOnClickListener { showMenuDialog() }
    }

    private fun showMenuDialog() {
        val menuItems = arrayOf("Settings", "About", "Developers", "Contact Us", "Exit")

        AlertDialog.Builder(this)
            .setTitle("Menu")
            .setItems(menuItems) { dialog, which ->
                when (which) {
                    0 -> showSettingsDialog()
                    1 -> showAboutDialog()
                    2 -> showDevelopersDialog()
                    3 -> showContactUsDialog()
                    4 -> finishAffinity()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showSettingsDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null)
        val soundToggle = dialogView.findViewById<Switch>(R.id.sound_toggle_switch)
        val soundIcon = dialogView.findViewById<TextView>(R.id.sound_toggle_icon)

        soundToggle.isChecked = isSoundEnabled
        updateSoundIcon(soundIcon, isSoundEnabled)

        soundToggle.setOnCheckedChangeListener { _, isChecked ->
            updateSoundIcon(soundIcon, isChecked)
        }

        AlertDialog.Builder(this)
            .setTitle("Settings")
            .setView(dialogView)
            .setPositiveButton("Done") { dialog, _ ->
                isSoundEnabled = soundToggle.isChecked
                saveSettings()

                if (isSoundEnabled) {
                    try {
                        mediaPlayer?.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    speakText("Settings saved")
                } else {
                    try {
                        mediaPlayer?.pause()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    Toast.makeText(this, "Settings saved - Sound disabled", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun updateSoundIcon(iconTextView: TextView, isSoundOn: Boolean) {
        iconTextView.text = if (isSoundOn) "🔊" else "🔇"
    }

    private fun showContactUsDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_contact, null)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showDevelopersDialog() {
        val intent = Intent(this, DeveloperActivity::class.java)
        startActivity(intent)
    }

    private fun showAboutDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_about, null)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                var result = tts.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    result = tts.setLanguage(Locale.UK)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        result = tts.setLanguage(Locale.getDefault())
                    }
                }

                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts.setSpeechRate(0.9f)
                    tts.setPitch(1.2f)

                    tts.voices?.find { voice ->
                        voice.name.contains("female", ignoreCase = true) ||
                                voice.name.contains("child", ignoreCase = true) ||
                                (voice.locale.language == "en" && !voice.name.contains("male", ignoreCase = true))
                    }?.let { tts.voice = it }

                    tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {}
                        override fun onDone(utteranceId: String?) {}
                        override fun onError(utteranceId: String?) {}
                    })

                    isTTSReady = true
                } else {
                    Toast.makeText(this, "Voice language not supported", Toast.LENGTH_SHORT).show()
                    isTTSReady = false
                }
            }
        } else {
            Toast.makeText(this, "Voice feature not available", Toast.LENGTH_SHORT).show()
            isTTSReady = false
        }
    }

    private fun speakText(text: String, utteranceId: String = "DEFAULT") {
        if (!isTTSReady || textToSpeech == null || !isSoundEnabled) {
            return
        }

        textToSpeech?.let { tts ->
            tts.stop()
            val params = Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        }
    }

    private fun setupClickListeners() {
        categoryCards.forEach { (cardId, triple) ->
            val (activityClass, moduleName, _) = triple

            findViewById<CardView>(cardId)?.setOnClickListener { view ->
                animateCardClick(view)

                try {
                    // Keep music playing for Art-related modules
                    if (activityClass == ArtActivity::class.java ||
                        activityClass == ArtModeSelectionActivity::class.java) {
                        // Keep music playing for Art modules
                    } else {
                        // Pause music for all other modules
                        try {
                            mediaPlayer?.pause()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    val intent = Intent(this, activityClass)
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Opening $moduleName...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun animateCardClick(view: View) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

    private fun animateCategories() {
        (0 until categoryGrid.childCount).forEach { index ->
            categoryGrid.getChildAt(index)?.let { child ->
                child.alpha = 0f
                child.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .setStartDelay((index * 80).toLong())
                    .start()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadSettings()

        // Start music when resuming if sound is enabled
        if (isSoundEnabled && mediaPlayer != null) {
            try {
                if (!mediaPlayer!!.isPlaying) {
                    mediaPlayer?.start()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onPause() {
        // Stop music when app is paused or user switches to another app
        try {
            mediaPlayer?.pause()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        textToSpeech?.stop()
        super.onPause()
    }

    override fun onStop() {
        // Ensure music is stopped when app goes to background
        try {
            mediaPlayer?.pause()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onStop()
    }

    override fun onDestroy() {
        textToSpeech?.let { tts ->
            tts.stop()
            tts.shutdown()
        }
        textToSpeech = null

        mediaPlayer?.let { mp ->
            mp.stop()
            mp.release()
        }
        mediaPlayer = null

        isTTSReady = false
        super.onDestroy()
    }
}