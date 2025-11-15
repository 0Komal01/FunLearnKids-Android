package com.marwadiuniversity.abckids

data class CountingQuestion(
    val id: Int,
    val type: String, // "addition", "subtraction", "counting"
    val question: String,
    val visualObjects: List<CountingObject>,
    val correctAnswer: Int,
    val options: List<Int>
)

data class CountingObject(
    val type: String,
    val count: Int,
    val imageResource: String,
    val action: String? = null // "add", "remove", null
)
