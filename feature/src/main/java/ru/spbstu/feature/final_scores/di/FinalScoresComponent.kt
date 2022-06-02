package ru.spbstu.feature.final_scores.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.feature.final_scores.presentation.FinalScoresFragment

@Subcomponent(
    modules = [
        FinalScoresModule::class
    ]
)
@ScreenScope
interface FinalScoresComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): FinalScoresComponent
    }

    fun inject(finalScoresFragment: FinalScoresFragment)
}