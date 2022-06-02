package ru.spbstu.feature.login.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.login.presentation.LoginFragment

@Subcomponent(
    modules = [
        LoginModule::class
    ]
)
@ScreenScope
interface LoginComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): LoginComponent
    }

    fun inject(loginFragment: LoginFragment)
}