package ru.spbstu.feature.auth.presentation

import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentAuthBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent

class AuthFragment : BaseFragment<AuthViewModel>(
    R.layout.fragment_auth,
) {
    override val binding by viewBinding(FragmentAuthBinding::bind)

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()
        binding.frgAuthMbLogin.setDebounceClickListener {
            viewModel.openLogin()
        }
        binding.frgAuthMbRegistration.setDebounceClickListener {
            viewModel.openRegistration()
        }
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .authComponentFactory()
            .create(this)
            .inject(this)
    }
}