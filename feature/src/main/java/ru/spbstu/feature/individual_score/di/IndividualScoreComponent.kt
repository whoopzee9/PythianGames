package ru.spbstu.feature.individual_score.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.individual_score.presentation.IndividualScoreFragment

@Subcomponent(
    modules = [
        IndividualScoreModule::class
    ]
)
@ScreenScope
interface IndividualScoreComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): IndividualScoreComponent
    }

    fun inject(individualScoreFragment: IndividualScoreFragment)
}