package com.sample.dynamicui.data.repository

import com.sample.dynamicui.data.remote.ApiClient
import com.sample.dynamicui.domain.model.Component
import com.sample.dynamicui.domain.repository.DynamicRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class DynamicRepositoryImpl(
    private val baseUrl: String = "https://api.example.com", // replace with actual
    private val httpClient: HttpClient = ApiClient.http
) : DynamicRepository {

    override suspend fun fetchLayout(layoutId: String): Component {
        // Pass layoutId as path param
        return httpClient.get("${baseUrl}/dynamic-ui/screen/${layoutId}").body()
    }
}