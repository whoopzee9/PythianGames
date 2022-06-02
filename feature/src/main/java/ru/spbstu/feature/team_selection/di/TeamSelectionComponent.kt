package ru.spbstu.feature.team_selection.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.team_selection.presentation.TeamSelectionFragment

@Subcomponent(
    modules = [
        TeamSelectionModule::class
    ]
)
@ScreenScope
interface TeamSelectionComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): TeamSelectionComponent
    }

    fun inject(teamSelectionFragment: TeamSelectionFragment)
}