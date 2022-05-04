package ru.spbstu.feature.domain.model

import androidx.annotation.DrawableRes
import ru.spbstu.common.model.Player
import ru.spbstu.common.utils.TeamsConstants

data class PlayerInfo(
    val id: String = "",
    @DrawableRes val iconRes: Int = 0,
    val teamStr: String = "",
    val playerNum: Int = 1,
    val name: String = "",
    val readyFlag: Boolean = false
)

fun PlayerInfo.toPlayer(): Player {
    return Player(
        id = id,
        iconRes = iconRes,
        team = TeamsConstants.getTeamFromString(teamStr),
        playerNum = playerNum,
        name = name,
        isActiveTurn = false
    )
}