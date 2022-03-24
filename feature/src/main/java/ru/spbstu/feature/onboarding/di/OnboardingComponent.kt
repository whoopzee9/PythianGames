package ru.spbstu.feature.onboarding.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.onboarding.presentation.OnboardingFragment

@Subcomponent(
    modules = [
        OnboardingModule::class
    ]
)
@ScreenScope
interface OnboardingComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): OnboardingComponent
    }

    fun inject(onboardingFragment: OnboardingFragment)
}