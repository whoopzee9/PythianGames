package ru.spbstu.feature.registration.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.registration.presentation.RegistrationViewModel

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class RegistrationModule {

    @Provides
    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    fun provideViewModel(router: FeatureRouter): ViewModel {
        return RegistrationViewModel(router)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): RegistrationViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(RegistrationViewModel::class.java)
    }
}