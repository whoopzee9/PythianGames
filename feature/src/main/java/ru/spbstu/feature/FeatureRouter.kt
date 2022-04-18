package ru.spbstu.feature

import ru.spbstu.common.base.BaseBackRouter
import ru.spbstu.feature.room_connection.presentation.RoomConnectionFragment

interface FeatureRouter : BaseBackRouter {
    fun openRegistrationFragment()
    fun openLoginFragment()
    fun openAuthFragment()
    fun openOnboarding()
    fun openMainFragment()
    fun openRoomConnectionFragment(mode: RoomConnectionFragment.Companion.RoomMode)
    fun openTeamSelectionFragment()
    fun openCharacterSelectionFragment()
    fun openTeamDisplayFragment()
    fun openGameFragment()
}
