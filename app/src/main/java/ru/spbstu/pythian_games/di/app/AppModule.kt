package ru.spbstu.pythian_games.di.app

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.spbstu.common.di.scope.ApplicationScope
import ru.spbstu.pythian_games.App

@Module
class AppModule {

    @Provides
    @ApplicationScope
    fun provideContext(application: App): Context {
        return application
    }
}
