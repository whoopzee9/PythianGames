package ru.spbstu.feature.test.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.InventoryModel
import kotlin.random.Random

class TestViewModel(router: FeatureRouter) : BackViewModel(router) {

    private val _testData: MutableStateFlow<List<InventoryModel>> = MutableStateFlow(listOf())
    val testData get() :StateFlow<List<InventoryModel>> = _testData

    init {
        _testData.value = Random.nextBytes(TEST_RANDOM_SIZE).mapIndexed { index, byte ->
            InventoryModel(index.toLong(), "$TEST_PREFIX$byte")
        }
    }

    fun removeItemById(id: Long) {
        _testData.value = _testData.value.filter { it.id != id }
    }

    private companion object {
        const val TEST_RANDOM_SIZE = 33
        const val TEST_PREFIX = "Test"
    }
}
