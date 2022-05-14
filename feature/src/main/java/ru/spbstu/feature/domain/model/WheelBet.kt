package ru.spbstu.feature.domain.model

data class WheelBet(
    val playerId: String = "",
    val type: WheelBetType = WheelBetType.WhiteBean,
    val number: Int = 0,
    val amountBid: Int = 0
)

enum class WheelBetType {
    WhiteBean,
    BlackBean,
    Dragon
}