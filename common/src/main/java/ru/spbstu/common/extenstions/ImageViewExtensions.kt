package ru.spbstu.common.extenstions

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.widget.ImageView
import androidx.core.content.ContextCompat
import ru.spbstu.common.R

fun ImageView.setDisabled() {
    imageTintList = ColorStateList.valueOf(
        ContextCompat.getColor(
            context,
            R.color.image_disabled_tint
        )
    )
    imageTintMode = PorterDuff.Mode.MULTIPLY
    isEnabled = false
}

fun ImageView.setEnabled() {
    imageTintMode = PorterDuff.Mode.DST
    isEnabled = true
}
