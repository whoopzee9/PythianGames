package ru.spbstu.feature.room_connection.presentation

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.extenstions.readValue
import ru.spbstu.common.model.Card
import ru.spbstu.common.model.CardType
import ru.spbstu.common.model.Question
import ru.spbstu.common.model.QuestionGame
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.common.utils.DatabaseReferences
import ru.spbstu.common.utils.GameUtils
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Game
import ru.spbstu.feature.domain.model.PlayerInfo
import ru.spbstu.feature.domain.model.ToothResult
import ru.spbstu.feature.utils.GameJoiningDataWrapper
import timber.log.Timber

class RoomConnectionViewModel(
    val router: FeatureRouter,
    private val gameJoiningDataWrapper: GameJoiningDataWrapper
) : BackViewModel(router) {

    private val _state: MutableStateFlow<UIState> = MutableStateFlow(UIState.Initial)
    val state: StateFlow<UIState> = _state

    fun createRoom(name: String, numOfTeams: Int, numOfPlayers: Int) {
        _state.value = UIState.Progress
        val database = Firebase.database
        val ref = database.getReference(DatabaseReferences.GAMES_REF)

        ref.child(name).readValue(onSuccess = { snapshot ->
            if (snapshot.exists()) {
                _state.value = UIState.GameAlreadyExists
            } else {
                val id = Firebase.auth.currentUser?.uid ?: ""
                val boardSize = if (numOfPlayers > 4) 5 else 3
                val inventory = hashMapOf(
                    ToothResult.Sieve.name to 16,
                    ToothResult.Brush.name to 8,
                    ToothResult.Rope.name to 12,
                )
                val events = hashMapOf(
                    ToothResult.Treasure.name to 12,
                    ToothResult.ToolLoss.name to 8,
                    ToothResult.Collapse.name to 6,
                    ToothResult.Rainfall.name to 2,
                    ToothResult.RiverVertical.name to 2,
                    ToothResult.RiverHorizontal.name to 2,
                    ToothResult.Cavern.name to 4,
                )
                setupLayers(boardSize) { cards ->
                    val game = Game(
                        name = name,
                        numOfTeams = numOfTeams,
                        numOfPlayers = numOfPlayers,
                        numOfPlayersJoined = 1,
                        creatorId = id,
                        players = hashMapOf(id to PlayerInfo(id = id)),
                        cards = cards,
                        inventoryPool = inventory,
                        eventPool = events
                    )
                    ref.child(name).setValue(game).addOnCompleteListener {
                        if (it.isSuccessful) {
                            gameJoiningDataWrapper.game = game
                            gameJoiningDataWrapper.playerInfo = PlayerInfo(id = id)
                            _state.value = UIState.Success
                            router.openTeamSelectionFragment()
                        } else {
                            Timber.tag(TAG).e(it.exception)
                            it.exception?.printStackTrace()
                            _state.value = UIState.Failure
                        }
                    }
                }
            }
        }, onCancelled = { error ->
            Timber.tag(TAG).e(error.message)
            _state.value = UIState.Failure
        })
    }

    private fun setupLayers(boardSize: Int, onSuccess: (List<Card>) -> Unit) {
        val database = Firebase.database
        val questionsRef = database.getReference(DatabaseReferences.QUESTIONS_REF)
        questionsRef.readValue(onSuccess = { snapshot ->
            val generic = object : GenericTypeIndicator<HashMap<String, Question>>() {}
            val questions = snapshot.getValue(generic)
            if (questions != null) {
                val cardList = mutableListOf<Card>()
                for (i in 1..5) {
                    val layerQuestions =
                        questions.filter { it.value.questionTier == i }.values.shuffled()
                    val toothAmount = GameUtils.getToothAmount(i, boardSize)
                    val currCards = mutableListOf<Card>()
                    for (j in 0 until toothAmount) {
                        currCards.add(Card(layer = i, type = CardType.Tooth))
                    }
                    for (j in 0 until boardSize * boardSize - toothAmount) {
                        currCards.add(
                            Card(
                                layer = i,
                                type = CardType.Question,
                                question = QuestionGame(question = layerQuestions[j])
                            )
                        )
                    }
                    currCards.shuffle()
                    currCards.forEachIndexed { index, card ->
                        card.cardNum = index + 1
                    }
                    cardList.addAll(currCards)
                }
                onSuccess(cardList)
            }
        }, onCancelled = { error ->
            Timber.tag(TAG).e(error.message)
            _state.value = UIState.Failure
        })
    }

    fun joinRoom(name: String) {
        _state.value = UIState.Progress
        val database = Firebase.database
        val ref = database.getReference(DatabaseReferences.GAMES_REF)
        ref.child(name).readValue(onSuccess = { snapshot ->
            if (snapshot.exists()) {
                val game = snapshot.getValue(Game::class.java)
                if (game != null) { //game?.code == code
                    if (game.numOfPlayersJoined < game.numOfPlayers) {
                        ref.child(name).child("numOfPlayersJoined")
                            .setValue(game.numOfPlayersJoined + 1)
                        val id = Firebase.auth.currentUser?.uid ?: ""
                        ref.child(name)
                            .child("players")
                            .child(id)
                            .setValue(PlayerInfo(id = id)).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    _state.value = UIState.Success
                                    gameJoiningDataWrapper.game = game
                                    gameJoiningDataWrapper.playerInfo = PlayerInfo(id = id)
                                    router.openTeamSelectionFragment()
                                } else {
                                    Timber.tag(TAG).e(it.exception)
                                    _state.value = UIState.Failure
                                }
                            }
                    } else {
                        _state.value = UIState.GameIsFull
                    }
                } else {
                    _state.value = UIState.WrongCode
                }
            } else {
                _state.value = UIState.GameDoesntExist
            }
        }, onCancelled = { error ->
            Timber.tag(TAG).e(error.message)
            _state.value = UIState.Failure
        })
    }

    sealed class UIState {
        object Initial : UIState()
        object Progress : UIState()
        object Success : UIState()
        object GameAlreadyExists : UIState()
        object GameDoesntExist : UIState()
        object GameIsFull : UIState()
        object WrongCode : UIState()
        object Failure : UIState()
    }

    companion object {
        private val TAG = RoomConnectionViewModel::class.simpleName
    }
}