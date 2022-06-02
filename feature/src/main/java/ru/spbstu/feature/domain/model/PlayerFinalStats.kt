package ru.spbstu.feature.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import ru.spbstu.common.base.BaseModel
import ru.spbstu.common.model.Team

@Keep
data class PlayerFinalStats(
    val playerId: String = "",
    override val id: Long = playerId.hashCode().toLong(),
    val name: String = "",
    val team: Team = Team.Red,
    @DrawableRes val imageRes: Int = 0,
    val coinsCollected: HashMap<String, Int> = hashMapOf(),
    val questionsAnswered: HashMap<String, Int> = hashMapOf(),
): BaseModel(id) {
    override fun isContentEqual(other: BaseModel): Boolean {
        return other is PlayerFinalStats && playerId == other.playerId && name == other.name &&
                coinsCollected == other.coinsCollected &&
                questionsAnswered == other.questionsAnswered
    }
}