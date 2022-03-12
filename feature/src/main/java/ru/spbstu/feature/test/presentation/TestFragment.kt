package ru.spbstu.feature.test.presentation

import android.view.ViewGroup
import ru.spbstu.common.di.FeatureUtils
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

    override fun setupViews() {
        super.setupViews()
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
}
