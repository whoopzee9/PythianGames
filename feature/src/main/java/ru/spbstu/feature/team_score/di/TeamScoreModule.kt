package ru.spbstu.feature.team_score.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.team_score.presentation.TeamScoreViewModel
import ru.spbstu.feature.utils.GameJoiningDataWrapper

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class TeamScoreModule {

    @Provides
    @IntoMap
    @ViewModelKey(TeamScoreViewModel::class)
    fun provideViewModel(
        router: FeatureRouter,
        gameJoiningDataWrapper: GameJoiningDataWrapper
    ): ViewModel {
        return TeamScoreViewModel(router, gameJoiningDataWrapper)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): TeamScoreViewModel {
        return ViewModelProvider(
            fragment,
            viewModelFactory
        ).get(TeamScoreViewModel::class.java)
    }
}