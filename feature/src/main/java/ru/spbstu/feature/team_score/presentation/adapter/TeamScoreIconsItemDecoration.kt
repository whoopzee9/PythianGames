package ru.spbstu.feature.team_score.presentation.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.spbstu.common.extenstions.dpToPx

class TeamScoreIconsItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = parent.context.dpToPx(5f).toInt()
        outRect.top = parent.context.dpToPx(5f).toInt()
        outRect.left = parent.context.dpToPx(5f).toInt()
        outRect.right = parent.context.dpToPx(5f).toInt()
    }
}