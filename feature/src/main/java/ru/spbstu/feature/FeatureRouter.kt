package ru.spbstu.feature

import ru.spbstu.common.base.BaseBackRouter

interface FeatureRouter : BaseBackRouter {
    fun openRegistrationFragment()
    fun openLoginFragment()
    fun openAuthFragment()
    fun openOnboarding()
    fun openMainFragment()
}
