package ru.spbstu.feature.domain.model

import androidx.annotation.Keep
import ru.spbstu.common.model.Card

@Keep
data class Game(
    val name: String = "",
    val code: String = "",
    val numOfTeams: Int = 0,
    val numOfPlayers: Int = 0,
    val numOfPlayersReady: Int = 0,
    val numOfPlayersJoined: Int = 0,
    val players: HashMap<String, PlayerInfo> = hashMapOf(),
    val currentPlayerId: String = "",
    val creatorId: String = "",
    val morganTurnFlag: Boolean = false,
    val playingFlag: Boolean = false,
    val gameState: GameState = GameState(GameStateTypes.Start, false, 1, 0),
    val cards: List<Card> = listOf(),
    val morganPosition: Int = 0,
    val morganSideSelecting: Boolean = false,
    val inventoryPool: HashMap<String, Int> = hashMapOf(),
    val eventPool: HashMap<String, Int> = hashMapOf()
)

@Keep
data class GameState(
    val type: GameStateTypes = GameStateTypes.Start,
    val param1: Any? = null,
    val param2: Any? = null,
    val param3: Any? = null,
    val param4: Any? = null,
    val bidInfo: HashMap<String, WheelBet>? = null,
    val card: Card? = null,
    val collapseNum: CollapseState? = null
)

@Keep
data class CollapseState(
    val num: Int = 0,
    val initialPosNum: Int = 0
)

@Keep
enum class GameStateTypes {
    Start,
    MorganTurn,
    Turn,
    Wheel,
    Question,
    Tooth
}

@Keep
enum class ToothType {
    Tooth,
    Bone
}

@Keep
enum class ToothResult {
    Sieve,
    Brush,
    Rope,
    Bone,
    Treasure,
    ToolLoss,
    Collapse,
    Rainfall,
    RiverHorizontal,
    RiverVertical,
    Cavern
}