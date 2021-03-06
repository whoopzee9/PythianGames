package ru.spbstu.feature.onboarding.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.onboarding.presentation.OnboardingViewModel

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class OnboardingModule {

    @Provides
    @IntoMap
    @ViewModelKey(OnboardingViewModel::class)
    fun provideViewModel(router: FeatureRouter): ViewModel {
        return OnboardingViewModel(router)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): OnboardingViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(OnboardingViewModel::class.java)
    }
}