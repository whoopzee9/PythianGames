package ru.spbstu.feature.game.presentation.adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import ru.spbstu.common.base.BaseAdapter
import ru.spbstu.common.base.BaseViewHolder
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.TeamsConstants
import ru.spbstu.feature.databinding.IncludeTeamStatisticsBinding
import ru.spbstu.feature.domain.model.TeamStatistics

class TeamsStatisticsAdapter :
    BaseAdapter<TeamStatistics, TeamsStatisticsAdapter.TeamsStatisticsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TeamsStatisticsViewHolder = TeamsStatisticsViewHolder(parent)

    inner class TeamsStatisticsViewHolder(parent: ViewGroup) :
        BaseViewHolder<TeamStatistics, IncludeTeamStatisticsBinding>(
            parent.viewBinding(
                IncludeTeamStatisticsBinding::inflate
            )
        ) {

        private lateinit var item: TeamStatistics

        override fun bind(item: TeamStatistics) {
            this.item = item
            binding.root.strokeColor = ContextCompat.getColor(
                binding.root.context,
                TeamsConstants.getTeamFromString(item.team).colorRes
            )
//            binding.includeTeamStatisticsTvTeamStatsCoinsValue.text = item.totalCoins.toString()
            binding.includeTeamStatisticsTvTeamStatsCoinsValueInYellow.text =
                item.totalCoinsInYellow.toString()
//            binding.includeTeamStatisticsTvTeamStatsQuestionsValue.text =
//                item.totalQuestions.toString()
            binding.includeTeamStatisticsTvTeamStatsQuestionsValueInYellow.text =
                item.totalQuestionsInYellow.toString()
            binding.includeTeamStatisticsTvTeamStatsInventoryValue.text =
                item.totalInventory.toString()
        }
    }
}