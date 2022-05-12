package ru.spbstu.feature.domain.model

import ru.spbstu.feature.game.presentation.GameFragment

data class Game(
    val name: String = "",
    val code: String = "",
    val numOfTeams: Int = 0,
    val numOfPlayers: Int = 0,
    val numOfPlayersReady: Int = 0,
    val numOfPlayersJoined: Int = 0,
    val players: HashMap<String, PlayerInfo> = hashMapOf(),
    val currentPlayerTurn: String = "",
    val morganTurnFlag: Boolean = false,
    val playingFlag: Boolean = false,
    val gameState: GameFragment.GameState = GameFragment.GameState.Start,
    val cards: List<Card> = listOf()
)