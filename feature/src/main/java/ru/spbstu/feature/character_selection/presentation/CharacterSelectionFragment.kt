package ru.spbstu.feature.character_selection.presentation

import androidx.core.widget.addTextChangedListener
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentCharacterSelectionBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent

class CharacterSelectionFragment : BaseFragment<CharacterSelectionViewModel>(
    R.layout.fragment_character_selection,
) {
    override val binding by viewBinding(FragmentCharacterSelectionBinding::bind)

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()
        setNextButtonClickability()
        binding.frgCharacterSelectionIvCharacter1.setDebounceClickListener {
            binding.frgCharacterSelectionIvCharacter1.setBackgroundResource(R.drawable.background_character_selection)
            binding.frgCharacterSelectionIvCharacter2.background = null
            binding.frgCharacterSelectionIvCharacter3.background = null
            binding.frgCharacterSelectionIvCharacter4.background = null
            viewModel.isCharacterSelected = true
            setNextButtonClickability()
        }
        binding.frgCharacterSelectionIvCharacter2.setDebounceClickListener {
            binding.frgCharacterSelectionIvCharacter1.background = null
            binding.frgCharacterSelectionIvCharacter2.setBackgroundResource(R.drawable.background_character_selection)
            binding.frgCharacterSelectionIvCharacter3.background = null
            binding.frgCharacterSelectionIvCharacter4.background = null
            viewModel.isCharacterSelected = true
            setNextButtonClickability()
        }
        binding.frgCharacterSelectionIvCharacter3.setDebounceClickListener {
            binding.frgCharacterSelectionIvCharacter1.background = null
            binding.frgCharacterSelectionIvCharacter2.background = null
            binding.frgCharacterSelectionIvCharacter3.setBackgroundResource(R.drawable.background_character_selection)
            binding.frgCharacterSelectionIvCharacter4.background = null
            viewModel.isCharacterSelected = true
            setNextButtonClickability()
        }
        binding.frgCharacterSelectionIvCharacter4.setDebounceClickListener {
            binding.frgCharacterSelectionIvCharacter1.background = null
            binding.frgCharacterSelectionIvCharacter2.background = null
            binding.frgCharacterSelectionIvCharacter3.background = null
            binding.frgCharacterSelectionIvCharacter4.setBackgroundResource(R.drawable.background_character_selection)
            viewModel.isCharacterSelected = true
            setNextButtonClickability()
        }
        binding.frgCharacterSelectionEtName.addTextChangedListener {
            setNextButtonClickability()
        }


        //TODO this is for disabling characters
//        binding.frgCharacterSelectionIvCharacter4.imageTintList = ColorStateList.valueOf(
//            ContextCompat.getColor(
//                requireContext(),
//                R.color.image_disabled_tint
//            )
//        )
//        binding.frgCharacterSelectionIvCharacter4.imageTintMode = PorterDuff.Mode.MULTIPLY
    }

    private fun setNextButtonClickability() {
        binding.frgCharacterSelectionMbNext.isEnabled =
            viewModel.isCharacterSelected && !binding.frgCharacterSelectionEtName.text.isNullOrEmpty()
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .characterSelectionComponentFactory()
            .create(this)
            .inject(this)
    }
}