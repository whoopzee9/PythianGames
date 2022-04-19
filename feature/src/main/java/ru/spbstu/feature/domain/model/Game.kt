package ru.spbstu.feature.domain.model

data class Game(
    val name: String = "",
    val code: String = "",
    val numOfTeams: Int = 0,
    val numOfPlayers: Int = 0,
    val numOfPlayersReady: Int = 0,
    val numOfPlayersJoined: Int = 0,
    val players: List<PlayerInfo> = listOf(),
    val currentPlayerTurn: String = "",
    val morganTurnFlag: Boolean = false,
    val playingFlag: Boolean = false,
    val turnState: String = ""
)