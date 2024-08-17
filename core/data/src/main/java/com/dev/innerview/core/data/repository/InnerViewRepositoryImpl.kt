package com.dev.innerview.core.data.repository

import com.dev.innerview.core.data_api.InnerViewRepository
import com.dev.innerview.core.database.datasource.InnerViewDataSource
import com.dev.innerview.core.model.InnerView
import com.dev.innerview.core.model.InnerViewType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime
import javax.inject.Inject

class InnerViewRepositoryImpl @Inject constructor(
    private val innerViewDataSource: InnerViewDataSource
) : InnerViewRepository {

    override fun getInnerViews(): Flow<List<InnerView>> =
        innerViewDataSource.innerViewData
            .map {
                it.map { innerViewSchema ->
                    InnerView(
                        id = innerViewSchema._id,
                        title = innerViewSchema.title,
                        type = InnerViewType.stringToInnerViewType(innerViewSchema.type)!!,
                        createdAt = ZonedDateTime.parse(innerViewSchema.createdAt),
                    )
                }
            }

    override suspend fun addInnerView(title: String, type: InnerViewType) {
        innerViewDataSource.addInnerView(title, type.name)
    }
}