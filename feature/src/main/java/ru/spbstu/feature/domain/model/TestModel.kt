package ru.spbstu.feature.domain.model

import ru.spbstu.common.base.BaseModel

data class TestModel(
    override val id: Long = 0,
    val value: String = ""
) : BaseModel(id) {

    override fun isContentEqual(other: BaseModel): Boolean =
        other is TestModel && value == other.value
}
