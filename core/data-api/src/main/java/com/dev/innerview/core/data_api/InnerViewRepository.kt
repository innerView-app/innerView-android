package com.dev.innerview.core.data_api

import com.dev.innerview.core.model.InnerView
import kotlinx.coroutines.flow.Flow

interface InnerViewRepository {

    fun getInnerViews(): Flow<List<InnerView>>

}