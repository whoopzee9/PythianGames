package ru.spbstu.feature.onboarding.presentation

import androidx.core.content.ContextCompat
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentOnboardingBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent

class OnboardingFragment : BaseFragment<OnboardingViewModel>(
    R.layout.fragment_onboarding,
) {

    override val binding by viewBinding(FragmentOnboardingBinding::bind)

    override fun setupViews() {
        super.setupViews()
        binding.frgOnboardingMbNext.setDebounceClickListener {
            viewModel.nextState()
        }
    }

    override fun subscribe() {
        super.subscribe()
        viewModel.uiState.observe {
            handleUIState(it)
        }
    }

    private fun handleUIState(state: OnboardingViewModel.UIState) {
        when (state) {
            OnboardingViewModel.UIState.First -> {
                binding.frgOnboardingIvIcon.setImageResource(R.drawable.ic_cleaning_196)
                binding.frgOnboardingTvTitle.setText(R.string.onboarding_excavations_title)
                binding.frgOnboardingTvDescription.setText(R.string.onboarding_excavations_description)
                binding.frgOnboardingView1.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.button_tint_secondary
                    )
                )
                binding.frgOnboardingView2.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.button_tint_primary
                    )
                )
                binding.frgOnboardingView3.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.button_tint_primary
                    )
                )
                binding.frgOnboardingMbNext.setText(R.string.next)
                binding.frgOnboardingMbNext.isChecked = false
            }
            OnboardingViewModel.UIState.Second -> {
                binding.frgOnboardingIvIcon.setImageResource(R.drawable.ic_tooth_117)
                binding.frgOnboardingTvTitle.setText(R.string.onboarding_artifacts_title)
                binding.frgOnboardingTvDescription.setText(R.string.onboarding_artifacts_description)
                binding.frgOnboardingView1.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.button_tint_primary
                    )
                )
                binding.frgOnboardingView2.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.button_tint_secondary
                    )
                )
                binding.frgOnboardingView3.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.button_tint_primary
                    )
                )
                binding.frgOnboardingMbNext.setText(R.string.next)
                binding.frgOnboardingMbNext.isChecked = false
            }
            OnboardingViewModel.UIState.Third -> {
                binding.frgOnboardingIvIcon.setImageResource(R.drawable.ic_teamup_199)
                binding.frgOnboardingTvTitle.setText(R.string.onboarding_teamup_title)
                binding.frgOnboardingTvDescription.setText(R.string.onboarding_teamup_description)
                binding.frgOnboardingView1.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.button_tint_primary
                    )
                )
                binding.frgOnboardingView2.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.button_tint_primary
                    )
                )
                binding.frgOnboardingView3.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.button_tint_secondary
                    )
                )
                binding.frgOnboardingMbNext.setText(R.string.play)
                binding.frgOnboardingMbNext.isChecked = true
            }
        }
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .onboardingComponentFactory()
            .create(this)
            .inject(this)
    }

}