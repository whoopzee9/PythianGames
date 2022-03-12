package ru.spbstu.pythian_games.root.presentation.main

import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.events.SetBottomNavVisibility
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.pythian_games.R
import ru.spbstu.pythian_games.databinding.FragmentMainBinding
import ru.spbstu.pythian_games.navigation.Navigator
import ru.spbstu.pythian_games.root.di.RootApi
import ru.spbstu.pythian_games.root.di.RootComponent
import javax.inject.Inject

class MainFragment : BaseFragment<MainViewModel>(R.layout.fragment_main) {

    override val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)

    @Inject
    lateinit var navigator: Navigator

    override fun setupViews() {
        val navHost =
            childFragmentManager.findFragmentById(R.id.bottomNavHost) as NavHostFragment
        val navController = navHost.navController
        navigator.attachNavController(navController)
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            NavigationUI.onNavDestinationSelected(item, navController)
        }
    }

    override fun onStart() {
        EventBus.getDefault().register(this)
        navigator.checkBottomBar()
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        navigator.detachNavController()
    }

    override fun inject() {
        FeatureUtils.getFeature<RootComponent>(this, RootApi::class.java)
            .mainFragmentComponentFactory()
            .create(requireActivity())
            .inject(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: SetBottomNavVisibility) {
        if (!event.isAnimated) {
            binding.bottomNavigationView.isVisible = event.isVisible
        } else {
            if (event.isVisible) {
                binding.bottomNavigationView.isVisible = event.isVisible
                binding.bottomNavigationView.animate().translationY(0f)
                    .setDuration(BOTTOM_BAR_DURATION).start()
            } else {
                binding.bottomNavigationView.post {
                    binding.bottomNavigationView.animate()
                        .translationY(binding.bottomNavigationView.measuredHeight.toFloat())
                        .setDuration(BOTTOM_BAR_DURATION).start()
                }
            }
        }
    }

    private companion object {
        const val BOTTOM_BAR_DURATION = 300L
    }
}
