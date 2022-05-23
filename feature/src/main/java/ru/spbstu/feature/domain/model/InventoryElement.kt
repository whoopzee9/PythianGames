package ru.spbstu.feature.domain.model

import ru.spbstu.feature.R

data class InventoryElement(
    val name: String = "",
    val amount: Int = 0
)

fun InventoryElement.toInventoryModel(): InventoryModel {
    val iconRes = when (name) {
        ToothResult.Sieve.name -> R.drawable.ic_sieve_78
        ToothResult.Rope.name -> R.drawable.ic_rope_78
        ToothResult.Brush.name -> R.drawable.ic_brush_78
        else -> throw IllegalStateException("Wrong inventory name!")
    }
    return InventoryModel(
        name = name,
        iconRes = iconRes
    )
}