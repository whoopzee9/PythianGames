package ru.spbstu.feature.credits.presentation.adapter

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.spbstu.common.base.BaseAdapter
import ru.spbstu.common.base.BaseViewHolder
import ru.spbstu.common.extenstions.dpToPx
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.databinding.ItemCreditBinding
import ru.spbstu.feature.domain.model.CreditsModel

class CreditsAdapter : BaseAdapter<CreditsModel, CreditsAdapter.CreditsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditsViewHolder =
        CreditsViewHolder(parent)

    inner class CreditsViewHolder(parent: ViewGroup) :
        BaseViewHolder<CreditsModel, ItemCreditBinding>(parent.viewBinding(ItemCreditBinding::inflate)) {

        private lateinit var item: CreditsModel

        override fun bind(item: CreditsModel) {
            this.item = item
            binding.itemCreditTvTitle.text = item.title
            binding.itemCreditTvDesc.text = item.desc
        }
    }
}

class CreditsItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.right = parent.context.dpToPx(16f).toInt()
        outRect.left = parent.context.dpToPx(16f).toInt()
        outRect.top = parent.context.dpToPx(16f).toInt()
        outRect.bottom = parent.context.dpToPx(16f).toInt()
    }
}

