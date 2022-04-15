package ru.spbstu.feature.domain.model

import androidx.annotation.DrawableRes
import ru.spbstu.common.base.BaseModel

data class InventoryModel(
    override val id: Long = 0,
    val name: String = "",
    @DrawableRes val iconRes: Int = 0
) : BaseModel(id) {

    override fun isContentEqual(other: BaseModel): Boolean =
        other is InventoryModel && name == other.name && iconRes == other.iconRes
}
