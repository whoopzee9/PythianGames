package ru.spbstu.common.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import ru.spbstu.common.R
import ru.spbstu.common.extenstions.dpToPx
import ru.spbstu.common.model.Player
import kotlin.math.min


class CardStack @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var clearLayer: Drawable?

    private var height = resources.getDimension(R.dimen.dp_35)
    private var width = resources.getDimension(R.dimen.dp_58)
    private var spacing = resources.getDimension(R.dimen.dp_2)

    private val firstLayerColor: Int
    private val secondLayerColor: Int
    private val thirdLayerColor: Int
    private val fourthLayerColor: Int
    private val fifthLayerColor: Int

    var count = 5

    init {

        clearLayer = ContextCompat.getDrawable(context, R.drawable.ic_clear_layer_36)
        clearLayer?.setBounds(0, 0, width.toInt(), height.toInt())
        clearLayer?.setTintMode(PorterDuff.Mode.MULTIPLY)

        firstLayerColor = ContextCompat.getColor(context, R.color.color_layer_yellow)
        secondLayerColor = ContextCompat.getColor(context, R.color.color_layer_orange)
        thirdLayerColor = ContextCompat.getColor(context, R.color.color_layer_red)
        fourthLayerColor = ContextCompat.getColor(context, R.color.color_layer_blue)
        fifthLayerColor = ContextCompat.getColor(context, R.color.color_layer_purple)
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
                    clearLayer?.setTint(fifthLayerColor)
                    clearLayer?.draw(canvas)
                    canvas.translate(0f, -spacing)
                    clearLayer?.setTint(fourthLayerColor)
                    clearLayer?.draw(canvas)
                    canvas.translate(0f, -spacing)
                    clearLayer?.setTint(thirdLayerColor)
                    clearLayer?.draw(canvas)
                    canvas.translate(0f, -spacing)
                    clearLayer?.setTint(secondLayerColor)
                    clearLayer?.draw(canvas)
                    canvas.translate(0f, -spacing)
                    clearLayer?.setTint(firstLayerColor)
                    clearLayer?.draw(canvas)
                }
            }
            4 -> {
                if (canvas != null) {
                    canvas.translate(0f, spacing * 4)
                    clearLayer?.setTint(fifthLayerColor)
                    clearLayer?.draw(canvas)
                    canvas.translate(0f, -spacing)
                    clearLayer?.setTint(fourthLayerColor)
                    clearLayer?.draw(canvas)
                    canvas.translate(0f, -spacing)
                    clearLayer?.setTint(thirdLayerColor)
                    clearLayer?.draw(canvas)
                    canvas.translate(0f, -spacing)
                    clearLayer?.setTint(secondLayerColor)
                    clearLayer?.draw(canvas)
                    canvas.translate(0f, spacing * (-1))
                }
            }
            3 -> {
                if (canvas != null) {
                    canvas.translate(0f, spacing * 4)
                    clearLayer?.setTint(fifthLayerColor)
                    clearLayer?.draw(canvas)
                    canvas.translate(0f, -spacing)
                    clearLayer?.setTint(fourthLayerColor)
                    clearLayer?.draw(canvas)
                    canvas.translate(0f, -spacing)
                    clearLayer?.setTint(thirdLayerColor)
                    clearLayer?.draw(canvas)
                    canvas.translate(0f, spacing * (-2))
                }
            }
            2 -> {
                if (canvas != null) {
                    canvas.translate(0f, spacing * 4)
                    clearLayer?.setTint(fifthLayerColor)
                    clearLayer?.draw(canvas)
                    canvas.translate(0f, -spacing)
                    clearLayer?.setTint(fourthLayerColor)
                    clearLayer?.draw(canvas)
                    canvas.translate(0f, spacing * (-3))
                }
            }
            1 -> {
                if (canvas != null) {
                    canvas.translate(0f, spacing * 4)
                    clearLayer?.setTint(fifthLayerColor)
                    clearLayer?.draw(canvas)
                    canvas.translate(0f, spacing * (-4))
                }
            }
            else -> {
            }
        }
    }
}