package ru.spbstu.common.widgets

import android.content.Context
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import androidx.core.content.ContextCompat
import ru.spbstu.common.R
import ru.spbstu.common.extenstions.dpToPx
import ru.spbstu.common.extenstions.scale
import ru.spbstu.common.extenstions.setPivot
import ru.spbstu.common.model.Player
import ru.spbstu.common.model.PlayerBoard
import ru.spbstu.common.model.Team
import kotlin.math.min

class Board @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private var size = 5
    private var totalPlayers = 8
    private var totalTeams = 4

    private val dx = 32f
    private val dy = 19f

    private var cardHeight = context.dpToPx(35f)
    private var cardWidth = context.dpToPx(58f)
    private var spacing = context.dpToPx(8f)
    private var boardSpacing = context.dpToPx(5.5f)

    private var iconHeight = context.dpToPx(32f)
    private var iconWidth = context.dpToPx(32f)
    private var strokeWidth = context.dpToPx(3f)
    private var activeTurnStrokeWidth = context.dpToPx(1f)
    private var characterBackground: GradientDrawable

    private val playersList: MutableList<PlayerBoard> = mutableListOf()

    private var currPlayer: PlayerBoard? = null

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

        val board = MorganBoard(context, attrs, defStyleAttr)
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

        attributesArray.recycle()
    }

    fun setTotalPlayers(players: Int) {
        totalPlayers = players
    }

    fun setTotalTeams(teams: Int) {
        totalTeams = teams
    }

    fun addPlayer(player: Player) {
        val playerBoard = PlayerBoard(player, 1, 1)
        determineStartPositions(playerBoard)
        playersList.add(playerBoard)
        val icon = BoardIcon(context)
        icon.setPlayer(playerBoard)
        addView(icon)
        requestLayout()
    }

    fun setCurrentPlayer(playerId: Long) {
        currPlayer = playersList.first { it.player.id == playerId }
    }

    fun getCurrentPlayer() = currPlayer

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var curWidth: Int
        var curHeight: Int
        var curLeft: Int
        var curTop: Int

        //get the available size of child view

        val childLeft = paddingLeft
        val childTop = paddingTop
        val childRight = measuredWidth - paddingRight
        val childBottom = measuredHeight - paddingBottom
        val childWidth = childRight - childLeft
        val childHeight = childBottom - childTop

        var maxHeight = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            when (child) {
                is MorganBoard -> {
                    child.measure(
                        MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                        MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST)
                    )
                    curWidth = child.measuredWidth
                    curLeft = 0
                    curHeight = child.measuredHeight
                    curTop = 0
                    child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight)
                }
                is CardStack -> {
                    val h = (i - 1) / size
                    val w = (i - 1) % size

                    val shift =
                        if (size == 5) spacing.toInt() else (spacing + cardHeight + boardSpacing / 2).toInt()

                    curLeft = width / 2 + context.dpToPx(-h * dx + w * dx).toInt()
                    curTop = height / 2 + context.dpToPx(h * dy + w * dy).toInt() + shift


                    child.measure(
                        MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                        MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST)
                    )
                    curWidth = child.measuredWidth
                    curLeft -= curWidth / 2
                    curHeight = child.measuredHeight
                    curTop -= curHeight * 5 / 2
                    if (curLeft + curWidth >= childRight) {
                        curLeft = childLeft
                        curTop += maxHeight
                        maxHeight = 0
                    }

                    child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight)

                }
                is BoardIcon -> {
                    val player = child.getPlayer()
                    val sameSquareList = playersList.filter { player.x == it.x && player.y == it.y }

                    val baseCurLeft = width / 2 + context.dpToPx(-player.y * dx + player.x * dx)
                        .toInt() - iconWidth.toInt() / 2
                    val baseCurTop =
                        cardHeight.toInt() + context.dpToPx(player.y * dy + player.x * dy)
                            .toInt() - boardSpacing.toInt()
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
                        else -> { // 3 icons
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

                    curWidth = iconWidth.toInt()
                    curHeight = iconHeight.toInt()

                    child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight)
                }
                is BoardArrow -> { //arrows
                    val player = currPlayer
                    if (player != null && player.player.isActiveTurn) {
                        //todo get info about layers from cardStack's to decide where we can go
                        when (child.getDirection()) {
                            BoardArrow.Direction.Up -> {
                                if (player.y > 0) {
                                    val arrowShift = 0.2f
                                    curLeft =
                                        width / 2 + context.dpToPx(-(player.y - arrowShift) * dx + player.x * dx)
                                            .toInt()
                                    curTop =
                                        cardHeight.toInt() + context.dpToPx((player.y - arrowShift) * dy + player.x * dy)
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

                                    curWidth = child.measuredWidth
                                    curHeight = child.measuredHeight

                                    child.layout(
                                        curLeft,
                                        curTop,
                                        curLeft + curWidth,
                                        curTop + curHeight
                                    )
                                }
                            }
                            BoardArrow.Direction.Down -> {
                                if (player.y < size - 1) {
                                    val arrowShift = 1.4f
                                    curLeft =
                                        width / 2 + context.dpToPx(-(player.y + arrowShift) * dx + player.x * dx)
                                            .toInt()
                                    curTop =
                                        cardHeight.toInt() + context.dpToPx((player.y + arrowShift) * dy + player.x * dy)
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

                                    curWidth = child.measuredWidth
                                    curHeight = child.measuredHeight

                                    child.layout(
                                        curLeft,
                                        curTop,
                                        curLeft + curWidth,
                                        curTop + curHeight
                                    )
                                }
                            }
                            BoardArrow.Direction.Left -> {
                                if (player.x > 0) {
                                    val arrowShift = 1.4f
                                    curLeft =
                                        width / 2 + context.dpToPx(-player.y * dx + (player.x - arrowShift) * dx)
                                            .toInt()
                                    curTop =
                                        cardHeight.toInt() + context.dpToPx(player.y * dy + (player.x - 0.2f) * dy)
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

                                    curWidth = child.measuredWidth
                                    curHeight = child.measuredHeight

                                    child.layout(
                                        curLeft,
                                        curTop,
                                        curLeft + curWidth,
                                        curTop + curHeight
                                    )
                                }
                            }
                            BoardArrow.Direction.Right -> {
                                if (player.x < size - 1) {
                                    curLeft =
                                        width / 2 + context.dpToPx(-player.y * dx + (player.x + 0.2f) * dx)
                                            .toInt()
                                    curTop =
                                        cardHeight.toInt() + context.dpToPx(player.y * dy + (player.x + 1.3f) * dy)
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

                                    curWidth = child.measuredWidth
                                    curHeight = child.measuredHeight

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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        scaleGestureDetector.onTouchEvent(event)
        translationHandler.onTouch(this, event)
        return true
    }

    private fun determineStartPositions(playerBoard: PlayerBoard) {
        when (totalPlayers) {
            2 -> {
                when (playerBoard.player.team) {
                    Team.Green -> {
                        playerBoard.x = 0
                        playerBoard.y = 0
                    }
                    Team.Blue -> {
                        playerBoard.x = size - 1
                        playerBoard.y = size - 1
                    }
                    else -> {}
                }
            }
            4 -> {
                when (totalTeams) {
                    2 -> {
                        when (playerBoard.player.team) {
                            Team.Green -> {
                                when (playerBoard.player.playerNum) {
                                    1 -> {
                                        playerBoard.x = 0
                                        playerBoard.y = 0
                                    }
                                    2 -> {
                                        playerBoard.x = size - 1
                                        playerBoard.y = size - 1
                                    }
                                }
                            }
                            Team.Blue -> {
                                when (playerBoard.player.playerNum) {
                                    1 -> {
                                        playerBoard.x = size - 1
                                        playerBoard.y = 0
                                    }
                                    2 -> {
                                        playerBoard.x = 0
                                        playerBoard.y = size - 1
                                    }
                                }
                            }
                        }
                    }
                    4 -> {
                        when (playerBoard.player.team) {
                            Team.Green -> {
                                playerBoard.x = 0
                                playerBoard.y = 0
                            }
                            Team.Blue -> {
                                playerBoard.x = size - 1
                                playerBoard.y = size - 1
                            }
                            Team.Orange -> {
                                playerBoard.x = size - 1
                                playerBoard.y = 0
                            }
                            Team.Red -> {
                                playerBoard.x = 0
                                playerBoard.y = size - 1
                            }
                        }
                    }
                }
            }
            6 -> {
                when (playerBoard.player.team) {
                    Team.Green -> {
                        when (playerBoard.player.playerNum) {
                            1 -> {
                                playerBoard.x = 0
                                playerBoard.y = 0
                            }
                            2 -> {
                                playerBoard.x = size - 1
                                playerBoard.y = size - 1
                            }
                        }
                    }
                    Team.Blue -> {
                        when (playerBoard.player.playerNum) {
                            1 -> {
                                playerBoard.x = size - 1
                                playerBoard.y = 0
                            }
                            2 -> {
                                playerBoard.x = 0
                                playerBoard.y = size - 1
                            }
                        }
                    }
                    Team.Red -> {
                        when (playerBoard.player.playerNum) {
                            1 -> {
                                playerBoard.x = 0
                                playerBoard.y = size / 2
                            }
                            2 -> {
                                playerBoard.x = size - 1
                                playerBoard.y = size / 2
                            }
                        }
                    }
                }
            }
            8 -> {
                when (totalTeams) {
                    2 -> {
                        when (playerBoard.player.team) {
                            Team.Green -> {
                                when (playerBoard.player.playerNum) {
                                    1 -> {
                                        playerBoard.x = 0
                                        playerBoard.y = 0
                                    }
                                    2 -> {
                                        playerBoard.x = size - 1
                                        playerBoard.y = size - 1
                                    }
                                    3 -> {
                                        playerBoard.x = size - 1
                                        playerBoard.y = 0
                                    }
                                    4 -> {
                                        playerBoard.x = 0
                                        playerBoard.y = size - 1
                                    }
                                }
                            }
                            Team.Blue -> {
                                when (playerBoard.player.playerNum) {
                                    1 -> {
                                        playerBoard.x = size / 2
                                        playerBoard.y = 0
                                    }
                                    2 -> {
                                        playerBoard.x = size / 2
                                        playerBoard.y = size - 1
                                    }
                                    3 -> {
                                        playerBoard.x = 0
                                        playerBoard.y = size / 2
                                    }
                                    4 -> {
                                        playerBoard.x = size - 1
                                        playerBoard.y = size / 2
                                    }
                                }
                            }
                        }
                    }
                    4 -> {
                        when (playerBoard.player.team) {
                            Team.Green -> {
                                when (playerBoard.player.playerNum) {
                                    1 -> {
                                        playerBoard.x = 0
                                        playerBoard.y = 0
                                    }
                                    2 -> {
                                        playerBoard.x = size - 1
                                        playerBoard.y = size - 1
                                    }
                                }
                            }
                            Team.Blue -> {
                                when (playerBoard.player.playerNum) {
                                    1 -> {
                                        playerBoard.x = size - 1
                                        playerBoard.y = 0
                                    }
                                    2 -> {
                                        playerBoard.x = 0
                                        playerBoard.y = size - 1
                                    }
                                }
                            }
                            Team.Orange -> {
                                when (playerBoard.player.playerNum) {
                                    1 -> {
                                        playerBoard.x = size / 2
                                        playerBoard.y = 0
                                    }
                                    2 -> {
                                        playerBoard.x = size / 2
                                        playerBoard.y = size - 1
                                    }
                                }
                            }
                            Team.Red -> {
                                when (playerBoard.player.playerNum) {
                                    1 -> {
                                        playerBoard.x = 0
                                        playerBoard.y = size / 2
                                    }
                                    2 -> {
                                        playerBoard.x = size - 1
                                        playerBoard.y = size / 2
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private val originContentRect by lazy {
        run {
            val array = IntArray(2)
            getLocationOnScreen(array)
            Rect(array[0], array[1], array[0] + width, array[1] + height)
        }
    }

    private val translationHandler by lazy {
        object : OnTouchListener {
            private var prevX = 0f
            private var prevY = 0f
            private var moveStarted = false
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event == null || scaleX == 1f) return false

                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        prevX = event.x
                        prevY = event.y
                    }

                    MotionEvent.ACTION_POINTER_UP -> {
                        if (event.actionIndex == 0) {
                            try {
                                prevX = event.getX(1)
                                prevY = event.getY(1)
                            } catch (e: Exception) {
                            }
                        }
                    }

                    MotionEvent.ACTION_MOVE -> {
                        if (event.pointerCount > 1) {
                            prevX = event.x
                            prevY = event.y
                            return false
                        }
                        moveStarted = true
                        run {
                            translationX += (event.x - prevX)
                            translationY += (event.y - prevY)
                            checkTranslation()
                        }
                        prevX = event.x
                        prevY = event.y
                    }

                    MotionEvent.ACTION_UP -> {
                        if (!moveStarted) return false
                        reset()
                        //translateToOriginalRect()
                    }
                }
                return true
            }

            private fun reset() {
                prevX = 0f
                prevY = 0f
                moveStarted = false
            }
        }
    }

    private fun checkTranslation() {
        if (translationX > MAX_TRANSLATION_X) {
            translationX = MAX_TRANSLATION_X
        }
        if (translationX < MIN_TRANSLATION_X) {
            translationX = MIN_TRANSLATION_X
        }
        if (translationY > MAX_TRANSLATION_Y) {
            translationY = MAX_TRANSLATION_Y
        }
        if (translationY < MIN_TRANSLATION_Y) {
            translationY = MIN_TRANSLATION_Y
        }
    }

    private val scaleGestureDetector by lazy {
        ScaleGestureDetector(
            context,
            object : ScaleGestureDetector.OnScaleGestureListener {
                var totalScale = 1f

                override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                    run {
                        val actualPivot = PointF(
                            (detector.focusX - translationX + pivotX * (totalScale - 1)) / totalScale,
                            (detector.focusY - translationY + pivotY * (totalScale - 1)) / totalScale,
                        )

                        translationX -= (pivotX - actualPivot.x) * (totalScale - 1)
                        translationY -= (pivotY - actualPivot.y) * (totalScale - 1)
                        checkTranslation()
                        setPivot(actualPivot)
                    }
                    return true
                }

                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val prev = totalScale
                    totalScale *= detector.scaleFactor
                    totalScale = totalScale.coerceIn(MIN_SCALE_FACTOR, MAX_SCALE_FACTOR)
                    //Log.d("qwerty", "onScale")
                    run {
                        val scaleAnimation = ScaleAnimation(
                            prev,
                            totalScale,
                            prev,
                            totalScale,
                            detector.focusX,
                            detector.focusY
                        )
                        scaleAnimation.duration = 0
                        scaleAnimation.fillAfter = true
                        startAnimation(scaleAnimation)
                        scale(totalScale)
                        invalidate()
                        getContentViewTranslation().run {
                            translationX += x
                            translationY += y
                            checkTranslation()
                        }
                    }
                    return true
                }

                override fun onScaleEnd(detector: ScaleGestureDetector) = Unit
            })
    }

    private fun getContentViewTranslation(): PointF {
        return run {
            originContentRect.let { rect ->
                val array = IntArray(2)
                getLocationOnScreen(array)
                PointF(
                    when {
                        array[0] > rect.left -> rect.left - array[0].toFloat()
                        array[0] + width * scaleX < rect.right -> rect.right - (array[0] + width * scaleX)
                        else -> 0f
                    },
                    when {
                        array[1] > rect.top -> rect.top - array[1].toFloat()
                        array[1] + height * scaleY < rect.bottom -> rect.bottom - (array[1] + height * scaleY)
                        else -> 0f
                    }
                )
            }
        }
    }

    companion object {
        const val MAX_SCALE_FACTOR = 1.2f
        const val MIN_SCALE_FACTOR = 1f
        const val MAX_TRANSLATION_X = 190f
        const val MIN_TRANSLATION_X = -190f
        const val MAX_TRANSLATION_Y = 50f
        const val MIN_TRANSLATION_Y = -50f
    }
}