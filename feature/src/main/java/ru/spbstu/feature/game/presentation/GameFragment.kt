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
import android.widget.Toast
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
import ru.spbstu.common.extenstions.handleBackPressed
import ru.spbstu.common.extenstions.setCorrectStyle
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setIncorrectStyle
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.setToDisabledStyle
import ru.spbstu.common.extenstions.setToSelectedStyle
import ru.spbstu.common.extenstions.setToUnselectedStyle
import ru.spbstu.common.extenstions.subscribe
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.model.CardType
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
import ru.spbstu.feature.domain.model.Game
import ru.spbstu.feature.domain.model.GameState
import ru.spbstu.feature.domain.model.GameStateTypes
import ru.spbstu.feature.domain.model.InventoryModel
import ru.spbstu.feature.domain.model.PlayerInfo
import ru.spbstu.feature.domain.model.WheelBet
import ru.spbstu.feature.domain.model.WheelBetType
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

        viewModel.size = if (viewModel.gameJoiningDataWrapper.game.numOfPlayers > 4) 5 else 3
        binding.frgGameBoard.setSize(viewModel.size)

        setupAdapter()
        setupStatisticsPopup()

        setupDiceDialog()
        setupWheelDialog()
        setupQuestionsDialog()
        setupMorganDialog()


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
        binding.frgGameFabAction1.setDebounceClickListener {

            when (viewModel.game.value.gameState.type) {
                GameStateTypes.Turn -> { //dig or clean
                    if (viewModel.game.value.currentPlayerId == viewModel.currentUserId) {
                        val moving = binding.frgGameBoard.canDigInCurrentPosition()
                        val size = binding.frgGameBoard.getSize()
                        val card = viewModel.game.value.cards.firstOrNull {
                            it.layer == moving.layer &&
                                    it.cardNum == moving.position.y * size + moving.position.x + 1
                        }
                        if (card != null) {
                            if (moving.canBet) {
                                viewModel.setGameState(
                                    GameState(
                                        type = GameStateTypes.Wheel,
                                        param2 = moving.layer,
                                        card = card
                                    )
                                )
                                viewModel.updateBidInfo(
                                    WheelBet(
                                        playerId = viewModel.currentUserId ?: "",
                                        skipped = true
                                    )
                                )

                            } else {
                                //todo set state depending on the card (question, tooth)
                                if (card.type == CardType.Question) {
                                    viewModel.setGameState(
                                        GameState(
                                            type = GameStateTypes.Question,
                                            card = card
                                        )
                                    ) //todo do we need params here?
                                } else {
                                    viewModel.setGameState(GameState(type = GameStateTypes.Tooth)) //todo do we need params here?
                                }
                            }
                        }
                    }
                }
                GameStateTypes.Wheel -> {
                    if (viewModel.game.value.currentPlayerId != viewModel.currentUserId) {
                        val wheelBet = when {
                            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans1.isActivated -> WheelBet(
                                playerId = viewModel.currentUserId ?: "",
                                type = WheelBetType.BlackBean,
                                number = 1,
                                amountBid = viewModel.bidAmount,
                                skipped = false
                            )
                            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans2.isActivated -> WheelBet(
                                playerId = viewModel.currentUserId ?: "",
                                type = WheelBetType.BlackBean,
                                number = 2,
                                amountBid = viewModel.bidAmount,
                                skipped = false
                            )
                            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans3.isActivated -> WheelBet(
                                playerId = viewModel.currentUserId ?: "",
                                type = WheelBetType.BlackBean,
                                number = 3,
                                amountBid = viewModel.bidAmount,
                                skipped = false
                            )
                            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans1.isActivated -> WheelBet(
                                playerId = viewModel.currentUserId ?: "",
                                type = WheelBetType.WhiteBean,
                                number = 1,
                                amountBid = viewModel.bidAmount,
                                skipped = false
                            )
                            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans2.isActivated -> WheelBet(
                                playerId = viewModel.currentUserId ?: "",
                                type = WheelBetType.WhiteBean,
                                number = 2,
                                amountBid = viewModel.bidAmount,
                                skipped = false
                            )
                            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans3.isActivated -> WheelBet(
                                playerId = viewModel.currentUserId ?: "",
                                type = WheelBetType.WhiteBean,
                                number = 3,
                                amountBid = viewModel.bidAmount,
                                skipped = false
                            )
                            binding.frgGameWheelLayout.includeWheelDialogIvDragon1.isActivated -> WheelBet(
                                playerId = viewModel.currentUserId ?: "",
                                type = WheelBetType.Dragon,
                                number = 1,
                                amountBid = viewModel.bidAmount,
                                skipped = false
                            )
                            binding.frgGameWheelLayout.includeWheelDialogIvDragon2.isActivated -> WheelBet(
                                playerId = viewModel.currentUserId ?: "",
                                type = WheelBetType.Dragon,
                                number = 2,
                                amountBid = viewModel.bidAmount,
                                skipped = false
                            )
                            else -> throw IllegalStateException("Sector not chosen!")
                        }
                        viewModel.updateBidInfo(wheelBet)
                    }
                }
                GameStateTypes.Question -> {
                    //todo handle answer
                    val list = listOf(
                        binding.frgGameQuestionLayout.includeQuestionDialogCvAnswer1Wrapper,
                        binding.frgGameQuestionLayout.includeQuestionDialogCvAnswer2Wrapper,
                        binding.frgGameQuestionLayout.includeQuestionDialogCvAnswer3Wrapper,
                        binding.frgGameQuestionLayout.includeQuestionDialogCvAnswer4Wrapper,
                    )
                    var selectedAnswer = 0
                    var selectedCard =
                        binding.frgGameQuestionLayout.includeQuestionDialogCvAnswer1Wrapper
                    list.forEachIndexed { index, materialCardView ->
                        if (materialCardView.isChecked) {
                            selectedAnswer = index + 1
                            selectedCard = materialCardView
                        }
                    }

                    if (selectedAnswer == 0) {
                        Toast.makeText(requireContext(), R.string.choose_answer, Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        val card = viewModel.game.value.gameState.card
                        if (card != null && card.question?.question?.correctAnswer == selectedAnswer) {
                            viewModel.setGameState(
                                GameState(
                                    type = GameStateTypes.Question,
                                    card = card,
                                    param1 = selectedAnswer
                                )
                            )
                            viewModel.updateCard(card.copy(cleared = true))
                            selectedCard.setCorrectStyle()
                            list.forEach {
                                if (it != selectedCard) {
                                    it.setToDisabledStyle()
                                }
                            }
                        } else if (card != null) {
                            viewModel.setGameState(
                                GameState(
                                    type = GameStateTypes.Question,
                                    card = card,
                                    param1 = selectedAnswer
                                )
                            )
                            val newAlreadyAnswered = card.question?.alreadyAnswered ?: mutableListOf()
                            newAlreadyAnswered[selectedAnswer] = selectedAnswer
                            viewModel.updateCard(
                                card.copy(
                                    question = card.question?.copy(
                                        alreadyAnswered = newAlreadyAnswered
                                    ),
                                    cleared = false
                                )
                            )
                            selectedCard.setIncorrectStyle()
                            list.forEach {
                                if (it != selectedCard) {
                                    it.setToDisabledStyle()
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.frgGameFabAction2.setDebounceClickListener {
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

        binding.frgGameFabAction3.setDebounceClickListener {
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
                            val card = viewModel.game.value.cards.firstOrNull {
                                it.layer == moving.layer &&
                                        it.cardNum == moving.position.y * size + moving.position.x + 1
                            }
                            if (card != null) {
                                if (moving.canBet) {
                                    viewModel.setGameState(
                                        GameState(
                                            type = GameStateTypes.Wheel,
                                            param2 = moving.layer,
                                            card = card
                                        )
                                    )
                                    viewModel.updateBidInfo(
                                        WheelBet(
                                            playerId = viewModel.currentUserId ?: "",
                                            skipped = true
                                        )
                                    )

                                } else {
                                    //todo set state depending on the card (question, tooth)
                                    if (card.type == CardType.Question) {
                                        viewModel.setGameState(
                                            GameState(
                                                type = GameStateTypes.Question,
                                                card = card
                                            )
                                        ) //todo do we need params here?
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

        binding.frgGameFabAction4.setDebounceClickListener {
            when (viewModel.game.value.gameState.type) {
                GameStateTypes.Turn -> { //skip
                    showConfirmationDialog(actionOk = {
                        viewModel.passTurnToNextPlayer()
                    }, actionCancel = {}, text = getString(R.string.skip_confirmation))
                }
                GameStateTypes.Wheel -> { //skip
                    if (viewModel.game.value.currentPlayerId != viewModel.currentUserId) {
                        showConfirmationDialog(actionOk = {
                            viewModel.updateBidInfo(
                                WheelBet(
                                    playerId = viewModel.currentUserId ?: "", skipped = true
                                )
                            )
                        }, actionCancel = {}, text = getString(R.string.bet_skip_confirmation))
                    }
                }
                GameStateTypes.Question -> { //ask morgan
                    if (viewModel.game.value.currentPlayerId == viewModel.currentUserId) {
                        val morganAnswer = Random().nextInt(4) + 1
                        val card = viewModel.game.value.gameState.card
                        when (morganAnswer) {
                            1, 3 -> { //gives right answer
                                binding.frgGameMorganAnswerLayout.includeMorganAnswerTvAnswer.text =
                                    getString(
                                        R.string.morgan_question_answer,
                                        card?.question?.question?.correctAnswer
                                    )
                            }
                            2 -> { // gives wrong answer
                                val possibilities = mutableListOf(1, 2, 3, 4)
                                possibilities.remove(card?.question?.question?.correctAnswer)
                                card?.question?.alreadyAnswered?.forEach {
                                    possibilities.remove(it)
                                }
                                possibilities.shuffle()
                                binding.frgGameMorganAnswerLayout.includeMorganAnswerTvAnswer.text =
                                    getString(
                                        R.string.morgan_question_answer,
                                        possibilities.first()
                                    )
                            }
                            4 -> { // gives coin
                                binding.frgGameMorganAnswerLayout.includeMorganAnswerTvAnswer.setText(
                                    R.string.morgan_coin_answer
                                )
                                viewModel.giveCurrentPlayerCoins(1, 1)
                            }
                        }
                        binding.frgGameMorganAnswerLayout.root.visibility = View.VISIBLE
                        binding.frgGameQuestionLayout.root.visibility = View.GONE
                        binding.frgGameFabAction1.visibility = View.INVISIBLE
                        binding.frgGameFabAction4.visibility = View.INVISIBLE
                        viewModel.setupAndStartDelayTimer(5, onFinishCallback = {
                            binding.frgGameMorganAnswerLayout.root.visibility = View.GONE
                            binding.frgGameQuestionLayout.root.visibility = View.VISIBLE
                            binding.frgGameFabAction1.visibility = View.VISIBLE
                        }, onTickCallback = {
                            binding.frgGameMorganAnswerLayout.includeMorganAnswerMbClose.text =
                                getString(R.string.ok_delay, it)
                        })
                    }
                }
            }
        }

        binding.frgGameMorganAnswerLayout.includeMorganAnswerMbClose.setDebounceClickListener {
            viewModel.delayTimer?.onFinish()
            viewModel.delayTimer?.cancel()
        }

        handleBackPressed {  }

        binding.frgGameFabAction1.visibility = View.GONE
        binding.frgGameFabAction2.visibility = View.GONE
        binding.frgGameFabAction3.visibility = View.GONE
        binding.frgGameFabAction4.visibility = View.GONE
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
        //_binding = null
        super.onDestroyView()
    }

    //------------------------------------------------------------------------------ Main handler for the game
    private fun handleGameData(game: Game) {
        if (binding.frgGameBoard.getPlayersAmount() != game.players.values.size) {
            game.players.values.forEach {
                binding.frgGameBoard.addPlayer(it.toPlayer())
            }
        }
        val currPlayer = viewModel.game.value.players[viewModel.currentUserId]
        (statisticsBinding.root.background as GradientDrawable).setStroke(
            resources.getDimension(R.dimen.dp_1).toInt(),
            ContextCompat.getColor(
                requireContext(),
                TeamsConstants.getTeamFromString(currPlayer?.teamStr ?: "").colorRes
            )
        )
        binding.frgGameTeamStatsWrapper.strokeColor = ContextCompat.getColor(
            requireContext(),
            TeamsConstants.getTeamFromString(currPlayer?.teamStr ?: "").colorRes
        )
        binding.frgGameInventoryWrapper.strokeColor = ContextCompat.getColor(
            requireContext(),
            TeamsConstants.getTeamFromString(currPlayer?.teamStr ?: "").colorRes
        )

        determineMorganPosition(game)
        binding.frgGameBoard.setCurrentPlayer(viewModel.currentUserId ?: "")
        binding.frgGameBoard.updatePlayers(game.players.mapValues { it.value.toPlayer() } as HashMap<String, Player>)
        binding.frgGameBoard.setActiveTurnPlayer(game.currentPlayerId)
        binding.frgGameBoard.clearArrowCallback()
        binding.frgGameBoard.setCardList(game.cards)
        binding.frgGameDiceLayout.root.visibility = View.GONE
        binding.frgGameWheelLayout.root.visibility = View.GONE
        binding.frgGameQuestionLayout.root.visibility = View.GONE
        binding.frgGameFabActionDecrease.visibility = View.GONE
        binding.frgGameFabActionIncrease.visibility = View.GONE
        binding.frgGameIvCoin.visibility = View.GONE
        binding.frgGameTvBidAmount.visibility = View.GONE

        when (game.gameState.type) {
            GameStateTypes.Start -> {
                handleStartGameState(game)
            }
            GameStateTypes.Turn -> {
                handleTurnGameState(game)
            }
            GameStateTypes.Wheel -> {
                handleWheelGameState(game)
            }
            GameStateTypes.Question -> {
                handleQuestionGameState(game)
            }
        }
    }

    //------------------------------------------------------------------------------ Start handler
    private fun handleStartGameState(game: Game) {
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

    //------------------------------------------------------------------------------ Turn handler
    private fun handleTurnGameState(game: Game) {
        clearWheelInfo()

        binding.frgGameDiceLayout.root.visibility = View.GONE
        val activePlayersTeam = TeamsConstants.getTeamFromString(
            game.players[game.currentPlayerId]?.teamStr ?: ""
        )
        if (game.currentPlayerId == viewModel.currentUserId) {
            binding.frgGameTvAction.setText(R.string.your_turn)
            binding.frgGameFabAction4.setImageResource(R.drawable.ic_close_24)
            binding.frgGameFabAction4.setMaxImageSize(resources.getDimension(R.dimen.dp_24).toInt())
            binding.frgGameBoard.clearSelectedMovingDirection()
            binding.frgGameBoard.determineDiggingAvailability()
            val canDig = binding.frgGameBoard.canDigInCurrentPosition()
            if (canDig.canDig) {
                binding.frgGameFabAction1.visibility = View.VISIBLE
                binding.frgGameFabAction1.setImageResource(
                    if (canDig.isCleaning) R.drawable.ic_brush_24 else R.drawable.ic_shovel_24
                )
            } else {
                binding.frgGameFabAction1.visibility = View.INVISIBLE
            }

            if (binding.frgGameBoard.getSelectedMovingDirection() == null) {
                binding.frgGameFabAction2.visibility = View.INVISIBLE
                binding.frgGameFabAction3.visibility = View.INVISIBLE
            } else {
                binding.frgGameFabAction2.visibility = View.VISIBLE
                binding.frgGameFabAction3.visibility = View.VISIBLE
            }

            binding.frgGameFabAction4.visibility = View.VISIBLE

            binding.frgGameBoard.setArrowCallback { direction, movingPossibilities ->
                binding.frgGameFabAction2.visibility = View.VISIBLE
                if (movingPossibilities.canDig) {
                    binding.frgGameFabAction3.visibility = View.VISIBLE
                    binding.frgGameFabAction3.setImageResource(
                        if (movingPossibilities.isCleaning)
                            R.drawable.ic_walk_and_clean_24
                        else
                            R.drawable.ic_walk_and_dig_24
                    )
                } else {
                    binding.frgGameFabAction3.visibility = View.INVISIBLE
                }
            }
            //todo calculate where we can go, then clear this info when turn ends
        } else {
            binding.frgGameBoard.clearArrowCallback()
            binding.frgGameTvAction.text =
                getString(R.string.players_turn, game.players[game.currentPlayerId]?.name)
            binding.frgGameFabAction1.visibility = View.INVISIBLE
            binding.frgGameFabAction2.visibility = View.INVISIBLE
            binding.frgGameFabAction3.visibility = View.INVISIBLE
            binding.frgGameFabAction4.visibility = View.INVISIBLE
        }
        binding.frgGameTvAction.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                activePlayersTeam.colorRes
            )
        )
    }

    //------------------------------------------------------------------------------ Wheel handler
    private fun handleWheelGameState(game: Game) {
        binding.frgGameWheelLayout.root.visibility = View.VISIBLE
        binding.frgGameFabAction1.setImageResource(R.drawable.ic_check_24)
        binding.frgGameFabAction4.setImageResource(R.drawable.ic_close_24)
        binding.frgGameFabAction4.setMaxImageSize(resources.getDimension(R.dimen.dp_24).toInt())
        binding.frgGameFabAction1.visibility = View.VISIBLE
        binding.frgGameFabAction4.visibility = View.VISIBLE
        binding.frgGameFabAction2.visibility = View.INVISIBLE
        binding.frgGameFabAction3.visibility = View.INVISIBLE
        binding.frgGameFabActionDecrease.visibility = View.VISIBLE
        binding.frgGameFabActionIncrease.visibility = View.VISIBLE
        binding.frgGameIvCoin.visibility = View.VISIBLE
        binding.frgGameTvBidAmount.visibility = View.VISIBLE

        binding.frgGameTvBidAmount.text = viewModel.bidAmount.toString()

        val wheelBackground = binding.frgGameWheelLayout.root.background as GradientDrawable
        val activePlayer = viewModel.game.value.players[viewModel.game.value.currentPlayerId]
        wheelBackground.setStroke(
            resources.getDimension(R.dimen.dp_1).toInt(),
            ContextCompat.getColor(
                requireContext(),
                TeamsConstants.getTeamFromString(activePlayer?.teamStr ?: "").colorRes
            )
        )

        binding.frgGameTvAction.setText(R.string.test_luck)
        binding.frgGameTvAction.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                TeamsConstants.getTeamFromString(activePlayer?.teamStr ?: "").colorRes
            )
        )

        binding.frgGameFabAction1.isEnabled = false
        val sectorsList = listOf(
            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans1,
            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans2,
            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans3,
            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans1,
            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans2,
            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans3,
            binding.frgGameWheelLayout.includeWheelDialogIvDragon1,
            binding.frgGameWheelLayout.includeWheelDialogIvDragon2,
        )
        sectorsList.forEach {
            if (it.isActivated && viewModel.bidAmount > 0) {
                binding.frgGameFabAction1.isEnabled = true
            }
        }

        val betMap = game.gameState.bidInfo
        val layer = game.gameState.param2 as? Long
        val card = game.gameState.card

        if (layer != null) {
            val centralBackground = GradientDrawable()
            val colorRes = when (layer) {
                1L -> R.color.color_layer_yellow
                2L -> R.color.color_layer_orange
                3L -> R.color.color_layer_red
                4L -> R.color.color_layer_blue
                5L -> R.color.color_layer_purple
                else -> throw IllegalStateException("Wrong layer in $TAG")
            }
            centralBackground.setStroke(
                resources.getDimension(R.dimen.dp_1).toInt(),
                ContextCompat.getColor(requireContext(), R.color.stroke_color_secondary)
            )
            centralBackground.shape = GradientDrawable.OVAL
            centralBackground.color = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    colorRes
                )
            )
            binding.frgGameWheelLayout.includeWheelDialogCentralView.background =
                centralBackground
        }
        if (betMap != null && betMap.isNotEmpty() && card != null) {

            // if all players made their move -> next state
            if (betMap.size == game.players.size && viewModel.currentUserId == game.currentPlayerId) {
                if (card.type == CardType.Question) {
                    viewModel.setGameState(
                        GameState(
                            type = GameStateTypes.Question,
                            card = card
                        )
                    ) //todo do we need params here?
                } else {
                    viewModel.setGameState(GameState(type = GameStateTypes.Tooth)) //todo do we need params here?
                }
            }

            if (betMap.containsKey(viewModel.currentUserId) ||
                viewModel.currentUserId == game.currentPlayerId
            ) {  // if already bid, you cant do anything
                sectorsList.forEach {
                    it.isEnabled = false
                }
                binding.frgGameFabAction1.visibility = View.INVISIBLE
                binding.frgGameFabAction2.visibility = View.INVISIBLE
                binding.frgGameFabAction3.visibility = View.INVISIBLE
                binding.frgGameFabAction4.visibility = View.INVISIBLE
                binding.frgGameFabActionDecrease.visibility = View.INVISIBLE
                binding.frgGameFabActionIncrease.visibility = View.INVISIBLE
                binding.frgGameIvCoin.visibility = View.INVISIBLE
                binding.frgGameTvBidAmount.visibility = View.INVISIBLE
                binding.frgGameTvAction.setText(R.string.bets_results)
            }

            blockFullWheelSectors(betMap)
            displayBidCoins(betMap)

        }
    }

    //--------------------------------------------------------------------------------- Question Handler
    private fun handleQuestionGameState(game: Game) {
        val questionBackground = binding.frgGameQuestionLayout.root.background as GradientDrawable
        val morganBackground = binding.frgGameMorganAnswerLayout.root.background as GradientDrawable
        val activePlayer = game.players[game.currentPlayerId]
        questionBackground.setStroke(
            resources.getDimension(R.dimen.dp_1).toInt(),
            ContextCompat.getColor(
                requireContext(),
                TeamsConstants.getTeamFromString(activePlayer?.teamStr ?: "").colorRes
            )
        )
        morganBackground.setStroke(
            resources.getDimension(R.dimen.dp_1).toInt(),
            ContextCompat.getColor(
                requireContext(),
                TeamsConstants.getTeamFromString(activePlayer?.teamStr ?: "").colorRes
            )
        )

        val question = game.gameState.card?.question ?: return

        binding.frgGameQuestionLayout.root.visibility = View.VISIBLE
        binding.frgGameQuestionLayout.includeQuestionDialogTvQuestion.text =
            question.question.questionText
        binding.frgGameQuestionLayout.includeQuestionDialogTvAnswer1.text =
            question.question.answer1
        binding.frgGameQuestionLayout.includeQuestionDialogTvAnswer2.text =
            question.question.answer2
        binding.frgGameQuestionLayout.includeQuestionDialogTvAnswer3.text =
            question.question.answer3
        binding.frgGameQuestionLayout.includeQuestionDialogTvAnswer4.text =
            question.question.answer4
        binding.frgGameQuestionLayout.includeQuestionDialogTvQuestionNumber.setText(
            when (game.gameState.card.layer) {
                1 -> R.string.layer_1_question_title
                2 -> R.string.layer_2_question_title
                3 -> R.string.layer_3_question_title
                4 -> R.string.layer_4_question_title
                5 -> R.string.layer_5_question_title
                else -> R.string.question
            }
        )

        val wrapperList = listOf(
            binding.frgGameQuestionLayout.includeQuestionDialogCvAnswer1Wrapper,
            binding.frgGameQuestionLayout.includeQuestionDialogCvAnswer2Wrapper,
            binding.frgGameQuestionLayout.includeQuestionDialogCvAnswer3Wrapper,
            binding.frgGameQuestionLayout.includeQuestionDialogCvAnswer4Wrapper
        )

        val answeredNum = game.gameState.param1 as? Long

        if (game.currentPlayerId == viewModel.currentUserId) { // current user is answering
            binding.frgGameFabAction1.visibility = View.VISIBLE
            binding.frgGameFabAction1.isEnabled = false
            binding.frgGameFabAction1.setImageResource(R.drawable.ic_check_24)
            binding.frgGameFabAction2.visibility = View.INVISIBLE
            binding.frgGameFabAction3.visibility = View.INVISIBLE
            binding.frgGameFabAction4.visibility = if (
                binding.frgGameBoard.canAskMorganInPosition(activePlayer?.position ?: Position())
            ) View.VISIBLE else View.INVISIBLE
            binding.frgGameFabAction4.setMaxImageSize(resources.getDimension(R.dimen.dp_30).toInt())
            binding.frgGameFabAction4.setImageResource(R.drawable.morgan)
            wrapperList.forEachIndexed { index, materialCardView ->
                if (game.gameState.card.question?.alreadyAnswered?.contains(index + 1) == true) {
                    materialCardView.setToDisabledStyle()
                }
            }

            if (answeredNum != null) { //start timer to change state
                wrapperList[(answeredNum - 1).toInt()].setIncorrectStyle()
                wrapperList.forEachIndexed { index, materialCardView ->
                    if (index != answeredNum.toInt() - 1) {
                        materialCardView.setToDisabledStyle()
                    }
                }
                viewModel.setupAndStartDelayTimer(3, onFinishCallback = {
                    viewModel.setGameState(GameState(GameStateTypes.Turn))
                    viewModel.passTurnToNextPlayer()
                }, onTickCallback = {})
            }
        } else { // for other users
            binding.frgGameFabAction1.visibility = View.INVISIBLE
            binding.frgGameFabAction2.visibility = View.INVISIBLE
            binding.frgGameFabAction3.visibility = View.INVISIBLE
            binding.frgGameFabAction4.visibility = View.INVISIBLE
            wrapperList.forEach {
                it.setToDisabledStyle()
            }
            if (answeredNum != null) {
                wrapperList[(answeredNum - 1).toInt()].setIncorrectStyle()
            }
        }
    }

    private fun clearWheelInfo() {
        viewModel.bidAmount = 0
        listOf(
            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans1,
            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans2,
            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans3,
            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans1,
            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans2,
            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans3,
            binding.frgGameWheelLayout.includeWheelDialogIvDragon1,
            binding.frgGameWheelLayout.includeWheelDialogIvDragon2,
        ).forEach {
            it.isEnabled = true
            it.isActivated = false
        }
    }

    private fun blockFullWheelSectors(map: HashMap<String, WheelBet>) {
        val black1Amount =
            map.values.count { it.type == WheelBetType.BlackBean && it.number == 1 && !it.skipped }
        val black2Amount =
            map.values.count { it.type == WheelBetType.BlackBean && it.number == 2 && !it.skipped }
        val black3Amount =
            map.values.count { it.type == WheelBetType.BlackBean && it.number == 3 && !it.skipped }
        val white1Amount =
            map.values.count { it.type == WheelBetType.WhiteBean && it.number == 1 && !it.skipped }
        val white2Amount =
            map.values.count { it.type == WheelBetType.WhiteBean && it.number == 2 && !it.skipped }
        val white3Amount =
            map.values.count { it.type == WheelBetType.WhiteBean && it.number == 3 && !it.skipped }
        val dragon1Amount =
            map.values.count { it.type == WheelBetType.Dragon && it.number == 1 && !it.skipped }
        val dragon2Amount =
            map.values.count { it.type == WheelBetType.Dragon && it.number == 2 && !it.skipped }

        if (black1Amount >= 3) {
            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans1.isEnabled = false
            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans1.isActivated = false
        }
        if (black2Amount >= 3) {
            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans2.isEnabled = false
            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans2.isActivated = false
        }
        if (black3Amount >= 3) {
            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans3.isEnabled = false
            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans3.isActivated = false
        }
        if (white1Amount >= 3) {
            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans1.isEnabled = false
            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans1.isActivated = false
        }
        if (white2Amount >= 3) {
            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans2.isEnabled = false
            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans2.isActivated = false
        }
        if (white3Amount >= 3) {
            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans3.isEnabled = false
            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans3.isActivated = false
        }
        if (dragon1Amount >= 3) {
            binding.frgGameWheelLayout.includeWheelDialogIvDragon1.isEnabled = false
            binding.frgGameWheelLayout.includeWheelDialogIvDragon1.isActivated = false
        }
        if (dragon2Amount >= 3) {
            binding.frgGameWheelLayout.includeWheelDialogIvDragon2.isEnabled = false
            binding.frgGameWheelLayout.includeWheelDialogIvDragon2.isActivated = false
        }


        binding.frgGameFabAction1.isEnabled = false
        listOf(
            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans1,
            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans2,
            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans3,
            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans1,
            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans2,
            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans3,
            binding.frgGameWheelLayout.includeWheelDialogIvDragon1,
            binding.frgGameWheelLayout.includeWheelDialogIvDragon2,
        ).forEach {
            if (it.isActivated && viewModel.bidAmount > 0) {
                binding.frgGameFabAction1.isEnabled = true
            }
        }
    }

    private fun displayBidCoins(map: HashMap<String, WheelBet>) {
        map.forEach {
            if (it.value.skipped) {
                return@forEach
            }
            val player = viewModel.game.value.players[it.key]
            val viewBackground = GradientDrawable()
            viewBackground.setStroke(
                resources.getDimension(R.dimen.dp_1).toInt(),
                ContextCompat.getColor(requireContext(), R.color.stroke_color_secondary)
            )
            viewBackground.shape = GradientDrawable.OVAL

            viewBackground.color = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    TeamsConstants.getTeamFromString(player?.teamStr ?: "").colorRes
                )
            )
            when (it.value.type) {
                WheelBetType.WhiteBean -> {
                    when (it.value.number) {
                        1 -> {
                            if (binding.frgGameWheelLayout.includeWheelDialogWhiteBeans1View1.visibility == View.VISIBLE) {
                                if (binding.frgGameWheelLayout.includeWheelDialogWhiteBeans1View2.visibility == View.VISIBLE) {
                                    binding.frgGameWheelLayout.includeWheelDialogWhiteBeans1View3.background =
                                        viewBackground
                                    binding.frgGameWheelLayout.includeWheelDialogWhiteBeans1View3.visibility =
                                        View.VISIBLE
                                } else {
                                    binding.frgGameWheelLayout.includeWheelDialogWhiteBeans1View2.background =
                                        viewBackground
                                    binding.frgGameWheelLayout.includeWheelDialogWhiteBeans1View2.visibility =
                                        View.VISIBLE
                                }
                            } else {
                                binding.frgGameWheelLayout.includeWheelDialogWhiteBeans1View1.background =
                                    viewBackground
                                binding.frgGameWheelLayout.includeWheelDialogWhiteBeans1View1.visibility =
                                    View.VISIBLE
                            }
                        }
                        2 -> {
                            if (binding.frgGameWheelLayout.includeWheelDialogWhiteBeans2View1.visibility == View.VISIBLE) {
                                if (binding.frgGameWheelLayout.includeWheelDialogWhiteBeans2View2.visibility == View.VISIBLE) {
                                    binding.frgGameWheelLayout.includeWheelDialogWhiteBeans2View3.background =
                                        viewBackground
                                    binding.frgGameWheelLayout.includeWheelDialogWhiteBeans2View3.visibility =
                                        View.VISIBLE
                                } else {
                                    binding.frgGameWheelLayout.includeWheelDialogWhiteBeans2View2.background =
                                        viewBackground
                                    binding.frgGameWheelLayout.includeWheelDialogWhiteBeans2View2.visibility =
                                        View.VISIBLE
                                }
                            } else {
                                binding.frgGameWheelLayout.includeWheelDialogWhiteBeans2View1.background =
                                    viewBackground
                                binding.frgGameWheelLayout.includeWheelDialogWhiteBeans2View1.visibility =
                                    View.VISIBLE
                            }
                        }
                        3 -> {
                            if (binding.frgGameWheelLayout.includeWheelDialogWhiteBeans3View1.visibility == View.VISIBLE) {
                                if (binding.frgGameWheelLayout.includeWheelDialogWhiteBeans3View2.visibility == View.VISIBLE) {
                                    binding.frgGameWheelLayout.includeWheelDialogWhiteBeans3View3.background =
                                        viewBackground
                                    binding.frgGameWheelLayout.includeWheelDialogWhiteBeans3View3.visibility =
                                        View.VISIBLE
                                } else {
                                    binding.frgGameWheelLayout.includeWheelDialogWhiteBeans3View2.background =
                                        viewBackground
                                    binding.frgGameWheelLayout.includeWheelDialogWhiteBeans3View2.visibility =
                                        View.VISIBLE
                                }
                            } else {
                                binding.frgGameWheelLayout.includeWheelDialogWhiteBeans3View1.background =
                                    viewBackground
                                binding.frgGameWheelLayout.includeWheelDialogWhiteBeans3View1.visibility =
                                    View.VISIBLE
                            }
                        }
                    }
                }
                WheelBetType.BlackBean -> {
                    when (it.value.number) {
                        1 -> {
                            if (binding.frgGameWheelLayout.includeWheelDialogBlackBeans1View1.visibility == View.VISIBLE) {
                                if (binding.frgGameWheelLayout.includeWheelDialogBlackBeans1View2.visibility == View.VISIBLE) {
                                    binding.frgGameWheelLayout.includeWheelDialogBlackBeans1View3.background =
                                        viewBackground
                                    binding.frgGameWheelLayout.includeWheelDialogBlackBeans1View3.visibility =
                                        View.VISIBLE
                                } else {
                                    binding.frgGameWheelLayout.includeWheelDialogBlackBeans1View2.background =
                                        viewBackground
                                    binding.frgGameWheelLayout.includeWheelDialogBlackBeans1View2.visibility =
                                        View.VISIBLE
                                }
                            } else {
                                binding.frgGameWheelLayout.includeWheelDialogBlackBeans1View1.background =
                                    viewBackground
                                binding.frgGameWheelLayout.includeWheelDialogBlackBeans1View1.visibility =
                                    View.VISIBLE
                            }
                        }
                        2 -> {
                            if (binding.frgGameWheelLayout.includeWheelDialogBlackBeans2View1.visibility == View.VISIBLE) {
                                if (binding.frgGameWheelLayout.includeWheelDialogBlackBeans2View2.visibility == View.VISIBLE) {
                                    binding.frgGameWheelLayout.includeWheelDialogBlackBeans2View3.background =
                                        viewBackground
                                    binding.frgGameWheelLayout.includeWheelDialogBlackBeans2View3.visibility =
                                        View.VISIBLE
                                } else {
                                    binding.frgGameWheelLayout.includeWheelDialogBlackBeans2View2.background =
                                        viewBackground
                                    binding.frgGameWheelLayout.includeWheelDialogBlackBeans2View2.visibility =
                                        View.VISIBLE
                                }
                            } else {
                                binding.frgGameWheelLayout.includeWheelDialogBlackBeans2View1.background =
                                    viewBackground
                                binding.frgGameWheelLayout.includeWheelDialogBlackBeans2View1.visibility =
                                    View.VISIBLE
                            }
                        }
                        3 -> {
                            if (binding.frgGameWheelLayout.includeWheelDialogBlackBeans3View1.visibility == View.VISIBLE) {
                                if (binding.frgGameWheelLayout.includeWheelDialogBlackBeans3View2.visibility == View.VISIBLE) {
                                    binding.frgGameWheelLayout.includeWheelDialogBlackBeans3View3.background =
                                        viewBackground
                                    binding.frgGameWheelLayout.includeWheelDialogBlackBeans3View3.visibility =
                                        View.VISIBLE
                                } else {
                                    binding.frgGameWheelLayout.includeWheelDialogBlackBeans3View2.background =
                                        viewBackground
                                    binding.frgGameWheelLayout.includeWheelDialogBlackBeans3View2.visibility =
                                        View.VISIBLE
                                }
                            } else {
                                binding.frgGameWheelLayout.includeWheelDialogBlackBeans3View1.background =
                                    viewBackground
                                binding.frgGameWheelLayout.includeWheelDialogBlackBeans3View1.visibility =
                                    View.VISIBLE
                            }
                        }
                    }
                }
                WheelBetType.Dragon -> {
                    when (it.value.number) {
                        1 -> {
                            if (binding.frgGameWheelLayout.includeWheelDialogDragon1View1.visibility == View.VISIBLE) {
                                if (binding.frgGameWheelLayout.includeWheelDialogDragon1View2.visibility == View.VISIBLE) {
                                    binding.frgGameWheelLayout.includeWheelDialogDragon1View3.background =
                                        viewBackground
                                    binding.frgGameWheelLayout.includeWheelDialogDragon1View3.visibility =
                                        View.VISIBLE
                                } else {
                                    binding.frgGameWheelLayout.includeWheelDialogDragon1View2.background =
                                        viewBackground
                                    binding.frgGameWheelLayout.includeWheelDialogDragon1View2.visibility =
                                        View.VISIBLE
                                }
                            } else {
                                binding.frgGameWheelLayout.includeWheelDialogDragon1View1.background =
                                    viewBackground
                                binding.frgGameWheelLayout.includeWheelDialogDragon1View1.visibility =
                                    View.VISIBLE
                            }
                        }
                        2 -> {
                            if (binding.frgGameWheelLayout.includeWheelDialogDragon2View1.visibility == View.VISIBLE) {
                                if (binding.frgGameWheelLayout.includeWheelDialogDragon2View2.visibility == View.VISIBLE) {
                                    binding.frgGameWheelLayout.includeWheelDialogDragon2View3.background =
                                        viewBackground
                                    binding.frgGameWheelLayout.includeWheelDialogDragon2View3.visibility =
                                        View.VISIBLE
                                } else {
                                    binding.frgGameWheelLayout.includeWheelDialogDragon2View2.background =
                                        viewBackground
                                    binding.frgGameWheelLayout.includeWheelDialogDragon2View2.visibility =
                                        View.VISIBLE
                                }
                            } else {
                                binding.frgGameWheelLayout.includeWheelDialogDragon2View1.background =
                                    viewBackground
                                binding.frgGameWheelLayout.includeWheelDialogDragon2View1.visibility =
                                    View.VISIBLE
                            }
                        }
                    }
                }
            }
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
            it.visibility = View.GONE
        }

        val list = listOf(
            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans1,
            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans2,
            binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans3,
            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans1,
            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans2,
            binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans3,
            binding.frgGameWheelLayout.includeWheelDialogIvDragon1,
            binding.frgGameWheelLayout.includeWheelDialogIvDragon2,
        )
        list.forEach {
            it.setDebounceClickListener {
                list.forEach {
                    it.isActivated = false
                }
                it.isActivated = true
                binding.frgGameFabAction1.isEnabled = true
            }
        }

        //todo check players coins total amount
        binding.frgGameFabActionDecrease.setDebounceClickListener {
            if (viewModel.bidAmount > 0) {
                viewModel.bidAmount--
                binding.frgGameTvBidAmount.text = viewModel.bidAmount.toString()
                if (viewModel.bidAmount == 0) {
                    binding.frgGameFabAction1.isEnabled = false
                }
            }
        }

        binding.frgGameFabActionIncrease.setDebounceClickListener {
            val player = viewModel.game.value.players[viewModel.currentUserId]
            var totalYellowCoins = 0
            player?.coinsCollected?.forEach {
                totalYellowCoins += it.key.toInt() * it.value
            }
            if (viewModel.bidAmount < totalYellowCoins && viewModel.bidAmount < 99) {
                viewModel.bidAmount++
                binding.frgGameTvBidAmount.text = viewModel.bidAmount.toString()
                if (viewModel.bidAmount > 0) {
                    listOf(
                        binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans1,
                        binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans2,
                        binding.frgGameWheelLayout.includeWheelDialogIvBlackBeans3,
                        binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans1,
                        binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans2,
                        binding.frgGameWheelLayout.includeWheelDialogIvWhiteBeans3,
                        binding.frgGameWheelLayout.includeWheelDialogIvDragon1,
                        binding.frgGameWheelLayout.includeWheelDialogIvDragon2,
                    ).forEach {
                        if (it.isActivated && viewModel.bidAmount > 0) {
                            binding.frgGameFabAction1.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    private fun setupQuestionsDialog() {
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

        with(binding.frgGameQuestionLayout) {
            val list = listOf(
                includeQuestionDialogCvAnswer1Wrapper,
                includeQuestionDialogCvAnswer2Wrapper,
                includeQuestionDialogCvAnswer3Wrapper,
                includeQuestionDialogCvAnswer4Wrapper
            )
            list.forEachIndexed { index, card ->
                card.setToUnselectedStyle()
                card.isCheckable = true
                card.checkedIcon = null
                card.setDebounceClickListener {
                    //todo save index of clicked
                    binding.frgGameFabAction1.isEnabled = true
                    list.forEach {
                        if (it.isEnabled) {
                            it.setToUnselectedStyle()
                        }
                    }
                    card.setToSelectedStyle()
                }
            }
        }
    }

    private fun setupMorganDialog() {
        val morganBackground = GradientDrawable()
        morganBackground.setStroke(
            resources.getDimension(R.dimen.dp_1).toInt(),
            ContextCompat.getColor(requireContext(), R.color.color_team_green)
        )
        morganBackground.color = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_transparent_background
            )
        )
        morganBackground.cornerRadius = resources.getDimension(R.dimen.dp_14)
        binding.frgGameMorganAnswerLayout.root.background = morganBackground
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
                        viewModel.setupAndStartDelayTimer(1, onFinishCallback = {
                            val side = viewModel.game.value.morganPosition
                            val size = binding.frgGameBoard.getSize()
                            val position = if (dice > size) dice - size else dice
                            viewModel.setMorganStartPosition((side - 1) * size + position - 1)
                            val firstPlayer = viewModel.getFirstPlayerByMorganPosition(
                                (side - 1) * size + position - 1,
                                viewModel.game.value.players.values.toList()
                            )
                            viewModel.setActivePlayerId(firstPlayer.id) {
                                viewModel.setGameState(GameState(type = GameStateTypes.Turn))
                            }
                            viewModel.updatePlayersTurnOrders(firstPlayer.turnOrder)
                        }, onTickCallback = {
                        })
//                        lifecycleScope.launch {
//                            //delay is needed to display dice longer before closing
//                            delay(1000) //todo maybe change to timer
//                            val side = viewModel.game.value.morganPosition
//                            val size = binding.frgGameBoard.getSize()
//                            val position = if (dice > size) dice - size else dice
//                            viewModel.setMorganStartPosition((side - 1) * size + position - 1)
//                            val firstPlayer = viewModel.getFirstPlayerByMorganPosition(
//                                (side - 1) * size + position - 1,
//                                viewModel.game.value.players.values.toList()
//                            )
//                            viewModel.setActivePlayerId(firstPlayer.id) {
//                                viewModel.setGameState(GameState(type = GameStateTypes.Turn))
//                            }
//                            viewModel.updatePlayersTurnOrders(firstPlayer.turnOrder)
//                        }
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
//        adapter.bindData(
//            listOf(
//                InventoryModel(1, "", R.drawable.ic_brush_78),
//                InventoryModel(2, "", R.drawable.ic_rope_78),
//                InventoryModel(3, "", R.drawable.ic_rope_78),
//                InventoryModel(4, "", R.drawable.ic_sieve_78),
//                InventoryModel(5, "", R.drawable.ic_sieve_78),
//                InventoryModel(6, "", R.drawable.ic_brush_78),
//                InventoryModel(7, "", R.drawable.ic_sieve_78),
//                InventoryModel(8, "", R.drawable.ic_brush_78),
//                InventoryModel(9, "", R.drawable.ic_brush_78),
//            )
//        )

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