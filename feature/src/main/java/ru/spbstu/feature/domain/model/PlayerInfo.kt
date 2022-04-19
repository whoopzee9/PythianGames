package ru.spbstu.feature.domain.model

import androidx.annotation.DrawableRes
import ru.spbstu.common.R

data class PlayerInfo(
    val id: String = "",
    @DrawableRes val iconRes: Int = R.drawable.character_1,
    val teamStr: String = "",
    val playerNum: Int = 1,
    val name: String = ""
)