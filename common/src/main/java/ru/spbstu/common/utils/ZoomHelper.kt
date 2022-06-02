package ru.spbstu.common.utils

import android.graphics.PointF
import android.graphics.Rect
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.animation.ScaleAnimation
import ru.spbstu.common.extenstions.scale
import ru.spbstu.common.extenstions.setPivot

class ZoomHelper(private val zoomingView: View) {

    fun onTouchEvent(event: MotionEvent?) {
        scaleGestureDetector.onTouchEvent(event)
        translationHandler.onTouch(zoomingView, event)
    }

    private val originContentRect by lazy {
        zoomingView.run {
            val array = IntArray(2)
            getLocationOnScreen(array)
            Rect(array[0], array[1], array[0] + width, array[1] + height)
        }
    }

    private val translationHandler by lazy {
        object : View.OnTouchListener {
            private var prevX = 0f
            private var prevY = 0f
            private var moveStarted = false
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event == null || zoomingView.scaleX == 1f) return false

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
                        run {
                            zoomingView.translationX += (event.x - prevX)
                            zoomingView.translationY += (event.y - prevY)
                            checkTranslation()
                        }
                        prevX = event.x
                        prevY = event.y
                    }

                    MotionEvent.ACTION_UP -> {
                        if (!moveStarted) return false
                        reset()
                    }
                }
                return true
            }

            private fun reset() {
                prevX = 0f
                prevY = 0f
                moveStarted = false
            }
        }
    }

    private val scaleGestureDetector by lazy {
        with(zoomingView) {
            ScaleGestureDetector(
                context,
                object : ScaleGestureDetector.OnScaleGestureListener {
                    var totalScale = 1f

                    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                        run {
                            val actualPivot = PointF(
                                (detector.focusX - translationX + pivotX * (totalScale - 1)) / totalScale,
                                (detector.focusY - translationY + pivotY * (totalScale - 1)) / totalScale,
                            )

                            translationX -= (pivotX - actualPivot.x) * (totalScale - 1)
                            translationY -= (pivotY - actualPivot.y) * (totalScale - 1)
                            checkTranslation()
                            setPivot(actualPivot)
                        }
                        return true
                    }

                    override fun onScale(detector: ScaleGestureDetector): Boolean {
                        val prev = totalScale
                        totalScale *= detector.scaleFactor
                        totalScale =
                            totalScale.coerceIn(MIN_SCALE_FACTOR, MAX_SCALE_FACTOR)

                        run {
                            val scaleAnimation = ScaleAnimation(
                                prev,
                                totalScale,
                                prev,
                                totalScale,
                                detector.focusX,
                                detector.focusY
                            )
                            scaleAnimation.duration = 0
                            scaleAnimation.fillAfter = true
                            startAnimation(scaleAnimation)
                            scale(totalScale)
                            invalidate()
                            getContentViewTranslation().run {
                                translationX += x
                                translationY += y
                                checkTranslation()
                            }
                        }
                        return true
                    }

                    override fun onScaleEnd(detector: ScaleGestureDetector) = Unit
                })
        }
    }

    private fun getContentViewTranslation(): PointF {
        return zoomingView.run {
            originContentRect.let { rect ->
                val array = IntArray(2)
                getLocationOnScreen(array)
                PointF(
                    when {
                        array[0] > rect.left -> rect.left - array[0].toFloat()
                        array[0] + width * scaleX < rect.right -> rect.right - (array[0] + width * scaleX)
                        else -> 0f
                    },
                    when {
                        array[1] > rect.top -> rect.top - array[1].toFloat()
                        array[1] + height * scaleY < rect.bottom -> rect.bottom - (array[1] + height * scaleY)
                        else -> 0f
                    }
                )
            }
        }
    }

    private fun checkTranslation() {
        with(zoomingView) {
            if (translationX > MAX_TRANSLATION_X) {
                translationX = MAX_TRANSLATION_X
            }
            if (translationX < MIN_TRANSLATION_X) {
                translationX = MIN_TRANSLATION_X
            }
            if (translationY > MAX_TRANSLATION_Y) {
                translationY = MAX_TRANSLATION_Y
            }
            if (translationY < MIN_TRANSLATION_Y) {
                translationY = MIN_TRANSLATION_Y
            }
        }
    }

    companion object {
        const val MAX_SCALE_FACTOR = 1.2f
        const val MIN_SCALE_FACTOR = 1f
        const val MAX_TRANSLATION_X = 190f
        const val MIN_TRANSLATION_X = -190f
        const val MAX_TRANSLATION_Y = 50f
        const val MIN_TRANSLATION_Y = -50f
    }
}