package ru.spbstu.feature.room_connection.presentation

import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter

class RoomConnectionViewModel(val router: FeatureRouter) : BackViewModel(router) {
    fun createRoom(name: String, code: String) {
        router.openTeamSelectionFragment()
    }

    fun joinRoom(name: String, code: String) {
        router.openTeamSelectionFragment()
    }
}