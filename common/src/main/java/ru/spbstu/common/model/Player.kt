package ru.spbstu.common.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import ru.spbstu.common.R

data class Player(
    val id: String = "",
    @DrawableRes val iconRes: Int = R.drawable.character_1,
    val team: Team = Team.Red,
    val playerNum: Int = 1,
    val name: String = "",
    val isActiveTurn: Boolean = false
)

sealed class Team(@ColorRes val colorRes: Int) {
    object Red : Team(R.color.color_team_red)
    object Orange : Team(R.color.color_team_orange)
    object Blue : Team(R.color.color_team_blue)
    object Green : Team(R.color.color_team_green)
}