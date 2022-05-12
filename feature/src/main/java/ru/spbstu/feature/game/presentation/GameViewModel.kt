package ru.spbstu.feature.game.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Game
import ru.spbstu.feature.utils.GameJoiningDataWrapper

class GameViewModel(
    val router: FeatureRouter,
    val gameJoiningDataWrapper: GameJoiningDataWrapper
) : BackViewModel(router) {
    private val _game: MutableStateFlow<Game> = MutableStateFlow(Game())
    val game: StateFlow<Game> = _game

    fun setGame(game: Game) {
        _game.value = game
    }
}