package ru.spbstu.feature.game.presentation.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import ru.spbstu.common.base.BaseAdapter
import ru.spbstu.common.base.BaseViewHolder
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.ItemInventoryDialogBinding
import ru.spbstu.feature.domain.model.InventoryModel

class InventoryDialogAdapter(val onItemClick: (InventoryModel) -> Unit) :
    BaseAdapter<InventoryModel, InventoryDialogAdapter.InventoryDialogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryDialogViewHolder =
        InventoryDialogViewHolder(parent)

    inner class InventoryDialogViewHolder(parent: ViewGroup) :
        BaseViewHolder<InventoryModel, ItemInventoryDialogBinding>(
            parent.viewBinding(
                ItemInventoryDialogBinding::inflate
            )
        ) {

        private lateinit var item: InventoryModel

        init {
            binding.root.setDebounceClickListener {
                onItemClick(item)
            }
        }

        override fun bind(item: InventoryModel) {
            this.item = item
            binding.itemInventoryDialogIvItem.setImageResource(item.iconRes)
            if (item.isSelected) {
                binding.root.strokeColor =
                    ContextCompat.getColor(binding.root.context, R.color.button_tint_primary)
                binding.root.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.button_tint_primary
                    )
                )
            } else {
                binding.root.strokeColor =
                    ContextCompat.getColor(binding.root.context, R.color.stroke_color_secondary)
                binding.root.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.background_primary
                    )
                )
            }
        }
    }
}