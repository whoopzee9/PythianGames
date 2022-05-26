package ru.spbstu.feature.teams_display.presentation

import android.os.CountDownTimer
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

class TeamsDisplayViewModel(
    val router: FeatureRouter,
    val gameJoiningDataWrapper: GameJoiningDataWrapper
) : BackViewModel(router) {
    private val _timerData: MutableStateFlow<Long> =
        MutableStateFlow(1000L * 6)
    val timerData: StateFlow<Long> get() = _timerData

    val startGameTimer: CountDownTimer = object : CountDownTimer(1000L * 6, 1000L) {
        override fun onTick(millisUntilFinished: Long) {
            _timerData.value = millisUntilFinished
        }

        override fun onFinish() {
            router.openGameFragment()
        }
    }

    fun setUserLastGame() {
        val ref = Firebase.database.getReference(DatabaseReferences.USERS_REF)
        val currentUserId = Firebase.auth.currentUser?.uid

        ref.child(currentUserId ?: "")
            .child("lastGameName")
            .setValue(gameJoiningDataWrapper.game.name)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                } else {
                    Timber.tag(TAG).e(it.exception)
                }
            }
    }

    companion object {
        private val TAG = TeamsDisplayViewModel::class.simpleName
    }
}