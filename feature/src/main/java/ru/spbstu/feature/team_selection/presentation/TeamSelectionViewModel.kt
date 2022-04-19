package ru.spbstu.feature.team_selection.presentation

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.common.utils.DatabaseReferences
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.utils.GameJoiningDataWrapper

class TeamSelectionViewModel(
    private val router: FeatureRouter,
    val gameJoiningDataWrapper: GameJoiningDataWrapper
) : BackViewModel(router) {
    fun openCharacterSelectionFragment() {
        router.openCharacterSelectionFragment()
    }
    fun setTeam(teamStr: String) {
        gameJoiningDataWrapper.playerInfo = gameJoiningDataWrapper.playerInfo.copy(teamStr = teamStr)
    }
}