package ru.spbstu.common.widgets

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import ru.spbstu.common.R
import ru.spbstu.common.extenstions.dpToPx
import ru.spbstu.common.model.Player
import ru.spbstu.common.utils.ZoomHelper
import kotlin.math.min

class Board @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private var size = 5
    private var totalPlayers = 8
    private var totalTeams = 4

    //todo idk what to do with these shifts
    private val dx = 32f
    private val dy = 19f

    private var cardHeight = resources.getDimension(R.dimen.dp_35)
    private var cardWidth = resources.getDimension(R.dimen.dp_58)
    private var spacing = resources.getDimension(R.dimen.dp_8)
    private var boardSpacing = context.dpToPx(5.5f)

    private var iconHeight = resources.getDimension(R.dimen.dp_32)
    private var iconWidth = resources.getDimension(R.dimen.dp_32)
    private var strokeWidth = resources.getDimension(R.dimen.dp_3)
    private var activeTurnStrokeWidth = resources.getDimension(R.dimen.dp_1)
    private var characterBackground: GradientDrawable

    private val playersList: MutableList<Player> = mutableListOf()

    private var currPlayer: Player? = null

    private val zoomHelper = ZoomHelper(this)

    init {

        characterBackground = ContextCompat.getDrawable(
            context,
            R.drawable.background_character_board
        ) as GradientDrawable

        val attributesArray =
            context.obtainStyledAttributes(attrs, R.styleable.Board, defStyleAttr, 0)

        size = attributesArray.getInt(
            R.styleable.Board_board_size,
            5
        )

        initView()

        attributesArray.recycle()
    }

    private fun initView() {
        val board = MorganBoard(context, attrs, defStyleAttr)
        board.setSize(size)
        addView(board)

        for (i in 0 until size * size) {
            addView(CardStack(context))
        }

        // arrows
        val arrowUp = BoardArrow(context)
        arrowUp.setDirection(BoardArrow.Direction.Up)
        val arrowDown = BoardArrow(context)
        arrowDown.setDirection(BoardArrow.Direction.Down)
        val arrowLeft = BoardArrow(context)
        arrowLeft.setDirection(BoardArrow.Direction.Left)
        val arrowRight = BoardArrow(context)
        arrowRight.setDirection(BoardArrow.Direction.Right)
        arrowUp.setOnClickCallback {
            arrowDown.resetHighlighted()
            arrowLeft.resetHighlighted()
            arrowRight.resetHighlighted()
        }
        arrowDown.setOnClickCallback {
            arrowUp.resetHighlighted()
            arrowLeft.resetHighlighted()
            arrowRight.resetHighlighted()
        }
        arrowLeft.setOnClickCallback {
            arrowUp.resetHighlighted()
            arrowDown.resetHighlighted()
            arrowRight.resetHighlighted()
        }
        arrowRight.setOnClickCallback {
            arrowUp.resetHighlighted()
            arrowDown.resetHighlighted()
            arrowLeft.resetHighlighted()
        }
        addView(arrowUp)
        addView(arrowDown)
        addView(arrowLeft)
        addView(arrowRight)
    }

    fun setTotalPlayers(players: Int) {
        totalPlayers = players
    }

    fun setTotalTeams(teams: Int) {
        totalTeams = teams
    }

    fun addPlayer(player: Player) {
        playersList.add(player)
        val icon = BoardIcon(context)
        icon.setPlayer(player)
        addView(icon)
        requestLayout()
    }

    fun setCurrentPlayer(playerId: String) {
        currPlayer = playersList.first { it.id == playerId }
    }

    fun getCurrentPlayer() = currPlayer

    fun setSize(size: Int) {
        this.size = size
        removeAllViews()
        initView()
        invalidate()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val childLeft = paddingLeft
        val childTop = paddingTop
        val childRight = measuredWidth - paddingRight
        val childBottom = measuredHeight - paddingBottom
        val childWidth = childRight - childLeft
        val childHeight = childBottom - childTop

        for (i in 0 until childCount) {
            when (val child = getChildAt(i)) {
                is MorganBoard -> {
                    layoutBoard(child, childWidth, childHeight)
                }
                is CardStack -> {
                    layoutCardStack(child, childWidth, childHeight, i)
                }
                is BoardIcon -> {
                    layoutBoardIcon(child, childWidth, childHeight)
                }
                is BoardArrow -> { //arrows
                    layoutBoardArrow(child, childWidth, childHeight)
                }
            }
        }
    }

    private fun layoutBoard(child: View, childWidth: Int, childHeight: Int) {
        child.measure(
            MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST)
        )
        val curWidth = child.measuredWidth
        val curLeft = 0
        val curHeight = child.measuredHeight
        val curTop = 0
        child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight)
    }

    private fun layoutCardStack(child: View, childWidth: Int, childHeight: Int, index: Int) {
        val h = (index - 1) / size
        val w = (index - 1) % size

        val shift =
            if (size == 5) spacing.toInt() else (spacing + cardHeight + boardSpacing / 2).toInt()

        var curLeft = width / 2 + context.dpToPx(-h * dx + w * dx).toInt()
        var curTop = height / 2 + context.dpToPx(h * dy + w * dy).toInt() + shift

        child.measure(
            MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST)
        )
        val curWidth = child.measuredWidth
        curLeft -= curWidth / 2
        val curHeight = child.measuredHeight
        curTop -= curHeight * 5 / 2

        child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight)
    }

    private fun layoutBoardIcon(child: View, childWidth: Int, childHeight: Int) {
        if (child !is BoardIcon) {
            return
        }
        val player = child.getPlayer()
        val sameSquareList =
            playersList.filter { player.position.x == it.position.x && player.position.y == it.position.y }

        val baseCurLeft =
            width / 2 + context.dpToPx(-player.position.y * dx + player.position.x * dx)
                .toInt() - iconWidth.toInt() / 2
        val baseCurTop =
            cardHeight.toInt() + context.dpToPx(player.position.y * dy + player.position.x * dy)
                .toInt() - boardSpacing.toInt()
        val curLeft: Int
        val curTop: Int
        when (sameSquareList.size) {
            1 -> {
                curLeft = baseCurLeft
                curTop = baseCurTop
            }
            2 -> {
                val index = sameSquareList.indexOf(player)
                curLeft = baseCurLeft + (index * 2 - 1) * iconWidth.toInt() / 3
                curTop = baseCurTop
            }
            else -> { // 3 icons, more players will not display
                val index = sameSquareList.indexOf(player)
                if (index < 2) {
                    curLeft = baseCurLeft + (index * 2 - 1) * iconWidth.toInt() / 3
                    curTop = baseCurTop
                } else {
                    curLeft = baseCurLeft
                    curTop = baseCurTop + iconHeight.toInt() / 3
                }
            }
        }

        child.measure(
            MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST)
        )

        val curWidth = iconWidth.toInt()
        val curHeight = iconHeight.toInt()

        child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight)
    }

    private fun layoutBoardArrow(child: View, childWidth: Int, childHeight: Int) {
        if (child !is BoardArrow) {
            return
        }
        val player = currPlayer
        if (player != null && player.isActiveTurn) {
            //todo get info about layers from cardStack's to decide where we can go
            val curLeft: Int
            val curTop: Int
            when (child.getDirection()) {
                BoardArrow.Direction.Up -> {
                    if (player.position.y > 0) {
                        val arrowShift = 0.2f
                        curLeft =
                            width / 2 + context.dpToPx(-(player.position.y - arrowShift) * dx + player.position.x * dx)
                                .toInt()
                        curTop =
                            cardHeight.toInt() + context.dpToPx((player.position.y - arrowShift) * dy + player.position.x * dy)
                                .toInt()
                        child.measure(
                            MeasureSpec.makeMeasureSpec(
                                childWidth,
                                MeasureSpec.AT_MOST
                            ),
                            MeasureSpec.makeMeasureSpec(
                                childHeight,
                                MeasureSpec.AT_MOST
                            )
                        )

                        val curWidth = child.measuredWidth
                        val curHeight = child.measuredHeight

                        child.layout(
                            curLeft,
                            curTop,
                            curLeft + curWidth,
                            curTop + curHeight
                        )
                    }
                }
                BoardArrow.Direction.Down -> {
                    if (player.position.y < size - 1) {
                        val arrowShift = 1.4f
                        curLeft =
                            width / 2 + context.dpToPx(-(player.position.y + arrowShift) * dx + player.position.x * dx)
                                .toInt()
                        curTop =
                            cardHeight.toInt() + context.dpToPx((player.position.y + arrowShift) * dy + player.position.x * dy)
                                .toInt()
                        child.measure(
                            MeasureSpec.makeMeasureSpec(
                                childWidth,
                                MeasureSpec.AT_MOST
                            ),
                            MeasureSpec.makeMeasureSpec(
                                childHeight,
                                MeasureSpec.AT_MOST
                            )
                        )

                        val curWidth = child.measuredWidth
                        val curHeight = child.measuredHeight

                        child.layout(
                            curLeft,
                            curTop,
                            curLeft + curWidth,
                            curTop + curHeight
                        )
                    }
                }
                BoardArrow.Direction.Left -> {
                    if (player.position.x > 0) {
                        val arrowShift = 1.4f
                        curLeft =
                            width / 2 + context.dpToPx(-player.position.y * dx + (player.position.x - arrowShift) * dx)
                                .toInt()
                        curTop =
                            cardHeight.toInt() + context.dpToPx(player.position.y * dy + (player.position.x - 0.2f) * dy)
                                .toInt()
                        child.measure(
                            MeasureSpec.makeMeasureSpec(
                                childWidth,
                                MeasureSpec.AT_MOST
                            ),
                            MeasureSpec.makeMeasureSpec(
                                childHeight,
                                MeasureSpec.AT_MOST
                            )
                        )

                        val curWidth = child.measuredWidth
                        val curHeight = child.measuredHeight

                        child.layout(
                            curLeft,
                            curTop,
                            curLeft + curWidth,
                            curTop + curHeight
                        )
                    }
                }
                BoardArrow.Direction.Right -> {
                    if (player.position.x < size - 1) {
                        curLeft =
                            width / 2 + context.dpToPx(-player.position.y * dx + (player.position.x + 0.2f) * dx)
                                .toInt()
                        curTop =
                            cardHeight.toInt() + context.dpToPx(player.position.y * dy + (player.position.x + 1.3f) * dy)
                                .toInt()
                        child.measure(
                            MeasureSpec.makeMeasureSpec(
                                childWidth,
                                MeasureSpec.AT_MOST
                            ),
                            MeasureSpec.makeMeasureSpec(
                                childHeight,
                                MeasureSpec.AT_MOST
                            )
                        )

                        val curWidth = child.measuredWidth
                        val curHeight = child.measuredHeight

                        child.layout(
                            curLeft,
                            curTop,
                            curLeft + curWidth,
                            curTop + curHeight
                        )
                    }
                }
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

        var width = calculateSize(suggestedMinimumWidth, lp.width, widthMeasureSpec)
        var height = calculateSize(suggestedMinimumHeight, lp.height, heightMeasureSpec)

        width += paddingLeft + paddingRight
        height += paddingTop + paddingBottom

        setMeasuredDimension(width, height)
    }

    private fun calculateSize(
        suggestedSize: Int,
        paramSize: Int,
        measureSpec: Int
    ): Int {
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
        return (cardHeight * (size + 2)).toInt() + (spacing * 2 + boardSpacing * (size)).toInt()
    }

    override fun getSuggestedMinimumWidth(): Int {
        return (cardWidth * (size + 2)).toInt() + (spacing * 3 + boardSpacing * (size + 2)).toInt()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        zoomHelper.onTouchEvent(event)
        return true
    }

}