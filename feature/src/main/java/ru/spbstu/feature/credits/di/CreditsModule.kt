package ru.spbstu.feature.credits.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.credits.presentation.CreditsViewModel

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class CreditsModule {

    @Provides
    @IntoMap
    @ViewModelKey(CreditsViewModel::class)
    fun provideViewModel(router: FeatureRouter): ViewModel {
        return CreditsViewModel(router)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): CreditsViewModel {
        return ViewModelProvider(
            fragment,
            viewModelFactory
        ).get(CreditsViewModel::class.java)
    }
}