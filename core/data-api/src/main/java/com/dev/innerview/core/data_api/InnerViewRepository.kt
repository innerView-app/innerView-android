package com.dev.innerview.core.data_api

import com.dev.innerview.core.model.InnerView
import com.dev.innerview.core.model.InnerViewType
import kotlinx.coroutines.flow.Flow

interface InnerViewRepository {

    fun getInnerViews(): Flow<List<InnerView>>

    suspend fun addInnerView(title: String, type: InnerViewType)
}