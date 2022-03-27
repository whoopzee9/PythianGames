package ru.spbstu.feature.character_selection.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.character_selection.presentation.CharacterSelectionViewModel

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class CharacterSelectionModule {

    @Provides
    @IntoMap
    @ViewModelKey(CharacterSelectionViewModel::class)
    fun provideViewModel(router: FeatureRouter): ViewModel {
        return CharacterSelectionViewModel(router)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): CharacterSelectionViewModel {
        return ViewModelProvider(
            fragment,
            viewModelFactory
        ).get(CharacterSelectionViewModel::class.java)
    }
}