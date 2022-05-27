package ru.spbstu.feature.domain.model

import ru.spbstu.feature.R

data class InventoryElement(
    val name: String = "",
    val amount: Int = 0
)

fun InventoryElement.toInventoryModel(id: Int): InventoryModel {
    val iconRes = when (name) {
        ToothResult.Sieve.name -> R.drawable.ic_sieve_78
        ToothResult.Rope.name -> R.drawable.ic_rope_78
        ToothResult.Brush.name -> R.drawable.ic_brush_78
        ToothResult.Bone.name -> R.drawable.ic_bone_155
        else -> throw IllegalStateException("Wrong inventory name!")
    }
    return InventoryModel(
        id = id.toLong(),
        name = name,
        iconRes = iconRes
    )
}