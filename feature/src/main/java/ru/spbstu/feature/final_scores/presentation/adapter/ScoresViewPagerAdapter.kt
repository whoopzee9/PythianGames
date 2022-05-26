package ru.spbstu.feature.final_scores.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.spbstu.feature.individual_score.presentation.IndividualScoreFragment
import ru.spbstu.feature.team_score.presentation.TeamScoreFragment

class ScoresViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TeamScoreFragment()
            1 -> IndividualScoreFragment()
            else -> throw IllegalStateException("wrong viewpager position")
        }
    }
}