package ru.spbstu.feature.main.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.main.presentation.MainViewModel
import ru.spbstu.feature.utils.GameJoiningDataWrapper

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class MainModule {

    @Provides
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun provideViewModel(
        router: FeatureRouter,
        gameJoiningDataWrapper: GameJoiningDataWrapper
    ): ViewModel {
        return MainViewModel(router, gameJoiningDataWrapper)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): MainViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(MainViewModel::class.java)
    }
}