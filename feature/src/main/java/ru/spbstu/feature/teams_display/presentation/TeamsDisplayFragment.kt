package ru.spbstu.feature.teams_display.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.subscribe
import ru.spbstu.common.utils.DatabaseReferences
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentTeamsDisplayBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.domain.model.Game
import ru.spbstu.feature.domain.model.toPlayer

class TeamsDisplayFragment : BaseFragment<TeamsDisplayViewModel>(
    R.layout.fragment_teams_display,
) {
    private var _binding: FragmentTeamsDisplayBinding? = null
    override val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamsDisplayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()

        setupBoard()
    }

    override fun subscribe() {
        super.subscribe()
        val ref = Firebase.database.getReference(DatabaseReferences.GAMES_REF)
        ref.child(viewModel.gameJoiningDataWrapper.game.name).subscribe(onSuccess = { snapshot ->
            handleGameSnapshotData(snapshot)
        }, onCancelled = {})

        viewModel.timerData.observe {
            binding.frgTeamsDisplayTvWaitingForPlayers.text =
                getString(R.string.game_starts_in, it / 1000)
        }
    }

    private fun handleGameSnapshotData(snapshot: DataSnapshot) {
        val game = snapshot.getValue(Game::class.java)
        if (game != null) {
            val players = game.players.values
            val displayedPlayersIds = binding.frgTeamsDisplayBoard.getDisplayedPlayersIds()
            players.forEach {
                if (!displayedPlayersIds.contains(it.id) && it.readyFlag) {
                    binding.frgTeamsDisplayBoard.addPlayer(it.toPlayer())
                }
            }
            val readyCount = players.count { it.readyFlag }
            binding.frgTeamsDisplayTvWaitingForPlayers.text =
                getString(R.string.waiting_for_players, readyCount, game.numOfPlayers)

            if (readyCount == game.numOfPlayers) {
                viewModel.startGameTimer.start()
            }
        }
    }

    private fun setupBoard() {
        with(binding.frgTeamsDisplayBoard) {
            numOfTeams = viewModel.gameJoiningDataWrapper.game.numOfTeams
            numOfPlayers = viewModel.gameJoiningDataWrapper.game.numOfPlayers
            size = if (viewModel.gameJoiningDataWrapper.game.numOfPlayers > 4) 5 else 3
        }
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .teamsDisplayComponentFactory()
            .create(this)
            .inject(this)
    }
}