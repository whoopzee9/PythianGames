package ru.spbstu.feature.character_selection.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.character_selection.presentation.CharacterSelectionFragment

@Subcomponent(
    modules = [
        CharacterSelectionModule::class
    ]
)
@ScreenScope
interface CharacterSelectionComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): CharacterSelectionComponent
    }

    fun inject(characterSelectionFragment: CharacterSelectionFragment)
}