package ru.spbstu.feature.domain.model

import androidx.annotation.Keep

@Keep
data class WheelBet(
    val playerId: String = "",
    val type: WheelBetType = WheelBetType.WhiteBean,
    val number: Int = 0,
    val amountBid: Int = 0,
    val skipped: Boolean = false
)

enum class WheelBetType {
    WhiteBean,
    BlackBean,
    Dragon
}