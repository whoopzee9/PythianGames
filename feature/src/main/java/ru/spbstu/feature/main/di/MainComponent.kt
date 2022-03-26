package ru.spbstu.feature.main.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.main.presentation.MainFragment

@Subcomponent(
    modules = [
        MainModule::class
    ]
)
@ScreenScope
interface MainComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): MainComponent
    }

    fun inject(mainFragment: MainFragment)
}