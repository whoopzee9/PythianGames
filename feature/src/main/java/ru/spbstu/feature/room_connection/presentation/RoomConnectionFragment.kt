package ru.spbstu.feature.room_connection.presentation

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentRoomConnectionBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import java.lang.IllegalStateException

class RoomConnectionFragment : BaseFragment<RoomConnectionViewModel>(
    R.layout.fragment_room_connection,
) {
    override val binding by viewBinding(FragmentRoomConnectionBinding::bind)

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()
        binding.frgRoomConnectionEtName.addTextChangedListener {
            binding.frgRoomConnectionEtNameLayout.error = null
        }
        binding.frgRoomConnectionEtAccessCode.addTextChangedListener {
            binding.frgRoomConnectionEtAccessCodeLayout.error = null
        }
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner_layout,
            resources.getStringArray(R.array.number_of_players)
        )
        adapter.setDropDownViewResource(R.layout.item_spinner_row_item)
        binding.frgRoomConnectionSpPlayers.adapter = adapter
    }

    override fun setupFromArguments(args: Bundle) {
        super.setupFromArguments(args)
        args.getSerializable(MODE_KEY).let {
            when (it as RoomMode) {
                RoomMode.Create -> {
                    binding.frgRoomConnectionMbNext.setText(R.string.create)
                    binding.frgRoomConnectionSpPlayers.visibility = View.VISIBLE
                    binding.frgRoomConnectionMbNext.setDebounceClickListener {
                        checkFields { name, code ->
                            val selectedPosition = binding.frgRoomConnectionSpPlayers.selectedItemPosition
                            var numOfTeams = 0
                            var numOfPlayers = 0
                            when (selectedPosition) {
                                0 -> { // 1 x 1
                                    numOfTeams = 2
                                    numOfPlayers = 2
                                }
                                1 -> { // 2 x 2
                                    numOfTeams = 2
                                    numOfPlayers = 4
                                }
                                2 -> { // 1 x 1 x 1 x 1
                                    numOfTeams = 4
                                    numOfPlayers = 4
                                }
                                3 -> { // 2 x 2 x 2
                                    numOfTeams = 3
                                    numOfPlayers = 6
                                }
                                4 -> { // 4 x 4
                                    numOfTeams = 2
                                    numOfPlayers = 8
                                }
                                5 -> { // 2 x 2 x 2 x 2
                                    numOfTeams = 4
                                    numOfPlayers = 8
                                }
                                else -> throw IllegalStateException("Wrong selected position")
                            }
                            viewModel.createRoom(name, code, numOfTeams, numOfPlayers)
                        }
                    }
                }
                RoomMode.Join -> {
                    binding.frgRoomConnectionMbNext.setText(R.string.join)
                    binding.frgRoomConnectionSpPlayers.visibility = View.GONE
                    binding.frgRoomConnectionMbNext.setDebounceClickListener {
                        checkFields { name, code ->
                            viewModel.joinRoom(name, code)
                        }
                    }
                }
            }
        }
    }

    override fun subscribe() {
        super.subscribe()
        viewModel.state.observe {
            handleUIState(it)
        }
    }

    private fun handleUIState(state: RoomConnectionViewModel.UIState) {
        when (state) {
            RoomConnectionViewModel.UIState.Failure -> {
                Toast.makeText(requireContext(), R.string.try_again_later, Toast.LENGTH_SHORT).show()
                binding.frgRoomConnectionPbProgress.visibility = View.GONE
                setActionsClickability(true)
            }
            RoomConnectionViewModel.UIState.GameAlreadyExists -> {
                Toast.makeText(requireContext(), R.string.game_already_exists, Toast.LENGTH_SHORT).show()
                binding.frgRoomConnectionPbProgress.visibility = View.GONE
                setActionsClickability(true)
            }
            RoomConnectionViewModel.UIState.Initial -> {
                binding.frgRoomConnectionPbProgress.visibility = View.GONE
                setActionsClickability(true)
            }
            RoomConnectionViewModel.UIState.Progress -> {
                binding.frgRoomConnectionPbProgress.visibility = View.VISIBLE
                setActionsClickability(false)
            }
            RoomConnectionViewModel.UIState.Success -> {
                binding.frgRoomConnectionPbProgress.visibility = View.GONE
                setActionsClickability(true)
            }
            RoomConnectionViewModel.UIState.WrongCode -> {
                Toast.makeText(requireContext(), R.string.wrong_code, Toast.LENGTH_SHORT).show()
                binding.frgRoomConnectionPbProgress.visibility = View.GONE
                setActionsClickability(true)
            }
            RoomConnectionViewModel.UIState.GameDoesntExist -> {
                Toast.makeText(requireContext(), R.string.game_doesnt_exist, Toast.LENGTH_SHORT).show()
                binding.frgRoomConnectionPbProgress.visibility = View.GONE
                setActionsClickability(true)
            }
            RoomConnectionViewModel.UIState.GameIsFull -> {
                Toast.makeText(requireContext(), R.string.game_is_full, Toast.LENGTH_SHORT).show()
                binding.frgRoomConnectionPbProgress.visibility = View.GONE
                setActionsClickability(true)
            }
        }
    }

    private fun setActionsClickability(isClickable: Boolean) {
        binding.frgRoomConnectionMbNext.isClickable = isClickable
    }

    private fun checkFields(onCheck: (name: String, code: String) -> Unit) {
        val name = binding.frgRoomConnectionEtName.text.toString()
        if (name.isEmpty()) {
            binding.frgRoomConnectionEtNameLayout.error = getString(R.string.enter_room_name)
            return
        }

        val code = binding.frgRoomConnectionEtAccessCode.text.toString()
        if (code.isEmpty()) {
            binding.frgRoomConnectionEtAccessCodeLayout.error = getString(R.string.enter_code)
            return
        }
        onCheck(name, code)
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .roomConnectionComponentFactory()
            .create(this)
            .inject(this)
    }

    companion object {
        private const val MODE_KEY = "ru.spbstu.pythian_games.MODE_KEY"
        fun makeBundle(mode: RoomMode): Bundle {
            return bundleOf(MODE_KEY to mode)
        }

        enum class RoomMode {
            Create,
            Join
        }
    }
}