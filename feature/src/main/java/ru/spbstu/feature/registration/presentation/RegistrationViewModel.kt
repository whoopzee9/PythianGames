package ru.spbstu.feature.registration.presentation

import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter
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