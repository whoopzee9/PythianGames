package ru.spbstu.feature.room_connection.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.room_connection.presentation.RoomConnectionViewModel

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class RoomConnectionModule {

    @Provides
    @IntoMap
    @ViewModelKey(RoomConnectionViewModel::class)
    fun provideViewModel(router: FeatureRouter): ViewModel {
        return RoomConnectionViewModel(router)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): RoomConnectionViewModel {
        return ViewModelProvider(
            fragment,
            viewModelFactory
        ).get(RoomConnectionViewModel::class.java)
    }
}