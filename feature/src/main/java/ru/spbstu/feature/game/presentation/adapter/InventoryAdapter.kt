package ru.spbstu.feature.game.presentation.adapter

import android.view.ViewGroup
import ru.spbstu.common.base.BaseAdapter
import ru.spbstu.common.base.BaseViewHolder
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.databinding.ItemInventoryBinding
import ru.spbstu.feature.domain.model.InventoryModel

class InventoryAdapter : BaseAdapter<InventoryModel, InventoryAdapter.InventoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder =
        InventoryViewHolder(parent)

    inner class InventoryViewHolder(parent: ViewGroup) :
        BaseViewHolder<InventoryModel, ItemInventoryBinding>(parent.viewBinding(ItemInventoryBinding::inflate)) {

        private lateinit var item: InventoryModel

        override fun bind(item: InventoryModel) {
            this.item = item
            binding.root.setImageResource(item.iconRes)
        }
    }
}
