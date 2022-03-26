package ru.spbstu.feature.main.presentation

import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.room_connection.presentation.RoomConnectionFragment

class MainViewModel(val router: FeatureRouter) : BackViewModel(router) {
    fun openRoomConnectionFragment(mode: RoomConnectionFragment.Companion.RoomMode) {
        router.openRoomConnectionFragment(mode)
    }
}