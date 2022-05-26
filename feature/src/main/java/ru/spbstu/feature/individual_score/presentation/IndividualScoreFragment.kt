package ru.spbstu.feature.individual_score.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.handleBackPressed
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.utils.DatabaseReferences
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentIndividualScoreBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.individual_score.presentation.adapter.IndividualScoreAdapter
import ru.spbstu.feature.individual_score.presentation.adapter.IndividualScoreItemDecoration

class IndividualScoreFragment : BaseFragment<IndividualScoreViewModel>(
    R.layout.fragment_individual_score,
) {
    private var _binding: FragmentIndividualScoreBinding? = null
    override val binding get() = _binding!!

    private lateinit var adapter: IndividualScoreAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIndividualScoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()

        setupAdapter()

        handleBackPressed { }
    }

    private fun setupAdapter() {
        adapter = IndividualScoreAdapter()
        binding.frgIndividualScoreRvPlayers.adapter = adapter
        binding.frgIndividualScoreRvPlayers.addItemDecoration(IndividualScoreItemDecoration())
        adapter.bindData(viewModel.getPlayersStats())
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .individualScoreComponentFactory()
            .create(this)
            .inject(this)
    }

}