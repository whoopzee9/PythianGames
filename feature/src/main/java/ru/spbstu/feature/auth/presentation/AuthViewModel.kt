package ru.spbstu.feature.auth.presentation

import ru.spbstu.common.token.TokenRepository
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter

class AuthViewModel(val router: FeatureRouter, val tokenRepository: TokenRepository) : BackViewModel(router) {

    fun openLogin() {
        router.openLoginFragment()
    }

    fun openRegistration() {
        router.openRegistrationFragment()
    }

    fun openOnboarding() {
        router.openOnboarding()
    }
}