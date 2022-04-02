package ru.spbstu.common.widgets

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import ru.spbstu.common.R
import ru.spbstu.common.extenstions.setDebounceClickListener

class BoardArrow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {

    private var direction: Direction = Direction.Up
    var isHighlighted = false
    private var onClickCallback: (() -> Unit)? = null

    init {
        setDirection(direction)

        setDebounceClickListener {
            isHighlighted = true
            drawable.setTintMode(PorterDuff.Mode.SRC_ATOP)
            requestLayout()
            onClickCallback?.invoke()
        }
    }

    fun setDirection(direction: Direction) {
        this.direction = direction
        when (direction) {
            Direction.Up -> {
                setImageResource(R.drawable.ic_board_arrow_down_26)
                rotation = 180f
            }
            Direction.Down -> {
                setImageResource(R.drawable.ic_board_arrow_down_26)
                rotation = 0f
            }
            Direction.Left -> {
                setImageResource(R.drawable.ic_board_arrow_left_26)
                rotation = 0f
            }
            Direction.Right -> {
                setImageResource(R.drawable.ic_board_arrow_left_26)
                rotation = 180f
            }
        }
        drawable.setTintMode(PorterDuff.Mode.DST)
        drawable.setTint(ContextCompat.getColor(context, R.color.arrow_highlighted_color))
        requestLayout()
    }

    fun getDirection() = direction

    fun setOnClickCallback(callback: () -> Unit) {
        onClickCallback = callback
    }

    fun resetHighlighted() {
        isHighlighted = false
        drawable.setTintMode(PorterDuff.Mode.DST)
        requestLayout()
    }

    enum class Direction {
        Up,
        Down,
        Left,
        Right
    }
}