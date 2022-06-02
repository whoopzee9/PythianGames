package ru.spbstu.feature.onboarding.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.model.EventState
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter

class OnboardingViewModel(val router: FeatureRouter) : BackViewModel(router) {

    private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.First)
    val uiState: StateFlow<UIState> = _uiState

    fun nextState() {
        when (uiState.value) {
            UIState.First -> _uiState.value = UIState.Second
            UIState.Second -> _uiState.value = UIState.Third
            UIState.Third -> router.openAuthFragment()
        }
    }

    sealed class UIState {
        object First: UIState()
        object Second: UIState()
        object Third: UIState()
    }
}