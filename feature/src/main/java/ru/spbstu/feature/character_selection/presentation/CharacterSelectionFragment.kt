package ru.spbstu.feature.character_selection.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setDisabled
import ru.spbstu.common.extenstions.setEnabled
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.subscribe
import ru.spbstu.common.utils.DatabaseReferences
import ru.spbstu.common.utils.TeamsConstants
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentCharacterSelectionBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.domain.model.PlayerInfo

class CharacterSelectionFragment : BaseFragment<CharacterSelectionViewModel>(
    R.layout.fragment_character_selection,
) {

    private var _binding: FragmentCharacterSelectionBinding? = null
    override val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()
        setNextButtonClickability()
        setCharacters()

        with(binding) {

            frgCharacterSelectionIvCharacter1.setDebounceClickListener {
                frgCharacterSelectionIvCharacter1.setBackgroundResource(R.drawable.background_character_selection)
                frgCharacterSelectionIvCharacter2.background = null
                frgCharacterSelectionIvCharacter3.background = null
                frgCharacterSelectionIvCharacter4.background = null
                viewModel.selectedCharacterNum = 1
                setNextButtonClickability()
            }
            frgCharacterSelectionIvCharacter2.setDebounceClickListener {
                frgCharacterSelectionIvCharacter1.background = null
                frgCharacterSelectionIvCharacter2.setBackgroundResource(R.drawable.background_character_selection)
                frgCharacterSelectionIvCharacter3.background = null
                frgCharacterSelectionIvCharacter4.background = null
                viewModel.selectedCharacterNum = 2
                setNextButtonClickability()
            }
            frgCharacterSelectionIvCharacter3.setDebounceClickListener {
                frgCharacterSelectionIvCharacter1.background = null
                frgCharacterSelectionIvCharacter2.background = null
                frgCharacterSelectionIvCharacter3.setBackgroundResource(R.drawable.background_character_selection)
                frgCharacterSelectionIvCharacter4.background = null
                viewModel.selectedCharacterNum = 3
                setNextButtonClickability()
            }
            frgCharacterSelectionIvCharacter4.setDebounceClickListener {
                frgCharacterSelectionIvCharacter1.background = null
                frgCharacterSelectionIvCharacter2.background = null
                frgCharacterSelectionIvCharacter3.background = null
                frgCharacterSelectionIvCharacter4.setBackgroundResource(R.drawable.background_character_selection)
                viewModel.selectedCharacterNum = 4
                setNextButtonClickability()
            }
            frgCharacterSelectionEtName.addTextChangedListener {
                setNextButtonClickability()
            }

            frgCharacterSelectionMbNext.setDebounceClickListener {
                val name = frgCharacterSelectionEtName.text.toString()
                if (name.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.empty_name, Toast.LENGTH_SHORT).show()
                } else {
                    val selectedView = when (viewModel.selectedCharacterNum) {
                        1 -> frgCharacterSelectionIvCharacter1
                        2 -> frgCharacterSelectionIvCharacter2
                        3 -> frgCharacterSelectionIvCharacter3
                        4 -> frgCharacterSelectionIvCharacter4
                        else -> {
                            Toast.makeText(
                                requireContext(),
                                R.string.character_not_selected,
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setDebounceClickListener
                        }
                    }
                    val id = selectedView.tag as Int
                    viewModel.setCharacter(id, name)
                }
            }
        }
    }

    override fun subscribe() {
        super.subscribe()
        val ref = Firebase.database.getReference(DatabaseReferences.GAMES_REF)
        ref.child(viewModel.gameJoiningDataWrapper.game.name).child("players").subscribe(
            onSuccess = { snapshot ->
                handleGameSnapshotData(snapshot)
            }, onCancelled = {
            }
        )
    }

    private fun handleGameSnapshotData(snapshot: DataSnapshot) {
        val generic = object : GenericTypeIndicator<HashMap<String, PlayerInfo>>() {}
        val players = snapshot.getValue(generic)
        players?.forEach { entry ->
            val imageViewList = listOf(
                binding.frgCharacterSelectionIvCharacter1,
                binding.frgCharacterSelectionIvCharacter2,
                binding.frgCharacterSelectionIvCharacter3,
                binding.frgCharacterSelectionIvCharacter4
            ).filter { it.visibility == View.VISIBLE }
            imageViewList.forEach {
                if (entry.value.iconRes != 0 && it.tag as Int == entry.value.iconRes) {
                    it.setDisabled()
                } else {
                    it.setEnabled()
                }
            }
        }
    }

    private fun setCharacters() {
        when (viewModel.gameJoiningDataWrapper.game.numOfTeams) {
            2 -> { //choice of 4 characters for each team
                binding.frgCharacterSelectionIvCharacter1.visibility = View.VISIBLE
                binding.frgCharacterSelectionIvCharacter2.visibility = View.VISIBLE
                binding.frgCharacterSelectionIvCharacter3.visibility = View.VISIBLE
                binding.frgCharacterSelectionIvCharacter4.visibility = View.VISIBLE
                when (viewModel.gameJoiningDataWrapper.playerInfo.teamStr) {
                    TeamsConstants.GREEN_TEAM -> {
                        binding.frgCharacterSelectionIvCharacter1.setImageResource(R.drawable.character_1)
                        binding.frgCharacterSelectionIvCharacter1.tag = R.drawable.character_1
                        binding.frgCharacterSelectionIvCharacter2.setImageResource(R.drawable.character_2)
                        binding.frgCharacterSelectionIvCharacter2.tag = R.drawable.character_2
                        binding.frgCharacterSelectionIvCharacter3.setImageResource(R.drawable.character_3)
                        binding.frgCharacterSelectionIvCharacter3.tag = R.drawable.character_3
                        binding.frgCharacterSelectionIvCharacter4.setImageResource(R.drawable.character_4)
                        binding.frgCharacterSelectionIvCharacter4.tag = R.drawable.character_4
                    }
                    TeamsConstants.BLUE_TEAM -> {
                        binding.frgCharacterSelectionIvCharacter1.setImageResource(R.drawable.character_5)
                        binding.frgCharacterSelectionIvCharacter1.tag = R.drawable.character_5
                        binding.frgCharacterSelectionIvCharacter2.setImageResource(R.drawable.character_6)
                        binding.frgCharacterSelectionIvCharacter2.tag = R.drawable.character_6
                        binding.frgCharacterSelectionIvCharacter3.setImageResource(R.drawable.character_7)
                        binding.frgCharacterSelectionIvCharacter3.tag = R.drawable.character_7
                        binding.frgCharacterSelectionIvCharacter4.setImageResource(R.drawable.character_8)
                        binding.frgCharacterSelectionIvCharacter4.tag = R.drawable.character_8
                    }
                    else -> throw IllegalStateException("Wrong teams in ${CharacterSelectionFragment::class.simpleName}!")
                }
            }
            3, 4 -> { //choice of 2 characters for each team
                binding.frgCharacterSelectionIvCharacter1.visibility = View.VISIBLE
                binding.frgCharacterSelectionIvCharacter2.visibility = View.GONE
                binding.frgCharacterSelectionIvCharacter3.visibility = View.VISIBLE
                binding.frgCharacterSelectionIvCharacter4.visibility = View.GONE
                when (viewModel.gameJoiningDataWrapper.playerInfo.teamStr) {
                    TeamsConstants.GREEN_TEAM -> {
                        binding.frgCharacterSelectionIvCharacter1.setImageResource(R.drawable.character_1)
                        binding.frgCharacterSelectionIvCharacter1.tag = R.drawable.character_1
                        binding.frgCharacterSelectionIvCharacter3.setImageResource(R.drawable.character_2)
                        binding.frgCharacterSelectionIvCharacter3.tag = R.drawable.character_2
                    }
                    TeamsConstants.BLUE_TEAM -> {
                        binding.frgCharacterSelectionIvCharacter1.setImageResource(R.drawable.character_3)
                        binding.frgCharacterSelectionIvCharacter1.tag = R.drawable.character_3
                        binding.frgCharacterSelectionIvCharacter3.setImageResource(R.drawable.character_4)
                        binding.frgCharacterSelectionIvCharacter3.tag = R.drawable.character_4
                    }
                    TeamsConstants.RED_TEAM -> {
                        binding.frgCharacterSelectionIvCharacter1.setImageResource(R.drawable.character_5)
                        binding.frgCharacterSelectionIvCharacter1.tag = R.drawable.character_5
                        binding.frgCharacterSelectionIvCharacter3.setImageResource(R.drawable.character_6)
                        binding.frgCharacterSelectionIvCharacter3.tag = R.drawable.character_6
                    }
                    TeamsConstants.ORANGE_TEAM -> {
                        binding.frgCharacterSelectionIvCharacter1.setImageResource(R.drawable.character_7)
                        binding.frgCharacterSelectionIvCharacter1.tag = R.drawable.character_7
                        binding.frgCharacterSelectionIvCharacter3.setImageResource(R.drawable.character_8)
                        binding.frgCharacterSelectionIvCharacter3.tag = R.drawable.character_8
                    }
                    else -> throw IllegalStateException("Wrong teams in ${CharacterSelectionFragment::class.simpleName}!")
                }
            }
            else -> throw IllegalStateException("Wrong teams amount in ${CharacterSelectionFragment::class.simpleName}!")
        }

    }

    private fun setNextButtonClickability() {
        binding.frgCharacterSelectionMbNext.isEnabled =
            viewModel.selectedCharacterNum != 0 && !binding.frgCharacterSelectionEtName.text.isNullOrEmpty()
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .characterSelectionComponentFactory()
            .create(this)
            .inject(this)
    }
}