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
    private val httpClient: HttpClient = ApiClient.http
) : DynamicRepository {

    override suspend fun fetchLayout(layoutId: String): Component {
        // Pass layoutId as path param
        //return httpClient.get("${baseUrl}/dynamic-ui/screen/${layoutId}").body()
        Log.d("DynamicRepositoryImpl", "Fetching layout: $layoutId")
        return mockLayout(layoutId)
    }

    fun mockLayout(layoutId: String): Component {
        val layoutJsonMap = mapOf(
            "home" to
                    """
                    {
                      "id": "home-component",
                      "type": "container",
                      "properties": {
                        "background": "white"
                      },
                      "children": [
                        {
                          "id": "title",
                          "type": "text",
                          "properties": {
                            "text": "Hello World 1"
                          }
                        },
                        {
                          "id": "button1",
                          "type": "button",
                          "properties": {
                            "text": "Click Me"
                          },
                          "onInteraction": [
                            {
                              "event": "onClick",
                              "action": [
                                {
                                  "type": "navigate",
                                  "properties": {
                                    "target": "profile"
                                  }
                                }
                              ]
                            }
                          ]
                        },
                        {
                          "id": "button2",
                          "type": "button",
                          "properties": {
                            "text": "Refresh Me"
                          },
                          "onInteraction": [
                            {
                              "event": "onClick",
                              "action": [
                                {
                                  "type": "refresh"
                                }
                              ]
                            }
                          ]
                        }
                      ]
                    } 
                    """.trimIndent(),
            "profile" to
                    """
                    {
                      "id": "profile-component",
                      "type": "container",
                      "properties": {
                        "background": "white"
                      },
                      "children": [
                        {
                          "id": "title",
                          "type": "text",
                          "properties": {
                            "text": "Hello World 2"
                          }
                        },
                        {
                          "id": "button1",
                          "type": "button",
                          "properties": {
                            "text": "Click Me"
                          },
                          "onInteraction": [
                            {
                              "event": "onClick",
                              "action": [
                                {
                                  "type": "navigate",
                                  "properties": {
                                    "target": "home"
                                  }
                                }
                              ]
                            }
                          ]
                        }
                      ]
                    } 
                """.trimIndent()
        )
        val jsonString = layoutJsonMap[layoutId]
            ?: throw IllegalArgumentException("No mock layout found for id: $layoutId")
        return kotlinx.serialization.json.Json.decodeFromString(Component.serializer(), jsonString)
    }
}