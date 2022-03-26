package ru.spbstu.pythian_games.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import org.greenrobot.eventbus.EventBus
import ru.spbstu.common.events.SetBottomNavVisibility
import ru.spbstu.feature.FeatureRouter
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
        if (navController?.currentDestination?.id == R.id.onboardingFragment) {
            return
        }
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
