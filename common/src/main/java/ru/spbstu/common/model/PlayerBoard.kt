package ru.spbstu.common.model

data class PlayerBoard(
    val player: Player = Player(),
    var x: Int = 0,
    var y: Int = 0
)