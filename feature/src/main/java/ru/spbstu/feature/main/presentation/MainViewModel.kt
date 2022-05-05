package ru.spbstu.feature.main.presentation

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.room_connection.presentation.RoomConnectionFragment

class MainViewModel(val router: FeatureRouter) : BackViewModel(router) {
    fun openRoomConnectionFragment(mode: RoomConnectionFragment.Companion.RoomMode) {
        router.openRoomConnectionFragment(mode)
    }

    fun logout() {
        val auth = Firebase.auth
        auth.signOut()
        router.openAuthFragment()
    }
}