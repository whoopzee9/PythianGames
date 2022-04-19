package ru.spbstu.feature.character_selection.presentation

import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.utils.GameJoiningDataWrapper

class CharacterSelectionViewModel(
    val router: FeatureRouter,
    private val gameJoiningDataWrapper: GameJoiningDataWrapper
) : BackViewModel(router) {
    var isCharacterSelected = false
}