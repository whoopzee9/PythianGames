package ru.spbstu.feature.teams_display.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.handleBackPressed
import ru.spbstu.common.extenstions.setDebounceClickListener
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
import ru.spbstu.feature.game.presentation.GameFragment
import ru.spbstu.feature.game.presentation.dialog.ConfirmationDialogFragment

class TeamsDisplayFragment : BaseFragment<TeamsDisplayViewModel>(
    R.layout.fragment_teams_display,
) {
    private var _binding: FragmentTeamsDisplayBinding? = null
    override val binding get() = _binding!!

    private var listener: ValueEventListener? = null

    private var confirmationDialogFragment: ConfirmationDialogFragment? = null

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

        binding.frgTeamsDisplayIbExit.setDebounceClickListener {
            viewModel.exit()
        }

        binding.frgTeamsDisplayIbShare.setDebounceClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            val shareBody = getString(R.string.share_body, viewModel.game.value.name)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(intent, getString(R.string.share_using)))
        }

        binding.frgTeamsDisplayMbDelete.setDebounceClickListener {
            showConfirmationDialog(actionOk = {
                viewModel.deleteRoom()
                confirmationDialogFragment?.dismiss()
            }, actionCancel = {
                confirmationDialogFragment?.dismiss()
            })
        }

        handleBackPressed {  }
    }

    override fun subscribe() {
        super.subscribe()
        val ref = Firebase.database.getReference(DatabaseReferences.GAMES_REF)
        listener = ref.child(viewModel.gameJoiningDataWrapper.game.name).subscribe(onSuccess = { snapshot ->
            handleGameSnapshotData(snapshot)
        }, onCancelled = {})

        viewModel.timerData.observe {
            binding.frgTeamsDisplayTvWaitingForPlayers.text =
                getString(R.string.game_starts_in, it / 1000)
        }
    }

    override fun onDestroyView() {
        val ref = Firebase.database.getReference(DatabaseReferences.GAMES_REF)
        listener?.let { ref.child(viewModel.gameJoiningDataWrapper.game.name).removeEventListener(it) }
        //_binding = null
        super.onDestroyView()
    }

    private fun handleGameSnapshotData(snapshot: DataSnapshot) {
        val game = snapshot.getValue(Game::class.java)
        if (game != null) {
            viewModel.setGame(game)
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

            val creator = players.firstOrNull { it.id == game.creatorId }

            val currentUserId = Firebase.auth.currentUser?.uid
            if (creator != null) {
                if (currentUserId == creator.id) {
                    binding.frgTeamsDisplayMbDelete.visibility = View.VISIBLE
                } else {
                    binding.frgTeamsDisplayMbDelete.visibility = View.GONE
                }
            } else {
                if (players.first().id == currentUserId) {
                    binding.frgTeamsDisplayMbDelete.visibility = View.VISIBLE
                } else {
                    binding.frgTeamsDisplayMbDelete.visibility = View.GONE
                }
            }

            if (readyCount == game.numOfPlayers) {
                viewModel.startGameTimer.start()
                viewModel.setUserLastGame()
            }
        } else {
            Toast.makeText(requireContext(), R.string.game_deleted, Toast.LENGTH_SHORT).show()
            viewModel.openMainFragment()
        }
    }

    private fun setupBoard() {
        with(binding.frgTeamsDisplayBoard) {
            numOfTeams = viewModel.gameJoiningDataWrapper.game.numOfTeams
            numOfPlayers = viewModel.gameJoiningDataWrapper.game.numOfPlayers
            size = if (viewModel.gameJoiningDataWrapper.game.numOfPlayers > 4) 5 else 3
        }
    }

    private fun showConfirmationDialog(
        actionOk: () -> Unit,
        actionCancel: () -> Unit
    ) {
        if (confirmationDialogFragment == null) {
            confirmationDialogFragment =
                ConfirmationDialogFragment.newInstance(getString(R.string.delete_confirmation))
        }
        val dialog = confirmationDialogFragment
        if (dialog != null) {
            dialog.setDialogWarningText(getString(R.string.delete_confirmation))
            dialog.setOnOkClickListener {
                actionOk.invoke()
                dialog.dismiss()
            }
            dialog.setOnCancelClickListener {
                actionCancel.invoke()
                dialog.dismiss()
            }
            dialog.show(parentFragmentManager, CONFIRMATION_DIALOG_TAG)
        }
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .teamsDisplayComponentFactory()
            .create(this)
            .inject(this)
    }

    companion object {
        private val TAG = GameFragment::class.java.simpleName
        private val CONFIRMATION_DIALOG_TAG = "${TAG}CONFIRMATION_DIALOG_TAG"
    }
}