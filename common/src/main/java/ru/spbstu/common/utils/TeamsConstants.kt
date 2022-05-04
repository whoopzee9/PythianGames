package ru.spbstu.common.utils

import ru.spbstu.common.model.Team

object TeamsConstants {
    const val GREEN_TEAM = "green"
    const val BLUE_TEAM = "blue"
    const val RED_TEAM = "red"
    const val ORANGE_TEAM = "orange"

    fun getTeamFromString(teamStr: String): Team {
        return when (teamStr) {
            GREEN_TEAM -> Team.Green
            BLUE_TEAM -> Team.Blue
            RED_TEAM -> Team.Red
            ORANGE_TEAM -> Team.Orange
            else -> throw IllegalStateException("Wrong team name in ${Team::class.simpleName}")
        }
    }
}