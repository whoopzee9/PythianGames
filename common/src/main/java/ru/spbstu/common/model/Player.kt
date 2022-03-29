package ru.spbstu.common.model

import androidx.annotation.DrawableRes

data class Player(
    @DrawableRes val iconRes: Int,
    val teamNum: Int,
    val playerNum: Int,
    val name: String
)