package ru.spbstu.feature.test.presentation.adapter

import android.view.ViewGroup
import ru.spbstu.common.base.BaseAdapter
import ru.spbstu.common.base.BaseViewHolder
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.databinding.ItemTestBinding
import ru.spbstu.feature.domain.model.TestModel


class TestAdapter(val onItemClick: (Long) -> Unit) :
    BaseAdapter<TestModel, TestAdapter.TestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder =
        TestViewHolder(parent)

    inner class TestViewHolder(parent: ViewGroup) :
        BaseViewHolder<TestModel, ItemTestBinding>(parent.viewBinding(ItemTestBinding::inflate)) {

        private lateinit var item: TestModel

        init {
            binding.root.setDebounceClickListener {
                onItemClick(item.id)
            }
        }

        override fun bind(item: TestModel) {
            this.item = item
            binding.itemTestTvText.text = item.value
        }
    }
}
