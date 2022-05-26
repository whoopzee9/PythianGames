package ru.spbstu.feature.team_score.presentation.adapter

import android.view.ViewGroup
import ru.spbstu.common.base.BaseAdapter
import ru.spbstu.common.base.BaseViewHolder
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.databinding.ItemScoreIconBinding
import ru.spbstu.feature.domain.model.TeamPlayerIcon

class TeamScoreIconsAdapter :
    BaseAdapter<TeamPlayerIcon, TeamScoreIconsAdapter.TeamScoreIconsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamScoreIconsViewHolder =
        TeamScoreIconsViewHolder(parent)

    inner class TeamScoreIconsViewHolder(parent: ViewGroup) :
        BaseViewHolder<TeamPlayerIcon, ItemScoreIconBinding>(parent.viewBinding(ItemScoreIconBinding::inflate)) {

        private lateinit var item: TeamPlayerIcon

        override fun bind(item: TeamPlayerIcon) {
            this.item = item
            binding.root.setImageResource(item.iconRes)
        }
    }
}