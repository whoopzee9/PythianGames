package ru.spbstu.feature.domain.model

import androidx.annotation.DrawableRes
import ru.spbstu.common.model.Player
import ru.spbstu.common.model.PlayerState
import ru.spbstu.common.model.Position
import ru.spbstu.common.utils.TeamsConstants

data class PlayerInfo(
    val id: String = "",
    @DrawableRes val iconRes: Int = 0,
    val teamStr: String = "",
    val playerNum: Int = 1,
    val name: String = "",
    val readyFlag: Boolean = false,
    val position: Position = Position(),
    val turnOrder: Int = 0,
    val coinsCollected: HashMap<String, Int> = hashMapOf(),
    val questionsAnswered: HashMap<String, Int> = hashMapOf(),
    val inventory: HashMap<String, InventoryElement> = hashMapOf(),
    val state: PlayerState = PlayerState()
)

fun PlayerInfo.toPlayer(): Player {
    return Player(
        id = id,
        iconRes = iconRes,
        team = TeamsConstants.getTeamFromString(teamStr),
        playerNum = playerNum,
        name = name,
        isActiveTurn = false,
        position = position,
        turnOrder = turnOrder,
        state = state
    )
}

fun PlayerInfo.toInventoryPlayer(isSelected: Boolean = false): InventoryPlayer {
    return InventoryPlayer(
        playerId = id,
        iconRes = iconRes,
        isSelected = isSelected
    )
}