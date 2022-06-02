package ru.spbstu.feature.data.repository

import ru.spbstu.feature.data.source.FeatureDataSource
import ru.spbstu.feature.domain.repository.FeatureRepository
import javax.inject.Inject

class FeatureRepositoryImpl @Inject constructor(private val featureDataSource: FeatureDataSource) :
    FeatureRepository
