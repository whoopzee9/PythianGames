package ru.spbstu.common.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import ru.spbstu.common.R
import ru.spbstu.common.extenstions.dpToPx
import kotlin.math.min

class FlatBoard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    var size = 5
    var numOfPlayers = 8
    var numOfTeams = 2
    private val padding = context.dpToPx(10f).toInt()
    private val childWidth = context.dpToPx(60f).toInt()
    private val childHeight = context.dpToPx(60f).toInt()

    init {
        for (i in 0 until size * size) {
            val background = GradientDrawable()
            background.color =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.background_primary))
            background.setStroke(
                context.dpToPx(1f).toInt(),
                ContextCompat.getColor(context, R.color.stroke_color_secondary)
            )
            val view = View(context)
            view.background = background
            val params = LayoutParams(childWidth, childHeight)
            view.layoutParams = params
            view.requestLayout()
            addView(view)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val h = i / size
            val w = i % size

            val curLeft = w * (childWidth + padding)
            val curTop = h * (childHeight + padding)
            val curRight = curLeft + childWidth
            val curBottom = curTop + childHeight

            val background = child.background as GradientDrawable

            background.color =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.background_primary))
            background.setStroke(
                context.dpToPx(1f).toInt(),
                ContextCompat.getColor(context, R.color.stroke_color_secondary)
            )

            when (numOfPlayers) {
                2 -> {
                    if (i == 0) {
                        background.color = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.button_green
                            )
                        )
                        child.background = background
                    }
                    if (i == childCount - 1) {
                        background.color = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.button_blue
                            )
                        )
                        child.background = background
                    }
                }
                4 -> {
                    when (numOfTeams) {
                        2 -> {
                            if (i == 0 || i == childCount - 1) {
                                background.color = ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.button_green
                                    )
                                )
                                child.background = background
                            }
                            if (i == size - 1 || i == size * (size - 1)) {
                                background.color = ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.button_blue
                                    )
                                )
                                child.background = background
                            }
                        }
                        4 -> {
                            if (i == 0) {
                                background.color = ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.button_green
                                    )
                                )
                                child.background = background
                            }
                            if (i == childCount - 1) {
                                background.color = ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.button_blue
                                    )
                                )
                                child.background = background
                            }
                            if (i == size - 1) {
                                background.color = ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.button_orange
                                    )
                                )
                                child.background = background
                            }
                            if (i == size * (size - 1)) {
                                background.color = ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.button_red
                                    )
                                )
                                child.background = background
                            }
                        }
                    }
                }
                6 -> {
                    if (i == 0 || i == childCount - 1) {
                        background.color = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.button_green
                            )
                        )
                        child.background = background
                    }
                    if (i == size - 1 || i == size * (size - 1)) {
                        background.color = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.button_blue
                            )
                        )
                        child.background = background
                    }
                    if (i == size * (size / 2) || i == size * (size / 2) + size - 1) {
                        background.color = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.button_blue
                            )
                        )
                        child.background = background
                    }
                }
                8 -> {
                    when (numOfTeams) {
                        2 -> {
                            if (i == 0 || i == childCount - 1 || i == size - 1 || i == size * (size - 1)) {
                                background.color = ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.button_green
                                    )
                                )
                                child.background = background
                            }
                            if (i == size * (size / 2) || i == size * (size / 2) + size - 1 || i == size / 2 || i == size * (size - 1) + size / 2) {
                                background.color = ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.button_blue
                                    )
                                )
                                child.background = background
                            }
                        }
                        4 -> {
                            if (i == 0 || i == childCount - 1) {
                                background.color = ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.button_green
                                    )
                                )
                                child.background = background
                            }
                            if (i == size - 1 || i == size * (size - 1)) {
                                background.color = ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.button_blue
                                    )
                                )
                                child.background = background
                            }
                            if (i == size * (size / 2) || i == size * (size / 2) + size - 1) {
                                background.color = ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.button_orange
                                    )
                                )
                                child.background = background
                            }
                            if (i == size / 2 || i == size * (size - 1) + size / 2) {
                                background.color = ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.button_red
                                    )
                                )
                                child.background = background
                            }
                        }
                    }
                }
                else -> {}
            }

            child.background = background

            child.layout(curLeft, curTop, curRight, curBottom)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var lp: LayoutParams? = layoutParams
        if (lp == null)
            lp = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )

        var width = calculateSize(suggestedMinimumWidth, lp.width, widthMeasureSpec)
        var height = calculateSize(suggestedMinimumHeight, lp.height, heightMeasureSpec)

        width += paddingLeft + paddingRight
        height += paddingTop + paddingBottom

        setMeasuredDimension(width, height)
    }

    private fun calculateSize(suggestedSize: Int, paramSize: Int, measureSpec: Int): Int {
        var result = 0
        val size = MeasureSpec.getSize(measureSpec)
        val mode = MeasureSpec.getMode(measureSpec)

        when (mode) {
            MeasureSpec.AT_MOST ->
                result = when (paramSize) {
                    LayoutParams.WRAP_CONTENT -> min(suggestedSize, size)
                    LayoutParams.MATCH_PARENT -> size
                    else -> min(paramSize, size)
                }
            MeasureSpec.EXACTLY -> result = size
            MeasureSpec.UNSPECIFIED ->
                result =
                    if (paramSize == LayoutParams.WRAP_CONTENT ||
                        paramSize == LayoutParams.MATCH_PARENT
                    )
                        suggestedSize
                    else {
                        paramSize
                    }
        }

        return result
    }

    override fun getSuggestedMinimumHeight(): Int {
        return childHeight * size + padding * (size - 1)
    }

    override fun getSuggestedMinimumWidth(): Int {
        return childWidth * size + padding * (size - 1)
    }
}