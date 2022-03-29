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

        binding.frgTeamsDisplayBoard.numOfTeams = 4
        binding.frgTeamsDisplayBoard.numOfPlayers = 8
        binding.frgTeamsDisplayBoard.size = 5
        binding.frgTeamsDisplayBoard.addPlayer(Player(R.drawable.character_2, 1, 1, "sdfsdf"))
        binding.frgTeamsDisplayBoard.addPlayer(Player(R.drawable.character_1, 1, 2, "qweqwe"))
        binding.frgTeamsDisplayBoard.addPlayer(Player(R.drawable.character_3, 2, 1, "123123"))
        binding.frgTeamsDisplayBoard.addPlayer(Player(R.drawable.character_5, 2, 2, "gdf"))
        binding.frgTeamsDisplayBoard.addPlayer(Player(R.drawable.character_4, 3, 1, "fgdаывываfg"))
        binding.frgTeamsDisplayBoard.addPlayer(Player(R.drawable.character_6, 3, 2, "12"))
        binding.frgTeamsDisplayBoard.addPlayer(Player(R.drawable.character_7, 4, 1, "12dfgddfgdf3123"))
        binding.frgTeamsDisplayBoard.addPlayer(Player(R.drawable.character_8, 4, 2, "12wewfsddfs3123"))


    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .teamsDisplayComponentFactory()
            .create(this)
            .inject(this)
    }
}