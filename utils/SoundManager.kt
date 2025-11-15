package com.marwadiuniversity.abckids.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log

class SoundManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null

    companion object {
        private const val TAG = "SoundManager"
        private const val MAX_STREAMS = 5
    }

    init {
        initializeSoundPool()
    }

    private fun initializeSoundPool() {
        soundPool = SoundPool.Builder()
            .setMaxStreams(MAX_STREAMS)
            .build()
    }

    fun playSound(resourceId: Int) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, resourceId)
            mediaPlayer?.apply {
                setOnCompletionListener { release() }
                start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing sound: ${e.message}")
        }
    }

    fun playLetterSound(letter: String) {
        // Implementation for letter pronunciation
        // You would add actual sound files for each letter
        Log.d(TAG, "Playing sound for letter: $letter")
    }

    fun playSuccessSound() {
        // Play success sound effect
        Log.d(TAG, "Playing success sound")
    }

    fun playErrorSound() {
        // Play error sound effect
        Log.d(TAG, "Playing error sound")
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        soundPool?.release()
        soundPool = null
    }
}