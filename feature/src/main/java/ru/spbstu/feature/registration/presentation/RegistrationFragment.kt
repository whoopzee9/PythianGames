package ru.spbstu.feature.registration.presentation

import androidx.core.widget.addTextChangedListener
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentRegistrationBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent

class RegistrationFragment : BaseFragment<RegistrationViewModel>(
    R.layout.fragment_registration,
) {

    override val binding by viewBinding(FragmentRegistrationBinding::bind)

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()

        binding.frgRegistrationMbRegistration.setDebounceClickListener {
            checkFields()
        }
        binding.frgRegistrationEtEmail.addTextChangedListener {
            binding.frgRegistrationEtEmailLayout.error = null
            //binding.frgRegistrationEtEmailLayout.isErrorEnabled = false
        }
        binding.frgRegistrationEtPassword.addTextChangedListener {
            binding.frgRegistrationEtPasswordLayout.error = null
            //binding.frgRegistrationEtEmailLayout.isErrorEnabled = false
        }
        binding.frgRegistrationEtRepeatPassword.addTextChangedListener {
            binding.frgRegistrationEtRepeatPasswordLayout.error = null
            //binding.frgRegistrationEtEmailLayout.isErrorEnabled = false
        }
    }

    private fun checkFields() {
        val email = binding.frgRegistrationEtEmail.text.toString()
        if (email.isEmpty()) {
            binding.frgRegistrationEtEmailLayout.error = getString(R.string.enter_email)
            return
        }
        if (!viewModel.isEmailValid(email)) {
            binding.frgRegistrationEtEmailLayout.error = getString(R.string.wrong_email)
            return
        }

        val password = binding.frgRegistrationEtPassword.text.toString()
        if (password.isEmpty()) {
            binding.frgRegistrationEtPasswordLayout.error = getString(R.string.enter_password)
            return
        }

        val repeatPassword = binding.frgRegistrationEtRepeatPassword.text.toString()
        if (repeatPassword.isEmpty()) {
            binding.frgRegistrationEtRepeatPasswordLayout.error = getString(R.string.enter_password)
            return
        }

        if (!viewModel.arePasswordsMatch(password, repeatPassword)) {
            binding.frgRegistrationEtPasswordLayout.error = " "
            binding.frgRegistrationEtRepeatPasswordLayout.error =
                getString(R.string.passwords_dont_match)
            return
        }

        viewModel.register(email, password)
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .registrationComponentFactory()
            .create(this)
            .inject(this)
    }
}