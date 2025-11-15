package com.marwadiuniversity.abckids.repository

import android.util.Log
import com.marwadiuniversity.abckids.api.RetrofitClient
import com.marwadiuniversity.abckids.data.YouTubeVideo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class YouTubeRepository {

    private val api = RetrofitClient.youtubeApi
    private val apiKey = "YOUR_API_KEY"

    companion object {
        private const val TAG = "YouTubeRepository"
    }

    suspend fun searchVideos(query: String, maxResults: Int = 15): List<YouTubeVideo> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Searching videos for: $query")

                // Request more videos to filter for embeddable ones
                val response = api.searchVideos(
                    query = query,
                    maxResults = maxResults,
                    apiKey = apiKey
                )

                if (response.isSuccessful) {
                    val searchItems = response.body()?.items ?: emptyList()
                    Log.d(TAG, "Found ${searchItems.size} videos")

                    if (searchItems.isEmpty()) {
                        Log.w(TAG, "No videos found for query: $query")
                        return@withContext emptyList()
                    }

                    val videos = searchItems.map { item ->
                        YouTubeVideo(
                            id = item.id.videoId,
                            title = item.snippet.title,
                            thumbnail = item.snippet.thumbnails.high?.url
                                ?: item.snippet.thumbnails.medium?.url
                                ?: item.snippet.thumbnails.default?.url ?: "",
                            channelTitle = item.snippet.channelTitle
                        )
                    }

                    // Return first 8 videos
                    val result = videos.take(8)
                    Log.d(TAG, "Returning ${result.size} videos")
                    result
                } else {
                    Log.e(TAG, "API Error: ${response.code()} - ${response.message()}")
                    Log.e(TAG, "Error body: ${response.errorBody()?.string()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception searching videos: ${e.message}", e)
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun getVideoDetails(videoIds: List<String>): List<YouTubeVideo> {
        return withContext(Dispatchers.IO) {
            try {
                val idsString = videoIds.joinToString(",")
                Log.d(TAG, "Getting details for videos: $idsString")

                val response = api.getVideoDetails(
                    videoIds = idsString,
                    apiKey = apiKey
                )

                if (response.isSuccessful) {
                    val videoItems = response.body()?.items ?: emptyList()
                    Log.d(TAG, "Got details for ${videoItems.size} videos")

                    videoItems.map { item ->
                        YouTubeVideo(
                            id = item.id,
                            title = item.snippet.title,
                            thumbnail = item.snippet.thumbnails.high?.url
                                ?: item.snippet.thumbnails.medium?.url
                                ?: item.snippet.thumbnails.default?.url ?: "",
                            duration = parseDuration(item.contentDetails.duration),
                            channelTitle = item.snippet.channelTitle
                        )
                    }
                } else {
                    Log.e(TAG, "API Error: ${response.code()} - ${response.message()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception getting video details: ${e.message}", e)
                emptyList()
            }
        }
    }

    private fun parseDuration(duration: String): String {
        try {
            val hours = Regex("(\\d+)H").find(duration)?.groupValues?.get(1)?.toInt() ?: 0
            val minutes = Regex("(\\d+)M").find(duration)?.groupValues?.get(1)?.toInt() ?: 0
            val seconds = Regex("(\\d+)S").find(duration)?.groupValues?.get(1)?.toInt() ?: 0

            return when {
                hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
                minutes > 0 -> String.format("%d:%02d", minutes, seconds)
                else -> "0:${String.format("%02d", seconds)}"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing duration: $duration", e)
            return ""
        }
    }

}
