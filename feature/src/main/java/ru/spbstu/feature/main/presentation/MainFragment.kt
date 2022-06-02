package ru.spbstu.feature.main.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.subscribe
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.DatabaseReferences
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentMainBinding
import ru.spbstu.feature.databinding.FragmentTeamSelectionBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.domain.model.Game
import ru.spbstu.feature.room_connection.presentation.RoomConnectionFragment

class MainFragment : BaseFragment<MainViewModel>(
    R.layout.fragment_main,
) {
    private var _binding: FragmentMainBinding? = null
    override val binding get() = _binding!!

    private var listener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()

        binding.frgMainMbCreate.setDebounceClickListener {
            viewModel.openRoomConnectionFragment(RoomConnectionFragment.Companion.RoomMode.Create)
        }
        binding.frgMainMbJoin.setDebounceClickListener {
            viewModel.openRoomConnectionFragment(RoomConnectionFragment.Companion.RoomMode.Join)
        }
        binding.frgMainMbTutorial.setDebounceClickListener {}
        binding.frgMainMbTutorial.isEnabled = false
        binding.frgMainMbLogOut.setDebounceClickListener {
            viewModel.logout()
        }

        //todo change to subscription
        viewModel.getUserInfo {
            if (viewModel.lastGameName != null) {
                binding.frgMainMbReturnToGame.visibility = View.VISIBLE
            } else {
                binding.frgMainMbReturnToGame.visibility = View.GONE
            }
        }

        binding.frgMainMbReturnToGame.setDebounceClickListener {
            val readyCount = viewModel.game.value.players.count { it.value.readyFlag }
            Log.d("qwerty", "readyCount $readyCount players ${viewModel.game.value.numOfPlayers}")
            if (readyCount == viewModel.game.value.numOfPlayers) { //move to gameFragment
                viewModel.openGameFragment()
            } else {
                val player = viewModel.game.value.players[viewModel.currentUserId]
                when {
                    player?.teamStr?.isEmpty() == true -> { //return to teamSelection
                        viewModel.openTeamSelectionFragment()
                    }
                    player?.iconRes == 0 -> {
                        viewModel.openCharacterSelectionFragment()
                    }
                    else -> {
                        viewModel.openTeamsDisplayFragment()
                    }
                }
            }
        }

        binding.frgMainMbCredits.setDebounceClickListener {
            viewModel.openCreditsFragment()
        }
    }

    override fun subscribe() {
        super.subscribe()
        val ref = Firebase.database.getReference(DatabaseReferences.GAMES_REF)
        viewModel.getUserInfo {
            listener = ref.child(viewModel.lastGameName ?: "").subscribe(onSuccess = { snapshot ->
                handleGameSnapshotData(snapshot)
            }, onCancelled = {})
        }
    }

    private fun handleGameSnapshotData(snapshot: DataSnapshot) {
        val game = snapshot.getValue(Game::class.java)
        if (game != null) {
            viewModel.setGame(game)
        }
    }

    override fun onDestroyView() {
        val ref = Firebase.database.getReference(DatabaseReferences.GAMES_REF)
        listener?.let { ref.child(viewModel.gameJoiningDataWrapper.game.name).removeEventListener(it) }
        //_binding = null
        super.onDestroyView()
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .mainComponentFactory()
            .create(this)
            .inject(this)
    }
}