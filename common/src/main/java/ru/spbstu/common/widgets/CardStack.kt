package ru.spbstu.common.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import ru.spbstu.common.R
import ru.spbstu.common.extenstions.dpToPx
import ru.spbstu.common.extenstions.scale
import ru.spbstu.common.extenstions.setPivot
import ru.spbstu.common.extenstions.toBitmap
import kotlin.math.max
import kotlin.math.min


class CardStack @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var firstLayer: Drawable?
    private var secondLayer: Drawable?
    private var thirdLayer: Drawable?
    private var fourthLayer: Drawable?
    private var fifthLayer: Drawable?

    private var height: Float
    private var width: Float
    private var spacing: Float

    var count = 5

    init {
        firstLayer = ContextCompat.getDrawable(context, R.drawable.ic_first_layer_36)
        secondLayer = ContextCompat.getDrawable(context, R.drawable.ic_second_layer_36)
        thirdLayer = ContextCompat.getDrawable(context, R.drawable.ic_third_layer_36)
        fourthLayer = ContextCompat.getDrawable(context, R.drawable.ic_fourth_layer_36)
        fifthLayer = ContextCompat.getDrawable(context, R.drawable.ic_fifth_layer_36)

        height = context.dpToPx(35f)
        width = context.dpToPx(58f)
        spacing = context.dpToPx(2f)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var lp: ViewGroup.LayoutParams? = layoutParams
        if (lp == null)
            lp = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
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
                    ViewGroup.LayoutParams.WRAP_CONTENT -> min(suggestedSize, size)
                    ViewGroup.LayoutParams.MATCH_PARENT -> size
                    else -> min(paramSize, size)
                }
            MeasureSpec.EXACTLY -> result = size
            MeasureSpec.UNSPECIFIED ->
                result =
                    if (paramSize == ViewGroup.LayoutParams.WRAP_CONTENT ||
                        paramSize == ViewGroup.LayoutParams.MATCH_PARENT
                    )
                        suggestedSize
                    else {
                        paramSize
                    }
        }

        return result
    }

    override fun getSuggestedMinimumHeight(): Int {
        return height.toInt() + spacing.toInt() * 5
    }

    override fun getSuggestedMinimumWidth(): Int {
        return width.toInt()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        when (count) {
            5 -> {
                if (canvas != null) {
                    canvas.translate(0f, spacing * 4)
                    fifthLayer?.draw(canvas)
                    canvas.translate(0f, -spacing)
                    fourthLayer?.draw(canvas)
                    canvas.translate(0f, -spacing)
                    thirdLayer?.draw(canvas)
                    canvas.translate(0f, -spacing)
                    secondLayer?.draw(canvas)
                    canvas.translate(0f, -spacing)
                    firstLayer?.draw(canvas)
                }
            }
            4 -> {
                if (canvas != null) {
                    canvas.translate(0f, spacing * 4)
                    fifthLayer?.draw(canvas)
                    canvas.translate(0f, -spacing)
                    fourthLayer?.draw(canvas)
                    canvas.translate(0f, -spacing)
                    thirdLayer?.draw(canvas)
                    canvas.translate(0f, -spacing)
                    secondLayer?.draw(canvas)
                    canvas.translate(0f, spacing * (-1))
                }
            }
            3 -> {
                if (canvas != null) {
                    canvas.translate(0f, spacing * 4)
                    fifthLayer?.draw(canvas)
                    canvas.translate(0f, -spacing)
                    fourthLayer?.draw(canvas)
                    canvas.translate(0f, -spacing)
                    thirdLayer?.draw(canvas)
                    canvas.translate(0f, spacing * (-2))
                }
            }
            2 -> {
                if (canvas != null) {
                    canvas.translate(0f, spacing * 4)
                    fifthLayer?.draw(canvas)
                    canvas.translate(0f, -spacing)
                    fourthLayer?.draw(canvas)
                    canvas.translate(0f, spacing * (-3))
                }
            }
            1 -> {
                if (canvas != null) {
                    canvas.translate(0f, spacing * 4)
                    fifthLayer?.draw(canvas)
                    canvas.translate(0f, spacing * (-4))
                }
            }
            else -> {

            }
        }
    }

    private var moveStarted = false


//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        super.onTouchEvent(event)
//        when (event.actionMasked) {
//            MotionEvent.ACTION_MOVE -> {
//                Log.d("qwerty", "move")
//                moveStarted = true
//            }
//            MotionEvent.ACTION_DOWN -> {
//                Log.d("qwerty", "down")
//                return false
//            }
//            MotionEvent.ACTION_UP -> {
//                Log.d("qwerty", "up")
//                Log.d("qwerty", "moveStarted: $moveStarted")
//                if (!moveStarted) {
//                    Log.d("qwerty", "before perform click")
//                    performClick()
//                    return false
//                }
//                moveStarted = false
//            }
//        }
//        //Log.d("qwerty", "onTouchEvent super val: ${super.onTouchEvent(event)}")
//        return false
//    }
}