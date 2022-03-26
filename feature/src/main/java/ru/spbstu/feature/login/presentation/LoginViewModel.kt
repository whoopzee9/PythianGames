package ru.spbstu.feature.login.presentation

import android.util.Patterns
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter

class LoginViewModel(val router: FeatureRouter) : BackViewModel(router) {
    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun login(email: String, password: String) {

    }
}