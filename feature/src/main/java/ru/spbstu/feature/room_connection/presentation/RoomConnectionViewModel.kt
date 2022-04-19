package ru.spbstu.feature.room_connection.presentation

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.common.utils.DatabaseReferences
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Game
import ru.spbstu.feature.utils.GameJoiningDataWrapper
import timber.log.Timber

class RoomConnectionViewModel(
    val router: FeatureRouter,
    private val gameJoiningDataWrapper: GameJoiningDataWrapper
) : BackViewModel(router) {

    private val _state: MutableStateFlow<UIState> = MutableStateFlow(UIState.Initial)
    val state: StateFlow<UIState> = _state

    fun createRoom(name: String, code: String, numOfTeams: Int, numOfPlayers: Int) {
        _state.value = UIState.Progress
        val database = Firebase.database
        val ref = database.getReference(DatabaseReferences.GAMES_REF)
        ref.child(name).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    _state.value = UIState.GameAlreadyExists
                } else {
                    val game = Game(
                        name = name,
                        code = code,
                        numOfTeams = numOfTeams,
                        numOfPlayers = numOfPlayers,
                        numOfPlayersJoined = 1
                    )
                    ref.child(name).setValue(game)
                    gameJoiningDataWrapper.game = game
                    _state.value = UIState.Success
                    router.openTeamSelectionFragment()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.tag(TAG).e(error.message)
                _state.value = UIState.Success
            }
        })
    }

    fun joinRoom(name: String, code: String) {
        _state.value = UIState.Progress
        val database = Firebase.database
        val ref = database.getReference(DatabaseReferences.GAMES_REF)
        ref.child(name).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val game = snapshot.getValue(Game::class.java)
                    if (game?.code == code) {
                        if (game.numOfPlayersJoined < game.numOfPlayers) {
                            ref.child(name).child("numOfPlayersJoined")
                                .setValue(game.numOfPlayersJoined + 1)
                            _state.value = UIState.Success
                            gameJoiningDataWrapper.game = game
                            router.openTeamSelectionFragment()
                        } else {
                            _state.value = UIState.GameIsFull
                        }
                    } else {
                        _state.value = UIState.WrongCode
                    }
                } else {
                    _state.value = UIState.GameDoesntExist
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.tag(TAG).e(error.message)
                _state.value = UIState.Success
            }
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