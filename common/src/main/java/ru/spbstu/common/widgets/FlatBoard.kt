package ru.spbstu.common.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.core.content.ContextCompat
import ru.spbstu.common.R
import ru.spbstu.common.extenstions.dpToPx
import ru.spbstu.common.extenstions.scale
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.model.Player
import kotlin.math.min

class FlatBoard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    var size = 5
    var numOfPlayers = 8
    var numOfTeams = 4
    private val padding = resources.getDimension(R.dimen.dp_10).toInt()
    private val squareWidth = resources.getDimension(R.dimen.dp_60).toInt()
    private val squareHeight = resources.getDimension(R.dimen.dp_60).toInt()
    private var playersCount = 0
    private var leftPlayerOffset = 0
    private var topPlayerOffset = 0
    private var additionalMatchParentOffset = 0
    private var idsList: MutableList<String> = mutableListOf()

    init {
        for (i in 0 until size * size) {
            val background = GradientDrawable()
            background.color =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.background_primary))
            background.setStroke(
                resources.getDimension(R.dimen.dp_1).toInt(),
                ContextCompat.getColor(context, R.color.stroke_color_secondary)
            )
            val view = View(context)
            view.background = background
            val params = LayoutParams(squareWidth, squareHeight)
            view.layoutParams = params
            view.requestLayout()
            addView(view)
        }
    }

    fun getDisplayedPlayersIds(): List<String> = idsList

    fun addPlayer(player: Player) {
        val view = CharacterIcon(context)
        view.setDrawableResource(player.iconRes)
        view.setPlayer(player)
        addView(view)
        idsList.add(player.id)
        requestLayout()
        playersCount++
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)

            if (child is CharacterIcon) {

                val childLeft = paddingLeft
                val childTop = paddingTop
                val childRight = measuredWidth - paddingRight
                val childBottom = measuredHeight - paddingBottom
                val childWidth = childRight - childLeft
                val childHeight = childBottom - childTop

                child.setDebounceClickListener {
                    child.pivotY = child.height.toFloat()
                    child.pivotX = child.width / 2f
                    val to = if (child.scaleX == 1.0f) 1.2f else 1.0f
                    val scaleAnimation = ScaleAnimation(
                        child.scaleX,
                        to,
                        child.scaleY,
                        to,
                        child.pivotX,
                        child.pivotY
                    )
                    child.scale = to
                    scaleAnimation.duration = 100
                    scaleAnimation.fillAfter = true

                    scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationEnd(animation: Animation?) {
                            child.scale(to)
                        }

                        override fun onAnimationRepeat(animation: Animation?) {}
                    })
                    child.startAnimation(scaleAnimation)
                }

                child.measure(
                    MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST)
                )
                var curWidth: Int = child.measuredWidth
                var curHeight: Int = child.measuredHeight

                calculatePlayerOffset(child.getPlayer())

                val left = context.dpToPx(5f)
                    .toInt() + padding + leftPlayerOffset - child.additionalWidth / 2 + additionalMatchParentOffset
                val top = -context.dpToPx(10f).toInt() + padding * 4 + topPlayerOffset

                child.layout(left, top, curWidth + left, curHeight + top)

            } else {
                val h = i / size
                val w = i % size

                val curLeft = w * (squareWidth + padding) + padding + additionalMatchParentOffset
                val curTop = h * (squareHeight + padding) + padding * 4
                val curRight = curLeft + squareWidth
                val curBottom = curTop + squareHeight

                val background = child.background as GradientDrawable

                background.color =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.background_primary
                        )
                    )
                background.setStroke(
                    resources.getDimension(R.dimen.dp_1).toInt(),
                    ContextCompat.getColor(context, R.color.stroke_color_secondary)
                )

                when (numOfPlayers) {
                    2 -> {
                        if (i == 0) {
                            background.color = ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    context,
                                    R.color.color_team_green
                                )
                            )
                            child.background = background
                        }
                        if (i == size * size - 1) {
                            background.color = ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    context,
                                    R.color.color_team_blue
                                )
                            )
                            child.background = background
                        }
                    }
                    4 -> {
                        when (numOfTeams) {
                            2 -> {
                                if (i == 0 || i == size * size - 1) {
                                    background.color = ColorStateList.valueOf(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.color_team_green
                                        )
                                    )
                                    child.background = background
                                }
                                if (i == size - 1 || i == size * (size - 1)) {
                                    background.color = ColorStateList.valueOf(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.color_team_blue
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
                                            R.color.color_team_green
                                        )
                                    )
                                    child.background = background
                                }
                                if (i == size * size - 1) {
                                    background.color = ColorStateList.valueOf(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.color_team_blue
                                        )
                                    )
                                    child.background = background
                                }
                                if (i == size - 1) {
                                    background.color = ColorStateList.valueOf(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.color_team_orange
                                        )
                                    )
                                    child.background = background
                                }
                                if (i == size * (size - 1)) {
                                    background.color = ColorStateList.valueOf(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.color_team_red
                                        )
                                    )
                                    child.background = background
                                }
                            }
                        }
                    }
                    6 -> {
                        if (i == 0 || i == size * size - 1) {
                            background.color = ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    context,
                                    R.color.color_team_green
                                )
                            )
                            child.background = background
                        }
                        if (i == size - 1 || i == size * (size - 1)) {
                            background.color = ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    context,
                                    R.color.color_team_blue
                                )
                            )
                            child.background = background
                        }
                        if (i == size * (size / 2) || i == size * (size / 2) + size - 1) {
                            background.color = ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    context,
                                    R.color.color_team_red
                                )
                            )
                            child.background = background
                        }
                    }
                    8 -> {
                        when (numOfTeams) {
                            2 -> {
                                if (i == 0 || i == size * size - 1 || i == size - 1 || i == size * (size - 1)) {
                                    background.color = ColorStateList.valueOf(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.color_team_green
                                        )
                                    )
                                    child.background = background
                                }
                                if (i == size * (size / 2) || i == size * (size / 2) + size - 1 || i == size / 2 || i == size * (size - 1) + size / 2) {
                                    background.color = ColorStateList.valueOf(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.color_team_blue
                                        )
                                    )
                                    child.background = background
                                }
                            }
                            4 -> {
                                if (i == 0 || i == size * size - 1) {
                                    background.color = ColorStateList.valueOf(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.color_team_green
                                        )
                                    )
                                    child.background = background
                                }
                                if (i == size - 1 || i == size * (size - 1)) {
                                    background.color = ColorStateList.valueOf(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.color_team_blue
                                        )
                                    )
                                    child.background = background
                                }
                                if (i == size * (size / 2) || i == size * (size / 2) + size - 1) {
                                    background.color = ColorStateList.valueOf(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.color_team_red
                                        )
                                    )
                                    child.background = background
                                }
                                if (i == size / 2 || i == size * (size - 1) + size / 2) {
                                    background.color = ColorStateList.valueOf(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.color_team_orange
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
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var lp: LayoutParams? = layoutParams
        if (lp == null)
            lp = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )

        var width = calculateSize(suggestedMinimumWidth, lp.width, widthMeasureSpec, true)
        var height = calculateSize(suggestedMinimumHeight, lp.height, heightMeasureSpec, false)

        width += paddingLeft + paddingRight
        height += paddingTop + paddingBottom

        setMeasuredDimension(width, height)
    }

    private fun calculateSize(
        suggestedSize: Int,
        paramSize: Int,
        measureSpec: Int,
        isWidth: Boolean
    ): Int {
        var result = 0
        val size = MeasureSpec.getSize(measureSpec)
        val mode = MeasureSpec.getMode(measureSpec)

        if (paramSize == LayoutParams.MATCH_PARENT && isWidth) {
            additionalMatchParentOffset = (size - suggestedSize) / 2
        }

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
        return squareHeight * size + padding * (size + 3)
    }

    override fun getSuggestedMinimumWidth(): Int {
        return squareWidth * size + padding * (size + 1)
    }

    private fun calculatePlayerOffset(player: Player) {
        leftPlayerOffset = (squareWidth + padding) * player.position.x
        topPlayerOffset = (squareWidth + padding) * player.position.y
    }
}