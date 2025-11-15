package com.marwadiuniversity.abckids

data class MatchingCard(
    val id: Int,
    val pairId: Int,
    val imageResource: String,
    val description: String,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false,
    val isEmpty: Boolean = false // For empty slots in grid
)

data class CardMatchingSet(
    val id: Int,
    val type: String, // "letters", "objects", "colors", "shapes"
    val cards: List<MatchingCard>
)