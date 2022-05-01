package ru.spbstu.feature.registration.presentation

import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.common.utils.DatabaseReferences
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.domain.model.User
import timber.log.Timber

class RegistrationViewModel(val router: FeatureRouter) : BackViewModel(router) {

    private val _state: MutableStateFlow<UIState> = MutableStateFlow(UIState.Initial)
    val state: StateFlow<UIState> = _state

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun arePasswordsMatch(password: String, repeatPassword: String): Boolean {
        return password == repeatPassword
    }

    fun register(auth: FirebaseAuth, email: String, password: String) {
        _state.value = UIState.Progress
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                createUser(email)
            } else {
                Timber.tag(TAG).e(it.exception)
                _state.value = UIState.Failure
            }
        }
    }

    private fun createUser(email: String) {
        val database = Firebase.database
        val userId = Firebase.auth.currentUser?.uid ?: ""
        val userRef = database.getReference(DatabaseReferences.USERS_REF)
        userRef.child(userId).setValue(User(userId, email)).addOnCompleteListener {
            if (it.isSuccessful) {
                _state.value = UIState.Success
                router.openMainFragment()
            } else {
                Timber.tag(TAG).e(it.exception)
                _state.value = UIState.Failure
            }
        }
    }

    sealed class UIState {
        object Initial: UIState()
        object Progress: UIState()
        object Success: UIState()
        object Failure: UIState()
    }

    companion object {
        private val TAG = RegistrationViewModel::class.simpleName
    }
}