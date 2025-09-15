package com.sample.dynamicui.data.repository

import android.util.Log
import com.sample.dynamicui.data.remote.ApiClient
import com.sample.dynamicui.domain.model.Component
import com.sample.dynamicui.domain.repository.LayoutRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LayoutRepositoryImpl(
    private val baseUrl: String = "https://api.example.com", // replace with actual
    private val httpClient: HttpClient = ApiClient.mockClient // change the mockClinet to http client, for action production
) : LayoutRepository {

    override suspend fun fetchLayout(layoutId: String): Component {
        Log.d("DynamicRepositoryImpl", "Fetching layout: $layoutId")
        // Pass layoutId as path param
        val component = httpClient.get("${baseUrl}/dynamic-ui/screen/${layoutId}").body<Component>()
        return component
    }

    fun produceNumbers(): Flow<Int> = flow {
        for (i in 1..5) {
            delay(100) // Simulate some work
            emit(i) // Emit the value
        }
    }.flowOn(kotlinx.coroutines.Dispatchers.IO)
}
