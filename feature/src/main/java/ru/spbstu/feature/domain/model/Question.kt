package ru.spbstu.feature.domain.model

data class Question(
    val id: String = "",
    val answer1: String = "",
    val answer2: String = "",
    val answer3: String = "",
    val answer4: String = "",
    val questionText: String = "",
    val correctAnswer: Int = 0,
    val questionTier: Int = 0
)