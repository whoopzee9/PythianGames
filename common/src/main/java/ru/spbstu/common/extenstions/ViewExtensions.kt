package ru.spbstu.common.extenstions

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import ru.spbstu.common.R
import ru.spbstu.common.utils.DebounceClickListener
import ru.spbstu.common.utils.DebouncePostHandler


@Suppress("DEPRECATION")
fun View.setLightStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowInsetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            var flags = systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            systemUiVisibility = flags
        }
    }
}

@Suppress("DEPRECATION")
fun View.clearLightStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowInsetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            var flags = systemUiVisibility
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            systemUiVisibility = flags
        }
    }
}

fun View.setDebounceClickListener(
    delay: Long = DebouncePostHandler.DEFAULT_DELAY,
    onClickListener: View.OnClickListener
) {
    setOnClickListener(DebounceClickListener(delay, onClickListener))
}

@SuppressLint("ResourceType")
fun TextView.setResourceColor(@ColorRes resId: Int) {
    val color = Color.parseColor(resources.getString(resId))
    this.setTextColor(color)
}

fun View.rotateView(startAngle: Float = 0f, endAngle: Float = 180f, duration: Long = 300) {
    val rotate = ObjectAnimator.ofFloat(this, "rotation", startAngle, endAngle)
    rotate.duration = duration
    rotate.start()
}

fun View.margin(
    left: Float? = null,
    top: Float? = null,
    right: Float? = null,
    bottom: Float? = null
) {
    layoutParams<ViewGroup.MarginLayoutParams> {
        left?.run { leftMargin = dpToPx(this).toInt() }
        top?.run { topMargin = dpToPx(this).toInt() }
        right?.run { rightMargin = dpToPx(this).toInt() }
        bottom?.run { bottomMargin = dpToPx(this).toInt() }
    }
}

inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
    if (layoutParams is T) block(layoutParams as T)
}

fun View.dpToPx(dp: Float): Float = context.dpToPx(dp)

fun View.scale(scale: Float) {
    scaleX = scale
    scaleY = scale
}

fun View.setPivot(point: PointF) {
    pivotX = point.x
    pivotY = point.y
}

fun View.getBitmapFromView(): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val c = Canvas(bitmap)
    layout(left, top, right, bottom)
    draw(c)
    return bitmap
}

fun View.setBlurBackground(background: Bitmap) {
    val radius = 10f

    val overlay = Bitmap.createBitmap(
        measuredWidth,
        measuredHeight, Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(overlay)

    canvas.translate((-left).toFloat(), (-top).toFloat())
    canvas.drawBitmap(background, 0f, 0f, null)

    val rs = RenderScript.create(context)
    val overlayAlloc = Allocation.createFromBitmap(
        rs, overlay
    )
    val blur = ScriptIntrinsicBlur.create(
        rs, overlayAlloc.element
    )
    blur.setInput(overlayAlloc)
    blur.setRadius(radius)
    blur.forEach(overlayAlloc)
    overlayAlloc.copyTo(overlay)
    rs.destroy()


    setBackground(BitmapDrawable(resources, overlay))
    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color_transparent_background))
    backgroundTintMode = PorterDuff.Mode.SRC_ATOP
}
