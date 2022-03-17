package ru.spbstu.common.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
    private var firstLayerBitmap: Bitmap
    private var secondLayerBitmap: Bitmap
    private var thirdLayerBitmap: Bitmap
    private var fourthLayerBitmap: Bitmap
    private var fifthLayerBitmap: Bitmap

    private var height: Float
    private var width: Float
    private var spacing: Float

    private var scaleFactor = 1f
    private var scaleGestureDetector: ScaleGestureDetector

    private var prevX = 0f
    private var prevY = 0f
    private var moveStarted = false

    var count = 5

    init {
        firstLayer = ContextCompat.getDrawable(context, R.drawable.ic_first_layer_36)
        secondLayer = ContextCompat.getDrawable(context, R.drawable.ic_second_layer_36)
        thirdLayer = ContextCompat.getDrawable(context, R.drawable.ic_third_layer_36)
        fourthLayer = ContextCompat.getDrawable(context, R.drawable.ic_fourth_layer_36)
        fifthLayer = ContextCompat.getDrawable(context, R.drawable.ic_fifth_layer_36)
//        firstLayerBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_first_layer_36)
//        secondLayerBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_second_layer_36)
//        thirdLayerBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_third_layer_36)
//        fourthLayerBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_fourth_layer_36)
//        fifthLayerBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_fifth_layer_36)
        firstLayerBitmap = firstLayer!!.toBitmap()
        secondLayerBitmap = secondLayer!!.toBitmap()
        thirdLayerBitmap = thirdLayer!!.toBitmap()
        fourthLayerBitmap = fourthLayer!!.toBitmap()
        fifthLayerBitmap = fifthLayer!!.toBitmap()

        height = context.dpToPx(35f)
        width = context.dpToPx(58f)
        spacing = context.dpToPx(2f)

        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
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
        return context.dpToPx(288f).toInt()
        //return height.toInt() + spacing.toInt() * 5
    }

    override fun getSuggestedMinimumWidth(): Int {
        return context.dpToPx(315f).toInt()
        //return width.toInt()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()
        canvas?.scale(scaleFactor, scaleFactor)
        val dx = 32f
        val dy = 19f

        canvas?.translate(suggestedMinimumWidth / 2f - 90, 0f)
        for (i in 0 until 5) {
            for (j in 0 until 5) {
                val newX = -i * dx + j * dx
                val newY = i * dy + j * dy
                val newXpx = context.dpToPx(newX)
                val newYpx = context.dpToPx(newY)
                canvas?.translate(newXpx, newYpx)
                //count = (1 + Math.random() * 5).toInt()
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

                canvas?.translate(-newXpx, -newYpx)
            }
        }

        canvas?.restore()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
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
                this.run {
                    translationX += (event.x - prevX)
                    translationY += (event.y - prevY)
                }
                prevX = event.x
                prevY = event.y
            }

            MotionEvent.ACTION_UP -> {
                if (!moveStarted) return false
//                reset()
//                translateToOriginalRect()
            }
        }
        scaleGestureDetector.onTouchEvent(event)
        return true
    }

    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor

            // Don't let the object get too small or too large.
            scaleFactor = max(0.1f, min(scaleFactor, 5.0f))
            invalidate()
            return true
        }
    }
}