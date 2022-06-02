package ru.spbstu.feature.game.presentation.adapter

import android.view.ViewGroup
import ru.spbstu.common.base.BaseAdapter
import ru.spbstu.common.base.BaseViewHolder
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.ItemInventoryPlayerBinding
import ru.spbstu.feature.domain.model.InventoryPlayer

class InventoryDialogPlayersAdapter(val onItemClick: (InventoryPlayer) -> Unit) :
    BaseAdapter<InventoryPlayer, InventoryDialogPlayersAdapter.InventoryDialogPlayersViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InventoryDialogPlayersViewHolder =
        InventoryDialogPlayersViewHolder(parent)

    inner class InventoryDialogPlayersViewHolder(parent: ViewGroup) :
        BaseViewHolder<InventoryPlayer, ItemInventoryPlayerBinding>(
            parent.viewBinding(
                ItemInventoryPlayerBinding::inflate
            )
        ) {

        private lateinit var item: InventoryPlayer

        init {
            binding.root.setDebounceClickListener {
                onItemClick(item)
            }
        }

        override fun bind(item: InventoryPlayer) {
            this.item = item
            binding.root.setImageResource(item.iconRes)
            if (item.isSelected) {
                binding.root.setBackgroundResource(R.drawable.background_character_selection)
            } else {
                binding.root.background = null
            }
        }
    }
}