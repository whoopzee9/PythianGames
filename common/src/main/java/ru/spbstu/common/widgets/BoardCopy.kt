package ru.spbstu.common.widgets

import android.content.Context
import android.graphics.PointF
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import ru.spbstu.common.R
import ru.spbstu.common.extenstions.dpToPx
import ru.spbstu.common.extenstions.scale
import ru.spbstu.common.extenstions.setPivot
import kotlin.math.min

class BoardCopy @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private var size = 5
    private val dx = 32f
    private val dy = 19f
    private val boardDx = context.dpToPx(33f)
    private val boardDy = context.dpToPx(20f)

    private var cardHeight = context.dpToPx(35f)
    private var cardWidth = context.dpToPx(58f)
    private var spacing = context.dpToPx(8f)
    private var boardSpacing = context.dpToPx(5.5f)
    private var skeletonWidth = 0
    private var skeletonHeight = 0

    init {
        val attributesArray =
            context.obtainStyledAttributes(attrs, R.styleable.Board, defStyleAttr, 0)

        size = attributesArray.getInt(
            R.styleable.Board_board_size,
            5
        )

//        val board = MorganBoard(context, attrs, defStyleAttr)
//        board.size = size
//        addView(board)

        val skeleton = ImageView(context)
        skeleton.setImageResource(R.drawable.ic_skeleton_big)
        if (size == 3) {
            skeleton.setImageResource(R.drawable.ic_skeleton_small)
        }
        addView(skeleton)

        for (i in 0 until 4) {
            val corner = ImageView(context)
            corner.setImageResource(if (i % 2 == 0) R.drawable.ic_top_corner_blank_55 else R.drawable.ic_side_corner)
            addView(corner)
        }

        for (i in 0 until (4 * (size - 2))) {
            val square = ImageView(context)
            square.setImageResource(R.drawable.ic_blank_layer_36)
            addView(square)
        }

        for (i in 0 until size * size) {
            addView(CardStack(context))
        }

        attributesArray.recycle()
    }

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
            if (child is ImageView) {
                when (i) {
                    0 -> {
                        child.measure(
                            MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                            MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST)
                        )
                        skeletonWidth = child.measuredWidth
                        skeletonHeight = child.measuredHeight
                        curLeft = (measuredWidth - child.measuredWidth) / 2
                        curTop = (measuredHeight - child.measuredHeight) / 2
                        curWidth = child.measuredWidth
                        curHeight = child.measuredHeight

                        child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight)
                    }
                    1, 2, 3, 4 -> {
                        child.measure(
                            MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                            MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST)
                        )
                        val angle = when (i) {
                            1, 2 -> 0f
                            3, 4 -> 180f
                            else -> 0f
                        }
                        child.rotation = angle
                        curLeft = when (i) {
                            1, 3 -> (measuredWidth - child.measuredWidth) / 2
                            2 -> ((r - l) / 2 + cardWidth * (size / 2) + spacing + boardSpacing * (size / 2 + 1)).toInt()
                            4 -> ((measuredWidth - skeletonWidth) / 2 - cardWidth - spacing - boardSpacing).toInt()
                            else -> 0
                        }
                        curTop = when (i) {
                            1 -> 0
                            3 -> (cardHeight * size + spacing * 2 + boardSpacing * (size / 2 + 4 + (size - 3) / 2) ).toInt()
                            2, 4 -> (measuredHeight - child.measuredHeight) / 2
                            else -> 0
                        }

                        curWidth = child.measuredWidth
                        curHeight = child.measuredHeight
                        child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight)
                    }
                    else -> {
                        val j = i - 5
                        child.measure(
                            MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                            MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST)
                        )
                        curLeft = 0
                        curTop = 0
                        curWidth = child.measuredWidth
                        curHeight = child.measuredHeight
                        when (j / (size - 2)) {
                            0 -> {
                                curLeft = ((r - l) / 2 + boardSpacing + boardDx * ((j % (size - 2)) + 1)).toInt()
                                curTop = (boardDy * ((j % (size - 2)) + 2) + boardSpacing / 2 - context.dpToPx(1f)).toInt()
                            }
                            1 -> {
                                curLeft = ((r - l) / 2 + boardSpacing * 3 / 2 + (boardDx - context.dpToPx(1f)) * ((j % (size - 2)) + 1)).toInt()
                                curTop = ((measuredHeight) / 2 + cardHeight / 2 + spacing - boardSpacing / 2 + boardDy * (size - 3 - (j % (size - 2)))).toInt()
                            }
                            2 -> {
                                curLeft = ((r - l) / 2 - curWidth - boardSpacing - boardDx * ((j % (size - 2)) + 1)).toInt()
                                curTop = ((measuredHeight) / 2 + cardHeight / 2 + spacing - boardSpacing / 2 + boardDy * (size - 3 - (j % (size - 2)))).toInt()
                            }
                            3 -> {
                                curLeft = ((r - l) / 2 - curWidth - boardSpacing - boardDx * ((j % (size - 2)) + 1)).toInt()
                                curTop = (boardDy * ((j % (size - 2)) + 2) + boardSpacing / 2 - context.dpToPx(1f)).toInt()
                            }
                        }
                        child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight)
                    }
                }
            } else {
                val h = (i - 5 - 4 * (size - 2)) / size
                val w = (i - 5 - 4 * (size - 2)) % size

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

//            child.setOnTouchListener { v, event ->
//                //scaleGestureDetector.onTouchEvent(event)
//                translationHandler.onTouch(v, event)
//                false
//            }
//

//            child.setDebounceClickListener {
//                Log.d("qwerty", "click")
//                (child as CardStack).count--
//                child.invalidate()
//                false
//            }

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

    private val originContentRect by lazy {
        run {
            val array = IntArray(2)
            getLocationOnScreen(array)
            Rect(array[0], array[1], array[0] + width, array[1] + height)
        }
    }

    private val translationHandler by lazy {
        object : View.OnTouchListener {
            private var prevX = 0f
            private var prevY = 0f
            private var moveStarted = false
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event == null || (scaleX ?: 1f) == 1f) return false

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
        const val MAX_TRANSLATION_X = 300f
        const val MIN_TRANSLATION_X = -300f
        const val MAX_TRANSLATION_Y = 100f
        const val MIN_TRANSLATION_Y = -100f
        private const val CORRECT_LOCATION_ANIMATION_DURATION = 300L
    }
}