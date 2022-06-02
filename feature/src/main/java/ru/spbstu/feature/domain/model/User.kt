package ru.spbstu.feature.domain.model

import androidx.annotation.Keep

@Keep
data class User(
    val id: String = "",
    val email: String = "",
    val lastGameName: String? = null
)