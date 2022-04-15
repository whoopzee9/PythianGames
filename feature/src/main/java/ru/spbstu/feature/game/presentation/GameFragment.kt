package ru.spbstu.feature.game.presentation

import android.content.res.ColorStateList
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.animation.ScaleAnimation
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.dpToPx
import ru.spbstu.common.extenstions.scale
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setPivot
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.model.Player
import ru.spbstu.common.model.Team
import ru.spbstu.common.widgets.Board
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentGameBinding
import ru.spbstu.feature.databinding.FragmentGameStatisticsDialogBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.domain.model.InventoryModel
import ru.spbstu.feature.game.presentation.adapter.InventoryAdapter
import ru.spbstu.feature.game.presentation.adapter.InventoryItemDecoration

class GameFragment : BaseFragment<GameViewModel>(
    R.layout.fragment_game,
) {

    private lateinit var adapter: InventoryAdapter

    private lateinit var statisticsPopup: PopupWindow
    private val statisticsBinding by viewBinding(FragmentGameStatisticsDialogBinding::inflate)

    override val binding by viewBinding(FragmentGameBinding::bind)

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()
        //binding.stack.addPlayer(Player())

        val player = Player(1, R.drawable.character_2, Team.Orange, 2, "12", true)
        binding.frgGameBoard.addPlayer(player)
        binding.frgGameBoard.setCurrentPlayer(player.id)
        binding.frgGameBoard.addPlayer(Player(2, R.drawable.character_3, Team.Green, 1, "2", false))
        binding.frgGameBoard.addPlayer(Player(3, R.drawable.character_4, Team.Red, 2, "qwe", false))
        binding.frgGameBoard.addPlayer(
            Player(
                4,
                R.drawable.character_5,
                Team.Red,
                2,
                "qwавe",
                false
            )
        )
        binding.frgGameBoard.addPlayer(Player(1, R.drawable.character_2, Team.Green, 2, "", false))

//        binding.zoomView.setOnTouchListener { v, event ->
//            scaleGestureDetector.onTouchEvent(event)
//            translationHandler.onTouch(v, event)
//            binding.frgGameBoard.dispatchTouchEvent(event)
//            true
//        }
        setupAdapter()
        setupStatisticsPopup()

        binding.frgGameTeamStatsWrapper.setDebounceClickListener {
            //todo change teams stats and amount of teams
            statisticsPopup.showAsDropDown(it, 0, -it.height)
        }
    }

    private fun setupStatisticsPopup() {
        val background = GradientDrawable()
        background.setStroke(
            resources.getDimension(R.dimen.dp_1).toInt(),
            ContextCompat.getColor(requireContext(), R.color.color_team_green)
        )
        background.color = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_transparent_background))
        background.cornerRadius = resources.getDimension(R.dimen.dp_14)
        statisticsBinding.root.background = background
        statisticsPopup = PopupWindow(
            statisticsBinding.root,
            resources.displayMetrics.widthPixels - resources.getDimension(R.dimen.dp_24)
                .toInt(),
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        statisticsPopup.animationStyle = R.style.TopMenuAnimation
    }

    private fun setupAdapter() {
        adapter = InventoryAdapter()
        adapter.bindData(
            listOf(
                InventoryModel(1, "", R.drawable.ic_brush_78),
                InventoryModel(2, "", R.drawable.ic_rope_78),
                InventoryModel(3, "", R.drawable.ic_rope_78),
                InventoryModel(4, "", R.drawable.ic_sieve_78),
                InventoryModel(5, "", R.drawable.ic_sieve_78),
                InventoryModel(6, "", R.drawable.ic_brush_78),
                InventoryModel(7, "", R.drawable.ic_sieve_78),
                InventoryModel(8, "", R.drawable.ic_brush_78),
                InventoryModel(9, "", R.drawable.ic_brush_78),
            )
        )

        binding.frgGameRvInventory.addItemDecoration(InventoryItemDecoration())
        binding.frgGameRvInventory.adapter = adapter
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