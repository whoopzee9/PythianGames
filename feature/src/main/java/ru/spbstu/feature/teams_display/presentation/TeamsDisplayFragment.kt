package ru.spbstu.feature.teams_display.presentation

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.dpToPx
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.R
import ru.spbstu.feature.auth.presentation.AuthViewModel
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
//        val draw = (binding.view.background as GradientDrawable)
//        draw.setStroke(requireContext().dpToPx(2f).toInt(), ContextCompat.getColor(requireContext(), R.color.button_blue))
//        draw.color = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.button_red))
//        binding.view.background = draw
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .teamsDisplayComponentFactory()
            .create(this)
            .inject(this)
    }
}