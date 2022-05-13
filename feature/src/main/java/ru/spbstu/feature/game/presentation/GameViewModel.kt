package ru.spbstu.feature.game.presentation

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.common.utils.DatabaseReferences
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Game
import ru.spbstu.feature.domain.model.GameState
import ru.spbstu.feature.domain.model.PlayerInfo
import ru.spbstu.feature.utils.GameJoiningDataWrapper
import timber.log.Timber

class GameViewModel(
    val router: FeatureRouter,
    val gameJoiningDataWrapper: GameJoiningDataWrapper
) : BackViewModel(router) {
    private val _game: MutableStateFlow<Game> = MutableStateFlow(Game())
    val game: StateFlow<Game> = _game

    val currentUserId = Firebase.auth.currentUser?.uid

    fun setGame(game: Game) {
        _game.value = game
    }

    fun setGameState(gameState: GameState) {
        val ref = Firebase.database.getReference(DatabaseReferences.GAMES_REF)
        ref.child(gameJoiningDataWrapper.game.name)
            .child("gameState")
            .setValue(gameState).addOnCompleteListener {
                if (it.isSuccessful) {

                } else {
                    Timber.tag(TAG).e(it.exception)
                }
            }
    }

    fun setMorganSide(side: Int) {
        val ref = Firebase.database.getReference(DatabaseReferences.GAMES_REF)
        ref.child(gameJoiningDataWrapper.game.name)
            .child("morganPosition")
            .setValue(side)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                } else {
                    Timber.tag(TAG).e(it.exception)
                }
            }
        ref.child(gameJoiningDataWrapper.game.name)
            .child("morganSideSelecting")
            .setValue(true)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                } else {
                    Timber.tag(TAG).e(it.exception)
                }
            }
    }

    fun setMorganStartPosition(position: Int) {
        val ref = Firebase.database.getReference(DatabaseReferences.GAMES_REF)
        ref.child(gameJoiningDataWrapper.game.name)
            .child("morganPosition")
            .setValue(position)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                } else {
                    Timber.tag(TAG).e(it.exception)
                }
            }
        ref.child(gameJoiningDataWrapper.game.name)
            .child("morganSideSelecting")
            .setValue(false)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                } else {
                    Timber.tag(TAG).e(it.exception)
                }
            }
    }

    fun setActivePlayerId(playerId: String) {
        val ref = Firebase.database.getReference(DatabaseReferences.GAMES_REF)
        ref.child(gameJoiningDataWrapper.game.name)
            .child("currentPlayerId")
            .setValue(playerId)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                } else {
                    Timber.tag(TAG).e(it.exception)
                }
            }
    }

    fun updatePlayersTurnOrders(firstNum: Int) {
        val newMap = hashMapOf<String, PlayerInfo>()
        game.value.players.values.forEach {
            val newTurn =
                if (it.turnOrder >= firstNum)
                    it.turnOrder - firstNum + 1
                else
                    it.turnOrder + (game.value.players.values.size - firstNum + 1)
            newMap[it.id] = it.copy(turnOrder = newTurn)
        }
        val ref = Firebase.database.getReference(DatabaseReferences.GAMES_REF)
        ref.child(gameJoiningDataWrapper.game.name)
            .child("players")
            .setValue(newMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                } else {
                    Timber.tag(TAG).e(it.exception)
                }
            }
    }

    fun getFirstPlayerByMorganPosition(position: Int, playerList: List<PlayerInfo>): PlayerInfo {
        when (playerList.size) {
            2 -> {
                return if (position in 2..5) {
                    playerList.first { it.turnOrder == 2 }
                } else {
                    playerList.first { it.turnOrder == 1 }
                }
            }
            4 -> {
                return when (position) {
                    1, 2 -> {
                        playerList.first { it.turnOrder == 2 }
                    }
                    3, 4 -> {
                        playerList.first { it.turnOrder == 3 }
                    }
                    5, 6 -> {
                        playerList.first { it.turnOrder == 4 }
                    }
                    else -> {
                        playerList.first { it.turnOrder == 1 }
                    }
                }
            }
            6 -> {
                return when (position) {
                    1, 2 -> {
                        playerList.first { it.turnOrder == 2 }
                    }
                    3, 4, 5 -> {
                        playerList.first { it.turnOrder == 3 }
                    }
                    6, 7, 8 -> {
                        playerList.first { it.turnOrder == 4 }
                    }
                    9, 10 -> {
                        playerList.first { it.turnOrder == 5 }
                    }
                    11, 12, 13 -> {
                        playerList.first { it.turnOrder == 6 }
                    }
                    else -> {
                        playerList.first { it.turnOrder == 1 }
                    }
                }
            }
            8 -> {
                return when (position) {
                    1, 2 -> {
                        playerList.first { it.turnOrder == 2 }
                    }
                    3, 4 -> {
                        playerList.first { it.turnOrder == 3 }
                    }
                    5, 6 -> {
                        playerList.first { it.turnOrder == 4 }
                    }
                    7, 8 -> {
                        playerList.first { it.turnOrder == 5 }
                    }
                    9, 10 -> {
                        playerList.first { it.turnOrder == 6 }
                    }
                    11, 12 -> {
                        playerList.first { it.turnOrder == 7 }
                    }
                    13, 14 -> {
                        playerList.first { it.turnOrder == 8 }
                    }
                    else -> {
                        playerList.first { it.turnOrder == 1 }
                    }
                }
            }
            else -> throw IllegalStateException("Wrong player amount!")
        }
    }

    companion object {
        private val TAG = GameViewModel::class.simpleName
    }
}