package ru.spbstu.pythian_games.root.presentation

import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ru.spbstu.common.base.BaseActivity
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.pythian_games.R
import ru.spbstu.pythian_games.databinding.ActivityRootBinding
import ru.spbstu.pythian_games.navigation.Navigator
import ru.spbstu.pythian_games.root.di.RootApi
import ru.spbstu.pythian_games.root.di.RootComponent
import javax.inject.Inject


class RootActivity : BaseActivity<RootViewModel>() {

    @Inject
    lateinit var navigator: Navigator

    override val binding: ActivityRootBinding by viewBinding(ActivityRootBinding::inflate)

    private lateinit var auth: FirebaseAuth

    override fun setupViews() {
        super.setupViews()
        navigator.attachActivity(this)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        val navController = navHostFragment.navController
        navigator.attachNavController(navController)
        auth = Firebase.auth
        auth.useAppLanguage()
        if (auth.currentUser == null) {
            navigator.clearBackStackAndOpenAuthFragment()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        navigator.detachActivity()
        navigator.detachNavController()
    }

    override fun inject() {
        FeatureUtils.getFeature<RootComponent>(this, RootApi::class.java)
            .mainActivityComponentFactory()
            .create(this)
            .inject(this)
    }
}
