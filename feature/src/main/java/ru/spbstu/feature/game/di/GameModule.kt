package ru.spbstu.feature.game.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.game.presentation.GameViewModel

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class GameModule {

    @Provides
    @IntoMap
    @ViewModelKey(GameViewModel::class)
    fun provideViewModel(router: FeatureRouter): ViewModel {
        return GameViewModel(router)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): GameViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(GameViewModel::class.java)
    }
}