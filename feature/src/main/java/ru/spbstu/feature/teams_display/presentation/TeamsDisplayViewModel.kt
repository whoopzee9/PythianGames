package ru.spbstu.feature.teams_display.presentation

import android.os.CountDownTimer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.utils.GameJoiningDataWrapper

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
}