package ru.spbstu.feature.registration.presentation

import android.util.Patterns
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter

class RegistrationViewModel(val router: FeatureRouter) : BackViewModel(router) {

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun arePasswordsMatch(password: String, repeatPassword: String): Boolean {
        return password == repeatPassword
    }

    fun register(email: String, password: String) {
        TODO("Not yet implemented")
    }
}