package ru.spbstu.common.model

import androidx.annotation.Keep

@Keep
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