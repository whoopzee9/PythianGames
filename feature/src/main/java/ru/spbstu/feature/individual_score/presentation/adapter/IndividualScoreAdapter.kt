package ru.spbstu.feature.individual_score.presentation.adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import ru.spbstu.common.base.BaseAdapter
import ru.spbstu.common.base.BaseViewHolder
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.GameUtils
import ru.spbstu.feature.databinding.ItemIndividualScoreBinding
import ru.spbstu.feature.domain.model.PlayerFinalStats

class IndividualScoreAdapter :
    BaseAdapter<PlayerFinalStats, IndividualScoreAdapter.IndividualScoreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndividualScoreViewHolder =
        IndividualScoreViewHolder(parent)

    inner class IndividualScoreViewHolder(parent: ViewGroup) :
        BaseViewHolder<PlayerFinalStats, ItemIndividualScoreBinding>(
            parent.viewBinding(
                ItemIndividualScoreBinding::inflate
            )
        ) {

        private lateinit var item: PlayerFinalStats

        override fun bind(item: PlayerFinalStats) {
            this.item = item
            binding.itemIndividualScoreIvIcon.setImageResource(item.imageRes)
            binding.itemIndividualScoreTvName.text = item.name
            binding.root.strokeColor =
                ContextCompat.getColor(binding.root.context, item.team.colorRes)
            var totalCoins = 0
            item.coinsCollected.forEach {
                totalCoins += GameUtils.getLayerNumber(GameUtils.Layers.valueOf(it.key)) * it.value
            }
            var totalQuestions = 0
            item.questionsAnswered.forEach {
                totalQuestions += GameUtils.getLayerNumber(GameUtils.Layers.valueOf(it.key)) * it.value
            }
            binding.itemIndividualScoreTvCoinsValueInYellow.text = totalCoins.toString()
            binding.itemIndividualScoreTvQuestionsValueInYellow.text = totalQuestions.toString()
            binding.itemIndividualScoreTvResultScore.text = (totalCoins + totalQuestions).toString()
        }
    }
}