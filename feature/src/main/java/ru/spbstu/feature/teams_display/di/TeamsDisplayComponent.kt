package ru.spbstu.feature.teams_display.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.teams_display.presentation.TeamsDisplayFragment

@Subcomponent(
    modules = [
        TeamsDisplayModule::class
    ]
)
@ScreenScope
interface TeamsDisplayComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): TeamsDisplayComponent
    }

    fun inject(teamsDisplayFragment: TeamsDisplayFragment)
}