package com.marwadiuniversity.abckids.api

import com.marwadiuniversity.abckids.data.YouTubeSearchResponse
import com.marwadiuniversity.abckids.data.YouTubeVideoDetailsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {

    @GET("search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("videoEmbeddable") embeddable: String = "true",
        @Query("videoSyndicated") syndicated: String = "true",  // Add this
        @Query("videoCategoryId") categoryId: String = "27",
        @Query("maxResults") maxResults: Int = 20,  // Increase to get more options
        @Query("safeSearch") safeSearch: String = "strict",
        @Query("key") apiKey: String
    ): Response<YouTubeSearchResponse>

    @GET("videos")
    suspend fun getVideoDetails(
        @Query("part") part: String = "snippet,contentDetails",
        @Query("id") videoIds: String,
        @Query("key") apiKey: String
    ): Response<YouTubeVideoDetailsResponse>
}
