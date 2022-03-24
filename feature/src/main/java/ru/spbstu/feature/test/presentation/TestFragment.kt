package ru.spbstu.feature.test.presentation

import android.graphics.PointF
import android.graphics.Rect
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import androidx.core.view.doOnLayout
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.scale
import ru.spbstu.common.extenstions.setPivot
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentTestBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.test.presentation.adapter.TestAdapter


class TestFragment : ToolbarFragment<TestViewModel>(
    R.layout.fragment_test,
    R.string.error_connection,
    ToolbarType.EMPTY
) {

    override val binding by viewBinding(FragmentTestBinding::bind)

    private lateinit var adapter: TestAdapter

    override fun getToolbarLayout(): ViewGroup = binding.frgTestLayoutToolbar.root


//    private val gestureDetector by lazy {
//        GestureDetector(requireContext(), GestureListener())
//    }
//    private var mScale = 1.0
//    private val mScaleGestureDetector by lazy {
//        ScaleGestureDetector(
//            requireContext(),
//            object : ScaleGestureDetector.OnScaleGestureListener {
//                override fun onScale(detector: ScaleGestureDetector): Boolean {
//                    // firstly we will get the scale factor
//                    val scale = 1 - detector.scaleFactor
//                    val prevScale = mScale
//                    mScale += scale
//
//                    // we can maximise our focus to 10f only
//                    if (mScale > 10f)
//                        mScale = 10.0
//
//                    val scaleAnimation = ScaleAnimation(
//                        (1f / prevScale).toFloat(),
//                        (1f / mScale).toFloat(),
//                        (1f / prevScale).toFloat(),
//                        (1f / mScale).toFloat(), detector.focusX, detector.focusY
//                    );
//
//                    // duration of animation will be 0.It will
//                    // not change by self after that
//                    scaleAnimation.duration = 0
//                    scaleAnimation.fillAfter = true
//
//                    // we are setting it as animation
//                    binding.cardStack.startAnimation(scaleAnimation)
//                    return true;
//                }
//
//                override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
//                    return true
//                }
//
//                override fun onScaleEnd(detector: ScaleGestureDetector?) {
//                }
//            })
//    }

    override fun setupViews() {
        super.setupViews()
//        binding.cardStack.setOnClickListener {
//            binding.cardStack.count--
//            binding.cardStack.invalidate()
//        }
//        binding.cardStack.doOnLayout { originContentRect }
//        binding.cardStack.setOnTouchListener { view, event ->
//            //Log.d("qwerty", "touch")
//            scaleGestureDetector.onTouchEvent(event)
//            translationHandler.onTouch(view, event)
//            false
//        }
//        binding.cardStack.setOnTouchListener { v, event ->
//            mScaleGestureDetector.onTouchEvent(event)
//            gestureDetector.onTouchEvent(event)
//            true
//        }
        initAdapter()
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .testComponentFactory()
            .create(this)
            .inject(this)
    }

    override fun subscribe() {
        super.subscribe()
        viewModel.testData.observe {
            adapter.bindData(it)
        }
    }

    private fun initAdapter() {
        adapter = TestAdapter(viewModel::removeItemById)
        binding.frgTestRvList.adapter = adapter
    }

    private class GestureListener : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            return true
        }
    }

    companion object {
        private const val MAX_SCALE_FACTOR = 5f
        private const val MIN_SCALE_FACTOR = 1f
        private const val CORRECT_LOCATION_ANIMATION_DURATION = 300L
    }
}
