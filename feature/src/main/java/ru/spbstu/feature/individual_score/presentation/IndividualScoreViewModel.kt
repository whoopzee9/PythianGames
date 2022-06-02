package ru.spbstu.feature.individual_score.presentation

import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.common.utils.GameUtils
import ru.spbstu.common.utils.TeamsConstants
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.PlayerFinalStats
import ru.spbstu.feature.utils.GameJoiningDataWrapper

class IndividualScoreViewModel(
    val router: FeatureRouter,
    val gameJoiningDataWrapper: GameJoiningDataWrapper
) : BackViewModel(router) {

    fun getPlayersStats(): List<PlayerFinalStats> {
        return gameJoiningDataWrapper.game.players.map {
            PlayerFinalStats(
                playerId = it.value.id,
                name = it.value.name,
                team = TeamsConstants.getTeamFromString(it.value.teamStr),
                imageRes = it.value.iconRes,
                coinsCollected = it.value.coinsCollected,
                questionsAnswered = it.value.questionsAnswered
            )
        }
    }
}