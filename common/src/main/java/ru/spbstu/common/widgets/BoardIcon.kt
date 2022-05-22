package ru.spbstu.common.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import ru.spbstu.common.R
import ru.spbstu.common.extenstions.dpToPx
import ru.spbstu.common.model.Player
import kotlin.math.min

class BoardIcon @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {
    private var activeTurnStrokeWidth = context.dpToPx(2f)
    private var player: Player = Player()
    private var characterBackground = ContextCompat.getDrawable(
        context,
        R.drawable.background_character_board
    ) as GradientDrawable
    private var iconHeight = resources.getDimension(R.dimen.dp_32)
    private var iconWidth = resources.getDimension(R.dimen.dp_32)
    private var activePlayerId: String = ""

    private val textPaint: Paint = Paint()
    private var fontHeight = 0f
    private val textMargin = resources.getDimension(R.dimen.dp_4)

    init {
        textPaint.textSize = resources.getDimension(R.dimen.sp_14)
        textPaint.color = ContextCompat.getColor(context, R.color.text_color_primary)
        textPaint.typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
        val metrics = textPaint.fontMetrics
        fontHeight = metrics.descent - metrics.ascent
    }

    fun setPlayer(player: Player) {
        this.player = player
        val draw = ContextCompat.getDrawable(context, player.iconRes)
        draw?.setBounds(0, 0, iconWidth.toInt(), iconHeight.toInt())
        setImageDrawable(draw)
        updateBackground()
        requestLayout()
    }

    private fun updateBackground() {
        characterBackground.color = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                player.team.colorRes
            )
        )

        characterBackground.setStroke(
            activeTurnStrokeWidth.toInt(),
            ContextCompat.getColor(
                context,
                if (player.id == activePlayerId)
                    R.color.color_active_turn
                else
                    player.team.colorRes
            )
        )
        background = characterBackground
    }

    fun getPlayer() = player

    fun setActivePlayerId(id: String) {
        if (activePlayerId != id) {
            activePlayerId = id
            updateBackground()
            requestLayout()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawText("texttext", 0f, 0f, textPaint)
        canvas?.translate(0f, fontHeight)
        super.onDraw(canvas)
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
        return iconHeight.toInt() + fontHeight.toInt() + textMargin.toInt()
    }

    override fun getSuggestedMinimumWidth(): Int {
        return iconWidth.toInt()
    }
}