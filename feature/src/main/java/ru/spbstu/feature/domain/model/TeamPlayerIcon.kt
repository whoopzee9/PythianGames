package ru.spbstu.feature.domain.model

import androidx.annotation.DrawableRes
import ru.spbstu.common.base.BaseModel

data class TeamPlayerIcon(
    val playerId: String = "",
    override val id: Long = playerId.hashCode().toLong(),
    @DrawableRes val iconRes: Int = 0
): BaseModel(id) {
    override fun isContentEqual(other: BaseModel): Boolean {
        return other is TeamPlayerIcon && playerId == other.playerId && iconRes == other.iconRes
    }
}