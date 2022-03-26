package ru.spbstu.feature.login.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.login.presentation.LoginViewModel

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class LoginModule {

    @Provides
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    fun provideViewModel(router: FeatureRouter): ViewModel {
        return LoginViewModel(router)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): LoginViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(LoginViewModel::class.java)
    }
}