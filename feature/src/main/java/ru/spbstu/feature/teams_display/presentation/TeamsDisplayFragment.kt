package ru.spbstu.feature.teams_display.presentation

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.model.Player
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentTeamsDisplayBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent

class TeamsDisplayFragment : BaseFragment<TeamsDisplayViewModel>(
    R.layout.fragment_teams_display,
) {
    override val binding by viewBinding(FragmentTeamsDisplayBinding::bind)

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()

        lifecycleScope.launch {
            binding.flat.addPlayer(Player(R.drawable.character_2, 1, 1, "sdfsdf"))
            //delay(3000)
            binding.flat.addPlayer(Player(R.drawable.character_1, 1, 2, "qweqwe"))
            //delay(3000)
            binding.flat.addPlayer(Player(R.drawable.character_3, 2, 1, "123123"))
            //delay(3000)
            binding.flat.addPlayer(Player(R.drawable.character_4, 3, 1, "fgdаывываfg"))
            //delay(3000)
            binding.flat.addPlayer(Player(R.drawable.character_5, 2, 2, "gdf"))
            //delay(3000)
            binding.flat.addPlayer(Player(R.drawable.character_6, 3, 2, "12"))
            //delay(3000)
            binding.flat.addPlayer(Player(R.drawable.character_7, 4, 1, "12dfgd3123"))
            binding.flat.addPlayer(Player(R.drawable.character_8, 4, 2, "12wew3123"))
            //delay(3000)

        }
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .teamsDisplayComponentFactory()
            .create(this)
            .inject(this)
    }
}