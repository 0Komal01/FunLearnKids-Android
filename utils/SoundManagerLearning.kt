package com.marwadiuniversity.abckids.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log

class SoundManagerLearning(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    companion object {
        private const val TAG = "SoundManagerLearning"
    }

    fun playSound(resourceId: Int) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, resourceId)
            mediaPlayer?.apply {
                setOnCompletionListener { release() }
                setOnErrorListener { _, _, _ ->
                    Log.e(TAG, "MediaPlayer error occurred")
                    release()
                    true
                }
                start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing sound: ${e.message}")
        }
    }

    fun playSuccessSound() {
        try {
            // Play system notification sound for correct answer
            val uri = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                        .build()
                )
                setDataSource(context, uri)
                setOnCompletionListener { release() }
                setOnErrorListener { _, _, _ ->
                    Log.e(TAG, "Success sound failed")
                    true
                }
                setOnPreparedListener { start() }
                prepareAsync()
            }
            Log.d(TAG, "Playing success sound")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play success sound: ${e.message}")
        }
    }

    fun playErrorSound() {
        try {
            // Play system alarm sound for incorrect answer (short duration)
            val uri = android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                        .build()
                )
                setDataSource(context, uri)
                setOnCompletionListener { release() }
                setOnErrorListener { _, _, _ ->
                    Log.e(TAG, "Error sound failed")
                    true
                }
                setOnPreparedListener {
                    start()
                    // Stop after 400ms for a short error sound
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        if (isPlaying) {
                            stop()
                            release()
                        }
                    }, 400)
                }
                prepareAsync()
            }
            Log.d(TAG, "Playing error sound")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play error sound: ${e.message}")
        }
    }

    fun playLetterSound(letter: String) {
        // Implementation for letter pronunciation - currently just logs
        Log.d(TAG, "Playing sound for letter: $letter")
    }

    fun release() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            mediaPlayer = null
            Log.d(TAG, "SoundManagerLearning resources released")
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing SoundManagerLearning: ${e.message}")
        }
    }
}