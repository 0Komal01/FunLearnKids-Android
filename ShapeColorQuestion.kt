package com.marwadiuniversity.abckids

data class ShapeColorQuestion(
    val id: Int,
    val type: String, // "match_color", "match_shape", "find_shape"
    val targetShape: ShapeColorItem,
    val options: List<ShapeColorItem>,
    val correctOptionId: Int
)

data class ShapeColorItem(
    val id: Int,
    val shape: String, // "circle", "square", "triangle", "rectangle"
    val color: String, // "red", "blue", "yellow", "green"
    val imageResource: String
)
