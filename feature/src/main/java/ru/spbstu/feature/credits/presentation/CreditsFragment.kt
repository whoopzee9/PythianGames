package ru.spbstu.feature.credits.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.feature.R
import ru.spbstu.feature.credits.presentation.adapter.CreditsAdapter
import ru.spbstu.feature.credits.presentation.adapter.CreditsItemDecoration
import ru.spbstu.feature.databinding.FragmentCreditsBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.domain.model.CreditsModel

class CreditsFragment : BaseFragment<CreditsViewModel>(
    R.layout.fragment_credits,
) {
    private var _binding: FragmentCreditsBinding? = null
    override val binding get() = _binding!!

    private lateinit var adapter: CreditsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreditsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()

        setupAdapter()
    }

    private fun setupAdapter() {
        adapter = CreditsAdapter()
        binding.frgCreditsRvCredits.adapter = adapter
        binding.frgCreditsRvCredits.addItemDecoration(CreditsItemDecoration())

        adapter.bindData(listOf(
            CreditsModel(1, getString(R.string.authors_game_design_title), getString(R.string.authors_game_design)),
            CreditsModel(2, getString(R.string.authors_ux_design_title), getString(R.string.authors_ux_design)),
            CreditsModel(3, getString(R.string.authors_programming_title), getString(R.string.authors_programming)),
            CreditsModel(4, getString(R.string.authors_qa_title), getString(R.string.authors_qa))
        ))
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .creditsComponentFactory()
            .create(this)
            .inject(this)
    }

}