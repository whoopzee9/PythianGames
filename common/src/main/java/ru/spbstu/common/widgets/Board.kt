package ru.spbstu.common.widgets

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.children
import ru.spbstu.common.R
import ru.spbstu.common.extenstions.dpToPx
import ru.spbstu.common.model.MovingPossibilities
import ru.spbstu.common.model.Player
import ru.spbstu.common.model.Position
import ru.spbstu.common.utils.ZoomHelper
import kotlin.math.min

class Board @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private var size = 5

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

    private var morganPosition: Int = 0
    private var isMorganSelectingSide: Boolean = false
    private var morganSelectedSide: Int = 0

    private val playersList: HashMap<String, Player> = hashMapOf()

    private var currPlayer: Player? = null
    private var activeTurnPlayer: Player? = null

    private val zoomHelper = ZoomHelper(this)

    private var selectedMovingDirection: BoardArrow.Direction? = null

    private var arrowCallback: ((
        BoardArrow.Direction,
        MovingPossibilities
    ) -> Unit)? = null

    private var canDigInCurrentPosition = MovingPossibilities()
    private var canDigUp = MovingPossibilities()
    private var canDigDown = MovingPossibilities()
    private var canDigLeft = MovingPossibilities()
    private var canDigRight = MovingPossibilities()

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
            selectedMovingDirection = BoardArrow.Direction.Up
            arrowCallback?.invoke(BoardArrow.Direction.Up, canDigUp)
            requestLayout()
        }
        arrowDown.setOnClickCallback {
            arrowUp.resetHighlighted()
            arrowLeft.resetHighlighted()
            arrowRight.resetHighlighted()
            selectedMovingDirection = BoardArrow.Direction.Down
            arrowCallback?.invoke(BoardArrow.Direction.Down, canDigDown)
            requestLayout()
        }
        arrowLeft.setOnClickCallback {
            arrowUp.resetHighlighted()
            arrowDown.resetHighlighted()
            arrowRight.resetHighlighted()
            selectedMovingDirection = BoardArrow.Direction.Left
            arrowCallback?.invoke(BoardArrow.Direction.Left, canDigLeft)
            requestLayout()
        }
        arrowRight.setOnClickCallback {
            arrowUp.resetHighlighted()
            arrowDown.resetHighlighted()
            arrowLeft.resetHighlighted()
            selectedMovingDirection = BoardArrow.Direction.Right
            arrowCallback?.invoke(BoardArrow.Direction.Right, canDigRight)
            requestLayout()
        }
        addView(arrowUp)
        addView(arrowDown)
        addView(arrowLeft)
        addView(arrowRight)
    }

    fun addPlayer(player: Player) {
        playersList[player.id] = player
        val icon = BoardIcon(context)
        icon.setPlayer(player)
        addView(icon)
        requestLayout()
    }

    fun updatePlayer(player: Player) {
        if (playersList[player.id] != player) {
            playersList[player.id] = player
            requestLayout()
        }
    }

    fun updatePlayers(players: HashMap<String, Player>) {
        if (playersList != players) {
            players.forEach {
                playersList[it.key] = it.value
            }
            requestLayout()
        }
    }

    fun setCurrentPlayer(playerId: String) {
        if (currPlayer != playersList[playerId]) {
            currPlayer = playersList[playerId]
            requestLayout()
        }
    }

    fun getCurrentPlayer() = currPlayer

    fun setActiveTurnPlayer(playerId: String) {
        if (activeTurnPlayer != playersList[playerId]) {
            activeTurnPlayer = playersList[playerId]
            Log.d("qwerty", "active turn $activeTurnPlayer")
            invalidate()
            requestLayout()
        }
    }

    fun getActiveTurnPlayer() = activeTurnPlayer

    fun getPlayersAmount() = playersList.size

    fun setSize(size: Int) {
        this.size = size
        removeAllViews()
        initView()
        invalidate()
    }

    fun getSize() = size

    fun setMorganPosition(position: Int) {
        if (morganPosition != position) {
            morganPosition = position
            requestLayout()
        }
    }

    fun setMorganSelectingSide(isSelecting: Boolean) {
        if (isMorganSelectingSide != isSelecting) {
            isMorganSelectingSide = isSelecting
            requestLayout()
        }
    }

    fun setMorganSelectedSide(side: Int) {
        if (morganSelectedSide != side) {
            morganSelectedSide = side
            morganPosition = (side - 1) * size
            requestLayout()
        }
    }

    fun clearSelectedMovingDirection() {
        selectedMovingDirection = null
    }

    fun getSelectedMovingDirection() = selectedMovingDirection

    fun setArrowCallback(
        callback: (
            BoardArrow.Direction,
            MovingPossibilities
        ) -> Unit
    ) {
        arrowCallback = callback
    }

    fun clearArrowCallback() {
        arrowCallback = null
    }

    fun determineDiggingAvailability() {
        currPlayer = playersList[currPlayer?.id]
        if (currPlayer != null) {
            canDigInCurrentPosition = canDigInPosition(currPlayer!!.position)
            canDigUp =
                canDigInPosition(Position(currPlayer!!.position.x, currPlayer!!.position.y - 1))
            canDigDown =
                canDigInPosition(Position(currPlayer!!.position.x, currPlayer!!.position.y + 1))
            canDigLeft =
                canDigInPosition(Position(currPlayer!!.position.x - 1, currPlayer!!.position.y))
            canDigRight =
                canDigInPosition(Position(currPlayer!!.position.x + 1, currPlayer!!.position.y))
        }
    }

    fun canDigInCurrentPosition() = canDigInCurrentPosition
    fun canDigUp() = canDigUp
    fun canDigDown() = canDigDown
    fun canDigLeft() = canDigLeft
    fun canDigRight() = canDigRight

    fun canAskMorganInPosition(position: Position): Boolean {
        val morganPos = when {
            morganPosition < size -> {
                Position(x = morganPosition, y = 0)
            }
            morganPosition >= size && morganPosition <= (size - 1) * 2 -> {
                Position(x = size - 1, y = morganPosition - size + 1)
            }
            morganPosition > (size - 1) * 2 && morganPosition <= (size - 1) * 3 -> {
                Position(x = (size - 1) * 3 - morganPosition, y = size - 1)
            }
            morganPosition > (size - 1) * 3 -> {
                Position(x = 0, y = (size - 1) * 4 - morganPosition)
            }
            else -> {
                Position()
            }
        }
        return position.x == morganPos.x || position.y == morganPos.y
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
                    child.setMorganPosition(morganPosition)
                    child.setMorganSelectingSide(isMorganSelectingSide)
                    child.setSelectedSide(morganSelectedSide)
                    layoutBoard(child, childWidth, childHeight)
                }
                is CardStack -> {
                    val cardPos = i - 1
                    child.setPosition(cardPos % size, cardPos / size)
                    layoutCardStack(child, childWidth, childHeight, i)
                }
                is BoardIcon -> {
                    child.setActivePlayerId(activeTurnPlayer?.id ?: "")
                    layoutBoardIcon(child, childWidth, childHeight)
                }
                is BoardArrow -> { //arrows
                    layoutBoardArrow(child, childWidth, childHeight)
                }
            }
        }
    }

    private fun canDigInPosition(position: Position): MovingPossibilities {
        val stacks = children.filter { it is CardStack }
        val currStack =
            stacks.firstOrNull { (it as CardStack).getPosition() == position } as? CardStack
        if (currStack != null) {
            if (currStack.getStackCount() == 0) {
                return MovingPossibilities(canDig = false, isCleaning = false)
            }
            val topStack = stacks.firstOrNull {
                (it as CardStack).getPosition().x == position.x &&
                        it.getPosition().y == position.y - 1
            } as? CardStack
            val bottomStack = stacks.firstOrNull {
                (it as CardStack).getPosition().x == position.x &&
                        it.getPosition().y == position.y + 1
            } as? CardStack
            val leftStack = stacks.firstOrNull {
                (it as CardStack).getPosition().x == position.x - 1 &&
                        it.getPosition().y == position.y
            } as? CardStack
            val rightStack = stacks.firstOrNull {
                (it as CardStack).getPosition().x == position.x + 1 &&
                        it.getPosition().y == position.y
            } as? CardStack

            if (topStack != null && topStack.getCurrentLayer() < currStack.getCurrentLayer()) {
                return MovingPossibilities(canDig = false, isCleaning = false)
            }
            if (bottomStack != null && bottomStack.getCurrentLayer() < currStack.getCurrentLayer()) {
                return MovingPossibilities(canDig = false, isCleaning = false)
            }
            if (leftStack != null && leftStack.getCurrentLayer() < currStack.getCurrentLayer()) {
                return MovingPossibilities(canDig = false, isCleaning = false)
            }
            if (rightStack != null && rightStack.getCurrentLayer() < currStack.getCurrentLayer()) {
                return MovingPossibilities(canDig = false, isCleaning = false)
            }
            return MovingPossibilities(
                canDig = true,
                isCleaning = currStack.isCleaning(),
                canBet = currStack.getCurrentLayer() > 1,
                position = currStack.getPosition(),
                layer = currStack.getCurrentLayer()
            )
        }
        return MovingPossibilities(canDig = false, isCleaning = false)
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
        val playerID = child.getPlayer().id
        val player = playersList[playerID] ?: child.getPlayer()
        val sameSquareList =
            playersList.filter { player.position.x == it.value.position.x && player.position.y == it.value.position.y }

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
                val index = sameSquareList.values.indexOf(player)
                curLeft = baseCurLeft + (index * 2 - 1) * iconWidth.toInt() / 3
                curTop = baseCurTop
            }
            else -> { // 3 icons, more players will not display
                val values = sameSquareList.values.toMutableList()
                if (player.id == activeTurnPlayer?.id) {
                    val last = values.last()
                    val curr = values.first { it.id == player.id }
                    val currIndex = values.indexOf(player)
                    values[currIndex] = last
                    values[values.size - 1] = curr
                }
                val index = values.indexOf(player)
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
        if (child.getDirection() != selectedMovingDirection) {
            child.resetHighlighted()
        }
        if (player != null && player.id == activeTurnPlayer?.id) {
            val curLeft: Int
            val curTop: Int
            child.visibility = View.VISIBLE
            when (child.getDirection()) {
                BoardArrow.Direction.Up -> {
                    if (player.position.y > 0) {
                        val arrowShift = 0.3f
                        curLeft =
                            width / 2 + context.dpToPx(-(player.position.y - arrowShift + 0.2f) * dx + player.position.x * dx)
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

                        child.visibility = View.VISIBLE
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
                        val arrowShift = 1.2f
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
                        val arrowShift = 1.3f
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
                            width / 2 + context.dpToPx(-player.position.y * dx + (player.position.x + 0.0f) * dx)
                                .toInt()
                        curTop =
                            cardHeight.toInt() + context.dpToPx(player.position.y * dy + (player.position.x + 1.1f) * dy)
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
        } else {
            child.visibility = View.GONE
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