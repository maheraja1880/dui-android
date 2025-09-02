package com.sample.dynamicui.data.repository

import android.util.Log
import com.sample.dynamicui.data.remote.ApiClient
import com.sample.dynamicui.domain.model.Component
import com.sample.dynamicui.domain.repository.DynamicRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class DynamicRepositoryImpl(
    private val baseUrl: String = "https://api.example.com", // replace with actual
    private val httpClient: HttpClient = ApiClient.mockClient // change the mockClinet to http client, for action production
) : DynamicRepository {

    override suspend fun fetchLayout(layoutId: String): Component {
        Log.d("DynamicRepositoryImpl", "Fetching layout: $layoutId")
        // Pass layoutId as path param
        val component = httpClient.get("${baseUrl}/dynamic-ui/screen/${layoutId}").body<Component>()
        return component
    }
}
