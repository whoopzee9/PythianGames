package ru.spbstu.pythian_games.di.app

import dagger.Module
import dagger.Provides
import ru.spbstu.common.di.scope.ApplicationScope
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.pythian_games.navigation.Navigator

@Module
class NavigationModule {

    @ApplicationScope
    @Provides
    fun provideNavigator(): Navigator = Navigator()

    @ApplicationScope
    @Provides
    fun provideSplashRouter(navigator: Navigator): FeatureRouter = navigator
}
