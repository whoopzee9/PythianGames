package ru.spbstu.feature.login.presentation

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
import ru.spbstu.feature.databinding.FragmentLoginBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent

class LoginFragment : BaseFragment<LoginViewModel>(
    R.layout.fragment_login,
) {
    override val binding by viewBinding(FragmentLoginBinding::bind)

    private lateinit var auth: FirebaseAuth

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()

        auth = Firebase.auth

        binding.frgLoginMbLogin.setDebounceClickListener {
            checkFields()
        }
        binding.frgLoginEtEmail.addTextChangedListener {
            binding.frgLoginEtEmailLayout.error = null
        }
        binding.frgLoginEtPassword.addTextChangedListener {
            binding.frgLoginEtPasswordLayout.error = null
        }
    }

    override fun subscribe() {
        super.subscribe()
        viewModel.state.observe {
            handleUIState(it)
        }
    }

    private fun handleUIState(state: LoginViewModel.UIState) {
        when (state) {
            LoginViewModel.UIState.Initial -> {
                binding.frgLoginPbProgress.visibility = View.GONE
                setActionsClickability(true)
            }
            LoginViewModel.UIState.Progress -> {
                binding.frgLoginPbProgress.visibility = View.VISIBLE
                setActionsClickability(false)
            }
            LoginViewModel.UIState.Success -> {
                binding.frgLoginPbProgress.visibility = View.GONE
                setActionsClickability(true)
            }
            LoginViewModel.UIState.Failure -> {
                Toast.makeText(
                    requireContext(),
                    R.string.try_again_later,
                    Toast.LENGTH_SHORT
                ).show()
                binding.frgLoginPbProgress.visibility = View.GONE
                setActionsClickability(true)
            }
        }
    }

    private fun setActionsClickability(isClickable: Boolean) {
        binding.frgLoginMbLogin.isClickable = isClickable
    }

    private fun checkFields() {
        val email = binding.frgLoginEtEmail.text.toString()
        if (email.isEmpty()) {
            binding.frgLoginEtEmailLayout.error = getString(R.string.enter_email)
            return
        }
        if (!viewModel.isEmailValid(email)) {
            binding.frgLoginEtEmailLayout.error = getString(R.string.wrong_email)
            return
        }

        val password = binding.frgLoginEtPassword.text.toString()
        if (password.isEmpty()) {
            binding.frgLoginEtPasswordLayout.error = getString(R.string.enter_password)
            return
        }

        viewModel.login(auth, email, password)
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .loginComponentFactory()
            .create(this)
            .inject(this)
    }
}