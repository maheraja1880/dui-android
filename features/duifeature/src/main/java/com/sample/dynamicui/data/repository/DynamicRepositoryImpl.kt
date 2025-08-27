package com.sample.dynamicui.data.repository

import com.sample.dynamicui.data.remote.ApiClient
import com.sample.dynamicui.domain.model.Component
import com.sample.dynamicui.domain.repository.DynamicRepository
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

open class DynamicRepositoryImpl(
    private val baseUrl: String = "https://api.example.com" // replace with actual
) : DynamicRepository {
    override suspend fun fetchLayout(layoutId: String): Component {
        return ApiClient.http.get("${baseUrl}/dynamic-ui/screen") {
            parameter("layoutId", layoutId)
        }.body()
    }
}