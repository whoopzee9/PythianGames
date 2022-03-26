package ru.spbstu.common.extenstions

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import ru.spbstu.common.R

fun MaterialButton.changeToInactiveStyle(@ColorRes color: Int) {
    this.apply {
        setBackgroundColor(ContextCompat.getColor(context, R.color.background_primary))
        setTextColor(ContextCompat.getColor(context, color))
        setStrokeColorResource(color)
        setStrokeWidthResource(R.dimen.dp_2)
    }
}

fun MaterialButton.changeToActiveStyle(@ColorRes color: Int) {
    this.apply {
        setBackgroundColor(ContextCompat.getColor(context, color))
        setTextColor(ContextCompat.getColor(context, R.color.text_color_secondary))
        setStrokeColorResource(color)
        setStrokeWidthResource(R.dimen.dp_2)
    }
}