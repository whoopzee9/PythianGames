package ru.spbstu.common.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import ru.spbstu.common.R

@Keep
data class Player(
    val id: String = "",
    @DrawableRes val iconRes: Int = R.drawable.character_1,
    val team: Team = Team.Red,
    val playerNum: Int = 1,
    val name: String = "",
    val isActiveTurn: Boolean = false,
    val position: Position = Position(),
    val turnOrder: Int = 0,
    val state: PlayerState = PlayerState()
)

@Keep
data class Position(
    val x: Int = 0,
    val y: Int = 0
)

@Keep
data class PlayerState(
    val skippingTurn: Boolean = false,
    val amountLeft: Int = 0,
    val type: PlayerStateType? = null
)

@Keep
enum class PlayerStateType {
    ToolLoss,
    Cavern
}

@Keep
sealed class Team(@ColorRes val colorRes: Int) {
    object Red : Team(R.color.color_team_red)
    object Orange : Team(R.color.color_team_orange)
    object Blue : Team(R.color.color_team_blue)
    object Green : Team(R.color.color_team_green)
}