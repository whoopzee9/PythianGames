package ru.spbstu.common.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import ru.spbstu.common.R
import ru.spbstu.common.extenstions.dpToPx
import ru.spbstu.common.model.Player
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class CharacterIcon @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var imageHeight = context.dpToPx(50f)
    private var imageWidth = context.dpToPx(50f)
    private var drawableRes: Int
    var scale = 1f
    private var defScale = 1f
    private var maxScale = 1.2f
    private var bounds = Rect()
    private var textWidth = 0
    var additionalWidth = 0
    private var paint = Paint()
    private var textPadding = 10
    var fontHeight = 0f
    var drawable : Drawable?
    val rect = Rect()
    private var offset = 0

    private lateinit var player: Player

    init {
        drawableRes = R.drawable.character_1
        drawable = ContextCompat.getDrawable(context, drawableRes)

        paint.textSize = resources.getDimension(R.dimen.sp_14)
        paint.color = ContextCompat.getColor(context, R.color.text_color_primary)
        paint.typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
        val metrics = paint.fontMetrics
        fontHeight = metrics.descent - metrics.ascent
    }

    fun setDrawableResource(@DrawableRes res: Int) {
        drawableRes = res
        drawable = ContextCompat.getDrawable(context, drawableRes)
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

    override fun getSuggestedMinimumWidth(): Int {
        var suggested = super.getSuggestedMinimumHeight()
        suggested = Math.max(suggested, computeMaximumWidth())
        return suggested
    }

    override fun getSuggestedMinimumHeight(): Int {
        var suggested = super.getSuggestedMinimumWidth()
        suggested = max(suggested, computeMaximumHeight())
        return suggested
    }

    private fun computeMaximumHeight(): Int {
        return (imageHeight * scale ).toInt() + fontHeight.toInt() + paddingTop + paddingBottom
    }

    private fun computeMaximumWidth(): Int {
        return (imageWidth * scale).toInt() + (additionalWidth * maxScale).toInt() + offset
    }

    fun setPlayer(player: Player) {
        this.player = player
        paint.getTextBounds(player.name, 0, player.name.length, bounds)
        textWidth = bounds.width()
        additionalWidth = if (textWidth > imageWidth) (textWidth - imageWidth).toInt() else 0
        requestLayout()
    }

    fun getPlayer() = player

    override fun onDraw(canvas: Canvas?) {
        val group = parent as ViewGroup
        getDrawingRect(rect)
        group.offsetDescendantRectToMyCoords(this, rect)
        val relativeLeft = rect.left
        val relativeRight = rect.right

        if (scale > defScale) {
            val text = player.name
            paint.getTextBounds(text, 0, text.length, bounds)
            textWidth = bounds.width()
            additionalWidth = if (textWidth > imageWidth) (textWidth - imageWidth).toInt() else 0
            offset = if (relativeLeft < 50 && textWidth > imageWidth) abs(rect.left) else 0
            val x = if (textWidth > imageWidth) {
                if (relativeLeft < 50) {
                    abs(40f)
                } else if (relativeRight > group.width) {
                    (group.width - relativeRight).toFloat()
                } else {
                    0f
                }
            } else (imageWidth - textWidth) / 2
            canvas?.drawText(text, x, fontHeight - textPadding, paint)
        }

        canvas?.translate(additionalWidth / 2f, fontHeight)
        drawable?.setBounds(0,0,imageWidth.toInt(), imageHeight.toInt())
        drawable?.draw(canvas!!)
        canvas?.translate(-additionalWidth / 2f, -fontHeight)
    }
}