package ru.spbstu.feature.character_selection.presentation

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.extenstions.readValue
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.common.utils.DatabaseReferences
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.PlayerInfo
import ru.spbstu.feature.utils.GameJoiningDataWrapper
import timber.log.Timber

class CharacterSelectionViewModel(
    private val router: FeatureRouter,
    val gameJoiningDataWrapper: GameJoiningDataWrapper
) : BackViewModel(router) {
    var selectedCharacterNum = 0

    private val _state: MutableStateFlow<UIState> = MutableStateFlow(UIState.Initial)
    val state: StateFlow<UIState> = _state

    fun setCharacter(resId: Int, name: String) {
        _state.value = UIState.Progress
        val database = Firebase.database
        val ref = database.getReference(DatabaseReferences.GAMES_REF)
        ref.child(gameJoiningDataWrapper.game.name).child("players").readValue(
            onSuccess = { snapshot ->
                val generic = object : GenericTypeIndicator<HashMap<String, PlayerInfo>>() {}
                val players = snapshot.getValue(generic)
                val team = gameJoiningDataWrapper.playerInfo.teamStr
                val alreadyJoined = players?.count { it.value.teamStr == team } ?: 0

                val currPlayer = gameJoiningDataWrapper.playerInfo.copy(
                    iconRes = resId,
                    playerNum = alreadyJoined + 1,
                    name = name
                )
                ref.child(gameJoiningDataWrapper.game.name)
                    .child("players")
                    .child(Firebase.auth.currentUser?.uid ?: "")
                    .setValue(currPlayer)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            router.openTeamDisplayFragment()
                            _state.value = UIState.Success
                        } else {
                            Timber.tag(TAG).e(it.exception)
                            _state.value = UIState.Success
                        }
                    }
            }, onCancelled = { error ->
                Timber.tag(TAG).e(error.message)
                _state.value = UIState.Failure
            }
        )
    }

    sealed class UIState {
        object Initial : UIState()
        object Progress : UIState()
        object Success : UIState()
        object Failure : UIState()
    }

    companion object {
        private val TAG = CharacterSelectionViewModel::class.simpleName
    }
}