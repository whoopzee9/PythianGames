package ru.spbstu.feature.registration.presentation

import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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

    private lateinit var auth: FirebaseAuth

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()
        auth = Firebase.auth

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

    override fun subscribe() {
        super.subscribe()
        viewModel.state.observe {
            handleUIState(it)
        }
    }

    private fun handleUIState(state: RegistrationViewModel.UIState) {
        when (state) {
            RegistrationViewModel.UIState.Initial -> {
                binding.frgRegistrationPbProgress.visibility = View.GONE
                setActionsClickability(true)
            }
            RegistrationViewModel.UIState.Progress -> {
                binding.frgRegistrationPbProgress.visibility = View.VISIBLE
                setActionsClickability(false)
            }
            RegistrationViewModel.UIState.Success -> {
                binding.frgRegistrationPbProgress.visibility = View.GONE
                setActionsClickability(true)
            }
            is RegistrationViewModel.UIState.Failure -> {
                Toast.makeText(
                    requireContext(),
                    state.messageRes,
                    Toast.LENGTH_SHORT
                ).show()
                binding.frgRegistrationPbProgress.visibility = View.GONE
                setActionsClickability(true)
            }
        }
    }

    private fun setActionsClickability(isClickable: Boolean) {
        binding.frgRegistrationMbRegistration.isClickable = isClickable
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

        if (password.length < 6) {
            binding.frgRegistrationEtPasswordLayout.error = " "
            binding.frgRegistrationEtRepeatPasswordLayout.error =
                getString(R.string.short_password)
        }

        if (!viewModel.arePasswordsMatch(password, repeatPassword)) {
            binding.frgRegistrationEtPasswordLayout.error = " "
            binding.frgRegistrationEtRepeatPasswordLayout.error =
                getString(R.string.passwords_dont_match)
            return
        }

        viewModel.register(auth, email, password)
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .registrationComponentFactory()
            .create(this)
            .inject(this)
    }
}