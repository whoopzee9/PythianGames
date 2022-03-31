package ru.spbstu.feature.game.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.game.presentation.GameFragment

@Subcomponent(
    modules = [
        GameModule::class
    ]
)
@ScreenScope
interface GameComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): GameComponent
    }

    fun inject(gameFragment: GameFragment)
}