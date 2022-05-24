package ru.spbstu.feature.individual_score.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.individual_score.presentation.IndividualScoreViewModel

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class IndividualScoreModule {

    @Provides
    @IntoMap
    @ViewModelKey(IndividualScoreViewModel::class)
    fun provideViewModel(router: FeatureRouter): ViewModel {
        return IndividualScoreViewModel(router)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): IndividualScoreViewModel {
        return ViewModelProvider(
            fragment,
            viewModelFactory
        ).get(IndividualScoreViewModel::class.java)
    }
}