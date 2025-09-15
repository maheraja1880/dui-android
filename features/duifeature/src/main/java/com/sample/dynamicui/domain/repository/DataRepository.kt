package com.sample.dynamicui.domain.repository

import com.sample.dynamicui.domain.model.AnySerializable
import kotlinx.coroutines.flow.Flow

interface DataRepository {

    suspend fun fetchDataForLayout(layoutId: String): Flow<String>

}