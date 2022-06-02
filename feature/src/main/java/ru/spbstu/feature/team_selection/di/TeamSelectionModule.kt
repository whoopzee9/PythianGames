package ru.spbstu.feature.team_selection.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.team_selection.presentation.TeamSelectionViewModel
import ru.spbstu.feature.utils.GameJoiningDataWrapper

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class TeamSelectionModule {

    @Provides
    @IntoMap
    @ViewModelKey(TeamSelectionViewModel::class)
    fun provideViewModel(
        router: FeatureRouter,
        gameJoiningDataWrapper: GameJoiningDataWrapper
    ): ViewModel {
        return TeamSelectionViewModel(router, gameJoiningDataWrapper)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): TeamSelectionViewModel {
        return ViewModelProvider(
            fragment,
            viewModelFactory
        ).get(TeamSelectionViewModel::class.java)
    }
}