package ru.spbstu.common.widgets

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import ru.spbstu.common.R
import ru.spbstu.common.extenstions.dpToPx
import ru.spbstu.common.extenstions.scale
import kotlin.math.min

class MorganBoard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    var size = 5

    private var height = context.dpToPx(35f)
    private var width = context.dpToPx(58f)
    private var spacing = context.dpToPx(8f)
    private var boardSpacing = context.dpToPx(5.5f)

    private var skeletonWidth = 0
    private var skeletonHeight = 0

    private val dx = context.dpToPx(33f)
    private val dy = context.dpToPx(20f)

    init {
        var attributesArray =
            context.obtainStyledAttributes(attrs, R.styleable.MorganBoard, defStyleAttr, 0)

        size = attributesArray.getInt(
            R.styleable.MorganBoard_size,
            5
        )
        attributesArray.recycle()

        //needed to change MorganBoard size according to main Board size
        attributesArray =
            context.obtainStyledAttributes(attrs, R.styleable.Board, defStyleAttr, 0)

        size = attributesArray.getInt(
            R.styleable.Board_board_size,
            5
        )

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

        for (i in 0 until childCount) {
            val child = getChildAt(i) as ImageView
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
                        2 -> ((r - l) / 2 + width * (size / 2) + spacing + boardSpacing * (size / 2 + 1)).toInt()
                        4 -> ((measuredWidth - skeletonWidth) / 2 - width - spacing - boardSpacing).toInt()
                        else -> 0
                    }
                    curTop = when (i) {
                        1 -> 0
                        3 -> (height * size + spacing * 2 + boardSpacing * (size / 2 + 4 + (size - 3) / 2) ).toInt()
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
                            curLeft = ((r - l) / 2 + boardSpacing + dx * ((j % (size - 2)) + 1)).toInt()
                            curTop = (dy * ((j % (size - 2)) + 2) + boardSpacing / 2 - context.dpToPx(1f)).toInt()
                        }
                        1 -> {
                            curLeft = ((r - l) / 2 + boardSpacing * 3 / 2 + (dx - context.dpToPx(1f)) * ((j % (size - 2)) + 1)).toInt()
                            curTop = ((measuredHeight) / 2 + height / 2 + spacing - boardSpacing / 2 + (dy) * (size - 3 - (j % (size - 2)))).toInt()
                        }
                        2 -> {
                            curLeft = ((r - l) / 2 - curWidth - boardSpacing - dx * ((j % (size - 2)) + 1)).toInt()
                            curTop = ((measuredHeight) / 2 + height / 2 + spacing - boardSpacing / 2 + (dy) * (size - 3 - (j % (size - 2)))).toInt()
                        }
                        3 -> {
                            curLeft = ((r - l) / 2 - curWidth - boardSpacing - dx * ((j % (size - 2)) + 1)).toInt()
                            curTop = (dy * ((j % (size - 2)) + 2) + boardSpacing / 2 - context.dpToPx(1f)).toInt()
                        }
                    }
                    child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight)
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
        return (height * (size + 2)).toInt() + (spacing * 2 + boardSpacing * (size)).toInt()
    }

    override fun getSuggestedMinimumWidth(): Int {
        return (width * (size + 2)).toInt() + (spacing * 3 + boardSpacing * (size + 2)).toInt()
    }
}