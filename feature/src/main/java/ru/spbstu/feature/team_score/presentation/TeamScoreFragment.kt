package ru.spbstu.feature.team_score.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.handleBackPressed
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.utils.DatabaseReferences
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentTeamScoreBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent

class TeamScoreFragment : BaseFragment<TeamScoreViewModel>(
    R.layout.fragment_team_score,
) {
    private var _binding: FragmentTeamScoreBinding? = null
    override val binding get() = _binding!!

    private var listener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamScoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_primary)
        requireView().setLightStatusBar()

        handleBackPressed { }
    }

    override fun subscribe() {
        super.subscribe()
//        val ref = Firebase.database.getReference(DatabaseReferences.GAMES_REF)
//        listener = ref.child(viewModel.gameJoiningDataWrapper.game.name).subscribe(onSuccess = { snapshot ->
//            handleGameSnapshotData(snapshot)
//        }, onCancelled = {})

    }

    override fun onDestroyView() {
        val ref = Firebase.database.getReference(DatabaseReferences.GAMES_REF)
        listener?.let { ref.removeEventListener(it) }
        //_binding = null
        super.onDestroyView()
    }

    private fun handleGameSnapshotData(snapshot: DataSnapshot) {

    }


    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .teamScoreComponentFactory()
            .create(this)
            .inject(this)
    }

}