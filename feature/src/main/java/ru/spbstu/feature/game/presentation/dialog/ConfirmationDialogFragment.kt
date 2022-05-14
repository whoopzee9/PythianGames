package ru.spbstu.feature.game.presentation.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentConfirmationDialogBinding

class ConfirmationDialogFragment : DialogFragment() {
    private var _binding: FragmentConfirmationDialogBinding? = null
    private val binding get() = _binding!!
    private var onOkClickListener: (() -> Unit)? = null
    private var onCancelClickListener: (() -> Unit)? = null
    private var dialogWarningText: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmationDialogBinding.inflate(inflater, container, false)

        dialog?.window?.setDimAmount(DIM_ALPHA)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setupDialogView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.frgDeleteDialogMbCancel.setDebounceClickListener {
            onCancelClickListener?.invoke()
            dialog?.dismiss()
        }
        binding.frgDeleteDialogMbOk.setDebounceClickListener {
            onOkClickListener?.invoke()
            dialog?.dismiss()
        }
    }

    fun setOnOkClickListener(onClick: () -> Unit) {
        onOkClickListener = onClick
    }

    fun setOnCancelClickListener(onClick: () -> Unit) {
        onCancelClickListener = onClick
    }

    fun setDialogWarningText(dialogWarningText: String) {
        this.dialogWarningText = dialogWarningText
    }

    private fun setupDialogView() {
        binding.frgDeleteDialogText.text =
            if (!dialogWarningText.isNullOrEmpty()) dialogWarningText
            else getString(R.string.skip_confirmation)
    }

    companion object {
        private const val DIM_ALPHA = 0.25F
        fun newInstance(dialogWarningText: String): ConfirmationDialogFragment {
            val fragment = ConfirmationDialogFragment()
            fragment.setDialogWarningText(dialogWarningText)
            return fragment
        }
    }
}