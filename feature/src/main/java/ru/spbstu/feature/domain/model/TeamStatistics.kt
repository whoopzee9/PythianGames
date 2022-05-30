package ru.spbstu.feature.domain.model

import androidx.annotation.Keep
import ru.spbstu.common.base.BaseModel

@Keep
data class TeamStatistics(
    override val id: Long = 0,
    val team: String = "",
    val totalCoins: Int = 0,
    val totalCoinsInYellow: Int = 0,
    val totalQuestions: Int = 0,
    val totalQuestionsInYellow: Int = 0,
    val totalInventory: Int = 0
) : BaseModel(id) {
    override fun isContentEqual(other: BaseModel): Boolean {
        return other is TeamStatistics && team == other.team && totalCoins == other.totalCoins &&
                totalCoinsInYellow == other.totalCoinsInYellow &&
                totalQuestions == other.totalQuestions &&
                totalQuestionsInYellow == other.totalQuestionsInYellow &&
                totalInventory == other.totalInventory
    }
}