package ru.spbstu.feature.character_selection.presentation

import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter

class CharacterSelectionViewModel(val router: FeatureRouter) : BackViewModel(router) {
    var isCharacterSelected = false
}