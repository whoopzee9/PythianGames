package ru.spbstu.common.model

data class Card(
    val layer: Int = 0,
    var cardNum: Int = 0,
    val type: CardType = CardType.Question,
    val question: QuestionGame? = null,
    val cleared: Boolean = false
)

enum class CardType {
    Question,
    Tooth
}

data class QuestionGame(
    val question: Question = Question(),
    val alreadyAnswered: MutableList<Int> = mutableListOf()
)