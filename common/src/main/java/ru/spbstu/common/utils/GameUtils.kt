package ru.spbstu.common.utils

import ru.spbstu.common.model.Position

object GameUtils {
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
                else -> throw IllegalStateException("Wrong board size in ${GameUtils::class.simpleName}")
            }
            2 -> return when (size) {
                3 -> TOOTH_LAYER_2_SIZE_3
                5 -> TOOTH_LAYER_2_SIZE_5
                else -> throw IllegalStateException("Wrong board size in ${GameUtils::class.simpleName}")
            }
            3 -> return when (size) {
                3 -> TOOTH_LAYER_3_SIZE_3
                5 -> TOOTH_LAYER_3_SIZE_5
                else -> throw IllegalStateException("Wrong board size in ${GameUtils::class.simpleName}")
            }
            4 -> return when (size) {
                3 -> TOOTH_LAYER_4_SIZE_3
                5 -> TOOTH_LAYER_4_SIZE_5
                else -> throw IllegalStateException("Wrong board size in ${GameUtils::class.simpleName}")
            }
            5 -> return when (size) {
                3 -> TOOTH_LAYER_5_SIZE_3
                5 -> TOOTH_LAYER_5_SIZE_5
                else -> throw IllegalStateException("Wrong board size in ${GameUtils::class.simpleName}")
            }
            else -> throw IllegalStateException("Wrong layer in ${GameUtils::class.simpleName}")
        }
    }

    fun getPlayerStartPosition(
        numOfTeams: Int,
        numOfPlayers: Int,
        teamStr: String,
        playerNum: Int
    ): Position {
        when (numOfPlayers) {
            2 -> {
                when (teamStr) {
                    TeamsConstants.GREEN_TEAM -> return Position(0, 0)
                    TeamsConstants.BLUE_TEAM -> return Position(2, 2)
                }
            }
            4 -> {
                when (numOfTeams) {
                    2 -> when (teamStr) {
                        TeamsConstants.GREEN_TEAM -> when (playerNum) {
                            1 -> return Position(0, 0)
                            2 -> return Position(0, 0)
                        }
                        TeamsConstants.BLUE_TEAM -> when (playerNum) {
                            1 -> return Position(2, 0)
                            2 -> return Position(0, 2)
                        }
                    }
                    4 -> when (teamStr) {
                        TeamsConstants.GREEN_TEAM -> return Position(0, 0)
                        TeamsConstants.BLUE_TEAM -> return Position(2, 2)
                        TeamsConstants.ORANGE_TEAM -> return Position(2, 0)
                        TeamsConstants.RED_TEAM -> return Position(0, 2)
                    }
                }
            }
            6 -> {
                when (teamStr) {
                    TeamsConstants.GREEN_TEAM -> when (playerNum) {
                        1 -> return Position(0, 0)
                        2 -> return Position(4, 4)
                    }
                    TeamsConstants.BLUE_TEAM -> when (playerNum) {
                        1 -> return Position(4, 0)
                        2 -> return Position(0, 4)
                    }
                    TeamsConstants.RED_TEAM -> when (playerNum) {
                        1 -> return Position(0, 2)
                        2 -> return Position(4, 2)
                    }
                }
            }
            8 -> {
                when (numOfTeams) {
                    2 -> {
                        when (teamStr) {
                            TeamsConstants.GREEN_TEAM -> when (playerNum) {
                                1 -> return Position(0, 0)
                                2 -> return Position(4, 4)
                                3 -> return Position(4, 0)
                                4 -> return Position(0, 4)
                            }
                            TeamsConstants.BLUE_TEAM -> when (playerNum) {
                                1 -> return Position(2, 0)
                                2 -> return Position(2, 4)
                                3 -> return Position(0, 2)
                                4 -> return Position(4, 2)
                            }
                        }
                    }
                    4 -> {
                        when (teamStr) {
                            TeamsConstants.GREEN_TEAM -> when (playerNum) {
                                1 -> return Position(0, 0)
                                2 -> return Position(4, 4)
                            }
                            TeamsConstants.BLUE_TEAM -> when (playerNum) {
                                1 -> return Position(4, 0)
                                2 -> return Position(0, 4)
                            }
                            TeamsConstants.ORANGE_TEAM -> when (playerNum) {
                                1 -> return Position(2, 0)
                                2 -> return Position(2, 4)
                            }
                            TeamsConstants.RED_TEAM -> when (playerNum) {
                                1 -> return Position(0, 2)
                                2 -> return Position(4, 2)
                            }
                        }
                    }
                }
            }
        }
        throw IllegalStateException("Wrong player state in ${GameUtils::class.simpleName}")
    }

    fun getPlayerOrderNumber(
        numOfTeams: Int,
        numOfPlayers: Int,
        teamStr: String,
        playerNum: Int
    ): Int {
        when (numOfPlayers) {
            2 -> {
                when (teamStr) {
                    TeamsConstants.GREEN_TEAM -> return 1
                    TeamsConstants.BLUE_TEAM -> return 2
                }
            }
            4 -> {
                when (numOfTeams) {
                    2 -> when (teamStr) {
                        TeamsConstants.GREEN_TEAM -> return 1 + (numOfTeams * (playerNum - 1))
                        TeamsConstants.BLUE_TEAM -> return 2 + (numOfTeams * (playerNum - 1))
                    }
                    4 -> when (teamStr) {
                        TeamsConstants.GREEN_TEAM -> return 1
                        TeamsConstants.BLUE_TEAM -> return 3
                        TeamsConstants.ORANGE_TEAM -> return 2
                        TeamsConstants.RED_TEAM -> return 4
                    }
                }
            }
            6 -> {
                when (teamStr) {
                    TeamsConstants.GREEN_TEAM -> return 1 + (numOfTeams * (playerNum - 1))
                    TeamsConstants.BLUE_TEAM -> return 2 + (numOfTeams * (playerNum - 1))
                    TeamsConstants.RED_TEAM -> return 3 + (numOfTeams * (playerNum - 1))
                }
            }
            8 -> {
                when (numOfTeams) {
                    2 -> {
                        when (teamStr) {
                            TeamsConstants.GREEN_TEAM -> return 1 + (numOfTeams * (playerNum - 1))
                            TeamsConstants.BLUE_TEAM -> return 2 + (numOfTeams * (playerNum - 1))
                        }
                    }
                    4 -> {
                        when (teamStr) {
                            TeamsConstants.GREEN_TEAM -> return 1 + (numOfTeams * (playerNum - 1))
                            TeamsConstants.BLUE_TEAM -> return 2 + (numOfTeams * (playerNum - 1))
                            TeamsConstants.ORANGE_TEAM -> return 3 + (numOfTeams * (playerNum - 1))
                            TeamsConstants.RED_TEAM -> return 4 + (numOfTeams * (playerNum - 1))
                        }
                    }
                }
            }
        }
        throw IllegalStateException("Wrong player state in ${GameUtils::class.simpleName}")
    }
}