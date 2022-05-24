package ru.spbstu.feature.team_score.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.team_score.presentation.TeamScoreFragment

@Subcomponent(
    modules = [
        TeamScoreModule::class
    ]
)
@ScreenScope
interface TeamScoreComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): TeamScoreComponent
    }

    fun inject(teamScoreFragment: TeamScoreFragment)
}