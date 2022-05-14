package ru.spbstu.common.model

data class MovingPossibilities(
    val canDig: Boolean = false,
    val isCleaning: Boolean = false,
    val canBet: Boolean = false,
    val position: Position = Position(),
    val layer: Int = 0
)