package ru.spbstu.feature.game.presentation

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.setToSelectedStyle
import ru.spbstu.common.extenstions.setToUnselectedStyle
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.model.Player
import ru.spbstu.common.model.Team
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentGameBinding
import ru.spbstu.feature.databinding.FragmentGameStatisticsDialogBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.domain.model.InventoryModel
import ru.spbstu.feature.game.presentation.adapter.InventoryAdapter
import ru.spbstu.feature.game.presentation.adapter.InventoryItemDecoration
import java.util.*

class GameFragment : BaseFragment<GameViewModel>(
    R.layout.fragment_game,
) {

    private lateinit var adapter: InventoryAdapter

    private lateinit var statisticsPopup: PopupWindow
    private val statisticsBinding by viewBinding(FragmentGameStatisticsDialogBinding::inflate)

    private var _binding: FragmentGameBinding? = null
    override val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val timer = Timer()

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()
        //binding.stack.addPlayer(Player())

        val player = Player("1", R.drawable.character_2, Team.Orange, 2, "12", true)
        binding.frgGameBoard.addPlayer(player)
        binding.frgGameBoard.setCurrentPlayer(player.id)
        binding.frgGameBoard.addPlayer(
            Player(
                "2",
                R.drawable.character_3,
                Team.Green,
                1,
                "2",
                false
            )
        )
        binding.frgGameBoard.addPlayer(
            Player(
                "3",
                R.drawable.character_4,
                Team.Red,
                2,
                "qwe",
                false
            )
        )
        binding.frgGameBoard.addPlayer(
            Player(
                "4",
                R.drawable.character_5,
                Team.Red,
                2,
                "qwавe",
                false
            )
        )
        binding.frgGameBoard.addPlayer(
            Player(
                "5",
                R.drawable.character_2,
                Team.Green,
                2,
                "",
                false
            )
        )

        setupAdapter()
        setupStatisticsPopup()
        
        val questionBackground = GradientDrawable()
        questionBackground.setStroke(
            resources.getDimension(R.dimen.dp_1).toInt(),
            ContextCompat.getColor(requireContext(), R.color.color_team_green)
        )
        questionBackground.color = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_transparent_background
            )
        )
        questionBackground.cornerRadius = resources.getDimension(R.dimen.dp_14)
        binding.frgGameQuestionLayout.root.background = questionBackground

        binding.frgGameTeamStatsWrapper.setDebounceClickListener {
            //todo change teams stats and amount of teams
            statisticsPopup.showAsDropDown(it, 0, -it.height)
//            timer.schedule(object: TimerTask() {
//                override fun run() {
//                    lifecycleScope.launch(Dispatchers.Main) {
//                        statisticsPopup.dismiss()
//                    }
//                }
//
//            }, 2000)
        }
        binding.frgGameMbAction1.setDebounceClickListener {
            //todo fill question info
//            val bkg = binding.root.getBitmapFromView()
//            binding.frgGameQuestionLayout.root.post {
//                binding.frgGameQuestionLayout.root.setBlurBackground(bkg)
//            }
            with(binding.frgGameQuestionLayout) {
                val list = listOf(
                    includeQuestionDialogCvAnswer1Wrapper,
                    includeQuestionDialogCvAnswer2Wrapper,
                    includeQuestionDialogCvAnswer3Wrapper,
                    includeQuestionDialogCvAnswer4Wrapper
                )
                list.forEachIndexed { index, card ->
                    card.setToUnselectedStyle()
                    card.setDebounceClickListener {
                        //todo save index of clicked
                        list.forEach { it.setToUnselectedStyle() }
                        card.setToSelectedStyle()
                    }
                }
                includeQuestionDialogTvQuestionNumber.text = getString(R.string.question_number, 4)
                includeQuestionDialogTvQuestion.text =
                    "sdfsdgfwdfwefwdf wdfsdcvrgedfgvef vedvdecvefde fdd sdfsdfsdf sdfsdfsdf sdfsdfsdf sdfsdfsdfs sdfsdfsdfsd sdfsd fsd fsd f"
                includeQuestionDialogTvAnswer1.text =
                    "sdfsdfsdfs sdfgdfgdfg d dgdfgdfgdfg dfgdfgdfgdf gdfgdfgdf dfgdfgdfg dfgfd d"
                includeQuestionDialogTvAnswer2.text =
                    "dsffffffffffffffffffffffffffffffffffffffffff sdfsdfsdfsdf sdfsdfsdf sdfsdf"
                includeQuestionDialogTvAnswer3.text =
                    "qweq32ef24rfw34r 2rfd24eredf 2erf sfsdfsdfsd fsdfsdf sdf sdfwd3r"
                includeQuestionDialogTvAnswer4.text =
                    "wqer2erfsd erwf fwef wefwef wef wef sdf sdfsd fsdfsdfsd wef w"

            }

            binding.frgGameQuestionLayout.root.visibility = View.VISIBLE
            binding.frgGameTvAction.visibility = View.GONE
            //todo change titles of buttons
        }
    }

    private fun setupStatisticsPopup() {
        val background = GradientDrawable()
        background.setStroke(
            resources.getDimension(R.dimen.dp_1).toInt(),
            ContextCompat.getColor(requireContext(), R.color.color_team_green)
        )
        background.color = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_transparent_background
            )
        )
        background.cornerRadius = resources.getDimension(R.dimen.dp_14)
        //statisticsBinding.root.background = background
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

}