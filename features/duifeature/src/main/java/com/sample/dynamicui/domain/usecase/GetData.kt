package com.sample.dynamicui.domain.usecase

import com.sample.dynamicui.domain.model.AnySerializable
import com.sample.dynamicui.domain.model.Component
import com.sample.dynamicui.domain.repository.DataRepository
import com.sample.dynamicui.domain.repository.LayoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import javax.inject.Inject

class GetData @Inject constructor(
    private val repository: DataRepository
){
    suspend operator fun invoke(layoutId: String): Flow<String> {
        return repository.fetchDataForLayout(layoutId)
    }
}