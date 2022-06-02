package ru.spbstu.pythian_games.navigation

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import ru.spbstu.feature.FeatureRouter
import ru.spbstu.feature.room_connection.presentation.RoomConnectionFragment
import ru.spbstu.pythian_games.R
import timber.log.Timber

class Navigator : FeatureRouter {

    private var navController: NavController? = null
    private var activity: AppCompatActivity? = null

    override fun back() {
        val popped = navController!!.popBackStack()

        if (!popped) {
            activity!!.finish()
        }
    }

    fun clearBackStackAndOpenAuthFragment() {
        while (navController?.popBackStack() == true) {
            Timber.tag(TAG).d("Skipped backstack entry")
        }
        navController?.navigate(R.id.open_auth_fragment)
    }

    override fun openRegistrationFragment() {
        when (navController?.currentDestination?.id) {
            R.id.authFragment -> navController?.navigate(R.id.action_authFragment_to_registrationFragment)
        }
    }

    override fun openLoginFragment() {
        when (navController?.currentDestination?.id) {
            R.id.authFragment -> navController?.navigate(R.id.action_authFragment_to_loginFragment)
        }
    }

    override fun openAuthFragment() {
        clearBackStackAndOpenAuthFragment()
    }

    override fun openOnboarding() {
        when (navController?.currentDestination?.id) {
            R.id.authFragment -> navController?.navigate(R.id.action_authFragment_to_onboardingFragment)
        }
    }

    override fun openMainFragment() {
        when (navController?.currentDestination?.id) {
            R.id.loginFragment -> navController?.navigate(R.id.action_loginFragment_to_mainFragment)
            R.id.registrationFragment -> navController?.navigate(R.id.action_registrationFragment_to_mainFragment)
            R.id.finalScoresFragment -> navController?.navigate(R.id.action_finalScoresFragment_to_mainFragment)
            R.id.teamSelectionFragment -> navController?.navigate(R.id.action_teamSelectionFragment_to_mainFragment)
            R.id.characterSelectionFragment -> navController?.navigate(R.id.action_characterSelectionFragment_to_mainFragment)
            R.id.teamsDisplayFragment -> navController?.navigate(R.id.action_teamsDisplayFragment_to_mainFragment)
        }
    }

    override fun openRoomConnectionFragment(mode: RoomConnectionFragment.Companion.RoomMode) {
        when (navController?.currentDestination?.id) {
            R.id.mainFragment -> navController?.navigate(
                R.id.action_mainFragment_to_roomConnectionFragment,
                RoomConnectionFragment.makeBundle(mode)
            )
        }
    }

    override fun openTeamSelectionFragment() {
        when (navController?.currentDestination?.id) {
            R.id.roomConnectionFragment -> navController?.navigate(R.id.action_roomConnectionFragment_to_teamSelectionFragment)
            R.id.mainFragment -> navController?.navigate(R.id.action_mainFragment_to_teamSelectionFragment)
        }
    }

    override fun openCharacterSelectionFragment() {
        when (navController?.currentDestination?.id) {
            R.id.teamSelectionFragment -> navController?.navigate(R.id.action_teamSelectionFragment_to_characterSelectionFragment)
            R.id.mainFragment -> {
                navController?.navigate(R.id.action_mainFragment_to_teamSelectionFragment)
                navController?.navigate(R.id.action_teamSelectionFragment_to_characterSelectionFragment)
            }
        }
    }

    override fun openTeamDisplayFragment() {
        when (navController?.currentDestination?.id) {
            R.id.characterSelectionFragment -> navController?.navigate(R.id.action_characterSelectionFragment_to_teamsDisplayFragment)
            R.id.mainFragment -> {
                navController?.navigate(R.id.action_mainFragment_to_teamSelectionFragment)
                navController?.navigate(R.id.action_teamSelectionFragment_to_characterSelectionFragment)
                navController?.navigate(R.id.action_characterSelectionFragment_to_teamsDisplayFragment)
            }
        }
    }

    override fun openGameFragment() {
        when (navController?.currentDestination?.id) {
            R.id.teamsDisplayFragment -> navController?.navigate(R.id.action_teamsDisplayFragment_to_gameFragment)
            R.id.mainFragment -> navController?.navigate(R.id.action_mainFragment_to_gameFragment)
        }
    }

    override fun openFinalScoresFragment() {
        when (navController?.currentDestination?.id) {
            R.id.gameFragment -> navController?.navigate(R.id.action_gameFragment_to_finalScoresFragment)
        }
    }

    override fun openCreditsFragment() {
        when (navController?.currentDestination?.id) {
            R.id.mainFragment -> navController?.navigate(R.id.action_mainFragment_to_creditsFragment)
        }
    }

    fun attachActivity(activity: AppCompatActivity) {
        this.activity = activity
    }

    fun attachNavController(navController: NavController) {
        this.navController = navController
    }

    fun detachActivity() {
        activity = null
    }

    fun detachNavController() {
        navController = null
    }

    private companion object {
        val TAG = Navigator::class.simpleName
    }
}
