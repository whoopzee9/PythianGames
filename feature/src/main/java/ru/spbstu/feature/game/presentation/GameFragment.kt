package ru.spbstu.feature.game.presentation

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.subscribe
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.model.MovingPossibilities
import ru.spbstu.common.model.Player
import ru.spbstu.common.model.Position
import ru.spbstu.common.utils.DatabaseReferences
import ru.spbstu.common.utils.TeamsConstants
import ru.spbstu.common.widgets.BoardArrow
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentGameBinding
import ru.spbstu.feature.databinding.FragmentGameStatisticsDialogBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.domain.model.CardType
import ru.spbstu.feature.domain.model.Game
import ru.spbstu.feature.domain.model.GameState
import ru.spbstu.feature.domain.model.GameStateTypes
import ru.spbstu.feature.domain.model.InventoryModel
import ru.spbstu.feature.domain.model.PlayerInfo
import ru.spbstu.feature.domain.model.toPlayer
import ru.spbstu.feature.game.presentation.adapter.InventoryAdapter
import ru.spbstu.feature.game.presentation.adapter.InventoryItemDecoration
import ru.spbstu.feature.game.presentation.dialog.ConfirmationDialogFragment
import java.util.*

class GameFragment : BaseFragment<GameViewModel>(
    R.layout.fragment_game,
) {

    private lateinit var adapter: InventoryAdapter

    private lateinit var statisticsPopup: PopupWindow
    private val statisticsBinding by viewBinding(FragmentGameStatisticsDialogBinding::inflate)

    private var _binding: FragmentGameBinding? = null
    override val binding get() = _binding!!

    private var listener: ValueEventListener? = null

    private var confirmationDialogFragment: ConfirmationDialogFragment? = null

    private val timer = Timer()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()

        binding.frgGameBoard.setSize(
            if (viewModel.gameJoiningDataWrapper.game.numOfPlayers > 4) 5 else 3
        )

        setupAdapter()
        setupStatisticsPopup()

        setupDiceDialog()
        setupWheelDialog()

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

            when (viewModel.game.value.gameState.type) {
                GameStateTypes.Turn -> { //dig or clean
                    if (viewModel.game.value.currentPlayerId == viewModel.currentUserId) {
                        val moving = binding.frgGameBoard.canDigInCurrentPosition()
                        val size = binding.frgGameBoard.getSize()
                        if (moving.canBet) {
                            viewModel.setGameState(GameState(type = GameStateTypes.Wheel)) //todo do we need params here?

                        } else {
                            //todo set state depending on the card (question, tooth)
                            val card = viewModel.game.value.cards.firstOrNull {
                                it.layer == moving.layer &&
                                        it.cardNum == moving.position.y * size + moving.position.x + 1
                            }
                            if (card != null) {
                                if (card.type == CardType.Question) {
                                    viewModel.setGameState(GameState(type = GameStateTypes.Question)) //todo do we need params here?
                                } else {
                                    viewModel.setGameState(GameState(type = GameStateTypes.Tooth)) //todo do we need params here?
                                }
                            }
                        }
                    }
                }
            }
//            with(binding.frgGameQuestionLayout) {
//                val list = listOf(
//                    includeQuestionDialogCvAnswer1Wrapper,
//                    includeQuestionDialogCvAnswer2Wrapper,
//                    includeQuestionDialogCvAnswer3Wrapper,
//                    includeQuestionDialogCvAnswer4Wrapper
//                )
//                list.forEachIndexed { index, card ->
//                    card.setToUnselectedStyle()
//                    card.setDebounceClickListener {
//                        //todo save index of clicked
//                        list.forEach { it.setToUnselectedStyle() }
//                        card.setToSelectedStyle()
//                    }
//                }
//                includeQuestionDialogTvQuestionNumber.text = getString(R.string.question_number, 4)
//                includeQuestionDialogTvQuestion.text =
//                    "sdfsdgfwdfwefwdf wdfsdcvrgedfgvef vedvdecvefde fdd sdfsdfsdf sdfsdfsdf sdfsdfsdf sdfsdfsdfs sdfsdfsdfsd sdfsd fsd fsd f"
//                includeQuestionDialogTvAnswer1.text =
//                    "sdfsdfsdfs sdfgdfgdfg d dgdfgdfgdfg dfgdfgdfgdf gdfgdfgdf dfgdfgdfg dfgfd d"
//                includeQuestionDialogTvAnswer2.text =
//                    "dsffffffffffffffffffffffffffffffffffffffffff sdfsdfsdfsdf sdfsdfsdf sdfsdf"
//                includeQuestionDialogTvAnswer3.text =
//                    "qweq32ef24rfw34r 2rfd24eredf 2erf sfsdfsdfsd fsdfsdf sdf sdfwd3r"
//                includeQuestionDialogTvAnswer4.text =
//                    "wqer2erfsd erwf fwef wefwef wef wef sdf sdfsd fsdfsdfsd wef w"
//
//            }
//
//            binding.frgGameQuestionLayout.root.visibility = View.VISIBLE
//            binding.frgGameTvAction.visibility = View.GONE
//
        }

        binding.frgGameMbAction2.setDebounceClickListener {
            when (viewModel.game.value.gameState.type) {
                GameStateTypes.Turn -> { //walk
                    if (viewModel.game.value.currentPlayerId == viewModel.currentUserId) {
                        val direction = binding.frgGameBoard.getSelectedMovingDirection()
                        val player =
                            viewModel.game.value.players[viewModel.currentUserId] ?: PlayerInfo()
                        val newPosition: Position
                        if (direction != null) {
                            newPosition = when (direction) {
                                BoardArrow.Direction.Up -> {
                                    Position(player.position.x, player.position.y - 1)
                                }
                                BoardArrow.Direction.Down -> {
                                    Position(player.position.x, player.position.y + 1)
                                }
                                BoardArrow.Direction.Left -> {
                                    Position(player.position.x - 1, player.position.y)
                                }
                                BoardArrow.Direction.Right -> {
                                    Position(player.position.x + 1, player.position.y)
                                }
                            }
                            viewModel.movePlayer(player.id, newPosition)
                            viewModel.passTurnToNextPlayer()
                        }
                    }
                }
            }
        }

        binding.frgGameMbAction3.setDebounceClickListener {
            when (viewModel.game.value.gameState.type) {
                GameStateTypes.Turn -> { //walk and dig or clean
                    if (viewModel.game.value.currentPlayerId == viewModel.currentUserId) {
                        val direction = binding.frgGameBoard.getSelectedMovingDirection()
                        val player =
                            viewModel.game.value.players[viewModel.currentUserId] ?: PlayerInfo()
                        val newPosition: Position
                        val moving: MovingPossibilities
                        if (direction != null) {
                            when (direction) {
                                BoardArrow.Direction.Up -> {
                                    newPosition = Position(player.position.x, player.position.y - 1)
                                    moving = binding.frgGameBoard.canDigUp()
                                }
                                BoardArrow.Direction.Down -> {
                                    newPosition = Position(player.position.x, player.position.y + 1)
                                    moving = binding.frgGameBoard.canDigDown()
                                }
                                BoardArrow.Direction.Left -> {
                                    newPosition = Position(player.position.x - 1, player.position.y)
                                    moving = binding.frgGameBoard.canDigLeft()
                                }
                                BoardArrow.Direction.Right -> {
                                    newPosition = Position(player.position.x + 1, player.position.y)
                                    moving = binding.frgGameBoard.canDigRight()
                                }
                            }
                            viewModel.movePlayer(player.id, newPosition)

                            val size = binding.frgGameBoard.getSize()
                            if (moving.canBet) {
                                viewModel.setGameState(GameState(type = GameStateTypes.Wheel)) //todo do we need params here?

                            } else {
                                //todo set state depending on the card (question, tooth)
                                val card = viewModel.game.value.cards.firstOrNull {
                                    it.layer == moving.layer &&
                                            it.cardNum == moving.position.y * size + moving.position.x + 1
                                }
                                if (card != null) {
                                    if (card.type == CardType.Question) {
                                        viewModel.setGameState(GameState(type = GameStateTypes.Question)) //todo do we need params here?
                                    } else {
                                        viewModel.setGameState(GameState(type = GameStateTypes.Tooth)) //todo do we need params here?
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.frgGameMbAction4.setDebounceClickListener {
            when (viewModel.game.value.gameState.type) {
                GameStateTypes.Turn -> { //skip
                    showConfirmationDialog(actionOk = {
                        viewModel.passTurnToNextPlayer()
                    }, actionCancel = {}, text = getString(R.string.skip_confirmation))
                }
            }
        }

        binding.frgGameMbAction1.visibility = View.GONE
        binding.frgGameMbAction2.visibility = View.GONE
        binding.frgGameMbAction3.visibility = View.GONE
        binding.frgGameMbAction4.visibility = View.GONE
    }

    override fun subscribe() {
        super.subscribe()
        val ref = Firebase.database.getReference(DatabaseReferences.GAMES_REF)
        listener = ref.child(viewModel.gameJoiningDataWrapper.game.name)
            .subscribe(onSuccess = { snapshot ->
                val game = snapshot.getValue(Game::class.java)
                if (game != null) {
                    viewModel.setGame(game)
                    handleGameData(game)
                }
            }, onCancelled = {})
    }

    override fun onDestroyView() {
        val ref = Firebase.database.getReference(DatabaseReferences.GAMES_REF)
        listener?.let { ref.removeEventListener(it) }
        _binding = null
        super.onDestroyView()
    }

    // Main handler for the game
    private fun handleGameData(game: Game) {
        if (binding.frgGameBoard.getPlayersAmount() != game.players.values.size) {
            game.players.values.forEach {
                binding.frgGameBoard.addPlayer(it.toPlayer())
            }
        }
        determineMorganPosition(game)
        binding.frgGameBoard.setCurrentPlayer(viewModel.currentUserId ?: "")
        binding.frgGameBoard.updatePlayers(game.players.mapValues { it.value.toPlayer() } as HashMap<String, Player>)
        binding.frgGameBoard.setActiveTurnPlayer(game.currentPlayerId)
        binding.frgGameBoard.clearArrowCallback()
        when (game.gameState.type) {
            GameStateTypes.Start -> {

                val isRolled = game.gameState.param1 as? Boolean
                val rollNum = game.gameState.param2 as? Long
                val rolled = game.gameState.param3 as? Long

                if (isRolled != null && rollNum != null && rolled != null) {
                    val rollUser = game.players.values.first()
                    binding.frgGameDiceLayout.includeDiceDialogIvDice.isClickable =
                        rollUser.id == viewModel.currentUserId
                    binding.frgGameDiceLayout.root.visibility = View.VISIBLE
                    if (rollUser.id == viewModel.currentUserId) {
                        binding.frgGameDiceLayout.includeDiceDialogTvTitle.setText(
                            if (rollNum == 1L) R.string.roll_dice_morgan_side else R.string.roll_dice_morgan_position
                        )
                    } else {
                        binding.frgGameDiceLayout.includeDiceDialogTvTitle.setText(R.string.determining_morgan_position)
                    }
                    if (rollUser.id != viewModel.currentUserId && isRolled) {
                        binding.frgGameDiceLayout.includeDiceDialogIvDice.performClick()
                    }
                } else {
                    throw IllegalStateException("Wrong game state params!")
                }
            }
            GameStateTypes.Turn -> {
                binding.frgGameDiceLayout.root.visibility = View.GONE
                val activePlayersTeam = TeamsConstants.getTeamFromString(
                    game.players[game.currentPlayerId]?.teamStr ?: ""
                )
                if (game.currentPlayerId == viewModel.currentUserId) {
                    binding.frgGameTvAction.setText(R.string.your_turn)
                    binding.frgGameMbAction4.setImageResource(R.drawable.ic_close_24)
                    binding.frgGameBoard.clearSelectedMovingDirection()
                    binding.frgGameBoard.determineDiggingAvailability()
                    val canDig = binding.frgGameBoard.canDigInCurrentPosition()
                    if (canDig.canDig) {
                        binding.frgGameMbAction1.visibility = View.VISIBLE
                        binding.frgGameMbAction1.setImageResource(
                            if (canDig.isCleaning) R.drawable.ic_brush_24 else R.drawable.ic_shovel_24
                        )
                    } else {
                        binding.frgGameMbAction1.visibility = View.INVISIBLE
                    }

                    if (binding.frgGameBoard.getSelectedMovingDirection() == null) {
                        binding.frgGameMbAction2.visibility = View.INVISIBLE
                        binding.frgGameMbAction3.visibility = View.INVISIBLE
                    } else {
                        binding.frgGameMbAction2.visibility = View.VISIBLE
                        binding.frgGameMbAction3.visibility = View.VISIBLE
                    }

                    binding.frgGameMbAction4.visibility = View.VISIBLE

                    binding.frgGameBoard.setArrowCallback { direction, movingPossibilities ->
                        binding.frgGameMbAction2.visibility = View.VISIBLE
                        if (movingPossibilities.canDig) {
                            binding.frgGameMbAction3.visibility = View.VISIBLE
                            binding.frgGameMbAction3.setImageResource(
                                if (movingPossibilities.isCleaning)
                                    R.drawable.ic_walk_and_clean_24
                                else
                                    R.drawable.ic_walk_and_dig_24
                            )
                        } else {
                            binding.frgGameMbAction3.visibility = View.INVISIBLE
                        }
                    }
                    //todo calculate where we can go, then clear this info when turn ends
                } else {
                    binding.frgGameBoard.clearArrowCallback()
                    binding.frgGameTvAction.text =
                        getString(R.string.players_turn, game.players[game.currentPlayerId]?.name)
                    binding.frgGameMbAction1.visibility = View.GONE
                    binding.frgGameMbAction2.visibility = View.GONE
                    binding.frgGameMbAction3.visibility = View.GONE
                    binding.frgGameMbAction4.visibility = View.GONE
                }
                binding.frgGameTvAction.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        activePlayersTeam.colorRes
                    )
                )
            }
            GameStateTypes.Question -> TODO()
        }
    }

    private fun showConfirmationDialog(
        actionOk: () -> Unit,
        actionCancel: () -> Unit,
        text: String
    ) {
        if (confirmationDialogFragment == null) {
            confirmationDialogFragment =
                ConfirmationDialogFragment.newInstance(getString(R.string.skip_confirmation))
        }
        val dialog = confirmationDialogFragment
        if (dialog != null) {
            dialog.setDialogWarningText(text)
            dialog.setOnOkClickListener {
                actionOk.invoke()
                dialog.dismiss()
            }
            dialog.setOnCancelClickListener {
                actionCancel.invoke()
                dialog.dismiss()
            }
            dialog.show(parentFragmentManager, CONFIRMATION_DIALOG_TAG)
        }
    }

    private fun determineMorganPosition(game: Game) {
        binding.frgGameBoard.setMorganSelectingSide(game.morganSideSelecting)
        if (game.morganSideSelecting) {
            binding.frgGameBoard.setMorganSelectedSide(game.morganPosition)
        } else {
            binding.frgGameBoard.setMorganPosition(game.morganPosition)
        }
    }

    private fun setupWheelDialog() {
        val wheelBackground = GradientDrawable()
        wheelBackground.setStroke(
            resources.getDimension(R.dimen.dp_1).toInt(),
            ContextCompat.getColor(requireContext(), R.color.color_team_blue)
        )
        wheelBackground.color = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_transparent_background
            )
        )
        wheelBackground.shape = GradientDrawable.OVAL
        binding.frgGameWheelLayout.root.background = wheelBackground

        val centralBackground = GradientDrawable()
        centralBackground.setStroke(
            resources.getDimension(R.dimen.dp_1).toInt(),
            ContextCompat.getColor(requireContext(), R.color.stroke_color_secondary)
        )
        centralBackground.shape = GradientDrawable.OVAL
        centralBackground.color = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_layer_orange
            )
        )
        binding.frgGameWheelLayout.includeWheelDialogCentralView.background = centralBackground

        val viewsBackground = GradientDrawable()
        viewsBackground.setStroke(
            resources.getDimension(R.dimen.dp_1).toInt(),
            ContextCompat.getColor(requireContext(), R.color.stroke_color_secondary)
        )
        viewsBackground.shape = GradientDrawable.OVAL

        viewsBackground.color = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_team_red
            )
        )
        listOf(
            binding.frgGameWheelLayout.includeWheelDialogWhiteBeans1View1,
            binding.frgGameWheelLayout.includeWheelDialogWhiteBeans1View2,
            binding.frgGameWheelLayout.includeWheelDialogWhiteBeans1View3,
            binding.frgGameWheelLayout.includeWheelDialogWhiteBeans2View1,
            binding.frgGameWheelLayout.includeWheelDialogWhiteBeans2View2,
            binding.frgGameWheelLayout.includeWheelDialogWhiteBeans2View3,
            binding.frgGameWheelLayout.includeWheelDialogWhiteBeans3View1,
            binding.frgGameWheelLayout.includeWheelDialogWhiteBeans3View2,
            binding.frgGameWheelLayout.includeWheelDialogWhiteBeans3View3,
            binding.frgGameWheelLayout.includeWheelDialogBlackBeans1View1,
            binding.frgGameWheelLayout.includeWheelDialogBlackBeans1View2,
            binding.frgGameWheelLayout.includeWheelDialogBlackBeans1View3,
            binding.frgGameWheelLayout.includeWheelDialogBlackBeans2View1,
            binding.frgGameWheelLayout.includeWheelDialogBlackBeans2View2,
            binding.frgGameWheelLayout.includeWheelDialogBlackBeans2View3,
            binding.frgGameWheelLayout.includeWheelDialogBlackBeans3View1,
            binding.frgGameWheelLayout.includeWheelDialogBlackBeans3View2,
            binding.frgGameWheelLayout.includeWheelDialogBlackBeans3View3,
            binding.frgGameWheelLayout.includeWheelDialogDragon1View1,
            binding.frgGameWheelLayout.includeWheelDialogDragon1View2,
            binding.frgGameWheelLayout.includeWheelDialogDragon1View3,
            binding.frgGameWheelLayout.includeWheelDialogDragon2View1,
            binding.frgGameWheelLayout.includeWheelDialogDragon2View2,
            binding.frgGameWheelLayout.includeWheelDialogDragon2View3,
        ).forEach {
            it.background = viewsBackground
        }
    }

    private fun setupDiceDialog() {
        val diceBackground = GradientDrawable()
        diceBackground.setStroke(
            resources.getDimension(R.dimen.dp_1).toInt(),
            ContextCompat.getColor(requireContext(), R.color.color_neutral_action)
        )
        diceBackground.color = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_transparent_background
            )
        )
        diceBackground.cornerRadius = resources.getDimension(R.dimen.dp_14)
        binding.frgGameDiceLayout.root.background = diceBackground

        binding.frgGameDiceLayout.includeDiceDialogTvDiceValue.text = "1"
        val random = Random(viewModel.gameJoiningDataWrapper.game.name.hashCode().toLong())

        binding.frgGameDiceLayout.includeDiceDialogIvDice.setDebounceClickListener {
            if (viewModel.game.value.players.values.first().id == viewModel.currentUserId) {
                viewModel.setGameState(viewModel.game.value.gameState.copy(param1 = true)) //another users should see roll
            }

            //is needed to prevent multiple rolls
            binding.frgGameDiceLayout.includeDiceDialogIvDice.isClickable = false

            binding.frgGameDiceLayout.includeDiceDialogDiceWrapper.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.shake_animation
                )
            )
            var dice = 0
            lifecycleScope.launch {
                for (i in 0..20) {
                    launch(Dispatchers.Main) {
                        dice = random.nextInt(4) + 1
                        binding.frgGameDiceLayout.includeDiceDialogTvDiceValue.text =
                            dice.toString()
                    }
                    delay(30)
                }
                if (viewModel.game.value.players.values.first().id == viewModel.currentUserId) {
                    if (viewModel.game.value.gameState.type == GameStateTypes.Start &&
                        viewModel.game.value.gameState.param2 as Long == 1L
                    ) {
                        viewModel.setGameState(
                            viewModel.game.value.gameState.copy(
                                param1 = false,
                                param2 = 2,
                                param3 = dice
                            )
                        )
                        viewModel.setMorganSide(dice)
                    } else {
                        lifecycleScope.launch {
                            //delay is needed to display dice longer before closing
                            delay(1000) //todo maybe change to timer
                            val side = viewModel.game.value.morganPosition
                            val size = binding.frgGameBoard.getSize()
                            val position = if (dice > size) dice - size else dice
                            viewModel.setGameState(GameState(type = GameStateTypes.Turn))
                            viewModel.setMorganStartPosition((side - 1) * size + position - 1)
                            val firstPlayer = viewModel.getFirstPlayerByMorganPosition(
                                (side - 1) * size + position - 1,
                                viewModel.game.value.players.values.toList()
                            )
                            viewModel.setActivePlayerId(firstPlayer.id)
                            viewModel.updatePlayersTurnOrders(firstPlayer.turnOrder)
                        }
                    }
                }
            }
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

    companion object {
        private val TAG = GameFragment::class.java.simpleName
        private val CONFIRMATION_DIALOG_TAG = "${TAG}CONFIRMATION_DIALOG_TAG"
    }
}