package ru.spbstu.feature.team_selection.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.handleBackPressed
import ru.spbstu.common.extenstions.setActiveStyle
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setDisabledStyle
import ru.spbstu.common.extenstions.setInactiveStyle
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.subscribe
import ru.spbstu.common.utils.DatabaseReferences
import ru.spbstu.common.utils.TeamsConstants
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentTeamSelectionBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.domain.model.Game

class TeamSelectionFragment : BaseFragment<TeamSelectionViewModel>(
    R.layout.fragment_team_selection,
) {

    private var _binding: FragmentTeamSelectionBinding? = null
    override val binding get() = _binding!!

    private var listener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()
        binding.frgTeamSelectionMbGreenTeam.setInactiveStyle(R.color.color_team_green)
        binding.frgTeamSelectionMbBlueTeam.setInactiveStyle(R.color.color_team_blue)
        binding.frgTeamSelectionMbRedTeam.setInactiveStyle(R.color.color_team_red)
        binding.frgTeamSelectionMbOrangeTeam.setInactiveStyle(R.color.color_team_orange)
        binding.frgTeamSelectionMbNext.isEnabled = false

        binding.frgTeamSelectionMbGreenTeam.setDebounceClickListener {
            binding.frgTeamSelectionMbGreenTeam.setActiveStyle(R.color.color_team_green)
            binding.frgTeamSelectionMbBlueTeam.setInactiveStyle(R.color.color_team_blue)
            binding.frgTeamSelectionMbRedTeam.setInactiveStyle(R.color.color_team_red)
            binding.frgTeamSelectionMbOrangeTeam.setInactiveStyle(R.color.color_team_orange)
            binding.frgTeamSelectionMbNext.isEnabled = true
            viewModel.setTeam(TeamsConstants.GREEN_TEAM)
        }
        binding.frgTeamSelectionMbBlueTeam.setDebounceClickListener {
            binding.frgTeamSelectionMbGreenTeam.setInactiveStyle(R.color.color_team_green)
            binding.frgTeamSelectionMbBlueTeam.setActiveStyle(R.color.color_team_blue)
            binding.frgTeamSelectionMbRedTeam.setInactiveStyle(R.color.color_team_red)
            binding.frgTeamSelectionMbOrangeTeam.setInactiveStyle(R.color.color_team_orange)
            binding.frgTeamSelectionMbNext.isEnabled = true
            viewModel.setTeam(TeamsConstants.BLUE_TEAM)
        }
        binding.frgTeamSelectionMbRedTeam.setDebounceClickListener {
            binding.frgTeamSelectionMbGreenTeam.setInactiveStyle(R.color.color_team_green)
            binding.frgTeamSelectionMbBlueTeam.setInactiveStyle(R.color.color_team_blue)
            binding.frgTeamSelectionMbRedTeam.setActiveStyle(R.color.color_team_red)
            binding.frgTeamSelectionMbOrangeTeam.setInactiveStyle(R.color.color_team_orange)
            binding.frgTeamSelectionMbNext.isEnabled = true
            viewModel.setTeam(TeamsConstants.RED_TEAM)
        }
        binding.frgTeamSelectionMbOrangeTeam.setDebounceClickListener {
            binding.frgTeamSelectionMbGreenTeam.setInactiveStyle(R.color.color_team_green)
            binding.frgTeamSelectionMbBlueTeam.setInactiveStyle(R.color.color_team_blue)
            binding.frgTeamSelectionMbRedTeam.setInactiveStyle(R.color.color_team_red)
            binding.frgTeamSelectionMbOrangeTeam.setActiveStyle(R.color.color_team_orange)
            binding.frgTeamSelectionMbNext.isEnabled = true
            viewModel.setTeam(TeamsConstants.ORANGE_TEAM)
        }
        updateTeamsButtons()
        binding.frgTeamSelectionMbNext.setDebounceClickListener {
            viewModel.saveTeam()
        }

        handleBackPressed {
            viewModel.onBack()
        }
    }

    override fun subscribe() {
        super.subscribe()
        val ref = Firebase.database.getReference(DatabaseReferences.GAMES_REF)
        listener = ref.child(viewModel.gameJoiningDataWrapper.game.name).subscribe(onSuccess = { snapshot ->
            handleGameSnapshotData(snapshot)
        }, onCancelled = {})
    }

    override fun onDestroyView() {
        val ref = Firebase.database.getReference(DatabaseReferences.GAMES_REF)
        listener?.let { ref.removeEventListener(it) }
        //_binding = null
        super.onDestroyView()
    }

    private fun handleGameSnapshotData(snapshot: DataSnapshot) {
        val game = snapshot.getValue(Game::class.java)
        if (game != null) {
            val playersPerTeam = game.numOfPlayers / game.numOfTeams
            if (game.players.filter { it.value.teamStr == TeamsConstants.GREEN_TEAM }.size >= playersPerTeam) {
                binding.frgTeamSelectionMbGreenTeam.setDisabledStyle()
            }
            if (game.players.filter { it.value.teamStr == TeamsConstants.BLUE_TEAM }.size >= playersPerTeam) {
                binding.frgTeamSelectionMbBlueTeam.setDisabledStyle()
            }
            if (game.numOfTeams > 2 && game.players.filter { it.value.teamStr == TeamsConstants.RED_TEAM }.size >= playersPerTeam) {
                binding.frgTeamSelectionMbRedTeam.setDisabledStyle()
            }
            if (game.numOfTeams > 3 && game.players.filter { it.value.teamStr == TeamsConstants.ORANGE_TEAM }.size >= playersPerTeam) {
                binding.frgTeamSelectionMbOrangeTeam.setDisabledStyle()
            }
        }
    }

    private fun updateTeamsButtons() {
        val game = viewModel.gameJoiningDataWrapper.game
        when (game.numOfTeams) {
            2 -> {
                binding.frgTeamSelectionMbGreenTeam.visibility = View.VISIBLE
                binding.frgTeamSelectionMbBlueTeam.visibility = View.VISIBLE
                binding.frgTeamSelectionMbRedTeam.visibility = View.GONE
                binding.frgTeamSelectionMbOrangeTeam.visibility = View.GONE
            }
            3 -> {
                binding.frgTeamSelectionMbGreenTeam.visibility = View.VISIBLE
                binding.frgTeamSelectionMbBlueTeam.visibility = View.VISIBLE
                binding.frgTeamSelectionMbRedTeam.visibility = View.VISIBLE
                binding.frgTeamSelectionMbOrangeTeam.visibility = View.GONE
            }
            4 -> {
                binding.frgTeamSelectionMbGreenTeam.visibility = View.VISIBLE
                binding.frgTeamSelectionMbBlueTeam.visibility = View.VISIBLE
                binding.frgTeamSelectionMbRedTeam.visibility = View.VISIBLE
                binding.frgTeamSelectionMbOrangeTeam.visibility = View.VISIBLE
            }
        }
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .teamSelectionComponentFactory()
            .create(this)
            .inject(this)
    }
}