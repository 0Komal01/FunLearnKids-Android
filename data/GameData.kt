package com.marwadiuniversity.abckids.data

data class QuizQuestion(
    val emoji: String,
    val correctAnswer: String,
    val options: List<String>
)

object GameData {
    val animalQuestions = listOf(
        QuizQuestion("🐶", "Dog", listOf("Cat", "Dog", "Cow", "Lion")),
        QuizQuestion("🐱", "Cat", listOf("Dog", "Cat", "Bear", "Frog")),
        QuizQuestion("🐮", "Cow", listOf("Cow", "Horse", "Sheep", "Pig")),
        QuizQuestion("🐷", "Pig", listOf("Pig", "Dog", "Cat", "Cow")),
        QuizQuestion("🐴", "Horse", listOf("Horse", "Sheep", "Cow", "Lion")),
        QuizQuestion("🐑", "Sheep", listOf("Sheep", "Goat", "Cow", "Pig")),
        QuizQuestion("🐸", "Frog", listOf("Frog", "Duck", "Penguin", "Rabbit")),
        QuizQuestion("🐘", "Elephant", listOf("Elephant", "Lion", "Bear", "Tiger")),
        QuizQuestion("🦁", "Lion", listOf("Lion", "Tiger", "Dog", "Wolf")),
        QuizQuestion("🐯", "Tiger", listOf("Tiger", "Lion", "Bear", "Cat")),
        QuizQuestion("🐻", "Bear", listOf("Bear", "Dog", "Cat", "Monkey")),
        QuizQuestion("🐵", "Monkey", listOf("Monkey", "Cat", "Dog", "Lion")),
        QuizQuestion("🐔", "Chicken", listOf("Chicken", "Duck", "Bird", "Penguin")),
        QuizQuestion("🐦", "Bird", listOf("Bird", "Eagle", "Owl", "Duck")),
        QuizQuestion("🦆", "Duck", listOf("Duck", "Penguin", "Chicken", "Goose")),
        QuizQuestion("🐺", "Wolf", listOf("Wolf", "Dog", "Tiger", "Lion")),
        QuizQuestion("🦅", "Eagle", listOf("Eagle", "Owl", "Bird", "Crow")),
        QuizQuestion("🐙", "Octopus", listOf("Octopus", "Fish", "Crab", "Whale")),
        QuizQuestion("🐧", "Penguin", listOf("Penguin", "Duck", "Swan", "Goose")),
        QuizQuestion("🦉", "Owl", listOf("Owl", "Eagle", "Crow", "Parrot"))
    )
}
