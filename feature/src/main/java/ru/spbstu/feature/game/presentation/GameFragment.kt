package ru.spbstu.feature.game.presentation

import android.graphics.PointF
import android.graphics.Rect
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.animation.ScaleAnimation
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.scale
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setPivot
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.widgets.Board
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentGameBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent

class GameFragment : BaseFragment<GameViewModel>(
    R.layout.fragment_game,
) {
    override val binding by viewBinding(FragmentGameBinding::bind)

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()

        binding.morganBoard2.size = 3

//        binding.zoomView.setOnTouchListener { v, event ->
//            scaleGestureDetector.onTouchEvent(event)
//            translationHandler.onTouch(v, event)
//            binding.frgGameBoard.dispatchTouchEvent(event)
//            true
//        }
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .gameComponentFactory()
            .create(this)
            .inject(this)
    }

    private val originContentRect by lazy {
        binding.frgGameBoard.run {
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
                if (event == null || (binding.frgGameBoard.scaleX ?: 1f) == 1f) return false

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
                            binding.frgGameBoard.translationX += (event.x - prevX)
                            binding.frgGameBoard.translationY += (event.y - prevY)
                        }
                        prevX = event.x
                        prevY = event.y
                    }

                    MotionEvent.ACTION_UP -> {
                        if (!moveStarted) return false
                        reset()
                        //translateToOriginalRect()
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
        ScaleGestureDetector(
            context,
            object : ScaleGestureDetector.OnScaleGestureListener {
                var totalScale = 1f

                override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                    binding.frgGameBoard.run {
                        val actualPivot = PointF(
                            (detector.focusX - translationX + pivotX * (totalScale - 1)) / totalScale,
                            (detector.focusY - translationY + pivotY * (totalScale - 1)) / totalScale,
                        )

                        translationX -= (pivotX - actualPivot.x) * (totalScale - 1)
                        translationY -= (pivotY - actualPivot.y) * (totalScale - 1)
                        setPivot(actualPivot)
                    }
                    return true
                }

                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val prev = totalScale
                    totalScale *= detector.scaleFactor
                    totalScale = totalScale.coerceIn(Board.MIN_SCALE_FACTOR, Board.MAX_SCALE_FACTOR)
                    //Log.d("qwerty", "onScale")
                    binding.frgGameBoard.run {
                        val scaleAnimation = ScaleAnimation(prev, totalScale, prev, totalScale, detector.focusX, detector.focusY)
                        scaleAnimation.duration = 0
                        scaleAnimation.fillAfter = true
                        startAnimation(scaleAnimation)
                        scale(totalScale)
                        invalidate()
                        getContentViewTranslation().run {
                            translationX += x
                            translationY += y
                        }
                    }
                    return true
                }

                override fun onScaleEnd(detector: ScaleGestureDetector) = Unit
            })
    }

    private fun getContentViewTranslation(): PointF {
        return binding.frgGameBoard.run {
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
}