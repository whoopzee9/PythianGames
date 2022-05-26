package ru.spbstu.feature.domain.model

import androidx.annotation.DrawableRes
import ru.spbstu.common.base.BaseModel

data class InventoryPlayer(
    val playerId: String = "",
    override val id: Long = playerId.hashCode().toLong(),
    @DrawableRes val iconRes: Int = 0,
    val isSelected: Boolean = false
) : BaseModel(id) {
    override fun isContentEqual(other: BaseModel): Boolean {
        return other is InventoryPlayer && playerId == other.playerId && iconRes == other.iconRes &&
                isSelected == other.isSelected
    }
}