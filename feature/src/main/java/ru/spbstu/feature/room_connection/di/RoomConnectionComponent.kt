package ru.spbstu.feature.room_connection.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.room_connection.presentation.RoomConnectionFragment

@Subcomponent(
    modules = [
        RoomConnectionModule::class
    ]
)
@ScreenScope
interface RoomConnectionComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): RoomConnectionComponent
    }

    fun inject(roomConnectionFragment: RoomConnectionFragment)
}