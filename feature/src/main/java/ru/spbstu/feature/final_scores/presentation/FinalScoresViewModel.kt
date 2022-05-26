package ru.spbstu.feature.final_scores.presentation

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.common.utils.DatabaseReferences
import ru.spbstu.feature.FeatureRouter
import timber.log.Timber

class FinalScoresViewModel(val router: FeatureRouter) : BackViewModel(router) {
    fun openMainFragment() {
        router.openMainFragment()
    }

    fun clearUserLastGame(onSuccess: () -> Unit) {
        val ref = Firebase.database.getReference(DatabaseReferences.USERS_REF)
        val currentUserId = Firebase.auth.currentUser?.uid

        ref.child(currentUserId ?: "")
            .child("lastGameName")
            .setValue(null)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess.invoke()
                } else {
                    Timber.tag(TAG).e(it.exception)
                }
            }
    }

    companion object {
        private val TAG = FinalScoresViewModel::class.simpleName
    }
}