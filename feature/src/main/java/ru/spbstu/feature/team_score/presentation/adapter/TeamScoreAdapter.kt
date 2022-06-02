package ru.spbstu.feature.team_score.presentation.adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import ru.spbstu.common.base.BaseAdapter
import ru.spbstu.common.base.BaseViewHolder
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.GameUtils
import ru.spbstu.feature.databinding.ItemTeamsScoreBinding
import ru.spbstu.feature.domain.model.TeamFinalStats

class TeamScoreAdapter : BaseAdapter<TeamFinalStats, TeamScoreAdapter.TeamScoreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamScoreViewHolder =
        TeamScoreViewHolder(parent)

    inner class TeamScoreViewHolder(parent: ViewGroup) :
        BaseViewHolder<TeamFinalStats, ItemTeamsScoreBinding>(
            parent.viewBinding(
                ItemTeamsScoreBinding::inflate
            )
        ) {

        private lateinit var item: TeamFinalStats
        private val adapter = TeamScoreIconsAdapter()

        init {
            binding.itemTeamsScoreRvIcons.adapter = adapter
            binding.itemTeamsScoreRvIcons.addItemDecoration(TeamScoreIconsItemDecoration())
        }

        override fun bind(item: TeamFinalStats) {
            this.item = item
            adapter.bindData(item.players)

            binding.root.strokeColor =
                ContextCompat.getColor(binding.root.context, item.team.colorRes)
            var totalCoins = 0
            var totalCoinsInYellow = 0
            item.coinsCollected.forEach {
                totalCoins += it.value
                totalCoinsInYellow += GameUtils.getLayerNumber(GameUtils.Layers.valueOf(it.key)) * it.value
            }
            var totalQuestions = 0
            var totalQuestionsInYellow = 0
            item.questionsAnswered.forEach {
                totalQuestions += it.value
                totalQuestionsInYellow += GameUtils.getLayerNumber(GameUtils.Layers.valueOf(it.key)) * it.value
            }
            //binding.itemTeamsScoreTvTeamStatsCoinsValue.text = totalCoins.toString()
            binding.itemTeamsScoreTvTeamStatsCoinsValueInYellow.text = totalCoinsInYellow.toString()
            //binding.itemTeamsScoreTvTeamStatsQuestionsValue.text = totalQuestions.toString()
            binding.itemTeamsScoreTvTeamStatsQuestionsValueInYellow.text =
                totalQuestionsInYellow.toString()
        }
    }
}