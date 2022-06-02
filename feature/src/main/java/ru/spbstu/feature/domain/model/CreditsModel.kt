package ru.spbstu.feature.domain.model

import ru.spbstu.common.base.BaseModel

data class CreditsModel(
    override val id: Long = 0,
    val title: String = "",
    val desc: String = ""
): BaseModel(id) {
    override fun isContentEqual(other: BaseModel): Boolean {
        return other is CreditsModel && title == other.title && desc == other.desc
    }
}