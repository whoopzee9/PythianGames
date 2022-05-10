package ru.spbstu.common.utils

object BoardConstants {
    private const val TOOTH_LAYER_1_SIZE_3 = 9
    private const val TOOTH_LAYER_1_SIZE_5 = 25

    private const val TOOTH_LAYER_2_SIZE_3 = 2
    private const val TOOTH_LAYER_2_SIZE_5 = 7

    private const val TOOTH_LAYER_3_SIZE_3 = 2
    private const val TOOTH_LAYER_3_SIZE_5 = 13

    private const val TOOTH_LAYER_4_SIZE_3 = 3
    private const val TOOTH_LAYER_4_SIZE_5 = 13

    private const val TOOTH_LAYER_5_SIZE_3 = 4
    private const val TOOTH_LAYER_5_SIZE_5 = 15

    fun getToothAmount(layer: Int, size: Int): Int {
        when (layer) {
            1 -> return when (size) {
                3 -> TOOTH_LAYER_1_SIZE_3
                5 -> TOOTH_LAYER_1_SIZE_5
                else -> throw IllegalStateException("Wrong board size in ${BoardConstants::class.simpleName}")
            }
            2 -> return when (size) {
                3 -> TOOTH_LAYER_2_SIZE_3
                5 -> TOOTH_LAYER_2_SIZE_5
                else -> throw IllegalStateException("Wrong board size in ${BoardConstants::class.simpleName}")
            }
            3 -> return when (size) {
                3 -> TOOTH_LAYER_3_SIZE_3
                5 -> TOOTH_LAYER_3_SIZE_5
                else -> throw IllegalStateException("Wrong board size in ${BoardConstants::class.simpleName}")
            }
            4 -> return when (size) {
                3 -> TOOTH_LAYER_4_SIZE_3
                5 -> TOOTH_LAYER_4_SIZE_5
                else -> throw IllegalStateException("Wrong board size in ${BoardConstants::class.simpleName}")
            }
            5 -> return when (size) {
                3 -> TOOTH_LAYER_5_SIZE_3
                5 -> TOOTH_LAYER_5_SIZE_5
                else -> throw IllegalStateException("Wrong board size in ${BoardConstants::class.simpleName}")
            }
            else -> throw IllegalStateException("Wrong layer in ${BoardConstants::class.simpleName}")
        }
    }
}