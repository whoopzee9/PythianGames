package ru.spbstu.common.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import ru.spbstu.common.R
import ru.spbstu.common.extenstions.dpToPx
import ru.spbstu.common.model.PlayerBoard

class BoardIcon @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {
    private var activeTurnStrokeWidth = context.dpToPx(1f)
    private var playerBoard: PlayerBoard = PlayerBoard()
    private var characterBackground = ContextCompat.getDrawable(
        context,
        R.drawable.background_character_board
    ) as GradientDrawable
    private var iconHeight = context.dpToPx(32f)
    private var iconWidth = context.dpToPx(32f)

    fun setPlayer(playerBoard: PlayerBoard) {
        this.playerBoard = playerBoard
        val draw = ContextCompat.getDrawable(context, playerBoard.player.iconRes)
        draw?.setBounds(0, 0, iconWidth.toInt(), iconHeight.toInt())
        setImageDrawable(draw)
        characterBackground.color = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                playerBoard.player.team.colorRes
            )
        )
        characterBackground.setStroke(
            activeTurnStrokeWidth.toInt(),
            ContextCompat.getColor(
                context,
                if (playerBoard.player.isActiveTurn) R.color.color_active_turn else playerBoard.player.team.colorRes
            )
        )
        background = characterBackground
        requestLayout()
    }

    fun getPlayer() = playerBoard

}