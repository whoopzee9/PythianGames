package ru.spbstu.feature.team_score.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.handleBackPressed
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentTeamScoreBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.team_score.presentation.adapter.TeamScoreAdapter
import ru.spbstu.feature.team_score.presentation.adapter.TeamScoreItemDecoration

class TeamScoreFragment : BaseFragment<TeamScoreViewModel>(
    R.layout.fragment_team_score,
) {
    private var _binding: FragmentTeamScoreBinding? = null
    override val binding get() = _binding!!

    private lateinit var adapter: TeamScoreAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamScoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()

        binding.frgTeamScoreRvTeams.layoutManager = GridLayoutManager(requireContext(), 2)
        setupAdapter()

        handleBackPressed { }
    }

    private fun setupAdapter() {
        adapter = TeamScoreAdapter()
        binding.frgTeamScoreRvTeams.adapter = adapter
        binding.frgTeamScoreRvTeams.addItemDecoration(TeamScoreItemDecoration())
        adapter.bindData(viewModel.getTeamsStats())
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .teamScoreComponentFactory()
            .create(this)
            .inject(this)
    }

}