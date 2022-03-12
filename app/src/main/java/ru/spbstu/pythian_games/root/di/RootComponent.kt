package ru.spbstu.pythian_games.root.di

import dagger.BindsInstance
import dagger.Component
import ru.spbstu.common.di.CommonApi
import ru.spbstu.common.di.scope.FeatureScope
import ru.spbstu.pythian_games.navigation.Navigator
import ru.spbstu.pythian_games.root.presentation.di.RootActivityComponent
import ru.spbstu.pythian_games.root.presentation.main.di.MainFragmentComponent

@Component(
    dependencies = [
        RootDependencies::class
    ],
    modules = [
        RootFeatureModule::class
    ]
)
@FeatureScope
interface RootComponent {

    fun mainActivityComponentFactory(): RootActivityComponent.Factory

    fun mainFragmentComponentFactory(): MainFragmentComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance navigator: Navigator,
            deps: RootDependencies
        ): RootComponent
    }

    @Component(
        dependencies = [
            CommonApi::class,
        ]
    )
    interface RootFeatureDependenciesComponent : RootDependencies
}
