package com.marwadiuniversity.abckids.data

data class YouTubeSearchResponse(
    val items: List<SearchItem>?
)

data class SearchItem(
    val id: VideoId,
    val snippet: Snippet
)

data class VideoId(
    val videoId: String
)

data class Snippet(
    val title: String,
    val description: String,
    val thumbnails: Thumbnails,
    val channelTitle: String
)

data class Thumbnails(
    val default: Thumbnail?,
    val medium: Thumbnail?,
    val high: Thumbnail?,
    val standard: Thumbnail?,
    val maxres: Thumbnail?
)

data class Thumbnail(
    val url: String,
    val width: Int?,
    val height: Int?
)

data class YouTubeVideoDetailsResponse(
    val items: List<VideoDetailsItem>?
)

data class VideoDetailsItem(
    val id: String,
    val snippet: Snippet,
    val contentDetails: ContentDetails
)

data class ContentDetails(
    val duration: String
)

data class YouTubeVideo(
    val id: String,
    val title: String,
    val thumbnail: String,
    val duration: String = "",
    val category: String = "General",
    val channelTitle: String = ""
)

data class YouTubeCategory(
    val name: String,
    val icon: String,
    val searchQuery: String,
    var videos: List<YouTubeVideo> = emptyList()
)

object YouTubeData {
    val categories = listOf(
        YouTubeCategory(
            name = "Alphabet Songs",
            icon = "🔤",
            searchQuery = "ABC alphabet songs for abckids"
        ),
        YouTubeCategory(
            name = "Number Songs",
            icon = "🔢",
            searchQuery = "number counting songs for abckids"
        ),
        YouTubeCategory(
            name = "Shapes & Colors",
            icon = "🎨",
            searchQuery = "shapes colors learning for abckids"
        ),
        YouTubeCategory(
            name = "Nursery Rhymes",
            icon = "🎵",
            searchQuery = "nursery rhymes for children"
        ),
        YouTubeCategory(
            name = "Educational Videos",
            icon = "📚",
            searchQuery = "educational videos for abckids learning"
        )
    )
}
