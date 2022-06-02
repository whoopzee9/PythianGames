package ru.spbstu.feature.test.presentation.adapter

import android.view.ViewGroup
import ru.spbstu.common.base.BaseAdapter
import ru.spbstu.common.base.BaseViewHolder
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.databinding.ItemTestBinding
import ru.spbstu.feature.domain.model.InventoryModel


class TestAdapter(val onItemClick: (Long) -> Unit) :
    BaseAdapter<InventoryModel, TestAdapter.TestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder =
        TestViewHolder(parent)

    inner class TestViewHolder(parent: ViewGroup) :
        BaseViewHolder<InventoryModel, ItemTestBinding>(parent.viewBinding(ItemTestBinding::inflate)) {

        private lateinit var item: InventoryModel

        init {
            binding.root.setDebounceClickListener {
                onItemClick(item.id)
            }
        }

        override fun bind(item: InventoryModel) {
            this.item = item

        }
    }
}
