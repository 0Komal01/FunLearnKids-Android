package com.marwadiuniversity.abckids

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class YouTubePlayerActivity : AppCompatActivity() {

    private lateinit var backArrow: ImageView
    private lateinit var videoTitle: TextView
    private var videoId: String = ""
    private var title: String = ""
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val TAG = "YouTubePlayerActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_player)

        videoId = intent.getStringExtra("video_id") ?: ""
        title = intent.getStringExtra("video_title") ?: "Video"

        Log.d(TAG, "Opening YouTube video: $videoId - $title")

        if (videoId.isEmpty()) {
            finish()
            return
        }

        initializeViews()
        setupClickListeners()

        // Delay 2.5 seconds before redirecting to YouTube
        handler.postDelayed({
            if (!isFinishing) openInYouTubeApp()
        }, 2500)
    }

    private fun initializeViews() {
        backArrow = findViewById(R.id.btn_back)
        videoTitle = findViewById(R.id.video_title)
        videoTitle.text = title
    }

    private fun setupClickListeners() {
        backArrow.setOnClickListener { finish() }
    }

    private fun openInYouTubeApp() {
        Log.d(TAG, "Redirecting to YouTube app for video: $videoId")
        try {
            val youtubeAppIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
            youtubeAppIntent.putExtra("force_fullscreen", true)
            startActivity(youtubeAppIntent)
        } catch (e: Exception) {
            Log.w(TAG, "YouTube app not available, opening in browser...")
            try {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
                startActivity(webIntent)
            } catch (ex: Exception) {
                Log.e(TAG, "Cannot open YouTube: ${ex.message}")
            }
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
