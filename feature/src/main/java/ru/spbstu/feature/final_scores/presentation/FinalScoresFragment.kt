package ru.spbstu.feature.final_scores.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.handleBackPressed
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.utils.DatabaseReferences
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentFinalScoresBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.final_scores.presentation.adapter.ScoresViewPagerAdapter

class FinalScoresFragment : BaseFragment<FinalScoresViewModel>(
    R.layout.fragment_final_scores,
) {
    private var _binding: FragmentFinalScoresBinding? = null
    override val binding get() = _binding!!

    private lateinit var adapter: ScoresViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinalScoresBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()

        setupViewPager()

        binding.frgFinalScoresMbQuit.setDebounceClickListener {
            viewModel.clearUserLastGame {
                viewModel.openMainFragment()
            }
        }

        handleBackPressed { }
    }

    private fun setupViewPager() {
        adapter = ScoresViewPagerAdapter(this)
        binding.frgFinalScoresViewPager.adapter = adapter
        /*
            This is needed to remove overscrolling from viewPager
            Link to this solution: https://stackoverflow.com/a/56942231
         */
        val child = binding.frgFinalScoresViewPager.getChildAt(0)
        if (child is RecyclerView) {
            child.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }
        binding.frgFinalScoresViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    binding.frgFinalScoresView1.background.setTint(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.button_tint_secondary
                        )
                    )
                    binding.frgFinalScoresView2.background.setTint(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.button_tint_primary
                        )
                    )
                } else {
                    binding.frgFinalScoresView1.background.setTint(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.button_tint_primary
                        )
                    )
                    binding.frgFinalScoresView2.background.setTint(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.button_tint_secondary
                        )
                    )
                }
            }
        })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .finalScoresComponentFactory()
            .create(this)
            .inject(this)
    }

}