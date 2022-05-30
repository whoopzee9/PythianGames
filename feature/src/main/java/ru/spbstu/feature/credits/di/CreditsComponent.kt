package ru.spbstu.feature.credits.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.credits.presentation.CreditsFragment

@Subcomponent(
    modules = [
        CreditsModule::class
    ]
)
@ScreenScope
interface CreditsComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): CreditsComponent
    }

    fun inject(creditsFragment: CreditsFragment)
}