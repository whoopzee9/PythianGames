package ru.spbstu.feature.data.remote.source

import ru.spbstu.feature.data.remote.api.FeatureApiService
import ru.spbstu.feature.data.source.FeatureDataSource
import javax.inject.Inject

class FeatureDataSourceImpl @Inject constructor(private val featureApiService: FeatureApiService) :
    FeatureDataSource
