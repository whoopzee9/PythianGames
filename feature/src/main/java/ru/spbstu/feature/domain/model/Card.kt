package ru.spbstu.feature.domain.model

data class Card(
    val layer: Int = 0,
    var cardNum: Int = 0,
    val type: CardType = CardType.Question,
    val question: QuestionGame? = null
)

enum class CardType {
    Question,
    Tooth
}

data class QuestionGame(
    val question: Question = Question(),
    val alreadyAnswered: HashMap<Int, Int> = hashMapOf()
)