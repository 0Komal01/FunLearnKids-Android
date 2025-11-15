package com.marwadiuniversity.abckids

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marwadiuniversity.abckids.adapters.YouTubeCategoryAdapter
import com.marwadiuniversity.abckids.data.YouTubeData
import com.marwadiuniversity.abckids.repository.YouTubeRepository
import kotlinx.coroutines.*

class YouTubeActivity : AppCompatActivity() {

    private lateinit var backArrow: ImageView
    private lateinit var categoriesRecycler: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: YouTubeCategoryAdapter
    private val repository = YouTubeRepository()
    private var loadingJob: Job? = null

    companion object {
        private const val TAG = "YouTubeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube)

        Log.d(TAG, "=== YouTubeActivity Created ===")

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        loadVideos()
    }

    private fun initializeViews() {
        backArrow = findViewById(R.id.btn_back)
        categoriesRecycler = findViewById(R.id.categories_recycler)
        progressBar = findViewById(R.id.progress_bar)
    }

    private fun setupRecyclerView() {
        adapter = YouTubeCategoryAdapter(
            context = this,
            categories = YouTubeData.categories,
            onVideoClick = { video ->
                Log.d(TAG, "=== Video Clicked ===")
                Log.d(TAG, "Title: ${video.title}")
                Log.d(TAG, "ID: ${video.id}")

                if (video.id.isEmpty()) {
                    Toast.makeText(this, "Invalid video", Toast.LENGTH_SHORT).show()
                    return@YouTubeCategoryAdapter
                }

                try {
                    val intent = android.content.Intent(this, YouTubePlayerActivity::class.java)
                    intent.putExtra("video_id", video.id)
                    intent.putExtra("video_title", video.title)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Error opening video: ${e.message}")
                    Toast.makeText(this, "Error opening video", Toast.LENGTH_SHORT).show()
                }
            }
        )

        categoriesRecycler.layoutManager = LinearLayoutManager(this)
        categoriesRecycler.adapter = adapter
    }

    private fun setupClickListeners() {
        backArrow.setOnClickListener {
            finish()
        }
    }

    private fun loadVideos() {
        // Cancel any existing loading job
        loadingJob?.cancel()

        progressBar.visibility = View.VISIBLE

        // Create a new job with SupervisorJob to prevent cancellation propagation
        loadingJob = lifecycleScope.launch(SupervisorJob() + Dispatchers.Main) {
            try {
                Log.d(TAG, "=== Starting to load videos ===")
                var totalVideosLoaded = 0

                YouTubeData.categories.forEachIndexed { index, category ->
                    if (!isActive) {
                        Log.d(TAG, "Job cancelled, stopping video load")
                        return@launch
                    }

                    Log.d(TAG, "Loading category: ${category.name}")

                    try {
                        // Load videos for this category
                        val videos = withContext(Dispatchers.IO) {
                            repository.searchVideos(category.searchQuery, maxResults = 10)
                        }

                        if (isActive) {
                            category.videos = videos
                            totalVideosLoaded += videos.size
                            Log.d(TAG, "✓ Loaded ${videos.size} videos for ${category.name}")

                            // Update UI on main thread
                            adapter.notifyItemChanged(index)
                        }
                    } catch (e: CancellationException) {
                        Log.d(TAG, "Category load cancelled: ${category.name}")
                        throw e // Re-throw to stop the loop
                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading ${category.name}: ${e.message}")
                        // Continue with next category even if one fails
                    }

                    // Small delay between categories to avoid rate limiting
                    delay(200)
                }

                if (isActive) {
                    progressBar.visibility = View.GONE

                    if (totalVideosLoaded == 0) {
                        Toast.makeText(
                            this@YouTubeActivity,
                            "No videos found. Please check your internet connection.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Log.d(TAG, "✓ Successfully loaded $totalVideosLoaded total videos")
                    }
                }

            } catch (e: CancellationException) {
                Log.d(TAG, "Video loading cancelled")
                progressBar.visibility = View.GONE
            } catch (e: Exception) {
                Log.e(TAG, "✗ Error loading videos: ${e.message}", e)

                if (isActive) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@YouTubeActivity,
                        "Failed to load videos. Please try again.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingJob?.cancel()
        Log.d(TAG, "=== YouTubeActivity Destroyed ===")
    }
}