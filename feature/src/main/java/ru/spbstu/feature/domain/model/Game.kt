package ru.spbstu.feature.domain.model

data class Game(
    val name: String = "",
    val code: String = "",
    val numOfTeams: Int = 0,
    val numOfPlayers: Int = 0,
    val numOfPlayersReady: Int = 0,
    val numOfPlayersJoined: Int = 0,
    val players: HashMap<String, PlayerInfo> = hashMapOf(),
    val currentPlayerId: String = "",
    val morganTurnFlag: Boolean = false,
    val playingFlag: Boolean = false,
    val gameState: GameState = GameState(GameStateTypes.Start, false, 1, 0),
    val cards: List<Card> = listOf(),
    val morganPosition: Int = 0,
    val morganSideSelecting: Boolean = false
)

data class GameState(
    val type: GameStateTypes = GameStateTypes.Start,
    val param1: Any? = null,
    val param2: Any? = null,
    val param3: Any? = null,
    val bidInfo: HashMap<String, WheelBet>? = null,
    val card: Card? = null
)

enum class GameStateTypes {
    Start,
    DiceRoll,
    Turn,
    Wheel,
    Question,
    Tooth

}