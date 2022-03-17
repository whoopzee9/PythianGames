package ru.spbstu.common.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Toast
import ru.spbstu.common.extenstions.dpToPx
import ru.spbstu.common.extenstions.setDebounceClickListener

class Board @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private var size = 5
    private val dx = 32f
    private val dy = 19f

    private var count = 0

    init {
        for (i in 0 until size * size) {
            addView(CardStack(context))
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

        var maxHeight = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val h = i / size
            val w = i % size

            curLeft = width / 2 + context.dpToPx(-h * dx + w * dx).toInt()
            curTop = height / 2 + context.dpToPx(h * dy + w * dy).toInt()

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

            child.setDebounceClickListener {
                Toast.makeText(context, "item :$i", Toast.LENGTH_SHORT).show()
            }

            Log.d("qwerty", "i: $i, h: $h w: $w l: $curLeft t: $curTop r: ${curLeft + curWidth} b: ${curTop + curHeight}")

        }
    }


}