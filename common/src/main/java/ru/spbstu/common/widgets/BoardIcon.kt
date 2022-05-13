package ru.spbstu.common.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import androidx.core.content.ContextCompat
import ru.spbstu.common.R
import ru.spbstu.common.extenstions.dpToPx
import ru.spbstu.common.model.Player

class BoardIcon @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {
    private var activeTurnStrokeWidth = context.dpToPx(2f)
    private var player: Player = Player()
    private var characterBackground = ContextCompat.getDrawable(
        context,
        R.drawable.background_character_board
    ) as GradientDrawable
    private var iconHeight = resources.getDimension(R.dimen.dp_32)
    private var iconWidth = resources.getDimension(R.dimen.dp_32)
    private var activePlayerId: String = ""

    fun setPlayer(player: Player) {
        this.player = player
        val draw = ContextCompat.getDrawable(context, player.iconRes)
        draw?.setBounds(0, 0, iconWidth.toInt(), iconHeight.toInt())
        setImageDrawable(draw)
        updateBackground()
        requestLayout()
    }

    private fun updateBackground() {
        characterBackground.color = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                player.team.colorRes
            )
        )

        characterBackground.setStroke(
            activeTurnStrokeWidth.toInt(),
            ContextCompat.getColor(
                context,
                if (player.id == activePlayerId)
                    R.color.color_active_turn
                else
                    player.team.colorRes
            )
        )
        background = characterBackground
    }

    fun getPlayer() = player

    fun setActivePlayerId(id: String) {
        activePlayerId = id
        updateBackground()
        requestLayout()
    }
}