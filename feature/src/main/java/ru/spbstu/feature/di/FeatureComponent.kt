package ru.spbstu.feature.di

import dagger.BindsInstance
import dagger.Component
import ru.spbstu.common.di.CommonApi
import ru.spbstu.common.di.scope.FeatureScope
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.auth.di.AuthComponent
import ru.spbstu.feature.character_selection.di.CharacterSelectionComponent
import ru.spbstu.feature.login.di.LoginComponent
import ru.spbstu.feature.main.di.MainComponent
import ru.spbstu.feature.onboarding.di.OnboardingComponent
import ru.spbstu.feature.registration.di.RegistrationComponent
import ru.spbstu.feature.room_connection.di.RoomConnectionComponent
import ru.spbstu.feature.team_selection.di.TeamSelectionComponent
import ru.spbstu.feature.test.di.TestComponent

@Component(
    dependencies = [
        FeatureDependencies::class,
    ],
    modules = [
        FeatureModule::class,
        FeatureDataModule::class
    ]
)
@FeatureScope
interface FeatureComponent {

    fun testComponentFactory(): TestComponent.Factory
    fun onboardingComponentFactory(): OnboardingComponent.Factory
    fun authComponentFactory(): AuthComponent.Factory
    fun registrationComponentFactory(): RegistrationComponent.Factory
    fun loginComponentFactory(): LoginComponent.Factory
    fun mainComponentFactory(): MainComponent.Factory
    fun roomConnectionComponentFactory(): RoomConnectionComponent.Factory
    fun teamSelectionComponentFactory(): TeamSelectionComponent.Factory
    fun characterSelectionComponentFactory(): CharacterSelectionComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance featureRouter: FeatureRouter,
            deps: FeatureDependencies
        ): FeatureComponent
    }

    @Component(
        dependencies = [
            CommonApi::class,
        ]
    )
    interface FeatureDependenciesComponent : FeatureDependencies
}
