package ru.spbstu.feature.main.presentation

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.spbstu.common.extenstions.readValue
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.common.utils.DatabaseReferences
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.Game
import ru.spbstu.feature.domain.model.User
import ru.spbstu.feature.room_connection.presentation.RoomConnectionFragment
import ru.spbstu.feature.utils.GameJoiningDataWrapper
import timber.log.Timber

class MainViewModel(val router: FeatureRouter, val gameJoiningDataWrapper: GameJoiningDataWrapper) :
    BackViewModel(router) {

    var lastGameName: String? = null
    private val currentUserId = Firebase.auth.currentUser?.uid
    private val ref = Firebase.database.getReference(DatabaseReferences.USERS_REF)

    fun openRoomConnectionFragment(mode: RoomConnectionFragment.Companion.RoomMode) {
        router.openRoomConnectionFragment(mode)
    }

    fun logout() {
        val auth = Firebase.auth
        auth.signOut()
        router.openAuthFragment()
    }

    fun getUserInfo(onSuccess: () -> Unit) {
        ref.child(currentUserId ?: "")
            .readValue(onSuccess = {
                val user = it.getValue(User::class.java)
                if (user != null) {
                    lastGameName = user.lastGameName
                    onSuccess.invoke()
                }
            }, onCancelled = {
                Timber.tag(TAG).e(it.message)
            })
    }

    fun openGameFragment() {
        gameJoiningDataWrapper.game = Game(name = lastGameName ?: "")
        router.openGameFragment()
    }

    fun openCreditsFragment() {
        router.openCreditsFragment()
    }

    companion object {
        private val TAG = MainViewModel::class.simpleName
    }
}