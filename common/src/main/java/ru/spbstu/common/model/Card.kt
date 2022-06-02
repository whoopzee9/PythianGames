package ru.spbstu.common.model

import androidx.annotation.Keep

@Keep
data class Card(
    val layer: Int = 0,
    var cardNum: Int = 0,
    val type: CardType = CardType.Question,
    val question: QuestionGame? = null,
    val cleared: Boolean = false
)

@Keep
enum class CardType {
    Question,
    Tooth
}

@Keep
data class QuestionGame(
    val question: Question = Question(),
    val alreadyAnswered: MutableList<Int> = mutableListOf()
)