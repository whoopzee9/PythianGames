package ru.spbstu.common.extenstions

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import ru.spbstu.common.R

fun MaterialCardView.setToUnselectedStyle() {
    strokeWidth = resources.getDimension(R.dimen.dp_1).toInt()
    strokeColor = ContextCompat.getColor(context, R.color.stroke_color_secondary)
    radius = resources.getDimension(R.dimen.dp_12)
    background = null
    //setCardBackgroundColor(ContextCompat.getColor(context, R.color.color_transparent_background))
    isEnabled = true
    isChecked = false
    if (getChildAt(0) is TextView) {
        (getChildAt(0) as TextView).setTextColor(
            ContextCompat.getColor(
                context,
                R.color.text_color_primary
            )
        )
    }
}

fun MaterialCardView.setToSelectedStyle() {
    strokeWidth = 0
    radius = resources.getDimension(R.dimen.dp_12)
    //setBackgroundColor(ContextCompat.getColor(context, R.color.button_tint_primary))
    setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.background_answer))
    setCardBackgroundColor(ContextCompat.getColor(context, R.color.button_tint_primary))
    isEnabled = true
    isChecked = true
    if (getChildAt(0) is TextView) {
        (getChildAt(0) as TextView).setTextColor(
            ContextCompat.getColor(
                context,
                R.color.text_color_secondary
            )
        )
    }
}

fun MaterialCardView.setToDisabledStyle() {
    strokeWidth = 0
    background = null
    isChecked = false
    isEnabled = false
    if (getChildAt(0) is TextView) {
        (getChildAt(0) as TextView).setTextColor(
            ContextCompat.getColor(
                context,
                R.color.text_color_disabled
            )
        )
    }
}

fun MaterialCardView.setCorrectStyle() {
    strokeWidth = 0
    radius = resources.getDimension(R.dimen.dp_12)
    //setBackgroundColor(ContextCompat.getColor(context, R.color.button_tint_primary))
    setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.background_correct_answer))
    setCardBackgroundColor(ContextCompat.getColor(context, R.color.background_button_correct))
    isEnabled = false
    if (getChildAt(0) is TextView) {
        (getChildAt(0) as TextView).setTextColor(
            ContextCompat.getColor(
                context,
                R.color.text_color_secondary
            )
        )
    }
}

fun MaterialCardView.setIncorrectStyle() {
    strokeWidth = 0
    radius = resources.getDimension(R.dimen.dp_12)
    //setBackgroundColor(ContextCompat.getColor(context, R.color.button_tint_primary))
    setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.background_incorrect_answer))
    setCardBackgroundColor(ContextCompat.getColor(context, R.color.background_button_incorrect))
    isEnabled = false
    if (getChildAt(0) is TextView) {
        (getChildAt(0) as TextView).setTextColor(
            ContextCompat.getColor(
                context,
                R.color.text_color_secondary
            )
        )
    }
}