package ru.spbstu.feature.team_selection.presentation

import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.changeToActiveStyle
import ru.spbstu.common.extenstions.changeToInactiveStyle
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentTeamSelectionBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent

class TeamSelectionFragment : BaseFragment<TeamSelectionViewModel>(
    R.layout.fragment_team_selection,
) {
    override val binding by viewBinding(FragmentTeamSelectionBinding::bind)

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()
        binding.frgTeamSelectionMbFirstTeam.changeToInactiveStyle(R.color.button_red)
        binding.frgTeamSelectionMbSecondTeam.changeToInactiveStyle(R.color.button_orange)
        binding.frgTeamSelectionMbThirdTeam.changeToInactiveStyle(R.color.button_blue)
        binding.frgTeamSelectionMbFourthTeam.changeToInactiveStyle(R.color.button_green)
        binding.frgTeamSelectionMbNext.isEnabled = false

        binding.frgTeamSelectionMbFirstTeam.setDebounceClickListener {
            binding.frgTeamSelectionMbFirstTeam.changeToActiveStyle(R.color.button_red)
            binding.frgTeamSelectionMbSecondTeam.changeToInactiveStyle(R.color.button_orange)
            binding.frgTeamSelectionMbThirdTeam.changeToInactiveStyle(R.color.button_blue)
            binding.frgTeamSelectionMbFourthTeam.changeToInactiveStyle(R.color.button_green)
            binding.frgTeamSelectionMbNext.isEnabled = true
        }
        binding.frgTeamSelectionMbSecondTeam.setDebounceClickListener {
            binding.frgTeamSelectionMbFirstTeam.changeToInactiveStyle(R.color.button_red)
            binding.frgTeamSelectionMbSecondTeam.changeToActiveStyle(R.color.button_orange)
            binding.frgTeamSelectionMbThirdTeam.changeToInactiveStyle(R.color.button_blue)
            binding.frgTeamSelectionMbFourthTeam.changeToInactiveStyle(R.color.button_green)
            binding.frgTeamSelectionMbNext.isEnabled = true
        }
        binding.frgTeamSelectionMbThirdTeam.setDebounceClickListener {
            binding.frgTeamSelectionMbFirstTeam.changeToInactiveStyle(R.color.button_red)
            binding.frgTeamSelectionMbSecondTeam.changeToInactiveStyle(R.color.button_orange)
            binding.frgTeamSelectionMbThirdTeam.changeToActiveStyle(R.color.button_blue)
            binding.frgTeamSelectionMbFourthTeam.changeToInactiveStyle(R.color.button_green)
            binding.frgTeamSelectionMbNext.isEnabled = true
        }
        binding.frgTeamSelectionMbFourthTeam.setDebounceClickListener {
            binding.frgTeamSelectionMbFirstTeam.changeToInactiveStyle(R.color.button_red)
            binding.frgTeamSelectionMbSecondTeam.changeToInactiveStyle(R.color.button_orange)
            binding.frgTeamSelectionMbThirdTeam.changeToInactiveStyle(R.color.button_blue)
            binding.frgTeamSelectionMbFourthTeam.changeToActiveStyle(R.color.button_green)
            binding.frgTeamSelectionMbNext.isEnabled = true
        }
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .teamSelectionComponentFactory()
            .create(this)
            .inject(this)
    }
}