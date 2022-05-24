package ru.spbstu.feature.game.presentation

import android.os.CountDownTimer
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.model.Card
import ru.spbstu.common.model.PlayerState
import ru.spbstu.common.model.Position
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.common.utils.DatabaseReferences
import ru.spbstu.common.utils.GameUtils
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Game
import ru.spbstu.feature.domain.model.GameState
import ru.spbstu.feature.domain.model.GameStateTypes
import ru.spbstu.feature.domain.model.InventoryElement
import ru.spbstu.feature.domain.model.PlayerInfo
import ru.spbstu.feature.domain.model.TeamStatistics
import ru.spbstu.feature.domain.model.WheelBet
import ru.spbstu.feature.utils.GameJoiningDataWrapper
import timber.log.Timber

class GameViewModel(
    val router: FeatureRouter,
    val gameJoiningDataWrapper: GameJoiningDataWrapper
) : BackViewModel(router) {
    private val _game: MutableStateFlow<Game> = MutableStateFlow(Game())
    val game: StateFlow<Game> = _game

    val currentUserId = Firebase.auth.currentUser?.uid

    val ref = Firebase.database.getReference(DatabaseReferences.GAMES_REF)

    var bidAmount: Int = 0

    var size = 0

    var delayTimer: CountDownTimer? = null

    fun setupAndStartDelayTimer(
        delaySec: Long,
        onFinishCallback: () -> Unit,
        onTickCallback: (Long) -> Unit
    ) {
        delayTimer = object : CountDownTimer(delaySec * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                onTickCallback(millisUntilFinished / 1000)
            }

            override fun onFinish() {
                onFinishCallback()
            }
        }
        delayTimer?.start()
    }

    fun setGame(game: Game) {
        _game.value = game
    }

    fun setGameState(gameState: GameState, onSuccess: () -> Unit = {}) {
        ref.child(gameJoiningDataWrapper.game.name)
            .child("gameState")
            .setValue(gameState).addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess.invoke()
                } else {
                    Timber.tag(TAG).e(it.exception)
                }
            }
    }

    fun setMorganSide(side: Int) {
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

    fun setMorganPosition(position: Int, onSuccess: () -> Unit = {}) {
        val newPosition = if (position >= (size - 1) * 4) position - (size - 1) * 4 else position
        ref.child(gameJoiningDataWrapper.game.name)
            .child("morganPosition")
            .setValue(newPosition)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess.invoke()
                } else {
                    Timber.tag(TAG).e(it.exception)
                }
            }
    }

    fun setActivePlayerId(playerId: String, callback: (() -> Unit)? = null) {
        ref.child(gameJoiningDataWrapper.game.name)
            .child("currentPlayerId")
            .setValue(playerId)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback?.invoke()
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

    fun movePlayer(id: String, position: Position) {
        ref.child(gameJoiningDataWrapper.game.name)
            .child("players")
            .child(id)
            .child("position")
            .setValue(position)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                } else {
                    Timber.tag(TAG).e(it.exception)
                }
            }
    }

    fun passTurnToNextPlayer(isMorganTurn: Boolean = false, onSuccess: () -> Unit = {}) {
        val currPlayer = game.value.players[currentUserId]
        val sortedPlayers = game.value.players.values.sortedBy { it.turnOrder }.toMutableList()
        if (isMorganTurn) {
            var firstAvailableNext: PlayerInfo? = null
            kotlin.run {
                sortedPlayers.forEachIndexed { index, playerInfo ->
                    if (!playerInfo.state.skippingTurn) {
                        firstAvailableNext = playerInfo
                        return@run
                    } else {
                        val oldState = playerInfo.state
                        val newState = PlayerState(
                            skippingTurn = oldState.amountLeft - 1 != 0,
                            amountLeft = oldState.amountLeft - 1,
                            type = playerInfo.state.type
                        )
                        sortedPlayers[index] = playerInfo.copy(state = newState)
                    }
                }
            }
            if (firstAvailableNext != null) {
                val newGame = game.value.copy(
                    currentPlayerId = firstAvailableNext?.id ?: "",
                    gameState = GameState(type = GameStateTypes.Turn)
                )
                sortedPlayers.forEach {
                    newGame.players[it.id] = it
                }
                if (game.value.currentPlayerId == currentUserId) {
                    updateGame(newGame, onSuccess)
                }
            } else {
                //todo start another morgan turn
            }

        } else {
            if (currPlayer?.turnOrder == game.value.players.size) {
                setGameState(GameState(type = GameStateTypes.MorganTurn, param1 = false))
            } else {
                val nextOrder = currPlayer?.turnOrder!! + 1
                var firstAvailableNext: PlayerInfo? = null
                kotlin.run {
                    sortedPlayers.forEachIndexed { index, playerInfo ->
                        if (playerInfo.turnOrder > currPlayer.turnOrder) {
                            if (!playerInfo.state.skippingTurn) {
                                firstAvailableNext = playerInfo
                                return@run
                            } else {
                                val oldState = playerInfo.state
                                val newState = PlayerState(
                                    skippingTurn = oldState.amountLeft - 1 != 0,
                                    amountLeft = oldState.amountLeft - 1,
                                    type = playerInfo.state.type
                                )
                                sortedPlayers[index] = playerInfo.copy(state = newState)
                            }
                        }
                    }
                }
                //            val firstAvailableNext = sortedPlayers.firstOrNull { it.turnOrder > currPlayer.turnOrder && !it.state.skippingTurn }
                if (firstAvailableNext != null) {
                    //val nextPlayer = game.value.players.values.first { it.turnOrder == nextOrder }

                    val newGame = game.value.copy(currentPlayerId = firstAvailableNext?.id ?: "")
                    sortedPlayers.forEach {
                        newGame.players[it.id] = it
                    }
                    if (game.value.currentPlayerId == currentUserId) {
                        updateGame(newGame, onSuccess)
                    }

                } else {
                    setGameState(GameState(type = GameStateTypes.MorganTurn, param1 = false))
                    //todo need to roll dice for morgan to move
                }
            }
        }
    }

    fun updateBidInfo(wheelBet: WheelBet) {
        ref.child(gameJoiningDataWrapper.game.name)
            .child("gameState")
            .child("bidInfo")
            .child(wheelBet.playerId)
            .setValue(wheelBet)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                } else {
                    Timber.tag(TAG).e(it.exception)
                }
            }
    }

    fun giveCurrentPlayerCoins(layer: Int, amount: Int) {
        val oldAmount =
            game.value.players[currentUserId]?.coinsCollected?.get(GameUtils.getLayerByNumber(layer).name) ?: 0
        ref.child(gameJoiningDataWrapper.game.name)
            .child("players")
            .child(currentUserId ?: "")
            .child("coinsCollected")
            .child(GameUtils.getLayerByNumber(layer).name)
            .setValue(oldAmount + amount)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                } else {
                    Timber.tag(TAG).e(it.exception)
                }
            }
    }

    fun setCardAnswered(layer: Int, amount: Int) {
        val oldAmount =
            game.value.players[currentUserId]?.questionsAnswered?.get(GameUtils.getLayerByNumber(layer).name) ?: 0
        ref.child(gameJoiningDataWrapper.game.name)
            .child("players")
            .child(currentUserId ?: "")
            .child("questionsAnswered")
            .child(GameUtils.getLayerByNumber(layer).name)
            .setValue(oldAmount + amount)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                } else {
                    Timber.tag(TAG).e(it.exception)
                }
            }
    }

    fun updateCard(newCard: Card) {
        val key = (newCard.layer - 1) * (size * size) + newCard.cardNum - 1
        ref.child(gameJoiningDataWrapper.game.name)
            .child("cards")
            .child(key.toString())
            .setValue(newCard)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                } else {
                    Timber.tag(TAG).e(it.exception)
                }
            }
    }

    fun setInventoryAmount(name: String, value: Int) {
        ref.child(gameJoiningDataWrapper.game.name)
            .child("inventoryPool")
            .child(name)
            .setValue(value)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                } else {
                    Timber.tag(TAG).e(it.exception)
                }
            }
    }

    fun setEventAmount(name: String, value: Int) {
        ref.child(gameJoiningDataWrapper.game.name)
            .child("eventPool")
            .child(name)
            .setValue(value)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                } else {
                    Timber.tag(TAG).e(it.exception)
                }
            }
    }

    fun setPlayerInventoryAmount(name: String, value: Int) {
        ref.child(gameJoiningDataWrapper.game.name)
            .child("players")
            .child(currentUserId ?: "")
            .child("inventory")
            .child(name)
            .setValue(InventoryElement(name, value))
            .addOnCompleteListener {
                if (it.isSuccessful) {

                } else {
                    Timber.tag(TAG).e(it.exception)
                }
            }
    }

    fun updateGame(newGame: Game, onSuccess: () -> Unit = {}) {
        ref.child(gameJoiningDataWrapper.game.name)
            .setValue(newGame)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess.invoke()
                } else {
                    Timber.tag(TAG).e(it.exception)
                }
            }
    }

    fun getTeamStatistics(teamStr: String) : TeamStatistics {
        val coinMap = hashMapOf<String, Int>()
        var inventory = 0
        game.value.players.forEach {
            if (it.value.teamStr == teamStr) {
                it.value.coinsCollected.forEach { coinEntry ->
                    val old = coinMap[coinEntry.key] ?: 0
                    coinMap[coinEntry.key] = old + coinEntry.value
                }
                inventory += it.value.inventory.size
            }
        }
        var coins = 0
        var coinsInYellow = 0
        coinMap.forEach {
            coins += it.value
            coinsInYellow += it.value * GameUtils.getLayerNumber(GameUtils.Layers.valueOf(it.key))
        }

        val questionMap = hashMapOf<String, Int>()
        game.value.players.forEach {
            if (it.value.teamStr == teamStr) {
                it.value.questionsAnswered.forEach { questionEntry ->
                    val old = questionMap[questionEntry.key] ?: 0
                    questionMap[questionEntry.key] = old + questionEntry.value
                }
            }
        }
        var questions = 0
        var questionsInYellow = 0
        questionMap.forEach {
            questions += it.value
            questionsInYellow += it.value * GameUtils.getLayerNumber(GameUtils.Layers.valueOf(it.key))
        }
        return TeamStatistics(
            id = teamStr.hashCode().toLong(),
            team = teamStr,
            totalCoins = coins,
            totalCoinsInYellow = coinsInYellow,
            totalQuestions = questions,
            totalQuestionsInYellow = questionsInYellow,
            totalInventory = inventory
        )
    }

    companion object {
        private val TAG = GameViewModel::class.simpleName
    }
}