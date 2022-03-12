package ru.spbstu.pythian_games.root.presentation

import ru.spbstu.common.base.BaseActivity
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.pythian_games.databinding.ActivityRootBinding
import ru.spbstu.pythian_games.navigation.Navigator
import ru.spbstu.pythian_games.root.di.RootApi
import ru.spbstu.pythian_games.root.di.RootComponent
import javax.inject.Inject


class RootActivity : BaseActivity<RootViewModel>() {

    @Inject
    lateinit var navigator: Navigator

    override val binding: ActivityRootBinding by viewBinding(ActivityRootBinding::inflate)

    override fun setupViews() {
        super.setupViews()
        navigator.attachActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        navigator.detachActivity()
    }

    override fun inject() {
        FeatureUtils.getFeature<RootComponent>(this, RootApi::class.java)
            .mainActivityComponentFactory()
            .create(this)
            .inject(this)
    }
}
