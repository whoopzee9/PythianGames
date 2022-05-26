package ru.spbstu.feature.main.presentation

import android.view.View
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentMainBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.room_connection.presentation.RoomConnectionFragment

class MainFragment : BaseFragment<MainViewModel>(
    R.layout.fragment_main,
) {
    override val binding by viewBinding(FragmentMainBinding::bind)

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

        viewModel.getUserInfo {
            if (viewModel.lastGameName != null) {
                binding.frgMainMbReturnToGame.visibility = View.VISIBLE
            } else {
                binding.frgMainMbReturnToGame.visibility = View.GONE
            }
        }

        binding.frgMainMbReturnToGame.setDebounceClickListener {
            viewModel.openGameFragment()
        }
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .mainComponentFactory()
            .create(this)
            .inject(this)
    }
}