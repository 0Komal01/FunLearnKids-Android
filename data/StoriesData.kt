package com.marwadiuniversity.abckids.data

data class Story(
    val id: Int,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val coverImage: String,
    val pages: List<StoryPage>,
    val moral: String,
    val backgroundColor: String = "#FF6B9D"
)

data class StoryPage(
    val image: String
)

object StoriesData {
    val stories = listOf(
        Story(
            id = 1,
            title = "The Three Little Pigs",
            subtitle = "Building strong homes",
            emoji = "🐷",
            coverImage = "story_three_pigs",
            backgroundColor = "#FFE0B2",
            moral = "Hard work and doing things properly keeps you safe.",
            pages = listOf(
                StoryPage(image = "story_three_pigs"),
                StoryPage(image = "three_pigs_page_1"),
                StoryPage(image = "three_pigs_page_2"),
                StoryPage(image = "three_pigs_page_3"),
                StoryPage(image = "three_pigs_page_4"),
                StoryPage(image = "three_pigs_page_5"),
                StoryPage(image = "three_pigs_page_6"),
                StoryPage(image = "three_pigs_page_7"),
                StoryPage(image = "three_pigs_page_8")
            )
        ),
        Story(
            id = 2,
            title = "The Ant and the Grasshopper",
            subtitle = "Work hard, play later",
            emoji = "🐜",
            coverImage = "story_ant_grasshopper",
            backgroundColor = "#C8E6C9",
            moral = "Hard work and planning ahead are important.",
            pages = listOf(
                StoryPage(image = "story_ant_grasshopper"),
                StoryPage(image = "ant_grasshopper_page_1"),
                StoryPage(image = "ant_grasshopper_page_2"),
                StoryPage(image = "ant_grasshopper_page_3"),
                StoryPage(image = "ant_grasshopper_page_4"),
                StoryPage(image = "ant_grasshopper_page_5"),
                StoryPage(image = "ant_grasshopper_page_6"),
                StoryPage(image = "ant_grasshopper_page_7"),
                StoryPage(image = "ant_grasshopper_page_8")
            )
        )
    )
}