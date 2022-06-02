package ru.spbstu.feature.team_score.presentation

import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.common.utils.TeamsConstants
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.TeamFinalStats
import ru.spbstu.feature.domain.model.TeamPlayerIcon
import ru.spbstu.feature.utils.GameJoiningDataWrapper

class TeamScoreViewModel(
    val router: FeatureRouter,
    val gameJoiningDataWrapper: GameJoiningDataWrapper
) : BackViewModel(router) {

    fun getTeamsStats(): List<TeamFinalStats> {
        val list = mutableListOf<TeamFinalStats>()
        val teams = mutableSetOf<String>()
        gameJoiningDataWrapper.game.players.forEach { entry ->
            teams.add(entry.value.teamStr)
        }
        teams.forEach {
            list.add(getTeamStatistics(it))
        }
        return list
    }

    private fun getTeamStatistics(teamStr: String): TeamFinalStats {
        val coinMap = hashMapOf<String, Int>()
        var inventory = 0
        gameJoiningDataWrapper.game.players.forEach {
            if (it.value.teamStr == teamStr) {
                it.value.coinsCollected.forEach { coinEntry ->
                    val old = coinMap[coinEntry.key] ?: 0
                    coinMap[coinEntry.key] = old + coinEntry.value
                }
                inventory += it.value.inventory.size
            }
        }

        val questionMap = hashMapOf<String, Int>()
        gameJoiningDataWrapper.game.players.forEach {
            if (it.value.teamStr == teamStr) {
                it.value.questionsAnswered.forEach { questionEntry ->
                    val old = questionMap[questionEntry.key] ?: 0
                    questionMap[questionEntry.key] = old + questionEntry.value
                }
            }
        }

        val players = mutableListOf<TeamPlayerIcon>()
        gameJoiningDataWrapper.game.players.forEach {
            if (it.value.teamStr == teamStr) {
                players.add(TeamPlayerIcon(playerId = it.value.id, iconRes = it.value.iconRes))
            }
        }

        return TeamFinalStats(
            id = teamStr.hashCode().toLong(),
            team = TeamsConstants.getTeamFromString(teamStr),
            players = players,
            coinsCollected = coinMap,
            questionsAnswered = questionMap
        )
    }
}