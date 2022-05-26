package ru.spbstu.feature.domain.model

import ru.spbstu.common.base.BaseModel
import ru.spbstu.common.model.Team

data class TeamFinalStats(
    override val id: Long = 0,
    val team: Team = Team.Red,
    val players: List<TeamPlayerIcon> = listOf(),
    val coinsCollected: HashMap<String, Int> = hashMapOf(),
    val questionsAnswered: HashMap<String, Int> = hashMapOf(),
) : BaseModel(id) {
    override fun isContentEqual(other: BaseModel): Boolean {
        return other is TeamFinalStats && team == other.team &&
                players == other.players && coinsCollected == other.coinsCollected &&
                questionsAnswered == other.questionsAnswered
    }
}