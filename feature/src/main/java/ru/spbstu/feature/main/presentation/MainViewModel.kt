package ru.spbstu.feature.main.presentation

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.extenstions.readValue
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.common.utils.DatabaseReferences
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Game
import ru.spbstu.feature.domain.model.PlayerInfo
import ru.spbstu.feature.domain.model.User
import ru.spbstu.feature.room_connection.presentation.RoomConnectionFragment
import ru.spbstu.feature.utils.GameJoiningDataWrapper
import timber.log.Timber

class MainViewModel(val router: FeatureRouter, val gameJoiningDataWrapper: GameJoiningDataWrapper) :
    BackViewModel(router) {

    var lastGameName: String? = null
    val currentUserId = Firebase.auth.currentUser?.uid
    private val ref = Firebase.database.getReference(DatabaseReferences.USERS_REF)

    private val _game: MutableStateFlow<Game> = MutableStateFlow(Game())
    val game: StateFlow<Game> = _game

    fun setGame(game: Game) {
        _game.value = game
    }

    fun openRoomConnectionFragment(mode: RoomConnectionFragment.Companion.RoomMode) {
        router.openRoomConnectionFragment(mode)
    }

    fun logout() {
        val auth = Firebase.auth
        auth.signOut()
        router.openAuthFragment()
    }

    fun getUserInfo(onSuccess: () -> Unit) {
        ref.child(currentUserId ?: "")
            .readValue(onSuccess = {
                val user = it.getValue(User::class.java)
                if (user != null) {
                    lastGameName = user.lastGameName
                    onSuccess.invoke()
                }
            }, onCancelled = {
                Timber.tag(TAG).e(it.message)
            })
    }

    fun openGameFragment() {
        Log.d("qwerty", "open GameFragment")
        gameJoiningDataWrapper.game = Game(name = lastGameName ?: "")
        router.openGameFragment()
    }

    fun openTeamSelectionFragment() {
        Log.d("qwerty", "open TeamSelectionFragment")
        gameJoiningDataWrapper.game = Game(name = lastGameName ?: "")
        router.openTeamSelectionFragment()
    }

    fun openCharacterSelectionFragment() {
        Log.d("qwerty", "open CharacterSelectionFragment")
        gameJoiningDataWrapper.game = game.value
        gameJoiningDataWrapper.playerInfo = game.value.players[currentUserId] ?: PlayerInfo()
        router.openCharacterSelectionFragment()
    }

    fun openTeamsDisplayFragment() {
        Log.d("qwerty", "open TeamsDisplayragment")
        gameJoiningDataWrapper.game = game.value
        router.openTeamDisplayFragment()
    }

    fun openCreditsFragment() {
        router.openCreditsFragment()
    }

    companion object {
        private val TAG = MainViewModel::class.simpleName
    }
}