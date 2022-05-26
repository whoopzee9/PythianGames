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
import androidx.core.view.isVisible
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
import ru.spbstu.common.model.PlayerState
import ru.spbstu.common.model.PlayerStateType
import ru.spbstu.common.model.Position
import ru.spbstu.common.utils.DatabaseReferences
import ru.spbstu.common.utils.GameUtils
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
import ru.spbstu.feature.domain.model.InventoryElement
import ru.spbstu.feature.domain.model.InventoryModel
import ru.spbstu.feature.domain.model.PlayerInfo
import ru.spbstu.feature.domain.model.TeamStatistics
import ru.spbstu.feature.domain.model.ToothResult
import ru.spbstu.feature.domain.model.ToothType
import ru.spbstu.feature.domain.model.WheelBet
import ru.spbstu.feature.domain.model.WheelBetType
import ru.spbstu.feature.domain.model.toInventoryModel
import ru.spbstu.feature.domain.model.toInventoryPlayer
import ru.spbstu.feature.domain.model.toPlayer
import ru.spbstu.feature.game.presentation.adapter.InventoryAdapter
import ru.spbstu.feature.game.presentation.adapter.InventoryDialogAdapter
import ru.spbstu.feature.game.presentation.adapter.InventoryDialogItemDecoration
import ru.spbstu.feature.game.presentation.adapter.InventoryDialogPlayersAdapter
import ru.spbstu.feature.game.presentation.adapter.InventoryDialogPlayersItemDecoration
import ru.spbstu.feature.game.presentation.adapter.InventoryItemDecoration
import ru.spbstu.feature.game.presentation.adapter.TeamsStatisticsAdapter
import ru.spbstu.feature.game.presentation.adapter.TeamsStatisticsItemDecoration
import ru.spbstu.feature.game.presentation.dialog.ConfirmationDialogFragment
import java.util.*

class GameFragment : BaseFragment<GameViewModel>(
    R.layout.fragment_game,
) {

    private lateinit var inventoryAdapter: InventoryAdapter
    private lateinit var statisticsAdapter: TeamsStatisticsAdapter
    private lateinit var inventoryDialogAdapter: InventoryDialogAdapter
    private lateinit var inventoryDialogPlayersAdapter: InventoryDialogPlayersAdapter

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

        setupAdapters()
        setupStatisticsPopup()

        setupDiceDialog()
        setupWheelDialog()
        setupQuestionsDialog()
        setupMorganDialog()
        setupToothDialog()
        setupInventoryDialog()


        binding.frgGameTeamStatsWrapper.setDebounceClickListener {
            //todo change teams stats and amount of teams
            val list = mutableListOf<TeamStatistics>()
            val teams = mutableSetOf<String>()
            viewModel.game.value.players.forEach { entry ->
                teams.add(entry.value.teamStr)
            }
            teams.forEach {
                list.add(viewModel.getTeamStatistics(it))
            }
            statisticsAdapter.bindData(list)
            val currPlayer = viewModel.game.value.players[viewModel.currentUserId] ?: PlayerInfo()
            statisticsBinding.frgGameStatisticsDialogTvYellowCoinsAmount.text =
                (currPlayer.coinsCollected[GameUtils.Layers.Yellow.name] ?: 0).toString()
            statisticsBinding.frgGameStatisticsDialogTvOrangeCoinsAmount.text =
                (currPlayer.coinsCollected[GameUtils.Layers.Orange.name] ?: 0).toString()
            statisticsBinding.frgGameStatisticsDialogTvRedCoinsAmount.text =
                (currPlayer.coinsCollected[GameUtils.Layers.Red.name] ?: 0).toString()
            statisticsBinding.frgGameStatisticsDialogTvBlueCoinsAmount.text =
                (currPlayer.coinsCollected[GameUtils.Layers.Blue.name] ?: 0).toString()
            statisticsBinding.frgGameStatisticsDialogTvPurpleCoinsAmount.text =
                (currPlayer.coinsCollected[GameUtils.Layers.Purple.name] ?: 0).toString()

            statisticsBinding.frgGameStatisticsDialogTvYellowQuestionsAmount.text =
                (currPlayer.questionsAnswered[GameUtils.Layers.Yellow.name] ?: 0).toString()
            statisticsBinding.frgGameStatisticsDialogTvOrangeQuestionsAmount.text =
                (currPlayer.questionsAnswered[GameUtils.Layers.Orange.name] ?: 0).toString()
            statisticsBinding.frgGameStatisticsDialogTvRedQuestionsAmount.text =
                (currPlayer.questionsAnswered[GameUtils.Layers.Red.name] ?: 0).toString()
            statisticsBinding.frgGameStatisticsDialogTvBlueQuestionsAmount.text =
                (currPlayer.questionsAnswered[GameUtils.Layers.Blue.name] ?: 0).toString()
            statisticsBinding.frgGameStatisticsDialogTvPurpleQuestionsAmount.text =
                (currPlayer.questionsAnswered[GameUtils.Layers.Purple.name] ?: 0).toString()

            statisticsBinding.root.strokeColor = ContextCompat.getColor(
                requireContext(),
                TeamsConstants.getTeamFromString(currPlayer.teamStr).colorRes
            )

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
                                    )
                                } else {
                                    viewModel.setGameState(
                                        GameState(
                                            type = GameStateTypes.Tooth,
                                            card = card,
                                            param1 = if (card.layer < 5) ToothType.Tooth else ToothType.Bone,
                                            param2 = "",
                                            param3 = false,
                                            param4 = false
                                        )
                                    )
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
                                    param1 = selectedAnswer,
                                    bidInfo = viewModel.game.value.gameState.bidInfo
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
                                    param1 = selectedAnswer,
                                    bidInfo = viewModel.game.value.gameState.bidInfo
                                )
                            )
                            val newAlreadyAnswered =
                                card.question?.alreadyAnswered ?: mutableListOf()
                            newAlreadyAnswered.add(selectedAnswer)
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
                                        )
                                    } else {
                                        viewModel.setGameState(
                                            GameState(
                                                type = GameStateTypes.Tooth,
                                                card = card,
                                                param1 = if (card.layer < 5) ToothType.Tooth else ToothType.Bone,
                                                param2 = "",
                                                param3 = false,
                                                param4 = false
                                            )
                                        )
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

        binding.frgGameInventoryWrapper.setDebounceClickListener {
            inventoryDialogAdapter.bindData(viewModel.inventory.value)

            val currPlayer = viewModel.game.value.players[viewModel.currentUserId]
            val teamPlayers = viewModel.game.value.players.filter {
                it.key != currPlayer?.id //&& it.value.teamStr == currPlayer?.teamStr
            }.values.map { it.toInventoryPlayer() }
            inventoryDialogPlayersAdapter.bindData(teamPlayers)

            //todo determine when we can access inventory
            viewModel.questionVisible = binding.frgGameQuestionLayout.root.isVisible
            viewModel.action1Visible = binding.frgGameFabAction1.isVisible
            viewModel.action2Visible = binding.frgGameFabAction2.isVisible
            viewModel.action3Visible = binding.frgGameFabAction3.isVisible
            viewModel.action4Visible = binding.frgGameFabAction4.isVisible

            binding.frgGameQuestionLayout.root.visibility = View.GONE
            binding.frgGameFabAction1.visibility = View.INVISIBLE
            binding.frgGameFabAction2.visibility = View.INVISIBLE
            binding.frgGameFabAction3.visibility = View.INVISIBLE
            binding.frgGameFabAction4.visibility = View.INVISIBLE

            viewModel.selectedInventoryPlayer = null
            viewModel.selectedInventoryItem = null
            binding.frgGameFabActionInventoryAccept.isEnabled = false
            binding.frgGameFabActionInventoryAccept.visibility = View.VISIBLE
            binding.frgGameFabActionInventoryClose.visibility = View.VISIBLE
            binding.frgGameInventoryDialogLayout.root.visibility = View.VISIBLE
        }

        binding.frgGameFabActionInventoryAccept.setDebounceClickListener {
            if (viewModel.selectedInventoryItem != null && viewModel.selectedInventoryPlayer != null) {
                viewModel.givePlayerInventoryItem(
                    viewModel.selectedInventoryPlayer!!,
                    viewModel.selectedInventoryItem!!
                )
            }
            hideInventoryDialog()
        }

        binding.frgGameFabActionInventoryClose.setDebounceClickListener {
            hideInventoryDialog()
        }

        handleBackPressed { }

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
        val currPlayer = game.players[viewModel.currentUserId]

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

        (binding.frgGameInventoryDialogLayout.root.background as GradientDrawable).setStroke(
            resources.getDimension(R.dimen.dp_1).toInt(),
            ContextCompat.getColor(
                requireContext(),
                TeamsConstants.getTeamFromString(currPlayer?.teamStr ?: "").colorRes
            )
        )

        determineMorganPosition(game)
        binding.frgGameBoard.setCurrentPlayer(viewModel.currentUserId ?: "")
        binding.frgGameBoard.updatePlayers(game.players.mapValues { it.value.toPlayer() } as HashMap<String, Player>)
        binding.frgGameBoard.setActiveTurnPlayer(game.currentPlayerId)
        binding.frgGameBoard.clearArrowCallback()
        binding.frgGameBoard.setCardList(game.cards)

        //inventory
        val inventory = mutableListOf<InventoryModel>()
        game.players[viewModel.currentUserId]?.inventory?.values?.forEachIndexed { index, inventoryElement ->
            for (i in 0 until inventoryElement.amount) {
                inventory.add(inventoryElement.toInventoryModel((index * 20) + i))
            }
        }
        viewModel.setInventory(inventory)
        inventoryAdapter.bindData(inventory)

        //team stats
        handleTeamStats(game)

        binding.frgGameDiceLayout.root.visibility = View.GONE
        binding.frgGameWheelLayout.root.visibility = View.GONE
        binding.frgGameQuestionLayout.root.visibility = View.GONE
        binding.frgGameToothLayout.root.visibility = View.GONE
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
            GameStateTypes.Tooth -> {
                handleToothGameState(game)
            }
            GameStateTypes.MorganTurn -> {
                handleMorganTurnGameState(game)
            }
        }
    }

    private fun handleTeamStats(game: Game) {
        val coinMap = hashMapOf<String, Int>()
        var inventory = 0
        game.players.forEach {
            if (it.value.teamStr == game.players[viewModel.currentUserId]?.teamStr) {
                it.value.coinsCollected.forEach { coinEntry ->
                    val old = coinMap[coinEntry.key] ?: 0
                    coinMap[coinEntry.key] = old + coinEntry.value
                }
                inventory += it.value.inventory.size
            }
        }
        var coins = 0
        var coinsInYellow = 0
        coinMap.forEach {
            coins += it.value
            coinsInYellow += it.value * GameUtils.getLayerNumber(GameUtils.Layers.valueOf(it.key))
        }

        binding.frgGameTvTeamStatsCoinsValue.text = coins.toString()
        binding.frgGameTvTeamStatsCoinsValueInYellow.text = coinsInYellow.toString()

        val questionMap = hashMapOf<String, Int>()
        game.players.forEach {
            if (it.value.teamStr == game.players[viewModel.currentUserId]?.teamStr) {
                it.value.questionsAnswered.forEach { questionEntry ->
                    val old = questionMap[questionEntry.key] ?: 0
                    questionMap[questionEntry.key] = old + questionEntry.value
                }
            }
        }
        var questions = 0
        var questionsInYellow = 0
        questionMap.forEach {
            questions += it.value
            questionsInYellow += it.value * GameUtils.getLayerNumber(GameUtils.Layers.valueOf(it.key))
        }
        binding.frgGameTvTeamStatsQuestionsValue.text = questions.toString()
        binding.frgGameTvTeamStatsQuestionsValueInYellow.text = questionsInYellow.toString()

        binding.frgGameTvTeamStatsInventoryValue.text = inventory.toString()
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
                binding.frgGameDiceLayout.includeDiceDialogTvTitle.setText(R.string.determining_morgan_start_position)
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
                binding.frgGameFabAction1.isEnabled = true

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
                binding.frgGameFabAction2.isEnabled = true
                binding.frgGameFabAction3.visibility = View.VISIBLE
                binding.frgGameFabAction3.isEnabled = true
            }

            binding.frgGameFabAction4.visibility = View.VISIBLE
            binding.frgGameFabAction4.isEnabled = true

            binding.frgGameBoard.setArrowCallback { direction, movingPossibilities ->
                binding.frgGameFabAction2.visibility = View.VISIBLE
                binding.frgGameFabAction2.isEnabled = true
                if (movingPossibilities.canDig) {
                    binding.frgGameFabAction3.visibility = View.VISIBLE
                    binding.frgGameFabAction3.isEnabled = true
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
        val activePlayer = game.players[game.currentPlayerId]
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
                viewModel.setupAndStartDelayTimer(2, onFinishCallback = {
                    if (card.type == CardType.Question) {
                        viewModel.setGameState(
                            GameState(
                                type = GameStateTypes.Question,
                                card = card,
                                bidInfo = game.gameState.bidInfo
                            )
                        )
                    } else {
                        //todo handle bets
                        val newGame = game.copy(
                            gameState = GameState(
                                type = GameStateTypes.Tooth,
                                card = card,
                                param1 = if (card.layer < 5) ToothType.Tooth else ToothType.Bone,
                                param2 = "",
                                param3 = false,
                                param4 = false
                            )
                        )
                        game.gameState.bidInfo.forEach {
                            if (!it.value.skipped) {
                                if (it.value.type == WheelBetType.Dragon) {
                                    val old =
                                        newGame.players[it.value.playerId]?.coinsCollected?.get(
                                            GameUtils.Layers.Yellow.name
                                        ) ?: 0
                                    newGame.players[it.value.playerId]?.coinsCollected?.set(
                                        GameUtils.Layers.Yellow.name,
                                        old + it.value.amountBid
                                    )
                                } else {
                                    removeCoinsAfterBid(newGame, it)
                                }
                            }
                        }
                        viewModel.updateGame(newGame)
                    }
                }, onTickCallback = {})
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

    private fun removeCoinsAfterBid(newGame: Game, entry: Map.Entry<String, WheelBet>) {
        var remaining = entry.value.amountBid
        while (remaining > 0) {
            val coins = newGame.players[entry.value.playerId]?.coinsCollected
            when {
                remaining > 4 && coins?.get(GameUtils.Layers.Purple.name) != null && coins[GameUtils.Layers.Purple.name]!! > 0 -> { //remove purple coins
                    val old =
                        newGame.players[entry.value.playerId]?.coinsCollected?.get(
                            GameUtils.Layers.Purple.name
                        ) ?: 0
                    newGame.players[entry.value.playerId]?.coinsCollected?.set(
                        GameUtils.Layers.Purple.name,
                        old - 1
                    )
                    remaining -= 5
                }
                remaining > 3 && coins?.get(GameUtils.Layers.Blue.name) != null && coins[GameUtils.Layers.Blue.name]!! > 0 -> { //remove blue coins
                    val old =
                        newGame.players[entry.value.playerId]?.coinsCollected?.get(
                            GameUtils.Layers.Blue.name
                        ) ?: 0
                    newGame.players[entry.value.playerId]?.coinsCollected?.set(
                        GameUtils.Layers.Blue.name,
                        old - 1
                    )
                    remaining -= 4
                }
                remaining > 2 && coins?.get(GameUtils.Layers.Red.name) != null && coins[GameUtils.Layers.Red.name]!! > 0 -> { //remove red coins
                    val old =
                        newGame.players[entry.value.playerId]?.coinsCollected?.get(
                            GameUtils.Layers.Red.name
                        ) ?: 0
                    newGame.players[entry.value.playerId]?.coinsCollected?.set(
                        GameUtils.Layers.Red.name,
                        old - 1
                    )
                    remaining -= 3
                }
                remaining > 1 && coins?.get(GameUtils.Layers.Orange.name) != null && coins[GameUtils.Layers.Orange.name]!! > 0 -> { //remove orange coins
                    val old =
                        newGame.players[entry.value.playerId]?.coinsCollected?.get(
                            GameUtils.Layers.Orange.name
                        ) ?: 0
                    newGame.players[entry.value.playerId]?.coinsCollected?.set(
                        GameUtils.Layers.Orange.name,
                        old - 1
                    )
                    remaining -= 2
                }
                remaining > 0 && coins?.get(GameUtils.Layers.Yellow.name) != null && coins[GameUtils.Layers.Yellow.name]!! > 0 -> { //remove orange coins
                    val old =
                        newGame.players[entry.value.playerId]?.coinsCollected?.get(
                            GameUtils.Layers.Yellow.name
                        ) ?: 0
                    newGame.players[entry.value.playerId]?.coinsCollected?.set(
                        GameUtils.Layers.Yellow.name,
                        old - 1
                    )
                    remaining -= 1
                }
            }
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

        val activePlayersTeam = TeamsConstants.getTeamFromString(
            game.players[game.currentPlayerId]?.teamStr ?: ""
        )
        binding.frgGameTvAction.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                activePlayersTeam.colorRes
            )
        )
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
        wrapperList.forEach {
            it.setToUnselectedStyle()
        }

        val answeredNum = game.gameState.param1 as? Long
        val card = game.gameState.card

        if (game.currentPlayerId == viewModel.currentUserId) { // current user is answering
            binding.frgGameTvAction.setText(R.string.your_turn)
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
                if (card.question?.alreadyAnswered?.contains(index + 1) == true) {
                    materialCardView.setToDisabledStyle()
                }
            }

            if (answeredNum != null) { //start timer to change state
                if (answeredNum.toInt() == card.question?.question?.correctAnswer) {
                    wrapperList[(answeredNum - 1).toInt()].setCorrectStyle()
                } else {
                    wrapperList[(answeredNum - 1).toInt()].setIncorrectStyle()
                }
                wrapperList.forEachIndexed { index, materialCardView ->
                    if (index != answeredNum.toInt() - 1) {
                        materialCardView.setToDisabledStyle()
                    }
                }
                viewModel.setupAndStartDelayTimer(3, onFinishCallback = {
                    val newGame = game.copy(gameState = GameState(GameStateTypes.Turn))

                    if (answeredNum.toInt() == card.question?.question?.correctAnswer) {
                        val oldQuestAmount =
                            newGame.players[game.currentPlayerId]?.questionsAnswered?.get(
                                GameUtils.getLayerByNumber(card.layer).name
                            ) ?: 0
                        newGame.players[game.currentPlayerId]?.questionsAnswered?.set(
                            GameUtils.getLayerByNumber(card.layer).name,
                            oldQuestAmount + 1
                        )
                        if (card.question?.alreadyAnswered?.isEmpty() == true) {
                            val oldCoinsAmount =
                                newGame.players[game.currentPlayerId]?.coinsCollected?.get(
                                    GameUtils.getLayerByNumber(card.layer).name
                                ) ?: 0
                            newGame.players[game.currentPlayerId]?.coinsCollected?.set(
                                GameUtils.getLayerByNumber(card.layer).name,
                                oldCoinsAmount + 1
                            )
                        }
                        game.gameState.bidInfo?.forEach {
                            if (!it.value.skipped) {
                                if (it.value.type == WheelBetType.WhiteBean) {
                                    val old =
                                        newGame.players[it.value.playerId]?.coinsCollected?.get(
                                            GameUtils.Layers.Yellow.name
                                        ) ?: 0
                                    newGame.players[it.value.playerId]?.coinsCollected?.set(
                                        GameUtils.Layers.Yellow.name,
                                        old + it.value.amountBid
                                    )
                                } else {
                                    removeCoinsAfterBid(newGame, it)
                                }
                            }
                        }
                    } else {
                        game.gameState.bidInfo?.forEach {
                            if (!it.value.skipped) {
                                if (it.value.type == WheelBetType.BlackBean) {
                                    val old =
                                        newGame.players[it.value.playerId]?.coinsCollected?.get(
                                            GameUtils.Layers.Yellow.name
                                        ) ?: 0
                                    newGame.players[it.value.playerId]?.coinsCollected?.set(
                                        GameUtils.Layers.Yellow.name,
                                        old + it.value.amountBid
                                    )
                                } else {
                                    removeCoinsAfterBid(newGame, it)
                                }
                            }
                        }
                    }

                    viewModel.updateGame(newGame) {
                        viewModel.passTurnToNextPlayer()
                    }
                }, onTickCallback = {})
            }
        } else { // for other users
            binding.frgGameTvAction.text =
                getString(R.string.players_turn, game.players[game.currentPlayerId]?.name)
            binding.frgGameFabAction1.visibility = View.INVISIBLE
            binding.frgGameFabAction2.visibility = View.INVISIBLE
            binding.frgGameFabAction3.visibility = View.INVISIBLE
            binding.frgGameFabAction4.visibility = View.INVISIBLE
            wrapperList.forEach {
                it.setToDisabledStyle()
            }
            if (answeredNum != null) {
                if (answeredNum.toInt() == card.question?.question?.correctAnswer) {
                    wrapperList[(answeredNum - 1).toInt()].setCorrectStyle()
                } else {
                    wrapperList[(answeredNum - 1).toInt()].setIncorrectStyle()
                }
            }
        }
    }

    //--------------------------------------------------------------------------------- Tooth Handler
    private fun handleToothGameState(game: Game) {
        val type = game.gameState.param1 as? String
        val result = game.gameState.param2 as? String
        val isRolled = game.gameState.param3 as? Boolean
        val toothShown = game.gameState.param4 as? Boolean
        val card = game.gameState.card

        if (type != null && result != null && isRolled != null && toothShown != null && card != null) {
            binding.frgGameToothLayout.root.visibility = View.GONE
            binding.frgGameDiceLayout.root.visibility = View.GONE
            binding.frgGameWheelLayout.root.visibility = View.GONE
            binding.frgGameQuestionLayout.root.visibility = View.GONE
            binding.frgGameFabAction1.visibility = View.INVISIBLE
            binding.frgGameFabAction2.visibility = View.INVISIBLE
            binding.frgGameFabAction3.visibility = View.INVISIBLE
            binding.frgGameFabAction4.visibility = View.INVISIBLE
            binding.frgGameFabActionIncrease.visibility = View.GONE
            binding.frgGameFabActionDecrease.visibility = View.GONE
            binding.frgGameIvCoin.visibility = View.GONE
            binding.frgGameTvBidAmount.visibility = View.GONE
            if (!toothShown) { //initial display of outcome
                val toothBackground = binding.frgGameToothLayout.root.background as GradientDrawable
                val activePlayer = game.players[game.currentPlayerId]
                toothBackground.setStroke(
                    resources.getDimension(R.dimen.dp_1).toInt(),
                    ContextCompat.getColor(
                        requireContext(),
                        TeamsConstants.getTeamFromString(activePlayer?.teamStr ?: "").colorRes
                    )
                )
                if (type == ToothType.Tooth.name) {
                    binding.frgGameToothLayout.includeToothDialogIvTooth.visibility = View.VISIBLE
                    binding.frgGameToothLayout.includeToothDialogIvTooth.setImageResource(R.drawable.ic_tooth_white_90)
                    binding.frgGameToothLayout.includeToothDialogTvTitle.setText(R.string.tooth)
                } else {
                    binding.frgGameToothLayout.includeToothDialogIvTooth.visibility = View.VISIBLE
                    binding.frgGameToothLayout.includeToothDialogIvTooth.setImageResource(R.drawable.ic_bone_155)
                    binding.frgGameToothLayout.includeToothDialogTvTitle.setText(R.string.bone)
                }
                binding.frgGameToothLayout.root.visibility = View.VISIBLE
                if (game.currentPlayerId == viewModel.currentUserId) {
                    viewModel.setupAndStartDelayTimer(2, onFinishCallback = {
                        if (type == ToothType.Tooth.name) {
                            viewModel.setGameState(
                                GameState(
                                    game.gameState.type,
                                    param1 = game.gameState.param1,
                                    param2 = game.gameState.param2,
                                    param3 = game.gameState.param3,
                                    param4 = true,
                                    card = card
                                )
                            )
                        } else {
                            viewModel.setGameState(GameState(GameStateTypes.Turn)) {
                                viewModel.passTurnToNextPlayer()
                            }
                        }
                    }, onTickCallback = {})
                }
                return
            }
            if (result.isEmpty()) { //rolling dice
                binding.frgGameDiceLayout.includeDiceDialogIvDice.isClickable =
                    game.currentPlayerId == viewModel.currentUserId
                binding.frgGameToothLayout.includeToothDialogTvDescription.visibility = View.GONE
                binding.frgGameToothLayout.root.visibility = View.GONE
                binding.frgGameDiceLayout.root.visibility = View.VISIBLE
                if (game.currentPlayerId == viewModel.currentUserId) {
                    binding.frgGameDiceLayout.includeDiceDialogTvTitle.setText(
                        R.string.roll_dice_tooth
                    )
                } else {
                    binding.frgGameDiceLayout.includeDiceDialogTvTitle.setText(R.string.determining_tooth)
                }
                if (game.currentPlayerId != viewModel.currentUserId && isRolled) {
                    binding.frgGameDiceLayout.includeDiceDialogIvDice.performClick()
                }
            } else { //displaying outcome
                binding.frgGameDiceLayout.root.visibility = View.GONE
                binding.frgGameToothLayout.includeToothDialogTvDescription.visibility = View.VISIBLE
                binding.frgGameToothLayout.root.visibility = View.VISIBLE
                binding.frgGameToothLayout.includeToothDialogIvTooth.visibility = View.GONE
                val toothType = ToothResult.valueOf(result)
                var newGame = game
                var newCurrPlayer = game.players[game.currentPlayerId]
                when (toothType) {
                    ToothResult.Sieve -> {
                        binding.frgGameToothLayout.includeToothDialogTvTitle.setText(R.string.inventory_result)
                        val currPlayer = game.players[game.currentPlayerId]
                        if (game.currentPlayerId == viewModel.currentUserId) {
                            binding.frgGameToothLayout.includeToothDialogTvDescription.setText(R.string.inventory_sieve)
                            binding.frgGameToothLayout.includeToothDialogIvTooth.visibility =
                                View.VISIBLE
                            binding.frgGameToothLayout.includeToothDialogIvTooth.setImageResource(R.drawable.ic_sieve_78)
                        } else {
                            binding.frgGameToothLayout.includeToothDialogTvDescription.text =
                                getString(R.string.inventory_player_receive, currPlayer?.name)
                        }
                        val oldValue =
                            currPlayer?.inventory?.get(toothType.name)
                                ?: InventoryElement(toothType.name, 0)
                        newCurrPlayer?.inventory?.set(
                            toothType.name,
                            oldValue.copy(amount = oldValue.amount + 1)
                        )
                        newGame.players[newCurrPlayer?.id ?: ""] = newCurrPlayer ?: PlayerInfo()
                    }
                    ToothResult.Brush -> {
                        binding.frgGameToothLayout.includeToothDialogTvTitle.setText(R.string.inventory_result)
                        val currPlayer = game.players[game.currentPlayerId]
                        if (game.currentPlayerId == viewModel.currentUserId) {
                            binding.frgGameToothLayout.includeToothDialogTvDescription.setText(R.string.inventory_brush)
                            binding.frgGameToothLayout.includeToothDialogIvTooth.visibility =
                                View.VISIBLE
                            binding.frgGameToothLayout.includeToothDialogIvTooth.setImageResource(R.drawable.ic_brush_78)
                        } else {
                            binding.frgGameToothLayout.includeToothDialogTvDescription.text =
                                getString(R.string.inventory_player_receive, currPlayer?.name)
                        }
                        val oldValue =
                            currPlayer?.inventory?.get(toothType.name)
                                ?: InventoryElement(toothType.name, 0)
                        newCurrPlayer?.inventory?.set(
                            toothType.name,
                            oldValue.copy(amount = oldValue.amount + 1)
                        )
                        newGame.players[newCurrPlayer?.id ?: ""] = newCurrPlayer ?: PlayerInfo()
                    }
                    ToothResult.Rope -> {
                        binding.frgGameToothLayout.includeToothDialogTvTitle.setText(R.string.inventory_result)
                        val currPlayer = game.players[game.currentPlayerId]
                        if (game.currentPlayerId == viewModel.currentUserId) {
                            binding.frgGameToothLayout.includeToothDialogTvDescription.setText(R.string.inventory_rope)
                            binding.frgGameToothLayout.includeToothDialogIvTooth.visibility =
                                View.VISIBLE
                            binding.frgGameToothLayout.includeToothDialogIvTooth.setImageResource(R.drawable.ic_rope_78)
                        } else {
                            binding.frgGameToothLayout.includeToothDialogTvDescription.text =
                                getString(R.string.inventory_player_receive, currPlayer?.name)
                        }
                        val oldValue =
                            currPlayer?.inventory?.get(toothType.name)
                                ?: InventoryElement(toothType.name, 0)
                        newCurrPlayer?.inventory?.set(
                            toothType.name,
                            oldValue.copy(amount = oldValue.amount + 1)
                        )
                        newGame.players[newCurrPlayer?.id ?: ""] = newCurrPlayer ?: PlayerInfo()
                    }
                    ToothResult.Treasure -> {
                        binding.frgGameToothLayout.includeToothDialogTvTitle.setText(R.string.event_treasure)
                        binding.frgGameToothLayout.includeToothDialogTvDescription.setText(R.string.event_treasure_desc)
                        val oldAmount =
                            game.players[game.currentPlayerId]?.coinsCollected?.get(
                                GameUtils.getLayerByNumber(
                                    card.layer
                                ).name
                            )
                                ?: 0
                        newCurrPlayer?.coinsCollected?.set(
                            GameUtils.getLayerByNumber(card.layer).name,
                            oldAmount + 3
                        )
                        newGame.players[newCurrPlayer?.id ?: ""] = newCurrPlayer ?: PlayerInfo()
                    }
                    ToothResult.ToolLoss -> {
                        binding.frgGameToothLayout.includeToothDialogTvTitle.setText(R.string.event_tool_loss)
                        binding.frgGameToothLayout.includeToothDialogTvDescription.setText(R.string.event_tool_loss_desc)
                        newCurrPlayer = newCurrPlayer?.copy(
                            state = PlayerState(
                                true,
                                3,
                                PlayerStateType.ToolLoss
                            )
                        )
                        newGame.players[newCurrPlayer?.id ?: ""] = newCurrPlayer ?: PlayerInfo()
                    }
                    ToothResult.Collapse -> {
                        binding.frgGameToothLayout.includeToothDialogTvTitle.setText(R.string.event_collapse)
                        binding.frgGameToothLayout.includeToothDialogTvDescription.setText(R.string.event_collapse_desc)
                        //todo handle collapse
                    }
                    ToothResult.Rainfall -> {
                        val currPlayer = game.players[game.currentPlayerId]
                        binding.frgGameToothLayout.includeToothDialogTvTitle.setText(R.string.event_rainfall)
                        binding.frgGameToothLayout.includeToothDialogTvDescription.setText(R.string.event_rainfall_desc)
                        game.players.forEach {
                            if (it.value.position.x in (currPlayer?.position?.x!! - 1)..(currPlayer.position.x + 1) &&
                                it.value.position.y in (currPlayer.position.y - 1)..(currPlayer.position.y + 1)
                            ) {
                                newGame.players[it.key] =
                                    it.value.copy(state = PlayerState(true, 1))
                            }
                        }
                    }
                    ToothResult.RiverHorizontal -> {
                        val currPlayer = game.players[game.currentPlayerId]
                        binding.frgGameToothLayout.includeToothDialogTvTitle.setText(R.string.event_river)
                        binding.frgGameToothLayout.includeToothDialogTvDescription.setText(R.string.event_river_desc_1)
                        game.players.forEach {
                            if (it.value.position.x == currPlayer?.position?.x) {
                                newGame.players[it.key] =
                                    it.value.copy(state = PlayerState(true, 1))
                            }
                        }
                    }
                    ToothResult.RiverVertical -> {
                        val currPlayer = game.players[game.currentPlayerId]
                        binding.frgGameToothLayout.includeToothDialogTvTitle.setText(R.string.event_river)
                        binding.frgGameToothLayout.includeToothDialogTvDescription.setText(R.string.event_river_desc_2)
                        game.players.forEach {
                            if (it.value.position.y == currPlayer?.position?.y) {
                                newGame.players[it.key] =
                                    it.value.copy(state = PlayerState(true, 1))
                            }
                        }
                    }
                    ToothResult.Cavern -> {
                        binding.frgGameToothLayout.includeToothDialogTvTitle.setText(R.string.event_cavern)
                        binding.frgGameToothLayout.includeToothDialogTvDescription.setText(R.string.event_cavern_desc)
                        //todo clear card below current
                    }
                }

                if (game.currentPlayerId == viewModel.currentUserId) {
                    viewModel.setupAndStartDelayTimer(3, onFinishCallback = {
                        val key =
                            (card.layer - 1) * (viewModel.size * viewModel.size) + card.cardNum - 1
                        val newCards = newGame.cards.toMutableList()
                        newCards[key] = newCards[key].copy(cleared = true)
                        newGame =
                            game.copy(gameState = GameState(GameStateTypes.Turn), cards = newCards)
                        viewModel.updateGame(newGame) {
                            viewModel.passTurnToNextPlayer()
                        }
                    }, onTickCallback = {})
                }
            }
        } else {
            throw IllegalStateException("Wrong game state params!")
        }
    }

    private fun handleMorganTurnGameState(game: Game) {
        val flag = game.gameState.param1 as? Boolean
        binding.frgGameTvAction.setText(R.string.morgan_turn)
        binding.frgGameTvAction.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_morgan_outline
            )
        )
        binding.frgGameFabAction1.visibility = View.INVISIBLE
        binding.frgGameFabAction2.visibility = View.INVISIBLE
        binding.frgGameFabAction3.visibility = View.INVISIBLE
        binding.frgGameFabAction4.visibility = View.INVISIBLE
        binding.frgGameDiceLayout.root.visibility = View.VISIBLE
        binding.frgGameDiceLayout.includeDiceDialogTvTitle.setText(R.string.determining_morgan_position)
        if (flag == false) {
            binding.frgGameDiceLayout.includeDiceDialogIvDice.performClick()
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
        hideAllBidCoins()
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

        hideAllBidCoins()

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
                if (viewModel.bidAmount > 0) {
                    binding.frgGameFabAction1.isEnabled = true
                }
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
                totalYellowCoins += (GameUtils.Layers.valueOf(it.key).ordinal + 1) * it.value
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

    private fun hideAllBidCoins() {
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
            when (viewModel.game.value.gameState.type) {
                GameStateTypes.Start -> {
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
                            }
                        }
                    }
                }
                GameStateTypes.Tooth -> {
                    if (viewModel.game.value.currentPlayerId == viewModel.currentUserId) {
                        viewModel.setGameState(viewModel.game.value.gameState.copy(param3 = true)) //another users should see roll
                    }
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
                        if (viewModel.game.value.currentPlayerId == viewModel.currentUserId) {
                            viewModel.setupAndStartDelayTimer(1, onFinishCallback = {
                                if (dice % 2 == 0) { //event
                                    val event = mutableListOf<String>()
                                    viewModel.game.value.eventPool.forEach {
                                        for (i in 0 until it.value) {
                                            event.add(it.key)
                                        }
                                    }
                                    event.shuffle()
                                    val eventDrew = event.first()
                                    viewModel.setGameState(
                                        viewModel.game.value.gameState.copy(
                                            param2 = eventDrew
                                        )
                                    )
                                    val oldValue = viewModel.game.value.eventPool[eventDrew] ?: 1
                                    viewModel.setEventAmount(eventDrew, oldValue - 1)
                                } else { //inventory
                                    val inv = mutableListOf<String>()
                                    viewModel.game.value.inventoryPool.forEach {
                                        for (i in 0 until it.value) {
                                            inv.add(it.key)
                                        }
                                    }
                                    inv.shuffle()
                                    val itemDrew = inv.first()
                                    viewModel.setGameState(
                                        viewModel.game.value.gameState.copy(
                                            param2 = itemDrew
                                        )
                                    )
                                    val oldValue = viewModel.game.value.inventoryPool[itemDrew] ?: 1
                                    viewModel.setInventoryAmount(itemDrew, oldValue - 1)
                                }
                            }, onTickCallback = {
                            })
                        }
                    }
                }
                GameStateTypes.MorganTurn -> {
                    if (viewModel.game.value.currentPlayerId == viewModel.currentUserId) {
                        viewModel.setGameState(viewModel.game.value.gameState.copy(param1 = true)) //another users should see roll
                    }
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
                        if (viewModel.game.value.currentPlayerId == viewModel.currentUserId) {
                            viewModel.setupAndStartDelayTimer(1, onFinishCallback = {
                                viewModel.passTurnToNextPlayer(true) {
                                    viewModel.setMorganPosition(viewModel.game.value.morganPosition + dice)
                                }
                            }, onTickCallback = {
                            })
                        }
                    }
                }
            }
        }
    }

    private fun setupToothDialog() {
        val toothBackground = GradientDrawable()
        toothBackground.setStroke(
            resources.getDimension(R.dimen.dp_1).toInt(),
            ContextCompat.getColor(requireContext(), R.color.color_team_green)
        )
        toothBackground.color = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_transparent_background
            )
        )
        toothBackground.cornerRadius = resources.getDimension(R.dimen.dp_14)
        binding.frgGameToothLayout.root.background = toothBackground
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

    private fun setupInventoryDialog() {
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
        binding.frgGameInventoryDialogLayout.root.background = background
    }

    private fun setupAdapters() {
        inventoryAdapter = InventoryAdapter()
        binding.frgGameRvInventory.addItemDecoration(InventoryItemDecoration())
        binding.frgGameRvInventory.adapter = inventoryAdapter

        statisticsAdapter = TeamsStatisticsAdapter()
        statisticsBinding.frgGameStatisticsDialogRvTeams.addItemDecoration(
            TeamsStatisticsItemDecoration()
        )
        statisticsBinding.frgGameStatisticsDialogRvTeams.adapter = statisticsAdapter

        inventoryDialogAdapter = InventoryDialogAdapter { item ->
            val inventory = viewModel.inventory.value.toMutableList()
            viewModel.inventory.value.forEachIndexed { index, inventoryModel ->
                if (item == inventoryModel) {
                    inventory[index] = inventoryModel.copy(isSelected = true)
                }
            }
            viewModel.selectedInventoryItem = item
            binding.frgGameFabActionInventoryAccept.isEnabled =
                viewModel.selectedInventoryPlayer != null
            inventoryDialogAdapter.bindData(inventory)
        }
        binding.frgGameInventoryDialogLayout.includeInventoryDialogRvInventory.addItemDecoration(
            InventoryDialogItemDecoration()
        )
        binding.frgGameInventoryDialogLayout.includeInventoryDialogRvInventory.adapter =
            inventoryDialogAdapter

        inventoryDialogPlayersAdapter = InventoryDialogPlayersAdapter { player ->
            val currPlayer = viewModel.game.value.players[viewModel.currentUserId]
            val teamPlayers = viewModel.game.value.players.filter {
                it.key != currPlayer?.id //&& it.value.teamStr == currPlayer?.teamStr
            }.values.map {
                it.toInventoryPlayer(player.playerId == it.id)
            }
            viewModel.selectedInventoryPlayer = viewModel.game.value.players[player.playerId]
            binding.frgGameFabActionInventoryAccept.isEnabled =
                viewModel.selectedInventoryItem != null
            inventoryDialogPlayersAdapter.bindData(teamPlayers)
        }
        binding.frgGameInventoryDialogLayout.includeInventoryDialogRvPlayers.addItemDecoration(
            InventoryDialogPlayersItemDecoration()
        )
        binding.frgGameInventoryDialogLayout.includeInventoryDialogRvPlayers.adapter =
            inventoryDialogPlayersAdapter
        binding.frgGameInventoryDialogLayout.includeInventoryDialogRvPlayers.itemAnimator = null
    }

    private fun hideInventoryDialog() {
        binding.frgGameQuestionLayout.root.visibility =
            if (viewModel.questionVisible) View.VISIBLE else View.GONE
        binding.frgGameFabAction1.visibility =
            if (viewModel.action1Visible) View.VISIBLE else View.INVISIBLE
        binding.frgGameFabAction2.visibility =
            if (viewModel.action2Visible) View.VISIBLE else View.INVISIBLE
        binding.frgGameFabAction3.visibility =
            if (viewModel.action3Visible) View.VISIBLE else View.INVISIBLE
        binding.frgGameFabAction4.visibility =
            if (viewModel.action4Visible) View.VISIBLE else View.INVISIBLE

        binding.frgGameFabActionInventoryAccept.visibility = View.GONE
        binding.frgGameFabActionInventoryClose.visibility = View.GONE
        binding.frgGameInventoryDialogLayout.root.visibility = View.GONE
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