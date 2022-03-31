package ru.spbstu.common.widgets

import android.content.Context
import android.util.AttributeSet
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

    private var skeletonWidth = 0
    private var skeletonHeight = 0

    private val dx = context.dpToPx(32f)
    private val dy = context.dpToPx(19f)

    init {

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
                        2 -> (measuredWidth - skeletonWidth) / 2 + (width * size + spacing + context.dpToPx(2f)).toInt()
                        4 -> (measuredWidth - skeletonWidth) / 2 - width.toInt() - spacing.toInt()
                        else -> 0
                    }
                    curTop = when (i) {
                        1 -> 0
                        3 -> (height * size + spacing).toInt() + (measuredHeight - skeletonHeight) / 2
                        2, 4 -> (measuredHeight - child.measuredHeight) / 2
                        else -> 0
                    }

                    curWidth = child.measuredWidth
                    curHeight = child.measuredHeight
                    child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight)
                }
                else -> {

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
        return (height * (size + 2)).toInt() + (spacing * (size - 1)).toInt()
    }

    override fun getSuggestedMinimumWidth(): Int {
        return (width * (size + 2)).toInt()
    }
}