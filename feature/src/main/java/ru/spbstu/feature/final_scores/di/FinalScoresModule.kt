package ru.spbstu.feature.final_scores.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.final_scores.presentation.FinalScoresViewModel

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class FinalScoresModule {

    @Provides
    @IntoMap
    @ViewModelKey(FinalScoresViewModel::class)
    fun provideViewModel(router: FeatureRouter): ViewModel {
        return FinalScoresViewModel(router)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): FinalScoresViewModel {
        return ViewModelProvider(
            fragment,
            viewModelFactory
        ).get(FinalScoresViewModel::class.java)
    }
}