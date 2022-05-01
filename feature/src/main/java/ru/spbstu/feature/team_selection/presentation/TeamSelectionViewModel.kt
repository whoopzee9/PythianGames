package ru.spbstu.feature.team_selection.presentation

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.common.utils.DatabaseReferences
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.utils.GameJoiningDataWrapper
import timber.log.Timber

class TeamSelectionViewModel(
    private val router: FeatureRouter,
    val gameJoiningDataWrapper: GameJoiningDataWrapper
) : BackViewModel(router) {
    private val _state: MutableStateFlow<UIState> = MutableStateFlow(UIState.Initial)
    val state: StateFlow<UIState> = _state

    fun setTeam(teamStr: String) {
        gameJoiningDataWrapper.playerInfo =
            gameJoiningDataWrapper.playerInfo.copy(teamStr = teamStr)
    }

    fun saveTeam() {
        _state.value = UIState.Progress
        val database = Firebase.database
        val ref = database.getReference(DatabaseReferences.GAMES_REF)
        ref.child(gameJoiningDataWrapper.game.name)
            .child("players")
            .child(Firebase.auth.currentUser?.uid ?: "")
            .child("teamStr")
            .setValue(gameJoiningDataWrapper.playerInfo.teamStr)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _state.value = UIState.Success
                    router.openCharacterSelectionFragment()
                } else {
                    Timber.tag(TAG).e(it.exception)
                    _state.value = UIState.Failure
                }
            }
    }

    sealed class UIState {
        object Initial : UIState()
        object Progress : UIState()
        object Success : UIState()
        object Failure : UIState()
    }

    companion object {
        private val TAG = TeamSelectionViewModel::class.simpleName
    }
}