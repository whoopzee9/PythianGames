package ru.spbstu.feature.room_connection.presentation

import android.os.Bundle
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
    }

    override fun setupFromArguments(args: Bundle) {
        super.setupFromArguments(args)
        args.getSerializable(MODE_KEY).let {
            when (it as RoomMode) {
                RoomMode.Create -> {
                    binding.frgRoomConnectionMbNext.setText(R.string.create)
                    binding.frgRoomConnectionMbNext.setDebounceClickListener {
                        checkFields { name, code ->
                            viewModel.createRoom(name, code)
                        }
                    }
                }
                RoomMode.Join -> {
                    binding.frgRoomConnectionMbNext.setText(R.string.join)
                    binding.frgRoomConnectionMbNext.setDebounceClickListener {
                        checkFields { name, code ->
                            viewModel.joinRoom(name, code)
                        }
                    }
                }
            }
        }
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