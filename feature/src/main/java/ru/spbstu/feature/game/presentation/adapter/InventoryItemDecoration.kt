package ru.spbstu.feature.game.presentation.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.spbstu.common.extenstions.dpToPx

class InventoryItemDecoration: RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = parent.context.dpToPx(10f).toInt()
    }
}