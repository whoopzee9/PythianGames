package ru.spbstu.common.extenstions

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import ru.spbstu.common.R

fun MaterialButton.setInactiveStyle(@ColorRes color: Int) {
    this.apply {
        if (!isEnabled) {
            setDisabledStyle()
        } else {
            setBackgroundColor(ContextCompat.getColor(context, R.color.background_primary))
            setTextColor(ContextCompat.getColor(context, color))
            setStrokeColorResource(color)
            setStrokeWidthResource(R.dimen.dp_2)
            isEnabled = true
            isChecked = false
        }
    }
}

fun MaterialButton.setActiveStyle(@ColorRes color: Int) {
    this.apply {
        setBackgroundColor(ContextCompat.getColor(context, color))
        setTextColor(ContextCompat.getColor(context, R.color.text_color_secondary))
        setStrokeColorResource(color)
        setStrokeWidthResource(R.dimen.dp_2)
        isEnabled = true
        isChecked = true
    }
}

fun MaterialButton.setDisabledStyle() {
    this.apply {
        setBackgroundColor(ContextCompat.getColor(context, R.color.background_primary))
        setTextColor(ContextCompat.getColor(context, R.color.text_color_disabled))
        setStrokeColorResource(R.color.button_disabled_tint)
        setStrokeWidthResource(R.dimen.dp_2)
        isEnabled = false
        isChecked = false
    }
}