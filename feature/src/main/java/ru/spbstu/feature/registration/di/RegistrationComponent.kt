package ru.spbstu.feature.registration.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.registration.presentation.RegistrationFragment

@Subcomponent(
    modules = [
        RegistrationModule::class
    ]
)
@ScreenScope
interface RegistrationComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): RegistrationComponent
    }

    fun inject(registrationFragment: RegistrationFragment)
}